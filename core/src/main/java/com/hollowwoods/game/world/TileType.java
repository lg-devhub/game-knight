package com.hollowwoods.game.world;

public enum TileType {
    // Stage 1 - The Awakening
    FOREST_DIRT(true),
    FOREST_PATH(true),
    FOREST_MOSS(true),
    FOREST_PUDDLE(true),

    // Stage 2 - The Forgotten
    VILLAGE_COBBLE(true),
    VILLAGE_FLAGSTONE(true),
    VILLAGE_OVERGROWN(true),
    VILLAGE_ASH(true),

    // Stage 3 - The Hollow
    CORRUPTED_STONE(true),
    BLOOD_STONE(true),
    ELDRITCH_EARTH(true),
    OBSIDIAN(true),

    // Estruturas e interiores
    WOOD_FLOOR(true),
    WORN_WOOD(true),
    CABIN_RUG(true),
    TRAPDOOR(true),

    WALL_IMPASSABLE(false);

    private final boolean walkable;

    TileType(boolean walkable) {
        this.walkable = walkable;
    }

    public boolean isWalkable() {
        return walkable;
    }
}
