# AGENTS.md --- THE HOLLOW WOODS

## 1. Papel do agente

Você é o agente principal de engenharia do projeto **THE HOLLOW WOODS**,
um jogo 2D de terror psicológico e sobrevivência desenvolvido em Java
com libGDX.

Seu trabalho é entregar um jogo **jogável, legível, estável, coerente e
compilável**. Não basta produzir uma demonstração visual, uma tela
bonita ou código que pareça correto. Toda funcionalidade anunciada
precisa existir e funcionar.

O projeto será desenvolvido com um modelo de capacidade limitada.
Portanto: - Trabalhe em etapas pequenas e verificáveis. - Leia o projeto
antes de modificá-lo. - Não reescreva arquivos inteiros sem
necessidade. - Não crie sistemas duplicados. - Não tente implementar
todos os subsistemas em uma única resposta. - Preserve o que já
funciona. - Compile e teste depois de cada conjunto coerente de
alterações. - Se não conseguir testar algo, informe explicitamente que
não foi validado.

## 2. Fonte de verdade e precedência

Em caso de conflito, siga esta ordem: 1. Requisitos explícitos deste
`AGENTS.md`. 2. `SKILLS.md`. 3. Prompts de implementação aprovados em
`PROMPTS.md`. 4. Código e arquitetura já existentes, desde que não
contradigam os itens anteriores.

Não invente funcionalidades, assets, arquivos, bibliotecas ou resultados
de testes. Não diga que algo foi concluído sem verificar.

## 3. Identidade do jogo

**Nome:** THE HOLLOW WOODS\
**Gênero:** terror psicológico, exploração e sobrevivência 2D top-down.\
**Tecnologia:** Java + libGDX.\
**Plataforma inicial:** desktop.\
**Perspectiva:** visão superior/3⁄4 top-down consistente.\
**Fantasia central:** o jogador explora uma floresta hostil com uma
lanterna limitada. Uma entidade sobrenatural se aproxima quando não está
sendo observada e recua ou congela quando é realmente iluminada e vista.

### Pilares inegociáveis

1.  **Legibilidade:** o jogador precisa entender onde está, para onde
    pode ir, o que é interativo e de onde vem o perigo.
2.  **Tensão por antecipação:** suspense vem de ritmo, silêncio,
    enquadramento, pistas e áudio espacial --- não de ruído permanente.
3.  **Luz como mecânica:** a lanterna é uma ferramenta estratégica, não
    apenas um efeito decorativo.
4.  **Perigo compreensível:** o comportamento da entidade deve ser
    consistente e comunicável ao jogador.
5.  **Atmosfera sem sacrificar jogabilidade:** neblina, chuva, sombras e
    pós-processamento nunca podem esconder informações essenciais.
6.  **Coerência:** lógica, arte, som, música e narrativa devem parecer
    partes do mesmo jogo.

## 4. Escopo de gameplay

O jogo deve conter: - Menu principal. - Introdução narrativa curta e
jogável ou tela de prólogo. - Sorteio de uma maldição no início de cada
nova partida. - Três estágios distintos e progressivos. - Pausa e
configurações. - Game Over. - Vitória/final. - HUD funcional. -
Pontuação e estatísticas da partida. - Salvamento de progresso e
recorde, quando compatível com a estrutura existente.

### Estágios

1.  **THE AWAKENING:** floresta e cabana. Ensina movimento, exploração,
    lanterna, interação e observação da entidade. Objetivo: encontrar a
    chave e alcançar a cabana.
2.  **THE FORGOTTEN:** vilarejo abandonado. Objetivo: encontrar três
    fragmentos de fotografia e avançar. Introduz rotas, abrigos, pistas
    e maior pressão.
3.  **THE HOLLOW:** coração da floresta/santuário subterrâneo. Objetivo:
    descobrir a origem da entidade e concluir a fuga/confronto
    narrativo. Maior dificuldade e conclusão da história.

Cada estágio precisa ter layout, objetivos, ritmo e identidade próprios.
Não conte três áreas visualmente idênticas como três níveis.

### Maldições

A partida sorteia uma maldição e mantém seu efeito até o fim: -
**BLACKOUT:** falhas breves e anunciadas da lanterna. - **FRAGILE
LIGHT:** consumo de bateria aumentado. - **LOST PATH:** mapa/minimapa
indisponível ou parcialmente oculto. - **WHISPERING:** eventos sonoros
falsos, raros e distinguíveis dos sinais reais. - **THE WATCHER:**
entidade mais rápida quando não observada.

A maldição deve ser mostrada antes da partida, no HUD e no resumo final.
Seu efeito deve ser real, testável e balanceado. Não use chiado alto
como efeito de WHISPERING.

## 5. Mecânica central: The Watcher

