package com.hollowwoods.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.hollowwoods.game.MainGame;
import com.hollowwoods.game.entities.InteractiveObject;
import com.hollowwoods.game.entities.Player;
import com.hollowwoods.game.entities.Watcher;
import com.hollowwoods.game.systems.*;
import com.hollowwoods.game.ui.DialogBox;
import com.hollowwoods.game.ui.HUD;
import com.hollowwoods.game.utils.Constants;
import com.hollowwoods.game.utils.GameAssets;
import com.hollowwoods.game.world.CollisionManager;
import com.hollowwoods.game.world.GameMap;
import com.hollowwoods.game.world.StageData;

public class GameScreen extends BaseScreen {

    public final int stageNumber;
    private final GameMap gameMap;
    private final Player player;
    private final Watcher watcher;
    private final CollisionManager collisionManager;
    private final AIController aiController;

    private final OrthographicCamera worldCamera;
    private final Viewport worldViewport;
    private final HUD hud;
    private final DialogBox dialogBox;
    private final LightingSystem lightingSystem;
    private final WeatherSystem weatherSystem;

    private final Vector2 worldMouse = new Vector2();
    private final Vector3 unprojectWorldTmp = new Vector3();

    private float damageFlashTimer = 0f;
    private float screenShakeTimer = 0f;
    private final Vector2 shakeOffset = new Vector2();

    public GameScreen(MainGame game, int stageNumber, float initialHealth, float initialBattery) {
        super(game);
        this.stageNumber = stageNumber;

        StageData stageData = StageData.createStage(stageNumber);
        this.gameMap = new GameMap(stageData);
        this.collisionManager = new CollisionManager();
        this.aiController = new AIController(collisionManager);

        this.player = new Player(stageData.playerSpawn.x, stageData.playerSpawn.y);
        this.player.health = initialHealth;
        this.player.battery = initialBattery;

        this.watcher = new Watcher(stageData.watcherSpawn.x, stageData.watcherSpawn.y);

        this.worldCamera = new OrthographicCamera();
        this.worldViewport = new FitViewport(Constants.VIRTUAL_WIDTH, Constants.VIRTUAL_HEIGHT, worldCamera);
        this.worldCamera.zoom = 0.85f;

        this.hud = new HUD();
        this.dialogBox = new DialogBox();
        this.lightingSystem = new LightingSystem();
        this.weatherSystem = new WeatherSystem();

        game.getSaveManager().saveCheckpoint(
                stageNumber,
                game.getScoreManager().calculateTotalScore(),
                game.getCurseManager().getActiveCurse().name(),
                player.health,
                player.battery
        );
    }

    @Override
    public void show() {
        game.getAudioManager().playStageAudio(stageNumber);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        worldViewport.update(width, height, false);
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        GameAssets assets = game.getAssets();
        AudioManager audioManager = game.getAudioManager();
        CurseManager curseManager = game.getCurseManager();
        ScoreManager scoreManager = game.getScoreManager();
        InventoryManager inventory = game.getInventoryManager();

        // 1. Menu de Pausa [ESC]
        audioManager.update(delta);
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if (dialogBox.isOpen()) {
                dialogBox.close();
            } else {
                game.pauseGame(this);
                return;
            }
        }

