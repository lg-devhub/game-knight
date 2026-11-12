# SKILLS.md --- THE HOLLOW WOODS

Este arquivo define os procedimentos especializados que o agente deve
seguir ao trabalhar no projeto. Use somente as skills relevantes para a
tarefa atual. Não tente executar todas simultaneamente.

------------------------------------------------------------------------

## Skill 1 --- Reconhecimento do projeto

**Objetivo:** entender a base existente sem quebrar configurações.

Procedimento: 1. Liste a estrutura de diretórios. 2. Identifique os
módulos Gradle e o módulo desktop. 3. Leia os arquivos de build e o
ponto de entrada. 4. Identifique as versões reais de Java, Gradle e
libGDX. 5. Localize telas, entidades, assets e sistemas existentes. 6.
Execute a compilação inicial. 7. Registre erros que já existiam antes
das alterações.

Entrega: resumo da arquitetura atual, riscos, comando de build e plano
incremental. Não comece uma reescrita antes dessa inspeção.

## Skill 2 --- Arquitetura de gameplay

**Objetivo:** manter lógica de jogo independente de desenho e interface.

Padrão recomendado: - `update(delta)` atualiza estados e simulação. -
`render(batch)` desenha o estado atual. - `handleInput()` traduz
controles em ações. - Sistemas recebem referências explícitas ou
dependências simples. - Entidades não devem carregar responsabilidades
de telas, áudio global ou salvamento.

Use enums para estados finitos. Use classes pequenas com
responsabilidades claras. Evite classes gigantes que concentram todo o
jogo.

## Skill 3 --- Movimento, câmera e colisão

**Objetivo:** movimento previsível e sem atravessar objetos.

Procedimento: 1. Ler entrada do teclado. 2. Normalizar vetor diagonal
para não acelerar em diagonal. 3. Multiplicar velocidade por `delta`. 4.
Resolver colisão nos eixos X e Y separadamente quando adequado. 5.
Restringir jogador aos limites válidos do mapa. 6. Fazer a câmera
acompanhar o jogador com suavização moderada. 7. Aplicar viewport para
preservar proporções em diferentes janelas.

Controles iniciais sugeridos: - WASD ou setas: movimentar. - Shift:
correr, se a mecânica de stamina estiver implementada. - F:
ligar/desligar lanterna, se aplicável. - E: interagir. - M: mapa, quando
permitido pela maldição. - ESC: pausar.

Não implemente controles que não tenham comportamento real.

## Skill 4 --- Lanterna e observação da entidade

**Objetivo:** fazer a luz ser uma mecânica funcional.

A observação precisa validar: 1. Lanterna ativa e com energia. 2.
Entidade dentro do alcance. 3. Entidade dentro do ângulo do cone. 4.
Linha de visão livre de obstáculos.

Para o cone: - Calcule o vetor do jogador até a entidade. - Compare o
ângulo com a direção da lanterna. - Use distância ao quadrado para
evitar raiz desnecessária em verificações frequentes. - Faça raycast ou
teste equivalente de obstáculos para validar linha de visão.

A entidade só entra em `OBSERVED` se todas as condições forem
verdadeiras. Caso contrário, pode entrar em `STALKING`, `SEARCHING` ou
`HUNTING` conforme a percepção e o estágio.

Teste obrigatório: posicione a entidade à frente, atrás de uma parede,
fora do cone e além do alcance. Verifique cada caso separadamente.

## Skill 5 --- IA da entidade

**Objetivo:** criar ameaça legível e justa.

Máquina de estados recomendada: - `OBSERVED`: congela ou recua; não
causa dano. - `STALKING`: aproxima-se de forma lenta quando não
observada. - `HUNTING`: persegue quando detecta o jogador ou uma
condição explícita é atingida. - `SEARCHING`: procura a última posição
conhecida. - `RETREATING`: recua após condição de segurança ou evento
narrativo.

Regras: - Use temporizadores com `delta`, não relógios bloqueantes. -
Não teletransporte a entidade para perto do jogador. - Não atravesse
obstáculos. - Não altere estado várias vezes no mesmo frame por
condições conflitantes. - Dê pistas de áudio/visuais antes de uma
perseguição sempre que possível. - Ajuste velocidade e agressividade por
estágio e maldição. - Garanta que o jogador tenha alguma resposta
possível: iluminar, fugir, esconder-se ou usar rota alternativa.

