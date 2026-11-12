package com.hollowwoods.game.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.hollowwoods.game.systems.AudioManager;

public class MenuButton {
    public final Rectangle bounds;
    public final String text;
    private final BitmapFont font;
    private final GlyphLayout layout;

    private boolean isHovered = false;
    private boolean wasHovered = false;
    public boolean enabled = true;

    private final Color normalColor = new Color(0.85f, 0.88f, 0.90f, 1f);
    private final Color hoverColor = new Color(0.95f, 0.28f, 0.28f, 1f);
    private final Color disabledColor = new Color(0.38f, 0.40f, 0.44f, 0.6f);

    public MenuButton(String text, float x, float y, float width, float height, BitmapFont font) {
        this.text = text;
        this.bounds = new Rectangle(x, y, width, height);
        this.font = font;
        this.layout = new GlyphLayout(font, text);
    }

    public void update(Vector2 mousePos, AudioManager audioManager) {
        if (!enabled) {
            isHovered = false;
            return;
        }

        wasHovered = isHovered;
        isHovered = bounds.contains(mousePos.x, mousePos.y);

        if (isHovered && !wasHovered && audioManager != null) {
            audioManager.playSound("button_click", 1.25f);
        }
    }

    public boolean isClicked(Vector2 mousePos, boolean justPressed) {
        return enabled && justPressed && bounds.contains(mousePos.x, mousePos.y);
    }

    public void render(SpriteBatch batch, ShapeRenderer shapeRenderer) {
        render(batch, null, shapeRenderer);
    }

    public void render(SpriteBatch batch, com.badlogic.gdx.graphics.Texture bannerTexture, ShapeRenderer shapeRenderer) {
        if (bannerTexture != null && isHovered && enabled) {
            batch.begin();
            batch.setColor(Color.WHITE);
            batch.draw(bannerTexture, bounds.x - 10, bounds.y - 4, bounds.width + 20, bounds.height + 8);
            font.setColor(Color.WHITE);
            float textX = bounds.x + (bounds.width - layout.width) / 2f;
            float textY = bounds.y + (bounds.height + layout.height) / 2f;
            font.draw(batch, text, textX, textY);
            batch.end();
            return;
        }

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        if (!enabled) {
            shapeRenderer.setColor(new Color(0.06f, 0.08f, 0.10f, 0.5f));
        } else if (isHovered) {
            shapeRenderer.setColor(new Color(0.35f, 0.08f, 0.10f, 0.85f));
        } else {
            shapeRenderer.setColor(new Color(0.04f, 0.05f, 0.07f, 0.65f));
        }
        shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        if (!enabled) {
            shapeRenderer.setColor(new Color(0.18f, 0.20f, 0.22f, 0.4f));
        } else if (isHovered) {
            shapeRenderer.setColor(new Color(0.92f, 0.25f, 0.25f, 1f));
        } else {
            shapeRenderer.setColor(new Color(0.28f, 0.30f, 0.35f, 0.6f));
        }
        shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
        shapeRenderer.end();

        batch.begin();
        if (!enabled) {
            font.setColor(disabledColor);
        } else if (isHovered) {
            font.setColor(hoverColor);
        } else {
            font.setColor(normalColor);
        }

        float textX = bounds.x + (bounds.width - layout.width) / 2f;
        float textY = bounds.y + (bounds.height + layout.height) / 2f;
        font.draw(batch, text, textX, textY);
        batch.end();
    }
}
