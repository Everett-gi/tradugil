/*
 * YouTube: o vocabulario de quem cria e de quem assiste.
 *
 * POR QUE ESTE ARQUIVO IMPORTA MAIS DO QUE PARECE
 *
 * O YouTube e a plataforma que a Marlene provavelmente ja usa. Ela pode
 * nunca ter aberto a Twitch e nao ter conta em rede nenhuma, e ainda assim
 * ver video de receita e de igreja no YouTube todo dia. E ali que ela vai
 * esbarrar nestas palavras primeiro, lidas na tela ou ditas pelo neto.
 *
 * Boa parte deste vocabulario nao e giria de adolescente: e jargao de
 * profissao. "Retencao", "monetizar", "strike" e "thumb" sao termos tecnicos
 * de quem trabalha com video, e o neto que fala deles esta falando do
 * trabalho dele. Explicar isso e explicar o que a pessoa da familia faz da
 * vida, o que e um uso do produto que a especificacao nao previu e que vale
 * atender.
 *
 * As explicacoes evitam supor conhecimento de plataforma: nao pressupoem que
 * a pessoa saiba o que e algoritmo, engajamento ou feed.
 */
export default [
  { termo: "retenção", idioma: "pt-BR", categorias: ["redes", "streaming"], variacoes: ["retencao", "retenção de audiência"], sentidos: [
    { simples: "Quanto tempo as pessoas assistem antes de desistir do vídeo.",
      detalhada: "É o número que mais importa para quem vive de vídeo: retenção baixa faz a plataforma parar de recomendar, mesmo que o vídeo tenha muitos cliques." }]},

  { termo: "monetizar", idioma: "pt-BR", categorias: ["redes", "trabalho"], variacoes: ["monetizado", "monetização", "desmonetizar"], sentidos: [
    { simples: "Ganhar dinheiro com um vídeo, pelos anúncios que aparecem nele.",
      detalhada: "\"Desmonetizado\" é quando a plataforma tira os anúncios, geralmente por causa do assunto do vídeo. Para quem vive disso, equivale a trabalhar de graça." }]},

  { termo: "strike", idioma: "en", categorias: ["redes", "trabalho"], variacoes: ["strikes", "tomou strike"], sentidos: [
    { simples: "Punição aplicada pela plataforma a um canal.",
      detalhada: "Três strikes derrubam o canal inteiro. Muitos vêm de música de fundo com direitos autorais, e não de conteúdo impróprio." }]},

  { termo: "collab", idioma: "en", categorias: ["redes", "trabalho"], variacoes: ["colab", "collabs", "colaboração"], sentidos: [
    { simples: "Vídeo feito por dois criadores juntos.",
      detalhada: "Serve para cada um apresentar o próprio público ao do outro.",
      formal: "colaboração" }]},

  { termo: "membro", idioma: "pt-BR", categorias: ["redes", "streaming"], variacoes: ["membresia", "virar membro"], sentidos: [
    { simples: "Quem paga uma mensalidade a um canal em troca de vantagens.",
      detalhada: "É o equivalente ao \"sub\" da Twitch, com o mesmo funcionamento." }]},

  { termo: "super chat", idioma: "en", categorias: ["redes", "streaming"], variacoes: ["superchat", "super"], sentidos: [
    { simples: "Mensagem paga que fica destacada no chat de uma transmissão.",
      detalhada: "Quanto mais se paga, mais tempo a mensagem fica fixada. É uma das principais fontes de renda de quem transmite ao vivo." }]},

  { termo: "sininho", idioma: "pt-BR", categorias: ["redes"], variacoes: ["ativar o sininho", "sino"], sentidos: [
    { simples: "O botão que avisa quando o canal publica algo novo.",
      detalhada: "\"Deixa o like e ativa o sininho\" é o pedido mais repetido da plataforma. Sem ele, nem quem é inscrito recebe aviso." }]},

  { termo: "inscrito", idioma: "pt-BR", categorias: ["redes"], variacoes: ["inscritos", "se inscrever", "inscreva-se"], sentidos: [
    { simples: "Quem segue um canal para acompanhar os vídeos novos.",
      detalhada: "O número de inscritos é a medida pública de tamanho de um canal, embora as visualizações digam mais sobre o alcance real." }]},

  { termo: "playlist do canal", idioma: "pt-BR", categorias: ["redes"], variacoes: ["playlist de videos"], sentidos: [
    { simples: "Sequência de vídeos organizada pelo criador para assistir em ordem.",
      detalhada: "Serve para séries e cursos, e ajuda a plataforma a emendar um vídeo no outro." }]},

  { termo: "chorar no algoritmo", idioma: "pt-BR", categorias: ["redes", "humor"], variacoes: ["reclamar do algoritmo"], sentidos: [
    { simples: "Culpar a plataforma quando o vídeo vai mal.",
      detalhada: "Piada corrente entre criadores, porque é impossível provar se a culpa foi mesmo do sistema." }]},

  { termo: "canal secundário", idioma: "pt-BR", categorias: ["redes"], variacoes: ["canal secundario", "canal 2"], sentidos: [
    { simples: "Segundo canal do mesmo criador, com conteúdo mais solto.",
      detalhada: "Costuma ter vídeos menos produzidos, cortes e bastidores." }]},

  { termo: "deixa o like", idioma: "pt-BR", categorias: ["redes"], variacoes: ["deixe seu like", "manda aquele like"], sentidos: [
    { simples: "Pedido para curtir o vídeo.",
      detalhada: "Aparece no começo de quase todo vídeo. A curtida conta como engajamento e ajuda o vídeo a ser recomendado." }]},

  { termo: "seção de comentários", idioma: "pt-BR", categorias: ["redes"], variacoes: ["secao de comentarios", "comentários", "coments"], sentidos: [
    { simples: "O espaço abaixo do vídeo onde as pessoas escrevem.",
      detalhada: "Muita gente vai direto para lá antes de assistir, procurando saber se o vídeo vale a pena." }]},

  { termo: "fixado", idioma: "pt-BR", categorias: ["redes"], variacoes: ["comentário fixado", "pinado"], sentidos: [
    { simples: "Comentário que o criador colocou no topo da lista.",
      detalhada: "Serve para correção, aviso ou para destacar um comentário engraçado." }]},

  { termo: "cortes de live", idioma: "pt-BR", categorias: ["redes", "streaming"], variacoes: ["canal de corte", "cortou a live"], sentidos: [
    { simples: "Trechos curtos tirados de uma transmissão longa.",
      detalhada: "No Brasil, é assim que a maior parte do público conhece um streamer: pelos cortes, e não pela live inteira." }]},

  { termo: "minutagem", idioma: "pt-BR", categorias: ["redes"], variacoes: ["timestamp", "marcação de tempo"], sentidos: [
    { simples: "A marcação de tempo que leva direto a um trecho do vídeo.",
      detalhada: "Nos comentários, aparece como \"3:47\" e vira um link clicável." }]},

  { termo: "on fire", idioma: "en", categorias: ["redes", "elogio"], variacoes: ["tá on fire", "em chamas"], sentidos: [
    { simples: "Numa sequência muito boa, acertando tudo.",
      detalhada: "Vale para criador com vários vídeos bem-sucedidos seguidos e para jogador numa boa fase." }]},

  { termo: "gravar do zero", idioma: "pt-BR", categorias: ["redes", "trabalho"], variacoes: ["regravar tudo"], sentidos: [
    { simples: "Ter que refazer o vídeo inteiro.",
      detalhada: "Acontece quando o áudio falha ou o assunto perde a validade. É o pesadelo de quem produz." }]},

  { termo: "roteirizar", idioma: "pt-BR", categorias: ["redes", "trabalho"], variacoes: ["roteiro", "roteirizado"], sentidos: [
    { simples: "Escrever antes o que vai ser dito no vídeo.",
      detalhada: "Vídeo que parece improvisado quase sempre é roteirizado; a naturalidade é o efeito, não a causa." }]},

  { termo: "queda de views", idioma: "pt-BR", categorias: ["redes", "trabalho"], variacoes: ["caiu as views", "views"], sentidos: [
    { simples: "Quando o número de visualizações despenca.",
      detalhada: "\"Views\" é visualizações. Uma queda súbita costuma significar que a plataforma parou de recomendar o canal." }]},

];