A entidade deve possuir estados claros, implementados por uma máquina de
estados ou lógica equivalente: - `OBSERVED`: está dentro do cone útil da
lanterna, dentro do alcance e com linha de visão desobstruída; não
avança. - `STALKING`: está fora do campo de visão; aproxima-se com
cautela. - `HUNTING`: detectou o jogador ou está em perseguição; acelera
e emite sinais sonoros próprios. - `SEARCHING`: perdeu o jogador;
procura na última posição conhecida. - `RETREATING`: abandona a
perseguição ou recua conforme as regras do estágio.

A condição de observação deve considerar: - Ângulo entre direção da
lanterna e direção da entidade. - Distância máxima de iluminação. -
Linha de visão, incluindo obstáculos. - Estado da lanterna e maldição
ativa.

Não considere a entidade observada apenas por estar perto do jogador.
Não permita que ela atravesse paredes. A regra deve ser previsível e
validada em teste.

A entidade deve ser assustadora por silhueta, movimento, distância e
timing. Evite aparições aleatórias injustas ou teletransporte sem aviso.

## 6. Diretrizes técnicas

Antes de programar: 1. Inspecione a estrutura de pastas. 2. Leia
`settings.gradle`, `build.gradle` e/ou arquivos Gradle Kotlin
existentes. 3. Identifique versão do Java, Gradle e libGDX. 4. Descubra
o ponto de entrada e as telas existentes. 5. Execute a compilação
inicial e registre erros preexistentes. 6. Preserve a configuração do
projeto. Não migre versões nem substitua o build sem motivo técnico
documentado.

Use APIs compatíveis com a versão instalada. Prefira soluções simples e
nativas do libGDX.

### Organização sugerida

Adapte aos pacotes reais do projeto; não crie estrutura duplicada se já
houver equivalente.

``` text
core/
  MainGame.java
  screens/
    MainMenuScreen.java
    StoryScreen.java
    CurseScreen.java
    GameScreen.java
    PauseScreen.java
    GameOverScreen.java
    VictoryScreen.java
  entities/
    Entity.java
    Player.java
    Watcher.java
  world/
    GameMap.java
    Obstacle.java
    CollisionManager.java
    StageDefinition.java
  systems/
    AIController.java
    LightingSystem.java
    FogSystem.java
    CurseManager.java
    InventoryManager.java
    ScoreManager.java
    AudioManager.java
    SaveManager.java
  ui/
    HUD.java
    DialogBox.java
  utils/
    Constants.java
    GameAssets.java
```

### Regras de implementação

-   Separe renderização, atualização, entrada, estado de jogo e regras
    de negócio.
-   Use `delta`/delta time para movimento e animações independentes da
    taxa de quadros.
-   Use câmera e viewport adequados para diferentes resoluções.
-   Centralize constantes de velocidade, alcance, volume e dificuldade.
-   Gerencie carregamento e descarregamento de assets.
-   Implemente `dispose()` para recursos que pertencem às
    telas/sistemas.
-   Evite criar `SpriteBatch`, `ShapeRenderer`, `Texture` ou `Sound`
    repetidamente a cada frame.
-   Não faça leitura de arquivos ou operações pesadas dentro do
    `render()`.
-   Trate colisões e limites do mapa.
-   Evite `Thread.sleep()` no loop do jogo.
-   Evite dependências externas desnecessárias.
-   Não faça download de assets em runtime. Use assets locais e registre
    suas licenças/créditos.
-   Não use fontes, imagens ou sons protegidos sem licença adequada.

## 7. Áudio: requisito crítico de qualidade

O áudio atual foi considerado inaceitável. A nova implementação deve
começar com uma configuração segura e conservadora.

### Proibições

-   Não gerar ruído branco/rosa contínuo para simular atmosfera.
-   Não usar estática de rádio constante.
-   Não somar várias camadas de vento, chuva e drones em volume alto.
-   Não reproduzir sons em todo frame.
-   Não reiniciar loops a cada chamada de `render()`.
-   Não aplicar distorção, clipping ou saturação como padrão.
-   Não usar picos repentinos de volume como susto.
-   Não deixar sons de teste ou placeholders ativos na build final.

### Regras obrigatórias

-   Volume mestre inicial: aproximadamente 35% a 45%.
-   Música inicial: aproximadamente 15% a 22% do volume mestre.
-   Ambiente inicial: aproximadamente 10% a 18% do volume mestre.
-   Efeitos iniciais: aproximadamente 20% a 30% do volume mestre.
-   Todos os volumes devem ser configuráveis e multiplicados pelo volume
    mestre.
-   Faça fade-in e fade-out para loops e transições.
-   Evite clipping: mantenha headroom e normalize assets de origem.
-   Sons simultâneos precisam de limites de instâncias e prioridades.
-   Eventos de áudio devem ser acionados por eventos de gameplay, não
    pelo loop de renderização.
