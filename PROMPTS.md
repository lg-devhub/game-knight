# PROMPTS.md --- THE HOLLOW WOODS

## Como usar este documento

Use os prompts na ordem indicada. Cole um prompt por vez no Antigravity.
Mantenha `AGENTS.md` e `SKILLS.md` na raiz do projeto para que o agente
consulte as regras permanentes.

**Importante para Gemini Flash:** não envie os quatro prompts de uma só
vez. O fluxo foi dividido para evitar alterações grandes, perda de
contexto e implementação superficial.

Ordem: 1. Prompt de lógica, arquitetura e funcionalidades. 2. Prompt de
direção visual e implementação artística. 3. Prompt de música e áudio.
4. Prompt de história e narrativa.

Antes de cada etapa, peça ao agente para ler os dois arquivos de
instruções. Cada prompt deve ser executado como uma fase independente,
com build/testes ao final.

------------------------------------------------------------------------

# PROMPT 1 --- LÓGICA, ARQUITETURA E FUNCIONALIDADES

Você é o responsável pela engenharia de gameplay de **THE HOLLOW
WOODS**, um jogo 2D top-down de terror psicológico e sobrevivência feito
em Java com libGDX.

Antes de agir, leia integralmente `AGENTS.md` e `SKILLS.md`. Em seguida,
inspecione o projeto atual. Não presuma que a estrutura, dependências ou
classes sugeridas já existem.

## Objetivo desta fase

Transformar a base atual em um jogo funcional e estruturado. Priorize
mecânicas, estados, progressão, controles, colisões e fluxo de telas.
Não tente finalizar arte, música ou efeitos visuais nesta fase. Use
placeholders temporários claramente identificados apenas quando
necessários.

## Regras de execução

1.  Analise pastas, Gradle, versão Java/libGDX, ponto de entrada e
    código existente.
2.  Execute uma compilação inicial e identifique erros preexistentes.
3.  Apresente um plano dividido em tarefas pequenas, com arquivos que
    pretende alterar.
4.  Implemente uma tarefa de cada vez.
5.  Compile após cada conjunto coerente de mudanças.
6.  Não reescreva o projeto inteiro.
7.  Não adicione dependências ou migre versões sem justificar.
8.  Não declare teste realizado sem executar.
9.  Preserve código que já funciona.

## Visão do jogo

O jogador controla **Elias**, que desperta em uma floresta desconhecida
durante a madrugada. Uma voz familiar surge em transmissões de rádio,
conduzindo-o em direção à irmã desaparecida. A floresta parece mudar
quando não está sendo observada. Uma entidade chamada **The Watcher**
persegue o jogador, mas congela quando é iluminada e efetivamente vista.

A mecânica central é o gerenciamento de atenção e luz: olhar para a
entidade pode mantê-la imóvel, mas limita a exploração e consome energia
da lanterna. O jogador precisa alternar exploração, observação, fuga e
busca por recursos.

## Funcionalidades obrigatórias

### A. Estrutura e estados

Implemente uma arquitetura clara com: - Estado global do jogo. - Telas
de menu, introdução, sorteio/apresentação da maldição, gameplay, pausa,
Game Over e vitória. - Gerenciamento de transição entre telas. -
Gerenciamento central de assets. - Sistemas separados para jogador,
mundo, entidade, áudio, pontuação, inventário e maldições, conforme
necessário.

Adapte os nomes e pacotes à estrutura real. Não crie duplicatas.

### B. Jogador

-   Movimento com WASD e/ou setas.
-   Movimento diagonal normalizado.
-   Colisão com paredes, árvores, cercas, móveis e limites do mapa.
-   Interação com E.
-   Animação/estado de movimento quando houver sprites.
-   Lanterna controlada de forma consistente.
-   Energia da lanterna com consumo e recarga/recursos, conforme
    balanceamento.
-   Feedback visual para interação e dano, se houver dano.

Não permita que o jogador atravesse obstáculos ou saia do mapa.

### C. Lanterna e regra de observação

A lanterna deve ter direção controlada de forma consistente com a
implementação existente (mouse ou direção de movimento, escolha uma e
documente). Deve possuir alcance e cone definidos.

The Watcher só fica em estado `OBSERVED` se: - A lanterna estiver ligada
e tiver energia; - A entidade estiver dentro do alcance; - A entidade
estiver dentro do ângulo do cone; - Não houver obstáculo bloqueando a
linha de visão.

