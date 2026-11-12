package com.hollowwoods.game.world;

import com.badlogic.gdx.math.Rectangle;

public class Obstacle {
    public enum Type {
        WALL,
        CABIN_WALL,
        CABIN_DOOR,
        STONE_WALL,
        FENCE,
        TREE,
        TOMBSTONE,
        MONOLITH,
        ALTAR,
        DECORATION
    }

    public final Rectangle bounds;
    public final Type type;
    public final boolean solid;
    public final boolean blocksLight;

    public Obstacle(float x, float y, float width, float height, Type type) {
        this(x, y, width, height, type, true, true);
    }

    public Obstacle(float x, float y, float width, float height, Type type, boolean solid, boolean blocksLight) {
        this.bounds = new Rectangle(x, y, width, height);
        this.type = type;
        this.solid = solid;
        this.blocksLight = blocksLight;
    }
}
