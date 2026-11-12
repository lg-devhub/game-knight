package com.hollowwoods.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.hollowwoods.game.utils.Constants;
import com.hollowwoods.game.world.CollisionManager;
import com.hollowwoods.game.world.Obstacle;

import java.util.List;

public class Player extends Entity {

    public float health = Constants.PLAYER_MAX_HEALTH;
    public float battery = Constants.PLAYER_MAX_BATTERY;
    public float stamina = Constants.PLAYER_MAX_STAMINA;
    public boolean isSprinting = false;

    public float flashlightAngle = 0f; // Graus (0 = Direita, 90 = Cima, 180 = Esquerda, 270 = Baixo)
    public boolean isFlashlightOn = true;
    public float invulnerabilityTimer = 0f;

    public Player(float x, float y) {
        super(x, y, 24, 24); // Caixa de colisão 24x24 pixels
        this.speed = Constants.PLAYER_SPEED;
    }

    public void handleInput(Vector2 worldMousePos, float delta, boolean blackoutActive, float batteryDrainMult) {
        // Alternar Lanterna [F] ou [Botão Direito do Mouse]
        if (Gdx.input.isKeyJustPressed(Input.Keys.F) || Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
            isFlashlightOn = !isFlashlightOn;
        }

        // Corrida com estamina [Shift]
        boolean sprintKey = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT);
        if (sprintKey && stamina > 5f && isMoving) {
            isSprinting = true;
            stamina = Math.max(0f, stamina - Constants.STAMINA_DRAIN * delta);
            speed = Constants.PLAYER_SPRINT_SPEED;
        } else {
            isSprinting = false;
            stamina = Math.min(Constants.PLAYER_MAX_STAMINA, stamina + Constants.STAMINA_RECOVERY * delta);
            speed = Constants.PLAYER_SPEED;
        }

        // Leitura de movimento (WASD ou Setas)
        float moveX = 0f;
        float moveY = 0f;

        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) moveY += 1f;
        if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) moveY -= 1f;
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) moveX -= 1f;
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) moveX += 1f;

        velocity.set(moveX, moveY);
        if (!velocity.isZero(0.001f)) {
            // Normalização diagonal para velocidade consistente em todas as 8 direções
            velocity.nor().scl(speed);
            isMoving = true;
        } else {
            isMoving = false;
        }

        // Orientação da lanterna e direção do personagem
        if (worldMousePos != null) {
            float dx = worldMousePos.x - position.x;
            float dy = worldMousePos.y - position.y;
            flashlightAngle = MathUtils.atan2(dy, dx) * MathUtils.radiansToDegrees;
            if (flashlightAngle < 0f) flashlightAngle += 360f;

            // Determina a direção do sprite baseado no ângulo do feixe
            if (flashlightAngle >= 45f && flashlightAngle < 135f) {
                direction = 3; // Up
            } else if (flashlightAngle >= 135f && flashlightAngle < 225f) {
                direction = 1; // Left
            } else if (flashlightAngle >= 225f && flashlightAngle < 315f) {
                direction = 0; // Down
            } else {
                direction = 2; // Right
            }
        } else if (isMoving) {
            if (Math.abs(moveX) > Math.abs(moveY)) {
                direction = (moveX > 0f) ? 2 : 1;
                flashlightAngle = (moveX > 0f) ? 0f : 180f;
            } else {
                direction = (moveY > 0f) ? 3 : 0;
                flashlightAngle = (moveY > 0f) ? 90f : 270f;
            }
        }

        // Consumo de bateria quando a lanterna está ligada
        if (isFlashlightOn && battery > 0f && !blackoutActive) {
            battery = Math.max(0f, battery - (Constants.BATTERY_DRAIN_NORMAL * batteryDrainMult) * delta);
        }

        if (invulnerabilityTimer > 0f) {
            invulnerabilityTimer -= delta;
        }
    }

    public void updatePhysics(float delta, CollisionManager collisionManager, List<Obstacle> obstacles,
                              float mapWidth, float mapHeight) {
        if (isMoving) {
            stateTime += delta;
            collisionManager.resolveMovement(position, velocity, bounds, delta, obstacles, mapWidth, mapHeight);
        }
    }

    @Override
    public void update(float delta) {
        stateTime += delta;
    }

    public boolean isLightEffective(boolean blackoutActive) {
        return isFlashlightOn && battery > 0f && !blackoutActive;
    }

    public void takeDamage(float amount) {
        if (invulnerabilityTimer <= 0f) {
            health = Math.max(0f, health - amount);
            invulnerabilityTimer = 1.6f; // Breve período de invulnerabilidade
        }
    }

    public void heal(float amount) {
        health = Math.min(Constants.PLAYER_MAX_HEALTH, health + amount);
    }

    public void rechargeBattery(float amount) {
        battery = Math.min(Constants.PLAYER_MAX_BATTERY, battery + amount);
    }
}
