/*
 * TikTok: a plataforma e o vocabulario que nasce nela.
 *
 * POR QUE ESTE ARQUIVO E O MAIS DIFICIL DE ESCREVER
 *
 * O TikTok e onde a giria nasce hoje, e boa parte dela e deliberadamente sem
 * sentido. "Skibidi" nao quer dizer nada especifico; "6-7" e um bordao cuja
 * graca esta em NAO ter significado. Isso quebra o formato de dicionario:
 * nao da para dar uma definicao limpa de uma palavra cuja funcao e nao ter
 * definicao.
 *
 * A saida foi explicar a FUNCAO em vez do significado. Para a Marlene, saber
 * que "skibidi" nao significa nada e que o neto nao esta falando codigo e
 * mais util do que qualquer definicao inventada. Dizer "nao tem sentido
 * fixo" e a informacao correta, e e o que impede a pessoa de achar que
 * entendeu errado.
 *
 * A SEGUNDA DIFICULDADE: A VELOCIDADE
 *
 * Metade deste arquivo vai estar velho em dois anos. Isso e esperado e nao e
 * problema: e o nivel 5 da cascata, a fila de termos desconhecidos, que
 * mantem o dicionario vivo. O que NAO pode acontecer e a explicacao afirmar
 * que algo esta "em alta" hoje: por isso os verbetes datam quando datam
 * ("estourou em 2025") em vez de dizer "atualmente".
 *
 * FONTES
 *
 * Conferido em setembro de 2026. "6-7" foi eleita palavra do ano de 2025
 * pelo Dictionary.com. Demais termos conferidos contra conversarcomadolescente.com.br,
 * slangwise e a cobertura de imprensa sobre girias da Geracao Alpha.
 * Deixei de fora varios termos que aparecem em listas de blog e que nao
 * consegui confirmar em uso real: lista de giria inflada com invencao e pior
 * que lista curta.
 */
