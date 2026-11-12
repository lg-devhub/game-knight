package com.hollowwoods.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.hollowwoods.game.MainGame;
import com.hollowwoods.game.utils.Constants;

public abstract class BaseScreen implements Screen {

    protected final MainGame game;
    protected final OrthographicCamera camera;
    protected final Viewport viewport;
    protected final SpriteBatch batch;
    protected final ShapeRenderer shapeRenderer;

    protected final Vector2 mousePos = new Vector2();
    private final Vector3 unprojectTmp = new Vector3();

    public BaseScreen(MainGame game) {
        this.game = game;
        this.camera = new OrthographicCamera();
        this.viewport = new FitViewport(Constants.VIRTUAL_WIDTH, Constants.VIRTUAL_HEIGHT, camera);
        this.batch = game.getBatch();
        this.shapeRenderer = game.getShapeRenderer();
        this.camera.position.set(Constants.VIRTUAL_WIDTH / 2f, Constants.VIRTUAL_HEIGHT / 2f, 0f);
    }

    protected void updateMousePos() {
        unprojectTmp.set(Gdx.input.getX(), Gdx.input.getY(), 0f);
        viewport.unproject(unprojectTmp);
        mousePos.set(unprojectTmp.x, unprojectTmp.y);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void render(float delta) {
        updateMousePos();
        Gdx.gl.glClearColor(0.04f, 0.05f, 0.07f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
    }

    @Override public void show() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {}
}
