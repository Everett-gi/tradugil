/*
 * Comida e bebida, esporte e ambiente escolar.
 *
 * As tres prateleiras que restavam magras (27, 25 e 28), num arquivo so.
 *
 * O QUE ELAS TEM EM COMUM
 *
 * Sao as tres categorias de vocabulario que a pessoa idosa MAIS conhece e
 * onde ela mais erra mesmo assim, porque o sentido mudou. "Marmita" ela sabe;
 * "marmitex" e "quentinha" mudam de nome conforme o estado. "Craque" ela usa
 * desde sempre; "pendurado" e recente. "Prova" ela sabe; "rec" e "DP" nao
 * existiam.
 *
 * Por isso muitas explicacoes aqui dizem explicitamente o que MUDOU, e nao so
 * o que a palavra significa. Para quem ja conhece metade do termo, essa e a
 * metade util.
 *
 * COMIDA TEM UM PROBLEMA REGIONAL SERIO
 *
 * O mesmo objeto tem nome diferente em cada estado, e a pessoa que muda de
 * cidade descobre isso pedindo errado no balcao. Onde isso acontece, o
 * verbete lista as variantes em vez de eleger uma como certa: nao ha certa.
 */
export default [
  /* --------------------------------------------------------------- comida --- */

  { termo: "pf", idioma: "pt-BR", categorias: ["comida"], variacoes: ["prato feito", "pê efe", "comercial"], sentidos: [
    { simples: "O prato padrão de restaurante popular: arroz, feijão, carne e salada.",
      detalhada: "Sigla de \"prato feito\". Em algumas regiões se chama \"comercial\" ou \"executivo\"." }]},

  { termo: "café completo", idioma: "pt-BR", categorias: ["comida", "regional"], variacoes: ["cafe completo", "café reforçado"], sentidos: [
    { simples: "Café da manhã com pão, queijo, bolo, frutas e mais.",
      detalhada: "Comum em Minas Gerais e no interior. Não é lanche: é refeição." }]},

  { termo: "salgado", idioma: "pt-BR", categorias: ["comida"], variacoes: ["salgadinho", "salgadinhos"], sentidos: [
    { simples: "Coxinha, esfiha, pastel e afins, vendidos em padaria.",
      detalhada: "\"Salgadinho\" tem dois sentidos que confundem: o de padaria e o de pacote de milho industrializado." }]},

  { termo: "pingado", idioma: "pt-BR", categorias: ["comida", "regional"], variacoes: ["cafe com leite", "café com leite"], sentidos: [
    { simples: "Café com um pouco de leite, servido em copo.",
      detalhada: "\"Pingado\" em São Paulo, \"média\" no Rio, que também é o nome do pão com manteiga acompanhado. Pedido de balcão de padaria." }]},

  { termo: "tirar o pé da jaca", idioma: "pt-BR", categorias: ["comida", "humor"], variacoes: ["voltar pra dieta"], sentidos: [
    { simples: "Voltar a se cuidar depois de um período de exageros.",
      detalhada: "É o movimento contrário de \"enfiar o pé na jaca\", e costuma vir em janeiro." }]},

  { termo: "vaquinha do lanche", idioma: "pt-BR", categorias: ["comida", "escolar"], variacoes: ["rachar o lanche"], sentidos: [
    { simples: "Juntar dinheiro entre colegas para comprar comida.",
      detalhada: "Cena clássica de intervalo escolar e de escritório." }]},

  { termo: "bebum", idioma: "pt-BR", categorias: ["comida", "critica"], variacoes: ["bêbum", "bebo"], sentidos: [
    { simples: "Pessoa embriagada.",
      detalhada: "Palavra antiga e informal. Dita a sério é ofensa; entre amigos, costuma ser brincadeira." }]},

  { termo: "levar o doce", idioma: "pt-BR", categorias: ["comida", "familia"], variacoes: ["levar alguma coisa"], sentidos: [
    { simples: "Contribuir com um prato quando se é convidado.",
      detalhada: "Regra não escrita de festa brasileira: quem é convidado pergunta o que pode levar." }]},

  /* -------------------------------------------------------------- esporte --- */

  { termo: "pelada", idioma: "pt-BR", categorias: ["esporte"], variacoes: ["peladinha", "jogar uma pelada"], sentidos: [
    { simples: "Jogo de futebol informal entre amigos.",
      detalhada: "Sem árbitro, sem uniforme e sem hora certa para acabar. É como quase todo brasileiro joga bola." }]},

  { termo: "racha", idioma: "pt-BR", categorias: ["esporte", "regional"], variacoes: ["rachão", "rachao"], sentidos: [
    { simples: "Outro nome para o futebol informal.",
      detalhada: "Mais comum no Sul e no Sudeste. Cuidado: \"racha\" também é corrida ilegal de carro, e o contexto separa." }]},

  { termo: "bater falta", idioma: "pt-BR", categorias: ["esporte"], variacoes: ["cobrar falta", "na barreira"], sentidos: [
    { simples: "Chutar a bola parada depois de uma infração.",
      detalhada: "A \"barreira\" é a fileira de jogadores que se posta na frente para atrapalhar." }]},

  { termo: "pintura", idioma: "pt-BR", categorias: ["esporte", "elogio"], variacoes: ["golaço", "golaco", "que pintura"], sentidos: [
    { simples: "Gol muito bonito.",
      detalhada: "\"Golaço\" é o mais comum. \"Pintura\" acrescenta a ideia de obra de arte." }]},

  { termo: "frango", idioma: "pt-BR", categorias: ["esporte", "critica"], variacoes: ["tomou frango", "frangueiro"], sentidos: [
    { simples: "Falha boba do goleiro num chute fácil.",
      detalhada: "\"Frangueiro\" é o goleiro que erra assim com frequência. Fora do futebol, virou sinônimo de erro grosseiro." }]},

  { termo: "cair de pé", idioma: "pt-BR", categorias: ["esporte", "emocao"], variacoes: ["cair de pe", "sair de cabeça erguida"], sentidos: [
    { simples: "Perder, mas com dignidade.",
      detalhada: "Dito quando o time joga bem e é eliminado. Vale muito além do esporte." }]},

  { termo: "escalação", idioma: "pt-BR", categorias: ["esporte"], variacoes: ["escalacao", "escalar o time"], sentidos: [
    { simples: "A lista de quem começa jogando.",
      detalhada: "Sai algumas horas antes da partida e é o assunto do dia entre torcedores." }]},

  { termo: "artilheiro", idioma: "pt-BR", categorias: ["esporte", "elogio"], variacoes: ["artilharia", "goleador"], sentidos: [
    { simples: "Quem faz mais gols numa competição.",
      detalhada: "\"Artilharia\" é a lista dos maiores goleadores.",
      formal: "goleador" }]},

  { termo: "tabelinha", idioma: "pt-BR", categorias: ["esporte"], variacoes: ["dar uma tabela", "tabela"], sentidos: [
    { simples: "Jogada em que dois jogadores trocam passes rápidos para passar pelo adversário.",
      detalhada: "Fora do campo, \"fazer tabelinha\" é combinar algo com outra pessoa antecipadamente." }]},

  { termo: "amarelar", idioma: "pt-BR", categorias: ["esporte", "critica"], variacoes: ["amarelou", "amarelão"], sentidos: [
    { simples: "Perder a coragem na hora decisiva.",
      detalhada: "Usado muito além do esporte, para quem desistiu de um confronto ou de uma decisão." }]},

  { termo: "de bicicleta", idioma: "pt-BR", categorias: ["esporte"], variacoes: ["bicicleta", "gol de bicicleta"], sentidos: [
    { simples: "Chute dado com o corpo no ar, de costas para o gol.",
      detalhada: "Uma das jogadas mais difíceis e mais celebradas do futebol." }]},

  { termo: "vestiário", idioma: "pt-BR", categorias: ["esporte"], variacoes: ["vestiario", "clima no vestiário"], sentidos: [
    { simples: "O lugar onde o time se troca, e por extensão o clima interno do grupo.",
      detalhada: "\"Perdeu o vestiário\" quer dizer que o técnico perdeu a autoridade sobre os jogadores." }]},

  /* --------------------------------------------------------------- escola --- */

  { termo: "chamada oral", idioma: "pt-BR", categorias: ["escolar"], variacoes: ["arguição", "prova oral"], sentidos: [
    { simples: "Avaliação em que o aluno responde falando, na frente da turma.",
      detalhada: "É a mais temida por quem tem vergonha de falar em público." }]},

  { termo: "média", idioma: "pt-BR", categorias: ["escolar"], variacoes: ["media", "fechar a média", "tirar média"], sentidos: [
    { simples: "A nota necessária para passar de ano.",
      detalhada: "\"Fechei a média\" é ter alcançado o mínimo. Também se diz \"passei na média\"." }]},

  { termo: "bimestre", idioma: "pt-BR", categorias: ["escolar", "tempo"], variacoes: ["trimestre", "semestre"], sentidos: [
    { simples: "O período em que o ano letivo é dividido.",
      detalhada: "Cada escola usa um: dois meses, três meses ou seis. As notas fecham no fim de cada um." }]},

  { termo: "conselho de classe", idioma: "pt-BR", categorias: ["escolar"], variacoes: ["conselho"], sentidos: [
    { simples: "Reunião dos professores para decidir quem passa e quem repete.",
      detalhada: "É onde um aluno na nota limite pode ser aprovado por avaliação do conjunto dos professores." }]},

  { termo: "colinha", idioma: "pt-BR", categorias: ["escolar"], variacoes: ["cola", "cola eletrônica"], sentidos: [
    { simples: "O papelzinho escondido com as respostas da prova.",
      detalhada: "Hoje o mais comum é o celular, o que mudou a fiscalização mais do que mudou a prática." }]},

  { termo: "lousa", idioma: "pt-BR", categorias: ["escolar", "regional"], variacoes: ["quadro", "quadro-negro", "quadro branco"], sentidos: [
    { simples: "O quadro em que o professor escreve.",
      detalhada: "\"Lousa\" em São Paulo, \"quadro\" na maior parte do país." }]},

  { termo: "carteira", idioma: "pt-BR", categorias: ["escolar"], variacoes: ["carteira escolar", "classe"], sentidos: [
    { simples: "A mesa individual do aluno.",
      detalhada: "Em algumas regiões se chama \"classe\", o que confunde com a turma." }]},

  { termo: "grêmio", idioma: "pt-BR", categorias: ["escolar"], variacoes: ["gremio", "grêmio estudantil"], sentidos: [
    { simples: "A organização de alunos que representa a turma diante da escola.",
      detalhada: "Tem eleição, chapa e campanha. Para muita gente, é o primeiro contato com política." }]},

  { termo: "reforço", idioma: "pt-BR", categorias: ["escolar"], variacoes: ["reforco", "aula de reforço"], sentidos: [
    { simples: "Aula extra para quem está com dificuldade numa matéria.",
      detalhada: "Pode ser oferecida pela escola ou contratada por fora." }]},

  { termo: "prova substitutiva", idioma: "pt-BR", categorias: ["escolar"], variacoes: ["substitutiva", "segunda chamada"], sentidos: [
    { simples: "Prova aplicada a quem faltou na data original.",
      detalhada: "Diferente da recuperação: aqui não houve nota ruim, houve ausência." }]},

  { termo: "formatura", idioma: "pt-BR", categorias: ["escolar", "familia"], variacoes: ["colação", "colação de grau", "baile de formatura"], sentidos: [
    { simples: "A cerimônia de conclusão do curso.",
      detalhada: "\"Colação de grau\" é a parte oficial; o baile é a festa. As duas costumam ser em dias diferentes." }]},
];
