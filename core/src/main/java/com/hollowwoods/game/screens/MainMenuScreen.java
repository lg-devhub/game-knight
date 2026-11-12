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

public class MainMenuScreen extends BaseScreen {

    private final MenuButton btnNewGame;
    private final MenuButton btnContinue;
    private final MenuButton btnSettings;
    private final MenuButton btnExit;

    private float fogScroll = 0f;
    private boolean showSettings = false;

    // Controles de áudio no modal de configurações
    private float masterVol;
    private float musicVol;
    private float ambientVol;
    private float sfxVol;

    public MainMenuScreen(MainGame game) {
        super(game);

        GameAssets assets = game.getAssets();
        float btnW = 280;
        float btnH = 50;
        float btnX = 140;
        float startY = 320;

        btnNewGame = new MenuButton("NEW GAME", btnX, startY, btnW, btnH, assets.menuFont);
        btnContinue = new MenuButton("CONTINUE", btnX, startY - 65, btnW, btnH, assets.menuFont);
        btnContinue.enabled = game.getSaveManager().hasContinue();

        btnSettings = new MenuButton("SETTINGS", btnX, startY - 130, btnW, btnH, assets.menuFont);
        btnExit = new MenuButton("EXIT", btnX, startY - 195, btnW, btnH, assets.menuFont);

        masterVol = game.getSaveManager().getMasterVolume();
        musicVol = game.getSaveManager().getMusicVolume();
        ambientVol = game.getSaveManager().getAmbientVolume();
        sfxVol = game.getSaveManager().getSfxVolume();
    }

    @Override
    public void show() {
        game.getAudioManager().playMenuMusic();
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        game.getAudioManager().update(delta);
        fogScroll += delta * 15f;

        GameAssets assets = game.getAssets();
        batch.setProjectionMatrix(camera.combined);

        // 1. Fundo atmosférico
        batch.begin();
        batch.setColor(Color.WHITE);
        batch.draw(assets.titleBg, 0, 0, Constants.VIRTUAL_WIDTH, Constants.VIRTUAL_HEIGHT);

        // 2. Névoa sutil animada
        batch.setColor(0.12f, 0.16f, 0.20f, 0.20f);
        batch.draw(
                assets.fogTexture,
                0, 0, Constants.VIRTUAL_WIDTH, Constants.VIRTUAL_HEIGHT,
                (int) fogScroll, 0, 512, 512, false, false
        );

        // 3. Logo gótico do título
        batch.setColor(Color.WHITE);
        batch.draw(assets.titleLogo, 60, Constants.VIRTUAL_HEIGHT - 240, 560, 180);

        // Recorde
        int hiScore = game.getSaveManager().getHighScore();
        if (hiScore > 0) {
            assets.bodyFont.setColor(new Color(0.85f, 0.75f, 0.45f, 0.9f));
            assets.bodyFont.draw(batch, "Melhor Recorde: " + hiScore + " pts", 80, Constants.VIRTUAL_HEIGHT - 250);
        }

        // Tagline no canto inferior direito (conforme arte de referência)
        assets.smallFont.setColor(new Color(0.72f, 0.75f, 0.80f, 0.75f));
        assets.smallFont.draw(batch, "SOME PLACES NEVER LET YOU GO...", Constants.VIRTUAL_WIDTH - 270, 32);

        // Vinheta
        batch.draw(assets.vignette, 0, 0, Constants.VIRTUAL_WIDTH, Constants.VIRTUAL_HEIGHT);
        batch.end();

        // 4. Botões do menu estilizados com banner vermelho
        boolean justPressed = Gdx.input.isButtonJustPressed(Input.Buttons.LEFT);
        btnNewGame.update(mousePos, game.getAudioManager());
        btnContinue.update(mousePos, game.getAudioManager());
        btnSettings.update(mousePos, game.getAudioManager());
        btnExit.update(mousePos, game.getAudioManager());

        btnNewGame.render(batch, assets.menuBanner, shapeRenderer);
        btnContinue.render(batch, assets.menuBanner, shapeRenderer);
        btnSettings.render(batch, assets.menuBanner, shapeRenderer);
        btnExit.render(batch, assets.menuBanner, shapeRenderer);

        // 5. Ações dos botões
        if (!showSettings) {
            if (btnNewGame.isClicked(mousePos, justPressed)) {
                game.getAudioManager().playSound("button_click");
                game.startNewGame();
            } else if (btnContinue.isClicked(mousePos, justPressed)) {
                game.getAudioManager().playSound("button_click");
                game.continueSavedGame();
            } else if (btnSettings.isClicked(mousePos, justPressed)) {
                game.getAudioManager().playSound("button_click");
                showSettings = true;
            } else if (btnExit.isClicked(mousePos, justPressed)) {
                Gdx.app.exit();
            }
        } else {
            renderSettingsModal(justPressed);
        }
    }

    private void renderSettingsModal(boolean justPressed) {
        float modalW = 580;
        float modalH = 400;
        float modalX = (Constants.VIRTUAL_WIDTH - modalW) / 2f;
        float modalY = (Constants.VIRTUAL_HEIGHT - modalH) / 2f;

        Gdx.gl.glEnable(GL20.GL_BLEND);
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(0.04f, 0.05f, 0.07f, 0.95f));
        shapeRenderer.rect(modalX, modalY, modalW, modalH);

