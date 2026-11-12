package com.hollowwoods.game.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.hollowwoods.game.utils.Constants;
import com.hollowwoods.game.utils.GameAssets;

public class DialogBox {

    private boolean isOpen = false;
    private String title = "";
    private String content = "";

    public void show(String title, String content) {
        this.isOpen = true;
        this.title = title;
        this.content = content;
    }

    public void close() {
        this.isOpen = false;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void render(SpriteBatch batch, ShapeRenderer shapeRenderer, GameAssets assets) {
        if (!isOpen) return;

        float boxW = 680;
        float boxH = 340;
        float boxX = (Constants.VIRTUAL_WIDTH - boxW) / 2f;
        float boxY = (Constants.VIRTUAL_HEIGHT - boxH) / 2f;

        // Fundo escuro com alto contraste
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(0.06f, 0.07f, 0.09f, 0.95f));
        shapeRenderer.rect(boxX, boxY, boxW, boxH);
        shapeRenderer.end();

        // Moldura envelhecida
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(new Color(0.65f, 0.50f, 0.35f, 0.9f));
        shapeRenderer.rect(boxX + 4, boxY + 4, boxW - 8, boxH - 8);
        shapeRenderer.rect(boxX + 8, boxY + 8, boxW - 16, boxH - 16);
        shapeRenderer.end();

        batch.begin();
        // Título da nota
        assets.headerFont.setColor(new Color(0.95f, 0.75f, 0.45f, 1f));
        assets.headerFont.draw(batch, title, boxX + 30, boxY + boxH - 35);

        // Corpo do texto com quebra de linha automática
        assets.bodyFont.setColor(new Color(0.88f, 0.90f, 0.92f, 0.95f));
        assets.bodyFont.draw(batch, content, boxX + 30, boxY + boxH - 80, boxW - 60, -1, true);

        // Rodapé de ação
        assets.smallFont.setColor(new Color(0.65f, 0.65f, 0.70f, 0.8f));
        assets.smallFont.draw(batch, "Pressione [ESPAÇO] ou [E] para fechar", boxX + 30, boxY + 35);
        batch.end();
    }
}
