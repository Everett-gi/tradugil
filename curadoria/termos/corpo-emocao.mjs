/*
 * Corpo, aparencia, saude mental e emocoes.
 *
 * Categoria delicada por dois motivos. Primeiro, muitos termos de aparencia
 * carregam julgamento, e a explicacao precisa dizer isso sem repetir o
 * julgamento. Segundo, o vocabulario de saude mental virou giria: palavras
 * clinicas como "ansiedade" e "gatilho" sao usadas com leveza no dia a dia,
 * e quem le precisa saber diferenciar o uso solto do uso literal.
 *
 * Onde um termo pode indicar sofrimento real, o verbete diz isso sem
 * alarmar, e sem tratar toda menção como sintoma.
 */
export default [
  { termo: "shape", idioma: "en", categorias: ["descricao"], variacoes: ["shapinho"], sentidos: [
    { simples: "A forma física do corpo de alguém.",
      detalhada: "\"Tá com shape\" elogia quem está em boa forma. Termo vindo da academia e espalhado pelas redes.",
      formal: "forma física" }]},

  { termo: "bombado", idioma: "pt-BR", categorias: ["descricao"], variacoes: ["bombada", "bombar"], sentidos: [
    { simples: "Pessoa muito musculosa.",
      detalhada: "\"Bombar\" também significa fazer sucesso: \"o vídeo bombou\". Os dois sentidos convivem sem confusão.",
      formal: "musculoso" }]},

  { termo: "trincado", idioma: "pt-BR", categorias: ["descricao"], variacoes: ["trincada"], sentidos: [
    { simples: "Corpo com músculos muito definidos.",
      detalhada: "Vem da imagem de linhas marcadas na pele. Elogio comum em contexto de academia.",
      formal: "definido" }]},

  { termo: "seco", idioma: "pt-BR", categorias: ["descricao"], variacoes: ["secar"], sentidos: [
    { simples: "Corpo magro e definido, com pouca gordura.",
      detalhada: "\"Secar\" é o processo de perder gordura. Fora do contexto de corpo, \"seco\" tem outros sentidos.",
      formal: "magro e definido" }]},

  { termo: "bulking", idioma: "en", categorias: ["descricao"], variacoes: ["bulk"], sentidos: [
    { simples: "Fase de comer mais para ganhar massa muscular.",
      detalhada: "O oposto é \"cutting\", fase de perder gordura. Vocabulário de academia que virou comum entre jovens.",
      formal: "ganho de massa" }]},

  { termo: "cutting", idioma: "en", categorias: ["descricao"], sentidos: [
    { simples: "Fase de comer menos para perder gordura.",
      detalhada: "Cuidado: em outros contextos, \"cutting\" em inglês se refere a automutilação. No vocabulário de academia, não tem essa relação.",
      formal: "perda de gordura" }]},

  { termo: "treino", idioma: "pt-BR", categorias: ["esporte"], variacoes: ["treinar", "malhar"], sentidos: [
    { simples: "Exercício físico feito com regularidade.",
      detalhada: "\"Malhar\" é o termo mais informal. \"Dia de perna\" e \"dia de braço\" dividem os treinos por parte do corpo.",
      formal: "exercício" }]},

  { termo: "no pain no gain", idioma: "en", categorias: ["esporte"], sentidos: [
    { simples: "Frase que diz que sem esforço não há resultado.",
      detalhada: "Bordão de academia dos anos 80. Hoje é criticado por incentivar treino além do limite seguro.",
      formal: "sem esforço não há resultado" }]},

  { termo: "gordofobia", idioma: "pt-BR", categorias: ["descricao", "atencao"], sentidos: [
    { simples: "Preconceito contra pessoas gordas.",
      detalhada: "Inclui piadas, exclusão e tratamento médico que atribui qualquer sintoma ao peso. Termo usado em discussões sobre saúde e direitos.",
      formal: "preconceito por peso" }]},

  { termo: "body positive", idioma: "en", categorias: ["descricao"], variacoes: ["corpo livre"], sentidos: [
    { simples: "Movimento que defende aceitar o próprio corpo como ele é.",
      detalhada: "Surgiu como reação a padrões de beleza inatingíveis. \"Body neutrality\" é uma variação: em vez de amar o corpo, apenas não pensar tanto nele.",
      formal: "aceitação corporal" }]},

  { termo: "padrão", idioma: "pt-BR", categorias: ["descricao", "critica"], variacoes: ["fora do padrão", "padrão de beleza"], sentidos: [
    { simples: "O modelo de aparência que a sociedade trata como ideal.",
      detalhada: "\"Fora do padrão\" descreve quem não corresponde a esse modelo. Discussão central em conversas sobre autoestima.",
      formal: "padrão estético" }]},

  { termo: "autoestima", idioma: "pt-BR", categorias: ["emocao"], sentidos: [
    { simples: "O quanto a pessoa se valoriza e se sente bem consigo mesma.",
      detalhada: "Diferente de vaidade: envolve como a pessoa se enxerga, não como quer ser vista." }]},

  { termo: "ansiedade", idioma: "pt-BR", categorias: ["emocao", "atencao"], variacoes: ["ansioso", "ansiosa"], sentidos: [
    { simples: "Preocupação intensa e antecipada com o que pode acontecer.",
      detalhada: "A palavra virou gíria: \"tô ansiosa\" muitas vezes é só expectativa. O transtorno de ansiedade é outra coisa, e envolve sintomas físicos e prejuízo na rotina. Vale prestar atenção quando é constante." }]},

  { termo: "crise", idioma: "pt-BR", categorias: ["emocao", "atencao"], variacoes: ["crise de ansiedade", "ataque de pânico"], sentidos: [
    { simples: "Episódio agudo de ansiedade, com sintomas físicos fortes.",
      detalhada: "Envolve coração acelerado, falta de ar e sensação de perigo iminente. Quem está em crise precisa de calma e ar, não de conselhos. Se for frequente, pede acompanhamento profissional.",
      formal: "crise de ansiedade" }]},

  { termo: "sobrecarga", idioma: "pt-BR", categorias: ["emocao", "trabalho"], variacoes: ["sobrecarregado"], sentidos: [
    { simples: "Ter mais responsabilidades do que consegue dar conta.",
      detalhada: "Diferente de burnout, que é o esgotamento já instalado. A sobrecarga é o estágio anterior, ainda reversível.",
      formal: "excesso de demandas" }]},

  { termo: "terapia", idioma: "pt-BR", categorias: ["emocao"], variacoes: ["fazer terapia", "terapeuta"], sentidos: [
    { simples: "Acompanhamento com psicólogo para cuidar da saúde mental.",
      detalhada: "\"Vai fazer terapia\" às vezes é usado como ofensa na internet, o que reforça estigma. Buscar terapia é cuidado, não fraqueza.",
      formal: "psicoterapia" }]},

  { termo: "psicólogo", idioma: "pt-BR", categorias: ["emocao"], variacoes: ["psi", "psicologa"], sentidos: [
    { simples: "Profissional que ajuda com questões emocionais por meio de conversa.",
      detalhada: "Diferente do psiquiatra, que é médico e pode receitar remédio. Muita gente acompanha com os dois." }]},

  { termo: "surto", idioma: "pt-BR", categorias: ["emocao", "atencao"], variacoes: ["surtar", "surtei"], sentidos: [
    { simples: "Reação emocional muito intensa.",
      detalhada: "Na internet quase sempre é empolgação exagerada. No sentido clínico, é episódio grave que precisa de atendimento. O contexto separa, e vale prestar atenção quando a conversa é séria.",
      formal: "reação intensa" }]},

  { termo: "sofrência", idioma: "pt-BR", categorias: ["emocao", "musica", "humor"], sentidos: [
    { simples: "Sofrimento amoroso vivido de forma dramática, quase como espetáculo.",
      detalhada: "Junção de \"sofrer\" com \"carência\". Virou nome de um estilo de sertanejo, e o termo é usado com humor.",
      formal: "sofrimento amoroso" }]},

  { termo: "carente", idioma: "pt-BR", categorias: ["emocao"], variacoes: ["carência"], sentidos: [
    { simples: "Pessoa que está precisando de atenção e afeto.",
      detalhada: "Usado com naturalidade e sem ofensa: \"tô carente\" é reconhecimento tranquilo do próprio estado.",
      formal: "necessitado de afeto" }]},

  { termo: "coração partido", idioma: "pt-BR", categorias: ["emocao", "relacionamento"], variacoes: ["heartbroken"], sentidos: [
    { simples: "Estado de tristeza depois de um término amoroso.",
      detalhada: "Expressão antiga que a internet manteve viva, muitas vezes com humor.",
      formal: "desgosto amoroso" }]},

  { termo: "luto", idioma: "pt-BR", categorias: ["emocao"], sentidos: [
    { simples: "O processo de sofrer uma perda importante.",
      detalhada: "Não se aplica só a morte: também se fala em luto por fim de relação, mudança de cidade ou perda de emprego." }]},

  { termo: "gatilhado", idioma: "pt-BR", categorias: ["emocao", "atencao"], variacoes: ["me gatilhou"], sentidos: [
    { simples: "Ter uma reação emocional forte despertada por algo.",
      detalhada: "Do inglês \"triggered\". Palavra de origem clínica que virou uso comum, às vezes com exagero. Quando a reação é intensa e recorrente, pede acompanhamento.",
      formal: "emocionalmente ativado" }]},

  { termo: "validar", idioma: "pt-BR", categorias: ["emocao"], variacoes: ["validação"], sentidos: [
    { simples: "Reconhecer que o sentimento da outra pessoa faz sentido.",
      detalhada: "\"Buscar validação\" é depender demais da aprovação alheia. \"Validar o sentimento\" é o contrário: acolher sem julgar.",
      formal: "reconhecer" }]},

  { termo: "acolher", idioma: "pt-BR", categorias: ["emocao"], variacoes: ["acolhimento"], sentidos: [
    { simples: "Receber alguém com atenção e sem julgamento.",
      detalhada: "Vocabulário de cuidado que passou da área da saúde para o uso comum." }]},

  { termo: "descarrego", idioma: "pt-BR", categorias: ["emocao"], variacoes: ["desabafo", "desabafar"], sentidos: [
    { simples: "Falar sobre o que está incomodando, para aliviar.",
      detalhada: "\"Posso desabafar?\" é pedido comum. Quem escuta geralmente não precisa resolver nada, só ouvir.",
      formal: "desabafo" }]},

  { termo: "tá difícil", idioma: "pt-BR", categorias: ["emocao"], variacoes: ["ta osso", "tá osso"], sentidos: [
    { simples: "Jeito de dizer que a situação está complicada.",
      detalhada: "\"Tá osso\" é a versão mais informal, e serve tanto para dificuldade real quanto para reclamação leve.",
      formal: "está difícil" }]},

  { termo: "no limite", idioma: "pt-BR", categorias: ["emocao", "atencao"], variacoes: ["no meu limite"], sentidos: [
    { simples: "Perto de não aguentar mais.",
      detalhada: "Costuma ser desabafo passageiro. Quando aparece com frequência e junto de isolamento, vale conversar com atenção." }]},

  { termo: "respira", idioma: "pt-BR", categorias: ["emocao"], variacoes: ["respira fundo"], sentidos: [
    { simples: "Conselho para se acalmar antes de reagir.",
      detalhada: "Simples e literal: respirar devagar reduz de fato os sintomas físicos de ansiedade.",
      formal: "acalme-se" }]},

  { termo: "presente", idioma: "pt-BR", categorias: ["emocao"], variacoes: ["estar presente"], sentidos: [
    { simples: "Estar de fato ali, com atenção, e não só de corpo.",
      detalhada: "\"Estar presente\" virou tema de conversas sobre atenção e uso de celular.",
      formal: "atento" }]},

  { termo: "chorar as pitangas", idioma: "pt-BR", categorias: ["emocao", "humor"], sentidos: [
    { simples: "Reclamar muito, lamentando-se.",
      detalhada: "Expressão antiga, usada com humor. Não indica choro de verdade.",
      formal: "lamentar-se" }]},

  { termo: "engolir sapo", idioma: "pt-BR", categorias: ["emocao"], sentidos: [
    { simples: "Aguentar algo desagradável sem poder reclamar.",
      detalhada: "Muito usado em contexto de trabalho, sobre situações que a pessoa suporta por necessidade.",
      formal: "suportar em silêncio" }]},

  { termo: "de saco cheio", idioma: "pt-BR", categorias: ["emocao"], variacoes: ["saco cheio"], sentidos: [
    { simples: "Cansado e irritado com uma situação que se repete.",
      detalhada: "Expressão informal comum, mais forte que \"cansado\" e mais leve que \"revoltado\".",
      formal: "farto" }]},

  { termo: "revoltado", idioma: "pt-BR", categorias: ["emocao"], variacoes: ["revoltada", "revolta"], sentidos: [
    { simples: "Muito indignado com algo considerado injusto.",
      detalhada: "Diferente de \"bravo\": a revolta envolve senso de injustiça, não apenas irritação.",
      formal: "indignado" }]},

  { termo: "indignado", idioma: "pt-BR", categorias: ["emocao"], sentidos: [
    { simples: "Reação forte diante de algo considerado errado.",
      detalhada: "Palavra formal que circula normalmente em conversa comum, principalmente sobre notícias." }]},

  { termo: "frustrado", idioma: "pt-BR", categorias: ["emocao"], variacoes: ["frustração"], sentidos: [
    { simples: "Decepcionado por algo que não saiu como esperava.",
      detalhada: "Diferente de triste: a frustração vem especificamente da expectativa não atendida." }]},

  { termo: "aliviado", idioma: "pt-BR", categorias: ["emocao"], variacoes: ["alívio"], sentidos: [
    { simples: "Sensação boa de quando uma preocupação passa.",
      detalhada: "\"Que alívio\" é reação comum a boa notícia depois de espera tensa." }]},

  { termo: "empolgado", idioma: "pt-BR", categorias: ["emocao"], variacoes: ["empolgação", "animado"], sentidos: [
    { simples: "Muito animado com algo que vai acontecer.",
      detalhada: "Equivalente brasileiro de \"hyped\". \"Empolgação\" também pode ser crítica: \"empolgou demais\".",
      formal: "animado" }]},

  { termo: "orgulhoso", idioma: "pt-BR", categorias: ["emocao"], variacoes: ["orgulho"], sentidos: [
    { simples: "Sentimento bom por algo que a pessoa ou alguém próximo conseguiu.",
      detalhada: "\"Tô orgulhosa de você\" é elogio afetivo. Diferente de arrogância, que é outro sentido de orgulho." }]},

  { termo: "grato", idioma: "pt-BR", categorias: ["emocao"], variacoes: ["gratidão", "grata"], sentidos: [
    { simples: "Sentimento de agradecimento.",
      detalhada: "\"Gratidão\" sozinha virou forma de encerrar mensagem, e às vezes é criticada por parecer automática.",
      formal: "agradecido" }]},

  { termo: "leve", idioma: "pt-BR", categorias: ["emocao"], variacoes: ["leveza", "mais leve"], sentidos: [
    { simples: "Sensação boa de quando o peso emocional diminui.",
      detalhada: "\"Fica leve\" é conselho para não levar algo a sério demais.",
      formal: "tranquilo" }]},

  { termo: "sono atrasado", idioma: "pt-BR", categorias: ["tempo", "atencao"], variacoes: ["dívida de sono"], sentidos: [
    { simples: "O acúmulo de noites mal dormidas.",
      detalhada: "Não se recupera totalmente dormindo mais no fim de semana, ao contrário do que se acredita. Falta crônica de sono afeta humor e concentração.",
      formal: "privação de sono" }]},

  { termo: "insônia", idioma: "pt-BR", categorias: ["atencao"], sentidos: [
    { simples: "Dificuldade persistente para dormir.",
      detalhada: "Quando dura semanas e atrapalha o dia seguinte, deixa de ser noite ruim e vira questão de saúde que pede avaliação." }]},

  { termo: "procrastinar", idioma: "pt-BR", categorias: ["acao"], variacoes: ["procrastinação"], sentidos: [
    { simples: "Adiar o que precisa ser feito, mesmo sabendo que vai fazer falta.",
      detalhada: "Muitas vezes não é preguiça: pode vir de ansiedade em relação à tarefa. Assunto comum em conversas sobre estudo e trabalho.",
      formal: "adiar" }]},

  { termo: "foco", idioma: "pt-BR", categorias: ["acao"], variacoes: ["focar", "focado"], sentidos: [
    { simples: "Concentração em uma coisa só.",
      detalhada: "\"Foco, força e fé\" é bordão de motivação. \"Perdi o foco\" descreve distração.",
      formal: "concentração" }]},

  { termo: "disciplina", idioma: "pt-BR", categorias: ["acao"], sentidos: [
    { simples: "Manter uma rotina mesmo sem vontade.",
      detalhada: "\"Disciplina supera motivação\" é frase comum em conteúdo de produtividade, e resume a ideia de não depender do ânimo do dia." }]},

  { termo: "rotina", idioma: "pt-BR", categorias: ["tempo"], variacoes: ["rotininha"], sentidos: [
    { simples: "A sequência de coisas que a pessoa faz todo dia.",
      detalhada: "\"Montar uma rotina\" é assunto frequente em conteúdo sobre organização e saúde mental." }]},

  { termo: "produtividade", idioma: "pt-BR", categorias: ["trabalho"], variacoes: ["produtivo"], sentidos: [
    { simples: "O quanto a pessoa consegue realizar num tempo.",
      detalhada: "Virou tema de uma indústria inteira de conteúdo, e também alvo de crítica por transformar descanso em culpa." }]},

  { termo: "pomodoro", idioma: "en", categorias: ["escolar", "trabalho"], sentidos: [
    { simples: "Técnica de estudo que alterna 25 minutos de trabalho com 5 de pausa.",
      detalhada: "Nome vindo do timer de cozinha em forma de tomate usado pelo criador da técnica nos anos 80.",
      formal: "técnica pomodoro" }]},

  { termo: "hiperfoco", idioma: "pt-BR", categorias: ["emocao"], sentidos: [
    { simples: "Concentração intensa em uma coisa só, a ponto de perder a noção do tempo.",
      detalhada: "Termo muito associado a discussões sobre TDAH, mas usado também no sentido comum de estar absorto.",
      formal: "concentração intensa" }]},

  { termo: "tdah", idioma: "pt-BR", categorias: ["atencao"], variacoes: ["déficit de atenção"], sentidos: [
    { simples: "Condição que afeta atenção, organização e controle de impulsos.",
      detalhada: "Transtorno do Déficit de Atenção com Hiperatividade. O termo virou piada na internet (\"meu TDAH falou mais alto\"), o que banaliza um diagnóstico que precisa de avaliação profissional." }]},

  { termo: "neurodivergente", idioma: "pt-BR", categorias: ["descricao"], variacoes: ["neuroatípico"], sentidos: [
    { simples: "Pessoa cujo funcionamento mental difere do considerado típico.",
      detalhada: "Inclui autismo, TDAH e outras condições. \"Neurotípico\" é o termo para quem não é. Vocabulário criado pela própria comunidade." }]},

  { termo: "espectro", idioma: "pt-BR", categorias: ["descricao"], variacoes: ["tea", "autista"], sentidos: [
    { simples: "Referência ao transtorno do espectro autista.",
      detalhada: "\"No espectro\" é como muita gente se refere ao próprio diagnóstico. \"Autista\" usado como ofensa na internet é discriminação, não gíria.",
      formal: "espectro autista" }]},

  { termo: "mascarar", idioma: "pt-BR", categorias: ["descricao", "emocao"], variacoes: ["masking"], sentidos: [
    { simples: "Esconder características próprias para parecer com os outros.",
      detalhada: "Do inglês \"masking\". Muito usado por pessoas autistas para descrever o esforço de se adaptar socialmente, que é exaustivo.",
      formal: "camuflagem social" }]},

  { termo: "socializar", idioma: "pt-BR", categorias: ["emocao"], variacoes: ["bateria social"], sentidos: [
    { simples: "Conviver com outras pessoas.",
      detalhada: "\"Bateria social acabou\" descreve o cansaço de quem precisa de tempo sozinho depois de conviver muito.",
      formal: "conviver" }]},

  { termo: "introvertido", idioma: "pt-BR", categorias: ["descricao"], variacoes: ["intro", "extrovertido"], sentidos: [
    { simples: "Pessoa que recarrega as energias ficando sozinha.",
      detalhada: "Não é o mesmo que tímido: um introvertido pode ser sociável e ainda assim precisar de tempo sozinho para descansar." }]},

  { termo: "sociável", idioma: "pt-BR", categorias: ["descricao"], variacoes: ["gente boa de rolê"], sentidos: [
    { simples: "Pessoa que se relaciona bem com os outros.",
      detalhada: "Diferente de extrovertido: dá para ser sociável e ainda assim precisar de silêncio depois." }]},

  { termo: "vergonha alheia", idioma: "pt-BR", categorias: ["emocao"], variacoes: ["cringe"], sentidos: [
    { simples: "Desconforto sentido por causa do constrangimento de outra pessoa.",
      detalhada: "É o mesmo sentimento que o inglês chama de \"cringe\", e o português tem nome próprio para ele há muito tempo." }]},

  { termo: "constrangido", idioma: "pt-BR", categorias: ["emocao"], variacoes: ["constrangimento"], sentidos: [
    { simples: "Sem graça, desconfortável numa situação.",
      detalhada: "\"Constrangimento\" também tem sentido legal, quando alguém é forçado a fazer algo contra a vontade." }]},

  { termo: "sem graça", idioma: "pt-BR", categorias: ["emocao"], sentidos: [
    { simples: "Envergonhado, sem saber como agir.",
      detalhada: "Também significa monótono, falando de coisas: \"a festa tava sem graça\".",
      formal: "envergonhado" }]},

  { termo: "gelado", idioma: "pt-BR", categorias: ["emocao"], variacoes: ["gelei"], sentidos: [
    { simples: "Reação de susto que paralisa a pessoa por um instante.",
      detalhada: "\"Gelei\" descreve o momento em que a pessoa fica sem reação diante de uma notícia ruim.",
      formal: "paralisado de susto" }]},

  { termo: "arrepiar", idioma: "pt-BR", categorias: ["emocao"], variacoes: ["arrepiou", "arrepio"], sentidos: [
    { simples: "Reação física a algo muito emocionante ou muito bonito.",
      detalhada: "\"Me arrepiei\" quase sempre é elogio: indica que aquilo emocionou de verdade.",
      formal: "emocionar" }]},

  { termo: "borboletas no estômago", idioma: "pt-BR", categorias: ["emocao", "relacionamento"], sentidos: [
    { simples: "Sensação de frio na barriga causada por nervosismo ou paixão.",
      detalhada: "Expressão internacional, presente em várias línguas com a mesma imagem.",
      formal: "nervosismo" }]},

  { termo: "friozinho na barriga", idioma: "pt-BR", categorias: ["emocao"], sentidos: [
    { simples: "Sensação de expectativa ou medo leve.",
      detalhada: "Versão brasileira das \"borboletas no estômago\". Serve para paixão, prova e altura.",
      formal: "nervosismo" }]},

  { termo: "nó na garganta", idioma: "pt-BR", categorias: ["emocao"], sentidos: [
    { simples: "Sensação de aperto que vem quando a pessoa segura o choro.",
      detalhada: "Descrição física de emoção contida, comum em relatos de momentos difíceis.",
      formal: "emoção contida" }]},

  { termo: "peso no peito", idioma: "pt-BR", categorias: ["emocao", "atencao"], sentidos: [
    { simples: "Sensação física de angústia.",
      detalhada: "Quando é constante e vem sem motivo aparente, pode ser sinal de ansiedade ou depressão, e vale procurar avaliação.",
      formal: "angústia" }]},

  { termo: "vazio", idioma: "pt-BR", categorias: ["emocao", "atencao"], variacoes: ["me sinto vazio"], sentidos: [
    { simples: "Sensação de falta de sentido ou de ausência de emoção.",
      detalhada: "Diferente de tristeza, que se sente. O vazio é a ausência do sentir, e quando persiste é sinal que pede atenção. O CVV atende de graça pelo 188.",
      formal: "vazio emocional" }]},

  { termo: "cansaço", idioma: "pt-BR", categorias: ["emocao"], variacoes: ["cansada", "exausto"], sentidos: [
    { simples: "Falta de energia física ou mental.",
      detalhada: "\"Cansaço que dormir não resolve\" é a forma como muita gente descreve exaustão emocional, diferente do cansaço físico." }]},

  { termo: "recarregar", idioma: "pt-BR", categorias: ["emocao"], variacoes: ["recarregar as energias"], sentidos: [
    { simples: "Descansar para recuperar disposição.",
      detalhada: "Metáfora de bateria, que ficou natural depois que todo mundo passou a carregar celular todo dia.",
      formal: "descansar" }]},

  { termo: "pausa", idioma: "pt-BR", categorias: ["tempo"], variacoes: ["dar uma pausa"], sentidos: [
    { simples: "Intervalo para descansar no meio de uma atividade.",
      detalhada: "\"Dar uma pausa\" também se aplica a relacionamento, com sentido bem diferente.",
      formal: "intervalo" }]},

  { termo: "desacelerar", idioma: "pt-BR", categorias: ["acao"], sentidos: [
    { simples: "Diminuir o ritmo da vida ou do trabalho.",
      detalhada: "Conselho comum em conversas sobre esgotamento, e mais fácil de dar do que de seguir.",
      formal: "reduzir o ritmo" }]},

  { termo: "desapegar", idioma: "pt-BR", categorias: ["acao", "emocao"], variacoes: ["desapego"], sentidos: [
    { simples: "Deixar ir algo ou alguém a que se estava preso.",
      detalhada: "Também usado literalmente para objetos: \"desapego\" é o nome dado a vendas de coisas usadas nas redes." }]},

  { termo: "seguir em frente", idioma: "pt-BR", categorias: ["acao"], variacoes: ["move on"], sentidos: [
    { simples: "Superar algo e continuar a vida.",
      detalhada: "\"Move on\" é a versão em inglês, também usada no Brasil.",
      formal: "superar" }]},

  { termo: "virar a página", idioma: "pt-BR", categorias: ["acao"], sentidos: [
    { simples: "Deixar um assunto para trás e começar outra fase.",
      detalhada: "Metáfora de livro. Mais definitivo que \"seguir em frente\": sugere encerramento consciente.",
      formal: "recomeçar" }]},

  { termo: "recomeçar", idioma: "pt-BR", categorias: ["acao"], variacoes: ["recomeço"], sentidos: [
    { simples: "Começar de novo depois de uma perda ou mudança.",
      detalhada: "Palavra comum em conversas sobre mudança de cidade, de carreira ou fim de relação." }]},

  { termo: "resiliência", idioma: "pt-BR", categorias: ["descricao"], variacoes: ["resiliente"], sentidos: [
    { simples: "Capacidade de se recuperar depois de algo difícil.",
      detalhada: "Termo da física que passou para a psicologia. É criticado quando usado para exigir que a pessoa aguente condições ruins em vez de mudá-las." }]},

  { termo: "vitimismo", idioma: "pt-BR", categorias: ["critica"], variacoes: ["se vitimizar"], sentidos: [
    { simples: "Acusação de que alguém exagera o próprio sofrimento para conseguir algo.",
      detalhada: "Termo usado com frequência para desqualificar queixas legítimas, então merece atenção quando aparece numa discussão.",
      formal: "exagero de sofrimento" }]},

  { termo: "mimimi", idioma: "pt-BR", categorias: ["critica"], sentidos: [
    { simples: "Forma debochada de chamar uma reclamação de exagerada.",
      detalhada: "Imita o som de choro. Costuma ser usado para encerrar discussão sem responder ao argumento.",
      formal: "reclamação" }]},

  { termo: "frescura", idioma: "pt-BR", categorias: ["critica"], variacoes: ["fresco", "frescurite"], sentidos: [
    { simples: "Exigência considerada desnecessária.",
      detalhada: "Muito usado para desqualificar necessidades reais, como restrição alimentar ou limite pessoal.",
      formal: "exigência excessiva" }]},

  { termo: "drama", idioma: "pt-BR", categorias: ["critica", "humor"], variacoes: ["dramática", "fazer drama"], sentidos: [
    { simples: "Reação exagerada a algo pequeno.",
      detalhada: "Pode ser brincadeira entre amigos ou crítica séria. \"Drama\" também nomeia a confusão em si: \"o drama da semana\".",
      formal: "exagero" }]},

  { termo: "paz", idioma: "pt-BR", categorias: ["emocao"], variacoes: ["em paz", "minha paz"], sentidos: [
    { simples: "Estado de tranquilidade emocional.",
      detalhada: "\"Não mexe com a minha paz\" virou frase comum sobre proteger o próprio bem-estar.",
      formal: "tranquilidade" }]},

  { termo: "energia", idioma: "pt-BR", categorias: ["emocao"], variacoes: ["boa energia", "energia ruim"], sentidos: [
    { simples: "A sensação que uma pessoa ou lugar transmite.",
      detalhada: "Uso sem conotação necessariamente espiritual: descreve impressão subjetiva, como \"vibe\".",
      formal: "clima" }]},

  { termo: "presença", idioma: "pt-BR", categorias: ["descricao"], variacoes: ["ter presença"], sentidos: [
    { simples: "Qualidade de quem chama atenção ao entrar num lugar.",
      detalhada: "Elogio sobre carisma e postura, não sobre aparência.",
      formal: "carisma" }]},

  { termo: "postura", idioma: "pt-BR", categorias: ["descricao"], sentidos: [
    { simples: "O jeito como a pessoa se comporta diante dos outros.",
      detalhada: "\"Falta de postura\" é crítica ao comportamento, não ao corpo.",
      formal: "comportamento" }]},

  { termo: "atitude", idioma: "pt-BR", categorias: ["descricao"], variacoes: ["ter atitude"], sentidos: [
    { simples: "Coragem para agir em vez de só reclamar.",
      detalhada: "\"Falta atitude\" cobra ação. Diferente do sentido comum da palavra, que é apenas comportamento.",
      formal: "iniciativa" }]},

  { termo: "personalidade", idioma: "pt-BR", categorias: ["descricao"], variacoes: ["ter personalidade"], sentidos: [
    { simples: "Ter opinião própria e não seguir a maioria por seguir.",
      detalhada: "\"Tem personalidade\" é elogio à independência, e não descrição neutra do caráter.",
      formal: "independência" }]},

  { termo: "vaidoso", idioma: "pt-BR", categorias: ["descricao"], variacoes: ["vaidade"], sentidos: [
    { simples: "Quem se preocupa muito com a própria aparência.",
      detalhada: "Nem sempre é crítica: cuidar de si é visto como positivo, e o excesso é o que gera o julgamento." }]},

  { termo: "produzida", idioma: "pt-BR", categorias: ["descricao"], variacoes: ["produzido", "se produzir"], sentidos: [
    { simples: "Arrumada com capricho para uma ocasião.",
      detalhada: "\"Se produzir\" é o ato de se arrumar bem. Elogio quando dito com admiração.",
      formal: "arrumado" }]},

  { termo: "natural", idioma: "pt-BR", categorias: ["descricao"], variacoes: ["sem filtro"], sentidos: [
    { simples: "Sem maquiagem ou edição.",
      detalhada: "\"Sem filtro\" ganhou peso com as redes sociais, onde a edição virou padrão.",
      formal: "sem edição" }]},

  { termo: "filtro", idioma: "pt-BR", categorias: ["redes"], sentidos: [
    { simples: "Efeito que muda a aparência da foto ou do vídeo.",
      detalhada: "Os filtros que alteram o rosto são discutidos por afetarem a autoimagem, principalmente de adolescentes.",
      formal: "efeito de imagem" }]},

  { termo: "photoshop", idioma: "en", categorias: ["redes"], variacoes: ["photoshopado", "editado"], sentidos: [
    { simples: "Imagem alterada por programa de edição.",
      detalhada: "Nome de um programa que virou verbo. \"Photoshopado\" acusa a foto de não corresponder à realidade.",
      formal: "imagem editada" }]},

  { termo: "sarado", idioma: "pt-BR", categorias: ["descricao"], variacoes: ["sarada"], sentidos: [
    { simples: "Pessoa com corpo atlético e definido.",
      detalhada: "Termo mais antigo que \"shape\" e \"trincado\", com o mesmo sentido.",
      formal: "atlético" }]},

  { termo: "magrelo", idioma: "pt-BR", categorias: ["descricao"], variacoes: ["magricela"], sentidos: [
    { simples: "Pessoa muito magra.",
      detalhada: "Pode ser apelido carinhoso entre próximos ou comentário indesejado sobre o corpo alheio, dependendo de quem fala.",
      formal: "muito magro" }]},

  { termo: "gostoso", idioma: "pt-BR", categorias: ["descricao"], variacoes: ["gostosa"], sentidos: [
    { simples: "Elogio à aparência física de alguém.",
      detalhada: "Dito a desconhecidos na rua, deixa de ser elogio e passa a ser importunação sexual, que é crime no Brasil desde 2018.",
      formal: "atraente" },
    { simples: "Falando de comida, quer dizer saboroso.",
      detalhada: "É o sentido original e o mais comum. O contexto separa sem dificuldade.",
      formal: "saboroso" }]},

  { termo: "cheiroso", idioma: "pt-BR", categorias: ["descricao"], variacoes: ["cheirosa"], sentidos: [
    { simples: "Que tem cheiro bom.",
      detalhada: "Usado como elogio afetuoso, principalmente com crianças e pessoas próximas.",
      formal: "perfumado" }]},

  { termo: "arrumadinho", idioma: "pt-BR", categorias: ["descricao"], sentidos: [
    { simples: "Bem apresentado, com aparência cuidada.",
      detalhada: "Diminutivo afetuoso, usado principalmente por gerações mais velhas.",
      formal: "bem apresentado" }]},

  { termo: "descabelado", idioma: "pt-BR", categorias: ["descricao", "humor"], sentidos: [
    { simples: "Com o cabelo bagunçado.",
      detalhada: "Também usado em sentido figurado para quem está atarefado: \"cheguei descabelada de tanta correria\".",
      formal: "despenteado" }]},
];