        // Sliders de volume
        drawSliderBar(modalX + 240, modalY + 270, 240, masterVol);
        drawSliderBar(modalX + 240, modalY + 210, 240, musicVol);
        drawSliderBar(modalX + 240, modalY + 150, 240, ambientVol);
        drawSliderBar(modalX + 240, modalY + 90, 240, sfxVol);
        shapeRenderer.end();

        // Moldura
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(new Color(0.7f, 0.2f, 0.2f, 0.9f));
        shapeRenderer.rect(modalX, modalY, modalW, modalH);
        shapeRenderer.end();

        // Interação com sliders
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            if (mousePos.x >= modalX + 240 && mousePos.x <= modalX + 480) {
                if (mousePos.y >= modalY + 260 && mousePos.y <= modalY + 285) {
                    masterVol = (mousePos.x - (modalX + 240)) / 240f;
                    game.getAudioManager().setMasterVolume(masterVol);
                } else if (mousePos.y >= modalY + 200 && mousePos.y <= modalY + 225) {
                    musicVol = (mousePos.x - (modalX + 240)) / 240f;
                    game.getAudioManager().setMusicVolume(musicVol);
                } else if (mousePos.y >= modalY + 140 && mousePos.y <= modalY + 165) {
                    ambientVol = (mousePos.x - (modalX + 240)) / 240f;
                    game.getAudioManager().setAmbientVolume(ambientVol);
                } else if (mousePos.y >= modalY + 80 && mousePos.y <= modalY + 105) {
                    sfxVol = (mousePos.x - (modalX + 240)) / 240f;
                    game.getAudioManager().setSfxVolume(sfxVol);
                }
            }
        }

        GameAssets assets = game.getAssets();
        batch.begin();
        assets.headerFont.setColor(new Color(0.95f, 0.25f, 0.25f, 1f));
        assets.headerFont.draw(batch, "CONFIGURACOES DE AUDIO", modalX + 30, modalY + modalH - 30);

        assets.bodyFont.setColor(Color.WHITE);
        assets.bodyFont.draw(batch, "Volume Geral:", modalX + 40, modalY + 282);
        assets.bodyFont.draw(batch, "Trilha / Musica:", modalX + 40, modalY + 222);
        assets.bodyFont.draw(batch, "Ambiente / Vento:", modalX + 40, modalY + 162);
        assets.bodyFont.draw(batch, "Efeitos Sonoros:", modalX + 40, modalY + 102);

        assets.smallFont.setColor(new Color(0.85f, 0.85f, 0.85f, 0.9f));
        assets.smallFont.draw(batch, (int)(masterVol * 100) + "%", modalX + 495, modalY + 282);
        assets.smallFont.draw(batch, (int)(musicVol * 100) + "%", modalX + 495, modalY + 222);
        assets.smallFont.draw(batch, (int)(ambientVol * 100) + "%", modalX + 495, modalY + 162);
        assets.smallFont.draw(batch, (int)(sfxVol * 100) + "%", modalX + 495, modalY + 102);

        // Botão de Mute
        boolean isMuted = game.getAudioManager().isMuted();
        assets.bodyFont.setColor(isMuted ? new Color(0.95f, 0.35f, 0.35f, 1f) : new Color(0.70f, 0.85f, 0.70f, 1f));
        String muteLabel = isMuted ? "[ MODO SILENCIOSO: ATIVADO (Clique para desmutar) ]" : "[ MODO SILENCIOSO: DESATIVADO (Clique para mutar) ]";
        assets.bodyFont.draw(batch, muteLabel, modalX + 80, modalY + 58);

        assets.smallFont.setColor(new Color(0.8f, 0.8f, 0.8f, 0.85f));
        assets.smallFont.draw(batch, "[ Clique aqui ou pressione ESC para fechar ]", modalX + 130, modalY + 28);
        batch.end();

        // Clique no Mute
        if (justPressed && mousePos.x >= modalX + 70 && mousePos.x <= modalX + 510 && mousePos.y >= modalY + 45 && mousePos.y <= modalY + 70) {
            game.getAudioManager().toggleMute();
            game.getAudioManager().playSound("button_click");
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) ||
                (justPressed && mousePos.y >= modalY + 10 && mousePos.y <= modalY + 40 && mousePos.x >= modalX + 90 && mousePos.x <= modalX + 480)) {
            game.getSaveManager().saveAudioSettings(masterVol, musicVol, ambientVol, sfxVol);
            showSettings = false;
        }
    }

    private void drawSliderBar(float x, float y, float w, float val) {
        shapeRenderer.setColor(new Color(0.18f, 0.22f, 0.26f, 0.8f));
        shapeRenderer.rect(x, y, w, 12);
        shapeRenderer.setColor(new Color(0.85f, 0.25f, 0.25f, 1f));
        shapeRenderer.rect(x, y, w * Math.max(0f, Math.min(1f, val)), 12);
    }
}