## Skill 6 --- Sistema de maldições

**Objetivo:** implementar maldições com efeitos reais e testáveis.

Cada maldição deve possuir: - Identificador estável. - Nome e
descrição. - Método de ativação/inicialização. - Efeito aplicado em
gameplay. - Método de encerramento/reset. - Indicação na interface.

Evite espalhar verificações de maldição por todo o código. Centralize
regras no `CurseManager` ou em estratégias específicas.

Testes: - BLACKOUT: falha ocorre com frequência e duração controladas,
com aviso. - FRAGILE LIGHT: consumo é maior que o padrão e mensurável. -
LOST PATH: mapa fica indisponível, mas objetivos continuam possíveis. -
WHISPERING: sons falsos são ocasionais, espacialmente coerentes e não
confundem sinais essenciais. - THE WATCHER: velocidade fora da
observação aumenta em relação ao padrão.

Nunca use volume alto ou estática contínua para diferenciar WHISPERING.

## Skill 7 --- Construção de estágios e objetivos

**Objetivo:** garantir progressão verdadeira.

Cada estágio deve definir: - Layout e limites. - Posição inicial do
jogador. - Obstáculos e rotas. - Objetivos e itens. - Pontos de
interação. - Regras de dificuldade. - Condição de conclusão. - Próximo
estágio ou final.

Não avance de estágio apenas por temporizador. O jogador precisa
concluir o objetivo. Evite mapas aleatórios sem validação de caminhos.
Se usar geração procedural, garanta conectividade entre spawn, itens e
saída.

## Skill 8 --- HUD e interface

**Objetivo:** comunicar informação sem poluir a tela.

HUD essencial: - Vida/condição do jogador, se houver dano. - Energia da
lanterna. - Estágio atual. - Objetivo atual. - Maldição ativa. -
Indicador de interação contextual. - Mapa apenas quando permitido.

Boas práticas: - Contraste consistente. - Tipografia legível. - Margens
seguras. - Não colocar texto sobre fundos sem contraste. - Não usar
ícones sem significado conhecido. - Não mostrar contadores que não
estejam ligados a dados reais. - Atualizar textos quando o estado muda,
sem recriar objetos de UI a cada frame.

## Skill 9 --- Direção de arte e renderização

**Objetivo:** construir horror legível.

Paleta: - Preto carvão e verde profundo para sombras. - Verde-musgo e
cinza azulado para ambiente. - Marrom envelhecido para madeira e
terra. - Âmbar quente para a lanterna e pequenas fontes de luz. -
Vermelho escuro apenas para alertas, ameaça e acentos narrativos.

Hierarquia visual: 1. Jogador e ameaças. 2. Rotas caminháveis e
obstáculos. 3. Itens e pontos interativos. 4. Decoração e partículas.

A cena não pode ser quase toda preta. A iluminação deve aumentar a
tensão sem ocultar o chão, o jogador ou o caminho. A chuva deve ser
discreta e não pode parecer riscos brancos enormes. Use efeitos em
camadas com intensidade controlada.

Não use placeholders geométricos como arte final. Se faltarem sprites,
implemente primeiro uma versão simples, coerente e temporária,
identificando-a como placeholder; não a apresente como arte concluída.

## Skill 10 --- Áudio seguro e mixagem

**Objetivo:** evitar chiado, clipping, loops duplicados e fadiga
auditiva.

### Hierarquia sonora

-   Música: composição ambiente discreta, com poucos elementos.
-   Ambiente: vento leve, folhas, chuva distante e sons naturais
    espaçados.
-   Efeitos: passos, interação, rádio, pistas da entidade e perseguição.
-   Silêncio: recurso intencional para criar tensão.

### Mixagem inicial

Os valores abaixo são multiplicadores de volume, não garantias de
loudness: - Master: 0.40. - Música: 0.18. - Ambiente: 0.14. - Efeitos:
0.25.

Volume final de cada canal = master × canal × volume do evento.

### Regras técnicas

