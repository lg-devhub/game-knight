package com.hollowwoods.game.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.utils.Disposable;
import com.hollowwoods.game.entities.InteractiveObject;
import com.hollowwoods.game.world.Obstacle;
import com.hollowwoods.game.world.TileType;

import java.util.HashMap;
import java.util.Map;

public class GameAssets implements Disposable {

    public Texture titleBg;
    public Texture titleLogo;
    public Texture menuBanner;
    public Texture playerSheet;
    public Texture watcherSheet;
    public Texture tilesSheet;
    public Texture propsSheet;
    public Texture treesSheet;
    public Texture itemsSheet;
    public Texture uiSheet;
    public Texture lightCone;
    public Texture lightHalo;
    public Texture fogTexture;
    public Texture vignette;

    // Animações do Jogador [direção: 0=Baixo, 1=Esquerda, 2=Direita, 3=Cima]
    public Animation<TextureRegion>[] playerWalk;
    public TextureRegion[] playerIdle;

    // Animações do Watcher [direção: 0=Baixo, 1=Esquerda, 2=Direita, 3=Cima]
    public Animation<TextureRegion>[] watcherWalk;
    public TextureRegion[] watcherIdle;
    public TextureRegion[] watcherAttack;

    // Regiões de Tiles
    private final Map<TileType, TextureRegion> tileRegions = new HashMap<>();
    private final Map<Obstacle.Type, TextureRegion> obstacleRegions = new HashMap<>();
    private final Map<InteractiveObject.Type, TextureRegion> itemRegions = new HashMap<>();

    // UI
    public TextureRegion uiHealthFrame;
    public TextureRegion uiHealthFill;
    public TextureRegion uiHealthCross;
    public TextureRegion uiBatteryFrame;
    public TextureRegion uiBatteryFill;
    public TextureRegion uiBatteryBolt;
    public TextureRegion uiObjectiveDiamond;
    public TextureRegion uiCurseSkull;
    public TextureRegion uiMinimapFrame;
    public TextureRegion uiPromptBox;

    // Tipografia
    public BitmapFont titleFont;
    public BitmapFont menuFont;
    public BitmapFont headerFont;
    public BitmapFont bodyFont;
    public BitmapFont smallFont;

    // Áudios
    private final Map<String, Sound> sounds = new HashMap<>();