Não use proximidade como substituto da verificação. A entidade não pode
congelar quando está atrás de uma parede ou fora do cone.

### D. IA de The Watcher

Implemente uma máquina de estados: - `OBSERVED`: fica imóvel ou recua,
sem avançar contra o jogador. - `STALKING`: aproxima-se lentamente
quando não observada. - `HUNTING`: persegue o jogador após detecção ou
condição clara. - `SEARCHING`: procura a última posição conhecida após
perder o jogador. - `RETREATING`: recua em condições específicas.

A IA deve: - Respeitar colisões e obstáculos. - Usar velocidade
independente de FPS. - Não teleportar para perto do jogador. - Ter
transições estáveis, sem alternar estados várias vezes no mesmo frame. -
Escalar a agressividade por estágio e maldição. - Dar pistas suficientes
para que a perseguição não pareça injusta.

### E. Maldições

Sortear uma maldição no início de cada nova partida. Mostrar nome e
descrição antes de iniciar, no HUD e no resumo final.

Implementar efeitos reais: 1. BLACKOUT --- lanterna falha por períodos
breves e controlados, com aviso. 2. FRAGILE LIGHT --- consumo de bateria
maior. 3. LOST PATH --- mapa/minimapa indisponível. 4. WHISPERING ---
sons falsos raros, com localização e intensidade discretas; nunca ruído
contínuo. 5. THE WATCHER --- entidade mais rápida enquanto não
observada.

O sorteio deve evitar repetir sempre a mesma maldição de forma
previsível, mas não precisa criar um sistema complexo. Deve ser possível
testar cada maldição isoladamente em modo de depuração, sem expor esse
modo na build final.

### F. Três estágios

Implemente três estágios realmente diferentes:

**Stage 1 --- THE AWAKENING** - Floresta e cabana. - Tutorial orgânico
de controles e lanterna. - Encontrar a chave e alcançar a cabana. -
Entidade lenta e introdução da mecânica de observação.

**Stage 2 --- THE FORGOTTEN** - Vilarejo abandonado, ruas, casas e rotas
alternativas. - Encontrar três fragmentos de fotografia. - Mais pistas,
recursos e maior agressividade da entidade.

**Stage 3 --- THE HOLLOW** - Coração da floresta e santuário
subterrâneo. - Descobrir a origem da entidade. - Caminhos mais complexos
e entidade mais perigosa. - Objetivo final que conclua a história.

Cada estágio precisa ter condição de conclusão e transição real. Garanta
que os objetivos sejam alcançáveis e que os caminhos não fiquem
bloqueados por geração aleatória.

### G. Itens e interação

-   Chave, fragmentos de fotografia, baterias e itens narrativos.
-   Feedback quando o jogador se aproxima de um objeto interativo.
-   Pressionar E para interagir/coletar.
-   Impedir coleta duplicada indevida.
-   Atualizar objetivo e inventário.
-   Verificar pré-requisitos antes de abrir portas ou concluir estágio.

### H. HUD

Mostrar somente dados reais: - Energia da lanterna. - Vida/condição,
caso exista sistema de dano. - Estágio atual. - Objetivo. - Maldição
ativa. - Indicador contextual de interação. - Mapa apenas quando
permitido.

### I. Pontuação e fluxo final

Calcular pontuação a partir de dados reais, por exemplo: - Objetivos
concluídos. - Pistas coletadas. - Tempo sobrevivido. - Recursos
restantes. - Penalidades por dano, se houver.

Implementar Game Over e vitória. Mostrar resumo com pontuação, tempo,
pistas e maldição. Salvar recorde usando Preferences do libGDX, se
compatível.

### J. Menu, pausa e configurações

-   NEW GAME.
-   CONTINUE apenas se houver progresso válido.
-   SETTINGS.
-   EXIT.
-   Pause/Resume.
-   Configurações de áudio e controles.
-   Navegação por mouse e teclado, se viável.
-   Nenhum botão pode ser decorativo: todos precisam executar sua ação.

## Critérios de aceitação desta fase

-   Build executa sem erros.
-   Menu inicia uma partida.
-   Jogador se move e colide corretamente.
-   Lanterna e energia funcionam.
-   A entidade respeita cone, alcance e obstáculos.
-   As cinco maldições alteram a jogabilidade.
-   Os três estágios podem ser concluídos.
-   Interações e itens funcionam.
-   Pausa, Game Over, vitória e pontuação funcionam.
-   Nenhum estado depende de temporizador bloqueante.
-   Não existem botões falsos ou contadores inventados.

