package com.hollowwoods.game.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class SaveManager {
    private static final String PREFS_NAME = "the_hollow_woods_save";

    private static final String KEY_HIGH_SCORE = "high_score";
    private static final String KEY_HAS_CONTINUE = "has_continue";
    private static final String KEY_SAVED_STAGE = "saved_stage";
    private static final String KEY_SAVED_SCORE = "saved_score";
    private static final String KEY_SAVED_CURSE = "saved_curse";
    private static final String KEY_SAVED_HEALTH = "saved_health";
    private static final String KEY_SAVED_BATTERY = "saved_battery";

    private static final String KEY_VOL_MASTER = "vol_master";
    private static final String KEY_VOL_MUSIC = "vol_music";
    private static final String KEY_VOL_AMBIENT = "vol_ambient";
    private static final String KEY_VOL_SFX = "vol_sfx";

    private final Preferences prefs;

    public SaveManager() {
        prefs = Gdx.app.getPreferences(PREFS_NAME);
    }

    public int getHighScore() {
        return prefs.getInteger(KEY_HIGH_SCORE, 0);
    }

    public void submitScore(int score) {
        int current = getHighScore();
        if (score > current) {
            prefs.putInteger(KEY_HIGH_SCORE, score);
            prefs.flush();
        }
    }

    public boolean hasContinue() {
        return prefs.getBoolean(KEY_HAS_CONTINUE, false);
    }

    public void saveCheckpoint(int stage, int score, String curseName, float health, float battery) {
        prefs.putBoolean(KEY_HAS_CONTINUE, true);
        prefs.putInteger(KEY_SAVED_STAGE, stage);
        prefs.putInteger(KEY_SAVED_SCORE, score);
        prefs.putString(KEY_SAVED_CURSE, curseName);
        prefs.putFloat(KEY_SAVED_HEALTH, health);
        prefs.putFloat(KEY_SAVED_BATTERY, battery);
        prefs.flush();
    }

    public void clearContinue() {
        prefs.putBoolean(KEY_HAS_CONTINUE, false);
        prefs.flush();
    }

    public int getSavedStage() {
        return prefs.getInteger(KEY_SAVED_STAGE, 1);
    }

    public int getSavedScore() {
        return prefs.getInteger(KEY_SAVED_SCORE, 0);
    }

    public String getSavedCurse() {
        return prefs.getString(KEY_SAVED_CURSE, "BLACKOUT");
    }

    public float getSavedHealth() {
        return prefs.getFloat(KEY_SAVED_HEALTH, 100f);
    }

    public float getSavedBattery() {
        return prefs.getFloat(KEY_SAVED_BATTERY, 100f);
    }

    // Configurações de áudio com volumes padrão seguros exigidos por AGENTS.md § 7
    public float getMasterVolume() {
        return prefs.getFloat(KEY_VOL_MASTER, 0.40f);
    }

    public float getMusicVolume() {
        return prefs.getFloat(KEY_VOL_MUSIC, 0.18f);
    }

    public float getAmbientVolume() {
        return prefs.getFloat(KEY_VOL_AMBIENT, 0.14f);
    }

    public float getSfxVolume() {
        return prefs.getFloat(KEY_VOL_SFX, 0.25f);
    }

    public void saveAudioSettings(float master, float music, float ambient, float sfx) {
        prefs.putFloat(KEY_VOL_MASTER, master);
        prefs.putFloat(KEY_VOL_MUSIC, music);
        prefs.putFloat(KEY_VOL_AMBIENT, ambient);
        prefs.putFloat(KEY_VOL_SFX, sfx);
        prefs.flush();
    }
}
