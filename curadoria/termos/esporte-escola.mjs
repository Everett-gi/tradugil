/*
 * Esporte e ambiente escolar.
 *
 * Duas prateleiras magras no mesmo arquivo (8 e 13 verbetes) porque o
 * vocabulario se cruza muito: boa parte da giria de esporte que a Marlene
 * ouve vem do neto falando de jogo na escola, e boa parte da giria escolar
 * descreve o intervalo, que e onde se joga bola.
 *
 * O futebol tem um problema proprio de traducao: muitos termos sao antigos e
 * a pessoa idosa ate conhece, mas o sentido MUDOU. "Craque" continua igual;
 * "pendurado" e "VAR" sao novos; e "dar chapeu" hoje aparece muito mais fora
 * do campo do que dentro. As explicacoes dizem qual e o uso atual, e nao so
 * o de origem.
 */
export default [
  /* ------------------------------------------------------------ esporte --- */

  { termo: "pendurado", idioma: "pt-BR", categorias: ["esporte"], variacoes: ["pendurada", "pendurados"], sentidos: [
    { simples: "Jogador que leva mais um cartão amarelo e fica fora do próximo jogo.",
      detalhada: "\"Está pendurado\" avisa que ele precisa se cuidar em campo. Fora do futebol, virou jeito de dizer que a pessoa está por um fio." }]},

  { termo: "var", idioma: "pt-BR", categorias: ["esporte"], variacoes: ["o var", "vaar"], sentidos: [
    { simples: "O árbitro de vídeo, que revê lances pela televisão.",
      detalhada: "Sigla de \"video assistant referee\". \"Foi pro VAR\" quer dizer que o lance está sendo revisto e o jogo parou." }]},

  { termo: "dar chapéu", idioma: "pt-BR", categorias: ["esporte", "humor"], variacoes: ["dar chapeu", "deu um chapéu"], sentidos: [
    { simples: "Passar a bola por cima do adversário e pegá-la do outro lado.",
      detalhada: "Drible clássico. Fora do campo, \"dar um chapéu\" em alguém é enganar essa pessoa, e esse uso é bem mais comum hoje." }]},

  { termo: "matar no peito", idioma: "pt-BR", categorias: ["esporte"], variacoes: ["matou no peito"], sentidos: [
    { simples: "Parar a bola usando o peito para dominá-la.",
      detalhada: "\"Matar\" a bola é fazer ela parar. Vale também no peito, na coxa e no pé." }]},

  { termo: "craque", idioma: "pt-BR", categorias: ["esporte", "elogio"], variacoes: ["craquinho"], sentidos: [
    { simples: "Jogador muito bom.",
      detalhada: "Palavra antiga e ainda corrente. Fora do esporte, elogia quem é excelente em qualquer coisa.",
      formal: "grande jogador" }]},

  { termo: "perna de pau", idioma: "pt-BR", categorias: ["esporte", "critica"], variacoes: ["pernas de pau"], sentidos: [
    { simples: "Jogador ruim, sem habilidade.",
      detalhada: "Crítica direta, e o oposto exato de craque." }]},

  { termo: "gol contra", idioma: "pt-BR", categorias: ["esporte"], variacoes: ["gol-contra"], sentidos: [
    { simples: "Gol marcado pelo jogador no próprio time.",
      detalhada: "Fora do futebol, virou expressão para quando alguém se prejudica sozinho." }]},

  { termo: "virar o jogo", idioma: "pt-BR", categorias: ["esporte", "acao"], variacoes: ["virou o jogo"], sentidos: [
    { simples: "Estar perdendo e passar a ganhar.",
      detalhada: "Muito usada fora do esporte, para qualquer situação que se inverte a favor de alguém." }]},

  { termo: "tirar o time de campo", idioma: "pt-BR", categorias: ["esporte", "acao"], variacoes: ["tirei meu time de campo"], sentidos: [
    { simples: "Desistir de uma situação para não se prejudicar.",
      detalhada: "\"Tirei meu time de campo\" é dizer que abandonou uma disputa ou um relacionamento antes de piorar." }]},

  { termo: "pendura no travessão", idioma: "pt-BR", categorias: ["esporte"], variacoes: ["na trave", "pegou na trave"], sentidos: [
    { simples: "Chute que bate na trave e não entra.",
      detalhada: "\"Foi na trave\" também descreve qualquer coisa que quase deu certo." }]},

  { termo: "clássico", idioma: "pt-BR", categorias: ["esporte"], variacoes: ["classico", "clássicos"], sentidos: [
    { simples: "Jogo entre dois times rivais da mesma cidade ou estado.",
      detalhada: "Tem peso maior que um jogo comum, independentemente da posição na tabela." }]},

  { termo: "zebra", idioma: "pt-BR", categorias: ["esporte"], variacoes: ["deu zebra"], sentidos: [
    { simples: "Resultado inesperado, em que o pior ganha do favorito.",
      detalhada: "\"Deu zebra\" saiu do esporte e hoje descreve qualquer surpresa ruim." }]},

  { termo: "camisa 10", idioma: "pt-BR", categorias: ["esporte", "elogio"], variacoes: ["camisa dez"], sentidos: [
    { simples: "O jogador mais habilidoso e criativo do time.",
      detalhada: "Fora do campo, chamar alguém de camisa 10 é dizer que é a pessoa mais importante do grupo." }]},

  { termo: "banco", idioma: "pt-BR", categorias: ["esporte"], variacoes: ["ficar no banco", "banco de reservas"], sentidos: [
    { simples: "O lugar dos jogadores que não estão jogando.",
      detalhada: "\"Ficou no banco\" quer dizer que não foi escalado. Fora do esporte, é ser deixado de fora de alguma coisa." }]},

  { termo: "treta de arquibancada", idioma: "pt-BR", categorias: ["esporte", "critica"], variacoes: ["treta na arquibancada"], sentidos: [
    { simples: "Confusão entre torcidas durante um jogo.",
      detalhada: "\"Treta\" sozinho é briga ou confusão em qualquer contexto." }]},

  { termo: "bater um bolão", idioma: "pt-BR", categorias: ["esporte", "elogio"], variacoes: ["bateu um bolao"], sentidos: [
    { simples: "Jogar futebol muito bem.",
      detalhada: "Expressão de quem assiste, dita como elogio depois de uma boa partida." }]},

  /* ------------------------------------------------------------- escola --- */

  { termo: "prova relâmpago", idioma: "pt-BR", categorias: ["escolar"], variacoes: ["prova relampago", "prova surpresa"], sentidos: [
    { simples: "Prova aplicada sem aviso prévio.",
      detalhada: "O professor anuncia na hora, justamente para ninguém estudar só na véspera." }]},

  { termo: "recuperação", idioma: "pt-BR", categorias: ["escolar"], variacoes: ["recuperacao", "recupera", "rec"], sentidos: [
    { simples: "Prova extra para quem ficou abaixo da nota necessária.",
      detalhada: "\"Ficou de rec\" é a forma curta, e é a notícia que ninguém quer dar em casa." }]},

  { termo: "trabalho em grupo", idioma: "pt-BR", categorias: ["escolar", "humor"], variacoes: ["trampo em grupo"], sentidos: [
    { simples: "Tarefa que vários alunos fazem juntos.",
      detalhada: "Virou piada recorrente na internet, pela queixa de que sempre uma pessoa faz tudo sozinha." }]},

  { termo: "nerd", idioma: "en", categorias: ["escolar", "descricao"], variacoes: ["nerds", "nerdola"], sentidos: [
    { simples: "Pessoa muito interessada em estudo ou em algum assunto específico.",
      detalhada: "Já foi ofensa e hoje é quase sempre neutro ou até elogio, dito pela própria pessoa." }]},

  { termo: "cdf", idioma: "pt-BR", categorias: ["escolar", "descricao"], variacoes: ["cê dê efe"], sentidos: [
    { simples: "Aluno muito dedicado aos estudos.",
      detalhada: "Sigla de \"cabeça de ferro\". Diferente de \"nerd\", ainda costuma vir com uma ponta de deboche." }]},

  { termo: "pé de sala", idioma: "pt-BR", categorias: ["escolar", "humor"], variacoes: ["pe de sala"], sentidos: [
    { simples: "Aluno que passa a aula toda conversando.",
      detalhada: "Expressão regional, comum no Nordeste, para quem atrapalha a turma." }]},

  { termo: "chamada", idioma: "pt-BR", categorias: ["escolar"], variacoes: ["fazer a chamada"], sentidos: [
    { simples: "O momento em que o professor confere quem está presente.",
      detalhada: "\"Já deu a chamada?\" é a pergunta de quem chegou atrasado." }]},

  { termo: "aula vaga", idioma: "pt-BR", categorias: ["escolar"], variacoes: ["janela", "horário vago"], sentidos: [
    { simples: "Horário sem aula, porque o professor faltou.",
      detalhada: "É a melhor notícia possível no meio da manhã." }]},

  { termo: "ficar de dp", idioma: "pt-BR", categorias: ["escolar"], variacoes: ["dp", "dependência"], sentidos: [
    { simples: "Passar de ano devendo uma matéria, que precisa ser refeita.",
      detalhada: "Sigla de \"dependência\". Comum no ensino médio e na faculdade." }]},

  { termo: "passar raspando", idioma: "pt-BR", categorias: ["escolar", "humor"], variacoes: ["passei raspando"], sentidos: [
    { simples: "Ser aprovado com a nota mínima.",
      detalhada: "Dito com alívio, não com orgulho. Vale também para qualquer coisa que deu certo por pouco." }]},

  { termo: "tirar de letra", idioma: "pt-BR", categorias: ["escolar", "elogio"], variacoes: ["tirou de letra"], sentidos: [
    { simples: "Fazer alguma coisa difícil com facilidade.",
      detalhada: "\"Tirei a prova de letra\" quer dizer que foi tranquila. Vale bem além da escola." }]},

  { termo: "queimar o filme", idioma: "pt-BR", categorias: ["escolar", "critica"], variacoes: ["queimou o filme", "queimar o filme de alguém"], sentidos: [
    { simples: "Estragar a reputação de alguém, ou a própria.",
      detalhada: "\"Queimei meu filme\" é ter passado vergonha de um jeito que os outros vão lembrar." }]},

  { termo: "grupo da sala", idioma: "pt-BR", categorias: ["escolar", "redes"], variacoes: ["grupo da turma"], sentidos: [
    { simples: "A conversa em grupo onde a turma combina tudo.",
      detalhada: "É onde circulam as datas de prova, as fotos do quadro e as brincadeiras. Também é onde nasce boa parte das brigas da turma." }]},

  { termo: "intervalo", idioma: "pt-BR", categorias: ["escolar"], variacoes: ["recreio"], sentidos: [
    { simples: "A pausa entre as aulas.",
      detalhada: "\"Recreio\" é a palavra do fundamental; \"intervalo\" é a do médio em diante." }]},
];
