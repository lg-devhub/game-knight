package com.hollowwoods.game.systems;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;
import com.hollowwoods.game.utils.GameAssets;

/**
 * Gerenciador central de áudio para THE HOLLOW WOODS em estrita conformidade com
 * as diretrizes de AGENTS.md § 7 e PROMPTS.md:
 * - Volumes conservadores por canal (Master: 0.40, Música: 0.18, Ambiente: 0.14, Efeitos: 0.25).
 * - Fades suaves de entrada/saída (sem cortes abruptos nem cliques sonoros).
 * - Camada dinâmica de perseguição (Watcher em HUNTING).
 * - Controle anti-acúmulo de passos e efeitos (timer de cadência real).
 * - Pausa com atenuação consistente do áudio de gameplay.
 * - Suporte a Mute e controles independentes de volume.
 */
public class AudioManager {

    private final GameAssets assets;

    // Configurações de volume (AGENTS.md § 7)
    private float masterVolume = 0.40f;
    private float musicVolume = 0.18f;
    private float ambientVolume = 0.14f;
    private float sfxVolume = 0.25f;
    private boolean isMuted = false;
    private boolean isPaused = false;

    // Estado da Trilha Musical Principal
    private String currentMusicName = null;
    private Sound currentMusicSound = null;
    private long musicId = -1;
    private float currentMusicFactor = 0f;
    private float targetMusicFactor = 0f;

    // Camada Dinâmica de Perseguição (HUNTING)
    private Sound chaseSound = null;
    private long chaseId = -1;
    private float currentChaseFactor = 0f;
    private float targetChaseFactor = 0f;
    private boolean isChaseActive = false;

    // Loops Ambientais (Chuva e Vento)
    private Sound rainSound = null;
    private long rainId = -1;
    private float targetRainFactor = 0f;
    private float currentRainFactor = 0f;

    private Sound windSound = null;
    private long windId = -1;
    private float targetWindFactor = 0f;
    private float currentWindFactor = 0f;

    // Temporizadores de Controle e Anti-Acúmulo
    private float footstepTimer = 0f;
    private static final float FOOTSTEP_INTERVAL = 0.42f; // Cadência natural de passos (impede acúmulo no render)
    private float heartbeatTimer = 0f;
    private float sfxCooldownTimer = 0f;

    public AudioManager(GameAssets assets) {
        this.assets = assets;
    }

    /**
     * Atualização contínua de áudio chamada no loop de jogo:
     * Realiza interpolação suave de fades de volume (fade-in e fade-out sem estalos).
     */
    public void update(float delta) {
        if (sfxCooldownTimer > 0f) {
            sfxCooldownTimer -= delta;
        }

        if (footstepTimer > 0f) {
            footstepTimer -= delta;
        }

        float pauseFactor = isPaused ? 0.20f : 1.0f;
        float fadeSpeed = 2.2f * delta;

        // 1. Atualização do fade da Música Principal
        currentMusicFactor = MathUtils.lerp(currentMusicFactor, targetMusicFactor * pauseFactor, fadeSpeed);
        if (currentMusicSound != null && musicId != -1) {
            float effVol = isMuted ? 0f : getEffectiveMusicVolume() * currentMusicFactor;
            currentMusicSound.setVolume(musicId, effVol);
            if (currentMusicFactor < 0.01f && targetMusicFactor == 0f) {
                currentMusicSound.stop(musicId);
                musicId = -1;
                currentMusicSound = null;
                currentMusicName = null;
            }
        }

        // 2. Atualização da Camada de Perseguição
        currentChaseFactor = MathUtils.lerp(currentChaseFactor, targetChaseFactor * pauseFactor, fadeSpeed * 1.5f);
        if (chaseSound != null && chaseId != -1) {
            float effVol = isMuted ? 0f : getEffectiveMusicVolume() * currentChaseFactor * 0.85f;
            chaseSound.setVolume(chaseId, effVol);
            if (currentChaseFactor < 0.01f && targetChaseFactor == 0f) {
                chaseSound.stop(chaseId);
                chaseId = -1;
            }
        }

        // 3. Atualização do Ambiente de Chuva
        currentRainFactor = MathUtils.lerp(currentRainFactor, targetRainFactor * pauseFactor, fadeSpeed);
        if (rainSound != null && rainId != -1) {
            float effVol = isMuted ? 0f : getEffectiveAmbientVolume() * currentRainFactor;
            rainSound.setVolume(rainId, effVol);
            if (currentRainFactor < 0.01f && targetRainFactor == 0f) {
                rainSound.stop(rainId);
                rainId = -1;
            }
        }

        // 4. Atualização do Ambiente de Vento
        currentWindFactor = MathUtils.lerp(currentWindFactor, targetWindFactor * pauseFactor, fadeSpeed);
        if (windSound != null && windId != -1) {
            float effVol = isMuted ? 0f : getEffectiveAmbientVolume() * currentWindFactor;
            windSound.setVolume(windId, effVol);
            if (currentWindFactor < 0.01f && targetWindFactor == 0f) {
                windSound.stop(windId);
                windId = -1;
            }
        }
    }

