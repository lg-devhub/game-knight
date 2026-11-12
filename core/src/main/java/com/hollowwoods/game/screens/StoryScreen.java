package com.hollowwoods.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.hollowwoods.game.MainGame;
import com.hollowwoods.game.utils.Constants;
import com.hollowwoods.game.utils.GameAssets;

/**
 * Tela de prólogo narrativo com revelação progressiva de texto sobre fundo atmosférico.
 * Estilo visual: floresta escura ao fundo com neblina e vinheta pesada,
 * texto de narrativa em branco/âmbar surgindo linha a linha.
 */
public class StoryScreen extends BaseScreen {

    private final String[] storyLines = {
            "Madrugada chuvosa. Elias desperta atordoado sobre a lama fria.",
            "Sem memoria de como chegou ali, o ar e pesado e a escuridao parece viva.",
            "Ao seu lado, um radio antigo chia em meio a estatica...",
            "E atraves do chiado, ecoa a voz de sua irma desaparecida:",
            "",
            "\"Elias... por favor, me escute...\"",
            "\"Essa floresta nao e real. Os caminhos mudam... as arvores nos cercam.\"",
            "\"Existe algo aqui... O OBSERVADOR. Ele so se move quando voce nao olha.\"",
            "\"Mantenha o feixe da lanterna fixo sobre ele! Encontre as pistas e fuja!\"",
            "",
            "A transmissao se interrompe em um estalo seco.",
            "Elias acende sua lanterna. A sobrevivencia comeca agora."
    };

    private float timer = 0f;
    private float globalTimer = 0f;
    private int visibleLines = 1;
    private float fogScroll = 0f;
    private final GlyphLayout layout = new GlyphLayout();

    public StoryScreen(MainGame game) {
        super(game);
    }

    @Override
    public void show() {
        game.getAudioManager().playSound("radio_static");
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        timer += delta;
        globalTimer += delta;
        fogScroll += delta * 12f;

        if (timer > 0.70f && visibleLines < storyLines.length) {
            timer = 0f;
            visibleLines++;
        }

        GameAssets assets = game.getAssets();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        // Fundo atmosférico com floresta escura
        batch.setColor(0.25f, 0.25f, 0.30f, 1f);
        batch.draw(assets.titleBg, 0, 0, Constants.VIRTUAL_WIDTH, Constants.VIRTUAL_HEIGHT);
        batch.setColor(Color.WHITE);

        // Neblina suave animada
        batch.setColor(0.10f, 0.14f, 0.18f, 0.15f);
        batch.draw(
                assets.fogTexture,
                0, 0, Constants.VIRTUAL_WIDTH, Constants.VIRTUAL_HEIGHT,
                (int) fogScroll, 0, 512, 512, false, false
        );
        batch.setColor(Color.WHITE);

        // Título do capítulo
        assets.titleFont.setColor(new Color(0.95f, 0.22f, 0.22f, 1f));
        String title = "O DESPERTAR NA ESCURIDAO";
        layout.setText(assets.titleFont, title);
        assets.titleFont.draw(batch, title, (Constants.VIRTUAL_WIDTH - layout.width) / 2f, Constants.VIRTUAL_HEIGHT - 80);

        // Linhas do prólogo com fade-in individual
        float startY = Constants.VIRTUAL_HEIGHT - 165;
        for (int i = 0; i < visibleLines; i++) {
            String line = storyLines[i];
            if (line.isEmpty()) continue;

            // Falas da irmã em âmbar dourado, narração em branco suave
            if (line.startsWith("\"")) {
                assets.bodyFont.setColor(new Color(1.0f, 0.85f, 0.42f, 0.95f));
            } else {
                assets.bodyFont.setColor(new Color(0.88f, 0.90f, 0.92f, 0.90f));
            }
            layout.setText(assets.bodyFont, line);
            assets.bodyFont.draw(batch, line, (Constants.VIRTUAL_WIDTH - layout.width) / 2f, startY - (i * 34));
        }

        // Prompt pulsante
        float pulse = 0.70f + 0.30f * (float) Math.sin(globalTimer * 5f);
        assets.smallFont.setColor(new Color(0.95f, 0.35f, 0.35f, pulse));
        String prompt;
        if (visibleLines < storyLines.length) {
            prompt = "Pressione [ESPACO] para Revelar Tudo";
        } else {
            prompt = "Pressione [ESPACO] ou [ENTER] para Entrar na Floresta";
        }
        layout.setText(assets.smallFont, prompt);
        assets.smallFont.draw(batch, prompt, (Constants.VIRTUAL_WIDTH - layout.width) / 2f, 60);

        // Vinheta pesada (atmosfera cinematográfica)
        batch.draw(assets.vignette, 0, 0, Constants.VIRTUAL_WIDTH, Constants.VIRTUAL_HEIGHT);
        batch.end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) ||
                Gdx.input.isKeyJustPressed(Input.Keys.ENTER) ||
                Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            if (visibleLines < storyLines.length) {
                visibleLines = storyLines.length;
            } else {
                game.getAudioManager().playSound("button_click");
                game.proceedFromStory();
            }
        }
    }
}