## Entrega

Ao concluir: 1. Liste arquivos criados/alterados. 2. Explique a
arquitetura implementada. 3. Informe o comando Gradle executado e
resultado real. 4. Liste testes manuais realizados. 5. Liste limitações
ainda existentes. 6. Não comece a fase visual ou musical sem eu
solicitar.

------------------------------------------------------------------------

# PROMPT 2 --- DIREÇÃO VISUAL, ARTE E INTERFACE

Você é o diretor de arte e responsável pela implementação visual de
**THE HOLLOW WOODS**. Leia `AGENTS.md` e `SKILLS.md` antes de alterar
qualquer arquivo. Use o gameplay e as interfaces já implementadas como
base. Não modifique regras centrais de gameplay sem autorização.

## Objetivo

Substituir a aparência provisória por uma identidade visual coerente,
original e legível, inspirada no terror psicológico 2D top-down. O
resultado deve parecer um jogo indie de horror cuidadosamente dirigido,
não uma tela preta com riscos de chuva e pequenos elementos quase
invisíveis.

## Identidade artística

**Nome:** THE HOLLOW WOODS\
**Gênero visual:** horror psicológico gótico, pixel art detalhada com
acabamento cinematográfico.\
**Perspectiva:** top-down/3⁄4 top-down consistente.\
**Paleta:** - Preto carvão para áreas profundas. - Verde-musgo e verde
muito escuro para vegetação. - Cinza azulado para neblina e distância. -
Marrom envelhecido para madeira, terra e construções. - Âmbar suave para
lanternas e janelas. - Vermelho escuro apenas em alertas, ameaça e
detalhes narrativos.

**Atmosfera:** floresta antiga, úmida, silenciosa, isolada, com
construções deterioradas, árvores retorcidas e sensação de presença
observando o jogador.

## Referências fornecidas

Use as imagens de referência anexadas a esta conversa como orientação de
atmosfera, composição, paleta e hierarquia. Não copie diretamente
personagens, logos, layouts ou assets de jogos existentes. A identidade
final deve ser própria.

## Regras de legibilidade --- prioridade máxima

A versão anterior ficou escura demais. Corrija isso.

-   Nunca deixe a tela quase toda preta durante o gameplay normal.
-   O chão caminhável precisa ser distinguível da vegetação e dos
    obstáculos.
-   O personagem precisa ser identificável em qualquer área jogável.
-   Itens e objetos interativos precisam de leitura visual própria.
-   A lanterna deve destacar o ambiente, mas não pode ser a única
    maneira de enxergar qualquer coisa.
-   Mantenha uma iluminação ambiente mínima.
-   Use contraste suficiente entre personagem, chão e cenário.
-   Evite áreas pretas sem detalhe que escondam perigos injustamente.
-   Neblina e sombras não podem cobrir objetivos ou informação
    essencial.

## Iluminação e lanterna

A lanterna é parte da mecânica: - Crie cone de luz suave e
direcionado. - O centro deve ser mais claro e as bordas devem
desaparecer gradualmente. - A luz deve revelar textura do terreno e
detalhes do cenário. - Evite um círculo duro ou um triângulo chapado. -
A iluminação deve respeitar paredes e obstáculos quando tecnicamente
viável. - Não use brilho excessivo nem branco puro como padrão. - A área
ao redor do jogador deve manter leitura mínima.

Não altere a regra lógica de observação de The Watcher. A apresentação
visual deve refletir o estado lógico real da entidade.

## Cenários por estágio

**THE AWAKENING** - Floresta fechada, trilhas de terra, árvores
retorcidas, cercas quebradas, pedras, folhas, cabana distante com janela
âmbar. - Poucos elementos, leitura clara do caminho. - Sensação de
descoberta e ameaça distante.

**THE FORGOTTEN** - Vilarejo abandonado, casas de madeira, ruas
estreitas, postes, poços, cercas e interiores parcialmente acessíveis. -
Composição mais densa, mas com caminhos legíveis. - Fotografias, sinais
de ocupação antiga e pistas ambientais.

**THE HOLLOW** - Santuário subterrâneo, raízes atravessando estruturas,
pedra úmida, símbolos antigos, arquitetura ritualística. - Contraste
maior e formas visuais mais estranhas. - O cenário deve sugerir a origem
da entidade sem explicar tudo.

