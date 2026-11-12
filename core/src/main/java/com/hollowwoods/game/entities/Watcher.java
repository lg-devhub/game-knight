package com.hollowwoods.game.entities;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.hollowwoods.game.utils.Constants;
import com.hollowwoods.game.world.CollisionManager;
import com.hollowwoods.game.world.Obstacle;

import java.util.List;

public class Watcher extends Entity {

    public enum State {
        OBSERVED,
        STALKING,
        HUNTING,
        SEARCHING,
        RETREATING
    }

    public State state = State.STALKING;

    public final Vector2 lastKnownPlayerPos = new Vector2();
    public float stareTimer = 0f;
    public float retreatTimer = 0f;
    public float searchTimer = 0f;
    public float attackCooldown = 0f;

    public boolean isDirectlyObserved = false;
    public float distanceToPlayer = 9999f;
    public boolean justAttacked = false;

    public Watcher(float x, float y) {
        super(x, y, 26, 26);
        this.speed = Constants.WATCHER_SPEED_STAGE1;
    }

    public void updateAI(float delta, Player player, List<Obstacle> obstacles, CollisionManager collisionManager,
                         boolean blackoutActive, float speedMultiplier, int stage,
                         float mapWidth, float mapHeight) {
        stateTime += delta;
        justAttacked = false;

        if (attackCooldown > 0f) {
            attackCooldown -= delta;
        }

        distanceToPlayer = position.dst(player.position);

        // Define a velocidade base conforme o estágio e aplica modificador de maldição
        float baseSpeed = (stage == 1) ? Constants.WATCHER_SPEED_STAGE1 :
                (stage == 2) ? Constants.WATCHER_SPEED_STAGE2 : Constants.WATCHER_SPEED_STAGE3;
        baseSpeed *= speedMultiplier;

        // 1. Verificação rigorosa da mecânica: OBSERVAÇÃO PELA LANTERNA
        isDirectlyObserved = checkObserved(player, obstacles, blackoutActive);

        if (isDirectlyObserved) {
            // REGRA INEGOCIÁVEL: Quando observada, a criatura fica completamente imóvel
            state = State.OBSERVED;
            velocity.setZero();
            isMoving = false;
            stareTimer += delta;

            // Se for encarada fixamente por mais de 4,5 segundos, recua para as sombras
            if (stareTimer > 4.5f) {
                stareTimer = 0f;
                state = State.RETREATING;
                retreatTimer = 3.0f;
            }
            return;
        }

        // Não observada: reseta temporizador de encaramento
        stareTimer = 0f;

        // 2. Estado RETREATING: Afasta-se do jogador temporariamente
        if (state == State.RETREATING) {
            retreatTimer -= delta;
            Vector2 away = new Vector2(position).sub(player.position).nor();
            velocity.set(away).scl(baseSpeed * 0.9f);
            isMoving = true;
            collisionManager.resolveMovement(position, velocity, bounds, delta, obstacles, mapWidth, mapHeight);

            if (retreatTimer <= 0f) {
                state = State.STALKING;
            }
            return;
        }

        // 3. Verificação de ataque corporal
        if (distanceToPlayer <= Constants.WATCHER_ATTACK_DIST && attackCooldown <= 0f) {
            player.takeDamage(Constants.WATCHER_ATTACK_DAMAGE);
            attackCooldown = 2.2f;
            justAttacked = true;
            // Após atingir o jogador, recua temporariamente
            state = State.RETREATING;
            retreatTimer = Constants.WATCHER_RETREAT_DURATION;
            return;
        }

        // 4. Gatilho de Perseguição (HUNTING) por proximidade enquanto não observada
        if (state == State.STALKING && distanceToPlayer <= Constants.WATCHER_HUNT_TRIGGER_DIST) {
            state = State.HUNTING;
            lastKnownPlayerPos.set(player.position);
        }

        boolean hasLOS = hasLineOfSight(player.position, obstacles);
        if (hasLOS) {
            lastKnownPlayerPos.set(player.position);
        }

        // 5. Execução dos estados de movimentação
        if (state == State.HUNTING) {
            if (hasLOS) {
                Vector2 dir = new Vector2(player.position).sub(position).nor();
                velocity.set(dir).scl(baseSpeed * Constants.WATCHER_HUNT_MULTIPLIER);
                isMoving = true;
                collisionManager.resolveMovement(position, velocity, bounds, delta, obstacles, mapWidth, mapHeight);
            } else {
                // Perdeu contato visual: inicia busca na última posição conhecida
                state = State.SEARCHING;
                searchTimer = 3.5f;
            }
        } else if (state == State.SEARCHING) {
            searchTimer -= delta;
            float dstToLast = position.dst(lastKnownPlayerPos);
            if (dstToLast > 24f) {
                Vector2 dir = new Vector2(lastKnownPlayerPos).sub(position).nor();
                velocity.set(dir).scl(baseSpeed);
                isMoving = true;
                collisionManager.resolveMovement(position, velocity, bounds, delta, obstacles, mapWidth, mapHeight);
            } else {
                isMoving = false;
                velocity.setZero();
            }

            if (searchTimer <= 0f || hasLOS) {
                if (hasLOS && distanceToPlayer <= Constants.WATCHER_HUNT_TRIGGER_DIST) {
                    state = State.HUNTING;
                } else {
                    state = State.STALKING;
                }
            }
        } else {
            // STALKING: Aproxima-se silenciosamente
            state = State.STALKING;
            if (distanceToPlayer < Constants.WATCHER_DETECTION_RANGE) {
                Vector2 target = hasLOS ? player.position : lastKnownPlayerPos;
                Vector2 dir = new Vector2(target).sub(position).nor();
                velocity.set(dir).scl(baseSpeed);
                isMoving = true;
                collisionManager.resolveMovement(position, velocity, bounds, delta, obstacles, mapWidth, mapHeight);
            } else {
                isMoving = false;
                velocity.setZero();
            }
        }

        // Atualiza direção de animação (0=Baixo, 1=Esquerda, 2=Direita, 3=Cima)
        if (isMoving && !velocity.isZero(0.1f)) {
            if (Math.abs(velocity.x) > Math.abs(velocity.y)) {
                direction = (velocity.x > 0f) ? 2 : 1;
            } else {
                direction = (velocity.y > 0f) ? 3 : 0;
            }
        }
    }