    /**
     * Inicia a reprodução suave de uma faixa musical com crossfade.
     */
    public void playMusic(String soundName) {
        if (soundName == null) {
            targetMusicFactor = 0f;
            return;
        }

        if (soundName.equals(currentMusicName) && musicId != -1) {
            targetMusicFactor = 1.0f;
            return;
        }

        // Para faixa anterior se existir
        if (currentMusicSound != null && musicId != -1) {
            currentMusicSound.stop(musicId);
            musicId = -1;
        }

        currentMusicName = soundName;
        currentMusicSound = assets.getSound(soundName);
        if (currentMusicSound != null) {
            float startVol = isMuted ? 0f : 0.01f;
            musicId = currentMusicSound.loop(startVol);
            currentMusicFactor = 0.05f;
            targetMusicFactor = 1.0f;
        }
    }

    /**
     * Toca a trilha do Menu Principal com brisa de vento suave.
     */
    public void playMenuMusic() {
        isPaused = false;
        setChaseActive(false);
        playMusic("music_menu");
        startWindLoop(0.35f);
        stopRainLoop();
    }

    /**
     * Inicia a paisagem sonora e musical específica do estágio atual (PROMPTS.md).
     */
    public void playStageAudio(int stageNumber) {
        isPaused = false;
        setChaseActive(false);

        switch (stageNumber) {
            case 1:
                // THE AWAKENING: quase silêncio, notas esparsas e isolamento
                playMusic("music_stage1");
                startRainLoop(0.35f);
                startWindLoop(0.40f);
                break;
            case 2:
                // THE FORGOTTEN: vilarejo abandonado, harmonia instável
                playMusic("music_stage2");
                startRainLoop(0.30f);
                startWindLoop(0.55f);
                break;
            case 3:
                // THE HOLLOW: coração da floresta e santuário ritualístico
                playMusic("music_stage3");
                startRainLoop(0.18f);
                startWindLoop(0.30f);
                break;
            default:
                playMusic("music_stage1");
                startRainLoop(0.30f);
                startWindLoop(0.35f);
                break;
        }
    }

    /**
     * Alterna a camada de perseguição quando o Watcher entra em HUNTING.
     */
    public void setChaseActive(boolean active) {
        if (this.isChaseActive == active) return;
        this.isChaseActive = active;

        if (active) {
            targetChaseFactor = 1.0f;
            if (chaseId == -1) {
                chaseSound = assets.getSound("music_chase");
                if (chaseSound != null) {
                    chaseId = chaseSound.loop(isMuted ? 0f : 0.01f);
                    currentChaseFactor = 0.05f;
                }
            }
        } else {
            targetChaseFactor = 0f;
        }
    }

    /**
     * Inicia loop suave de chuva na intensidade especificada.
     */
    public void startRainLoop(float intensity) {
        targetRainFactor = Math.max(0f, Math.min(1f, intensity));
        if (rainId == -1) {
            rainSound = assets.getSound("rain_loop");
            if (rainSound != null) {
                rainId = rainSound.loop(isMuted ? 0f : 0.01f);
                currentRainFactor = 0.05f;
            }
        }
    }

    public void stopRainLoop() {
        targetRainFactor = 0f;
    }

    /**
     * Inicia loop suave de vento florestal.
     */
    public void startWindLoop(float intensity) {
        targetWindFactor = Math.max(0f, Math.min(1f, intensity));
        if (windId == -1) {
            windSound = assets.getSound("wind_loop");
            if (windSound != null) {
                windId = windSound.loop(isMuted ? 0f : 0.01f);
                currentWindFactor = 0.05f;
            }
        }
    }

    public void stopWindLoop() {
        targetWindFactor = 0f;
    }

    /**
     * Mantido para compatibilidade com telas existentes que chamam startAmbientLoops.
     */
    public void startAmbientLoops() {
        if (currentMusicName == null) {
            playMenuMusic();
        }
    }

    /**
     * Para todos os loops imediatamente (ex: transição de tela).
     */
    public void stopAmbientLoops() {
        stopAllLoops();
    }

    public void stopAllLoops() {
        targetMusicFactor = 0f;
        targetChaseFactor = 0f;
        targetRainFactor = 0f;
        targetWindFactor = 0f;

        if (currentMusicSound != null && musicId != -1) {
            currentMusicSound.stop(musicId);
            musicId = -1;
            currentMusicSound = null;
            currentMusicName = null;
        }
        if (chaseSound != null && chaseId != -1) {
            chaseSound.stop(chaseId);
            chaseId = -1;
        }
        if (rainSound != null && rainId != -1) {
            rainSound.stop(rainId);
            rainId = -1;
        }
        if (windSound != null && windId != -1) {
            windSound.stop(windId);
            windId = -1;
        }
        heartbeatTimer = 0f;
        isChaseActive = false;
    }

