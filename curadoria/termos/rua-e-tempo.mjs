/*
 * Linguagem de rua e marcadores de tempo.
 *
 * As duas prateleiras mais vazias do catalogo (24 e 19), e as duas em que
 * isso mais incomodava: sao as categorias mais faladas do Brasil e as menos
 * cobertas aqui.
 *
 * POR QUE "LINGUAGEM DE RUA" ESTAVA COM 24
 *
 * Nao por falta de verbete, e sim por falta de etiqueta. Ha centenas de
 * termos de rua no dicionario, e a maioria entrou classificada por assunto
 * ("critica", "emocao", "humor") em vez de por registro. A prateleira ficou
 * vazia enquanto o conteudo estava espalhado.
 *
 * Este arquivo traz termos novos, e nao reclassifica os antigos: mudar a
 * categoria de um verbete ja publicado e uma decisao maior, que muda o que a
 * pessoa encontra num lugar onde ela ja tinha encontrado outra coisa.
 *
 * OS MARCADORES DE TEMPO SAO UM CASO A PARTE
 *
 * "Daqui a pouco" e "ja ja" nao sao girias: sao expressoes que todo brasileiro
 * usa e que ninguem consegue traduzir em minutos. Estao aqui porque sao
 * exatamente o tipo de coisa que deixa alguem esperando sem saber quanto
 * tempo, e porque para quem aprende portugues elas sao das mais confusas.
 */
