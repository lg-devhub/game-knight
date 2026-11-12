package com.hollowwoods.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.hollowwoods.game.screens.*;
import com.hollowwoods.game.systems.*;
import com.hollowwoods.game.utils.GameAssets;

public class MainGame extends Game {

    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;

    private GameAssets assets;
    private AudioManager audioManager;
    private SaveManager saveManager;
    private CurseManager curseManager;
    private ScoreManager scoreManager;
    private InventoryManager inventoryManager;

    @Override
    public void create() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        assets = new GameAssets();
        assets.load();

        audioManager = new AudioManager(assets);
        saveManager = new SaveManager();
        curseManager = new CurseManager();
        scoreManager = new ScoreManager();
        inventoryManager = new InventoryManager();

        // Inicializa configurações de volume salvas com defaults seguros
        audioManager.setMasterVolume(saveManager.getMasterVolume());
        audioManager.setMusicVolume(saveManager.getMusicVolume());
        audioManager.setAmbientVolume(saveManager.getAmbientVolume());
        audioManager.setSfxVolume(saveManager.getSfxVolume());

        setScreen(new MainMenuScreen(this));
    }

    public void goToMainMenu() {
        audioManager.stopAmbientLoops();
        setScreen(new MainMenuScreen(this));
    }

    public void startNewGame() {
        scoreManager.reset();
        inventoryManager.reset();
        curseManager.rollRandomCurse();
        setScreen(new StoryScreen(this));
    }

    public void continueSavedGame() {
        if (!saveManager.hasContinue()) {
            startNewGame();
            return;
        }
        int stage = saveManager.getSavedStage();
        String curseName = saveManager.getSavedCurse();
        curseManager.setCurseByName(curseName);
        inventoryManager.reset();
        setScreen(new GameScreen(this, stage, saveManager.getSavedHealth(), saveManager.getSavedBattery()));
    }

    public void proceedFromStory() {
        setScreen(new CurseScreen(this));
    }

    public void proceedFromCurse() {
        setScreen(new GameScreen(this, 1, 100f, 100f));
    }

    public void startStage(int stageNumber, float currentHealth, float currentBattery) {
        setScreen(new GameScreen(this, stageNumber, currentHealth, currentBattery));
    }

    public void pauseGame(GameScreen activeGameScreen) {
        setScreen(new PauseScreen(this, activeGameScreen));
    }

    public void gameOver() {
        setScreen(new GameOverScreen(this));
    }

    public void victory() {
        scoreManager.completeStage();
        int totalScore = scoreManager.calculateTotalScore();
        saveManager.submitScore(totalScore);
        saveManager.clearContinue();
        setScreen(new VictoryScreen(this, totalScore));
    }

    public SpriteBatch getBatch() { return batch; }
    public ShapeRenderer getShapeRenderer() { return shapeRenderer; }
    public GameAssets getAssets() { return assets; }
    public AudioManager getAudioManager() { return audioManager; }
    public SaveManager getSaveManager() { return saveManager; }
    public CurseManager getCurseManager() { return curseManager; }
    public ScoreManager getScoreManager() { return scoreManager; }
    public InventoryManager getInventoryManager() { return inventoryManager; }

    @Override
    public void dispose() {
        super.dispose();
        if (batch != null) batch.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();
        if (assets != null) assets.dispose();
    }
}
