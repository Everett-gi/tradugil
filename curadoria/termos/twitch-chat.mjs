/*
 * Emotes e vocabulario do chat da Twitch.
 *
 * POR QUE ISTO E UMA CATEGORIA A PARTE
 *
 * O chat da Twitch e o unico lugar deste dicionario onde a "palavra" nao e
 * uma palavra: e o nome de uma imagem. Quem le "OMEGALUL" fora do chat ve
 * letras sem sentido nenhum, porque a mensagem inteira esta no desenho de um
 * rosto rindo, e o nome so existe para invocar esse desenho.
 *
 * Isso muda como a explicacao precisa ser escrita. Nao basta dizer o que
 * significa: precisa dizer O QUE APARECE NA TELA, senao a pessoa nao liga
 * uma coisa a outra na proxima vez que vir. Por isso quase todo verbete aqui
 * comeca descrevendo a imagem.
 *
 * A SEGUNDA DIFICULDADE: A GRAFIA
 *
 * O brasileiro escuta e escreve estes nomes do jeito que soam. "PepeLaugh"
 * vira "pepelef", "OMEGALUL" vira "omegalol". Se so a grafia oficial
 * entrasse, quem procurasse do jeito que ouviu nao acharia nada. As
 * variacoes aqui incluem as formas aportuguesadas de proposito.
 *
 * FONTES
 *
 * Significados conferidos em setembro de 2026 contra own3d.tv (verbetes em
 * portugues), streamscheme, knowyourmeme e o ranking de uso da StreamElements.
 * Nao inventei nenhum: emote com significado errado e pior que emote
 * ausente, porque a pessoa acha que entendeu.
 */
