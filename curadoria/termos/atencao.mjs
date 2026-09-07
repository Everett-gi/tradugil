/*
 * TERMOS QUE PEDEM ATENCAO: a base do Modo Familia.
 *
 * ESTE ARQUIVO PRECISA DE REVISAO HUMANA ANTES DE IR AO AR.
 *
 * O Roberto, pai de dois adolescentes, e a persona que depende disto. Ele le
 * uma palavra na conversa do filho e precisa saber, na hora, se aquilo pede
 * uma conversa. Errar aqui tem custo real nos dois sentidos: deixar passar um
 * termo que importava, ou alarmar um pai por causa de uma giria inofensiva.
 *
 * COMO ESTES VERBETES FORAM ESCRITOS
 *
 * 1. Descrevem, nunca ensinam. A explicacao diz o que o termo SINALIZA, e
 *    nunca como fazer, onde conseguir ou como esconder. Um verbete que
 *    funcionasse como instrucao seria pior que a ausencia dele.
 *
 * 2. Nao acusam. "Costuma aparecer em conversas sobre X" e diferente de "seu
 *    filho esta fazendo X". A maioria dos adolescentes usa esses termos por
 *    repeticao, sem viver nada do que eles descrevem.
 *
 * 3. Apontam para conversa, nao para vigilancia. O alerta do Modo Familia
 *    sugere conversar, porque e o que funciona. Vigiar sem conversar
 *    costuma so ensinar o adolescente a esconder melhor.
 *
 * 4. Nao sao marcados nsfw. Ocultar do Roberto justamente o termo que ele
 *    precisa entender inverteria a finalidade do Modo Familia. Eles aparecem
 *    e vem sinalizados.
 *
 * O QUE FALTA DECIDIR COM A CURADORIA
 *
 * - Se o alerta deve mudar de texto conforme a categoria de risco.
 * - Se termos de automutilacao devem trazer o telefone do CVV (188) junto da
 *   explicacao, e em que formato.
 * - Onde fica a linha entre "pede atencao" e "e so giria de adolescente".
 */
