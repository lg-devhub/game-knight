package com.hollowwoods.game.systems;

public class InventoryManager {
    private boolean hasCabinKey = false;
    private int photoFragments = 0;
    private int ritualSeals = 0;
    private int batteryPacks = 0;
    private int medkits = 0;
    private int notesCount = 0;

    public void reset() {
        hasCabinKey = false;
        photoFragments = 0;
        ritualSeals = 0;
        batteryPacks = 0;
        medkits = 0;
        notesCount = 0;
    }

    public boolean hasCabinKey() {
        return hasCabinKey;
    }

    public void setHasCabinKey(boolean hasCabinKey) {
        this.hasCabinKey = hasCabinKey;
    }

    public int getPhotoFragments() {
        return photoFragments;
    }

    public void addPhotoFragment() {
        this.photoFragments++;
    }

    public int getRitualSeals() {
        return ritualSeals;
    }

    public void addRitualSeal() {
        this.ritualSeals++;
    }

    public int getBatteryPacks() {
        return batteryPacks;
    }

    public void addBatteryPack() {
        this.batteryPacks++;
    }

    public boolean useBatteryPack() {
        if (batteryPacks > 0) {
            batteryPacks--;
            return true;
        }
        return false;
    }

    public int getMedkits() {
        return medkits;
    }

    public void addMedkit() {
        this.medkits++;
    }

    public boolean useMedkit() {
        if (medkits > 0) {
            medkits--;
            return true;
        }
        return false;
    }

    public int getNotesCount() {
        return notesCount;
    }

    public void addNote() {
        this.notesCount++;
    }
}