export default [
  /* ------------------------------------------------- a plataforma em si --- */

  { termo: "dueto", idioma: "pt-BR", categorias: ["redes"], variacoes: ["duetar", "duet", "duetou"], sentidos: [
    { simples: "Vídeo gravado ao lado do vídeo de outra pessoa, na mesma tela.",
      detalhada: "A tela fica dividida em duas. Serve para responder, completar ou debochar do vídeo original." }]},

  { termo: "costura", idioma: "pt-BR", categorias: ["redes"], variacoes: ["stitch", "costurar"], sentidos: [
    { simples: "Pegar um pedaço do vídeo de outra pessoa e continuar a partir dele.",
      detalhada: "Diferente do dueto: aqui o trecho aparece primeiro e o seu vídeo vem depois, e não lado a lado." }]},

  { termo: "som", idioma: "pt-BR", categorias: ["redes", "musica"], variacoes: ["usar o som", "áudio original"], sentidos: [
    { simples: "O áudio de um vídeo, que outras pessoas podem reaproveitar.",
      detalhada: "\"Usar esse som\" é gravar o próprio vídeo com aquele áudio. É assim que uma música ou uma frase se espalha pela plataforma." }]},

  { termo: "floptok", idioma: "en", categorias: ["redes", "humor"], variacoes: ["flopar", "flopou", "flop"], sentidos: [
    { simples: "A fase em que os vídeos de alguém quase não são vistos.",
      detalhada: "\"Flopar\" é fracassar. \"Tô no floptok\" é reclamar, com humor, de que o sistema parou de mostrar seus vídeos." }]},

  { termo: "algoritmado", idioma: "pt-BR", categorias: ["redes", "critica"], variacoes: ["algoritmada", "feito pro algoritmo"], sentidos: [
    { simples: "Conteúdo que parece existir só para agradar ao sistema de recomendação.",
      detalhada: "É crítica: diz que aquilo foi calculado para prender atenção, e não para ter alguma coisa a dizer." }]},

  { termo: "bed rot", idioma: "en", categorias: ["redes", "emocao"], variacoes: ["bedrot", "apodrecer na cama"], sentidos: [
    { simples: "Passar horas na cama no celular, sem fazer nada.",
      detalhada: "Descrito como descanso por quem faz e como sinal de desânimo por quem observa. Quando vira rotina de todo dia, vale conversar sem repreender." }]},

  /* --------------------------------------- o vocabulario que nasce nela --- */

  { termo: "6-7", idioma: "en", categorias: ["redes", "humor"], variacoes: ["67", "six seven", "seis sete"], sentidos: [
    { simples: "Bordão que não significa nada, dito para interromper e fazer graça.",
      detalhada: "Vem acompanhado de um gesto com as duas mãos viradas para cima, subindo e descendo alternadamente. A graça está justamente em não ter sentido, e responder \"o que isso quer dizer?\" é parte da brincadeira. Foi eleita a palavra do ano de 2025 pelo Dictionary.com." }]},

  { termo: "aura", idioma: "pt-BR", categorias: ["redes", "elogio"], variacoes: ["farmar aura", "aura points", "perdeu aura"], sentidos: [
    { simples: "A presença e o carisma de alguém, contados como se fossem pontos.",
      detalhada: "\"Ganhou aura\" é ter feito algo impressionante; \"perdeu aura\" é ter passado vergonha. \"Farmar aura\" é agir de propósito para parecer interessante, e é quase sempre dito com deboche." }]},

  { termo: "cooked", idioma: "en", categorias: ["redes", "emocao"], variacoes: ["we're cooked", "tá cooked", "cozinhado"], sentidos: [
    { simples: "Ferrado, sem saída.",
      detalhada: "\"Tô cooked\" é dizer que se deu mal, geralmente antes de uma prova ou de uma conversa difícil." }]},

  { termo: "lock in", idioma: "en", categorias: ["redes", "acao"], variacoes: ["lockin", "lockado", "vou lockar"], sentidos: [
    { simples: "Focar de verdade em alguma coisa.",
      detalhada: "\"Preciso lockar\" é anunciar que vai parar de se distrair e estudar ou trabalhar para valer." }]},

  { termo: "glazing", idioma: "en", categorias: ["redes", "critica"], variacoes: ["glazar", "glazando", "glaze"], sentidos: [
    { simples: "Elogiar alguém de forma exagerada, a ponto de constranger.",
      detalhada: "É acusação: quem faz glazing está defendendo alguém além do razoável, e o chat cobra por isso." }]},

  { termo: "clout", idioma: "en", categorias: ["redes", "critica"], variacoes: ["cloutzeiro", "atrás de clout"], sentidos: [
    { simples: "Fama e influência na internet.",
      detalhada: "\"Fazer por clout\" acusa alguém de agir só para ganhar atenção, e não por acreditar no que faz." }]},

  { termo: "the ick", idioma: "en", categorias: ["redes", "relacionamento"], variacoes: ["ick", "deu ick", "bateu o ick"], sentidos: [
    { simples: "Quando um detalhe bobo faz a atração por alguém desaparecer de repente.",
      detalhada: "Pode ser qualquer coisa mínima, e a pessoa mesma costuma achar o motivo irracional." }]},

  { termo: "left on read", idioma: "en", categorias: ["redes", "relacionamento"], variacoes: ["visualizou e não respondeu", "vácuo"], sentidos: [
    { simples: "Alguém leu a sua mensagem e não respondeu.",
      detalhada: "No Brasil se diz \"deu vácuo\" ou \"me deixou no vácuo\", com o mesmo sentido." }]},

  { termo: "looksmaxxing", idioma: "en", categorias: ["redes", "descricao", "atencao"], variacoes: ["looksmax", "maxxing"], sentidos: [
    { simples: "Tentar melhorar ao máximo a própria aparência.",
      detalhada: "Vai de cuidados com a pele e postura até dietas e procedimentos. Circula em comunidades que cobram padrões muito rígidos, e por isso costuma vir junto de comparação e insatisfação com o corpo. Vale prestar atenção quando aparece com frequência.",
      formal: "busca por aparência ideal" }], risco: true},

  { termo: "crash out", idioma: "en", categorias: ["redes", "emocao"], variacoes: ["crashou", "crashando", "crash-out"], sentidos: [
    { simples: "Perder o controle emocional e agir por impulso.",
      detalhada: "\"Vou crashar\" avisa que a pessoa está no limite. Costuma ser dito com humor, mas quando se repete é sinal de que algo não está bem." }]},

  { termo: "vibe check", idioma: "en", categorias: ["redes", "emocao"], variacoes: ["checar a vibe"], sentidos: [
    { simples: "Avaliar rapidamente o clima de uma pessoa ou de um lugar.",
      detalhada: "\"Passou no vibe check\" é ter causado boa impressão." }]},

];
