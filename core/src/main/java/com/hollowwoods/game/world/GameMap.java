package com.hollowwoods.game.world;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.hollowwoods.game.entities.InteractiveObject;
import com.hollowwoods.game.utils.Constants;
import com.hollowwoods.game.utils.GameAssets;

public class GameMap {
    public final StageData stageData;
    private final boolean[][] explored = new boolean[Constants.MAP_WIDTH][Constants.MAP_HEIGHT];

    public GameMap(StageData stageData) {
        this.stageData = stageData;
    }

    public void updateExploration(Vector2 playerPos, float radius) {
        int centerX = (int) (playerPos.x / Constants.TILE_SIZE);
        int centerY = (int) (playerPos.y / Constants.TILE_SIZE);
        int rTiles = (int) (radius / Constants.TILE_SIZE) + 1;

        int minX = Math.max(0, centerX - rTiles);
        int maxX = Math.min(Constants.MAP_WIDTH - 1, centerX + rTiles);
        int minY = Math.max(0, centerY - rTiles);
        int maxY = Math.min(Constants.MAP_HEIGHT - 1, centerY + rTiles);

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                explored[x][y] = true;
            }
        }
    }

    public boolean isExplored(int x, int y) {
        if (x < 0 || y < 0 || x >= Constants.MAP_WIDTH || y >= Constants.MAP_HEIGHT) return false;
        return explored[x][y];
    }

    public void renderTiles(SpriteBatch batch, GameAssets assets, OrthographicCamera camera) {
        float halfW = (camera.viewportWidth * camera.zoom) / 2f;
        float halfH = (camera.viewportHeight * camera.zoom) / 2f;

        int startX = Math.max(0, (int) ((camera.position.x - halfW) / Constants.TILE_SIZE) - 1);
        int endX = Math.min(Constants.MAP_WIDTH - 1, (int) ((camera.position.x + halfW) / Constants.TILE_SIZE) + 1);
        int startY = Math.max(0, (int) ((camera.position.y - halfH) / Constants.TILE_SIZE) - 1);
        int endY = Math.min(Constants.MAP_HEIGHT - 1, (int) ((camera.position.y + halfH) / Constants.TILE_SIZE) + 1);

        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                TileType type = stageData.tileGrid[x][y];
                TextureRegion region = assets.getTileRegion(type);
                if (region != null) {
                    batch.draw(region, x * Constants.TILE_SIZE, y * Constants.TILE_SIZE, Constants.TILE_SIZE, Constants.TILE_SIZE);
                }
            }
        }
    }

    public void renderObstacles(SpriteBatch batch, GameAssets assets) {
        // Ordenação Y decrescente para profundidade visual correta
        stageData.obstacles.sort((o1, o2) -> Float.compare(o2.bounds.y, o1.bounds.y));

        for (Obstacle o : stageData.obstacles) {
            TextureRegion reg = assets.getObstacleRegion(o.type);
            if (reg != null) {
                batch.draw(reg, o.bounds.x, o.bounds.y, o.bounds.width, o.bounds.height);
            }
        }
    }

    public void renderInteractiveObjects(SpriteBatch batch, GameAssets assets, float stateTime) {
        for (InteractiveObject obj : stageData.interactiveObjects) {
            if (obj.collected) continue;

            TextureRegion reg = assets.getItemRegion(obj.type);
            if (reg != null) {
                float bounce = (float) Math.sin(stateTime * 4.0f) * 3f;
                batch.draw(reg, obj.position.x - 16, obj.position.y - 16 + bounce, 32, 32);
            }
        }
    }

    public InteractiveObject getNearbyInteractiveObject(Vector2 pos, float reachDistance) {
        for (InteractiveObject obj : stageData.interactiveObjects) {
            if (obj.collected) continue;
            if (obj.position.dst(pos) <= reachDistance) {
                return obj;
            }
        }
        return null;
    }
}
