package com.hollowwoods.game.world;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.List;

public class CollisionManager {

    private final Rectangle testBounds = new Rectangle();

    /**
     * Move uma entidade com resolução independente de eixos X e Y para permitir deslizamento suave contra paredes.
     */
    public void resolveMovement(Vector2 position, Vector2 velocity, Rectangle bounds,
                                float delta, List<Obstacle> obstacles, float mapWidth, float mapHeight) {
        if (velocity.isZero(0.0001f)) {
            return;
        }

        float halfW = bounds.width / 2f;
        float halfH = bounds.height / 2f;

        // 1. Testa e aplica deslocamento no eixo X
        float targetX = position.x + velocity.x * delta;
        testBounds.set(targetX - halfW, position.y - halfH, bounds.width, bounds.height);

        boolean collidesX = false;
        if (targetX - halfW < 32f || targetX + halfW > mapWidth - 32f) {
            collidesX = true;
        } else {
            for (Obstacle obstacle : obstacles) {
                if (obstacle.solid && testBounds.overlaps(obstacle.bounds)) {
                    collidesX = true;
                    break;
                }
            }
        }

        if (!collidesX) {
            position.x = targetX;
        }

        // 2. Testa e aplica deslocamento no eixo Y
        float targetY = position.y + velocity.y * delta;
        testBounds.set(position.x - halfW, targetY - halfH, bounds.width, bounds.height);

        boolean collidesY = false;
        if (targetY - halfH < 32f || targetY + halfH > mapHeight - 32f) {
            collidesY = true;
        } else {
            for (Obstacle obstacle : obstacles) {
                if (obstacle.solid && testBounds.overlaps(obstacle.bounds)) {
                    collidesY = true;
                    break;
                }
            }
        }

        if (!collidesY) {
            position.y = targetY;
        }

        // Atualiza a caixa de colisão final
        bounds.setPosition(position.x - halfW, position.y - halfH);
    }

    public boolean checkCollision(Rectangle bounds, List<Obstacle> obstacles) {
        for (Obstacle o : obstacles) {
            if (o.solid && bounds.overlaps(o.bounds)) {
                return true;
            }
        }
        return false;
    }
}
