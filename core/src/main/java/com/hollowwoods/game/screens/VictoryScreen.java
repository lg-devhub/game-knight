package com.hollowwoods.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.hollowwoods.game.MainGame;
import com.hollowwoods.game.systems.ScoreManager;
import com.hollowwoods.game.ui.MenuButton;
import com.hollowwoods.game.utils.Constants;
import com.hollowwoods.game.utils.GameAssets;

public class VictoryScreen extends BaseScreen {

    private final int finalScore;
    private final MenuButton btnPlayAgain;
    private final MenuButton btnMainMenu;
    private final GlyphLayout layout = new GlyphLayout();

    public VictoryScreen(MainGame game, int finalScore) {
        super(game);
        this.finalScore = finalScore;

        GameAssets assets = game.getAssets();
        float btnW = 260;
        float btnH = 50;
        float btnX1 = (Constants.VIRTUAL_WIDTH / 2f) - btnW - 20;
        float btnX2 = (Constants.VIRTUAL_WIDTH / 2f) + 20;
        float btnY = 90;

        btnPlayAgain = new MenuButton("PLAY AGAIN", btnX1, btnY, btnW, btnH, assets.menuFont);
        btnMainMenu = new MenuButton("MAIN MENU", btnX2, btnY, btnW, btnH, assets.menuFont);
    }

    @Override
    public void show() {
        game.getAudioManager().playVictoryAudio();
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        GameAssets assets = game.getAssets();
        ScoreManager sm = game.getScoreManager();
        int bestScore = game.getSaveManager().getHighScore();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        batch.setColor(0.18f, 0.14f, 0.16f, 1f);
        batch.draw(assets.titleBg, 0, 0, Constants.VIRTUAL_WIDTH, Constants.VIRTUAL_HEIGHT);
        batch.setColor(Color.WHITE);

        // Título de Vitória
        assets.titleFont.setColor(new Color(0.95f, 0.82f, 0.45f, 1f));
        String title = "ESCAPOU DA FLORESTA SOMBRIA";
        layout.setText(assets.titleFont, title);
        assets.titleFont.draw(batch, title, (Constants.VIRTUAL_WIDTH - layout.width) / 2f, Constants.VIRTUAL_HEIGHT - 100);

        // Epílogo Narrativo
        assets.bodyFont.setColor(new Color(0.90f, 0.92f, 0.95f, 0.95f));
        String epilogue1 = "Ao ativar os tres selos ancestrais, o olhar do Observador se despedacou em cinzas.";
        String epilogue2 = "Os primeiros raios da alvorada atravessam as copas das arvores. A ilusao finalmente cessou.";
        String epilogue3 = "Elias encontra o amuleto de sua irma na saida da trilha. O ciclo foi quebrado.";

        layout.setText(assets.bodyFont, epilogue1);
        assets.bodyFont.draw(batch, epilogue1, (Constants.VIRTUAL_WIDTH - layout.width) / 2f, Constants.VIRTUAL_HEIGHT - 160);
        layout.setText(assets.bodyFont, epilogue2);
        assets.bodyFont.draw(batch, epilogue2, (Constants.VIRTUAL_WIDTH - layout.width) / 2f, Constants.VIRTUAL_HEIGHT - 190);
        layout.setText(assets.bodyFont, epilogue3);
        assets.bodyFont.draw(batch, epilogue3, (Constants.VIRTUAL_WIDTH - layout.width) / 2f, Constants.VIRTUAL_HEIGHT - 220);

        // Estatísticas
        float statsY = Constants.VIRTUAL_HEIGHT - 290;
        assets.headerFont.setColor(new Color(1f, 0.88f, 0.35f, 1f));
        String scoreText = "PONTUACAO TOTAL: " + finalScore + " PTS   |   RECORDE: " + bestScore + " PTS";
        layout.setText(assets.headerFont, scoreText);
        assets.headerFont.draw(batch, scoreText, (Constants.VIRTUAL_WIDTH - layout.width) / 2f, statsY);

        assets.bodyFont.setColor(new Color(0.85f, 0.88f, 0.92f, 0.95f));
        String line1 = "Tempo de Sobrevivencia: " + (int) sm.getTimeSurvived() + "s";
        String line2 = "Pistas e Memorias Reveladas: " + sm.getCluesFound() + " | Itens Encontrados: " + sm.getItemsCollected();
        String line3 = "Estagios Concluidos: 3/3 (100%) | Ataques Sofridos: " + sm.getDamageTakenCount();
        String line4 = "Maldicao Superada: " + game.getCurseManager().getActiveCurse().getDisplayName();

        layout.setText(assets.bodyFont, line1);
        assets.bodyFont.draw(batch, line1, (Constants.VIRTUAL_WIDTH - layout.width) / 2f, statsY - 35);
        layout.setText(assets.bodyFont, line2);
        assets.bodyFont.draw(batch, line2, (Constants.VIRTUAL_WIDTH - layout.width) / 2f, statsY - 65);
        layout.setText(assets.bodyFont, line3);
        assets.bodyFont.draw(batch, line3, (Constants.VIRTUAL_WIDTH - layout.width) / 2f, statsY - 95);
        layout.setText(assets.bodyFont, line4);
        assets.bodyFont.draw(batch, line4, (Constants.VIRTUAL_WIDTH - layout.width) / 2f, statsY - 125);

        batch.draw(assets.vignette, 0, 0, Constants.VIRTUAL_WIDTH, Constants.VIRTUAL_HEIGHT);
        batch.end();

        boolean justPressed = Gdx.input.isButtonJustPressed(Input.Buttons.LEFT);
        btnPlayAgain.update(mousePos, game.getAudioManager());
        btnMainMenu.update(mousePos, game.getAudioManager());

        btnPlayAgain.render(batch, assets.menuBanner, shapeRenderer);
        btnMainMenu.render(batch, assets.menuBanner, shapeRenderer);

        if (btnPlayAgain.isClicked(mousePos, justPressed)) {
            game.getAudioManager().playSound("button_click");
            game.startNewGame();
        } else if (btnMainMenu.isClicked(mousePos, justPressed)) {
            game.getAudioManager().playSound("button_click");
            game.goToMainMenu();
        }
    }
}
