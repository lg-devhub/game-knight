package com.hollowwoods.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.hollowwoods.game.MainGame;
import com.hollowwoods.game.ui.MenuButton;
import com.hollowwoods.game.utils.Constants;
import com.hollowwoods.game.utils.GameAssets;

public class PauseScreen extends BaseScreen {

    private final GameScreen gameScreen;
    private final MenuButton btnResume;
    private final MenuButton btnMainMenu;

    public PauseScreen(MainGame game, GameScreen gameScreen) {
        super(game);
        this.gameScreen = gameScreen;

        GameAssets assets = game.getAssets();
        float btnW = 280;
        float btnH = 50;
        float btnX = (Constants.VIRTUAL_WIDTH - btnW) / 2f;
        float startY = 320;

        btnResume = new MenuButton("RETOMAR", btnX, startY, btnW, btnH, assets.menuFont);
        btnMainMenu = new MenuButton("MENU PRINCIPAL", btnX, startY - 75, btnW, btnH, assets.menuFont);
    }

    @Override
    public void show() {
        game.getAudioManager().pauseGameplayAudio();
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        game.getAudioManager().update(delta);

        Gdx.gl.glEnable(GL20.GL_BLEND);
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(0.02f, 0.03f, 0.04f, 0.85f));
        shapeRenderer.rect(0, 0, Constants.VIRTUAL_WIDTH, Constants.VIRTUAL_HEIGHT);
        shapeRenderer.end();

        GameAssets assets = game.getAssets();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        assets.titleFont.setColor(new Color(0.95f, 0.25f, 0.25f, 1f));
        String title = "JOGO PAUSADO";
        assets.titleFont.draw(batch, title, (Constants.VIRTUAL_WIDTH - 260) / 2f, 440);

        // Indicador de áudio e atalho de MUTE
        boolean isMuted = game.getAudioManager().isMuted();
        assets.bodyFont.setColor(isMuted ? new Color(0.95f, 0.35f, 0.35f, 1f) : new Color(0.70f, 0.75f, 0.80f, 0.85f));
        String muteText = isMuted ? "[M] Audio: MUTADO (Pressione M para ativar)" : "[M] Audio: LIGADO (Pressione M para mutar)";
        assets.bodyFont.draw(batch, muteText, (Constants.VIRTUAL_WIDTH - 360) / 2f, 180);
        batch.end();

        // Alternância rápida de MUTE com tecla [M]
        if (Gdx.input.isKeyJustPressed(Input.Keys.M)) {
            game.getAudioManager().toggleMute();
        }

        boolean justPressed = Gdx.input.isButtonJustPressed(Input.Buttons.LEFT);
        btnResume.update(mousePos, game.getAudioManager());
        btnMainMenu.update(mousePos, game.getAudioManager());

        btnResume.render(batch, assets.menuBanner, shapeRenderer);
        btnMainMenu.render(batch, assets.menuBanner, shapeRenderer);

        if (btnResume.isClicked(mousePos, justPressed) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.getAudioManager().playSound("button_click");
            game.getAudioManager().resumeGameplayAudio();
            game.setScreen(gameScreen);
        } else if (btnMainMenu.isClicked(mousePos, justPressed)) {
            game.getAudioManager().playSound("button_click");
            game.getAudioManager().stopAllLoops();
            game.goToMainMenu();
        }
    }
}
