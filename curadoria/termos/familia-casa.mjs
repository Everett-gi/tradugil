/*
 * Familia, casa e as relacoes de todo dia.
 *
 * Esta prateleira tinha UM verbete. O catalogo tornou isso visivel:
 * "Familia e casa 1" era uma prateleira que so se revelava vazia depois que
 * a pessoa clicava e esperava.
 *
 * E uma categoria diferente das outras por um motivo. As girias de jogos e
 * de redes chegam a Marlene pela tela; estas chegam pela boca, na mesa de
 * domingo, ditas pelos netos. Sao as que ela mais ouve e as que menos
 * consegue perguntar o que significam, porque perguntar na hora expoe que
 * ela nao entendeu.
 *
 * Por isso muitas explicacoes aqui dizem TAMBEM o tom: se e carinho, se e
 * deboche, se e reclamacao. Saber o significado sem saber o tom nao resolve
 * o problema dela, que e descobrir se o neto estava sendo gentil ou nao.
 *
 * A primeira versao deste arquivo tinha 11 termos que ja existiam em
 * rua-br.mjs, internet-br.mjs e regional.mjs. Nao dava para saber de cabeca:
 * sao 750 verbetes. A checagem de duplicata entre arquivos no gerador nasceu
 * disto.
 */
export default [
  { termo: "sogrão", idioma: "pt-BR", categorias: ["familia", "humor"], variacoes: ["sogrona", "sograo", "sogrinha"], sentidos: [
    { simples: "O sogro ou a sogra, dito com humor.",
      detalhada: "O aumentativo aqui é brincadeira, não tamanho. Aparece muito em conversa de casal sobre a família do outro." }]},

  { termo: "tio do pavê", idioma: "pt-BR", categorias: ["familia", "humor"], variacoes: ["tio do pave", "piada de tio"], sentidos: [
    { simples: "O parente que conta a mesma piada sem graça toda festa.",
      detalhada: "Vem da piada \"é pavê ou pá comê?\". Virou o nome de um tipo: o adulto que repete trocadilhos velhos e ri sozinho. É dito com carinho." }]},

  { termo: "agregado", idioma: "pt-BR", categorias: ["familia"], variacoes: ["agregada", "agregados"], sentidos: [
    { simples: "Quem não é da família mas vive junto e é tratado como se fosse.",
      detalhada: "Namorado de longa data, amigo que mora perto, afilhado. \"Ele é agregado aqui em casa\" é aceitação, não crítica." }]},

  { termo: "xodó", idioma: "pt-BR", categorias: ["familia", "relacionamento"], variacoes: ["xodo", "xodozinho"], sentidos: [
    { simples: "A pessoa preferida de alguém, tratada com carinho especial.",
      detalhada: "\"O neto é o xodó da avó.\" Palavra antiga e ainda muito usada, sobretudo no Nordeste.",
      formal: "preferido" }]},

  { termo: "caçula", idioma: "pt-BR", categorias: ["familia"], variacoes: ["cacula", "caçulinha"], sentidos: [
    { simples: "O filho ou a filha mais nova da família.",
      detalhada: "Carrega a fama de ser o mais mimado, e a piada em cima disso é constante entre irmãos.",
      formal: "filho mais novo" }]},

  { termo: "puxar ao pai", idioma: "pt-BR", categorias: ["familia", "descricao"], variacoes: ["puxou a mae", "puxou ao pai", "puxar a mãe"], sentidos: [
    { simples: "Parecer com o pai ou com a mãe, na aparência ou no jeito.",
      detalhada: "\"Puxou a mãe na teimosia\" fala do temperamento, não do rosto. É o que se diz olhando criança pequena." }]},

  { termo: "dar bronca", idioma: "pt-BR", categorias: ["familia", "critica"], variacoes: ["levou bronca", "tomar bronca"], sentidos: [
    { simples: "Repreender alguém por alguma coisa que fez de errado.",
      detalhada: "\"Levei bronca\" é ter sido repreendido. Entre adultos, vira jeito leve de contar que foi cobrado no trabalho.",
      formal: "repreender" }]},

  { termo: "de castigo", idioma: "pt-BR", categorias: ["familia"], variacoes: ["ficar de castigo", "botar de castigo"], sentidos: [
    { simples: "Proibido de fazer alguma coisa como punição.",
      detalhada: "Hoje o castigo mais comum é ficar sem o celular, e é assim que a palavra aparece nas conversas de adolescente." }]},

  { termo: "mesada", idioma: "pt-BR", categorias: ["familia", "trabalho"], variacoes: ["mesadinha"], sentidos: [
    { simples: "Dinheiro que os pais dão ao filho todo mês.",
      detalhada: "Serve para a criança aprender a se organizar. Entre adultos, vira piada sobre quem ainda depende dos pais." }]},

  { termo: "fazer feira", idioma: "pt-BR", categorias: ["familia", "comida"], variacoes: ["fazendo feira", "feira do mês"], sentidos: [
    { simples: "Fazer as compras grandes do mês.",
      detalhada: "Vem da feira livre, mas hoje se usa igual para supermercado. \"A feira tá cara\" é queixa sobre o preço da comida.",
      formal: "compras do mês" }]},

  { termo: "apertar o cinto", idioma: "pt-BR", categorias: ["familia", "trabalho"], variacoes: ["apertando o cinto"], sentidos: [
    { simples: "Gastar menos porque o dinheiro está curto.",
      detalhada: "\"Vamos ter que apertar o cinto esse mês\" avisa que a casa vai cortar despesas.",
      formal: "economizar" }]},

  { termo: "quebrar um galho", idioma: "pt-BR", categorias: ["familia", "acao"], variacoes: ["quebra um galho", "quebrou o galho"], sentidos: [
    { simples: "Ajudar alguém, ou resolver de um jeito provisório.",
      detalhada: "\"Quebra um galho pra mim\" é pedir um favor. \"Isso quebra o galho\" é dizer que serve por enquanto, sem ser o ideal." }]},

  { termo: "meter o bedelho", idioma: "pt-BR", categorias: ["familia", "critica"], variacoes: ["metendo o bedelho"], sentidos: [
    { simples: "Se meter em assunto que não é seu.",
      detalhada: "Queixa clássica sobre parente que opina na vida dos outros sem ter sido chamado." }]},

  { termo: "casa caindo", idioma: "pt-BR", categorias: ["familia", "humor"], variacoes: ["a casa caiu"], sentidos: [
    { simples: "Situação em que tudo dá errado ao mesmo tempo.",
      detalhada: "\"A casa caiu\" também quer dizer que alguém foi descoberto fazendo alguma coisa escondida." }]},

  { termo: "botar ordem na casa", idioma: "pt-BR", categorias: ["familia", "acao"], variacoes: ["pôr ordem na casa"], sentidos: [
    { simples: "Organizar uma situação que estava bagunçada.",
      detalhada: "Raramente é sobre arrumar cômodos: quase sempre é sobre disciplina, regras ou contas." }]},

  { termo: "república", idioma: "pt-BR", categorias: ["familia", "escolar"], variacoes: ["republica", "rep"], sentidos: [
    { simples: "Casa dividida por vários estudantes.",
      detalhada: "Comum em cidade universitária. Cada um paga uma parte do aluguel e todos dividem as tarefas, em teoria.",
      formal: "moradia estudantil" }]},

  { termo: "dividir apê", idioma: "pt-BR", categorias: ["familia", "trabalho"], variacoes: ["dividir ape", "dividir apartamento", "rolê de ape"], sentidos: [
    { simples: "Morar com outra pessoa e rachar as despesas.",
      detalhada: "\"Apê\" é apartamento. É como muita gente jovem consegue sair da casa dos pais numa cidade cara." }]},

  { termo: "sair da casa dos pais", idioma: "pt-BR", categorias: ["familia"], variacoes: ["saiu de casa"], sentidos: [
    { simples: "Passar a morar sozinho ou com outras pessoas.",
      detalhada: "\"Saiu de casa\" sozinho pode ter os dois sentidos: mudança planejada ou briga. O contexto separa." }]},

  { termo: "almoço de domingo", idioma: "pt-BR", categorias: ["familia", "comida"], variacoes: ["almoco de domingo"], sentidos: [
    { simples: "A refeição em que a família se reúne toda semana.",
      detalhada: "Instituição brasileira. Nas conversas dos netos costuma aparecer como compromisso do qual não dá para escapar." }]},

  { termo: "esquenta", idioma: "pt-BR", categorias: ["familia", "musica"], variacoes: ["esquentinha"], sentidos: [
    { simples: "Encontro pequeno na casa de alguém antes da festa de verdade.",
      detalhada: "\"Vai ter esquenta lá em casa\" é o convite para o começo da noite, geralmente entre poucos amigos." }]},

  { termo: "puxa-saco", idioma: "pt-BR", categorias: ["familia", "critica"], variacoes: ["puxa saco", "puxasaco", "puxar saco"], sentidos: [
    { simples: "Quem elogia demais alguém para conseguir vantagem.",
      detalhada: "Entre irmãos, é a acusação de estar agradando os pais de propósito. No trabalho, de estar agradando o chefe.",
      formal: "bajulador" }]},

  { termo: "queridinho", idioma: "pt-BR", categorias: ["familia", "critica"], variacoes: ["queridinha", "queridinhos"], sentidos: [
    { simples: "O favorito de alguém, dito com uma ponta de ciúme.",
      detalhada: "\"É o queridinho da mãe\" raramente é elogio: é reclamação de quem se sente menos preferido." }]},

  { termo: "levar na esportiva", idioma: "pt-BR", categorias: ["familia", "emocao"], variacoes: ["leva na esportiva"], sentidos: [
    { simples: "Não levar a mal, encarar com bom humor.",
      detalhada: "\"Leva na esportiva\" é o pedido para não se ofender com uma brincadeira." }]},

  { termo: "fazer as pazes", idioma: "pt-BR", categorias: ["familia", "relacionamento"], variacoes: ["fez as pazes"], sentidos: [
    { simples: "Voltar a se falar depois de uma briga.",
      detalhada: "Vale para irmãos, casais e amigos. É a expressão que fecha uma discussão.",
      formal: "reconciliar-se" }]},

  { termo: "abrir o jogo", idioma: "pt-BR", categorias: ["familia", "acao"], variacoes: ["abriu o jogo"], sentidos: [
    { simples: "Contar a verdade sobre alguma coisa que estava escondida.",
      detalhada: "\"Vou abrir o jogo com você\" anuncia uma conversa franca, quase sempre difícil.",
      formal: "ser franco" }]},

  { termo: "dar o braço a torcer", idioma: "pt-BR", categorias: ["familia", "acao"], variacoes: ["deu o braco a torcer"], sentidos: [
    { simples: "Admitir que estava errado.",
      detalhada: "Quase sempre aparece na negativa: \"ele não dá o braço a torcer\", ou seja, não reconhece o erro de jeito nenhum.",
      formal: "reconhecer o erro" }]},

  { termo: "pegar no pé", idioma: "pt-BR", categorias: ["familia", "critica"], variacoes: ["pegar no pe", "pegando no pe"], sentidos: [
    { simples: "Ficar cobrando ou implicando com alguém sem parar.",
      detalhada: "Reclamação clássica de filho para pai ou mãe. Não é agressão: é insistência que cansa.",
      formal: "importunar" }]},

  { termo: "chegar junto", idioma: "pt-BR", categorias: ["familia", "acao"], variacoes: ["chegou junto", "chega junto"], sentidos: [
    { simples: "Aparecer para ajudar quando alguém precisa.",
      detalhada: "\"Ele chegou junto\" é um elogio grande: quer dizer que a pessoa apareceu na hora difícil.",
      formal: "apoiar" }]},

  { termo: "dar uma força", idioma: "pt-BR", categorias: ["familia", "acao"], variacoes: ["deu uma forca", "dar forca"], sentidos: [
    { simples: "Ajudar alguém em alguma coisa.",
      detalhada: "Pedido comum e informal. \"Me dá uma força aí\" é \"me ajuda\".",
      formal: "ajudar" }]},

  { termo: "puxar assunto", idioma: "pt-BR", categorias: ["familia", "acao"], variacoes: ["puxou assunto"], sentidos: [
    { simples: "Começar uma conversa com alguém.",
      detalhada: "Normalmente para quebrar o silêncio ou para se aproximar de alguém.",
      formal: "iniciar conversa" }]},

  { termo: "pôr na mesa", idioma: "pt-BR", categorias: ["familia", "acao"], variacoes: ["por na mesa", "botar na mesa"], sentidos: [
    { simples: "Trazer um assunto para ser conversado abertamente.",
      detalhada: "\"Vamos pôr isso na mesa\" é propor falar de um problema em vez de fingir que ele não existe.",
      formal: "trazer para discussão" }]},

  { termo: "ficar de cara", idioma: "pt-BR", categorias: ["familia", "emocao"], variacoes: ["fiquei de cara"], sentidos: [
    { simples: "Ficar surpreso, quase sempre negativamente.",
      detalhada: "\"Fiquei de cara com o que ele disse\" mistura surpresa e decepção. Diferente de \"de cara\" sozinho, que quer dizer \"logo de início\"." }]},

  { termo: "dar trela", idioma: "pt-BR", categorias: ["familia", "acao"], variacoes: ["deu trela", "dando trela"], sentidos: [
    { simples: "Dar atenção a alguém, geralmente atenção demais.",
      detalhada: "\"Não dá trela pra ele\" é um conselho para não alimentar uma conversa ou uma provocação.",
      formal: "dar atenção" }]},

  { termo: "casa da mãe joana", idioma: "pt-BR", categorias: ["familia", "critica"], variacoes: ["casa da mae joana"], sentidos: [
    { simples: "Lugar sem regra nenhuma, onde cada um faz o que quer.",
      detalhada: "\"Isto aqui não é a casa da mãe Joana\" é a frase que um adulto usa para dizer que existem regras ali." }]},

  { termo: "criado solto", idioma: "pt-BR", categorias: ["familia", "critica"], variacoes: ["criada solta"], sentidos: [
    { simples: "Pessoa que cresceu sem limites, e que se comporta assim.",
      detalhada: "É crítica ao comportamento de adulto, com a explicação atribuída à criação. Dito por parentes, costuma ser meio brincadeira e meio queixa." }]},

  { termo: "de porteira fechada", idioma: "pt-BR", categorias: ["familia", "relacionamento"], variacoes: ["porteira fechada"], sentidos: [
    { simples: "Aceitar alguém junto com os filhos que essa pessoa já tem.",
      detalhada: "Expressão do interior, hoje usada em todo o país. Diz que o compromisso inclui a família inteira do outro." }]},

  { termo: "vaquinha", idioma: "pt-BR", categorias: ["familia", "trabalho"], variacoes: ["fazer vaquinha", "vakinha"], sentidos: [
    { simples: "Juntar dinheiro entre várias pessoas para um objetivo comum.",
      detalhada: "\"Vamos fazer uma vaquinha pro presente\" é cada um dar uma parte. Virou também o nome das campanhas de arrecadação na internet.",
      formal: "coleta coletiva" }]},

  { termo: "ajeitar a casa", idioma: "pt-BR", categorias: ["familia", "acao"], variacoes: ["ajeitando a casa"], sentidos: [
    { simples: "Arrumar a casa, deixar tudo no lugar.",
      detalhada: "Vale também no sentido figurado: organizar a própria vida antes de encarar outra coisa." }]},

  { termo: "criançada", idioma: "pt-BR", categorias: ["familia"], variacoes: ["criancada"], sentidos: [
    { simples: "O conjunto das crianças de uma casa ou de um lugar.",
      detalhada: "\"Chama a criançada pra comer\" fala com todas de uma vez.",
      formal: "as crianças" }]},

  { termo: "casa de vó", idioma: "pt-BR", categorias: ["familia", "comida"], variacoes: ["casa de vo", "comida de vo"], sentidos: [
    { simples: "Lugar onde se come bem e ninguém tem pressa.",
      detalhada: "Virou expressão para qualquer lugar acolhedor: \"esse restaurante é tipo casa de vó\"." }]},

  { termo: "estar de mudança", idioma: "pt-BR", categorias: ["familia"], variacoes: ["de mudanca", "em mudanca"], sentidos: [
    { simples: "Estar trocando de casa.",
      detalhada: "\"Tô de mudança\" quer dizer que a pessoa está no processo, que costuma levar dias." }]},
];
