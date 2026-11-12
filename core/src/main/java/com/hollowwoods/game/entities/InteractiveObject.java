package com.hollowwoods.game.entities;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class InteractiveObject {
    public enum Type {
        KEY_CABIN,
        PHOTO_FRAG_1,
        PHOTO_FRAG_2,
        PHOTO_FRAG_3,
        SEAL_1,
        SEAL_2,
        SEAL_3,
        BATTERY,
        MEDKIT,
        RADIO,
        NOTE_1,
        NOTE_2,
        NOTE_3,
        CABIN_DOOR,
        CRYPT_GATE,
        ALTAR
    }

    public final Type type;
    public final Vector2 position;
    public final Rectangle interactionBounds;
    public boolean collected = false;
    public boolean unlocked = false;

    public final String promptText;
    public final String loreTitle;
    public final String loreContent;

    public InteractiveObject(Type type, float x, float y, String promptText, String loreTitle, String loreContent) {
        this.type = type;
        this.position = new Vector2(x, y);
        this.interactionBounds = new Rectangle(x - 28, y - 28, 56, 56);
        this.promptText = promptText;
        this.loreTitle = loreTitle;
        this.loreContent = loreContent;
    }
}