## Personagens e criaturas

-   Protagonista com silhueta clara, mochila e lanterna.
-   Animações coerentes de idle, caminhada e interação, se houver
    sprites disponíveis.
-   The Watcher deve ser reconhecível por uma silhueta humanoide
    anormal, proporções perturbadoras e movimento controlado.
-   A criatura não deve se confundir com árvores ou decoração.
-   Não usar círculos ou retângulos como arte final.
-   Se ainda não houver sprite, use placeholder temporário identificável
    e descreva o asset necessário.

## Chuva, partículas e neblina

A chuva atual ficou exagerada e parece riscos brancos atravessando a
tela. Corrija: - Partículas pequenas, finas, pouco brilhantes e com
densidade moderada. - Não use riscos brancos grossos e diagonais por
toda a tela. - Chuva não deve reduzir contraste do cenário. - Neblina
deve ser localizada e sutil, com movimento lento. - Folhas e partículas
devem ser raras e discretas. - Efeitos devem respeitar limites de
desempenho.

## Interface

Desenvolva uma UI gótica minimalista: - Tipografia serifada ou display
com personalidade para títulos. - Fonte simples e legível para HUD. -
Texturas discretas de papel envelhecido, metal escuro ou madeira. -
Bordas finas, ícones consistentes e vermelho escuro para alertas. -
Botões com estados normal, hover, pressionado e desabilitado. - Não
sobrecarregue a tela com molduras ou ornamentos.

### Menu principal

-   Logo/título THE HOLLOW WOODS.
-   Fundo com floresta, lua encoberta ou cabana.
-   NEW GAME, CONTINUE, SETTINGS e EXIT.
-   Menu com hierarquia visual clara.
-   Fundo não pode prejudicar a leitura dos botões.

### HUD

-   Bateria/lanterna.
-   Vida, se houver.
-   Estágio.
-   Maldição.
-   Objetivo.
-   Interação contextual.
-   Minimapa somente quando permitido.

### Game Over

Texto principal: **YOU HAVE BEEN FOUND**. Mostrar estatísticas reais,
maldição e opções RETRY / MAIN MENU.

### Vitória

Tela de encerramento com conclusão narrativa, estatísticas e retorno ao
menu.

## Organização e implementação

-   Inspecione os assets e sistemas de renderização existentes.
-   Reutilize o pipeline de assets já criado.
-   Centralize dimensões, escala, cores e fontes.
-   Não instancie texturas ou renderizadores a cada frame.
-   Carregue assets uma vez e descarregue corretamente.
-   Garanta proporções adequadas ao redimensionar a janela.
-   Não use URLs ou downloads em runtime.
-   Registre assets, origem e licença.
-   Não altere a lógica de gameplay para esconder problemas visuais.

## Teste visual obrigatório

Execute o jogo e capture/inspecione: 1. Menu principal. 2. Gameplay do
estágio 1. 3. Gameplay com a lanterna ligada e desligada. 4. Entidade
dentro e fora do cone. 5. Estágio 2. 6. Estágio 3. 7. HUD em resolução
desktop. 8. Game Over e vitória.

Para cada captura, avalie: personagem legível? caminho visível? objetos
identificáveis? HUD claro? chuva discreta? luz sem estourar? Se a
resposta for não, corrija antes de concluir.

## Entrega

Liste assets e arquivos alterados, decisões de arte, testes visuais
realizados e limitações. Não comece a fase de música.

------------------------------------------------------------------------

# PROMPT 3 --- MÚSICA, AMBIÊNCIA E EFEITOS SONOROS

Você é o diretor de áudio e responsável pela implementação de música e
sound design de **THE HOLLOW WOODS**. Leia `AGENTS.md` e `SKILLS.md`
integralmente. Esta fase é crítica: a versão anterior apresentou chiado
muito alto, som estourado e áudio que tornava o jogo incompreensível.

## Objetivo

Criar uma identidade sonora original, minimalista, atmosférica e
confortável. O áudio deve aumentar a tensão e comunicar eventos de
gameplay. **Silêncio é uma ferramenta de design.** Não tente preencher
cada segundo com som.

## Proibições absolutas