        // Fechar diálogo com [ESPAÇO] ou [E]
        if (dialogBox.isOpen()) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) ||
                    Gdx.input.isKeyJustPressed(Input.Keys.ENTER) ||
                    Gdx.input.isKeyJustPressed(Input.Keys.E)) {
                dialogBox.close();
            }
        }

        // 2. Coordenadas do mouse no mundo
        unprojectWorldTmp.set(Gdx.input.getX(), Gdx.input.getY(), 0f);
        worldViewport.unproject(unprojectWorldTmp);
        worldMouse.set(unprojectWorldTmp.x, unprojectWorldTmp.y);

        // 3. Atualização lógica de gameplay
        if (!dialogBox.isOpen()) {
            // Uso rápido de itens
            if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
                if (inventory.useBatteryPack()) {
                    player.rechargeBattery(50f);
                    audioManager.playSound("flashlight_click");
                }
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
                if (inventory.useMedkit()) {
                    player.heal(40f);
                    audioManager.playSound("item_pickup");
                }
            }

            // Input e física do Jogador
            boolean blackout = curseManager.isFlashlightDisabled();
            float drainMult = curseManager.getBatteryDrainMultiplier();
            player.handleInput(worldMouse, delta, blackout, drainMult);
            player.updatePhysics(
                    delta, collisionManager, gameMap.stageData.obstacles,
                    Constants.MAP_WIDTH * Constants.TILE_SIZE,
                    Constants.MAP_HEIGHT * Constants.TILE_SIZE
            );

            // Passos de áudio controlados por cadência natural (impede acúmulo)
            audioManager.updateMovement(delta, player.isMoving);

            // Atualização da IA do Watcher
            float prevHealth = player.health;
            aiController.update(
                    delta, watcher, player, gameMap.stageData.obstacles,
                    curseManager, stageNumber,
                    Constants.MAP_WIDTH * Constants.TILE_SIZE,
                    Constants.MAP_HEIGHT * Constants.TILE_SIZE
            );

            // Camada dinâmica de perseguição (Watcher em HUNTING)
            audioManager.setChaseActive(watcher.state == Watcher.State.HUNTING);

            if (player.health < prevHealth) {
                scoreManager.recordDamageTaken();
                damageFlashTimer = 0.45f;
                screenShakeTimer = 0.35f;
                audioManager.playSound("creature_screech");
            }

            // Atualização de maldições e som fantasma de WHISPERING
            curseManager.update(delta);
            if (curseManager.triggerPhantomSound) {
                audioManager.playSound("branch_snap", 0.9f);
            }

            // Batimentos cardíacos por proximidade
            audioManager.updateWatcherProximity(delta, watcher.distanceToPlayer, Constants.WATCHER_DETECTION_RANGE);

            // Pontuação por tempo
            scoreManager.update(delta);

            // Atualização de exploração do mapa
            gameMap.updateExploration(player.position, Constants.FLASHLIGHT_RANGE * 0.75f);

            // Interações com objetos [E]
            InteractiveObject nearby = gameMap.getNearbyInteractiveObject(player.position, 60f);
            if (nearby != null && (Gdx.input.isKeyJustPressed(Input.Keys.E) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE))) {
                handleInteraction(nearby, inventory, scoreManager, audioManager);
            }
        }

        // 4. Câmera com suavização e screen shake
        float lerp = 4.5f * delta;
        worldCamera.position.x += (player.position.x - worldCamera.position.x) * lerp;
        worldCamera.position.y += (player.position.y - worldCamera.position.y) * lerp;

        if (screenShakeTimer > 0f) {
            screenShakeTimer -= delta;
            shakeOffset.set(MathUtils.random(-6f, 6f), MathUtils.random(-6f, 6f));
        } else {
            shakeOffset.setZero();
        }

        worldCamera.position.x += shakeOffset.x;
        worldCamera.position.y += shakeOffset.y;

        float halfW = (worldCamera.viewportWidth * worldCamera.zoom) / 2f;
        float halfH = (worldCamera.viewportHeight * worldCamera.zoom) / 2f;
        worldCamera.position.x = MathUtils.clamp(worldCamera.position.x, halfW, Constants.MAP_WIDTH * Constants.TILE_SIZE - halfW);
        worldCamera.position.y = MathUtils.clamp(worldCamera.position.y, halfH, Constants.MAP_HEIGHT * Constants.TILE_SIZE - halfH);
        worldCamera.update();

        // 5. ATUALIZAÇÃO E RENDERIZAÇÃO DO MUNDO
        weatherSystem.update(delta);
        boolean isBlackout = curseManager.isFlashlightDisabled();
        lightingSystem.updateLightmap(worldCamera, assets, player, isBlackout, stageNumber);

        batch.setProjectionMatrix(worldCamera.combined);
        batch.begin();

        gameMap.renderTiles(batch, assets, worldCamera);
        gameMap.renderInteractiveObjects(batch, assets, player.stateTime);
        renderPlayer(batch, assets);
        renderWatcher(batch, assets);
        gameMap.renderObstacles(batch, assets);

        batch.end();

        // 6. ILUMINAÇÃO DINÂMICA (LIGHTMAP E FEIXE VOLUMÉTRICO)
        lightingSystem.renderLightmap(batch, camera);
        lightingSystem.renderVolumetricBeam(batch, worldCamera, assets, player, isBlackout);

        // 7. EFEITOS CLIMÁTICOS (NEBLINA SUAVE E CHUVA FINA)
        weatherSystem.renderFog(batch, camera, assets);
        weatherSystem.renderRain(camera);

        // 8. RENDERIZAÇÃO DE OVERLAYS DE TELA E INTERFACE
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        // Vinheta suave
        batch.draw(assets.vignette, 0, 0, Constants.VIRTUAL_WIDTH, Constants.VIRTUAL_HEIGHT);

        // Flash de dano avermelhado
        if (damageFlashTimer > 0f) {
            damageFlashTimer -= delta;
            batch.setColor(0.9f, 0.15f, 0.15f, Math.min(0.45f, damageFlashTimer * 1.2f));
            batch.draw(assets.titleBg, 0, 0, Constants.VIRTUAL_WIDTH, Constants.VIRTUAL_HEIGHT);
            batch.setColor(Color.WHITE);
        }
        batch.end();

        // HUD e Diálogos
        InteractiveObject nearby = gameMap.getNearbyInteractiveObject(player.position, 60f);
        hud.render(batch, camera, assets, player, gameMap, curseManager, inventory, nearby);
        dialogBox.render(batch, shapeRenderer, assets);

        // 9. Verificação de Game Over
        if (player.health <= 0f) {
            scoreManager.setFinalBatteryPercent(player.battery);
            game.gameOver();
        }
    }

    private void renderPlayer(SpriteBatch batch, GameAssets assets) {
        TextureRegion frame = player.isMoving ?
                assets.playerWalk[player.direction].getKeyFrame(player.stateTime) :
                assets.playerIdle[player.direction];

        if (player.invulnerabilityTimer > 0f && ((int)(player.invulnerabilityTimer * 10f) % 2 == 0)) {
            batch.setColor(1f, 0.4f, 0.4f, 0.85f);
        } else {
            batch.setColor(Color.WHITE);
        }

        batch.draw(frame, player.position.x - 32, player.position.y - 32, 64, 64);
        batch.setColor(Color.WHITE);
    }

    private void renderWatcher(SpriteBatch batch, GameAssets assets) {
        TextureRegion frame;
        if (watcher.state == Watcher.State.OBSERVED) {
            frame = assets.watcherIdle[watcher.direction];
        } else if (watcher.state == Watcher.State.HUNTING) {
            frame = assets.watcherAttack[watcher.direction];
        } else {
            frame = assets.watcherWalk[watcher.direction].getKeyFrame(watcher.stateTime);
        }

        batch.setColor(Color.WHITE);
        batch.draw(frame, watcher.position.x - 32, watcher.position.y - 32, 64, 64);
    }

    private void handleInteraction(InteractiveObject obj, InventoryManager inventory,
                                  ScoreManager scoreManager, AudioManager audioManager) {
        switch (obj.type) {
            case KEY_CABIN:
                obj.collected = true;
                inventory.setHasCabinKey(true);
                scoreManager.addItem();
                audioManager.playSound("item_pickup");
                dialogBox.show(obj.loreTitle, obj.loreContent + "\n\n[ Chave Enferrujada adicionada ao inventário ]");
                break;

            case BATTERY:
                obj.collected = true;
                inventory.addBatteryPack();
                scoreManager.addItem();
                audioManager.playSound("item_pickup");
                dialogBox.show(obj.loreTitle, obj.loreContent + "\n\n[ Pressione [Q] para recarregar a lanterna ]");
                break;

            case MEDKIT:
                obj.collected = true;
                inventory.addMedkit();
                scoreManager.addItem();
                audioManager.playSound("item_pickup");
                dialogBox.show(obj.loreTitle, obj.loreContent + "\n\n[ Pressione [R] para curar seus ferimentos ]");
                break;

            case RADIO:
                audioManager.playSound("radio_static");
                scoreManager.addClue();
                dialogBox.show(obj.loreTitle, obj.loreContent);
                break;

            case NOTE_1:
            case NOTE_2:
            case NOTE_3:
                obj.collected = true;
                inventory.addNote();
                scoreManager.addClue();
                audioManager.playSound("item_pickup");
                dialogBox.show(obj.loreTitle, obj.loreContent);
                break;

            case PHOTO_FRAG_1:
            case PHOTO_FRAG_2:
            case PHOTO_FRAG_3:
                obj.collected = true;
                inventory.addPhotoFragment();
                scoreManager.addClue();
                audioManager.playSound("item_pickup");
                dialogBox.show(obj.loreTitle, obj.loreContent + "\n\n[ Fragmentos Coletados: " + inventory.getPhotoFragments() + "/3 ]");
                break;

            case SEAL_1:
            case SEAL_2:
            case SEAL_3:
                obj.collected = true;
                inventory.addRitualSeal();
                scoreManager.addClue();
                audioManager.playSound("curse_trigger", 1.4f);
                dialogBox.show(obj.loreTitle, obj.loreContent + "\n\n[ Selos Rituais Ativos: " + inventory.getRitualSeals() + "/3 ]");
                break;

            case CABIN_DOOR:
                if (inventory.hasCabinKey()) {
                    audioManager.playSound("door_unlock");
                    scoreManager.completeStage();
                    game.startStage(2, player.health, player.battery);
                } else {
                    audioManager.playSound("branch_snap");
                    dialogBox.show(obj.loreTitle, obj.loreContent);
                }
                break;

            case CRYPT_GATE:
                if (inventory.getPhotoFragments() >= 3) {
                    audioManager.playSound("door_unlock");
                    scoreManager.completeStage();
                    game.startStage(3, player.health, player.battery);
                } else {
                    audioManager.playSound("branch_snap");
                    dialogBox.show(obj.loreTitle, "O portão de ferro permanece lacrado. Requer os 3 fragmentos da fotografia para quebrar a barreira espiritual. Fragmentos: " + inventory.getPhotoFragments() + "/3.");
                }
                break;

            case ALTAR:
                if (inventory.getRitualSeals() >= 3) {
                    audioManager.playSound("curse_trigger");
                    game.victory();
                } else {
                    audioManager.playSound("branch_snap");
                    dialogBox.show(obj.loreTitle, "O Altar da Origem exige a colocação dos 3 Selos Rituais para dissipar o ciclo do Observador. Selos: " + inventory.getRitualSeals() + "/3.");
                }
                break;
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        hud.dispose();
        lightingSystem.dispose();
        weatherSystem.dispose();
    }
}
