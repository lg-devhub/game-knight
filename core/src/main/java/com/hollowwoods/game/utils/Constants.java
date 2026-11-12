package com.hollowwoods.game.utils;

public final class Constants {
    private Constants() {}

    public static final String GAME_TITLE = "THE HOLLOW WOODS";
    public static final int VIRTUAL_WIDTH = 1280;
    public static final int VIRTUAL_HEIGHT = 720;

    // Dimensões do mapa e tiles
    public static final int TILE_SIZE = 64;
    public static final int MAP_WIDTH = 40;   // 40 tiles = 2560 pixels
    public static final int MAP_HEIGHT = 40;  // 40 tiles = 2560 pixels

    // Jogador (Elias)
    public static final float PLAYER_SPEED = 145f;
    public static final float PLAYER_SPRINT_SPEED = 220f;
    public static final float PLAYER_MAX_HEALTH = 100f;
    public static final float PLAYER_MAX_BATTERY = 100f;
    public static final float PLAYER_MAX_STAMINA = 100f;
    public static final float STAMINA_DRAIN = 28f;
    public static final float STAMINA_RECOVERY = 18f;

    // Lanterna e Campo de Visão (FOV)
    public static final float FLASHLIGHT_RANGE = 380f;
    public static final float FLASHLIGHT_FOV_DEGREES = 56f;
    public static final float FLASHLIGHT_MIN_RADIUS = 48f; // Raio ambiente imediato ao redor do personagem
    public static final float BATTERY_DRAIN_NORMAL = 2.2f;
    public static final float BATTERY_DRAIN_FRAGILE = 5.5f; // 2.5x com a maldição FRAGILE LIGHT

    // A Entidade (The Watcher)
    public static final float WATCHER_DETECTION_RANGE = 650f;
    public static final float WATCHER_HUNT_TRIGGER_DIST = 180f;
    public static final float WATCHER_ATTACK_DIST = 44f;
    public static final float WATCHER_ATTACK_DAMAGE = 35f;
    public static final float WATCHER_RETREAT_DURATION = 3.2f;

    // Velocidades por estágio
    public static final float WATCHER_SPEED_STAGE1 = 80f;
    public static final float WATCHER_SPEED_STAGE2 = 115f;
    public static final float WATCHER_SPEED_STAGE3 = 150f;
    public static final float WATCHER_HUNT_MULTIPLIER = 1.45f;
    public static final float WATCHER_CURSE_SPEED_MULTIPLIER = 1.50f; // Maldição THE WATCHER
}