-   Não usar chiado constante.
-   Não usar ruído branco ou rosa como cama sonora contínua.
-   Não usar estática alta de rádio.
-   Não empilhar drones, vento, chuva e ruídos em volume elevado.
-   Não usar clipping ou distorção como padrão.
-   Não usar subgrave contínuo e forte.
-   Não reproduzir sons repetidamente a cada frame.
-   Não criar loops duplicados ao trocar de tela.
-   Não usar sons agudos constantes que causem fadiga.
-   Não usar jumpscares sonoros aleatórios em volume máximo.
-   Não deixar áudio tocando após sair da tela ou fechar o jogo.

Se os assets disponíveis forem ruins, prefira silêncio e poucos efeitos
limpos a uma trilha sonora improvisada e ruidosa.

## Direção musical

A música deve ser: - Original. - Lenta, minimalista e melancólica. -
Psicologicamente inquietante, sem soar como trilha épica. - Discreta e
com bastante espaço. - Construída com notas espaçadas, texturas tonais
suaves, ressonâncias controladas e drones musicais de baixa
intensidade. - Variável por estágio e intensidade de ameaça.

Evite copiar trilhas, melodias, timbres distintivos ou temas de
jogos/filmes existentes.

### Identidade por estágio

-   **THE AWAKENING:** quase silêncio, notas distantes e sensação de
    isolamento.
-   **THE FORGOTTEN:** harmonia mais instável, timbres orgânicos e
    pequenos motivos fragmentados.
-   **THE HOLLOW:** tensão mais densa, mas ainda com espaço para efeitos
    e silêncio.

### Estados dinâmicos

-   Exploração: música quase imperceptível.
-   Pistas narrativas: pequena mudança musical, sem bloquear a
    fala/radio.
-   Entidade próxima: tensão sutil e progressiva.
-   Perseguição: camada rítmica controlada, sem volume abrupto.
-   Pausa: áudio de gameplay interrompido ou reduzido.
-   Game Over/vitória: transição curta e específica.

Não faça a música reagir de forma exagerada a cada pequeno movimento.

## Paisagem sonora

Use poucas camadas e eventos espaçados: - Vento leve entre árvores. -
Folhas e galhos ocasionais. - Chuva distante e suave, se fizer sentido
para a cena. - Passos do protagonista sincronizados com movimento. -
Interações com objetos. - Rádio com voz inteligível e filtragem
discreta. - Sinais da entidade próprios e reconhecíveis. - Sons de
perseguição usados apenas quando a perseguição existe.

A chuva não deve ser um ruído de banda larga constante. Prefira gravação
limpa e discreta, com transientes controlados.

## Mixagem inicial obrigatória

Comece com estes multiplicadores conservadores: - Master: `0.40`. -
Música: `0.18`. - Ambiente: `0.14`. - Efeitos: `0.25`.

O volume final de cada canal deve considerar o master e o volume
individual. Exponha controles independentes na tela de configurações.

Esses números são valores iniciais de software, não substituem
normalização e audição real. Ajuste para evitar clipping e garantir
inteligibilidade.

## Implementação técnica

-   Inspecione `AudioManager` ou equivalente antes de criar outro.
-   Centralize carregamento e gerenciamento de áudio.
-   Inicie loops uma única vez por estado.
-   Guarde IDs de reprodução quando a API permitir.
-   Pare loops ao sair do estado/tela.
-   Use fade-in/fade-out nas transições.
-   Limite instâncias simultâneas de passos e eventos.
-   Não execute `play()` dentro do `render()` a cada frame.
-   Não recarregue assets de áudio durante gameplay.
-   Respeite pause/resume e `dispose()`.
-   Se o libGDX/ambiente tiver limitações de codecs, use formatos
    compatíveis e verifique a documentação da versão instalada.
-   Não baixe sons automaticamente em runtime.
-   Não adicione bibliotecas sem necessidade.

## Rádio e voz

A voz da irmã deve ser inteligível. O efeito de rádio deve ser leve: -
Redução moderada de banda. - Pequeno ruído apenas durante início/fim da
transmissão, se houver asset apropriado. - Voz claramente acima do
ambiente. - Sem estática permanente. - Sem volume abrupto. - Mensagens
curtas e espaçadas.

Se não houver arquivos de voz, não sintetize ruído fingindo que é fala.
Implemente o gatilho e deixe o asset claramente documentado como
pendente.

## Testes auditivos obrigatórios

