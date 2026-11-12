package com.hollowwoods.game.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Disposable;
import com.hollowwoods.game.entities.InteractiveObject;
import com.hollowwoods.game.entities.Player;
import com.hollowwoods.game.systems.CurseManager;
import com.hollowwoods.game.systems.InventoryManager;
import com.hollowwoods.game.utils.Constants;
import com.hollowwoods.game.utils.GameAssets;
import com.hollowwoods.game.world.GameMap;

/**
 * HUD do jogo reproduzindo fielmente o layout e estilo da imagem de referência:
 * - Canto Superior Esquerdo: Vida e Bateria com barras segmentadas e ícones estilizados.
 * - Canto Superior Direito: Nome do Estágio e Maldição ativa com ícone de caveira vermelha.
 * - Canto Inferior Esquerdo: Losango de objetivo com título e descrição do objetivo atual.
 * - Canto Inferior Direito: Minimapa emoldurado com moldura gótica de ferro e ponto do jogador.
 */
public class HUD implements Disposable {

    private final ShapeRenderer shapeRenderer;
    private final GlyphLayout layout = new GlyphLayout();

    public HUD() {
        shapeRenderer = new ShapeRenderer();
    }

    public void render(SpriteBatch batch, OrthographicCamera screenCamera, GameAssets assets,
                       Player player, GameMap map, CurseManager curseManager,
                       InventoryManager inventory, InteractiveObject nearbyObject) {

        shapeRenderer.setProjectionMatrix(screenCamera.combined);
        batch.setProjectionMatrix(screenCamera.combined);

        // 1. Superior esquerdo: Vida e Bateria segmentadas
        renderHealthAndBattery(batch, assets, player, inventory);

        // 2. Superior direito: Estágio e Maldição ativa
        renderStageAndCurse(batch, assets, map.stageData.stageName, curseManager);

        // 3. Inferior esquerdo: Losango e Texto do Objetivo (duas linhas)
        renderObjective(batch, assets, map.stageData.objectiveText);

        // 4. Inferior direito: Minimapa emoldurado
        renderMinimap(batch, assets, map, player, curseManager);

        // 5. Centro inferior: Prompt de interação contextual
        if (nearbyObject != null) {
            renderInteractionPrompt(batch, assets, nearbyObject.promptText);
        }
    }

    private void renderHealthAndBattery(SpriteBatch batch, GameAssets assets, Player player, InventoryManager inventory) {
        batch.begin();
        batch.setColor(Color.WHITE);

        // Ícone de Cruz de Vida
        float lifeY = Constants.VIRTUAL_HEIGHT - 44;
        batch.draw(assets.uiHealthCross, 24, lifeY, 22, 22);

        // Frame da Barra de Vida
        batch.draw(assets.uiHealthFrame, 52, lifeY, 140, 22);

        // Preenchimento de Vida (Segmentado em 10 blocos)
        float hpRatio = Math.max(0f, player.health / Constants.PLAYER_MAX_HEALTH);
        int totalSegments = 10;
        int activeHpSegments = Math.round(hpRatio * totalSegments);
        float segWidth = 12.2f;
        float segSpacing = 1.0f;

        for (int i = 0; i < totalSegments; i++) {
            float sx = 56 + i * (segWidth + segSpacing);
            if (i < activeHpSegments) {
                batch.setColor(0.85f, 0.15f, 0.18f, 1f); // Vermelho carmesim ativo
            } else {
                batch.setColor(0.20f, 0.05f, 0.07f, 0.7f); // Vazio apagado
            }
            batch.draw(assets.uiHealthFill, sx, lifeY + 4, segWidth, 14);
        }

        // Ícone de Lanterna / Bateria
        float batY = Constants.VIRTUAL_HEIGHT - 74;
        batch.setColor(Color.WHITE);
        batch.draw(assets.uiBatteryBolt, 24, batY, 22, 22);

        // Frame da Barra de Bateria
        batch.draw(assets.uiBatteryFrame, 52, batY, 140, 22);

        // Preenchimento de Bateria (Segmentado em 10 blocos)
        float batRatio = Math.max(0f, player.battery / Constants.PLAYER_MAX_BATTERY);
        int activeBatSegments = Math.round(batRatio * totalSegments);

        for (int i = 0; i < totalSegments; i++) {
            float sx = 56 + i * (segWidth + segSpacing);
            if (i < activeBatSegments) {
                batch.setColor(0.95f, 0.92f, 0.82f, 1f); // Branco/âmbar brilhante
            } else {
                batch.setColor(0.18f, 0.18f, 0.20f, 0.7f); // Vazio
            }
            batch.draw(assets.uiBatteryFill, sx, batY + 4, segWidth, 14);
        }

        // Atalhos de inventário discretos
        batch.setColor(Color.WHITE);
        assets.smallFont.setColor(new Color(0.70f, 0.75f, 0.80f, 0.85f));
        assets.smallFont.draw(batch, "[Q] Pilhas: " + inventory.getBatteryPacks(), 205, lifeY + 16);
        assets.smallFont.draw(batch, "[R] Curativos: " + inventory.getMedkits(), 205, batY + 16);
        batch.end();
    }