    @SuppressWarnings("unchecked")
    public void load() {
        titleBg = new Texture(Gdx.files.internal("images/title_bg.png"));
        titleLogo = new Texture(Gdx.files.internal("images/title_logo.png"));
        if (Gdx.files.internal("images/menu_banner.png").exists()) {
            menuBanner = new Texture(Gdx.files.internal("images/menu_banner.png"));
        }
        playerSheet = new Texture(Gdx.files.internal("images/player.png"));
        watcherSheet = new Texture(Gdx.files.internal("images/watcher.png"));
        tilesSheet = new Texture(Gdx.files.internal("images/tiles.png"));
        propsSheet = new Texture(Gdx.files.internal("images/props.png"));
        treesSheet = new Texture(Gdx.files.internal("images/trees.png"));
        itemsSheet = new Texture(Gdx.files.internal("images/items.png"));
        uiSheet = new Texture(Gdx.files.internal("images/ui.png"));
        lightCone = new Texture(Gdx.files.internal("images/light_cone.png"));
        if (Gdx.files.internal("images/light_halo.png").exists()) {
            lightHalo = new Texture(Gdx.files.internal("images/light_halo.png"));
        }
        fogTexture = new Texture(Gdx.files.internal("images/fog.png"));
        fogTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        vignette = new Texture(Gdx.files.internal("images/vignette.png"));

        // Player animations (64x64)
        playerWalk = new Animation[4];
        playerIdle = new TextureRegion[4];
        TextureRegion[][] pFrames = TextureRegion.split(playerSheet, 64, 64);
        for (int r = 0; r < 4; r++) {
            playerIdle[r] = pFrames[r][0];
            playerWalk[r] = new Animation<>(0.18f, pFrames[r][0], pFrames[r][1], pFrames[r][0], pFrames[r][2]);
            playerWalk[r].setPlayMode(Animation.PlayMode.LOOP);
        }

        // Watcher animations (64x64)
        watcherWalk = new Animation[4];
        watcherIdle = new TextureRegion[4];
        watcherAttack = new TextureRegion[4];
        TextureRegion[][] wFrames = TextureRegion.split(watcherSheet, 64, 64);
        for (int r = 0; r < 4; r++) {
            watcherIdle[r] = wFrames[r][0];
            watcherWalk[r] = new Animation<>(0.20f, wFrames[r][0], wFrames[r][1]);
            watcherWalk[r].setPlayMode(Animation.PlayMode.LOOP);
            watcherAttack[r] = wFrames[r][2];
        }

        // Tiles
        TextureRegion[][] tFrames = TextureRegion.split(tilesSheet, 64, 64);
        tileRegions.put(TileType.FOREST_DIRT, tFrames[0][0]);
        tileRegions.put(TileType.FOREST_PATH, tFrames[0][1]);
        tileRegions.put(TileType.FOREST_MOSS, tFrames[0][2]);
        tileRegions.put(TileType.FOREST_PUDDLE, tFrames[0][3]);

        tileRegions.put(TileType.VILLAGE_COBBLE, tFrames[1][0]);
        tileRegions.put(TileType.VILLAGE_FLAGSTONE, tFrames[1][1]);
        tileRegions.put(TileType.VILLAGE_OVERGROWN, tFrames[1][2]);
        tileRegions.put(TileType.VILLAGE_ASH, tFrames[1][3]);

        tileRegions.put(TileType.CORRUPTED_STONE, tFrames[2][0]);
        tileRegions.put(TileType.BLOOD_STONE, tFrames[2][1]);
        tileRegions.put(TileType.ELDRITCH_EARTH, tFrames[2][2]);
        tileRegions.put(TileType.OBSIDIAN, tFrames[2][3]);

        tileRegions.put(TileType.WOOD_FLOOR, tFrames[3][0]);
        tileRegions.put(TileType.WORN_WOOD, tFrames[3][1]);
        tileRegions.put(TileType.CABIN_RUG, tFrames[3][2]);
        tileRegions.put(TileType.TRAPDOOR, tFrames[3][3]);
        tileRegions.put(TileType.WALL_IMPASSABLE, tFrames[1][0]);

        // Props
        TextureRegion[][] prFrames = TextureRegion.split(propsSheet, 64, 64);
        obstacleRegions.put(Obstacle.Type.CABIN_WALL, prFrames[0][0]);
        obstacleRegions.put(Obstacle.Type.CABIN_DOOR, prFrames[0][2]);
        obstacleRegions.put(Obstacle.Type.STONE_WALL, prFrames[0][3]);
        obstacleRegions.put(Obstacle.Type.FENCE, prFrames[1][0]);
        obstacleRegions.put(Obstacle.Type.TOMBSTONE, prFrames[1][0]);
        obstacleRegions.put(Obstacle.Type.WALL, prFrames[0][0]);
        obstacleRegions.put(Obstacle.Type.DECORATION, prFrames[1][0]);
        obstacleRegions.put(Obstacle.Type.MONOLITH, new TextureRegion(propsSheet, 0, 128, 64, 128));
        obstacleRegions.put(Obstacle.Type.ALTAR, new TextureRegion(propsSheet, 64, 128, 128, 64));
        obstacleRegions.put(Obstacle.Type.TREE, new TextureRegion(treesSheet, 0, 0, 128, 128));

        // Itens
        TextureRegion[][] itFrames = TextureRegion.split(itemsSheet, 32, 32);
        itemRegions.put(InteractiveObject.Type.KEY_CABIN, itFrames[0][0]);
        itemRegions.put(InteractiveObject.Type.BATTERY, itFrames[0][1]);
        itemRegions.put(InteractiveObject.Type.MEDKIT, itFrames[0][2]);
        itemRegions.put(InteractiveObject.Type.RADIO, itFrames[0][3]);

        itemRegions.put(InteractiveObject.Type.PHOTO_FRAG_1, itFrames[1][0]);
        itemRegions.put(InteractiveObject.Type.PHOTO_FRAG_2, itFrames[1][1]);
        itemRegions.put(InteractiveObject.Type.PHOTO_FRAG_3, itFrames[1][2]);
        itemRegions.put(InteractiveObject.Type.NOTE_1, itFrames[1][3]);
        itemRegions.put(InteractiveObject.Type.NOTE_2, itFrames[1][3]);
        itemRegions.put(InteractiveObject.Type.NOTE_3, itFrames[1][3]);

        itemRegions.put(InteractiveObject.Type.SEAL_1, itFrames[2][0]);
        itemRegions.put(InteractiveObject.Type.SEAL_2, itFrames[2][1]);
        itemRegions.put(InteractiveObject.Type.SEAL_3, itFrames[2][2]);

        itemRegions.put(InteractiveObject.Type.CABIN_DOOR, prFrames[0][2]);
        itemRegions.put(InteractiveObject.Type.CRYPT_GATE, prFrames[0][2]);
        itemRegions.put(InteractiveObject.Type.ALTAR, new TextureRegion(propsSheet, 64, 128, 128, 64));

        // UI
        uiHealthCross = new TextureRegion(uiSheet, 4, 4, 28, 28);
        uiHealthFrame = new TextureRegion(uiSheet, 40, 8, 140, 20);
        uiHealthFill = new TextureRegion(uiSheet, 44, 12, 132, 12);

        uiBatteryBolt = new TextureRegion(uiSheet, 4, 40, 28, 28);
        uiBatteryFrame = new TextureRegion(uiSheet, 40, 44, 140, 20);
        uiBatteryFill = new TextureRegion(uiSheet, 44, 48, 132, 12);

        uiObjectiveDiamond = new TextureRegion(uiSheet, 4, 80, 24, 24);
        uiCurseSkull = new TextureRegion(uiSheet, 40, 80, 24, 24);
        uiMinimapFrame = new TextureRegion(uiSheet, 4, 120, 128, 128);
        uiPromptBox = new TextureRegion(uiSheet, 140, 120, 160, 40);

        loadFonts();
        loadSounds();
    }

