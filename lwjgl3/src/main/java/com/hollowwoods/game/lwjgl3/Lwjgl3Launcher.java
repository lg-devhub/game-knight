package com.hollowwoods.game.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.hollowwoods.game.MainGame;

public class Lwjgl3Launcher {
    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setTitle("THE HOLLOW WOODS");
        configuration.useVsync(true);
        configuration.setForegroundFPS(60);
        configuration.setWindowedMode(1280, 720);
        configuration.setResizable(true);
        new Lwjgl3Application(new MainGame(), configuration);
    }
}
