package com.hollowwoods.game.entities;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public abstract class Entity {
    public final Vector2 position = new Vector2();
    public final Vector2 velocity = new Vector2();
    public final Rectangle bounds = new Rectangle();

    // 0=Down, 1=Left, 2=Right, 3=Up
    public int direction = 0;
    public float stateTime = 0f;
    public boolean isMoving = false;
    public float speed = 100f;

    public Entity(float x, float y, float width, float height) {
        this.position.set(x, y);
        this.bounds.set(x - width / 2f, y - height / 2f, width, height);
    }

    public abstract void update(float delta);

    public void updateBounds() {
        bounds.setPosition(position.x - bounds.width / 2f, position.y - bounds.height / 2f);
    }
}