export default [
  /* ---------------------------------------------------------------- rua --- */

  { termo: "quebrar o galho na rua", idioma: "pt-BR", categorias: ["rua", "trabalho"], variacoes: ["fazer um corre"], sentidos: [
    { simples: "Arrumar um jeito de ganhar dinheiro no dia a dia.",
      detalhada: "\"Corre\" é a atividade informal de quem se vira. \"Tô no meu corre\" quer dizer que a pessoa está trabalhando, mesmo sem emprego formal." }]},

  { termo: "de bobeira", idioma: "pt-BR", categorias: ["rua", "tempo"], variacoes: ["bobeando", "na bobeira"], sentidos: [
    { simples: "Sem fazer nada, distraído.",
      detalhada: "\"Tava de bobeira\" é a explicação de quem foi pego desatento." }]},

  { termo: "role da vida", idioma: "pt-BR", categorias: ["rua", "emocao"], variacoes: ["o rolê da vida"], sentidos: [
    { simples: "O jeito como a vida vai levando a pessoa.",
      detalhada: "\"O rolê da vida é assim\" é conformismo bem-humorado com o que não dá para mudar." }]},

  { termo: "pá", idioma: "pt-BR", categorias: ["rua"], variacoes: ["e pá", "e tal e pá"], sentidos: [
    { simples: "\"E coisa e tal\", para encerrar uma lista sem terminar.",
      detalhada: "\"Fomos lá, comemos, e pá\" resume o resto sem detalhar. Muito comum na fala do Rio de Janeiro." }]},

  { termo: "sangue bom", idioma: "pt-BR", categorias: ["rua", "elogio"], variacoes: ["sangue-bom", "sangue bom demais"], sentidos: [
    { simples: "Pessoa boa, de confiança.",
      detalhada: "Elogio forte e antigo, ainda muito usado. \"Ele é sangue bom\" atesta o caráter de alguém.",
      formal: "pessoa de confiança" }]},

  { termo: "morou", idioma: "pt-BR", categorias: ["rua"], variacoes: ["moró", "morou?"], sentidos: [
    { simples: "\"Entendeu?\", ou \"combinado\".",
      detalhada: "Serve como pergunta no fim da frase e como resposta de concordância." }]},

  { termo: "pilantragem", idioma: "pt-BR", categorias: ["rua", "critica"], variacoes: ["pilantra", "pilantrar"], sentidos: [
    { simples: "Malandragem desonesta.",
      detalhada: "Diferente de \"malandragem\", que pode ser esperteza admirada. \"Pilantragem\" é sempre negativo." }]},

  { termo: "fita", idioma: "pt-BR", categorias: ["rua"], variacoes: ["que fita", "fita séria"], sentidos: [
    { simples: "Assunto, situação ou confusão.",
      detalhada: "\"Que fita é essa?\" pergunta o que está acontecendo. \"Fita séria\" é problema grande." }]},

  { termo: "de responsa", idioma: "pt-BR", categorias: ["rua", "elogio"], variacoes: ["responsa", "é responsa"], sentidos: [
    { simples: "De qualidade, ou de confiança.",
      detalhada: "Vale para pessoa e para coisa: \"um lanche de responsa\" é um lanche muito bom." }]},

  { termo: "trocar ideia", idioma: "pt-BR", categorias: ["rua", "acao"], variacoes: ["trocar uma ideia", "trocando ideia"], sentidos: [
    { simples: "Conversar com alguém, de bate-papo ou a sério.",
      detalhada: "\"Vamo trocar uma ideia\" pode abrir uma conversa leve ou uma conversa difícil, e é o tom de quem convida que decide qual das duas." }]},

  { termo: "pegar a visão", idioma: "pt-BR", categorias: ["rua", "acao"], variacoes: ["pegou a visao", "pegar a visao"], sentidos: [
    { simples: "Entender como as coisas funcionam.",
      detalhada: "Mais que compreender um fato: é entender a lógica por trás de uma situação.",
      formal: "compreender" }]},

  { termo: "tá ligado", idioma: "pt-BR", categorias: ["rua"], variacoes: ["ta ligado", "tá ligado?", "cê tá ligado"], sentidos: [
    { simples: "\"Você sabe do que estou falando?\"",
      detalhada: "Usado no fim da frase o tempo todo, quase como pontuação. Raramente espera resposta." }]},

  { termo: "meu chapa", idioma: "pt-BR", categorias: ["rua"], variacoes: ["chapa", "parceiro", "parça"], sentidos: [
    { simples: "Jeito informal de chamar alguém, sem saber ou sem usar o nome.",
      detalhada: "\"Parça\" é a forma mais jovem; \"chapa\" soa mais antiga e é mais comum entre homens mais velhos." }]},

  /* --------------------------------------------------------------- tempo --- */

  { termo: "no aperto", idioma: "pt-BR", categorias: ["tempo", "trabalho"], variacoes: ["em cima da hora", "no sufoco"], sentidos: [
    { simples: "Com o prazo quase estourando.",
      detalhada: "\"Em cima da hora\" é a variante mais comum, e descreve tanto o prazo quanto quem chega atrasado." }]},

  { termo: "de última hora", idioma: "pt-BR", categorias: ["tempo"], variacoes: ["de ultima hora", "última hora"], sentidos: [
    { simples: "Feito ou decidido no fim do prazo.",
      detalhada: "\"Mudança de última hora\" é a que desorganiza o plano de todo mundo." }]},

  { termo: "fim de semana emendado", idioma: "pt-BR", categorias: ["tempo"], variacoes: ["feriadão", "feriadao"], sentidos: [
    { simples: "Fim de semana esticado por um feriado.",
      detalhada: "\"Feriadão\" é como se chama o resultado: três ou quatro dias seguidos de folga." }]},

  { termo: "dia sim, dia não", idioma: "pt-BR", categorias: ["tempo"], variacoes: ["dia sim dia nao", "em dias alternados"], sentidos: [
    { simples: "Um dia sim e o seguinte não, alternando.",
      detalhada: "Aparece muito em instrução de remédio e de exercício.",
      formal: "em dias alternados" }]},

  { termo: "toda hora", idioma: "pt-BR", categorias: ["tempo"], variacoes: ["a toda hora", "o tempo todo"], sentidos: [
    { simples: "Com muita frequência, sem parar.",
      detalhada: "Costuma vir com queixa: \"ele manda mensagem toda hora\"." }]},

  { termo: "nunca mais", idioma: "pt-BR", categorias: ["tempo", "humor"], variacoes: ["faz tempo", "faz um tempão"], sentidos: [
    { simples: "Há muito tempo, num tom de exagero.",
      detalhada: "\"Nunca mais te vejo\" não quer dizer nunca: quer dizer que faz tempo demais, e é uma cobrança afetuosa." }]},

  { termo: "hoje em dia", idioma: "pt-BR", categorias: ["tempo"], variacoes: ["nos dias de hoje"], sentidos: [
    { simples: "Na época atual, em contraste com o passado.",
      detalhada: "Quase sempre abre uma comparação com \"antigamente\".",
      formal: "atualmente" }]},

  { termo: "de manhã cedo", idioma: "pt-BR", categorias: ["tempo"], variacoes: ["de manha cedo", "cedinho"], sentidos: [
    { simples: "No começo da manhã.",
      detalhada: "\"Cedinho\" é ainda mais cedo, e costuma significar antes das sete." }]},

  { termo: "altas horas", idioma: "pt-BR", categorias: ["tempo"], variacoes: ["a essas horas", "essas horas"], sentidos: [
    { simples: "Muito tarde da noite ou de madrugada.",
      detalhada: "\"Chegou altas horas\" é reclamação de quem estava esperando." }]},

  { termo: "num piscar de olhos", idioma: "pt-BR", categorias: ["tempo"], variacoes: ["num piscar", "rapidinho"], sentidos: [
    { simples: "Muito rápido, quase instantâneo.",
      detalhada: "\"Rapidinho\" é a forma coloquial e, como \"já já\", costuma demorar bem mais do que promete." }]},

  { termo: "empurrar com a barriga", idioma: "pt-BR", categorias: ["tempo", "critica"], variacoes: ["empurrando com a barriga"], sentidos: [
    { simples: "Ir adiando um problema sem resolver.",
      detalhada: "Diferente de enrolar alguém: aqui a pessoa está adiando para si mesma, geralmente algo que sabe que precisa fazer." }]},

  { termo: "virada de ano", idioma: "pt-BR", categorias: ["tempo", "familia"], variacoes: ["réveillon", "reveillon", "virada"], sentidos: [
    { simples: "A passagem de um ano para o outro, e a festa dela.",
      detalhada: "\"Onde você vai passar a virada?\" é a pergunta de dezembro no Brasil inteiro." }]},

  { termo: "fora de época", idioma: "pt-BR", categorias: ["tempo"], variacoes: ["fora de epoca", "fora de temporada"], sentidos: [
    { simples: "Em período diferente do habitual.",
      detalhada: "Vale para fruta, viagem e chuva. Viajar fora de época é o jeito de gastar menos." }]},
];