export default [
  { termo: "vape", idioma: "en", categorias: ["atencao"], risco: true, variacoes: ["pod", "vapear", "cigarro eletrônico"], sentidos: [
    { simples: "Cigarro eletrônico, aparelho que a pessoa usa para inalar vapor com nicotina.",
      detalhada: "A venda é proibida no Brasil pela Anvisa desde 2009, mas o produto circula. Costuma ter sabores doces e mais nicotina que o cigarro comum, o que facilita o vício em quem começa jovem.",
      formal: "cigarro eletrônico" }]},

  { termo: "beck", idioma: "pt-BR", categorias: ["atencao"], risco: true, variacoes: ["baseado", "fininho"], sentidos: [
    { simples: "Cigarro de maconha.",
      detalhada: "Termo antigo e muito conhecido. Aparece com naturalidade em conversas de adolescentes, às vezes sem qualquer envolvimento real, só como referência cultural.",
      formal: "cigarro de maconha" }]},

  { termo: "bala", idioma: "pt-BR", categorias: ["atencao"], risco: true, variacoes: ["balinha"], sentidos: [
    { simples: "Comprimido de droga sintética, geralmente ecstasy.",
      detalhada: "A palavra tem uso comum e inocente (doce, projétil), então o contexto é o que importa: menções a festa, noite e quantidade mudam o sentido.",
      formal: "comprimido de ecstasy" }]},

  { termo: "loló", idioma: "pt-BR", categorias: ["atencao"], risco: true, variacoes: ["lolo", "lança", "lança perfume"], sentidos: [
    { simples: "Substância inalante usada em festas, de venda proibida.",
      detalhada: "Causa efeito de curta duração e risco cardíaco imediato, inclusive na primeira vez. Circula principalmente em carnaval e festas de rua.",
      formal: "inalante" }]},

  { termo: "brisar", idioma: "pt-BR", categorias: ["atencao"], risco: true, sentidos: [
    { simples: "Ficar sob efeito de alguma substância.",
      detalhada: "Cuidado: \"brisar\" também é apenas ficar distraído ou pensativo, sem qualquer relação com drogas, e esse é o uso mais comum. O contexto separa os dois.",
      formal: "ficar sob efeito" }]},

  { termo: "chapado", idioma: "pt-BR", categorias: ["atencao"], risco: true, variacoes: ["chapada", "chapar"], sentidos: [
    { simples: "Sob forte efeito de álcool ou de outra substância.",
      detalhada: "Também usado com exagero e humor para dizer que a pessoa está muito cansada ou desligada, sem envolver nada. O tom da conversa indica qual dos dois.",
      formal: "sob efeito" }]},

  { termo: "mó onda", idioma: "pt-BR", categorias: ["atencao"], risco: true, variacoes: ["na onda", "onda"], sentidos: [
    { simples: "Pode indicar o efeito de uma substância.",
      detalhada: "\"Onda\" tem uso amplo e inocente, como \"que onda boa\" para um momento agradável. Só o contexto de festa ou de consumo muda o sentido.",
      formal: "efeito" }]},

  { termo: "cair de boca", idioma: "pt-BR", categorias: ["atencao"], risco: true, variacoes: ["cair matando"], sentidos: [
    { simples: "Consumir algo em grande quantidade, sem moderação.",
      detalhada: "Usado tanto para comida quanto para bebida. Quando o assunto é bebida, e em conversa de adolescente, merece atenção.",
      formal: "exagerar" }]},

  { termo: "ficar de porre", idioma: "pt-BR", categorias: ["atencao"], risco: true, variacoes: ["porre", "de porre", "bêbado"], sentidos: [
    { simples: "Beber a ponto de perder o controle.",
      detalhada: "No Brasil, a venda de bebida alcoólica a menores de 18 anos é proibida. Beber em grande quantidade em pouco tempo traz risco imediato de intoxicação.",
      formal: "embriaguez" }]},

  { termo: "apagar", idioma: "pt-BR", categorias: ["atencao"], risco: true, variacoes: ["apagou"], sentidos: [
    { simples: "Perder a consciência por excesso de bebida.",
      detalhada: "É sinal de intoxicação alcoólica, não de festa boa. Alguém que apagou precisa de acompanhamento, e não de ser deixado dormindo sozinho.",
      formal: "desmaiar" }]},

  { termo: "se cortar", idioma: "pt-BR", categorias: ["atencao"], risco: true, variacoes: ["cutting", "se machucar"], sentidos: [
    { simples: "Machucar o próprio corpo de propósito.",
      detalhada: "É sinal de sofrimento que pede ajuda profissional, não castigo nem sermão. O CVV atende de graça pelo telefone 188, 24 horas por dia, e também por chat no site cvv.org.br.",
      formal: "automutilação" }]},

  { termo: "sh", idioma: "en", categorias: ["atencao"], risco: true, variacoes: ["self harm"], sentidos: [
    { simples: "Sigla em inglês usada para falar de automutilação sem escrever a palavra.",
      detalhada: "De \"self harm\". Siglas assim circulam porque muitas redes bloqueiam os termos completos. Encontrar a sigla é motivo para uma conversa acolhedora, e o CVV atende pelo 188.",
      formal: "automutilação" }]},

  { termo: "gatilho", idioma: "pt-BR", categorias: ["atencao"], risco: true, variacoes: ["trigger", "tw"], sentidos: [
    { simples: "Algo que desperta uma lembrança ou reação emocional forte em alguém.",
      detalhada: "\"TW\" antes de um conteúdo é aviso de que ele pode ser pesado para quem viveu algo parecido. O uso do termo em si não indica risco: é cuidado, não sintoma.",
      formal: "estímulo emocional" }]},

  { termo: "ana", idioma: "pt-BR", categorias: ["atencao"], risco: true, variacoes: ["mia", "pró-ana"], sentidos: [
    { simples: "Apelidos usados em comunidades que tratam transtornos alimentares como escolha, e não como doença.",
      detalhada: "\"Ana\" vem de anorexia e \"mia\" de bulimia. São grupos que incentivam a doença, e a presença desses termos é sinal de alerta. Transtorno alimentar é doença tratável, com risco real de morte.",
      formal: "transtorno alimentar" }]},

  { termo: "jejum limpo", idioma: "pt-BR", categorias: ["atencao"], risco: true, variacoes: ["restrição"], sentidos: [
    { simples: "Termo usado para descrever ficar sem comer por longos períodos.",
      detalhada: "Aparece tanto em contextos de dieta quanto em comunidades que incentivam transtorno alimentar. Em adolescente, restrição alimentar prolongada pede avaliação médica.",
      formal: "restrição alimentar" }]},

  { termo: "nudes", idioma: "en", categorias: ["atencao"], risco: true, variacoes: ["nude", "mandar nudes"], sentidos: [
    { simples: "Fotos íntimas, sem roupa, trocadas por mensagem.",
      detalhada: "Quando envolve menor de 18 anos, produzir, enviar ou guardar essas imagens é crime no Brasil, mesmo que a própria pessoa tenha feito a foto. A vítima não é culpada, e a lei protege quem foi exposto.",
      formal: "imagens íntimas" }]},

  { termo: "pack", idioma: "en", categorias: ["atencao"], risco: true, sentidos: [
    { simples: "Conjunto de fotos íntimas vendido ou trocado.",
      detalhada: "\"Vender pack\" aparece em conversas de adolescentes. Quando envolve menor de idade, configura crime grave, e quem compra também responde.",
      formal: "conjunto de imagens íntimas" }]},

  { termo: "aliciamento", idioma: "pt-BR", categorias: ["atencao"], risco: true, variacoes: ["grooming"], sentidos: [
    { simples: "Quando um adulto se aproxima de uma criança ou adolescente pela internet para ganhar confiança com intenção sexual.",
      detalhada: "Do inglês \"grooming\". Começa com amizade, elogios e presentes, e evolui devagar para isolamento e segredo. É crime no Brasil, previsto no Estatuto da Criança e do Adolescente.",
      formal: "aliciamento" }]},

  { termo: "sugar", idioma: "en", categorias: ["atencao"], risco: true, variacoes: ["sugar daddy", "sugar baby"], sentidos: [
    { simples: "Relação em que uma pessoa mais velha dá dinheiro ou presentes a uma mais jovem.",
      detalhada: "Quando envolve menor de 18 anos, é exploração sexual, e não relacionamento. É crime, independentemente de haver consentimento aparente.",
      formal: "relação por interesse financeiro" }]},

  { termo: "sextar", idioma: "pt-BR", categorias: ["atencao"], risco: true, variacoes: ["sexting"], sentidos: [
    { simples: "Trocar mensagens de conteúdo sexual.",
      detalhada: "Do inglês \"sexting\". Cuidado: \"sextou\" é outra coisa completamente diferente, e apenas comemora a sexta-feira. As duas palavras são parecidas e não têm relação.",
      formal: "troca de mensagens sexuais" }]},

  { termo: "cyberbullying", idioma: "en", categorias: ["atencao"], risco: true, variacoes: ["bullying virtual"], sentidos: [
    { simples: "Perseguição e humilhação de alguém pela internet, de forma repetida.",
      detalhada: "É crime no Brasil desde 2023, com pena prevista em lei. Diferente de uma briga isolada: o que caracteriza é a repetição e a intenção de humilhar.",
      formal: "perseguição virtual" }]},

  { termo: "doxxing", idioma: "en", categorias: ["atencao"], risco: true, variacoes: ["doxar", "dox"], sentidos: [
    { simples: "Publicar dados pessoais de alguém na internet para que outros a persigam.",
      detalhada: "Inclui endereço, escola, telefone e local de trabalho. Costuma ser o passo que transforma um ataque virtual em risco físico.",
      formal: "exposição de dados pessoais" }]},

  { termo: "catfish", idioma: "en", categorias: ["atencao"], risco: true, variacoes: ["catfishing"], sentidos: [
    { simples: "Pessoa que finge ser outra na internet, usando fotos e nome falsos.",
      detalhada: "Pode ser brincadeira, golpe financeiro ou aproximação de adulto que se passa por adolescente. O último caso é o mais grave.",
      formal: "perfil falso" }]},

  { termo: "golpe do pix", idioma: "pt-BR", categorias: ["atencao"], risco: true, variacoes: ["golpe", "cair no golpe"], sentidos: [
    { simples: "Fraude em que alguém convence a vítima a transferir dinheiro.",
      detalhada: "Muito comum por WhatsApp, com o golpista se passando por parente que trocou de número. Idosos são o alvo preferido, e a orientação é sempre ligar para o número antigo antes de transferir.",
      formal: "fraude financeira" }]},

  { termo: "desafio", idioma: "pt-BR", categorias: ["atencao"], risco: true, variacoes: ["challenge", "desafio da internet"], sentidos: [
    { simples: "Brincadeira que circula pedindo que a pessoa faça algo e grave.",
      detalhada: "A grande maioria é inofensiva e engraçada. Uma minoria envolve risco físico real, e essas costumam se espalhar mais rápido justamente pelo perigo.",
      formal: "desafio viral" }]},

  { termo: "mule", idioma: "en", categorias: ["atencao"], risco: true, variacoes: ["laranja", "conta laranja"], sentidos: [
    { simples: "Pessoa que empresta a própria conta bancária para movimentar dinheiro de outra.",
      detalhada: "Em português é \"laranja\". Oferecido a jovens como dinheiro fácil pela internet, mas quem empresta a conta responde criminalmente pela lavagem.",
      formal: "intermediário financeiro" }]},

  { termo: "hentai", idioma: "en", categorias: ["atencao"], risco: true, nsfw: true, sentidos: [
    { simples: "Desenho japonês de conteúdo adulto.",
      detalhada: "Termo comum em comunidades de animação japonesa. Marcado como conteúdo impróprio: some da resposta quando o Modo Família está ligado.",
      formal: "conteúdo adulto ilustrado" }]},

  { termo: "onlyfans", idioma: "en", categorias: ["atencao"], risco: true, variacoes: ["of"], sentidos: [
    { simples: "Site de assinatura usado principalmente para conteúdo adulto.",
      detalhada: "Exige 18 anos para criar conta. Menções entre adolescentes costumam ser piada, mas também aparecem em conversas sobre ganhar dinheiro rápido.",
      formal: "plataforma de assinatura adulta" }]},

  { termo: "aposta", idioma: "pt-BR", categorias: ["atencao"], risco: true, variacoes: ["bet", "tigrinho", "casa de aposta"], sentidos: [
    { simples: "Jogo de azar pela internet, em que a pessoa arrisca dinheiro.",
      detalhada: "Proibido para menores de 18 anos no Brasil. Os jogos de imagens giratórias, como o chamado \"tigrinho\", são desenhados para causar vício, e o prejuízo costuma crescer devagar.",
      formal: "jogo de azar" }]},

  { termo: "hater", idioma: "en", categorias: ["atencao"], risco: true, sentidos: [
    { simples: "Pessoa que ataca alguém repetidamente na internet.",
      detalhada: "Quando os ataques são constantes e dirigidos à mesma pessoa, deixa de ser opinião e passa a ser perseguição, que tem consequência legal.",
      formal: "perseguidor virtual" }]},

  { termo: "cancelar", idioma: "pt-BR", categorias: ["atencao"], risco: true, variacoes: ["cancelamento em massa"], sentidos: [
    { simples: "Ataque coletivo a alguém na internet, geralmente por algo que a pessoa disse.",
      detalhada: "Entre adolescentes, um cancelamento na escola pode isolar completamente a pessoa. O efeito emocional costuma ser muito maior do que os adultos imaginam.",
      formal: "boicote coletivo" }]},

  { termo: "grupo secreto", idioma: "pt-BR", categorias: ["atencao"], risco: true, variacoes: ["grupo fechado", "close friends secreto"], sentidos: [
    { simples: "Grupo de conversa que a pessoa esconde das outras.",
      detalhada: "Ter espaço privado é normal e saudável na adolescência. O sinal de alerta é o segredo combinado com mudança de comportamento ou com um adulto participando.",
      formal: "grupo privado" }]},

  { termo: "sumir do mapa", idioma: "pt-BR", categorias: ["atencao"], risco: true, variacoes: ["sumido"], sentidos: [
    { simples: "Parar de responder e de aparecer, por um período longo.",
      detalhada: "Na maioria das vezes é só cansaço ou vontade de ficar sozinho. Quando vem junto de desânimo persistente e perda de interesse pelo que a pessoa gostava, pede conversa.",
      formal: "isolamento" }]},

  { termo: "tô mal", idioma: "pt-BR", categorias: ["atencao"], risco: true, variacoes: ["to mal", "não tô bem"], sentidos: [
    { simples: "Jeito comum de dizer que não se está bem emocionalmente.",
      detalhada: "Frase curta que muitas vezes é a única abertura que a pessoa consegue dar. Perguntar o que houve, sem julgar, costuma valer mais que qualquer conselho.",
      formal: "não estou bem" }]},

  { termo: "cansado de tudo", idioma: "pt-BR", categorias: ["atencao"], risco: true, variacoes: ["cansei", "não aguento mais"], sentidos: [
    { simples: "Expressão de exaustão que às vezes vai além do cansaço comum.",
      detalhada: "Quase sempre é desabafo passageiro. Quando aparece junto de despedidas, doação de objetos queridos ou perda de interesse por tudo, é sinal sério: o CVV atende de graça pelo 188, a qualquer hora.",
      formal: "exaustão emocional" }]},

  { termo: "cvv", idioma: "pt-BR", categorias: ["atencao"], sentidos: [
    { simples: "Serviço gratuito de apoio emocional por telefone, no número 188, 24 horas por dia.",
      detalhada: "Centro de Valorização da Vida. Atende de forma anônima e sigilosa por telefone, chat e e-mail, pelo site cvv.org.br. Qualquer pessoa pode ligar, inclusive quem está preocupado com outra.",
      formal: "Centro de Valorização da Vida" }]},

  { termo: "disque 100", idioma: "pt-BR", categorias: ["atencao"], variacoes: ["disque denúncia", "disque 100"], sentidos: [
    { simples: "Telefone gratuito para denunciar violência contra crianças e adolescentes.",
      detalhada: "Funciona 24 horas, é anônimo e nacional. Recebe denúncias de violência física, sexual, negligência e trabalho infantil.",
      formal: "Disque Direitos Humanos" }]},
];
