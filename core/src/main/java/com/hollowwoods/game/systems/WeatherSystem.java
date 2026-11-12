package com.hollowwoods.game.systems;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Disposable;
import com.hollowwoods.game.utils.Constants;
import com.hollowwoods.game.utils.GameAssets;

/**
 * Sistema de Clima e Atmosfera:
 * - Chuva fina, discreta e de baixo contraste (AGENTS.md § 8).
 * - Pequenas ondulações (ripples) suaves ao tocar o solo.
 * - Neblina volumétrica em camadas suaves e lentas sem prejudicar a leitura do mapa.
 */
public class WeatherSystem implements Disposable {

    private static class Raindrop {
        float x, y;
        float speed;
        float length;
        float alpha;

        Raindrop() {
            reset(true);
        }

        void reset(boolean randomizeY) {
            x = MathUtils.random(-50, Constants.VIRTUAL_WIDTH + 50);
            y = randomizeY ? MathUtils.random(0, Constants.VIRTUAL_HEIGHT) : Constants.VIRTUAL_HEIGHT + MathUtils.random(5, 30);
            speed = MathUtils.random(420f, 620f);
            length = MathUtils.random(10f, 16f);
            alpha = MathUtils.random(0.14f, 0.24f); // Fina e translúcida
        }
    }

    private static class Splash {
        float x, y;
        float radius;
        float maxRadius;
        float life;
        float maxLife;
        boolean active;
    }

    private final Raindrop[] raindrops;
    private final Splash[] splashes;
    private final ShapeRenderer shapeRenderer;

    private float fogScroll1 = 0f;
    private float fogScroll2 = 0f;

    public WeatherSystem() {
        this.shapeRenderer = new ShapeRenderer();
        this.raindrops = new Raindrop[130];
        for (int i = 0; i < raindrops.length; i++) {
            raindrops[i] = new Raindrop();
        }

        this.splashes = new Splash[25];
        for (int i = 0; i < splashes.length; i++) {
            splashes[i] = new Splash();
        }
    }

    public void update(float delta) {
        // Atualiza rolagem contínua da neblina
        fogScroll1 += delta * 9f;
        fogScroll2 += delta * 14f;

        // Atualiza gotas de chuva
        float windDrift = -110f * delta; // Inclinação suave com o vento
        for (Raindrop r : raindrops) {
            r.y -= r.speed * delta;
            r.x += windDrift;

            if (r.y < 0) {
                spawnSplash(r.x, MathUtils.random(5f, 40f));
                r.reset(false);
            }
        }

        // Atualiza ondulações
        for (Splash s : splashes) {
            if (s.active) {
                s.life += delta;
                s.radius = (s.life / s.maxLife) * s.maxRadius;
                if (s.life >= s.maxLife) {
                    s.active = false;
                }
            }
        }
    }

    private void spawnSplash(float x, float y) {
        for (Splash s : splashes) {
            if (!s.active) {
                s.active = true;
                s.x = x;
                s.y = y;
                s.radius = 1f;
                s.maxRadius = MathUtils.random(4f, 8f);
                s.life = 0f;
                s.maxLife = MathUtils.random(0.18f, 0.28f);
                break;
            }
        }
    }

    /**
     * Renderiza chuva fina e ondulações (em espaço de tela, sem poluir a visão do mundo).
     */
    public void renderRain(OrthographicCamera screenCamera) {
        shapeRenderer.setProjectionMatrix(screenCamera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        // Gotas de chuva finas com baixa opacidade
        for (Raindrop r : raindrops) {
            shapeRenderer.setColor(0.68f, 0.76f, 0.86f, r.alpha);
            // Traço inclinado suave
            shapeRenderer.line(r.x, r.y, r.x - 3f, r.y - r.length);
        }

        // Ondulações no chão
        for (Splash s : splashes) {
            if (s.active) {
                float splashAlpha = (1f - (s.life / s.maxLife)) * 0.22f;
                shapeRenderer.setColor(0.70f, 0.78f, 0.88f, splashAlpha);
                shapeRenderer.ellipse(s.x - s.radius, s.y - s.radius * 0.4f, s.radius * 2f, s.radius * 0.8f);
            }
        }

        shapeRenderer.end();
    }

    /**
     * Renderiza camadas sutis de neblina volumétrica sobre o cenário.
     */
    public void renderFog(SpriteBatch batch, OrthographicCamera screenCamera, GameAssets assets) {
        batch.setProjectionMatrix(screenCamera.combined);
        batch.begin();

        // Camada 1: Neblina lenta e profunda
        batch.setColor(0.14f, 0.18f, 0.22f, 0.12f);
        batch.draw(
                assets.fogTexture,
                0, 0, Constants.VIRTUAL_WIDTH, Constants.VIRTUAL_HEIGHT,
                (int) fogScroll1, 0, 512, 512, false, false
        );

        // Camada 2: Neblina mais alta e etérea
        batch.setColor(0.18f, 0.22f, 0.28f, 0.08f);
        batch.draw(
                assets.fogTexture,
                0, 0, Constants.VIRTUAL_WIDTH, Constants.VIRTUAL_HEIGHT,
                (int) fogScroll2, (int) (fogScroll2 * 0.5f), 512, 512, false, false
        );

        batch.setColor(Color.WHITE);
        batch.end();
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
    }
}