    private void renderStageAndCurse(SpriteBatch batch, GameAssets assets, String stageName, CurseManager curseManager) {
        batch.begin();
        // Estágio: "Stage 1 - The Awakening"
        assets.bodyFont.setColor(new Color(0.92f, 0.94f, 0.96f, 0.95f));
        layout.setText(assets.bodyFont, stageName);
        float stageX = Constants.VIRTUAL_WIDTH - layout.width - 24;
        assets.bodyFont.draw(batch, stageName, stageX, Constants.VIRTUAL_HEIGHT - 24);

        // Maldição: "☠ Curse: Blackout"
        String curseText = "Curse: " + curseManager.getActiveCurse().getDisplayName();
        layout.setText(assets.smallFont, curseText);
        float curseX = Constants.VIRTUAL_WIDTH - layout.width - 24;

        batch.setColor(Color.WHITE);
        batch.draw(assets.uiCurseSkull, curseX - 24, Constants.VIRTUAL_HEIGHT - 62, 18, 18);

        assets.smallFont.setColor(new Color(0.95f, 0.30f, 0.30f, 1f));
        assets.smallFont.draw(batch, curseText, curseX, Constants.VIRTUAL_HEIGHT - 48);
        batch.end();
    }

    private void renderObjective(SpriteBatch batch, GameAssets assets, String objectiveText) {
        batch.begin();
        batch.setColor(Color.WHITE);
        float objX = 24;
        float objY = 54;

        // Ícone de losango ◇
        batch.draw(assets.uiObjectiveDiamond, objX, objY - 4, 18, 18);

        // Texto do Objetivo em duas linhas conforme a referência:
        // "Objective:"
        // "Find the key and reach the cabin."
        assets.bodyFont.setColor(new Color(0.85f, 0.88f, 0.90f, 1f));
        assets.bodyFont.draw(batch, "Objective:", objX + 26, objY + 12);

        assets.smallFont.setColor(new Color(0.72f, 0.76f, 0.80f, 0.90f));
        assets.smallFont.draw(batch, objectiveText, objX + 26, objY - 10);
        batch.end();
    }

    private void renderMinimap(SpriteBatch batch, GameAssets assets, GameMap map, Player player, CurseManager curseManager) {
        int mmSize = 120;
        int mmX = Constants.VIRTUAL_WIDTH - mmSize - 20;
        int mmY = 20;

        // Fundo do mapa interno
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(0.05f, 0.06f, 0.08f, 0.92f));
        shapeRenderer.rect(mmX + 6, mmY + 6, mmSize - 12, mmSize - 12);

        if (curseManager.isMinimapDisabled()) {
            // Efeito estático sob maldição LOST_PATH
            for (int i = 0; i < 35; i++) {
                shapeRenderer.setColor(new Color(0.35f, 0.38f, 0.42f, MathUtils.random(0.15f, 0.5f)));
                shapeRenderer.rect(
                        mmX + MathUtils.random(8, mmSize - 20),
                        mmY + MathUtils.random(8, mmSize - 20),
                        MathUtils.random(4, 18),
                        1.5f
                );
            }
            shapeRenderer.end();

            batch.begin();
            assets.smallFont.setColor(new Color(0.92f, 0.28f, 0.28f, 0.95f));
            assets.smallFont.draw(batch, "SEM SINAL", mmX + 24, mmY + 64);
            batch.end();
        } else {
            // Desenha grade de caminhos explorados em tom cinza suave
            float tileW = (mmSize - 16f) / Constants.MAP_WIDTH;
            float tileH = (mmSize - 16f) / Constants.MAP_HEIGHT;

            for (int x = 0; x < Constants.MAP_WIDTH; x += 2) {
                for (int y = 0; y < Constants.MAP_HEIGHT; y += 2) {
                    if (map.isExplored(x, y)) {
                        shapeRenderer.setColor(new Color(0.24f, 0.28f, 0.32f, 0.75f));
                        shapeRenderer.rect(mmX + 8 + x * tileW, mmY + 8 + y * tileH, tileW * 2f, tileH * 2f);
                    }
                }
            }

            // Ponto de Elias no mapa (círculo vermelho brilhante com halo branco)
            float pRelX = (player.position.x / (Constants.MAP_WIDTH * Constants.TILE_SIZE)) * (mmSize - 16f);
            float pRelY = (player.position.y / (Constants.MAP_HEIGHT * Constants.TILE_SIZE)) * (mmSize - 16f);

            shapeRenderer.setColor(new Color(1f, 1f, 1f, 0.9f));
            shapeRenderer.circle(mmX + 8 + pRelX, mmY + 8 + pRelY, 3.5f);
            shapeRenderer.setColor(new Color(0.92f, 0.20f, 0.20f, 1f));
            shapeRenderer.circle(mmX + 8 + pRelX, mmY + 8 + pRelY, 2.2f);
            shapeRenderer.end();
        }

        // Desenha a Moldura Gótica de Ferro por cima
        batch.begin();
        batch.setColor(Color.WHITE);
        batch.draw(assets.uiMinimapFrame, mmX, mmY, mmSize, mmSize);
        batch.end();
    }

    private void renderInteractionPrompt(SpriteBatch batch, GameAssets assets, String promptText) {
        String fullPrompt = "[E] " + promptText;
        layout.setText(assets.bodyFont, fullPrompt);

        float boxW = Math.max(220, layout.width + 36);
        float boxH = 42;
        float boxX = (Constants.VIRTUAL_WIDTH - boxW) / 2f;
        float boxY = 85;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(0.04f, 0.05f, 0.07f, 0.88f));
        shapeRenderer.rect(boxX, boxY, boxW, boxH);
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(new Color(0.85f, 0.75f, 0.45f, 0.85f));
        shapeRenderer.rect(boxX, boxY, boxW, boxH);
        shapeRenderer.end();

        batch.begin();
        assets.bodyFont.setColor(new Color(0.96f, 0.94f, 0.88f, 1f));
        float textX = boxX + (boxW - layout.width) / 2f;
        float textY = boxY + (boxH + layout.height) / 2f;
        assets.bodyFont.draw(batch, fullPrompt, textX, textY);
        batch.end();
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
    }
}
