/*
 * Girias de jogos e esportes eletronicos.
 *
 * COMO ESCREVER A EXPLICACAO SIMPLES
 *
 * Quem le e a Marlene, 68 anos, que nunca jogou nada no computador. Uma ou
 * duas frases curtas, sem jargao, e sem pressupor que ela saiba o que e
 * partida ranqueada, lobby, respawn ou meta. Se precisar citar, explique em
 * poucas palavras dentro da propria frase.
 *
 * A explicacao detalhada e para quem quer a origem. Pode ser mais longa e
 * pode usar termos do meio.
 *
 * ACENTUACAO: os comentarios deste arquivo vao sem acento, como o resto do
 * codigo, mas TODO texto que o usuario le (simples, detalhada, formal) e
 * acentuado corretamente. E texto de dicionario, nao identificador.
 *
 * TERMOS QUE JA EXISTEM: "gg" ja veio no seed inicial (V3). Ele aparece aqui
 * so com o sentido novo. Repetir o sentido antigo criaria duas definicoes
 * identicas no mesmo verbete.
 */
export default [
  { termo: "gg", idioma: "en", categorias: ["gaming"], sentidos: [
    { simples: "Fora do jogo, quer dizer que algo acabou ou deu errado de vez.",
      detalhada: "Uso ampliado da sigla de fim de partida. Um \"gg\" solto depois de uma notícia ruim equivale a \"acabou\", \"era isso mesmo\".",
      formal: "acabou" }]},

  { termo: "brb", idioma: "en", categorias: ["gaming", "abreviacao"], sentidos: [
    { simples: "Aviso de que a pessoa volta em poucos minutos.",
      detalhada: "De \"be right back\", já volto. Mais curto que \"afk\": indica ausência rápida e com volta prevista.",
      formal: "já volto" }]},

  { termo: "ping", idioma: "en", categorias: ["gaming"], sentidos: [
    { simples: "Número que mostra o quanto a internet está demorando para responder.",
      detalhada: "Medido em milésimos de segundo. Quanto menor, melhor: ping alto é a causa mais comum de travamento em jogos pela internet.",
      formal: "latência" }]},

  { termo: "throw", idioma: "en", categorias: ["gaming"], variacoes: ["throwar", "throwou"], sentidos: [
    { simples: "Jogar fora uma partida que já estava ganha.",
      detalhada: "De \"throw the game\". Pode ser por erro ou de propósito. Quando é de propósito, é considerado sabotagem do próprio time.",
      formal: "perder por erro próprio" }]},

  { termo: "carry", idioma: "en", categorias: ["gaming"], variacoes: ["carregar", "carregou"], sentidos: [
    { simples: "Jogador que ganha a partida praticamente sozinho, puxando o time.",
      detalhada: "De \"carry\", carregar. Também usado como verbo: \"ele carregou\" quer dizer que o resto do time contribuiu pouco.",
      formal: "puxar o time" }]},

  { termo: "gank", idioma: "en", categorias: ["gaming"], variacoes: ["gankar", "gankou"], sentidos: [
    { simples: "Atacar alguém de surpresa, em grupo, quando a pessoa está sozinha.",
      detalhada: "Provável junção de \"gang\" com \"kill\". É estratégia comum e não é considerada desleal.",
      formal: "emboscada em grupo" }]},

  { termo: "aggro", idioma: "en", categorias: ["gaming"], sentidos: [
    { simples: "A atenção dos inimigos do jogo voltada para um jogador específico.",
      detalhada: "De \"aggression\". \"Puxar o aggro\" é fazer os inimigos atacarem você em vez do resto do time, geralmente de propósito.",
      formal: "atrair os inimigos" }]},

  { termo: "spawn", idioma: "en", categorias: ["gaming"], variacoes: ["spawnar", "respawn"], sentidos: [
    { simples: "O ponto onde o jogador reaparece depois de morrer.",
      detalhada: "De \"spawn\", surgir. \"Spawn kill\" é derrubar alguém no exato momento em que reaparece, antes de poder reagir, o que é considerado desleal.",
      formal: "ponto de retorno" }]},

  { termo: "loot", idioma: "en", categorias: ["gaming"], variacoes: ["lootar", "lootando"], sentidos: [
    { simples: "Os itens que o jogador recolhe pelo caminho.",
      detalhada: "De \"loot\", espólio. Muitos jogos têm \"loot box\", caixa com itens sorteados, que já foi comparada a jogo de azar em vários países.",
      formal: "itens recolhidos" }]},

  { termo: "grind", idioma: "en", categorias: ["gaming"], variacoes: ["grindar", "grindando"], sentidos: [
    { simples: "Repetir a mesma tarefa muitas vezes para progredir no jogo.",
      detalhada: "De \"grind\", moer. Fora dos jogos, \"grindar\" virou sinônimo de trabalhar muito e sem glamour para chegar a algum lugar.",
      formal: "repetir para progredir" }]},

  { termo: "meta", idioma: "en", categorias: ["gaming"], sentidos: [
    { simples: "A maneira de jogar que está dando mais certo no momento.",
      detalhada: "De \"most effective tactic available\", a tática mais eficaz disponível. Muda a cada atualização do jogo. \"Fora do meta\" é usar algo que a maioria abandonou.",
      formal: "estratégia dominante" }]},

  { termo: "nolife", idioma: "en", categorias: ["gaming", "critica"], variacoes: ["no life"], sentidos: [
    { simples: "Alguém que joga tanto que parece não ter mais nada na vida.",
      detalhada: "Quase sempre ofensa, às vezes usada com humor pela própria pessoa. Já foi criticado por tratar dedicação como defeito.",
      formal: "viciado em jogo" }]},

  { termo: "wipe", idioma: "en", categorias: ["gaming"], variacoes: ["wipar", "wipamos"], sentidos: [
    { simples: "Quando o time inteiro morre de uma vez.",
      detalhada: "De \"wipe\", limpar. Em jogos em que se joga em grupo, o wipe geralmente obriga a recomeçar a fase inteira.",
      formal: "derrota total do time" }]},

  { termo: "boss", idioma: "en", categorias: ["gaming"], variacoes: ["chefão"], sentidos: [
    { simples: "O inimigo mais forte de uma fase, que fecha aquela etapa do jogo.",
      detalhada: "Em português virou \"chefão\". Fora dos jogos, \"boss\" também é usado como elogio a alguém que resolve tudo.",
      formal: "inimigo final" }]},

  { termo: "hitbox", idioma: "en", categorias: ["gaming"], sentidos: [
    { simples: "A área invisível do personagem que conta como acerto quando alguém atira.",
      detalhada: "Reclamar da hitbox é reclamar que o tiro deveria ter acertado e não acertou, ou o contrário.",
      formal: "área de acerto" }]},

  { termo: "buildar", idioma: "pt-BR", categorias: ["gaming"], variacoes: ["build"], sentidos: [
    { simples: "Montar o personagem escolhendo habilidades e equipamentos.",
      detalhada: "Do inglês \"build\", construir. \"Build\" é também o resultado: o conjunto de escolhas que a pessoa montou.",
      formal: "montar o personagem" }]},

  { termo: "nerdar", idioma: "pt-BR", categorias: ["gaming"], sentidos: [
    { simples: "Passar horas mergulhado em um jogo ou num assunto.",
      detalhada: "De \"nerd\". Diferente de \"nolife\", costuma ser usado sem ofensa e até com orgulho.",
      formal: "mergulhar no assunto" }]},

  { termo: "upar", idioma: "pt-BR", categorias: ["gaming"], variacoes: ["upou", "upando"], sentidos: [
    { simples: "Subir de nível no jogo.",
      detalhada: "De \"level up\". Fora do jogo, \"upar\" ganhou o sentido de melhorar qualquer coisa: \"upei meu celular\".",
      formal: "subir de nível" }]},

  { termo: "rushar", idioma: "pt-BR", categorias: ["gaming"], variacoes: ["rush", "rushou"], sentidos: [
    { simples: "Avançar rápido e direto, sem esperar nem se preparar.",
      detalhada: "Do inglês \"rush\", correr. Fora dos jogos, \"rushar\" virou fazer qualquer coisa com pressa: \"rushei a prova\".",
      formal: "avançar com pressa" }]},

  { termo: "farmar", idioma: "pt-BR", categorias: ["gaming"], variacoes: ["farm", "farmando"], sentidos: [
    { simples: "Repetir tarefas simples no jogo para juntar dinheiro ou itens.",
      detalhada: "De \"farm\", cultivar. Fora dos jogos virou juntar qualquer coisa aos poucos: \"farmar seguidor\", \"farmar curtida\".",
      formal: "acumular recursos" }]},

  { termo: "dropar", idioma: "pt-BR", categorias: ["gaming"], variacoes: ["drop", "dropou"], sentidos: [
    { simples: "Um item cair do inimigo derrotado.",
      detalhada: "De \"drop\", soltar. \"Dropou\" quer dizer que o item apareceu.",
      formal: "soltar item" },
    { simples: "Fora do jogo, lançar algo novo, como uma música ou um vídeo.",
      detalhada: "\"Dropou o clipe\" quer dizer que o vídeo foi publicado. Uso vindo da indústria da música.",
      formal: "lançar" }]},

  { termo: "castar", idioma: "pt-BR", categorias: ["gaming", "esporte"], variacoes: ["caster", "cast"], sentidos: [
    { simples: "Narrar uma partida ao vivo para quem está assistindo.",
      detalhada: "De \"broadcast\", transmitir. O \"caster\" é o narrador de partidas de esporte eletrônico, equivalente ao locutor de futebol.",
      formal: "narrar a partida" }]},

  { termo: "main", idioma: "en", categorias: ["gaming"], variacoes: ["mainar"], sentidos: [
    { simples: "O personagem que a pessoa mais usa e conhece melhor.",
      detalhada: "De \"main\", principal. Virou verbo também: \"eu maino esse personagem\".",
      formal: "personagem principal" }]},

  { termo: "op", idioma: "en", categorias: ["gaming"], sentidos: [
    { simples: "Forte demais, desequilibrado a ponto de estragar o jogo.",
      detalhada: "De \"overpowered\". Quando muita gente reclama que algo está op, costuma vir um enfraquecimento na atualização seguinte.",
      formal: "forte demais" }]},

  { termo: "hp", idioma: "en", categorias: ["gaming"], sentidos: [
    { simples: "A quantidade de vida que resta ao personagem.",
      detalhada: "De \"health points\", pontos de vida. Fora do jogo, \"tô sem hp\" virou jeito de dizer que a pessoa está exausta.",
      formal: "pontos de vida" }]},

  { termo: "gg izi", idioma: "pt-BR", categorias: ["gaming", "humor"], variacoes: ["gg ez", "ggizi"], sentidos: [
    { simples: "Provocação dita ao vencer, misturando \"bom jogo\" com \"foi fácil\".",
      detalhada: "Junção de \"gg\" com \"ez\" escrito à brasileira. Em vários jogos é passível de punição por provocação.",
      formal: "foi fácil" }]},

  { termo: "pvp", idioma: "en", categorias: ["gaming"], sentidos: [
    { simples: "Modo em que jogadores enfrentam outros jogadores, e não o computador.",
      detalhada: "De \"player versus player\". O oposto é \"pve\", jogador contra o ambiente controlado pela máquina.",
      formal: "jogador contra jogador" }]},

  { termo: "nick", idioma: "en", categorias: ["gaming", "redes"], sentidos: [
    { simples: "O apelido que a pessoa usa dentro do jogo ou da internet.",
      detalhada: "De \"nickname\". Muitas vezes é por ele que a pessoa é conhecida, e não pelo nome real.",
      formal: "apelido" }]},

  { termo: "lobby", idioma: "en", categorias: ["gaming"], sentidos: [
    { simples: "A sala de espera onde os jogadores se juntam antes da partida começar.",
      detalhada: "De \"lobby\", saguão. É onde o grupo se organiza e escolhe os personagens.",
      formal: "sala de espera" }]},

  { termo: "party", idioma: "en", categorias: ["gaming"], sentidos: [
    { simples: "O grupo de amigos que joga junto na mesma partida.",
      detalhada: "\"Chama na party\" é um convite para jogar junto. Nada a ver com festa, apesar da tradução literal.",
      formal: "grupo" }]},

  { termo: "queue", idioma: "en", categorias: ["gaming"], variacoes: ["queuar"], sentidos: [
    { simples: "A fila de espera até o jogo encontrar adversários.",
      detalhada: "Pronuncia-se como a letra Q. \"Solo queue\" é entrar na fila sozinho e receber companheiros desconhecidos.",
      formal: "fila de espera" }]},

  { termo: "hard", idioma: "en", categorias: ["gaming"], sentidos: [
    { simples: "Quer dizer que algo é muito difícil ou muito intenso.",
      detalhada: "Do inglês \"hard\", duro. Também aparece colado em outra palavra para reforçar: \"hard carry\" é carregar o time de forma esmagadora.",
      formal: "difícil" }]},

  { termo: "cheater", idioma: "en", categorias: ["gaming", "critica"], variacoes: ["cheat", "cheatando"], sentidos: [
    { simples: "Quem usa programa proibido para trapacear no jogo.",
      detalhada: "De \"cheat\", trapaça. Diferente do \"smurf\", que joga dentro das regras: o cheater quebra as regras e costuma ser banido.",
      formal: "trapaceiro" }]},

  { termo: "aimbot", idioma: "en", categorias: ["gaming", "critica"], sentidos: [
    { simples: "Programa proibido que faz a mira acertar sozinha.",
      detalhada: "Junção de \"aim\", mira, com \"robot\". É das trapaças mais comuns e das mais fáceis de detectar.",
      formal: "mira automática" }]},

  { termo: "wallhack", idioma: "en", categorias: ["gaming", "critica"], sentidos: [
    { simples: "Programa proibido que deixa a pessoa enxergar os adversários através das paredes.",
      detalhada: "Junto do aimbot, é a trapaça mais reclamada em jogos de tiro.",
      formal: "visão através de paredes" }]},

  { termo: "ranqueada", idioma: "pt-BR", categorias: ["gaming"], variacoes: ["ranked", "rank"], sentidos: [
    { simples: "Partida que vale posição numa classificação, diferente da partida casual.",
      detalhada: "Do inglês \"ranked\". Perder na ranqueada faz a pessoa cair de posição, o que explica boa parte da tensão nesse modo.",
      formal: "partida classificatória" }]},

  { termo: "elo", idioma: "en", categorias: ["gaming"], sentidos: [
    { simples: "O nível de habilidade que o sistema atribui ao jogador.",
      detalhada: "Nome vindo de Arpad Elo, que criou o sistema de classificação usado no xadrez. \"Elo hell\" é a sensação de estar preso num nível.",
      formal: "nível de classificação" }]},

  { termo: "duo", idioma: "en", categorias: ["gaming"], variacoes: ["duo queue"], sentidos: [
    { simples: "Jogar em dupla fixa, sempre com a mesma pessoa.",
      detalhada: "Muitos jogos limitam o duo em modos competitivos, porque dois jogadores combinados têm vantagem sobre desconhecidos.",
      formal: "dupla" }]},

  { termo: "solo", idioma: "en", categorias: ["gaming"], sentidos: [
    { simples: "Jogar sozinho, sem amigos no time.",
      detalhada: "\"Solo queue\" é entrar na fila sozinho e receber companheiros que você nunca viu.",
      formal: "sozinho" }]},

  { termo: "tóxico", idioma: "pt-BR", categorias: ["gaming", "critica"], variacoes: ["toxic"], sentidos: [
    { simples: "Jogador que ofende, humilha ou estraga a partida dos outros de propósito.",
      detalhada: "Do inglês \"toxic\". Fora dos jogos, \"pessoa tóxica\" é alguém cuja convivência faz mal, e o uso já passou para relações e trabalho.",
      formal: "hostil" }]},

  { termo: "reportar", idioma: "pt-BR", categorias: ["gaming", "redes"], variacoes: ["report", "reportado"], sentidos: [
    { simples: "Avisar o sistema sobre alguém que quebrou as regras.",
      detalhada: "Do inglês \"report\", denunciar. Vale para jogos e para redes sociais.",
      formal: "denunciar" }]},

  { termo: "ban", idioma: "en", categorias: ["gaming", "redes"], variacoes: ["banido", "banir", "tomou ban"], sentidos: [
    { simples: "Ser expulso e proibido de voltar.",
      detalhada: "Pode ser temporário ou permanente. Nas redes sociais, \"shadowban\" é a versão invisível: a pessoa continua publicando, mas quase ninguém vê.",
      formal: "banimento" }]},

  { termo: "combo", idioma: "en", categorias: ["gaming"], sentidos: [
    { simples: "Sequência de golpes encaixados um atrás do outro.",
      detalhada: "De \"combination\". Em jogos de luta, decorar combos é boa parte do aprendizado.",
      formal: "sequência de golpes" }]},

  { termo: "speedrun", idioma: "en", categorias: ["gaming"], sentidos: [
    { simples: "Terminar um jogo o mais rápido possível, competindo por tempo.",
      detalhada: "Virou modalidade com regras próprias e recordes verificados por comunidades. Muita gente assiste a speedrun sem nunca ter jogado o jogo.",
      formal: "corrida contra o tempo" }]},

  { termo: "glitch", idioma: "en", categorias: ["gaming"], sentidos: [
    { simples: "Falha do jogo que faz algo estranho acontecer.",
      detalhada: "Diferente de \"bug\" apenas pelo uso: \"glitch\" costuma descrever a falha visível, engraçada ou aproveitável.",
      formal: "falha" }]},

  { termo: "bug", idioma: "en", categorias: ["gaming"], variacoes: ["bugado", "bugar", "bugou"], sentidos: [
    { simples: "Erro de programação que faz algo funcionar errado.",
      detalhada: "Em português virou \"bugado\", que se aplica a qualquer coisa que travou.",
      formal: "erro" },
    { simples: "Falando de gente, quer dizer que a pessoa ficou confusa e travou.",
      detalhada: "\"Fiquei bugada\" quer dizer que a pessoa não entendeu nada e não soube o que responder.",
      formal: "ficar confuso" }]},

  { termo: "patch", idioma: "en", categorias: ["gaming"], sentidos: [
    { simples: "Atualização que corrige erros ou muda regras do jogo.",
      detalhada: "De \"patch\", remendo. As \"patch notes\" são a lista do que mudou, onde aparecem os enfraquecimentos e reforços.",
      formal: "atualização" }]},

  { termo: "skin", idioma: "en", categorias: ["gaming"], sentidos: [
    { simples: "Roupa ou aparência diferente para o personagem, que não muda a força dele.",
      detalhada: "De \"skin\", pele. É o principal produto vendido em jogos gratuitos, e movimenta muito dinheiro.",
      formal: "aparência" }]},

  { termo: "f2p", idioma: "en", categorias: ["gaming"], variacoes: ["free to play"], sentidos: [
    { simples: "Jogo de graça para jogar, que ganha dinheiro vendendo itens.",
      detalhada: "De \"free to play\". Quando os itens vendidos dão vantagem real, o jogo passa a ser chamado de \"pay to win\".",
      formal: "gratuito" }]},

  { termo: "p2w", idioma: "en", categorias: ["gaming", "critica"], variacoes: ["pay to win"], sentidos: [
    { simples: "Jogo em que quem paga leva vantagem sobre quem não paga.",
      detalhada: "De \"pay to win\", pagar para vencer. É das críticas mais duras que um jogo pode receber, porque quebra a ideia de competição justa.",
      formal: "pagar para vencer" }]},

  { termo: "gacha", idioma: "en", categorias: ["gaming", "critica"], sentidos: [
    { simples: "Sistema em que a pessoa paga para sortear um item, sem saber o que vem.",
      detalhada: "Do japonês \"gachapon\", as máquinas de cápsula. É alvo de regulação em vários países pela semelhança com jogo de azar.",
      formal: "sorteio pago" }]},

  { termo: "sandbox", idioma: "en", categorias: ["gaming"], sentidos: [
    { simples: "Jogo sem objetivo fixo, em que a pessoa cria o que quiser.",
      detalhada: "De \"sandbox\", caixa de areia. Minecraft é o exemplo mais conhecido.",
      formal: "jogo livre" }]},

  { termo: "cooldown", idioma: "en", categorias: ["gaming"], variacoes: ["cd"], sentidos: [
    { simples: "O tempo de espera até poder usar uma habilidade de novo.",
      detalhada: "Fora do jogo virou brincadeira para descanso: \"tô em cooldown\" quer dizer que a pessoa precisa de um tempo.",
      formal: "tempo de recarga" }]},

  { termo: "tryhard", idioma: "en", categorias: ["gaming", "critica"], sentidos: [
    { simples: "Quem leva a sério demais um jogo que era só diversão.",
      detalhada: "Costuma ser crítica, mas parte dos jogadores usa como elogio a quem se dedica de verdade.",
      formal: "competitivo demais" }]},

  { termo: "casual", idioma: "en", categorias: ["gaming"], sentidos: [
    { simples: "Quem joga por diversão, sem se preocupar em ganhar.",
      detalhada: "O oposto de tryhard. Também nomeia o modo de partida que não vale classificação.",
      formal: "descompromissado" }]},

  { termo: "mira", idioma: "pt-BR", categorias: ["gaming"], sentidos: [
    { simples: "A pontaria do jogador em jogos de tiro.",
      detalhada: "\"Mira de ouro\" é elogio a quem acerta sempre; \"mira de manteiga\" é a piada para quem erra tudo.",
      formal: "pontaria" }]},

  { termo: "ragequit", idioma: "en", categorias: ["gaming", "emocao"], variacoes: ["rage quit", "dar rage"], sentidos: [
    { simples: "Ficar tão irritado a ponto de sair do jogo no meio.",
      detalhada: "Do inglês \"rage quit\", desistir de raiva. Em partidas em equipe, prejudica o time inteiro e costuma render punição.",
      formal: "abandonar de raiva" }]},

  { termo: "dodgear", idioma: "pt-BR", categorias: ["gaming"], variacoes: ["dodge", "dodgeou"], sentidos: [
    { simples: "Desviar de um golpe.",
      detalhada: "De \"dodge\", esquivar.",
      formal: "esquivar" },
    { simples: "Sair da fila antes da partida começar, para não pegar aquele adversário.",
      detalhada: "Costuma render punição de tempo, justamente para desestimular a prática.",
      formal: "abandonar a fila" }]},

  { termo: "one shot", idioma: "en", categorias: ["gaming"], variacoes: ["oneshot", "hitkill"], sentidos: [
    { simples: "Derrubar o adversário com um único golpe.",
      detalhada: "Fora dos jogos, \"one shot\" virou resolver algo de primeira, sem precisar tentar de novo.",
      formal: "golpe único" }]},

  { termo: "healer", idioma: "en", categorias: ["gaming"], sentidos: [
    { simples: "O jogador que cura os companheiros em vez de atacar.",
      detalhada: "De \"heal\", curar. Função pouco reconhecida na hora da vitória e muito cobrada na hora da derrota.",
      formal: "curandeiro" }]},

  { termo: "tank", idioma: "en", categorias: ["gaming"], variacoes: ["tankar", "tankou"], sentidos: [
    { simples: "O jogador que aguenta os ataques no lugar dos outros.",
      detalhada: "De \"tank\", tanque de guerra.",
      formal: "aguentar o dano" },
    { simples: "Fora do jogo, aguentar algo pesado sem desabar.",
      detalhada: "\"Tankei a semana inteira\" quer dizer que a pessoa suportou uma carga difícil.",
      formal: "aguentar" }]},

  { termo: "dps", idioma: "en", categorias: ["gaming"], sentidos: [
    { simples: "O quanto de dano um personagem causa por segundo.",
      detalhada: "De \"damage per second\". Também nomeia a função: o \"dps\" é quem ataca, em contraste com o tank e o healer.",
      formal: "dano por segundo" }]},

  { termo: "nolan", idioma: "pt-BR", categorias: ["gaming", "humor"], sentidos: [
    { simples: "Jogada tão confusa que ninguém entendeu como acabou dando certo.",
      detalhada: "Referência ao cineasta Christopher Nolan, conhecido por roteiros complicados. Uso brasileiro, comum em transmissões.",
      formal: "jogada confusa" }]},

  { termo: "gamer", idioma: "en", categorias: ["gaming"], sentidos: [
    { simples: "Pessoa que joga videogame com frequência.",
      detalhada: "Virou também rótulo de identidade, e às vezes é usado com ironia por quem acha o rótulo exagerado.",
      formal: "jogador" }]},

  { termo: "setup", idioma: "en", categorias: ["gaming"], sentidos: [
    { simples: "O conjunto de computador, cadeira e acessórios que a pessoa usa para jogar.",
      detalhada: "Mostrar o setup é conteúdo comum em vídeos. Também usado fora dos jogos para qualquer arranjo de equipamentos.",
      formal: "equipamento" }]},

  { termo: "fps", idioma: "en", categorias: ["gaming"], sentidos: [
    { simples: "Quantos quadros por segundo o jogo consegue mostrar.",
      detalhada: "De \"frames per second\". Quanto maior, mais suave a imagem.",
      formal: "quadros por segundo" },
    { simples: "A mesma sigla nomeia o tipo de jogo de tiro visto pelos olhos do personagem.",
      detalhada: "De \"first person shooter\", tiro em primeira pessoa. O contexto separa os dois sentidos sem dificuldade.",
      formal: "jogo de tiro em primeira pessoa" }]},

  { termo: "mmr", idioma: "en", categorias: ["gaming"], sentidos: [
    { simples: "Nota interna que o jogo usa para escolher adversários do seu nível.",
      detalhada: "De \"matchmaking rating\". Costuma ser invisível ao jogador, diferente do elo, que aparece na tela.",
      formal: "nota de pareamento" }]},

  { termo: "hitar", idioma: "pt-BR", categorias: ["gaming"], sentidos: [
    { simples: "Acertar o golpe ou o tiro.",
      detalhada: "De \"hit\", acertar. \"Não hitou\" quer dizer que passou perto e não pegou.",
      formal: "acertar" }]},

  { termo: "pushar", idioma: "pt-BR", categorias: ["gaming"], variacoes: ["push"], sentidos: [
    { simples: "Avançar com pressão para tomar terreno do adversário.",
      detalhada: "De \"push\", empurrar. Diferente de \"rushar\", que é correr sem preparo: pushar é avançar de forma organizada.",
      formal: "pressionar" }]},

  { termo: "eco", idioma: "en", categorias: ["gaming"], sentidos: [
    { simples: "Rodada em que o time economiza dinheiro e compra pouco de propósito.",
      detalhada: "De \"economy\". A ideia é perder uma rodada barata para ter dinheiro forte na próxima.",
      formal: "rodada de economia" }]},

  { termo: "ace", idioma: "en", categorias: ["gaming"], sentidos: [
    { simples: "Quando um único jogador derruba o time adversário inteiro.",
      detalhada: "Do baralho, onde o ás é a carta mais forte. É das jogadas mais celebradas em jogos de tiro por equipe.",
      formal: "derrubada completa" }]},

  { termo: "whiff", idioma: "en", categorias: ["gaming"], sentidos: [
    { simples: "Errar feio um golpe ou tiro que parecia fácil.",
      detalhada: "Palavra inglesa que imita o som de algo passando no vazio.",
      formal: "erro feio" }]},

  { termo: "peekar", idioma: "pt-BR", categorias: ["gaming"], variacoes: ["peek"], sentidos: [
    { simples: "Espiar rapidamente de trás de uma parede para ver o adversário.",
      detalhada: "De \"peek\", espiar. Técnica central em jogos de tiro tático, com várias variações de nome próprio.",
      formal: "espiar" }]},

  { termo: "clipar", idioma: "pt-BR", categorias: ["gaming", "streaming"], variacoes: ["clip", "clipe"], sentidos: [
    { simples: "Salvar um trecho curto de uma partida ou transmissão.",
      detalhada: "De \"clip\", recorte. \"Clipa isso\" é um pedido para gravar o momento antes que ele se perca.",
      formal: "gravar o trecho" }]},

  { termo: "montage", idioma: "en", categorias: ["gaming"], sentidos: [
    { simples: "Vídeo com os melhores momentos de um jogador, um atrás do outro.",
      detalhada: "Costuma vir com música e cortes rápidos. É um formato de vídeo muito antigo na cultura de jogos.",
      formal: "compilado" }]},

  { termo: "highlight", idioma: "en", categorias: ["gaming", "esporte"], sentidos: [
    { simples: "O melhor momento de uma partida.",
      detalhada: "De \"highlight\", destaque. O mesmo termo usado no esporte tradicional para os melhores lances.",
      formal: "melhor momento" }]},

  { termo: "hardcore", idioma: "en", categorias: ["gaming"], sentidos: [
    { simples: "Modo de jogo em que morrer apaga tudo e obriga a recomeçar do zero.",
      detalhada: "Fora dos jogos, \"hardcore\" descreve qualquer coisa levada ao extremo.",
      formal: "modo extremo" }]},

  { termo: "go next", idioma: "en", categorias: ["gaming"], variacoes: ["gg go next"], sentidos: [
    { simples: "Aceitar que a partida está perdida e já pensar na próxima.",
      detalhada: "Frase comum quando o time desiste mentalmente antes do fim. Virou também jeito de dizer \"deixa pra lá\" fora do jogo.",
      formal: "vamos para a próxima" }]},

  { termo: "jogar de bot", idioma: "pt-BR", categorias: ["gaming", "critica"], sentidos: [
    { simples: "Jogar mal, de forma automática, sem prestar atenção.",
      detalhada: "De \"bot\", o personagem controlado pelo computador, que joga de forma previsível.",
      formal: "jogar sem atenção" }]},

  { termo: "sweat", idioma: "en", categorias: ["gaming", "critica"], variacoes: ["sweaty"], sentidos: [
    { simples: "Jogador que se esforça ao máximo mesmo numa partida sem importância.",
      detalhada: "De \"sweat\", suor. Primo do \"tryhard\", com a mesma carga de crítica leve.",
      formal: "esforçado demais" }]},

  { termo: "chocar", idioma: "pt-BR", categorias: ["gaming", "humor"], variacoes: ["chocando", "chocado"], sentidos: [
    { simples: "Ficar parado no mesmo lugar sem fazer nada de útil.",
      detalhada: "Uso brasileiro em jogos, por analogia com a galinha que fica sentada no ninho.",
      formal: "ficar parado" }]},

  { termo: "que roubo", idioma: "pt-BR", categorias: ["gaming", "emocao"], sentidos: [
    { simples: "Reclamação de que algo foi injusto no jogo.",
      detalhada: "Na maioria das vezes não acusa trapaça de verdade: é desabafo de quem achou o resultado injusto.",
      formal: "que injustiça" }]},

  { termo: "puxar", idioma: "pt-BR", categorias: ["gaming"], sentidos: [
    { simples: "Atrair inimigos de propósito para longe do grupo.",
      detalhada: "Equivalente brasileiro de \"pull\". Costuma ser combinado antes com o time.",
      formal: "atrair" }]},

  { termo: "trocar", idioma: "pt-BR", categorias: ["gaming"], sentidos: [
    { simples: "Cair junto com o adversário, um derrubando o outro quase ao mesmo tempo.",
      detalhada: "\"Trocou\" é considerado resultado neutro em jogos por equipe, porque os dois times perdem um jogador.",
      formal: "troca de baixas" }]},

  { termo: "abrir", idioma: "pt-BR", categorias: ["gaming"], sentidos: [
    { simples: "Ser o primeiro a entrar num lugar perigoso, arriscando-se pelo time.",
      detalhada: "\"Quem abre\" é quem toma o primeiro tiro. Função arriscada e valorizada em jogos de tiro tático.",
      formal: "entrar primeiro" }]},

  { termo: "segurar", idioma: "pt-BR", categorias: ["gaming"], sentidos: [
    { simples: "Defender uma posição sozinho até os companheiros chegarem.",
      detalhada: "\"Segura aí\" é um pedido de tempo, não de força: o objetivo é atrasar o avanço adversário.",
      formal: "defender a posição" }]},

  { termo: "crashar", idioma: "pt-BR", categorias: ["gaming"], variacoes: ["crash", "crashou"], sentidos: [
    { simples: "O jogo ou o programa fechar sozinho, do nada.",
      detalhada: "De \"crash\", colidir. Diferente de travar: no crash o programa some da tela.",
      formal: "fechar sozinho" }]},

  { termo: "otimizado", idioma: "pt-BR", categorias: ["gaming"], variacoes: ["otimizar"], sentidos: [
    { simples: "Quando o jogo roda bem sem exigir um computador caro.",
      detalhada: "\"Jogo mal otimizado\" é crítica comum: significa que ele pesa mais do que deveria para o que entrega.",
      formal: "bem ajustado" }]},

  { termo: "input lag", idioma: "en", categorias: ["gaming"], sentidos: [
    { simples: "O atraso entre apertar o botão e o personagem reagir.",
      detalhada: "Diferente do lag de internet: pode acontecer mesmo jogando sem conexão, por causa da tela ou do controle.",
      formal: "atraso de comando" }]},

  { termo: "macro", idioma: "en", categorias: ["gaming"], sentidos: [
    { simples: "Comando gravado que executa várias ações com uma tecla só.",
      detalhada: "Em alguns jogos é permitido; em outros é considerado trapaça, por automatizar o que deveria ser habilidade.",
      formal: "comando automático" }]},

  { termo: "gg wp", idioma: "en", categorias: ["gaming"], variacoes: ["ggwp"], sentidos: [
    { simples: "Bom jogo e bem jogado, dito no fim da partida.",
      detalhada: "De \"good game, well played\". É a versão mais educada do \"gg\", sem margem para ironia.",
      formal: "bom jogo, bem jogado" }]},

  { termo: "sniper", idioma: "en", categorias: ["gaming"], sentidos: [
    { simples: "Jogador que atira de longe, escondido, com arma de precisão.",
      detalhada: "Do inglês \"sniper\", atirador de elite. \"Stream sniping\" é outra coisa: assistir à transmissão do adversário para saber onde ele está.",
      formal: "atirador de longa distância" }]},

  { termo: "rekt", idioma: "en", categorias: ["gaming", "humor"], variacoes: ["get rekt"], sentidos: [
    { simples: "Provocação dita quando alguém foi derrotado de forma humilhante.",
      detalhada: "Escrita deformada de \"wrecked\", destruído. Comum como legenda de vídeo de derrota.",
      formal: "arrasado" }]},

  { termo: "git gud", idioma: "en", categorias: ["gaming", "humor"], variacoes: ["gitgud"], sentidos: [
    { simples: "Resposta provocativa a quem reclama: \"fica bom\", ou seja, treine mais.",
      detalhada: "Escrita deformada de \"get good\". Virou bordão em comunidades de jogos difíceis.",
      formal: "melhore" }]},

  { termo: "kd", idioma: "en", categorias: ["gaming"], variacoes: ["kda"], sentidos: [
    { simples: "A relação entre quantas vezes o jogador derrubou e foi derrubado.",
      detalhada: "De \"kill/death\". É a estatística mais citada em jogos de tiro, e também a mais criticada por não medir o que o jogador fez pelo time.",
      formal: "proporção de abates" }]},

  { termo: "wr", idioma: "en", categorias: ["gaming", "esporte"], variacoes: ["world record"], sentidos: [
    { simples: "Recorde mundial de uma categoria de jogo.",
      detalhada: "De \"world record\". Em speedrun, cada jogo tem dezenas de categorias, cada uma com seu recorde próprio.",
      formal: "recorde mundial" }]},

  { termo: "pb", idioma: "en", categorias: ["gaming", "esporte"], variacoes: ["personal best"], sentidos: [
    { simples: "O melhor resultado que aquela pessoa já conseguiu.",
      detalhada: "De \"personal best\". Termo emprestado do atletismo, muito usado em speedrun.",
      formal: "melhor marca pessoal" }]},

  { termo: "nerfaram", idioma: "pt-BR", categorias: ["gaming", "humor"], sentidos: [
    { simples: "Brincadeira de dizer que algo ficou pior depois de uma mudança.",
      detalhada: "Uso ampliado para fora do jogo: \"nerfaram meu café\" quer dizer que o café piorou.",
      formal: "enfraqueceram" }]},

  { termo: "respawnar", idioma: "pt-BR", categorias: ["gaming", "humor"], sentidos: [
    { simples: "Voltar a aparecer depois de sumir.",
      detalhada: "Usado com humor para pessoas: \"ele respawnou depois de três meses sumido\".",
      formal: "reaparecer" }]},
];