    /**
     * Pausa de gameplay: atenua sons sem cortar abruptamente.
     */
    public void pauseGameplayAudio() {
        isPaused = true;
    }

    public void resumeGameplayAudio() {
        isPaused = false;
    }

    /**
     * Toca transição de derrota (Game Over).
     */
    public void playGameOverAudio() {
        stopAllLoops();
        playSound("music_gameover", 1.0f);
    }

    /**
     * Toca transição de vitória.
     */
    public void playVictoryAudio() {
        stopAllLoops();
        playSound("music_victory", 1.0f);
    }

    /**
     * Reprodução de passos com controle anti-acúmulo por delta time (AGENTS.md § 7).
     * Garante intervalo mínimo de 0.42s e leve variação de pitch orgânica.
     */
    public void updateMovement(float delta, boolean isMoving) {
        if (!isMoving || isPaused || isMuted) {
            return;
        }

        if (footstepTimer <= 0f) {
            footstepTimer = FOOTSTEP_INTERVAL;
            float pitch = 0.94f + MathUtils.random(0f, 0.12f);
            float vol = getEffectiveSfxVolume() * 0.75f;
            Sound step = assets.getSound("footstep");
            if (step != null) {
                step.play(vol, pitch, 0f);
            }
        }
    }

    /**
     * Retrocompatibilidade para chamadas diretas de playFootstep.
     */
    public void playFootstep() {
        if (footstepTimer <= 0f && !isPaused && !isMuted) {
            footstepTimer = FOOTSTEP_INTERVAL;
            float pitch = 0.94f + MathUtils.random(0f, 0.12f);
            float vol = getEffectiveSfxVolume() * 0.75f;
            Sound step = assets.getSound("footstep");
            if (step != null) {
                step.play(vol, pitch, 0f);
            }
        }
    }

    /**
     * Reprodução genérica de efeitos sonoros com headroom seguro.
     */
    public void playSound(String name) {
        playSound(name, 1.0f);
    }

    public void playSound(String name, float pitch) {
        if (isMuted || name == null) return;
        Sound s = assets.getSound(name);
        if (s != null) {
            s.play(getEffectiveSfxVolume(), pitch, 0f);
        }
    }

    /**
     * Batimentos cardíacos reativos à proximidade do Watcher:
     * Acelera o ritmo e aumenta a presença conforme a criatura se aproxima.
     */
    public void updateWatcherProximity(float delta, float distance, float maxDist) {
        if (isMuted || isPaused) return;

        if (distance < maxDist) {
            float proximityFactor = 1.0f - Math.min(1.0f, distance / maxDist);
            // Ritmo varia suavemente entre 1.4s (distante) e 0.42s (iminente)
            float interval = Math.max(0.42f, 1.4f - proximityFactor * 0.98f);
            heartbeatTimer += delta;
            if (heartbeatTimer >= interval) {
                heartbeatTimer = 0f;
                float vol = Math.min(1.0f, (0.25f + proximityFactor * 0.70f) * getEffectiveSfxVolume());
                Sound hb = assets.getSound("heartbeat");
                if (hb != null) {
                    hb.play(vol, 0.92f + proximityFactor * 0.25f, 0f);
                }
            }
        } else {
            heartbeatTimer = 0f;
        }
    }

    // Controles de Volume e Mute
    public float getMasterVolume() { return masterVolume; }
    public void setMasterVolume(float masterVolume) {
        this.masterVolume = MathUtils.clamp(masterVolume, 0f, 1f);
    }

    public float getMusicVolume() { return musicVolume; }
    public void setMusicVolume(float musicVolume) {
        this.musicVolume = MathUtils.clamp(musicVolume, 0f, 1f);
    }

    public float getAmbientVolume() { return ambientVolume; }
    public void setAmbientVolume(float ambientVolume) {
        this.ambientVolume = MathUtils.clamp(ambientVolume, 0f, 1f);
    }

    public float getSfxVolume() { return sfxVolume; }
    public void setSfxVolume(float sfxVolume) {
        this.sfxVolume = MathUtils.clamp(sfxVolume, 0f, 1f);
    }

    public boolean isMuted() { return isMuted; }
    public void setMuted(boolean muted) {
        this.isMuted = muted;
    }
    public void toggleMute() {
        this.isMuted = !this.isMuted;
    }

    public float getEffectiveMusicVolume() { return isMuted ? 0f : masterVolume * musicVolume; }
    public float getEffectiveAmbientVolume() { return isMuted ? 0f : masterVolume * ambientVolume; }
    public float getEffectiveSfxVolume() { return isMuted ? 0f : masterVolume * sfxVolume; }

    public boolean isChaseActive() { return isChaseActive; }
    public boolean isPaused() { return isPaused; }
}
