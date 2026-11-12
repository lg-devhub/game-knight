package com.hollowwoods.game.systems;

import com.badlogic.gdx.math.MathUtils;

public class CurseManager {

    public enum CurseType {
        BLACKOUT("BLACKOUT", "A lanterna apresenta falhas eletricas e apagoes periodicos."),
        FRAGILE_LIGHT("LUZ FRAGIL", "A bateria da lanterna esgota-se 2.5x mais rapido."),
        LOST_PATH("CAMINHO PERDIDO", "O mapa esta indisponivel. Voce esta desorientado na floresta."),
        WHISPERING("SUSSURROS", "Sons sutis de passos e galhos quebrando enganam sua percepcao."),
        THE_WATCHER("O OBSERVADOR", "A criatura move-se com velocidade 50% maior quando nao observada.");

        private final String displayName;
        private final String description;

        CurseType(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getDescription() {
            return description;
        }
    }

    private CurseType activeCurse;

    // Estado do Blackout
    private float blackoutTimer = 0f;
    private float nextBlackoutInterval = 14f;
    private float blackoutDuration = 2.0f;
    private boolean isBlackoutActive = false;

    // Estado do Whispering
    private float whisperTimer = 0f;
    public boolean triggerPhantomSound = false;

    public CurseManager() {
        rollRandomCurse();
    }

    public CurseType rollRandomCurse() {
        CurseType[] values = CurseType.values();
        int idx = MathUtils.random(values.length - 1);
        activeCurse = values[idx];
        resetState();
        return activeCurse;
    }

    public void setCurse(CurseType curse) {
        this.activeCurse = curse;
        resetState();
    }

    public void setCurseByName(String name) {
        for (CurseType c : CurseType.values()) {
            if (c.name().equalsIgnoreCase(name) || c.getDisplayName().equalsIgnoreCase(name)) {
                setCurse(c);
                return;
            }
        }
        setCurse(CurseType.BLACKOUT);
    }

    public CurseType getActiveCurse() {
        return activeCurse;
    }

    private void resetState() {
        blackoutTimer = 0f;
        nextBlackoutInterval = MathUtils.random(12f, 18f);
        isBlackoutActive = false;
        whisperTimer = 0f;
        triggerPhantomSound = false;
    }

    public void update(float delta) {
        triggerPhantomSound = false;

        if (activeCurse == CurseType.BLACKOUT) {
            blackoutTimer += delta;
            if (!isBlackoutActive) {
                if (blackoutTimer >= nextBlackoutInterval) {
                    isBlackoutActive = true;
                    blackoutTimer = 0f;
                    blackoutDuration = MathUtils.random(1.6f, 2.6f);
                }
            } else {
                if (blackoutTimer >= blackoutDuration) {
                    isBlackoutActive = false;
                    blackoutTimer = 0f;
                    nextBlackoutInterval = MathUtils.random(12f, 18f);
                }
            }
        } else if (activeCurse == CurseType.WHISPERING) {
            whisperTimer += delta;
            if (whisperTimer >= 9f) {
                whisperTimer = 0f;
                if (MathUtils.randomBoolean(0.6f)) {
                    triggerPhantomSound = true;
                }
            }
        }
    }

    public boolean isFlashlightDisabled() {
        return activeCurse == CurseType.BLACKOUT && isBlackoutActive;
    }

    public boolean isMinimapDisabled() {
        return activeCurse == CurseType.LOST_PATH;
    }

    public float getBatteryDrainMultiplier() {
        return (activeCurse == CurseType.FRAGILE_LIGHT) ? 2.5f : 1.0f;
    }

    public float getWatcherSpeedMultiplier() {
        return (activeCurse == CurseType.THE_WATCHER) ? 1.50f : 1.0f;
    }
}
