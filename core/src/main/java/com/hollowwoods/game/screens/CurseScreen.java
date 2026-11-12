package com.hollowwoods.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.hollowwoods.game.MainGame;
import com.hollowwoods.game.systems.CurseManager;
import com.hollowwoods.game.utils.Constants;
import com.hollowwoods.game.utils.GameAssets;

public class CurseScreen extends BaseScreen {

    private final CurseManager.CurseType curse;
    private final GlyphLayout layout = new GlyphLayout();
    private float pulseTimer = 0f;

    public CurseScreen(MainGame game) {
        super(game);
        this.curse = game.getCurseManager().getActiveCurse();
    }

    @Override
    public void show() {
        game.getAudioManager().playSound("curse_trigger");
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        pulseTimer += delta * 3f;

        GameAssets assets = game.getAssets();
        batch.setProjectionMatrix(camera.combined);

        float cardW = 680;
        float cardH = 380;
        float cardX = (Constants.VIRTUAL_WIDTH - cardW) / 2f;
        float cardY = (Constants.VIRTUAL_HEIGHT - cardH) / 2f;

        Gdx.gl.glEnable(GL20.GL_BLEND);
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(0.05f, 0.03f, 0.04f, 0.94f));
        shapeRenderer.rect(cardX, cardY, cardW, cardH);
        shapeRenderer.end();

        float pulse = 0.6f + 0.4f * (float) Math.sin(pulseTimer);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(new Color(0.85f * pulse, 0.15f, 0.18f, 0.9f));
        shapeRenderer.rect(cardX, cardY, cardW, cardH);
        shapeRenderer.rect(cardX + 4, cardY + 4, cardW - 8, cardH - 8);
        shapeRenderer.end();

        batch.begin();
        assets.bodyFont.setColor(new Color(0.75f, 0.75f, 0.8f, 0.9f));
        String sub = "A FLORESTA MARCOU SEU DESTINO";
        layout.setText(assets.bodyFont, sub);
        assets.bodyFont.draw(batch, sub, (Constants.VIRTUAL_WIDTH - layout.width) / 2f, cardY + cardH - 40);

        batch.setColor(Color.WHITE);
        batch.draw(assets.uiCurseSkull, (Constants.VIRTUAL_WIDTH - 48) / 2f, cardY + cardH - 120, 48, 48);

        assets.titleFont.setColor(new Color(0.95f, 0.2f, 0.25f, 1f));
        String curseName = "MALDICAO: " + curse.getDisplayName();
        layout.setText(assets.titleFont, curseName);
        assets.titleFont.draw(batch, curseName, (Constants.VIRTUAL_WIDTH - layout.width) / 2f, cardY + cardH - 145);

        assets.headerFont.setColor(new Color(0.9f, 0.88f, 0.85f, 0.95f));
        layout.setText(assets.headerFont, curse.getDescription());
        assets.headerFont.draw(batch, curse.getDescription(), (Constants.VIRTUAL_WIDTH - layout.width) / 2f, cardY + 140);

        assets.bodyFont.setColor(new Color(0.65f, 0.65f, 0.7f, 0.85f));
        String note = "Esta restricao permanecera ativa durante toda a sua travessia.";
        layout.setText(assets.bodyFont, note);
        assets.bodyFont.draw(batch, note, (Constants.VIRTUAL_WIDTH - layout.width) / 2f, cardY + 95);

        assets.smallFont.setColor(new Color(1f, 0.85f, 0.45f, pulse));
        String prompt = "Pressione [ESPAÇO] ou [ENTER] para Entrar na Floresta";
        layout.setText(assets.smallFont, prompt);
        assets.smallFont.draw(batch, prompt, (Constants.VIRTUAL_WIDTH - layout.width) / 2f, cardY + 45);

        batch.draw(assets.vignette, 0, 0, Constants.VIRTUAL_WIDTH, Constants.VIRTUAL_HEIGHT);
        batch.end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) ||
                Gdx.input.isKeyJustPressed(Input.Keys.ENTER) ||
                Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            game.getAudioManager().playSound("button_click");
            game.proceedFromCurse();
        }
    }
}
