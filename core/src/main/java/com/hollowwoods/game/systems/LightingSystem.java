package com.hollowwoods.game.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.hollowwoods.game.entities.Player;
import com.hollowwoods.game.utils.Constants;
import com.hollowwoods.game.utils.GameAssets;

/**
 * Sistema de iluminação 2D cinematográfica por FBO (Lightmap Multiplicativo + Aditivo).
 * Garante iluminação ambiente legível e segura (nunca breu absoluto - AGENTS.md § 8),
 * cone de lanterna âmbar suave com falloff radial e halo de 360° ao redor do jogador.
 */
public class LightingSystem implements Disposable {

    private FrameBuffer lightFbo;
    private TextureRegion fboRegion;
    private final SpriteBatch lightBatch;

    // Cor da escuridão ambiente: cinza azulado escuro (permite distinguir caminhos e obstáculos)
    private final Color ambientDarkness = new Color(0.20f, 0.22f, 0.26f, 1f);

    // Cor âmbar suave da lanterna (#fff2c2)
    private final Color flashlightColor = new Color(1.0f, 0.94f, 0.74f, 0.95f);

    // Cor do halo de 360° em torno de Elias
    private final Color haloColor = new Color(0.65f, 0.60f, 0.50f, 0.55f);

    public LightingSystem() {
        lightBatch = new SpriteBatch();
        createFbo(Constants.VIRTUAL_WIDTH, Constants.VIRTUAL_HEIGHT);
    }

    private void createFbo(int width, int height) {
        if (lightFbo != null) {
            lightFbo.dispose();
        }
        lightFbo = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, false);
        fboRegion = new TextureRegion(lightFbo.getColorBufferTexture());
        fboRegion.flip(false, true); // Corrige inversão Y do FBO no OpenGL
    }

    public void resize(int width, int height) {
        // Mantém resolução virtual consistente
    }

    /**
     * Renderiza o mapa de luzes na textura do FBO.
     */
    public void updateLightmap(OrthographicCamera worldCamera, GameAssets assets,
                               Player player, boolean isBlackout, int stageNumber) {

        lightFbo.begin();

        // 1. Limpa o FBO com a cor ambiente (escuridão legível, nunca 100% preta)
        Gdx.gl.glClearColor(ambientDarkness.r, ambientDarkness.g, ambientDarkness.b, ambientDarkness.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        lightBatch.setProjectionMatrix(worldCamera.combined);
        lightBatch.begin();
        // Modo Aditivo (adiciona luminosidade sobre a escuridão ambiente)
        lightBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);

        // 2. Halo de Luz Circular (360°) em torno de Elias
        if (assets.lightHalo != null) {
            float haloSize = 220f;
            lightBatch.setColor(haloColor);
            lightBatch.draw(
                    assets.lightHalo,
                    player.position.x - haloSize / 2f,
                    player.position.y - haloSize / 2f,
                    haloSize, haloSize
            );
        }

        // 3. Cone Direcional da Lanterna
        if (!isBlackout && player.battery > 0f) {
            // Tremulação sutil de bateria fraca
            float flicker = 1.0f;
            if (player.battery < 25f) {
                flicker = 0.75f + MathUtils.random(0f, 0.25f);
            }

            lightBatch.setColor(
                    flashlightColor.r * flicker,
                    flashlightColor.g * flicker,
                    flashlightColor.b * flicker,
                    flashlightColor.a * flicker
            );

            // Desenha o cone alinhado com o ângulo de mira da lanterna
            float coneW = Constants.FLASHLIGHT_RANGE * 1.15f;
            float coneH = Constants.FLASHLIGHT_RANGE * 1.15f;
            float originX = 64f * (coneW / 512f);
            float originY = 256f * (coneH / 512f);

            lightBatch.draw(
                    assets.lightCone,
                    player.position.x - originX,
                    player.position.y - originY,
                    originX, originY,
                    coneW, coneH,
                    1f, 1f,
                    player.flashlightAngle,
                    0, 0, 512, 512,
                    false, false
            );
        }

        // 4. Luzes de Cenário Específicas por Estágio
        if (stageNumber == 1 && assets.lightHalo != null) {
            // Luz quente âmbar da janela da Cabana (Stage 1)
            float cabinLightX = 26f * Constants.TILE_SIZE + 32f;
            float cabinLightY = 24f * Constants.TILE_SIZE + 32f;
            lightBatch.setColor(1.0f, 0.75f, 0.35f, 0.85f);
            lightBatch.draw(assets.lightHalo, cabinLightX - 160f, cabinLightY - 160f, 320f, 320f);
        } else if (stageNumber == 3 && assets.lightHalo != null) {
            // Pulso carmesim do Altar Ritualístico (Stage 3)
            float altarX = 18f * Constants.TILE_SIZE + 64f;
            float altarY = 18f * Constants.TILE_SIZE + 32f;
            float pulse = 0.65f + 0.35f * (float) Math.sin(player.stateTime * 2.5f);
            lightBatch.setColor(0.95f, 0.20f, 0.25f, 0.80f * pulse);
            lightBatch.draw(assets.lightHalo, altarX - 180f, altarY - 180f, 360f, 360f);
        }

        lightBatch.end();
        lightFbo.end();
    }

    /**
     * Aplica o Lightmap sobre a cena renderizada no mundo.
     */
    public void renderLightmap(SpriteBatch batch, OrthographicCamera screenCamera) {
        batch.setProjectionMatrix(screenCamera.combined);
        batch.begin();
        // Multiplica a cor do cenário pela cor do lightmap:
        // Onde o lightmap é âmbar/branco (1.0), a textura original é 100% revelada;
        // Onde é ambiente (0.20), fica na penumbra atmosférica legível.
        batch.setBlendFunction(GL20.GL_DST_COLOR, GL20.GL_ZERO);
        batch.setColor(Color.WHITE);
        batch.draw(fboRegion, 0, 0, Constants.VIRTUAL_WIDTH, Constants.VIRTUAL_HEIGHT);
        batch.end();

        // Restaura mistura padrão de transparência alfa
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    }

    /**
     * Efeito volumétrico sutil de poeira e névoa iluminada pelo feixe da lanterna.
     */
    public void renderVolumetricBeam(SpriteBatch batch, OrthographicCamera worldCamera,
                                     GameAssets assets, Player player, boolean isBlackout) {
        if (isBlackout || player.battery <= 0f) return;

        batch.setProjectionMatrix(worldCamera.combined);
        batch.begin();
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);

        batch.setColor(1.0f, 0.92f, 0.70f, 0.16f); // Muito suave e translúcido
        float coneW = Constants.FLASHLIGHT_RANGE * 1.15f;
        float coneH = Constants.FLASHLIGHT_RANGE * 1.15f;
        float originX = 64f * (coneW / 512f);
        float originY = 256f * (coneH / 512f);

        batch.draw(
                assets.lightCone,
                player.position.x - originX,
                player.position.y - originY,
                originX, originY,
                coneW, coneH,
                1f, 1f,
                player.flashlightAngle,
                0, 0, 512, 512,
                false, false
        );

        batch.end();
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    }

    @Override
    public void dispose() {
        if (lightFbo != null) {
            lightFbo.dispose();
        }
        lightBatch.dispose();
    }
}