Faça e relate estes testes: 1. Iniciar o jogo com configurações padrão.
2. Permanecer parado por 30 segundos. 3. Caminhar por 30 segundos. 4.
Interagir com itens. 5. Aproximar a entidade e observar os sinais. 6.
Entrar em perseguição. 7. Acionar WHISPERING. 8. Pausar e retomar. 9.
Trocar de estágio. 10. Sair do jogo e confirmar que nenhum loop
permanece.

Verifique: - Não há chiado contínuo. - Não há estalos ou clipping. - Não
há loops duplicados. - Passos não se acumulam. - A voz é inteligível. -
Efeitos importantes são audíveis sem elevar demais o volume. - Sliders e
mute funcionam. - Ambiente não encobre gameplay.

Se não for possível ouvir o resultado no ambiente de desenvolvimento,
informe isso e forneça os testes que ainda precisam ser feitos
manualmente. Não afirme que a mixagem foi aprovada sem audição.

## Entrega

Informe os arquivos e assets usados, os valores padrão, o funcionamento
dos canais e testes realmente realizados. Não comece a reescrever a
história.

------------------------------------------------------------------------

# PROMPT 4 --- HISTÓRIA, PERSONAGENS E NARRATIVA

Você é o roteirista e designer narrativo de **THE HOLLOW WOODS**. Leia
`AGENTS.md` e `SKILLS.md` antes de escrever ou alterar conteúdo. A
narrativa precisa se integrar aos três estágios, aos objetivos, à
entidade, às maldições e ao design de áudio/visual.

## Objetivo

Criar uma história original de terror psicológico, misteriosa e
emocional, que possa ser contada durante a exploração sem transformar o
jogo em uma sequência de longas caixas de texto.

## Premissa aprovada

O protagonista é **Elias**. Ele desperta em uma floresta desconhecida
durante a madrugada. Não se lembra claramente de como chegou ali. Um
rádio portátil começa a receber transmissões com a voz de sua irmã
desaparecida. A voz parece guiá-lo para dentro da floresta.

A floresta não se comporta como um lugar normal: caminhos parecem mudar
quando Elias deixa de observá-los, objetos aparecem em lugares
improváveis e a sensação de estar sendo seguido aumenta. Uma entidade
conhecida como **The Watcher** aproxima-se quando não está sendo vista e
congela quando é iluminada e observada.

A história deve preservar dúvida: a voz é realmente a irmã? A floresta
está alterando o espaço, a memória ou a percepção de Elias? O jogador
deve montar parte das respostas por meio de pistas.

## Tom e princípios

-   Terror psicológico, melancólico e inquietante.
-   Mistério progressivo.
-   Pouca exposição direta.
-   Diálogos curtos e naturais.
-   Nada de explicações excessivas.
-   Não transformar a entidade em um monstro genérico que apenas
    persegue.
-   Evitar clichês gratuitos, sustos sem consequência e textos longos.
-   O medo deve nascer de detalhes, contradições, ausência e descoberta.
-   A história precisa ter começo, desenvolvimento e conclusão
    compreensível, mesmo que preserve ambiguidade.

## Estrutura narrativa

### Prólogo --- The Call

Elias desperta na floresta. O rádio transmite uma mensagem curta da
irmã. O jogador recebe o primeiro objetivo e percebe que a floresta
parece reagir à ausência de luz.

### Stage 1 --- THE AWAKENING

Local: floresta e cabana. Objetivo de gameplay: encontrar a chave e
alcançar a cabana.

Função narrativa: - Apresentar Elias e a voz no rádio. - Sugerir que
alguém esteve ali recentemente. - Introduzir a entidade sem explicar sua
origem. - Apresentar a primeira contradição espacial ou temporal. -
Terminar com uma descoberta que justifique seguir para o vilarejo.

### Stage 2 --- THE FORGOTTEN

Local: vilarejo abandonado. Objetivo de gameplay: encontrar três
fragmentos de fotografia.

Função narrativa: - Revelar ligações entre a irmã, o vilarejo e
desaparecimentos antigos. - Usar fotografias, bilhetes, objetos e
gravações para construir contexto. - Mostrar que moradores conheciam uma
regra sobre a floresta. - Aumentar a dúvida sobre a confiabilidade das
transmissões. - Terminar com a revelação de que a origem do fenômeno
está no coração da floresta.

### Stage 3 --- THE HOLLOW

Local: santuário subterrâneo/coração da floresta. Objetivo de gameplay:
alcançar o núcleo do fenômeno e concluir o conflito.