-   O jogador deve conseguir ouvir passos, interação, sinais da entidade
    e objetivos sem ser encoberto pelo ambiente.
-   Implemente botão de mute e controles independentes para música,
    ambiente e efeitos.
-   Ao pausar, reduza ou interrompa o áudio de gameplay de forma
    consistente.

### Teste de áudio

Antes de considerar áudio pronto: 1. Inicie o jogo com volume padrão. 2.
Fique parado por pelo menos 30 segundos. 3. Caminhe, corra (se houver),
interaja e provoque um evento da entidade. 4. Teste pausa, retomada,
troca de tela e encerramento. 5. Verifique se não há chiado constante,
estalos, loops duplicados, volume excessivo ou sons presos. 6. Confirme
que todos os sliders realmente alteram seus canais.

Se não houver assets de áudio adequados, comece com silêncio ou poucos
sons limpos e discretos. É melhor uma ambientação simples e limpa do que
uma trilha ruidosa.

## 8. Qualidade visual e acessibilidade de leitura

A tela atual ficou escura demais. A escuridão deve criar atmosfera, não
impedir que o jogador veja o jogo.

### Regras

-   O jogador, o chão caminhável, obstáculos, itens e inimigos precisam
    ser distinguíveis.
-   A área próxima ao personagem deve permanecer legível mesmo fora do
    cone da lanterna.
-   A lanterna deve criar contraste e orientar a exploração, sem ser a
    única fonte que permite enxergar qualquer coisa.
-   A chuva deve ser discreta, fina e de baixo contraste; nunca uma
    camada de riscos brancos brilhantes sobre toda a tela.
-   Neblina deve ser localizada e sutil.
-   Não cobrir o personagem ou objetos interativos com partículas.
-   Não usar tela quase totalmente preta como estado normal.
-   Não desenhar apenas formas geométricas como arte final.
-   Não misturar sprites com perspectivas, escalas ou estilos
    incompatíveis.
-   HUD deve ter contraste alto, tamanho legível e não esconder a área
    de jogo.
-   Inclua opções de intensidade de efeitos, brilho e escala da
    interface quando viável.

### Critério de leitura

Em uma captura de tela sem explicação, deve ser possível identificar: -
O personagem. - O terreno e ao menos uma rota navegável. - A direção
aproximada da lanterna. - Um elemento de cenário. - O objetivo atual. -
Os indicadores essenciais do HUD.

## 9. Fluxo de trabalho obrigatório

Para cada tarefa: 1. **Inspecionar:** localizar arquivos e compreender a
implementação atual. 2. **Planejar:** listar arquivos que serão
alterados e o resultado esperado. 3. **Implementar:** fazer uma mudança
coesa por vez. 4. **Compilar:** executar a tarefa Gradle apropriada. 5.
**Testar:** validar o comportamento no jogo, não apenas a ausência de
erros de compilação. 6. **Relatar:** dizer o que mudou, o que foi
testado e o que ainda falta.

Não faça refatorações grandes junto com correções pequenas. Não apague
conteúdo existente sem explicar por quê.

### Ao encontrar erro

-   Leia a mensagem completa.
-   Identifique a causa provável.
-   Corrija a menor área possível.
-   Compile novamente.
-   Não esconda erros com comentários, `try/catch` vazio ou desativação
    de funcionalidades.

## 10. Critérios mínimos de aceitação

Uma entrega só pode ser chamada de funcional se: - O projeto compila. -
O jogo inicia sem exceção. - O menu permite iniciar e sair. - O jogador
se move de forma consistente. - Colisões impedem atravessar
obstáculos. - A lanterna liga/desliga ou funciona conforme a mecânica
definida. - A bateria muda de acordo com o uso. - A entidade respeita
observação, alcance e obstáculos. - Cada maldição altera efetivamente a
partida. - Os três estágios são acessíveis e diferentes. - Objetivos e
interações funcionam. - Game Over e vitória são alcançáveis. - Pontuação
e resumo final apresentam dados reais. - Áudio padrão é confortável, sem
ruído constante ou clipping. - HUD e mundo são legíveis em resolução
desktop comum.

## 11. Comunicação

Responda em português brasileiro. Seja objetivo e técnico. Não prometa o
que não implementou. Ao terminar cada fase, informe: - Arquivos
criados/alterados. - Funcionalidades implementadas. - Comando de
compilação executado e resultado. - Testes realmente realizados. -
Limitações conhecidas. - Próxima tarefa recomendada.

**Regra final:** priorize um jogo pequeno, completo e bem executado em
vez de um jogo enorme, cheio de sistemas quebrados ou efeitos
exagerados.
