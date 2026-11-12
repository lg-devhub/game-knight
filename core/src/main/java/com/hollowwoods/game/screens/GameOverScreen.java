package com.hollowwoods.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.MathUtils;
import com.hollowwoods.game.MainGame;
import com.hollowwoods.game.systems.ScoreManager;
import com.hollowwoods.game.ui.MenuButton;
import com.hollowwoods.game.utils.Constants;
import com.hollowwoods.game.utils.GameAssets;

/**
 * Tela de Game Over com atmosfera de corrupção visual e pulsação carmesim.
 * "YOU HAVE BEEN FOUND" em vermelho, com estatísticas reais da partida.
 */
public class GameOverScreen extends BaseScreen {

    private final MenuButton btnRetry;
    private final MenuButton btnMainMenu;
    private final GlyphLayout layout = new GlyphLayout();
    private float pulseTimer = 0f;
    private float glitchTimer = 0f;

    public GameOverScreen(MainGame game) {
        super(game);

        GameAssets assets = game.getAssets();
        float btnW = 260;
        float btnH = 50;
        float btnX1 = (Constants.VIRTUAL_WIDTH / 2f) - btnW - 20;
        float btnX2 = (Constants.VIRTUAL_WIDTH / 2f) + 20;
        float btnY = 90;

        btnRetry = new MenuButton("RETRY", btnX1, btnY, btnW, btnH, assets.menuFont);
        btnMainMenu = new MenuButton("MAIN MENU", btnX2, btnY, btnW, btnH, assets.menuFont);
    }

    @Override
    public void show() {
        game.getAudioManager().playGameOverAudio();
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        pulseTimer += delta * 2.5f;
        glitchTimer += delta;

        GameAssets assets = game.getAssets();
        ScoreManager sm = game.getScoreManager();
        int totalScore = sm.calculateTotalScore();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        // Fundo escuro com pulsação carmesim sutil
        float redPulse = 0.10f + 0.06f * (float) Math.sin(pulseTimer);
        batch.setColor(redPulse, 0.02f, 0.03f, 1f);
        batch.draw(assets.titleBg, 0, 0, Constants.VIRTUAL_WIDTH, Constants.VIRTUAL_HEIGHT);
        batch.setColor(Color.WHITE);

        // Efeito de linhas de distorção horizontais (glitch)
        if (assets.fogTexture != null) {
            float glitchAlpha = 0.04f + 0.03f * (float) Math.sin(glitchTimer * 18f);
            batch.setColor(0.95f, 0.15f, 0.15f, glitchAlpha);
            int offset = (int) (glitchTimer * 200f) % 512;
            batch.draw(assets.fogTexture, 0, 0, Constants.VIRTUAL_WIDTH, Constants.VIRTUAL_HEIGHT,
                    offset, 0, 512, 512, false, false);
            batch.setColor(Color.WHITE);
        }

        // Título: "YOU HAVE BEEN FOUND"
        float titleShake = MathUtils.random(-1.5f, 1.5f) * (0.3f + 0.7f * (float) Math.abs(Math.sin(pulseTimer * 1.5f)));
        assets.titleFont.setColor(new Color(0.98f, 0.12f, 0.12f, 1f));
        String mainTitle = "YOU HAVE BEEN FOUND";
        layout.setText(assets.titleFont, mainTitle);
        float titleX = (Constants.VIRTUAL_WIDTH - layout.width) / 2f + titleShake;
        assets.titleFont.draw(batch, mainTitle, titleX, Constants.VIRTUAL_HEIGHT - 120);

        // Subtítulo narrativo
        assets.bodyFont.setColor(new Color(0.62f, 0.64f, 0.68f, 0.85f));
        String sub = "O feixe da lanterna se apagou. A escuridao o consumiu.";
        layout.setText(assets.bodyFont, sub);
        assets.bodyFont.draw(batch, sub, (Constants.VIRTUAL_WIDTH - layout.width) / 2f, Constants.VIRTUAL_HEIGHT - 178);

        // Linha separadora
        assets.smallFont.setColor(new Color(0.45f, 0.12f, 0.14f, 0.7f));
        String divider = "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━";
        layout.setText(assets.smallFont, divider);
        assets.smallFont.draw(batch, divider, (Constants.VIRTUAL_WIDTH - layout.width) / 2f, Constants.VIRTUAL_HEIGHT - 210);

        // Pontuação Final em destaque dourado
        float statsY = Constants.VIRTUAL_HEIGHT - 252;
        assets.headerFont.setColor(new Color(1f, 0.88f, 0.42f, 1f));
        String scoreText = "PONTUACAO FINAL: " + totalScore + " PTS";
        layout.setText(assets.headerFont, scoreText);
        assets.headerFont.draw(batch, scoreText, (Constants.VIRTUAL_WIDTH - layout.width) / 2f, statsY);

        // Estatísticas reais da partida
        assets.bodyFont.setColor(new Color(0.82f, 0.84f, 0.88f, 0.92f));
        String timeText = "Tempo Sobrevivido: " + (int) sm.getTimeSurvived() + " segundos";
        String cluesText = "Pistas: " + sm.getCluesFound() + "  |  Itens: " + sm.getItemsCollected();
        String stagesText = "Estagios: " + sm.getStagesCompleted() + "/3  |  Ataques: " + sm.getDamageTakenCount();
        String curseText = "Maldicao Ativa: " + game.getCurseManager().getActiveCurse().getDisplayName();

        layout.setText(assets.bodyFont, timeText);
        assets.bodyFont.draw(batch, timeText, (Constants.VIRTUAL_WIDTH - layout.width) / 2f, statsY - 38);

        layout.setText(assets.bodyFont, cluesText);
        assets.bodyFont.draw(batch, cluesText, (Constants.VIRTUAL_WIDTH - layout.width) / 2f, statsY - 68);

        layout.setText(assets.bodyFont, stagesText);
        assets.bodyFont.draw(batch, stagesText, (Constants.VIRTUAL_WIDTH - layout.width) / 2f, statsY - 98);

        assets.smallFont.setColor(new Color(0.92f, 0.30f, 0.30f, 0.9f));
        layout.setText(assets.smallFont, curseText);
        assets.smallFont.draw(batch, curseText, (Constants.VIRTUAL_WIDTH - layout.width) / 2f, statsY - 128);

        // Vinheta cinematográfica
        batch.draw(assets.vignette, 0, 0, Constants.VIRTUAL_WIDTH, Constants.VIRTUAL_HEIGHT);
        batch.end();

        // Botões
        boolean justPressed = Gdx.input.isButtonJustPressed(Input.Buttons.LEFT);
        btnRetry.update(mousePos, game.getAudioManager());
        btnMainMenu.update(mousePos, game.getAudioManager());

        btnRetry.render(batch, assets.menuBanner, shapeRenderer);
        btnMainMenu.render(batch, assets.menuBanner, shapeRenderer);

        if (btnRetry.isClicked(mousePos, justPressed)) {
            game.getAudioManager().playSound("button_click");
            game.startNewGame();
        } else if (btnMainMenu.isClicked(mousePos, justPressed)) {
            game.getAudioManager().playSound("button_click");
            game.goToMainMenu();
        }
    }
}