-   Inicie loops uma única vez ao entrar no estado/tela apropriado.
-   Guarde identificadores de sons/loops e interrompa-os ao sair.
-   Não chame `play()` continuamente dentro de `render()`.
-   Use limites de instâncias para sons repetitivos.
-   Aplique fades ao trocar de tela ou estado.
-   Não reproduza áudio de ambiente em volume máximo.
-   Não crie ruído branco como substituto de vento.
-   Evite som de chuva com transientes agudos constantes.
-   Não aplique clipping deliberado.
-   Sons de susto devem ser curtos, controlados e precedidos por
    preparação quando possível.
-   Pausa deve pausar ou atenuar sons de gameplay.
-   Dispose de recursos de áudio corretamente.

### Teste auditivo

Faça um teste de 30 segundos em silêncio de gameplay, depois movimento,
interação, entidade, pausa e retorno. Se o som de fundo chama mais
atenção do que os eventos importantes, reduza-o. Se houver chiado
constante, remova a camada responsável em vez de tentar mascará-la com
outra.

## Skill 11 --- Música original e identidade musical

**Objetivo:** música original de horror psicológico sem imitar trilhas
existentes.

Direção: - Minimalista, lenta, melancólica e inquietante. - Texturas
tonais discretas, drones musicais suaves, notas espaçadas e ressonâncias
controladas. - A música deve deixar espaço para ambiente e efeitos. -
Não usar ruído estático contínuo. - Não usar subgrave alto constante. -
Não usar jumpscares musicais repetitivos. - Criar variações por estágio
e intensidade de ameaça. - Permitir trechos de silêncio.

A música não deve competir com a entidade ou com pistas de gameplay.
Evite melodias excessivamente presentes.

## Skill 12 --- Narrativa ambiental

**Objetivo:** contar a história sem interromper a exploração.

Use: - Objetos com contexto. - Bilhetes curtos. - Fotografias e
símbolos. - Mensagens de rádio breves. - Mudanças no ambiente. - Falas
raras e significativas.

Cada pista deve responder algo ou levantar uma pergunta concreta. Evite
longos blocos de texto durante a exploração. A narrativa deve respeitar
o ritmo do jogador e não explicar todo o mistério cedo demais.

## Skill 13 --- Pontuação, salvamento e fluxo

**Objetivo:** manter estatísticas confiáveis.

Registre apenas dados reais: - Tempo de sobrevivência. - Objetivos
concluídos. - Pistas encontradas. - Recursos restantes. - Dano recebido,
se existir. - Maldição sorteada. - Estágio alcançado.

Use `Preferences` do libGDX para recordes e configurações simples, se
compatível com a versão instalada. Não grave progresso incompleto como
se fosse uma campanha concluída. Trate dados ausentes com valores
padrão.

## Skill 14 --- Testes e diagnóstico

**Objetivo:** testar comportamento, não apenas compilar.

Checklist: - Build limpo. - Inicialização. - Menu e navegação. -
Movimento em quatro direções e diagonal. - Colisões. - Interação. -
Lanterna e bateria. - Linha de visão da entidade. - Cada estado da IA. -
Cada maldição. - Objetivos e transição de estágios. - Pausa/retomada. -
Game Over. - Vitória. - Pontuação e recorde. - Sliders de áudio e
mute. - Teste auditivo de 30 segundos. - Teste visual em resolução
desktop e janela redimensionada.

Quando não houver teste automatizado possível, faça teste manual e
registre o procedimento. Não invente resultados.

## Skill 15 --- Uso eficiente do Gemini Flash

**Objetivo:** reduzir erros por contexto e tarefas grandes demais.

-   Execute um prompt por vez.
-   Peça primeiro análise e plano; depois implementação.
-   Limite cada tarefa a um subsistema ou conjunto pequeno de arquivos.
-   Solicite que o agente leia os arquivos antes de editar.
-   Peça resumo dos arquivos alterados.
-   Compile após cada fase.
-   Não peça regeneração de todo o projeto.
-   Se o contexto ficar longo, peça um resumo técnico de continuidade
    com estado real do código, erros, testes e próximos passos.
-   Não aceite afirmações vagas como "tudo pronto"; exija evidência de
    build e teste.
-   Em caso de falha, peça diagnóstico antes de uma nova implementação.