    /**
     * Valida a regra de observação:
     * 1. Lanterna ativa, com bateria e sem blackout.
     * 2. Distância <= FLASHLIGHT_RANGE.
     * 3. Ângulo entre a mira da lanterna e a criatura <= FLASHLIGHT_FOV_DEGREES / 2.
     * 4. Linha de visão desobstruída por obstáculos sólidos.
     */
    public boolean checkObserved(Player player, List<Obstacle> obstacles, boolean blackoutActive) {
        if (!player.isLightEffective(blackoutActive)) {
            return false;
        }

        float dist = position.dst(player.position);
        if (dist > Constants.FLASHLIGHT_RANGE) {
            return false;
        }

        // Raio ambiente imediato (se a criatura estiver praticamente colada ao jogador)
        if (dist <= Constants.FLASHLIGHT_MIN_RADIUS) {
            return hasLineOfSight(player.position, obstacles);
        }

        // Cálculo angular do cone
        float dx = position.x - player.position.x;
        float dy = position.y - player.position.y;
        float angleToWatcher = MathUtils.atan2(dy, dx) * MathUtils.radiansToDegrees;
        if (angleToWatcher < 0f) angleToWatcher += 360f;

        float flashAngle = player.flashlightAngle;
        float angleDiff = Math.abs(MathUtils.atan2(
                MathUtils.sinDeg(angleToWatcher - flashAngle),
                MathUtils.cosDeg(angleToWatcher - flashAngle)
        ) * MathUtils.radiansToDegrees);

        float halfCone = Constants.FLASHLIGHT_FOV_DEGREES / 2.0f;
        if (angleDiff > halfCone) {
            return false;
        }

        // Verificação de oclusão por Raycasting
        return hasLineOfSight(player.position, obstacles);
    }

    public boolean hasLineOfSight(Vector2 target, List<Obstacle> obstacles) {
        Vector2 start = this.position;
        for (Obstacle o : obstacles) {
            if (o.solid && o.blocksLight && Intersector.intersectSegmentRectangle(start, target, o.bounds)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void update(float delta) {
        stateTime += delta;
    }
}