    private void loadFonts() {
        FreeTypeFontGenerator horrorGen = new FreeTypeFontGenerator(Gdx.files.internal("fonts/horror.ttf"));
        FreeTypeFontGenerator uiGen = new FreeTypeFontGenerator(Gdx.files.internal("fonts/ui.ttf"));

        FreeTypeFontParameter pTitle = new FreeTypeFontParameter();
        pTitle.size = 46;
        titleFont = horrorGen.generateFont(pTitle);

        FreeTypeFontParameter pMenu = new FreeTypeFontParameter();
        pMenu.size = 24;
        menuFont = horrorGen.generateFont(pMenu);

        FreeTypeFontParameter pHeader = new FreeTypeFontParameter();
        pHeader.size = 20;
        headerFont = uiGen.generateFont(pHeader);

        FreeTypeFontParameter pBody = new FreeTypeFontParameter();
        pBody.size = 16;
        bodyFont = uiGen.generateFont(pBody);

        FreeTypeFontParameter pSmall = new FreeTypeFontParameter();
        pSmall.size = 13;
        smallFont = uiGen.generateFont(pSmall);

        horrorGen.dispose();
        uiGen.dispose();
    }

    private void loadSounds() {
        String[] soundNames = {
                "music_menu", "music_stage1", "music_stage2", "music_stage3", "music_chase",
                "music_gameover", "music_victory", "ambient_music",
                "rain_loop", "wind_loop", "heartbeat", "footstep",
                "flashlight_click", "radio_static", "branch_snap", "creature_whisper",
                "creature_screech", "creature_breath", "item_pickup", "door_unlock",
                "curse_trigger", "button_click"
        };
        for (String name : soundNames) {
            String path = "audio/" + name + ".wav";
            if (Gdx.files.internal(path).exists()) {
                sounds.put(name, Gdx.audio.newSound(Gdx.files.internal(path)));
            }
        }
    }

    public TextureRegion getTileRegion(TileType type) {
        return tileRegions.getOrDefault(type, tileRegions.get(TileType.FOREST_DIRT));
    }

    public TextureRegion getObstacleRegion(Obstacle.Type type) {
        return obstacleRegions.getOrDefault(type, obstacleRegions.get(Obstacle.Type.TREE));
    }

    public TextureRegion getItemRegion(InteractiveObject.Type type) {
        return itemRegions.getOrDefault(type, itemRegions.get(InteractiveObject.Type.NOTE_1));
    }

    public Sound getSound(String name) {
        return sounds.get(name);
    }

    @Override
    public void dispose() {
        if (titleBg != null) titleBg.dispose();
        if (titleLogo != null) titleLogo.dispose();
        if (menuBanner != null) menuBanner.dispose();
        if (playerSheet != null) playerSheet.dispose();
        if (watcherSheet != null) watcherSheet.dispose();
        if (tilesSheet != null) tilesSheet.dispose();
        if (propsSheet != null) propsSheet.dispose();
        if (treesSheet != null) treesSheet.dispose();
        if (itemsSheet != null) itemsSheet.dispose();
        if (uiSheet != null) uiSheet.dispose();
        if (lightCone != null) lightCone.dispose();
        if (lightHalo != null) lightHalo.dispose();
        if (fogTexture != null) fogTexture.dispose();
        if (vignette != null) vignette.dispose();

        if (titleFont != null) titleFont.dispose();
        if (menuFont != null) menuFont.dispose();
        if (headerFont != null) headerFont.dispose();
        if (bodyFont != null) bodyFont.dispose();
        if (smallFont != null) smallFont.dispose();

        for (Sound s : sounds.values()) {
            s.dispose();
        }
        sounds.clear();
    }
}
