package com.hollowwoods.game.systems;

public class ScoreManager {
    private float timeSurvived = 0f;
    private int cluesFound = 0;
    private int itemsCollected = 0;
    private int stagesCompleted = 0;
    private int damageTakenCount = 0;
    private float finalBatteryPercent = 100f;

    public void update(float delta) {
        timeSurvived += delta;
    }

    public void addClue() {
        cluesFound++;
    }

    public void addItem() {
        itemsCollected++;
    }

    public void completeStage() {
        stagesCompleted++;
    }

    public void recordDamageTaken() {
        damageTakenCount++;
    }

    public void setFinalBatteryPercent(float batteryPercent) {
        this.finalBatteryPercent = Math.max(0f, Math.min(100f, batteryPercent));
    }

    public int calculateTotalScore() {
        int timeScore = (int) (timeSurvived * 12);
        int clueScore = cluesFound * 350;
        int itemScore = itemsCollected * 150;
        int stageScore = stagesCompleted * 1500;
        int batteryScore = (int) (finalBatteryPercent * 5);
        int penalty = damageTakenCount * 250;

        int total = timeScore + clueScore + itemScore + stageScore + batteryScore - penalty;
        return Math.max(100, total);
    }

    public float getTimeSurvived() {
        return timeSurvived;
    }

    public int getCluesFound() {
        return cluesFound;
    }

    public int getItemsCollected() {
        return itemsCollected;
    }

    public int getStagesCompleted() {
        return stagesCompleted;
    }

    public int getDamageTakenCount() {
        return damageTakenCount;
    }

    public void reset() {
        timeSurvived = 0f;
        cluesFound = 0;
        itemsCollected = 0;
        stagesCompleted = 0;
        damageTakenCount = 0;
        finalBatteryPercent = 100f;
    }
}
