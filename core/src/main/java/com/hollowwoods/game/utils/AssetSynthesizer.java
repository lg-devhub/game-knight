package com.hollowwoods.game.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Random;

/**
 * Utilitário de sintetização e geração local de assets para The Hollow Woods.
 * Gera arte 2D gótica de alta fidelidade, texturas e áudios em conformidade com
 * as diretrizes de AGENTS.md e a imagem de referência fornecida pelo usuário.
 */
public class AssetSynthesizer {

    private static final String IMAGES_DIR = "assets/images";
    private static final String AUDIO_DIR = "assets/audio";
    private static final String FONTS_DIR = "assets/fonts";

    public static void main(String[] args) {
        new File(IMAGES_DIR).mkdirs();
        new File(AUDIO_DIR).mkdirs();
        new File(FONTS_DIR).mkdirs();
        try {
            generateAudio();
            generateImages();
            System.out.println("Assets sintetizados com sucesso para o Prompt 2!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =========================================================================
    // 1. GERAÇÃO DE IMAGENS E TEXTURAS ARTÍSTICAS
    // =========================================================================

    public static void generateImages() throws IOException {
        System.out.println("Gerando imagens e texturas de arte 2D...");
        generateTitleBg(IMAGES_DIR + "/title_bg.png");
        generateTitleLogo(IMAGES_DIR + "/title_logo.png");
        generateMenuBanner(IMAGES_DIR + "/menu_banner.png");
        generatePlayerSheet(IMAGES_DIR + "/player.png");
        generateWatcherSheet(IMAGES_DIR + "/watcher.png");
        generateTiles(IMAGES_DIR + "/tiles.png");
        generateProps(IMAGES_DIR + "/props.png");
        generateTrees(IMAGES_DIR + "/trees.png");
        generateItems(IMAGES_DIR + "/items.png");
        generateUI(IMAGES_DIR + "/ui.png");
        generateLightCone(IMAGES_DIR + "/light_cone.png");
        generateLightHalo(IMAGES_DIR + "/light_halo.png");
        generateFog(IMAGES_DIR + "/fog.png");
        generateVignette(IMAGES_DIR + "/vignette.png");
        System.out.println("Todas as imagens foram geradas com sucesso.");
    }

    /**
     * Fundo do Menu Principal: Floresta de pinheiros góticos na penumbra,
     * lua de sangue avermelhada com nuvens e a imponente torre/mansão decrépita.
     */
    private static void generateTitleBg(String path) throws IOException {
        int w = 1280, h = 720;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Céu noturno escuro com gradiente azul petróleo / carvão
        GradientPaint skyGrad = new GradientPaint(
                0, 0, new Color(12, 14, 20),
                0, h, new Color(24, 20, 26)
        );
        g.setPaint(skyGrad);
        g.fillRect(0, 0, w, h);

        // Nuvens escuras de tempestade ao fundo
        Random rnd = new Random(1337);
        g.setColor(new Color(28, 24, 34, 110));
        for (int i = 0; i < 40; i++) {
            int cx = rnd.nextInt(w);
            int cy = rnd.nextInt(h / 2);
            int rw = 200 + rnd.nextInt(350);
            int rh = 50 + rnd.nextInt(90);
            g.fillOval(cx - rw / 2, cy - rh / 2, rw, rh);
        }

        // Lua de Sangue (Blood Moon) no quadrante direito superior (conforme referência)
        int moonX = 1010;
        int moonY = 170;
        int moonR = 85;

        // Brilho exterior carmesim da lua
        RadialGradientPaint moonHalo = new RadialGradientPaint(
                new Point2D.Float(moonX, moonY), moonR * 2.2f,
                new float[]{0.0f, 0.45f, 1.0f},
                new Color[]{
                        new Color(220, 40, 40, 160),
                        new Color(160, 25, 25, 80),
                        new Color(100, 15, 20, 0)
                }
        );
        g.setPaint(moonHalo);
        g.fillOval(moonX - (int)(moonR * 2.2f), moonY - (int)(moonR * 2.2f), (int)(moonR * 4.4f), (int)(moonR * 4.4f));

        // Disco da Lua com textura de crateras e sombras
        g.setColor(new Color(195, 35, 35));
        g.fillOval(moonX - moonR, moonY - moonR, moonR * 2, moonR * 2);

        // Manchas lunares mais escuras
        g.setColor(new Color(130, 20, 25, 180));
        g.fillOval(moonX - 45, moonY - 30, 50, 45);
        g.fillOval(moonX + 10, moonY - 15, 40, 55);
        g.fillOval(moonX - 25, moonY + 20, 60, 35);
        g.fillOval(moonX - 10, moonY - 55, 35, 30);

        // Nuvens finas passando na frente da lua
        g.setColor(new Color(15, 12, 18, 140));
        g.fillRoundRect(moonX - 120, moonY - 20, 220, 24, 12, 12);
        g.fillRoundRect(moonX - 60, moonY + 25, 180, 18, 10, 10);

        // Silhueta da Mansão / Torre Decrépita à direita (conforme referência)
        int tx = 950, ty = h - 180;
        g.setColor(new Color(10, 8, 12));
        // Base da estrutura de madeira
        g.fillRect(tx - 130, ty - 220, 260, 240);
        // Andares superiores em vigas
        g.fillRect(tx - 95, ty - 340, 190, 130);
        g.fillRect(tx - 65, ty - 430, 130, 100);
        // Telhados pontiagudos góticos
        Polygon roof1 = new Polygon(
                new int[]{tx - 75, tx, tx + 75},
                new int[]{ty - 430, ty - 510, ty - 430}, 3
        );
        g.fillPolygon(roof1);
        // Vigas, colunas quebradas e tábuas
        g.setStroke(new BasicStroke(4));
        g.drawLine(tx - 110, ty - 340, tx + 110, ty - 340);
        g.drawLine(tx - 80, ty - 430, tx + 80, ty - 430);
        g.drawLine(tx - 125, ty - 220, tx + 125, ty - 220);

        // Silhuetas de Pinheiros retorcidos em camadas (Parallax estático)
        // Camada 1: Ao longe (cinza azulado escuro)
        g.setColor(new Color(18, 22, 28));
        drawPineForestLayer(g, w, h, 60, 320, 45, 101);

        // Camada 2: Médio alcance
        g.setColor(new Color(12, 15, 20));
        drawPineForestLayer(g, w, h, 45, 380, 65, 202);

        // Camada 3: Primeiro plano denso
        g.setColor(new Color(7, 9, 12));
        drawPineForestLayer(g, w, h, 30, 480, 90, 303);

        // Solo e silhueta inferior da colina
        g.setColor(new Color(6, 7, 10));
        g.fillRect(0, h - 80, w, 80);

        // Névoa rasteira na base da floresta
        GradientPaint mistGrad = new GradientPaint(
                0, h - 140, new Color(20, 26, 32, 0),
                0, h, new Color(30, 38, 48, 120)
        );
        g.setPaint(mistGrad);
        g.fillRect(0, h - 140, w, 140);

        g.dispose();
        ImageIO.write(img, "png", new File(path));
    }

    private static void drawPineForestLayer(Graphics2D g, int w, int h, int spacing, int minHeight, int varHeight, int seed) {
        Random r = new Random(seed);
        for (int x = -40; x < w + 50; x += spacing + r.nextInt(20)) {
            int treeH = minHeight + r.nextInt(varHeight);
            int baseY = h - 60;
            int topY = baseY - treeH;
            int trunkW = Math.max(6, treeH / 35);

            // Tronco
            g.fillRect(x - trunkW / 2, topY + treeH / 3, trunkW, (treeH * 2) / 3);

            // Ramos triangulares descendentes
            int tiers = 5 + treeH / 70;
            for (int t = 0; t < tiers; t++) {
                float tierRatio = (float) t / tiers;
                int tierY = topY + (int) (treeH * 0.75f * tierRatio);
                int tierW = (int) (treeH * 0.38f * (0.35f + 0.65f * tierRatio));
                int tierH = treeH / tiers + 12;

                Polygon branch = new Polygon(
                        new int[]{x, x - tierW / 2, x + tierW / 2},
                        new int[]{tierY, tierY + tierH, tierY + tierH}, 3
                );
                g.fillPolygon(branch);
            }
        }
    }

    /**
     * Logo do Título: "THE HOLLOW WOODS" em tipografia gótica entalhada
     * com pontas afiadas, rachaduras e gotas de sangue escorrendo sob as letras L e W.
     */
    private static void generateTitleLogo(String path) throws IOException {
        int w = 800, h = 240;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Subtítulo THE
        g.setFont(new Font("Serif", Font.BOLD, 36));
        FontMetrics fmThe = g.getFontMetrics();
        int theX = (w - fmThe.stringWidth("THE")) / 2;
        int theY = 56;

        // Sombra suave do THE
        g.setColor(new Color(5, 5, 5, 230));
        g.drawString("THE", theX + 3, theY + 3);
        g.setColor(new Color(190, 195, 205));
        g.drawString("THE", theX, theY);

        // Título HOLLOW WOODS em duas linhas ou linha única imponente
        g.setFont(new Font("Serif", Font.BOLD, 68));
        FontMetrics fm = g.getFontMetrics();
        String line1 = "HOLLOW";
        String line2 = "WOODS";

        int x1 = (w - fm.stringWidth(line1)) / 2;
        int y1 = 126;
        int x2 = (w - fm.stringWidth(line2)) / 2;
        int y2 = 196;

        // 1. Sombra projetada profunda
        g.setColor(new Color(5, 5, 8, 240));
        for (int ox = -4; ox <= 4; ox++) {
            for (int oy = -4; oy <= 4; oy++) {
                if (ox != 0 || oy != 0) {
                    g.drawString(line1, x1 + ox, y1 + oy);
                    g.drawString(line2, x2 + ox, y2 + oy);
                }
            }
        }

        // 2. Brilho carmesim escuro de fundo
        g.setColor(new Color(160, 20, 25, 180));
        g.drawString(line1, x1 + 2, y1 + 3);
        g.drawString(line2, x2 + 2, y2 + 3);

        // 3. Gradiente metálico/ósseo esbranquiçado
        GradientPaint stoneGrad = new GradientPaint(
                0, y1 - 60, new Color(245, 245, 250),
                0, y2, new Color(155, 160, 175)
        );
        g.setPaint(stoneGrad);
        g.drawString(line1, x1, y1);
        g.drawString(line2, x2, y2);

        // 4. Detalhes de gotas e estrias de sangue escorrendo (conforme referência)
        g.setColor(new Color(185, 22, 26));
        // Gotas sob HOLLOW
        int dripY1 = y1 + 5;
        drawBloodDrip(g, x1 + 72, dripY1, 24);
        drawBloodDrip(g, x1 + 116, dripY1, 16);
        drawBloodDrip(g, x1 + 210, dripY1, 30);

        // Gotas sob WOODS
        int dripY2 = y2 + 6;
        drawBloodDrip(g, x2 + 45, dripY2, 32);
        drawBloodDrip(g, x2 + 130, dripY2, 40);
        drawBloodDrip(g, x2 + 220, dripY2, 28);

        // Efeito de sangue fresco coagulado no topo das gotas
        g.setColor(new Color(120, 12, 16));
        g.fillOval(x2 + 43, dripY2 + 28, 6, 6);
        g.fillOval(x2 + 128, dripY2 + 36, 7, 7);
        g.fillOval(x2 + 218, dripY2 + 24, 6, 6);

        g.dispose();
        ImageIO.write(img, "png", new File(path));
    }

    private static void drawBloodDrip(Graphics2D g, int x, int startY, int length) {
        g.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawLine(x, startY, x, startY + length - 4);
        g.fillOval(x - 3, startY + length - 4, 7, 7);
    }

    /**
     * Banner estilizado em vermelho-sangue texturizado para botões selecionados no menu
     * (conforme o botão "NEW GAME" na imagem de referência).
     */
    private static void generateMenuBanner(String path) throws IOException {
        int w = 320, h = 60;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Fundo vermelho carmesim profundo com textura gótica e pontas recortadas
        GradientPaint bannerGrad = new GradientPaint(
                0, 0, new Color(175, 25, 28),
                0, h, new Color(105, 14, 18)
        );
        g.setPaint(bannerGrad);

        // Polígono entalhado com pontas de fita nas laterais
        Polygon bannerPoly = new Polygon(
                new int[]{0, 16, 0, w, w - 16, w},
                new int[]{0, h / 2, h, h, h / 2, 0}, 6
        );
        g.fillPolygon(bannerPoly);

        // Borda dourada/bronze envelhecido
        g.setColor(new Color(230, 60, 60, 180));
        g.setStroke(new BasicStroke(1.5f));
        g.drawPolygon(bannerPoly);

        // Manchas de sujeira e desgaste
        Random rnd = new Random(456);
        g.setColor(new Color(60, 8, 10, 80));
        for (int i = 0; i < 35; i++) {
            int bx = rnd.nextInt(w);
            int by = rnd.nextInt(h);
            g.fillRect(bx, by, rnd.nextInt(6) + 2, rnd.nextInt(4) + 1);
        }

        g.dispose();
        ImageIO.write(img, "png", new File(path));
    }

    /**
     * Spritesheet de Elias (Player): top-down 3/4 com sobretudo escuro,
     * mochila detalhada nas costas, cabelo escuro e lanterna em punho.
     */
    private static void generatePlayerSheet(String path) throws IOException {
        int frameSize = 64;
        int cols = 4; // 0=Idle, 1=Walk1, 2=Walk2, 3=Action
        int rows = 4; // 0=Baixo, 1=Esquerda, 2=Direita, 3=Cima
        BufferedImage img = new BufferedImage(frameSize * cols, frameSize * rows, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color coatColor = new Color(52, 48, 42);       // Sobretudo marrom acinzentado escuro
        Color coatShade = new Color(34, 30, 26);
        Color backpackColor = new Color(74, 62, 46);   // Mochila de lona
        Color backpackStrap = new Color(30, 24, 18);
        Color skinColor = new Color(205, 175, 150);     // Pele
        Color hairColor = new Color(25, 20, 18);        // Cabelo escuro
        Color pantsColor = new Color(32, 36, 42);       // Calça grafite
        Color bootsColor = new Color(18, 16, 15);
        Color flashlightBody = new Color(40, 42, 45);
        Color flashlightLens = new Color(255, 240, 180);

        for (int dir = 0; dir < rows; dir++) {
            for (int f = 0; f < cols; f++) {
                int ox = f * frameSize;
                int oy = dir * frameSize;
                int cx = ox + frameSize / 2;
                int cy = oy + frameSize / 2;

                float legOffset = (f == 1) ? 3.5f : (f == 2 ? -3.5f : 0f);

                // 1. Sombra suave sob o personagem no solo
                g.setColor(new Color(10, 12, 15, 130));
                g.fillOval(cx - 16, cy + 14, 32, 12);

                if (dir == 0) { // OLHANDO PARA BAIXO
                    // Pernas / Botas
                    g.setColor(pantsColor);
                    g.fillRect(cx - 8, cy + 8, 6, 12);
                    g.fillRect(cx + 2, cy + 8, 6, 12);
                    g.setColor(bootsColor);
                    g.fillRect(cx - 9, (int) (cy + 18 + legOffset), 7, 6);
                    g.fillRect(cx + 2, (int) (cy + 18 - legOffset), 7, 6);

                    // Corpo e sobretudo
                    g.setColor(coatColor);
                    g.fillRoundRect(cx - 14, cy - 8, 28, 22, 8, 8);
                    g.setColor(coatShade);
                    g.drawLine(cx, cy - 6, cx, cy + 12);

                    // Ombros e mochila (visível atrás da cabeça)
                    g.setColor(backpackColor);
                    g.fillRoundRect(cx - 12, cy - 14, 24, 10, 4, 4);

                    // Cabeça e cabelo
                    g.setColor(hairColor);
                    g.fillOval(cx - 9, cy - 24, 18, 17);
                    g.setColor(skinColor);
                    g.fillOval(cx - 7, cy - 18, 14, 10);

                    // Braços e Lanterna voltada para frente
                    g.setColor(coatColor);
                    g.fillOval(cx - 16, cy - 4, 8, 14); // Braço esquerdo
                    g.fillOval(cx + 8, cy - 4, 8, 14);  // Braço direito segurando lanterna
                    // Lanterna
                    g.setColor(flashlightBody);
                    g.fillRect(cx + 10, cy + 6, 6, 10);
                    g.setColor(flashlightLens);
                    g.fillRect(cx + 10, cy + 15, 6, 3);

                } else if (dir == 1) { // OLHANDO PARA ESQUERDA
                    // Pernas / Botas
                    g.setColor(pantsColor);
                    g.fillRect(cx - 5, cy + 8, 10, 12);
                    g.setColor(bootsColor);
                    g.fillRect((int) (cx - 7 + legOffset), cy + 18, 8, 6);
                    g.fillRect((int) (cx - 2 - legOffset), cy + 18, 8, 6);

                    // Mochila nas costas (lado direito nesta visão)
                    g.setColor(backpackColor);
                    g.fillRoundRect(cx + 1, cy - 10, 14, 18, 4, 4);
                    g.setColor(backpackStrap);
                    g.drawLine(cx + 2, cy - 8, cx - 4, cy + 6);

                    // Corpo e sobretudo
                    g.setColor(coatColor);
                    g.fillRoundRect(cx - 12, cy - 8, 20, 22, 6, 6);

                    // Cabeça e perfil do rosto
                    g.setColor(hairColor);
                    g.fillOval(cx - 10, cy - 24, 18, 17);
                    g.setColor(skinColor);
                    g.fillRect(cx - 11, cy - 18, 5, 8);

                    // Braço estendido à esquerda com lanterna
                    g.setColor(coatColor);
                    g.fillRoundRect(cx - 16, cy - 2, 12, 8, 4, 4);
                    g.setColor(flashlightBody);
                    g.fillRect(cx - 21, cy - 1, 8, 6);
                    g.setColor(flashlightLens);
                    g.fillRect(cx - 23, cy - 1, 3, 6);

                } else if (dir == 2) { // OLHANDO PARA DIREITA
                    // Pernas / Botas
                    g.setColor(pantsColor);
                    g.fillRect(cx - 5, cy + 8, 10, 12);
                    g.setColor(bootsColor);
                    g.fillRect((int) (cx - 6 - legOffset), cy + 18, 8, 6);
                    g.fillRect((int) (cx - 1 + legOffset), cy + 18, 8, 6);

                    // Mochila nas costas (lado esquerdo nesta visão)
                    g.setColor(backpackColor);
                    g.fillRoundRect(cx - 15, cy - 10, 14, 18, 4, 4);
                    g.setColor(backpackStrap);
                    g.drawLine(cx - 3, cy - 8, cx + 4, cy + 6);

                    // Corpo e sobretudo
                    g.setColor(coatColor);
                    g.fillRoundRect(cx - 8, cy - 8, 20, 22, 6, 6);

                    // Cabeça e perfil do rosto
                    g.setColor(hairColor);
                    g.fillOval(cx - 8, cy - 24, 18, 17);
                    g.setColor(skinColor);
                    g.fillRect(cx + 6, cy - 18, 5, 8);

                    // Braço estendido à direita com lanterna
                    g.setColor(coatColor);
                    g.fillRoundRect(cx + 4, cy - 2, 12, 8, 4, 4);
                    g.setColor(flashlightBody);
                    g.fillRect(cx + 13, cy - 1, 8, 6);
                    g.setColor(flashlightLens);
                    g.fillRect(cx + 20, cy - 1, 3, 6);

                } else { // OLHANDO PARA CIMA (DE COSTAS, MOCHILA DESTACADA COMO NA REFERÊNCIA)
                    // Pernas / Botas
                    g.setColor(pantsColor);
                    g.fillRect(cx - 8, cy + 8, 6, 12);
                    g.fillRect(cx + 2, cy + 8, 6, 12);
                    g.setColor(bootsColor);
                    g.fillRect(cx - 9, (int) (cy + 18 - legOffset), 7, 6);
                    g.fillRect(cx + 2, (int) (cy + 18 + legOffset), 7, 6);

                    // Corpo / Sobretudo pelas costas
                    g.setColor(coatColor);
                    g.fillRoundRect(cx - 14, cy - 8, 28, 22, 8, 8);

                    // Mochila volumosa (elemento chave de silhueta de Elias)
                    g.setColor(backpackColor);
                    g.fillRoundRect(cx - 12, cy - 10, 24, 20, 6, 6);
                    g.setColor(backpackStrap);
                    g.drawRoundRect(cx - 12, cy - 10, 24, 20, 6, 6);
                    g.fillRect(cx - 10, cy - 4, 20, 4); // Bolso da mochila

                    // Cabeça (apenas cabelo escuro visto de costas)
                    g.setColor(hairColor);
                    g.fillOval(cx - 9, cy - 24, 18, 17);

                    // Braço apontando lanterna para frente/cima
                    g.setColor(coatColor);
                    g.fillOval(cx + 8, cy - 14, 7, 14);
                    g.setColor(flashlightBody);
                    g.fillRect(cx + 9, cy - 19, 5, 8);
                    g.setColor(flashlightLens);
                    g.fillRect(cx + 9, cy - 22, 5, 3);
                }
            }
        }

        g.dispose();
        ImageIO.write(img, "png", new File(path));
    }

    /**
     * Spritesheet de The Watcher: silhueta alongada e perturbadora, membros compridos,
     * postura curvada e olhos/crânio ocos com brilho espectral pálido.
     */
    private static void generateWatcherSheet(String path) throws IOException {
        int frameSize = 64;
        int cols = 4; // 0=Idle, 1=Walk1, 2=Walk2, 3=Attack/Hunting
        int rows = 4; // 0=Baixo, 1=Esquerda, 2=Direita, 3=Cima
        BufferedImage img = new BufferedImage(frameSize * cols, frameSize * rows, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color skinPale = new Color(175, 182, 190);      // Pele cadavérica pálida
        Color skinShadow = new Color(95, 105, 115);     // Sombras anatômicas
        Color eyeGlow = new Color(255, 235, 140);       // Olhos profundos / reflexo de luz
        Color auraDark = new Color(15, 12, 18, 160);

        for (int dir = 0; dir < rows; dir++) {
            for (int f = 0; f < cols; f++) {
                int ox = f * frameSize;
                int oy = dir * frameSize;
                int cx = ox + frameSize / 2;
                int cy = oy + frameSize / 2;

                float lurch = (f == 1) ? 3.0f : (f == 2 ? -3.0f : (f == 3 ? 5.0f : 0f));

                // 1. Sombra e miasma sombrio sob a criatura
                g.setColor(auraDark);
                g.fillOval(cx - 18, cy + 18, 36, 14);

                // 2. Pernas esguias e longas
                g.setColor(skinShadow);
                g.setStroke(new BasicStroke(3.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g.drawLine(cx - 7, cy + 6, (int) (cx - 10 + lurch), cy + 24);
                g.drawLine(cx + 7, cy + 6, (int) (cx + 10 - lurch), cy + 24);

                // 3. Tronco alongado e emaciado (costelas visíveis)
                g.setColor(skinPale);
                g.fillOval(cx - 11, cy - 14, 22, 24);
                g.setColor(skinShadow);
                g.drawLine(cx - 8, cy - 6, cx + 8, cy - 6);
                g.drawLine(cx - 7, cy - 1, cx + 7, cy - 1);
                g.drawLine(cx - 6, cy + 4, cx + 6, cy + 4);

                // 4. Braços desproporcionalmente longos (marca registrada de terror gótico)
                g.setStroke(new BasicStroke(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                if (f == 3) { // Postura de ataque/caça (braços abertos agressivos)
                    g.drawLine(cx - 10, cy - 10, cx - 22, cy + 6);
                    g.drawLine(cx + 10, cy - 10, cx + 22, cy + 6);
                } else {
                    g.drawLine(cx - 10, cy - 10, (int) (cx - 14 - lurch * 0.5f), cy + 16);
                    g.drawLine(cx + 10, cy - 10, (int) (cx + 14 + lurch * 0.5f), cy + 16);
                }

                // 5. Cabeça cadavérica e olhos inquietantes
                g.setColor(skinPale);
                g.fillOval(cx - 8, cy - 27, 16, 17);

                if (dir != 3) { // Se não estiver de costas, desenha olhos penetrantes
                    g.setColor(new Color(25, 20, 25)); // Órbitas oculares fundas
                    g.fillOval(cx - 6, cy - 22, 4, 5);
                    g.fillOval(cx + 2, cy - 22, 4, 5);

                    g.setColor(eyeGlow); // Fagulha nos olhos
                    g.fillRect(cx - 5, cy - 21, 2, 2);
                    g.fillRect(cx + 3, cy - 21, 2, 2);
                }
            }
        }

        g.dispose();
        ImageIO.write(img, "png", new File(path));
    }

    /**
     * Tileset detalhado (64x64 por tile) com contraste balanceado e paleta gótica legível:
     * - Row 0: Forest (Gravel Path, Dirt Trail, Deep Moss, Damp Mud)
     * - Row 1: Village (Cobblestone, Flagstone Paving, Overgrown Ruins, Ash Earth)
     * - Row 2: Shrine / Sanctum (Rune Tile, Corrupted Obsidian, Blood Pavement, Void Floor)
     * - Row 3: Cabin Interior (Dark Pine Planks, Worn Wood, Wool Carpet, Trapdoor)
     */
    private static void generateTiles(String path) throws IOException {
        int tileW = 64;
        int cols = 4;
        int rows = 4;
        BufferedImage img = new BufferedImage(tileW * cols, tileW * rows, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();

        // ------------------ ROW 0: THE AWAKENING (FOREST) ------------------
        // Tile 0: Forest Dirt Trail (terra batida escura com textura)
        drawTexturedTile(g, 0, 0, new Color(42, 48, 40), new Color(34, 38, 32), 101);
        // Tile 1: Forest Gravel Path (caminho navegável com cascalho claro e pedrinhas - alta legibilidade!)
        drawGravelPathTile(g, 64, 0, new Color(72, 74, 68), new Color(52, 54, 48), 202);
        // Tile 2: Deep Moss (musgo denso)
        drawTexturedTile(g, 128, 0, new Color(32, 46, 30), new Color(24, 36, 22), 303);
        // Tile 3: Damp Mud / Puddle (terra úmida com reflexo suave)
        drawPuddleTile(g, 192, 0, new Color(36, 42, 44), new Color(28, 34, 36), 404);

        // ------------------ ROW 1: THE FORGOTTEN (VILLAGE) ------------------
        // Tile 0: Cobblestone (paralelepípedos com rejunte visível)
        drawCobblestoneTile(g, 0, 64, new Color(68, 70, 74), new Color(42, 44, 48), 505);
        // Tile 1: Flagstone (lajotas de pedra envelhecidas)
        drawFlagstoneTile(g, 64, 64, new Color(82, 84, 88), new Color(54, 56, 60), 606);
        // Tile 2: Overgrown Cobblestone (pedras com ervas daninhas)
        drawCobblestoneTile(g, 128, 64, new Color(58, 66, 60), new Color(38, 44, 40), 707);
        // Tile 3: Ash & Charcoal Ground
        drawTexturedTile(g, 192, 64, new Color(48, 46, 50), new Color(32, 30, 34), 808);

        // ------------------ ROW 2: THE HOLLOW (SANCTUM) ------------------
        // Tile 0: Corrupted Stone Floor
        drawRuneFloorTile(g, 0, 128, new Color(46, 36, 44), new Color(30, 22, 28), 909);
        // Tile 1: Blood Inscribed Stone
        drawBloodStoneTile(g, 64, 128, new Color(54, 30, 36), new Color(38, 20, 24), 1010);
        // Tile 2: Ancient Eldritch Earth
        drawTexturedTile(g, 128, 128, new Color(36, 24, 32), new Color(24, 16, 22), 1111);
        // Tile 3: Corrupted Black Obsidian
        drawFlagstoneTile(g, 192, 128, new Color(28, 22, 30), new Color(18, 14, 20), 1212);

        // ------------------ ROW 3: CABIN INTERIOR ------------------
        // Tile 0: Wooden Planks (tábuas de madeira de pinho)
        drawWoodPlankTile(g, 0, 192, new Color(86, 62, 44), new Color(60, 42, 30));
        // Tile 1: Worn Aged Wood
        drawWoodPlankTile(g, 64, 192, new Color(70, 52, 38), new Color(48, 34, 24));
        // Tile 2: Vintage Rug (tapete desgastado bordô)
        drawRugTile(g, 128, 192, new Color(125, 42, 46), new Color(85, 26, 30));
        // Tile 3: Cellar Trapdoor (alçapão de ferro e madeira)
        drawTrapdoorTile(g, 192, 192, new Color(58, 44, 32), new Color(38, 28, 20));

        g.dispose();
        ImageIO.write(img, "png", new File(path));
    }

    private static void drawTexturedTile(Graphics2D g, int x, int y, Color base, Color dark, int seed) {
        g.setColor(base);
        g.fillRect(x, y, 64, 64);
        Random r = new Random(seed);
        g.setColor(dark);
        for (int i = 0; i < 90; i++) {
            int px = x + r.nextInt(64);
            int py = y + r.nextInt(64);
            g.fillRect(px, py, r.nextInt(3) + 1, r.nextInt(3) + 1);
        }
        g.setColor(new Color(base.getRed() + 15, base.getGreen() + 15, base.getBlue() + 15));
        for (int i = 0; i < 40; i++) {
            int px = x + r.nextInt(64);
            int py = y + r.nextInt(64);
            g.fillRect(px, py, 1, 1);
        }
    }

    private static void drawGravelPathTile(Graphics2D g, int x, int y, Color base, Color dark, int seed) {
        g.setColor(base);
        g.fillRect(x, y, 64, 64);
        Random r = new Random(seed);
        // Cascalho e pedrinhas
        for (int i = 0; i < 60; i++) {
            int px = x + r.nextInt(60);
            int py = y + r.nextInt(60);
            g.setColor(dark);
            g.fillOval(px, py, r.nextInt(4) + 2, r.nextInt(3) + 2);
            g.setColor(new Color(190, 192, 185)); // Pontos de destaque em luz
            g.fillRect(px + 1, py + 1, 1, 1);
        }
    }

    private static void drawPuddleTile(Graphics2D g, int x, int y, Color base, Color dark, int seed) {
        drawTexturedTile(g, x, y, base, dark, seed);
        // Poça de água com reflexo suave
        g.setColor(new Color(24, 32, 40, 200));
        g.fillOval(x + 10, y + 14, 44, 36);
        g.setColor(new Color(60, 80, 105, 120));
        g.drawOval(x + 12, y + 16, 40, 32);
    }

    private static void drawCobblestoneTile(Graphics2D g, int x, int y, Color base, Color mortar, int seed) {
        g.setColor(mortar);
        g.fillRect(x, y, 64, 64);
        Random r = new Random(seed);
        for (int row = 0; row < 4; row++) {
            int yPos = y + row * 16 + 2;
            int offset = (row % 2 == 0) ? 0 : 8;
            for (int col = 0; col < 5; col++) {
                int xPos = x + col * 16 - offset + 2;
                if (xPos + 12 > x && xPos < x + 64) {
                    g.setColor(new Color(base.getRed() + r.nextInt(16) - 8,
                            base.getGreen() + r.nextInt(16) - 8,
                            base.getBlue() + r.nextInt(16) - 8));
                    g.fillRoundRect(Math.max(x, xPos), yPos, 12, 12, 4, 4);
                }
            }
        }
    }

    private static void drawFlagstoneTile(Graphics2D g, int x, int y, Color base, Color mortar, int seed) {
        g.setColor(mortar);
        g.fillRect(x, y, 64, 64);
        g.setColor(base);
        g.fillRoundRect(x + 3, y + 3, 26, 26, 4, 4);
        g.fillRoundRect(x + 33, y + 3, 28, 26, 4, 4);
        g.fillRoundRect(x + 3, y + 33, 28, 28, 4, 4);
        g.fillRoundRect(x + 35, y + 33, 26, 28, 4, 4);
    }

    private static void drawWoodPlankTile(Graphics2D g, int x, int y, Color plank, Color gap) {
        g.setColor(gap);
        g.fillRect(x, y, 64, 64);
        int plankH = 15;
        for (int i = 0; i < 4; i++) {
            int py = y + i * 16 + 1;
            g.setColor(plank);
            g.fillRect(x, py, 64, plankH);
            // Pregos nas extremidades
            g.setColor(new Color(30, 24, 20));
            g.fillRect(x + 6, py + 7, 2, 2);
            g.fillRect(x + 56, py + 7, 2, 2);
            // Estrias de madeira
            g.setColor(new Color(plank.getRed() - 15, plank.getGreen() - 15, plank.getBlue() - 15));
            g.drawLine(x + 12, py + 4, x + 48, py + 4);
            g.drawLine(x + 18, py + 11, x + 52, py + 11);
        }
    }

    private static void drawRugTile(Graphics2D g, int x, int y, Color base, Color border) {
        drawWoodPlankTile(g, x, y, new Color(70, 52, 38), new Color(48, 34, 24));
        g.setColor(base);
        g.fillRect(x + 4, y + 4, 56, 56);
        g.setColor(border);
        g.drawRect(x + 6, y + 6, 51, 51);
        g.drawRect(x + 10, y + 10, 43, 43);
    }

    private static void drawRuneFloorTile(Graphics2D g, int x, int y, Color base, Color dark, int seed) {
        drawTexturedTile(g, x, y, base, dark, seed);
        g.setColor(new Color(185, 45, 55, 140));
        g.setStroke(new BasicStroke(1.5f));
        g.drawOval(x + 14, y + 14, 36, 36);
        g.drawLine(x + 18, y + 32, x + 46, y + 32);
        g.drawLine(x + 32, y + 18, x + 32, y + 46);
    }

    private static void drawBloodStoneTile(Graphics2D g, int x, int y, Color base, Color dark, int seed) {
        drawFlagstoneTile(g, x, y, base, dark, seed);
        g.setColor(new Color(150, 18, 24, 180));
        g.fillOval(x + 18, y + 22, 28, 20);
        g.fillOval(x + 34, y + 12, 18, 16);
    }

    private static void drawTrapdoorTile(Graphics2D g, int x, int y, Color wood, Color metal) {
        drawWoodPlankTile(g, x, y, wood, new Color(25, 20, 15));
        g.setColor(metal);
        g.setStroke(new BasicStroke(3));
        g.drawRect(x + 6, y + 6, 52, 52);
        // Ferragens e argola de ferro
        g.setColor(new Color(160, 165, 170));
        g.drawOval(x + 26, y + 26, 12, 12);
    }

    /**
     * Obstáculos e Props do Mundo:
     * - Parede de toras da cabana com textura de madeira desgastada
     * - Janela iluminada emanando luz âmbar aconchegante
     * - Porta rústica de carvalho com ferragens
     * - Cercas de madeira partidas (como no screenshot)
     * - Muros de pedra, monólitos e o Altar ritualístico
     */
    private static void generateProps(String path) throws IOException {
        int w = 256, h = 256;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 1. Parede de Toras da Cabana (0, 0, 64, 64)
        drawCabinWallProp(g, 0, 0);

        // 2. Janela da Cabana com Luz Âmbar Aconchegante (64, 0, 64, 64)
        drawCabinWindowProp(g, 64, 0);

        // 3. Porta da Cabana em Carvalho Maciço (128, 0, 64, 64)
        drawCabinDoorProp(g, 128, 0);

        // 4. Muro de Pedra / Ruínas do Vilarejo (192, 0, 64, 64)
        drawStoneWallProp(g, 192, 0);

        // 5. Cerca de Madeira Quebrada (0, 64, 64, 64) - Fiel à referência!
        drawWoodenFenceProp(g, 0, 64);

        // 6. Lápide / Cruz de Madeira (64, 64, 64, 64)
        drawCrossProp(g, 64, 64);

        // 7. Rocha / Bouldering com musgo (128, 64, 64, 64)
        drawMossyBoulderProp(g, 128, 64);

        // 8. Portão de Ferro da Cripta (192, 64, 64, 64)
        drawIronGateProp(g, 192, 64);

        // 9. Monólito Rúnico de Pedra (0, 128, 64, 128)
        drawMonolithProp(g, 0, 128);

        // 10. Altar Ritualístico Ancestral (64, 128, 128, 64)
        drawAncientAltarProp(g, 64, 128);

        g.dispose();
        ImageIO.write(img, "png", new File(path));
    }

    private static void drawCabinWallProp(Graphics2D g, int x, int y) {
        Color woodDark = new Color(54, 38, 26);
        Color woodPlank = new Color(78, 54, 38);
        g.setColor(woodDark);
        g.fillRect(x, y, 64, 64);
        for (int i = 0; i < 4; i++) {
            int py = y + i * 16;
            g.setColor(woodPlank);
            g.fillRoundRect(x + 1, py + 1, 62, 14, 4, 4);
            g.setColor(new Color(woodPlank.getRed() - 14, woodPlank.getGreen() - 14, woodPlank.getBlue() - 14));
            g.drawLine(x + 4, py + 8, x + 58, py + 8);
        }
    }

    private static void drawCabinWindowProp(Graphics2D g, int x, int y) {
        drawCabinWallProp(g, x, y);
        // Moldura escura da janela
        g.setColor(new Color(30, 20, 14));
        g.fillRoundRect(x + 12, y + 12, 40, 40, 6, 6);
        // Vidro iluminado em tom âmbar quente
        g.setColor(new Color(255, 195, 65));
        g.fillRect(x + 16, y + 16, 32, 32);
        // Brilho intenso no centro
        g.setColor(new Color(255, 245, 180));
        g.fillRect(x + 22, y + 22, 20, 20);
        // Cruzeta da janela
        g.setColor(new Color(38, 24, 16));
        g.fillRect(x + 30, y + 16, 4, 32);
        g.fillRect(x + 16, y + 30, 32, 4);
    }

    private static void drawCabinDoorProp(Graphics2D g, int x, int y) {
        drawCabinWallProp(g, x, y);
        g.setColor(new Color(42, 28, 18));
        g.fillRoundRect(x + 10, y + 2, 44, 60, 6, 6);
        g.setColor(new Color(68, 46, 30));
        g.fillRoundRect(x + 14, y + 6, 36, 52, 4, 4);
        // Dobradiças de ferro e maçaneta
        g.setColor(new Color(25, 25, 30));
        g.fillRect(x + 12, y + 14, 14, 4);
        g.fillRect(x + 12, y + 44, 14, 4);
        g.setColor(new Color(210, 175, 70)); // Fechadura dourada
        g.fillOval(x + 42, y + 32, 5, 5);
    }

    private static void drawStoneWallProp(Graphics2D g, int x, int y) {
        g.setColor(new Color(38, 40, 44));
        g.fillRect(x, y, 64, 64);
        g.setColor(new Color(58, 62, 66));
        g.fillRoundRect(x + 2, y + 2, 28, 16, 4, 4);
        g.fillRoundRect(x + 34, y + 2, 28, 16, 4, 4);
        g.fillRoundRect(x + 14, y + 22, 36, 18, 4, 4);
        g.fillRoundRect(x + 2, y + 44, 28, 18, 4, 4);
        g.fillRoundRect(x + 34, y + 44, 28, 18, 4, 4);
    }

    private static void drawWoodenFenceProp(Graphics2D g, int x, int y) {
        // Cerca com mourões e tábuas partidas conforme a captura do jogo
        Color postColor = new Color(68, 50, 34);
        Color railColor = new Color(52, 38, 26);
        // Mourões verticais
        g.setColor(postColor);
        g.fillRect(x + 6, y + 8, 10, 48);
        g.fillRect(x + 48, y + 8, 10, 48);
        // Trilhos horizontais
        g.setColor(railColor);
        g.fillRect(x, y + 18, 64, 8);
        g.fillRect(x, y + 36, 64, 8);
        // Pregos
        g.setColor(new Color(20, 16, 12));
        g.fillRect(x + 10, y + 21, 2, 2);
        g.fillRect(x + 52, y + 21, 2, 2);
        g.fillRect(x + 10, y + 39, 2, 2);
        g.fillRect(x + 52, y + 39, 2, 2);
    }

    private static void drawCrossProp(Graphics2D g, int x, int y) {
        g.setColor(new Color(58, 44, 32));
        g.fillRect(x + 27, y + 8, 10, 48);
        g.fillRect(x + 14, y + 20, 36, 9);
    }

    private static void drawMossyBoulderProp(Graphics2D g, int x, int y) {
        g.setColor(new Color(48, 52, 56));
        g.fillOval(x + 6, y + 10, 52, 44);
        g.setColor(new Color(36, 54, 34)); // Musgo cobrindo o topo
        g.fillOval(x + 12, y + 10, 38, 20);
    }

    private static void drawIronGateProp(Graphics2D g, int x, int y) {
        g.setColor(new Color(35, 38, 42));
        g.fillRect(x + 4, y + 4, 56, 56);
        g.setColor(new Color(15, 16, 20));
        for (int i = 0; i < 5; i++) {
            g.fillRect(x + 12 + i * 9, y + 8, 4, 48);
        }
        g.setColor(new Color(160, 25, 30)); // Correntes mágicas
        g.drawLine(x + 8, y + 32, x + 56, y + 32);
    }

    private static void drawMonolithProp(Graphics2D g, int x, int y) {
        g.setColor(new Color(42, 44, 50));
        g.fillRoundRect(x + 12, y + 8, 40, 112, 8, 8);
        // Runas carmesins entalhadas brilhando
        g.setColor(new Color(210, 40, 45, 190));
        g.drawString("ᛟ", x + 24, y + 42);
        g.drawString("ᚱ", x + 24, y + 68);
        g.drawString("ᛉ", x + 24, y + 94);
    }

    private static void drawAncientAltarProp(Graphics2D g, int x, int y) {
        g.setColor(new Color(50, 52, 58));
        g.fillRoundRect(x + 6, y + 16, 116, 40, 8, 8);
        // Bacia central do altar
        g.setColor(new Color(25, 22, 28));
        g.fillOval(x + 36, y + 22, 56, 28);
        // Fogo / Brilho místico carmesim
        RadialGradientPaint flameGrad = new RadialGradientPaint(
                new Point2D.Float(x + 64, y + 36), 26f,
                new float[]{0.0f, 0.6f, 1.0f},
                new Color[]{
                        new Color(255, 120, 60, 220),
                        new Color(200, 30, 40, 140),
                        new Color(80, 10, 20, 0)
                }
        );
        g.setPaint(flameGrad);
        g.fillOval(x + 44, y + 24, 40, 24);
    }

    /**
     * Árvores de Pinheiro Gótico (128x128):
     * Silhueta de pinheiro escuro com ramificações em camadas de agulhas.
     */
    private static void generateTrees(String path) throws IOException {
        int w = 256, h = 256;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Árvore 1: Pinheiro Grande (0, 0, 128, 128)
        drawDetailedPineTree(g, 0, 0, 128, 128, 77);

        // Árvore 2: Pinheiro Retorcido / Morto (128, 0, 128, 128)
        drawTwistedTree(g, 128, 0, 128, 128, 88);

        // Árvore 3: Pinheiro com musgo (0, 128, 128, 128)
        drawDetailedPineTree(g, 0, 128, 128, 128, 99);

        // Árvore 4: Pinheiro Corrompido / Sangue (128, 128, 128, 128)
        drawCorruptedPine(g, 128, 128, 128, 128, 111);

        g.dispose();
        ImageIO.write(img, "png", new File(path));
    }

    private static void drawDetailedPineTree(Graphics2D g, int x, int y, int w, int h, int seed) {
        int cx = x + w / 2;
        int cy = y + h / 2;

        // Sombra suave no chão
        g.setColor(new Color(8, 12, 14, 150));
        g.fillOval(cx - 38, cy + 28, 76, 26);

        // Tronco
        g.setColor(new Color(42, 30, 22));
        g.fillRect(cx - 6, cy + 10, 12, 32);

        // Copas em camadas de pinheiro (3 camadas)
        Color darkGreen = new Color(18, 28, 20);
        Color midGreen = new Color(28, 44, 30);
        Color lightNeedle = new Color(40, 62, 42);

        // Camada 1: Base ampla
        drawNeedleCluster(g, cx, cy + 16, 52, 32, darkGreen, midGreen, lightNeedle);
        // Camada 2: Média
        drawNeedleCluster(g, cx, cy - 6, 42, 28, darkGreen, midGreen, lightNeedle);
        // Camada 3: Topo afiado
        drawNeedleCluster(g, cx, cy - 28, 30, 26, darkGreen, midGreen, lightNeedle);
    }

    private static void drawNeedleCluster(Graphics2D g, int cx, int cy, int rx, int ry, Color c1, Color c2, Color c3) {
        g.setColor(c1);
        g.fillOval(cx - rx, cy - ry, rx * 2, ry * 2);
        g.setColor(c2);
        g.fillOval(cx - rx + 4, cy - ry + 4, (rx - 4) * 2, (ry - 4) * 2);
        g.setColor(c3);
        for (int i = -rx + 8; i < rx - 8; i += 8) {
            g.drawLine(cx + i, cy - ry + 6, cx + i + 4, cy + ry - 6);
        }
    }

    private static void drawTwistedTree(Graphics2D g, int x, int y, int w, int h, int seed) {
        int cx = x + w / 2;
        int cy = y + h / 2;
        g.setColor(new Color(8, 12, 14, 150));
        g.fillOval(cx - 30, cy + 30, 60, 20);

        g.setColor(new Color(36, 28, 22));
        g.setStroke(new BasicStroke(6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawLine(cx, cy + 40, cx - 4, cy);
        g.setStroke(new BasicStroke(4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawLine(cx - 4, cy, cx - 28, cy - 32);
        g.drawLine(cx - 4, cy, cx + 24, cy - 36);
        g.drawLine(cx - 4, cy, cx + 2, cy - 44);
    }

    private static void drawCorruptedPine(Graphics2D g, int x, int y, int w, int h, int seed) {
        drawDetailedPineTree(g, x, y, w, h, seed);
        // Folhas carmesins / corrompidas
        int cx = x + w / 2;
        int cy = y + h / 2;
        g.setColor(new Color(170, 25, 30, 110));
        g.fillOval(cx - 32, cy - 20, 64, 48);
    }

    /**
     * Itens e Objetos Interativos:
     * - Chave da Cabana (ornada e gótica)
     * - Bateria (célula pesada industrial amarelo/preto)
     * - Kit Médico (estojo de primeiros socorros com cruz vermelha)
     * - Rádio (rádio antigo com antena e botão de sintonia)
     * - Fragmentos de Fotografia 1, 2 e 3
     * - Notas e Documentos
     * - Selos Rituais Arcanos (1, 2 e 3)
     */
    private static void generateItems(String path) throws IOException {
        int w = 256, h = 256;
        int sz = 32;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 1. Chave da Cabana (0, 0, 32, 32)
        g.setColor(new Color(230, 175, 75));
        g.setStroke(new BasicStroke(2.5f));
        g.drawOval(4, 6, 11, 11);
        g.fillRect(14, 10, 14, 3);
        g.fillRect(22, 13, 3, 5);
        g.fillRect(26, 13, 3, 4);

        // 2. Bateria (32, 0, 32, 32)
        g.setColor(new Color(35, 36, 40));
        g.fillRoundRect(38, 4, 20, 24, 4, 4);
        g.setColor(new Color(245, 205, 50)); // Faixa amarela industrial
        g.fillRect(38, 12, 20, 8);
        g.setColor(new Color(190, 195, 200)); // Polo positivo
        g.fillRect(45, 1, 6, 3);

        // 3. Medkit (64, 0, 32, 32)
        g.setColor(new Color(235, 235, 225));
        g.fillRoundRect(68, 6, 24, 20, 4, 4);
        g.setColor(new Color(205, 30, 35)); // Cruz vermelha
        g.fillRect(78, 9, 4, 14);
        g.fillRect(73, 14, 14, 4);

        // 4. Rádio Antigo (96, 0, 32, 32)
        g.setColor(new Color(90, 65, 45));
        g.fillRoundRect(100, 8, 24, 20, 4, 4);
        g.setColor(new Color(30, 25, 22));
        g.fillOval(104, 12, 8, 8); // Alto-falante
        g.setColor(new Color(225, 215, 170));
        g.fillRect(114, 12, 8, 4); // Mostrador
        g.setColor(new Color(180, 185, 190));
        g.drawLine(102, 8, 98, 2); // Antena

        // 5, 6, 7. Fragmentos de Foto (0, 32), (32, 32), (64, 32)
        for (int i = 0; i < 3; i++) {
            int fx = i * 32;
            int fy = 32;
            g.setColor(new Color(225, 215, 195));
            g.fillRect(fx + 6, fy + 4, 20, 24);
            g.setColor(new Color(110, 95, 80)); // Imagem sépia rasgada
            g.fillRect(fx + 8, fy + 6, 16, 18);
            g.setColor(new Color(75, 60, 50));
            g.fillOval(fx + 12, fy + 9, 8, 8); // Silhueta de rosto
        }

        // 8. Nota Manuscrita (96, 32, 32, 32)
        g.setColor(new Color(235, 225, 195));
        g.fillRect(102, 36, 20, 24);
        g.setColor(new Color(55, 45, 38));
        g.drawLine(105, 42, 118, 42);
        g.drawLine(105, 47, 118, 47);
        g.drawLine(105, 52, 114, 52);

        // 9, 10, 11. Selos Rituais (0, 64), (32, 64), (64, 64)
        Color[] sealColors = {
                new Color(220, 35, 40),
                new Color(230, 150, 30),
                new Color(180, 50, 220)
        };
        for (int i = 0; i < 3; i++) {
            int sx = i * 32;
            int sy = 64;
            g.setColor(sealColors[i]);
            g.fillOval(sx + 5, sy + 5, 22, 22);
            g.setColor(new Color(255, 245, 200));
            g.drawOval(sx + 8, sy + 8, 16, 16);
            g.drawLine(sx + 16, sy + 8, sx + 16, sy + 24);
            g.drawLine(sx + 8, sy + 16, sx + 24, sy + 16);
        }

        g.dispose();
        ImageIO.write(img, "png", new File(path));
    }

    /**
     * Elementos e Ícones do HUD:
     * - Cruz médica branca (com borda circular como na referência)
     * - Barra segmentada de vida
     * - Ícone de lanterna/bateria
     * - Barra segmentada de bateria
     * - Caveira vermelha de maldição
     * - Losango de objetivo
     * - Moldura gótica de ferro do minimapa
     */
    private static void generateUI(String path) throws IOException {
        int w = 512, h = 512;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 1. Ícone de Vida: Cruz branca em círculo escuro (conforme captura)
        int hx = 4, hy = 4;
        g.setColor(new Color(20, 22, 26, 240));
        g.fillRoundRect(hx, hy, 28, 28, 6, 6);
        g.setColor(new Color(245, 248, 250));
        g.fillRect(hx + 12, hy + 6, 4, 16);
        g.fillRect(hx + 6, hy + 12, 16, 4);

        // 2. Barra de Vida (Frame e Segmentos)
        g.setColor(new Color(18, 20, 24, 230));
        g.fillRoundRect(40, 8, 140, 20, 4, 4);
        g.setColor(new Color(75, 78, 85));
        g.setStroke(new BasicStroke(1.5f));
        g.drawRoundRect(40, 8, 140, 20, 4, 4);

        // Preenchimento vermelho carmesim
        g.setColor(new Color(205, 32, 38));
        g.fillRect(44, 12, 132, 12);

        // 3. Ícone de Bateria / Lanterna (conforme captura)
        int bx = 4, by = 40;
        g.setColor(new Color(20, 22, 26, 240));
        g.fillRoundRect(bx, by, 28, 28, 6, 6);
        g.setColor(new Color(225, 230, 235));
        g.fillRect(bx + 7, by + 10, 14, 9);
        g.fillRect(bx + 21, by + 12, 2, 5); // Terminal

        // 4. Barra de Bateria
        g.setColor(new Color(18, 20, 24, 230));
        g.fillRoundRect(40, 44, 140, 20, 4, 4);
        g.setColor(new Color(75, 78, 85));
        g.drawRoundRect(40, 44, 140, 20, 4, 4);

        // Preenchimento branco/âmbar segmentado
        g.setColor(new Color(245, 240, 215));
        g.fillRect(44, 48, 132, 12);

        // 5. Losango de Objetivo ◇ (conforme captura)
        int ox = 4, oy = 80;
        g.setColor(new Color(235, 230, 215));
        Polygon diamond = new Polygon(
                new int[]{ox + 12, ox + 22, ox + 12, ox + 2},
                new int[]{oy + 2, oy + 12, oy + 22, oy + 12}, 4
        );
        g.drawPolygon(diamond);

        // 6. Caveira de Maldição ☠ (conforme captura)
        int sx = 40, sy = 80;
        g.setColor(new Color(215, 38, 42));
        g.fillOval(sx + 3, sy + 2, 18, 16);
        g.fillRect(sx + 6, sy + 16, 12, 7);
        g.setColor(new Color(20, 15, 18));
        g.fillOval(sx + 6, sy + 7, 4, 4);
        g.fillOval(sx + 14, sy + 7, 4, 4);
        g.drawLine(sx + 9, sy + 18, sx + 9, sy + 22);
        g.drawLine(sx + 12, sy + 18, sx + 12, sy + 22);
        g.drawLine(sx + 15, sy + 18, sx + 15, sy + 22);

        // 7. Moldura Gótica do Minimapa (conforme captura)
        int mx = 4, my = 120;
        int mw = 128, mh = 128;
        g.setColor(new Color(12, 14, 18, 220));
        g.fillRect(mx, my, mw, mh);
        g.setColor(new Color(65, 68, 75));
        g.setStroke(new BasicStroke(2.5f));
        g.drawRect(mx + 2, my + 2, mw - 4, mh - 4);
        g.setStroke(new BasicStroke(1.0f));
        g.drawRect(mx + 6, my + 6, mw - 12, mh - 12);

        // Rebites nos cantos da moldura
        g.setColor(new Color(145, 150, 160));
        g.fillRect(mx + 4, my + 4, 3, 3);
        g.fillRect(mx + mw - 7, my + 4, 3, 3);
        g.fillRect(mx + 4, my + mh - 7, 3, 3);
        g.fillRect(mx + mw - 7, my + mh - 7, 3, 3);

        g.dispose();
        ImageIO.write(img, "png", new File(path));
    }

    /**
     * Cone de Luz da Lanterna:
     * Feixe suave em ângulo com atenuação gradual (penumbra suave),
     * cor âmbar suave (#fff0b5) e sem estouro de branco puro.
     */
    private static void generateLightCone(String path) throws IOException {
        int size = 512;
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        int ox = 64, oy = 256;
        double maxDist = 420.0;
        double halfAngle = 28.0; // 56 graus no total

        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                double dx = x - ox;
                double dy = y - oy;
                double dist = Math.sqrt(dx * dx + dy * dy);
                if (dx <= 0 || dist > maxDist) continue;

                double angle = Math.toDegrees(Math.atan2(dy, dx));
                if (Math.abs(angle) > halfAngle) continue;

                // Queda de intensidade radial e angular com atenuação suave (Hermite / Smoothstep)
                double distNorm = dist / maxDist;
                double angleNorm = Math.abs(angle) / halfAngle;

                double distFalloff = Math.cos(distNorm * Math.PI * 0.5);
                double angleFalloff = Math.cos(angleNorm * Math.PI * 0.5);

                double intensity = Math.pow(distFalloff * angleFalloff, 1.4);

                int alpha = (int) (intensity * 255);
                if (alpha > 0) {
                    // Tom âmbar quente com centro levemente mais claro
                    int r = 255;
                    int gVal = (int) (225 + (1.0 - distNorm) * 20);
                    int b = (int) (150 + (1.0 - distNorm) * 45);
                    img.setRGB(x, y, (alpha << 24) | (r << 16) | (gVal << 8) | b);
                }
            }
        }
        ImageIO.write(img, "png", new File(path));
    }

    /**
     * Halo circular sutil de 360° em torno do jogador para manter leitura
     * da área ao redor mesmo sem apontar a lanterna (regra do AGENTS.md § 8).
     */
    private static void generateLightHalo(String path) throws IOException {
        int size = 256;
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        int cx = size / 2, cy = size / 2;
        double maxR = size / 2.0;

        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                double dist = Math.sqrt((x - cx) * (x - cx) + (y - cy) * (y - cy));
                if (dist >= maxR) continue;

                double norm = dist / maxR;
                double intensity = Math.pow(Math.cos(norm * Math.PI * 0.5), 1.6);
                int alpha = (int) (intensity * 210);

                if (alpha > 0) {
                    img.setRGB(x, y, (alpha << 24) | (245 << 16) | (230 << 8) | 195);
                }
            }
        }
        ImageIO.write(img, "png", new File(path));
    }

    /**
     * Textura de Neblina Suave Volumétrica com Repetição Perfeita (Seamless).
     */
    private static void generateFog(String path) throws IOException {
        int size = 512;
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                double nx = (double) x / size;
                double ny = (double) y / size;

                // Ondas senoidais combinadas para repetição suave contínua
                double w1 = Math.sin(nx * Math.PI * 2) * Math.cos(ny * Math.PI * 2);
                double w2 = Math.sin(nx * Math.PI * 4 + 1.2) * Math.sin(ny * Math.PI * 4 + 0.8) * 0.5;
                double w3 = Math.cos(nx * Math.PI * 6 - 0.5) * Math.sin(ny * Math.PI * 6 + 1.5) * 0.25;

                double combined = (w1 + w2 + w3 + 1.75) / 3.5;
                int alpha = (int) (combined * 95);

                img.setRGB(x, y, (alpha << 24) | (190 << 16) | (205 << 8) | 225);
            }
        }
        ImageIO.write(img, "png", new File(path));
    }

    /**
     * Vinheta Cinematográfica Suave nas Bordas.
     */
    private static void generateVignette(String path) throws IOException {
        int w = 1280, h = 720;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        int cx = w / 2, cy = h / 2;
        double maxDist = Math.sqrt(cx * cx + cy * cy);

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                double dist = Math.sqrt((x - cx) * (x - cx) + (y - cy) * (y - cy));
                double norm = dist / maxDist;
                if (norm < 0.45) continue;

                double v = (norm - 0.45) / 0.55;
                int alpha = (int) (Math.pow(v, 2.2) * 235);
                img.setRGB(x, y, (Math.min(255, alpha) << 24) | 0x050608);
            }
        }
        ImageIO.write(img, "png", new File(path));
    }

    // =========================================================================
    // 2. GERAÇÃO DE ÁUDIOS DE ALTA FIDELIDADE (Prompt 3 - AGENTS.md § 7)
    // =========================================================================

    private static void generateAudio() throws IOException {
        System.out.println("Sintetizando trilha sonora e paisagem sonora atmosférica...");

        // Trilhas Musicais Minimalistas e Melancólicas
        saveWav(AUDIO_DIR + "/music_menu.wav", generateMenuMusic(16.0f, 44100));
        saveWav(AUDIO_DIR + "/music_stage1.wav", generateStage1Music(16.0f, 44100));
        saveWav(AUDIO_DIR + "/music_stage2.wav", generateStage2Music(16.0f, 44100));
        saveWav(AUDIO_DIR + "/music_stage3.wav", generateStage3Music(16.0f, 44100));
        saveWav(AUDIO_DIR + "/music_chase.wav", generateChaseMusic(8.0f, 44100));
        saveWav(AUDIO_DIR + "/music_gameover.wav", generateGameOverMusic(4.5f, 44100));
        saveWav(AUDIO_DIR + "/music_victory.wav", generateVictoryMusic(5.5f, 44100));
        // Compatibilidade com código anterior
        saveWav(AUDIO_DIR + "/ambient_music.wav", generateStage1Music(16.0f, 44100));

        // Paisagem Sonora e Ambiência
        saveWav(AUDIO_DIR + "/rain_loop.wav", generateGentleRain(6.0f, 44100));
        saveWav(AUDIO_DIR + "/wind_loop.wav", generateGentleWind(6.0f, 44100));
        saveWav(AUDIO_DIR + "/heartbeat.wav", generateHeartbeat(1.2f, 44100));

        // Efeitos Sonoros Orgânicos de Gameplay
        saveWav(AUDIO_DIR + "/footstep.wav", generateFootstep(0.18f, 44100));
        saveWav(AUDIO_DIR + "/flashlight_click.wav", generateClick(0.08f, 44100));
        saveWav(AUDIO_DIR + "/radio_static.wav", generateRadioTransmission(1.8f, 44100));
        saveWav(AUDIO_DIR + "/branch_snap.wav", generateSnap(0.24f, 44100));
        saveWav(AUDIO_DIR + "/creature_whisper.wav", generateWhisper(1.6f, 44100));
        saveWav(AUDIO_DIR + "/creature_screech.wav", generateScreech(0.9f, 44100));
        saveWav(AUDIO_DIR + "/creature_breath.wav", generateBreath(1.6f, 44100));
        saveWav(AUDIO_DIR + "/item_pickup.wav", generatePickup(0.60f, 44100));
        saveWav(AUDIO_DIR + "/door_unlock.wav", generateUnlock(0.45f, 44100));
        saveWav(AUDIO_DIR + "/curse_trigger.wav", generateSubBoom(2.2f, 44100));
        saveWav(AUDIO_DIR + "/button_click.wav", generateButtonClick(0.06f, 44100));

        System.out.println("Todos os 18 arquivos de áudio foram sintetizados com sucesso.");
    }

    /**
     * Auxiliar para sintetizar nota acústica melancólica (piano/sino com harmônicos e decaimento natural)
     */
    private static void renderAcousticNote(double[] buffer, int sampleRate, double startTime,
                                           double freq, double amplitude, double decayRate) {
        int startSample = (int) (startTime * sampleRate);
        int total = buffer.length;
        // Harmônicos com pesos decrescentes e decaimentos diferenciados
        double[] harms = {1.0, 0.45, 0.20, 0.08, 0.03};
        double[] decays = {1.0, 1.4, 2.0, 2.8, 3.8};

        for (int i = startSample; i < total; i++) {
            double t = (double) (i - startSample) / sampleRate;
            double sample = 0;
            for (int h = 0; h < harms.length; h++) {
                double hFreq = freq * (h + 1);
                double hEnv = Math.exp(-t * decayRate * decays[h]);
                sample += Math.sin(2.0 * Math.PI * hFreq * t) * harms[h] * hEnv;
            }
            // Simulação de reverberação de cauda suave (delay atenuado)
            if (t > 0.25) {
                sample += Math.sin(2.0 * Math.PI * freq * (t - 0.25)) * 0.18 * Math.exp(-t * decayRate * 0.8);
            }
            buffer[i] += sample * amplitude;
        }
    }

    /**
     * Aplica crossfade de loop suave nas pontas do buffer (evita clique na repetição)
     */
    private static void applyLoopCrossfade(double[] buffer, int fadeSamples) {
        int len = buffer.length;
        fadeSamples = Math.min(fadeSamples, len / 4);
        for (int i = 0; i < fadeSamples; i++) {
            double t = (double) i / fadeSamples;
            double fadeIn = t;
            double fadeOut = 1.0 - t;
            double mixed = buffer[i] * fadeIn + buffer[len - fadeSamples + i] * fadeOut;
            buffer[i] = mixed;
            buffer[len - fadeSamples + i] = mixed;
        }
    }

    /**
     * Converte buffer double[] com headroom seguro para short[] 16-bit
     */
    private static short[] convertToShortArray(double[] buffer, double maxPeak) {
        short[] out = new short[buffer.length];
        double peak = 0;
        for (double v : buffer) {
            if (Math.abs(v) > peak) peak = Math.abs(v);
        }
        double scale = (peak > maxPeak) ? (maxPeak / peak) : 1.0;
        for (int i = 0; i < buffer.length; i++) {
            double s = Math.max(-0.95, Math.min(0.95, buffer[i] * scale));
            out[i] = (short) (s * 32767);
        }
        return out;
    }

    /**
     * Tema do Menu Principal: Melancolia em Lá menor, notas de piano/sino espaçadas,
     * reverb etéreo e atmosfera acolhedora e fúnebre.
     */
    private static short[] generateMenuMusic(float duration, int sampleRate) {
        int total = (int) (duration * sampleRate);
        double[] buf = new double[total];

        // Drone de suporte em frequência baixa muito suave (-20dB)
        for (int i = 0; i < total; i++) {
            double t = (double) i / sampleRate;
            double s = (Math.sin(2.0 * Math.PI * 55.0 * t) * 0.08 +
                        Math.sin(2.0 * Math.PI * 110.0 * t) * 0.05) * (0.8 + 0.2 * Math.sin(t * 0.4));
            buf[i] += s;
        }

        // Notas com bastante espaço e respiração:
        renderAcousticNote(buf, sampleRate, 0.4, 110.0, 0.35, 0.7);   // A2
        renderAcousticNote(buf, sampleRate, 3.8, 164.81, 0.28, 0.8);  // E3
        renderAcousticNote(buf, sampleRate, 7.2, 130.81, 0.32, 0.7);  // C3
        renderAcousticNote(buf, sampleRate, 10.6, 123.47, 0.26, 0.85); // B2
        renderAcousticNote(buf, sampleRate, 13.5, 103.83, 0.24, 0.9); // G#2

        applyLoopCrossfade(buf, sampleRate);
        return convertToShortArray(buf, 0.55);
    }

    /**
     * Stage 1: The Awakening — Quase silêncio, sensação de isolamento, névoa e solidão.
     */
    private static short[] generateStage1Music(float duration, int sampleRate) {
        int total = (int) (duration * sampleRate);
        double[] buf = new double[total];

        // Notas esparsas com longos intervalos de silêncio contemplativo
        renderAcousticNote(buf, sampleRate, 1.2, 146.83, 0.25, 0.6); // D3
        renderAcousticNote(buf, sampleRate, 6.8, 174.61, 0.22, 0.65); // F3
        renderAcousticNote(buf, sampleRate, 12.2, 110.0, 0.26, 0.55); // A2

        applyLoopCrossfade(buf, sampleRate);
        return convertToShortArray(buf, 0.45);
    }

    /**
     * Stage 2: The Forgotten — Harmonia instável de vilarejo abandonado, lembranças frias.
     */
    private static short[] generateStage2Music(float duration, int sampleRate) {
        int total = (int) (duration * sampleRate);
        double[] buf = new double[total];

        // Intervalos de trítono e acordes diminutos suaves
        renderAcousticNote(buf, sampleRate, 0.8, 138.59, 0.28, 0.75); // C#3
        renderAcousticNote(buf, sampleRate, 4.6, 196.00, 0.24, 0.8);  // G3 (trítono instável)
        renderAcousticNote(buf, sampleRate, 8.8, 123.47, 0.26, 0.7);  // B2
        renderAcousticNote(buf, sampleRate, 12.8, 174.61, 0.25, 0.75); // F3

        applyLoopCrossfade(buf, sampleRate);
        return convertToShortArray(buf, 0.50);
    }

    /**
     * Stage 3: The Hollow — Ressonância profunda de santuário antigo, rituais e mistério.
     */
    private static short[] generateStage3Music(float duration, int sampleRate) {
        int total = (int) (duration * sampleRate);
        double[] buf = new double[total];

        // Fundo harmônico sombrio
        for (int i = 0; i < total; i++) {
            double t = (double) i / sampleRate;
            double s = (Math.sin(2.0 * Math.PI * 46.25 * t) * 0.09 +
                        Math.sin(2.0 * Math.PI * 69.30 * t) * 0.06) * (0.8 + 0.2 * Math.sin(t * 0.6));
            buf[i] += s;
        }

        renderAcousticNote(buf, sampleRate, 1.0, 92.50, 0.32, 0.6);  // F#2
        renderAcousticNote(buf, sampleRate, 5.5, 110.00, 0.28, 0.65); // A2
        renderAcousticNote(buf, sampleRate, 9.8, 146.83, 0.26, 0.7);  // D3
        renderAcousticNote(buf, sampleRate, 13.5, 87.31, 0.30, 0.6);  // F2

        applyLoopCrossfade(buf, sampleRate);
        return convertToShortArray(buf, 0.55);
    }

    /**
     * Tema de Perseguição: Pulso rítmico soturno, tensão controlada sem estridência ou clipping.
     */
    private static short[] generateChaseMusic(float duration, int sampleRate) {
        int total = (int) (duration * sampleRate);
        double[] buf = new double[total];

        // Batidas rítmicas abafadas em 90 BPM (intervalo de 0.666s)
        double beatInterval = 60.0 / 90.0;
        int beats = (int) (duration / beatInterval);

        for (int b = 0; b < beats; b++) {
            double beatTime = b * beatInterval;
            int startSample = (int) (beatTime * sampleRate);
            for (int i = startSample; i < total; i++) {
                double t = (double) (i - startSample) / sampleRate;
                if (t > 0.4) break;
                // Pulso em grave (75 Hz decaindo para 45 Hz)
                double f = 75.0 - t * 70.0;
                double env = Math.exp(-t * 14.0);
                buf[i] += Math.sin(2.0 * Math.PI * f * t) * env * 0.28;
            }

            // Nota curta de tensão alternando A2 e Bb2
            double tensionFreq = (b % 2 == 0) ? 110.0 : 116.54;
            renderAcousticNote(buf, sampleRate, beatTime + 0.1, tensionFreq, 0.16, 2.5);
        }

        applyLoopCrossfade(buf, sampleRate / 2);
        return convertToShortArray(buf, 0.50);
    }

    /**
     * Transição de Derrota (Game Over): Acorde melancólico descendente com fade suave.
     */
    private static short[] generateGameOverMusic(float duration, int sampleRate) {
        int total = (int) (duration * sampleRate);
        double[] buf = new double[total];

        renderAcousticNote(buf, sampleRate, 0.2, 164.81, 0.35, 0.8); // E3
        renderAcousticNote(buf, sampleRate, 0.8, 146.83, 0.32, 0.8); // D3
        renderAcousticNote(buf, sampleRate, 1.4, 130.81, 0.30, 0.8); // C3
        renderAcousticNote(buf, sampleRate, 2.0, 110.00, 0.38, 0.6); // A2

        // Fade out natural até o final
        for (int i = 0; i < total; i++) {
            double t = (double) i / sampleRate;
            if (t > 3.0) {
                double fade = 1.0 - (t - 3.0) / 1.5;
                buf[i] *= Math.max(0.0, fade);
            }
        }
        return convertToShortArray(buf, 0.50);
    }

    /**
     * Transição de Vitória: Acorde ascendente etéreo de alívio e quebra de maldição.
     */
    private static short[] generateVictoryMusic(float duration, int sampleRate) {
        int total = (int) (duration * sampleRate);
        double[] buf = new double[total];

        renderAcousticNote(buf, sampleRate, 0.2, 130.81, 0.32, 0.5); // C3
        renderAcousticNote(buf, sampleRate, 0.7, 164.81, 0.28, 0.5); // E3
        renderAcousticNote(buf, sampleRate, 1.2, 196.00, 0.26, 0.5); // G3
        renderAcousticNote(buf, sampleRate, 1.8, 293.66, 0.30, 0.4); // D4 (nona límpida)

        for (int i = 0; i < total; i++) {
            double t = (double) i / sampleRate;
            if (t > 4.0) {
                double fade = 1.0 - (t - 4.0) / 1.5;
                buf[i] *= Math.max(0.0, fade);
            }
        }
        return convertToShortArray(buf, 0.50);
    }

    /**
     * Chuva fina e contínua: Gotas discretas estocásticas filtradas por passa-baixa duplo (sem chiado de alta).
     */
    private static short[] generateGentleRain(float duration, int sampleRate) {
        int total = (int) (duration * sampleRate);
        double[] buf = new double[total];
        Random rand = new Random(42);

        double lp1 = 0, lp2 = 0;
        for (int i = 0; i < total; i++) {
            double w = rand.nextDouble() * 2.0 - 1.0;
            // Filtro passa-baixa suave em ~650Hz
            lp1 += 0.05 * (w - lp1);
            lp2 += 0.05 * (lp1 - lp2);

            // Gotas sutis esparsas pingando
            double drop = 0;
            if (rand.nextDouble() < 0.0035) {
                drop = (rand.nextDouble() * 0.15) * Math.sin(2.0 * Math.PI * (450 + rand.nextInt(350)) * ((double) i / sampleRate));
            }
            buf[i] = (lp2 * 0.16 + drop);
        }

        applyLoopCrossfade(buf, sampleRate / 2);
        return convertToShortArray(buf, 0.25);
    }

    /**
     * Vento florestal suave: Brisa suave através das copas, filtrada em 320Hz com LFO lento.
     */
    private static short[] generateGentleWind(float duration, int sampleRate) {
        int total = (int) (duration * sampleRate);
        double[] buf = new double[total];
        Random rand = new Random(101);

        double f1 = 0, f2 = 0;
        for (int i = 0; i < total; i++) {
            double t = (double) i / sampleRate;
            double w = rand.nextDouble() * 2.0 - 1.0;
            double lfo = 0.015 + 0.012 * Math.sin(2.0 * Math.PI * 0.16 * t);
            f1 += lfo * (w - f1);
            f2 += lfo * (f1 - f2);
            buf[i] = (f1 - f2) * 1.8;
        }

        applyLoopCrossfade(buf, sampleRate / 2);
        return convertToShortArray(buf, 0.22);
    }

    /**
     * Batimento cardíaco binaural "lub-dub": Ressonância profunda e natural, sem estalo ou distorção.
     */
    private static short[] generateHeartbeat(float duration, int sampleRate) {
        int total = (int) (duration * sampleRate);
        double[] buf = new double[total];

        for (int i = 0; i < total; i++) {
            double t = (double) i / sampleRate;
            double s = 0;
            // Primeiro pulso (sístole)
            if (t >= 0.05 && t < 0.20) {
                double dt = t - 0.05;
                s = Math.sin(2.0 * Math.PI * 54.0 * dt) * Math.sin(Math.PI * (dt / 0.15)) * 0.65;
            }
            // Segundo pulso (diástole)
            else if (t >= 0.32 && t < 0.45) {
                double dt = t - 0.32;
                s = Math.sin(2.0 * Math.PI * 46.0 * dt) * Math.sin(Math.PI * (dt / 0.13)) * 0.45;
            }
            buf[i] = s;
        }
        return convertToShortArray(buf, 0.60);
    }

    /**
     * Passos abafados em solo úmido e terra solta: Curto, orgânico, sem estalo agudo.
     */
    private static short[] generateFootstep(float duration, int sampleRate) {
        int total = (int) (duration * sampleRate);
        double[] buf = new double[total];
        Random rand = new Random(55);

        double lp1 = 0, lp2 = 0;
        for (int i = 0; i < total; i++) {
            double t = (double) i / sampleRate;
            double env = Math.exp(-t * 26.0);
            double noise = rand.nextDouble() * 2.0 - 1.0;
            lp1 += 0.15 * (noise - lp1);
            lp2 += 0.15 * (lp1 - lp2);
            // Impacto abafado de pisada
            double thud = Math.sin(2.0 * Math.PI * 95.0 * t) * Math.exp(-t * 35.0) * 0.35;
            buf[i] = (lp2 * 0.20 + thud) * env;
        }
        return convertToShortArray(buf, 0.40);
    }

    /**
     * Clique de lanterna: Mecanismo tático de interruptor com duplo transitório rápido.
     */
    private static short[] generateClick(float duration, int sampleRate) {
        int total = (int) (duration * sampleRate);
        double[] buf = new double[total];
        for (int i = 0; i < total; i++) {
            double t = (double) i / sampleRate;
            double s1 = Math.sin(2.0 * Math.PI * 1650.0 * t) * Math.exp(-t * 90.0) * 0.35;
            double s2 = (t > 0.012) ? Math.sin(2.0 * Math.PI * 2200.0 * (t - 0.012)) * Math.exp(-(t - 0.012) * 110.0) * 0.25 : 0;
            buf[i] = s1 + s2;
        }
        return convertToShortArray(buf, 0.45);
    }

    /**
     * Transmissão de rádio: Sintonia analógica e ruído filtrado de baixa intensidade (sem estática alta).
     */
    private static short[] generateRadioTransmission(float duration, int sampleRate) {
        int total = (int) (duration * sampleRate);
        double[] buf = new double[total];
        Random rand = new Random(88);

        double bp = 0;
        for (int i = 0; i < total; i++) {
            double t = (double) i / sampleRate;
            double chirp = Math.sin(2.0 * Math.PI * (780.0 + 120.0 * Math.sin(t * 12.0)) * t) * 0.10;
            double noise = rand.nextDouble() * 2.0 - 1.0;
            bp += 0.08 * (noise - bp);
            double env = Math.sin(Math.PI * (t / duration));
            buf[i] = (bp * 0.12 + chirp) * env;
        }
        return convertToShortArray(buf, 0.35);
    }

    /**
     * Estalo de galho seco: Transitório rápido e natural de madeira sob tensão.
     */
    private static short[] generateSnap(float duration, int sampleRate) {
        int total = (int) (duration * sampleRate);
        double[] buf = new double[total];
        Random rand = new Random(99);

        for (int i = 0; i < total; i++) {
            double t = (double) i / sampleRate;
            double click = (rand.nextDouble() * 2.0 - 1.0) * Math.exp(-t * 55.0) * 0.45;
            double woodTone = Math.sin(2.0 * Math.PI * 340.0 * t) * Math.exp(-t * 30.0) * 0.25;
            buf[i] = click + woodTone;
        }
        return convertToShortArray(buf, 0.50);
    }

    /**
     * Sussurro espectral: Formantes vocais com modulação etérea suave.
     */
    private static short[] generateWhisper(float duration, int sampleRate) {
        int total = (int) (duration * sampleRate);
        double[] buf = new double[total];
        Random rand = new Random(123);

        double bp1 = 0, bp2 = 0;
        for (int i = 0; i < total; i++) {
            double t = (double) i / sampleRate;
            double env = Math.sin(Math.PI * (t / duration));
            double w = rand.nextDouble() * 2.0 - 1.0;
            bp1 += 0.05 * (w - bp1);
            bp2 += 0.12 * (w - bp2);
            double vowel = (bp1 * Math.sin(2.0 * Math.PI * 380.0 * t) +
                            bp2 * Math.sin(2.0 * Math.PI * 1250.0 * t)) * 0.18;
            buf[i] = vowel * env;
        }
        return convertToShortArray(buf, 0.38);
    }

    /**
     * Grito agudo com pitch bend controlado: Mantido rigorosamente abaixo de -6dBFS para não clipar.
     */
    private static short[] generateScreech(float duration, int sampleRate) {
        int total = (int) (duration * sampleRate);
        double[] buf = new double[total];
        for (int i = 0; i < total; i++) {
            double t = (double) i / sampleRate;
            double env = Math.sin(Math.PI * (t / duration));
            double f = 580.0 - 180.0 * (t / duration) + 30.0 * Math.sin(t * 18.0);
            double s = (Math.sin(2.0 * Math.PI * f * t) * 0.25 +
                        Math.sin(2.0 * Math.PI * (f * 1.5) * t) * 0.12) * env;
            buf[i] = s;
        }
        return convertToShortArray(buf, 0.40);
    }

    /**
     * Respiração profunda e ofegante da criatura.
     */
    private static short[] generateBreath(float duration, int sampleRate) {
        int total = (int) (duration * sampleRate);
        double[] buf = new double[total];
        Random rand = new Random(77);

        double f = 0;
        for (int i = 0; i < total; i++) {
            double t = (double) i / sampleRate;
            double env = Math.sin(Math.PI * (t / duration));
            f += 0.04 * ((rand.nextDouble() * 2.0 - 1.0) - f);
            buf[i] = f * env * 0.35;
        }
        return convertToShortArray(buf, 0.35);
    }

    /**
     * Sino de cristal límpido de item/pista encontrada (agradável e com reverberação suave).
     */
    private static short[] generatePickup(float duration, int sampleRate) {
        int total = (int) (duration * sampleRate);
        double[] buf = new double[total];
        renderAcousticNote(buf, sampleRate, 0.02, 587.33, 0.28, 1.2); // D5
        renderAcousticNote(buf, sampleRate, 0.10, 880.00, 0.22, 1.4); // A5
        renderAcousticNote(buf, sampleRate, 0.18, 1174.66, 0.18, 1.6); // D6
        return convertToShortArray(buf, 0.45);
    }

    /**
     * Ferrolho de porta destrancando: Mecanismo de ferro pesado deslizando.
     */
    private static short[] generateUnlock(float duration, int sampleRate) {
        int total = (int) (duration * sampleRate);
        double[] buf = new double[total];
        for (int i = 0; i < total; i++) {
            double t = (double) i / sampleRate;
            double s1 = Math.sin(2.0 * Math.PI * 180.0 * t) * Math.exp(-t * 22.0) * 0.35;
            double s2 = (t > 0.08) ? Math.sin(2.0 * Math.PI * 320.0 * (t - 0.08)) * Math.exp(-(t - 0.08) * 25.0) * 0.28 : 0;
            buf[i] = s1 + s2;
        }
        return convertToShortArray(buf, 0.45);
    }

    /**
     * Ressonância ritualística expansiva (subgrave controlado sem distorção).
     */
    private static short[] generateSubBoom(float duration, int sampleRate) {
        int total = (int) (duration * sampleRate);
        double[] buf = new double[total];
        for (int i = 0; i < total; i++) {
            double t = (double) i / sampleRate;
            double env = Math.exp(-t * 1.5);
            double f = 65.0 * Math.exp(-t * 2.0) + 32.0;
            double sub = Math.sin(2.0 * Math.PI * f * t) * 0.45 * env;
            // Ressonância metálica no topo
            double chime = Math.sin(2.0 * Math.PI * 440.0 * t) * 0.12 * Math.exp(-t * 3.5);
            buf[i] = sub + chime;
        }
        return convertToShortArray(buf, 0.50);
    }

    /**
     * Clique refinado de interface com som de madeira suave.
     */
    private static short[] generateButtonClick(float duration, int sampleRate) {
        int total = (int) (duration * sampleRate);
        double[] buf = new double[total];
        for (int i = 0; i < total; i++) {
            double t = (double) i / sampleRate;
            double s = Math.sin(2.0 * Math.PI * 880.0 * t) * Math.exp(-t * 70.0) * 0.35;
            buf[i] = s;
        }
        return convertToShortArray(buf, 0.40);
    }

    private static void saveWav(String path, short[] samples) throws IOException {
        int sampleRate = 44100;
        int channels = 1;
        int bitsPerSample = 16;
        int byteRate = sampleRate * channels * (bitsPerSample / 8);
        int blockAlign = channels * (bitsPerSample / 8);
        int dataSize = samples.length * 2;
        int chunkSize = 36 + dataSize;

        ByteBuffer header = ByteBuffer.allocate(44);
        header.order(ByteOrder.LITTLE_ENDIAN);
        header.put((byte) 'R').put((byte) 'I').put((byte) 'F').put((byte) 'F');
        header.putInt(chunkSize);
        header.put((byte) 'W').put((byte) 'A').put((byte) 'V').put((byte) 'E');
        header.put((byte) 'f').put((byte) 'm').put((byte) 't').put((byte) ' ');
        header.putInt(16);
        header.putShort((short) 1);
        header.putShort((short) channels);
        header.putInt(sampleRate);
        header.putInt(byteRate);
        header.putShort((short) blockAlign);
        header.putShort((short) bitsPerSample);
        header.put((byte) 'd').put((byte) 'a').put((byte) 't').put((byte) 'a');
        header.putInt(dataSize);

        try (FileOutputStream fos = new FileOutputStream(path)) {
            fos.write(header.array());
            ByteBuffer dataBuffer = ByteBuffer.allocate(dataSize);
            dataBuffer.order(ByteOrder.LITTLE_ENDIAN);
            for (short s : samples) {
                dataBuffer.putShort(s);
            }
            fos.write(dataBuffer.array());
        }
    }
}
