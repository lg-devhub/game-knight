package com.hollowwoods.game.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.hollowwoods.game.entities.InteractiveObject;
import com.hollowwoods.game.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class StageData {
    public final int stageNumber;
    public final String stageName;
    public final String objectiveText;
    public final Color ambientColor;

    public final Vector2 playerSpawn = new Vector2();
    public final Vector2 watcherSpawn = new Vector2();

    public final TileType[][] tileGrid = new TileType[Constants.MAP_WIDTH][Constants.MAP_HEIGHT];
    public final List<Obstacle> obstacles = new ArrayList<>();
    public final List<InteractiveObject> interactiveObjects = new ArrayList<>();

    public StageData(int stageNumber, String stageName, String objectiveText, Color ambientColor) {
        this.stageNumber = stageNumber;
        this.stageName = stageName;
        this.objectiveText = objectiveText;
        this.ambientColor = ambientColor;
    }

    public static StageData createStage(int stageNumber) {
        switch (stageNumber) {
            case 1:
                return createStage1();
            case 2:
                return createStage2();
            case 3:
                return createStage3();
            default:
                return createStage1();
        }
    }

    private static StageData createStage1() {
        StageData s = new StageData(
                1,
                "Stage 1 - The Awakening",
                "Encontre a chave enferrujada e alcance a cabana.",
                new Color(0.12f, 0.16f, 0.18f, 0.88f) // Escuridão equilibrada e legível
        );

        s.playerSpawn.set(280, 280);
        s.watcherSpawn.set(1600, 1500);

        // Grade de terreno da floresta
        for (int x = 0; x < Constants.MAP_WIDTH; x++) {
            for (int y = 0; y < Constants.MAP_HEIGHT; y++) {
                if ((x >= 4 && x <= 8 && y >= 4 && y <= 30) || (y >= 14 && y <= 16 && x >= 8 && x <= 32)) {
                    s.tileGrid[x][y] = TileType.FOREST_PATH;
                } else if ((x + y) % 5 == 0) {
                    s.tileGrid[x][y] = TileType.FOREST_MOSS;
                } else if (x == 18 && y == 12) {
                    s.tileGrid[x][y] = TileType.FOREST_PUDDLE;
                } else {
                    s.tileGrid[x][y] = TileType.FOREST_DIRT;
                }
            }
        }

        // Árvores de borda (limites intransponíveis)
        for (int i = 0; i < Constants.MAP_WIDTH; i++) {
            s.obstacles.add(new Obstacle(i * 64, 0, 64, 64, Obstacle.Type.TREE));
            s.obstacles.add(new Obstacle(i * 64, (Constants.MAP_HEIGHT - 1) * 64, 64, 64, Obstacle.Type.TREE));
        }
        for (int j = 1; j < Constants.MAP_HEIGHT - 1; j++) {
            s.obstacles.add(new Obstacle(0, j * 64, 64, 64, Obstacle.Type.TREE));
            s.obstacles.add(new Obstacle((Constants.MAP_WIDTH - 1) * 64, j * 64, 64, 64, Obstacle.Type.TREE));
        }

        // Construção da Cabana Abandonada (x=28..34, y=26..32)
        for (int x = 28; x <= 34; x++) {
            for (int y = 26; y <= 32; y++) {
                s.tileGrid[x][y] = TileType.WOOD_FLOOR;
            }
        }
        for (int x = 28; x <= 34; x++) {
            s.obstacles.add(new Obstacle(x * 64, 32 * 64, 64, 64, Obstacle.Type.CABIN_WALL));
            if (x != 31) { // Abertura para a porta em x=31
                s.obstacles.add(new Obstacle(x * 64, 26 * 64, 64, 64, Obstacle.Type.CABIN_WALL));
            }
        }
        for (int y = 27; y <= 31; y++) {
            s.obstacles.add(new Obstacle(28 * 64, y * 64, 64, 64, Obstacle.Type.CABIN_WALL));
            s.obstacles.add(new Obstacle(34 * 64, y * 64, 64, 64, Obstacle.Type.CABIN_WALL));
        }

        // Porta da cabana
        s.interactiveObjects.add(new InteractiveObject(
                InteractiveObject.Type.CABIN_DOOR,
                31 * 64 + 32, 26 * 64 + 32,
                "[E] Abrir Cabana Abandonada",
                "Porta da Cabana",
                "A porta esta trancada com um cadeado antigo. Encontre a chave para abri-la."
        ));

        // Árvores dispersas no cenário
        int[][] treeSpots = {
                {10, 8}, {12, 10}, {14, 6}, {8, 18}, {11, 22}, {15, 20}, {18, 25},
                {22, 10}, {25, 8}, {24, 18}, {20, 22}, {16, 28}, {22, 28},
                {6, 12}, {14, 14}, {18, 8}, {26, 14}, {12, 26}
        };
        for (int[] t : treeSpots) {
            s.obstacles.add(new Obstacle(t[0] * 64, t[1] * 64, 80, 80, Obstacle.Type.TREE));
        }

        // Cercas de madeira
        s.obstacles.add(new Obstacle(320, 360, 64, 16, Obstacle.Type.FENCE));
        s.obstacles.add(new Obstacle(384, 360, 64, 16, Obstacle.Type.FENCE));

        // Itens interativos
        s.interactiveObjects.add(new InteractiveObject(
                InteractiveObject.Type.RADIO,
                420, 310,
                "[E] Sintonizar Radio",
                "Transmissao de Radio",
                "...Elias? Por favor me escute... A criatura congela quando e iluminada... Procure a chave no circulo de pedras a leste e entre na cabana!"
        ));

        s.interactiveObjects.add(new InteractiveObject(
                InteractiveObject.Type.KEY_CABIN,
                21 * 64 + 32, 8 * 64 + 32,
                "[E] Pegar Chave Enferrujada",
                "Chave Enferrujada",
                "Uma chave de ferro antiga coberta por musgo. Parece servir na porta da cabana."
        ));

        s.interactiveObjects.add(new InteractiveObject(
                InteractiveObject.Type.BATTERY,
                700, 600,
                "[E] Pegar Bateria",
                "Bateria de Lanterna",
                "Recarrega 50% de energia da lanterna."
        ));

        s.interactiveObjects.add(new InteractiveObject(
                InteractiveObject.Type.MEDKIT,
                1200, 480,
                "[E] Pegar Curativo",
                "Kit Médico de Emergência",
                "Restaura 40 pontos de vida."
        ));

        s.interactiveObjects.add(new InteractiveObject(
                InteractiveObject.Type.NOTE_1,
                950, 950,
                "[E] Ler Pagina do Diario",
                "Diario de Bordo - Dia 1",
                "'Ele fica imóvel quando você o encara. Seus olhos vazios parecem temer a luz direta. Mas no instante em que você desvia o olhar, ele avança.'"
        ));

        return s;
    }

    private static StageData createStage2() {
        StageData s = new StageData(
                2,
                "Stage 2 - The Forgotten",
                "Encontre os 3 fragmentos da fotografia e abra o portão da cripta.",
                new Color(0.14f, 0.14f, 0.16f, 0.90f) // Névoa gótica acinzentada
        );

        s.playerSpawn.set(240, 240);
        s.watcherSpawn.set(1800, 1800);

        // Terreno do vilarejo
        for (int x = 0; x < Constants.MAP_WIDTH; x++) {
            for (int y = 0; y < Constants.MAP_HEIGHT; y++) {
                if (x % 3 == 0) {
                    s.tileGrid[x][y] = TileType.VILLAGE_COBBLE;
                } else if ((x + y) % 4 == 0) {
                    s.tileGrid[x][y] = TileType.VILLAGE_FLAGSTONE;
                } else {
                    s.tileGrid[x][y] = TileType.VILLAGE_OVERGROWN;
                }
            }
        }

        // Muros limítrofes
        for (int i = 0; i < Constants.MAP_WIDTH; i++) {
            s.obstacles.add(new Obstacle(i * 64, 0, 64, 64, Obstacle.Type.STONE_WALL));
            s.obstacles.add(new Obstacle(i * 64, (Constants.MAP_HEIGHT - 1) * 64, 64, 64, Obstacle.Type.STONE_WALL));
        }
        for (int j = 1; j < Constants.MAP_HEIGHT - 1; j++) {
            s.obstacles.add(new Obstacle(0, j * 64, 64, 64, Obstacle.Type.STONE_WALL));
            s.obstacles.add(new Obstacle((Constants.MAP_WIDTH - 1) * 64, j * 64, 64, 64, Obstacle.Type.STONE_WALL));
        }

        // Casas em ruínas (criam becos e corredores estreitos)
        buildHouse(s, 8, 8, 6, 5);
        buildHouse(s, 20, 8, 6, 5);
        buildHouse(s, 8, 20, 6, 5);

        // Cemitério
        for (int cx = 17; cx <= 23; cx += 2) {
            for (int cy = 21; cy <= 26; cy += 2) {
                s.obstacles.add(new Obstacle(cx * 64, cy * 64, 32, 44, Obstacle.Type.TOMBSTONE));
            }
        }

        // Portão da Cripta
        s.interactiveObjects.add(new InteractiveObject(
                InteractiveObject.Type.CRYPT_GATE,
                30 * 64 + 32, 28 * 64 + 32,
                "[E] Abrir Portao de Ferro da Cripta",
                "Portao Selado",
                "Um portão de ferro forjado antigo com tres encaixes. Requer os 3 fragmentos de fotografia da sua irma para abrir."
        ));

        // 3 Fragmentos de Fotografia
        s.interactiveObjects.add(new InteractiveObject(
                InteractiveObject.Type.PHOTO_FRAG_1,
                11 * 64 + 32, 10 * 64 + 32,
                "[E] Pegar Fragmento de Foto (1/3)",
                "Fragmento de Foto 1",
                "Revela o rosto sereno da irmã de Elias segurando um medalhão antigo de família."
        ));

        s.interactiveObjects.add(new InteractiveObject(
                InteractiveObject.Type.PHOTO_FRAG_2,
                20 * 64 + 32, 24 * 64 + 32,
                "[E] Pegar Fragmento de Foto (2/3)",
                "Fragmento de Foto 2",
                "Mostra a entrada das catacumbas do vilarejo sob uma lua avermelhada."
        ));

        s.interactiveObjects.add(new InteractiveObject(
                InteractiveObject.Type.PHOTO_FRAG_3,
                23 * 64 + 32, 11 * 64 + 32,
                "[E] Pegar Fragmento de Foto (3/3)",
                "Fragmento de Foto 3",
                "Uma anotação no verso: 'Não fuja do olhar dele. O medo é o alimento da floresta.'"
        ));

        // Suprimentos e lore
        s.interactiveObjects.add(new InteractiveObject(
                InteractiveObject.Type.BATTERY,
                14 * 64, 17 * 64,
                "[E] Pegar Bateria",
                "Bateria",
                "Recarrega 50% de lanterna."
        ));

        s.interactiveObjects.add(new InteractiveObject(
                InteractiveObject.Type.MEDKIT,
                26 * 64, 18 * 64,
                "[E] Pegar Kit Medico",
                "Curativos",
                "Restaura 40 de vida."
        ));

        s.interactiveObjects.add(new InteractiveObject(
                InteractiveObject.Type.NOTE_2,
                18 * 64, 14 * 64,
                "[E] Ler Registro dos Habitantes",
                "Anotacao do Vilarejo",
                "'Os aldeoes tentaram correr sem olhar para tras. Quanto mais rapido voce tenta escapar sem vigiar as sombras, mais voraz a entidade se torna.'"
        ));

        return s;
    }

    private static void buildHouse(StageData s, int startX, int startY, int w, int h) {
        for (int x = startX; x < startX + w; x++) {
            s.obstacles.add(new Obstacle(x * 64, startY * 64, 64, 64, Obstacle.Type.STONE_WALL));
            if (x != startX + w / 2) {
                s.obstacles.add(new Obstacle(x * 64, (startY + h - 1) * 64, 64, 64, Obstacle.Type.STONE_WALL));
            }
        }
        for (int y = startY + 1; y < startY + h - 1; y++) {
            s.obstacles.add(new Obstacle(startX * 64, y * 64, 64, 64, Obstacle.Type.STONE_WALL));
            s.obstacles.add(new Obstacle((startX + w - 1) * 64, y * 64, 64, 64, Obstacle.Type.STONE_WALL));
        }
    }

    private static StageData createStage3() {
        StageData s = new StageData(
                3,
                "Stage 3 - The Hollow",
                "Ative os 3 selos rituais no altar e escape do Observador.",
                new Color(0.20f, 0.08f, 0.10f, 0.90f) // Iluminação avermelhada e sombria
        );

        s.playerSpawn.set(240, 240);
        s.watcherSpawn.set(2000, 2000);

        // Terreno corrompido
        for (int x = 0; x < Constants.MAP_WIDTH; x++) {
            for (int y = 0; y < Constants.MAP_HEIGHT; y++) {
                if ((x + y) % 3 == 0) {
                    s.tileGrid[x][y] = TileType.CORRUPTED_STONE;
                } else if ((x * y) % 5 == 0) {
                    s.tileGrid[x][y] = TileType.BLOOD_STONE;
                } else {
                    s.tileGrid[x][y] = TileType.ELDRITCH_EARTH;
                }
            }
        }

        // Monolitos de borda
        for (int i = 0; i < Constants.MAP_WIDTH; i++) {
            s.obstacles.add(new Obstacle(i * 64, 0, 64, 64, Obstacle.Type.MONOLITH));
            s.obstacles.add(new Obstacle(i * 64, (Constants.MAP_HEIGHT - 1) * 64, 64, 64, Obstacle.Type.MONOLITH));
        }
        for (int j = 1; j < Constants.MAP_HEIGHT - 1; j++) {
            s.obstacles.add(new Obstacle(0, j * 64, 64, 64, Obstacle.Type.MONOLITH));
            s.obstacles.add(new Obstacle((Constants.MAP_WIDTH - 1) * 64, j * 64, 64, 64, Obstacle.Type.MONOLITH));
        }

        // Labirinto de monolitos antigos
        int[][] monoliths = {
                {8, 12}, {14, 8}, {18, 16}, {12, 24}, {22, 14}, {26, 22},
                {10, 18}, {24, 8}, {28, 16}, {16, 28}, {22, 28}
        };
        for (int[] m : monoliths) {
            s.obstacles.add(new Obstacle(m[0] * 64, m[1] * 64, 64, 128, Obstacle.Type.MONOLITH));
        }

        // Altar Central
        s.obstacles.add(new Obstacle(20 * 64, 20 * 64, 128, 64, Obstacle.Type.ALTAR));
        s.interactiveObjects.add(new InteractiveObject(
                InteractiveObject.Type.ALTAR,
                21 * 64, 20 * 64,
                "[E] Ativar Selos no Altar Sagrado",
                "O Altar da Origem",
                "Requer os 3 Selos Rituais para dissipar o ciclo do Observador e purificar a floresta."
        ));

        // 3 Selos Rituais
        s.interactiveObjects.add(new InteractiveObject(
                InteractiveObject.Type.SEAL_1,
                6 * 64 + 32, 26 * 64 + 32,
                "[E] Coletar Selo Carmesim (1/3)",
                "Selo Carmesim da Coragem",
                "Pulsa com calor sob sua mão, dissipando a cegueira do pavor."
        ));

        s.interactiveObjects.add(new InteractiveObject(
                InteractiveObject.Type.SEAL_2,
                30 * 64 + 32, 8 * 64 + 32,
                "[E] Coletar Selo Ambar (2/3)",
                "Selo Ambar da Memoria",
                "Restitui a certeza de que a irmã de Elias confiou sua sobrevivência à sua bravura."
        ));

        s.interactiveObjects.add(new InteractiveObject(
                InteractiveObject.Type.SEAL_3,
                31 * 64 + 32, 31 * 64 + 32,
                "[E] Coletar Selo do Vazio (3/3)",
                "Selo do Vazio da Revelacao",
                "O artefato final que quebra o vínculo do Observador com a realidade terrena."
        ));

        // Suprimentos
        s.interactiveObjects.add(new InteractiveObject(
                InteractiveObject.Type.BATTERY,
                16 * 64, 12 * 64,
                "[E] Pegar Bateria",
                "Bateria",
                "Recarrega 50% de lanterna."
        ));

        s.interactiveObjects.add(new InteractiveObject(
                InteractiveObject.Type.MEDKIT,
                24 * 64, 24 * 64,
                "[E] Pegar Kit Medico",
                "Kit Medico",
                "Restaura 40 de vida."
        ));

        s.interactiveObjects.add(new InteractiveObject(
                InteractiveObject.Type.NOTE_3,
                12 * 64, 10 * 64,
                "[E] Ler Gravura Antiga",
                "Tabua da Revelacao",
                "'Aquele que nao teme seu olhar quebra as correntes deste solo sombrio.'"
        ));

        return s;
    }
}