Função narrativa: - Conectar as pistas anteriores. - Revelar a relação
entre a entidade e o desaparecimento. - Dar significado à regra de
observação. - Conduzir a uma decisão ou ação final jogável. - Entregar
uma conclusão emocional e coerente.

## Personagens

### Elias

-   Protagonista controlado pelo jogador.
-   Reservado, confuso e determinado.
-   Não deve falar constantemente.
-   Reações curtas podem aparecer em momentos importantes.

### A irmã

-   Voz transmitida pelo rádio.
-   Afetuosa, mas progressivamente estranha.
-   Mensagens curtas, com pausas e subtexto.
-   Não revelar cedo se é realmente ela.

### The Watcher

-   Entidade ligada à floresta e ao ato de observar.
-   Sua regra de movimento deve ter significado narrativo.
-   Não deve falar de forma convencional.
-   Sua presença é comunicada por silhueta, comportamento, ambiente e
    sinais sonoros.

## Pistas e documentos

Crie uma lista de: - Bilhetes curtos. - Fotografias. - Registros de
moradores. - Símbolos recorrentes. - Mensagens de rádio. - Objetos
ambientais com significado.

Cada pista deve: 1. Ter texto curto. 2. Conter informação nova ou mudar
a interpretação de algo. 3. Estar ligada a um local específico. 4. Ser
opcional quando possível, sem bloquear a compreensão básica. 5. Evitar
repetir informação já entregue.

Os três fragmentos de fotografia do estágio 2 precisam formar uma imagem
ou revelar uma relação narrativa concreta quando reunidos.

## Maldições e narrativa

As maldições são fenômenos que afetam a partida, mas não devem
contradizer a história: - BLACKOUT: a floresta interfere na luz. -
FRAGILE LIGHT: a energia da lanterna se deteriora. - LOST PATH: a
orientação espacial deixa de ser confiável. - WHISPERING: ecos e
imitações de vozes aparecem; os eventos falsos não devem ser confundidos
injustamente com objetivos essenciais. - THE WATCHER: a entidade reage
de forma mais agressiva quando não é observada.

A maldição deve ter pequenas variações narrativas quando apropriado, mas
não precisa gerar uma história totalmente diferente por partida.

## Escrita de diálogos

Escreva as mensagens em inglês dentro do jogo, com linguagem natural,
curta e apropriada para terror. Mantenha documentação e explicações
técnicas em português.

Evite: - Exclamações excessivas. - Frases genéricas como "I am coming
for you" sem contexto. - Explicações longas em rádio. - Mensagens que
entregam a solução antes do jogador explorar.

Prefira frases curtas, ambíguas e emocionalmente específicas.

## Entregáveis narrativos

Produza: 1. Sinopse oficial em um parágrafo. 2. História completa com
começo, meio e fim. 3. Biografia breve dos personagens. 4. Sequência de
eventos por estágio. 5. Objetivos narrativos associados aos objetivos
jogáveis. 6. Lista de pistas e textos dos documentos. 7. Mensagens de
rádio em ordem de descoberta. 8. Falas ambientais ou reações curtas de
Elias. 9. Explicação interna da origem e das regras de The Watcher. 10.
Final principal e, se compatível com o escopo, uma variação opcional
baseada em pistas encontradas.

## Integração com o jogo existente

Antes de editar: - Leia como objetivos, diálogos, itens e transições são
implementados. - Não invente classes ou arquivos sem inspecionar. - Não
altere mecânicas de gameplay para acomodar uma ideia narrativa sem
explicar. - Conecte cada objetivo narrativo a um gatilho real. - Garanta
que documentos e mensagens possam ser encontrados/acionados. - Não
coloque textos longos sobre a tela durante perseguições.

## Critérios de aceitação

-   Os três estágios formam uma progressão narrativa.
-   O mistério principal tem respostas coerentes.
-   A voz da irmã tem evolução perceptível.
-   The Watcher tem origem e regras internas consistentes.
-   As pistas acrescentam informação, não repetem.
-   O final conclui o arco de Elias.
-   A narrativa não contradiz os sistemas de maldição, observação e
    lanterna.
-   O jogador consegue compreender o objetivo imediato sem ler todos os
    documentos.

## Entrega

Apresente primeiro a bíblia narrativa para aprovação. Depois de
aprovada, integre os textos e gatilhos ao jogo. Não altere código de
áudio ou arte nesta fase, exceto para conectar eventos já existentes.
