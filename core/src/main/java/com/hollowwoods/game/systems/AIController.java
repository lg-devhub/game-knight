package com.hollowwoods.game.systems;

import com.hollowwoods.game.entities.Player;
import com.hollowwoods.game.entities.Watcher;
import com.hollowwoods.game.world.CollisionManager;
import com.hollowwoods.game.world.Obstacle;

import java.util.List;

public class AIController {

    private final CollisionManager collisionManager;

    public AIController(CollisionManager collisionManager) {
        this.collisionManager = collisionManager;
    }

    public void update(float delta, Watcher watcher, Player player, List<Obstacle> obstacles,
                       CurseManager curseManager, int stage, float mapW, float mapH) {
        boolean blackout = (curseManager != null) && curseManager.isFlashlightDisabled();
        float speedMult = (curseManager != null) ? curseManager.getWatcherSpeedMultiplier() : 1.0f;

        watcher.updateAI(delta, player, obstacles, collisionManager, blackout, speedMult, stage, mapW, mapH);
    }
}