export default [
  /* ------------------------------------------------------------- riso --- */

  { termo: "OMEGALUL", idioma: "en", categorias: ["streaming", "humor"], variacoes: ["omegalol", "omega lul", "omegalull"], sentidos: [
    { simples: "Risada enorme. É o \"chorei de rir\" do chat da Twitch.",
      detalhada: "A imagem é o rosto do humorista espanhol El Risitas, esticado e deformado. O \"OMEGA\" na frente é o exagero: quando \"LUL\" não é suficiente, usa-se este. Aparece quando alguém erra feio, passa vergonha e percebe.",
      formal: "gargalhada" }]},

  { termo: "LUL", idioma: "en", categorias: ["streaming", "humor"], variacoes: ["lulw", "lul w"], sentidos: [
    { simples: "Risada. É o \"kkkk\" do chat da Twitch.",
      detalhada: "Mostra o rosto do humorista espanhol El Risitas rindo. \"LULW\" é a versão mais larga, usada para rir mais forte." }]},

  { termo: "PepeLaugh", idioma: "en", categorias: ["streaming", "humor"], variacoes: ["pepelef", "pepe laugh", "pepelaf", "pepelaugh"], sentidos: [
    { simples: "O chat rindo de alguma coisa que a pessoa transmitindo ainda não percebeu.",
      detalhada: "A imagem é o sapo Pepe rindo com os olhos apertados. Não é riso qualquer: é o riso de quem sabe o que vem. O chat enche de PepeLaugh quando o inimigo está atrás do streamer e ele não viu. Costuma vir acompanhado de \"here it comes\"." }]},

  { termo: "4Head", idioma: "en", categorias: ["streaming", "humor"], variacoes: ["four head"], sentidos: [
    { simples: "Risada de piada ruim, quase sempre irônica.",
      detalhada: "Mostra o rosto de um jogador profissional rindo, com a testa grande. Usar de verdade é raro: quase sempre é deboche." }]},

  /* -------------------------------------------------------- tensao e dor --- */

  { termo: "PepeHands", idioma: "en", categorias: ["streaming", "emocao"], variacoes: ["pepe hands", "pepehands"], sentidos: [
    { simples: "Choro. Tristeza mais forte que a do Sadge.",
      detalhada: "O sapo Pepe chorando com as mãos no rosto. Usado quando algo é comovente de verdade, e também de forma exagerada e cômica." }]},

  { termo: "FeelsBadMan", idioma: "en", categorias: ["streaming", "emocao"], variacoes: ["feels bad man", "feelsbad"], sentidos: [
    { simples: "Que situação ruim. Solidariedade com quem se deu mal.",
      detalhada: "O sapo Pepe de cara fechada e triste. É o emote original da família Pepe, de onde saiu todo o resto." }]},

  { termo: "FeelsGoodMan", idioma: "en", categorias: ["streaming", "emocao"], variacoes: ["feels good man", "feelsgood"], sentidos: [
    { simples: "Que bom. Satisfação tranquila.",
      detalhada: "O sapo Pepe com um sorriso satisfeito. É o oposto exato do FeelsBadMan." }]},

  { termo: "BibleThump", idioma: "en", categorias: ["streaming", "emocao"], variacoes: ["bible thump"], sentidos: [
    { simples: "Choro, quase sempre exagerado de brincadeira.",
      detalhada: "É o personagem chorão de um jogo chamado The Binding of Isaac. Um dos emotes mais antigos da Twitch." }]},

  { termo: "NotLikeThis", idioma: "en", categorias: ["streaming", "emocao"], variacoes: ["not like this", "notlikethis"], sentidos: [
    { simples: "\"Assim não!\" Frustração com uma derrota que parecia evitável.",
      detalhada: "Mostra uma pessoa com a cabeça baixa e a mão na testa. Aparece quando tudo estava indo bem e desandou no fim." }]},

  /* ---------------------------------------------------------- empolgacao --- */

  { termo: "PogChamp", idioma: "en", categorias: ["streaming", "gaming"], variacoes: ["pogchamp", "pog champ", "pogu", "PogU"], sentidos: [
    { simples: "Espanto e empolgação com uma jogada incrível.",
      detalhada: "Foi o emote de rosto surpreso mais famoso da Twitch. Deu origem a \"pog\" e \"poggers\". A imagem original foi retirada pela plataforma em 2021 e substituída várias vezes; \"PogU\" é uma das versões que o chat passou a usar no lugar." }]},

  { termo: "HYPERS", idioma: "en", categorias: ["streaming"], variacoes: ["hypers", "hyper s"], sentidos: [
    { simples: "Empolgação máxima, hype puro.",
      detalhada: "Mostra um rosto gritando de animação. Aparece em abertura de campeonato e em anúncio muito esperado." }]},

  { termo: "Clap", idioma: "en", categorias: ["streaming"], variacoes: ["clap", "peepoClap", "peepo clap"], sentidos: [
    { simples: "Palmas. Aprovação.",
      detalhada: "\"peepoClap\" é a versão com um bonequinho batendo palmas, e costuma ser mais afetuosa que irônica." }]},

  { termo: "catJAM", idioma: "en", categorias: ["streaming", "musica"], variacoes: ["cat jam", "catjam"], sentidos: [
    { simples: "O chat curtindo a música que está tocando.",
      detalhada: "Um gato branco balançando a cabeça no ritmo. Vira uma fileira inteira no chat quando a música agrada." }]},

  { termo: "pepeD", idioma: "en", categorias: ["streaming", "musica"], variacoes: ["pepe d", "peped"], sentidos: [
    { simples: "Dançando. O chat está gostando do som.",
      detalhada: "O sapo Pepe dançando com fones de ouvido. Primo do catJAM, com o mesmo uso." }]},

  { termo: "widepeepoHappy", idioma: "en", categorias: ["streaming", "emocao"], variacoes: ["wide peepo happy", "peepoHappy"], sentidos: [
    { simples: "Felicidade grande, quase infantil.",
      detalhada: "Um bonequinho sorrindo de orelha a orelha, esticado na horizontal. É a reação mais afetuosa do chat." }]},

  /* ------------------------------------------------------------ deboche --- */

  { termo: "WeirdChamp", idioma: "en", categorias: ["streaming", "critica"], variacoes: ["weird champ", "weirdchamp"], sentidos: [
    { simples: "\"Que estranho.\" Desaprovação de algo esquisito.",
      detalhada: "Rosto de decepção incrédula. Aparece quando alguém diz uma opinião muito ruim ou faz algo constrangedor." }]},

  { termo: "modCheck", idioma: "en", categorias: ["streaming", "humor"], variacoes: ["mod check", "modcheck"], sentidos: [
    { simples: "\"Quem perguntou?\" Ou \"cadê?\", procurando alguma coisa.",
      detalhada: "Um bonequinho fazendo sombra com a mão nos olhos, procurando no horizonte. Nasceu para chamar moderador ausente e hoje é usado sobretudo como deboche com quem contou algo que ninguém pediu." }]},

  { termo: "Jebaited", idioma: "en", categorias: ["streaming", "humor"], variacoes: ["jebaited", "jebeited"], sentidos: [
    { simples: "\"Caiu na pegadinha.\"",
      detalhada: "Rosto de um apresentador antigo da Twitch com expressão maliciosa. Usado quando alguém foi enganado de brincadeira." }]},

  { termo: "ResidentSleeper", idioma: "en", categorias: ["streaming", "critica"], variacoes: ["resident sleeper", "residentsleeper"], sentidos: [
    { simples: "\"Que tédio.\" A transmissão está parada.",
      detalhada: "Uma pessoa dormindo. Veio de uma maratona de jogo em que o jogador adormeceu ao vivo." }]},

  { termo: "DansGame", idioma: "en", categorias: ["streaming", "critica"], variacoes: ["dans game", "dansgame"], sentidos: [
    { simples: "Nojo ou desaprovação.",
      detalhada: "Rosto com expressão de repulsa. É dos emotes mais antigos da plataforma." }]},

  { termo: "PauseChamp", idioma: "en", categorias: ["streaming"], variacoes: ["pause champ", "pausechamp"], sentidos: [
    { simples: "Suspense. O chat esperando para ver o que vai acontecer.",
      detalhada: "Um rosto congelado no meio de uma reação. Aparece nos segundos antes de um resultado." }]},

  { termo: "5Head", idioma: "en", categorias: ["streaming", "elogio"], variacoes: ["five head", "5 head", "galaxy brain"], sentidos: [
    { simples: "\"Que jogada inteligente.\" Muitas vezes dito com ironia.",
      detalhada: "A imagem tem a testa alargada, sugerindo cérebro grande. Serve tanto para elogiar de verdade quanto para debochar de uma ideia burra." }]},

  { termo: "Aware", idioma: "en", categorias: ["streaming", "humor"], variacoes: ["unaware", "aware"], sentidos: [
    { simples: "\"Ele sabe o que está fazendo.\" Quase sempre irônico.",
      detalhada: "Usado quando a pessoa faz algo estranho de propósito, ou quando o chat quer sugerir que ela não faz ideia do que está fazendo." }]},

  { termo: "Chatting", idioma: "en", categorias: ["streaming", "critica"], variacoes: ["chatting", "yapping"], sentidos: [
    { simples: "\"Falando demais.\" Deboche com quem está discursando.",
      detalhada: "O emote mostra um bonequinho de boca aberta sem parar. \"Yapping\" tem o mesmo sentido e é mais usado fora da Twitch." }]},

  { termo: "Cinema", idioma: "en", categorias: ["streaming", "elogio"], variacoes: ["cinema", "peak cinema"], sentidos: [
    { simples: "\"Isso foi arte.\" Elogio exagerado a um momento da transmissão.",
      detalhada: "Dito com ironia na maioria das vezes, sobre um momento absolutamente banal." }]},

  { termo: "Kappa", idioma: "en", categorias: ["streaming", "humor"], variacoes: ["kappa", "kappaHD"], sentidos: [
    { simples: "Marca que a frase anterior era sarcasmo.",
      detalhada: "O rosto em preto e branco de um antigo funcionário da Twitch. É o emote mais famoso da plataforma e funciona como um \"estou brincando\" no fim da mensagem." }]},

  { termo: "TriHard", idioma: "en", categorias: ["streaming"], variacoes: ["tri hard", "trihard"], sentidos: [
    { simples: "Empolgação exagerada.",
      detalhada: "Rosto de um streamer com expressão animada. É um dos emotes mais usados da plataforma e também um dos mais usados de forma racista, o que faz muitos canais o bloquearem." }]},

  /* ------------------------------------------- vocabulario do chat, sem emote --- */

  { termo: "lurkar", idioma: "en", categorias: ["streaming"], variacoes: ["lurk", "lurker", "lurkando"], sentidos: [
    { simples: "Assistir à transmissão sem escrever nada no chat.",
      detalhada: "\"Vou lurkar\" avisa que a pessoa continua ali, só calada. É bem-vindo: lurker conta como espectador." }]},

  { termo: "primeiro", idioma: "pt-BR", categorias: ["streaming", "humor"], variacoes: ["first", "primeiro!"], sentidos: [
    { simples: "Quem escreve para marcar que chegou antes de todo mundo.",
      detalhada: "Vale para o chat da live e para o comentário do vídeo. É motivo de deboche na mesma medida em que é motivo de orgulho." }]},

  { termo: "corte", idioma: "pt-BR", categorias: ["streaming", "redes"], variacoes: ["cortes", "canal de cortes"], sentidos: [
    { simples: "Trecho curto tirado de uma live longa e publicado sozinho.",
      detalhada: "Existem canais que vivem só de publicar cortes de outras pessoas, com autorização. No Brasil, é assim que a maioria conhece um streamer antes de assistir a uma live inteira." }]},
];
