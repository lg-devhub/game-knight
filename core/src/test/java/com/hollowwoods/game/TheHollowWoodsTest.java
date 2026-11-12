package com.hollowwoods.game;

import com.hollowwoods.game.entities.InteractiveObject;
import com.hollowwoods.game.entities.Player;
import com.hollowwoods.game.entities.Watcher;
import com.hollowwoods.game.systems.CurseManager;
import com.hollowwoods.game.systems.InventoryManager;
import com.hollowwoods.game.systems.ScoreManager;
import com.hollowwoods.game.utils.Constants;
import com.hollowwoods.game.world.CollisionManager;
import com.hollowwoods.game.world.Obstacle;
import com.hollowwoods.game.world.StageData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TheHollowWoodsTest {

    private Player player;
    private Watcher watcher;
    private List<Obstacle> obstacles;
    private CollisionManager collisionManager;
    private CurseManager curseManager;

    @BeforeEach
    public void setUp() {
        player = new Player(200, 200);
        watcher = new Watcher(350, 200); // 150 unidades à direita
        obstacles = new ArrayList<>();
        collisionManager = new CollisionManager();
        curseManager = new CurseManager();
    }

    @Test
    public void testWatcherFrozenWhenDirectlyObserved() {
        // Jogador em (200, 200) virado para a Direita (0 graus) com lanterna ligada
        player.flashlightAngle = 0f;
        player.isFlashlightOn = true;
        player.battery = 100f;

        watcher.updateAI(0.016f, player, obstacles, collisionManager, false, 1.0f, 1, 2560, 2560);

        assertTrue(watcher.isDirectlyObserved, "The Watcher deve ser detectada como observada no cone da lanterna");
        assertEquals(Watcher.State.OBSERVED, watcher.state, "Estado deve ser OBSERVED");
        assertEquals(0f, watcher.velocity.len(), 0.001f, "Velocidade deve ser ZERO quando observada (congelada!)");
    }

    @Test
    public void testWatcherMovesWhenBehindPlayer() {
        player.flashlightAngle = 0f; // Olhando para a Direita
        player.isFlashlightOn = true;

        // Criatura posicionada atrás do jogador em (50, 200)
        watcher.position.set(50, 200);
        watcher.updateBounds();

        watcher.updateAI(0.016f, player, obstacles, collisionManager, false, 1.0f, 1, 2560, 2560);

        assertFalse(watcher.isDirectlyObserved, "Criatura atrás do jogador NÃO deve ser considerada observada");
        assertNotEquals(Watcher.State.OBSERVED, watcher.state);
        assertTrue(watcher.isMoving, "Criatura não observada deve estar em movimento");
        assertTrue(watcher.velocity.len() > 0f);
        assertTrue(watcher.velocity.x > 0f, "Deve mover-se na direção X positiva em direção ao jogador");
    }

    @Test
    public void testWatcherOccludedByObstacleRaycast() {
        player.flashlightAngle = 0f;
        player.isFlashlightOn = true;

        watcher.position.set(450, 200);
        watcher.updateBounds();

        // Insere obstáculo sólido que bloqueia luz entre jogador e criatura (x=300..364)
        obstacles.add(new Obstacle(300, 180, 64, 64, Obstacle.Type.CABIN_WALL));

        watcher.updateAI(0.016f, player, obstacles, collisionManager, false, 1.0f, 1, 2560, 2560);

        assertFalse(watcher.isDirectlyObserved, "Criatura atrás de parede sólida NÃO pode ser observada (raycast bloqueado)");
        assertNotEquals(Watcher.State.OBSERVED, watcher.state);
    }

    @Test
    public void testWatcherBeyondFlashlightRangeNotObserved() {
        player.flashlightAngle = 0f;
        player.isFlashlightOn = true;

        // Criatura além do alcance da lanterna (380 unidades)
        watcher.position.set(200 + Constants.FLASHLIGHT_RANGE + 100, 200);
        watcher.updateBounds();

        watcher.updateAI(0.016f, player, obstacles, collisionManager, false, 1.0f, 1, 2560, 2560);

        assertFalse(watcher.isDirectlyObserved, "Criatura além do alcance máximo da lanterna não pode ser observada");
        assertNotEquals(Watcher.State.OBSERVED, watcher.state);
    }

    @Test
    public void testFlashlightOffAllowsWatcherMovement() {
        player.flashlightAngle = 0f;
        player.isFlashlightOn = false; // Desligada

        watcher.updateAI(0.016f, player, obstacles, collisionManager, false, 1.0f, 1, 2560, 2560);

        assertFalse(player.isLightEffective(false));
        assertFalse(watcher.isDirectlyObserved);
        assertNotEquals(Watcher.State.OBSERVED, watcher.state);
    }

    @Test
    public void testCurseSystemModifiesGameplay() {
        // 1. BLACKOUT
        curseManager.setCurse(CurseManager.CurseType.BLACKOUT);
        assertEquals("BLACKOUT", curseManager.getActiveCurse().name());

        // 2. FRAGILE_LIGHT (dreno 2.5x)
        curseManager.setCurse(CurseManager.CurseType.FRAGILE_LIGHT);
        assertEquals(2.5f, curseManager.getBatteryDrainMultiplier(), 0.01f);

        // 3. LOST_PATH (minimap desativado)
        curseManager.setCurse(CurseManager.CurseType.LOST_PATH);
        assertTrue(curseManager.isMinimapDisabled());

        // 4. WHISPERING
        curseManager.setCurse(CurseManager.CurseType.WHISPERING);
        assertEquals(CurseManager.CurseType.WHISPERING, curseManager.getActiveCurse());

        // 5. THE_WATCHER (velocidade +50%)
        curseManager.setCurse(CurseManager.CurseType.THE_WATCHER);
        assertEquals(1.50f, curseManager.getWatcherSpeedMultiplier(), 0.01f);

        // Teste de aleatoriedade do sorteio
        CurseManager.CurseType rolled = curseManager.rollRandomCurse();
        assertNotNull(rolled);
    }

    @Test
    public void testStageProgressionAndStructure() {
        // Stage 1
        StageData s1 = StageData.createStage(1);
        assertEquals(1, s1.stageNumber);
        assertTrue(s1.stageName.contains("Stage 1"));
        boolean hasKey = s1.interactiveObjects.stream().anyMatch(o -> o.type == InteractiveObject.Type.KEY_CABIN);
        boolean hasCabinDoor = s1.interactiveObjects.stream().anyMatch(o -> o.type == InteractiveObject.Type.CABIN_DOOR);
        assertTrue(hasKey, "Stage 1 deve conter a Chave Enferrujada");
        assertTrue(hasCabinDoor, "Stage 1 deve conter a Porta da Cabana");

        // Stage 2
        StageData s2 = StageData.createStage(2);
        assertEquals(2, s2.stageNumber);
        assertTrue(s2.stageName.contains("Stage 2"));
        long photos = s2.interactiveObjects.stream().filter(o ->
                o.type == InteractiveObject.Type.PHOTO_FRAG_1 ||
                o.type == InteractiveObject.Type.PHOTO_FRAG_2 ||
                o.type == InteractiveObject.Type.PHOTO_FRAG_3
        ).count();
        boolean hasCryptGate = s2.interactiveObjects.stream().anyMatch(o -> o.type == InteractiveObject.Type.CRYPT_GATE);
        assertEquals(3, photos, "Stage 2 deve conter os 3 Fragmentos de Fotografia");
        assertTrue(hasCryptGate, "Stage 2 deve conter o Portão da Cripta");

        // Stage 3
        StageData s3 = StageData.createStage(3);
        assertEquals(3, s3.stageNumber);
        assertTrue(s3.stageName.contains("Stage 3"));
        long seals = s3.interactiveObjects.stream().filter(o ->
                o.type == InteractiveObject.Type.SEAL_1 ||
                o.type == InteractiveObject.Type.SEAL_2 ||
                o.type == InteractiveObject.Type.SEAL_3
        ).count();
        boolean hasAltar = s3.interactiveObjects.stream().anyMatch(o -> o.type == InteractiveObject.Type.ALTAR);
        assertEquals(3, seals, "Stage 3 deve conter os 3 Selos Rituais");
        assertTrue(hasAltar, "Stage 3 deve conter o Altar da Origem");
    }

    @Test
    public void testScoringCalculation() {
        ScoreManager sm = new ScoreManager();
        sm.update(60f); // 60s * 12 = 720
        sm.addClue();   // 350
        sm.addClue();   // 350
        sm.addItem();   // 150
        sm.completeStage(); // 1500
        sm.setFinalBatteryPercent(80f); // 80 * 5 = 400
        sm.recordDamageTaken(); // -250 penalidade

        int score = sm.calculateTotalScore();
        // 720 + 700 + 150 + 1500 + 400 - 250 = 3220
        assertEquals(3220, score);
    }

    @Test
    public void testInventoryManagement() {
        InventoryManager inv = new InventoryManager();
        assertFalse(inv.hasCabinKey());
        inv.setHasCabinKey(true);
        assertTrue(inv.hasCabinKey());

        assertEquals(0, inv.getPhotoFragments());
        inv.addPhotoFragment();
        inv.addPhotoFragment();
        inv.addPhotoFragment();
        assertEquals(3, inv.getPhotoFragments());

        assertEquals(0, inv.getRitualSeals());
        inv.addRitualSeal();
        assertEquals(1, inv.getRitualSeals());

        // Baterias
        assertEquals(0, inv.getBatteryPacks());
        inv.addBatteryPack();
        assertEquals(1, inv.getBatteryPacks());
        assertTrue(inv.useBatteryPack());
        assertEquals(0, inv.getBatteryPacks());
        assertFalse(inv.useBatteryPack());

        // Curativos
        assertEquals(0, inv.getMedkits());
        inv.addMedkit();
        assertEquals(1, inv.getMedkits());
        assertTrue(inv.useMedkit());
        assertEquals(0, inv.getMedkits());
    }

    @Test
    public void testVisualAssetFilesPresenceAndIntegrity() {
        String[] requiredImages = {
                "title_bg.png",
                "title_logo.png",
                "menu_banner.png",
                "player.png",
                "watcher.png",
                "tiles.png",
                "props.png",
                "trees.png",
                "items.png",
                "ui.png",
                "light_cone.png",
                "light_halo.png",
                "fog.png",
                "vignette.png"
        };

        for (String imgName : requiredImages) {
            java.io.File file = new java.io.File("assets/images/" + imgName);
            if (!file.exists()) {
                file = new java.io.File("../assets/images/" + imgName);
            }
            assertTrue(file.exists(), "Asset gráfico obrigatório não encontrado: " + imgName);
            assertTrue(file.length() > 500, "Arquivo de asset corrompido ou vazio: " + imgName);
        }
    }

    @Test
    public void testStageVisualLayoutsAndObjectives() {
        for (int s = 1; s <= 3; s++) {
            StageData stage = StageData.createStage(s);
            assertNotNull(stage.stageName);
            assertNotNull(stage.objectiveText);
            assertNotNull(stage.playerSpawn);
            assertNotNull(stage.watcherSpawn);
            assertFalse(stage.obstacles.isEmpty(), "Estágio " + s + " deve conter obstáculos de cenário");
            assertFalse(stage.interactiveObjects.isEmpty(), "Estágio " + s + " deve conter objetivos interativos");

            // Verifica que o spawn do jogador não está colidindo imediatamente com um obstáculo
            for (Obstacle o : stage.obstacles) {
                assertFalse(o.bounds.contains(stage.playerSpawn.x, stage.playerSpawn.y),
                        "Spawn do jogador no estágio " + s + " colide com obstáculo!");
            }
        }
    }

    @Test
    public void testAudioFilesPresenceAndIntegrity() {
        String[] requiredAudio = {
                "music_menu.wav",
                "music_stage1.wav",
                "music_stage2.wav",
                "music_stage3.wav",
                "music_chase.wav",
                "music_gameover.wav",
                "music_victory.wav",
                "rain_loop.wav",
                "wind_loop.wav",
                "heartbeat.wav",
                "footstep.wav",
                "flashlight_click.wav",
                "radio_static.wav",
                "branch_snap.wav",
                "creature_whisper.wav",
                "creature_screech.wav",
                "creature_breath.wav",
                "item_pickup.wav",
                "door_unlock.wav",
                "curse_trigger.wav",
                "button_click.wav"
        };

        for (String audName : requiredAudio) {
            java.io.File file = new java.io.File("assets/audio/" + audName);
            if (!file.exists()) {
                file = new java.io.File("../assets/audio/" + audName);
            }
            assertTrue(file.exists(), "Asset de áudio obrigatório não encontrado: " + audName);
            assertTrue(file.length() > 2000, "Arquivo de áudio muito curto ou corrompido: " + audName);
        }
    }

    @Test
    public void testAudioMixerVolumeChannelsAndFormulas() {
        com.hollowwoods.game.systems.AudioManager audio = new com.hollowwoods.game.systems.AudioManager(null);

        // Volumes padrão conservadores exigidos por AGENTS.md § 7
        assertEquals(0.40f, audio.getMasterVolume(), 0.001f);
        assertEquals(0.18f, audio.getMusicVolume(), 0.001f);
        assertEquals(0.14f, audio.getAmbientVolume(), 0.001f);
        assertEquals(0.25f, audio.getSfxVolume(), 0.001f);

        // Fórmulas de volume efetivo: Master * Canal
        assertEquals(0.40f * 0.18f, audio.getEffectiveMusicVolume(), 0.001f);
        assertEquals(0.40f * 0.14f, audio.getEffectiveAmbientVolume(), 0.001f);
        assertEquals(0.40f * 0.25f, audio.getEffectiveSfxVolume(), 0.001f);

        // Alteração e limites (clamp 0..1)
        audio.setMasterVolume(1.5f);
        assertEquals(1.0f, audio.getMasterVolume(), 0.001f);
        audio.setMasterVolume(-0.2f);
        assertEquals(0.0f, audio.getMasterVolume(), 0.001f);
    }

    @Test
    public void testAudioMuteAndUnmuteBehaviors() {
        com.hollowwoods.game.systems.AudioManager audio = new com.hollowwoods.game.systems.AudioManager(null);
        assertFalse(audio.isMuted());

        audio.setMuted(true);
        assertTrue(audio.isMuted());
        assertEquals(0.0f, audio.getEffectiveMusicVolume(), 0.001f, "Muted deve zerar volume efetivo");
        assertEquals(0.0f, audio.getEffectiveAmbientVolume(), 0.001f, "Muted deve zerar volume efetivo");
        assertEquals(0.0f, audio.getEffectiveSfxVolume(), 0.001f, "Muted deve zerar volume efetivo");

        audio.toggleMute();
        assertFalse(audio.isMuted());
        assertTrue(audio.getEffectiveMusicVolume() > 0f);
    }

    @Test
    public void testAudioPauseStateAttenuation() {
        com.hollowwoods.game.systems.AudioManager audio = new com.hollowwoods.game.systems.AudioManager(null);
        assertFalse(audio.isPaused());

        audio.pauseGameplayAudio();
        assertTrue(audio.isPaused());

        audio.resumeGameplayAudio();
        assertFalse(audio.isPaused());
    }
}
