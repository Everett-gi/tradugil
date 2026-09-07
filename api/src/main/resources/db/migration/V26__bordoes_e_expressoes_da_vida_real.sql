-- ---------------------------------------------------------------------------
-- bordoes e expressoes da vida real
--
-- 30 verbetes, 30 sentidos.
--
-- GERADO por curadoria/gerar-migracao.mjs. Nao edite este arquivo a mao:
-- mexa na fonte em curadoria/termos/ e gere de novo. Depois de aplicada,
-- a migracao e imutavel como qualquer outra, e termo novo entra em
-- migracao nova.
--
-- As chaves de busca (termo_normalizado, termo_colapsado) sao calculadas
-- pela mesma regra de Normalizador.java. ConsistenciaDoSeedIT confere no
-- banco que elas batem.
-- ---------------------------------------------------------------------------

INSERT INTO giria (termo, termo_normalizado, termo_colapsado, idioma_id, nsfw, risco_menor)
SELECT t.termo, t.norm, t.colapsado, i.id, t.nsfw, t.risco
FROM (VALUES
    ('lá ele', 'la ele', 'la ele', 'pt-BR', FALSE, FALSE),
    ('aí dento', 'ai dento', 'ai dento', 'pt-BR', FALSE, FALSE),
    ('meu consagrado', 'meu consagrado', 'meu consagrado', 'pt-BR', FALSE, FALSE),
    ('é nóis', 'e nois', 'e nois', 'pt-BR', FALSE, FALSE),
    ('tamo junto', 'tamo junto', 'tamo junto', 'pt-BR', FALSE, FALSE),
    ('salve', 'salve', 'salve', 'pt-BR', FALSE, FALSE),
    ('emendar o feriado', 'emendar o feriado', 'emendar o feriado', 'pt-BR', FALSE, FALSE),
    ('de segunda a segunda', 'de segunda a segunda', 'de segunda a segunda', 'pt-BR', FALSE, FALSE),
    ('daqui a pouco', 'daqui a pouco', 'daqui a pouco', 'pt-BR', FALSE, FALSE),
    ('já já', 'ja ja', 'ja ja', 'pt-BR', FALSE, FALSE),
    ('de vez em quando', 'de vez em quando', 'de vez em quando', 'pt-BR', FALSE, FALSE),
    ('à toa', 'a toa', 'a toa', 'pt-BR', FALSE, FALSE),
    ('na correria', 'na correria', 'na correria', 'pt-BR', FALSE, FALSE),
    ('madrugar', 'madrugar', 'madrugar', 'pt-BR', FALSE, FALSE),
    ('gororoba', 'gororoba', 'gororoba', 'pt-BR', FALSE, FALSE),
    ('matar a larica', 'matar a larica', 'matar a larica', 'pt-BR', FALSE, FALSE),
    ('pedir ifood', 'pedir ifood', 'pedir ifood', 'pt-BR', FALSE, FALSE),
    ('encher a cara', 'encher a cara', 'encher a cara', 'pt-BR', FALSE, TRUE),
    ('birita', 'birita', 'birita', 'pt-BR', FALSE, FALSE),
    ('tirar o pé do chão', 'tirar o pe do chao', 'tirar o pe do chao', 'pt-BR', FALSE, FALSE),
    ('pedir a saideira', 'pedir a saideira', 'pedir a saideira', 'pt-BR', FALSE, FALSE),
    ('dar um match', 'dar um match', 'dar um match', 'pt-BR', FALSE, FALSE),
    ('sair do sério', 'sair do serio', 'sair do serio', 'pt-BR', FALSE, FALSE),
    ('levar um fora', 'levar um fora', 'levar um fora', 'pt-BR', FALSE, FALSE),
    ('de rolo', 'de rolo', 'de rolo', 'pt-BR', FALSE, FALSE),
    ('pedir em namoro', 'pedir em namoro', 'pedir em namoro', 'pt-BR', FALSE, FALSE),
    ('termo', 'termo', 'termo', 'pt-BR', FALSE, FALSE),
    ('ficar às vezes', 'ficar as vezes', 'ficar as vezes', 'pt-BR', FALSE, FALSE),
    ('grude', 'grude', 'grude', 'pt-BR', FALSE, FALSE),
    ('cara-metade', 'cara metade', 'cara metade', 'pt-BR', FALSE, FALSE)
) AS t(termo, norm, colapsado, idioma, nsfw, risco)
JOIN idioma i ON i.codigo = t.idioma
ON CONFLICT (termo_normalizado, idioma_id) DO NOTHING;

INSERT INTO definicao (giria_id, explicacao_simples, explicacao_detalhada,
                       equivalente_formal, fonte_id)
SELECT g.id, d.simples, d.detalhada, d.formal,
       (SELECT id FROM fonte WHERE tipo = 'CURADORIA')
FROM (VALUES
    ('la ele', 'pt-BR', 'Brincadeira para se afastar de uma frase que saiu com duplo sentido.', 'Se alguém diz sem querer uma frase que dá para entender de dois jeitos, outra pessoa responde "lá ele", empurrando o sentido malicioso para um terceiro. É piada, e a reação esperada é rir. Nasceu na Bahia e se espalhou pelo país pela internet, com um empurrão do mascote da Copa de 2022, chamado La''eeb, cujo nome soa igual.', 'não estou falando de mim'),
    ('ai dento', 'pt-BR', 'Comemoração de quem acertou em cheio ou se deu bem.', 'Grito de aprovação nordestino, dito quando algo dá certo. Aparece muito ao lado de "lá ele", pelo mesmo caminho de popularização.', NULL),
    ('meu consagrado', 'pt-BR', 'Jeito exagerado e afetuoso de chamar alguém.', 'Bordão de humorista que virou vocativo comum. Trata a pessoa como se fosse uma celebridade, de brincadeira.', NULL),
    ('e nois', 'pt-BR', '"Estamos juntos." Confirmação de lealdade ou de combinado.', 'Serve de despedida, de agradecimento e de resposta a um favor. Não é erro de português: é forma marcada de propósito.', NULL),
    ('tamo junto', 'pt-BR', '"Conte comigo."', 'Dito depois de um pedido de ajuda, ou como despedida entre amigos. Primo próximo de "é nóis".', NULL),
    ('salve', 'pt-BR', 'Cumprimento informal, equivalente a "e aí".', 'Muito usado para abrir vídeo e mensagem de áudio. Também é o pedido de socorro: "mandei um salve" é ter pedido ajuda.', NULL),
    ('emendar o feriado', 'pt-BR', 'Não trabalhar no dia entre o feriado e o fim de semana.', 'Feriado numa quinta vira quatro dias de folga se a sexta for emendada. É a conta que todo brasileiro faz em janeiro.', NULL),
    ('de segunda a segunda', 'pt-BR', 'Todos os dias, sem folga.', '"Trabalho de segunda a segunda" é queixa de quem não tem descanso.', NULL),
    ('daqui a pouco', 'pt-BR', 'Em breve, sem hora marcada.', 'Pode significar cinco minutos ou duas horas. É uma das expressões que mais confundem quem espera precisão.', NULL),
    ('ja ja', 'pt-BR', 'Muito em breve, mais rápido que "daqui a pouco".', '"Vou já já" promete pressa. Na prática, costuma ser tão elástico quanto o "daqui a pouco".', NULL),
    ('de vez em quando', 'pt-BR', 'Às vezes, sem regularidade.', 'Menos frequente que "sempre" e mais que "quase nunca".', 'ocasionalmente'),
    ('a toa', 'pt-BR', 'Sem fazer nada, ou sem motivo.', '"Tô à toa" é estar livre. "Ele brigou à toa" é ter brigado sem razão.', NULL),
    ('na correria', 'pt-BR', 'Muito ocupado, sem tempo.', '"Tô na correria" é a resposta padrão de quem não pode conversar agora. Também descreve o trabalho informal do dia a dia.', NULL),
    ('madrugar', 'pt-BR', 'Ficar acordado até de madrugada, ou acordar muito cedo.', 'Os dois sentidos convivem, e o contexto separa: "madruguei jogando" é o primeiro, "madruguei pra pegar fila" é o segundo.', NULL),
    ('gororoba', 'pt-BR', 'Comida mal feita ou de aparência duvidosa.', 'Nem sempre é crítica: pode ser afetuoso quando alguém fala da própria comida improvisada.', NULL),
    ('matar a larica', 'pt-BR', 'Comer para acabar com uma fome repentina.', '"Larica" é a fome que bate de repente, muitas vezes de madrugada.', NULL),
    ('pedir ifood', 'pt-BR', 'Encomendar comida por aplicativo.', 'O nome do aplicativo virou verbo, do mesmo jeito que "xerocar" virou sinônimo de fotocopiar.', NULL),
    ('encher a cara', 'pt-BR', 'Beber muito álcool.', 'Dito de forma leve entre adultos. Quando aparece com frequência na conversa de um adolescente, vale prestar atenção.', 'beber em excesso'),
    ('birita', 'pt-BR', 'Bebida alcoólica.', 'Palavra antiga, hoje usada mais em tom de brincadeira que a sério.', NULL),
    ('tirar o pe do chao', 'pt-BR', 'Beber a ponto de ficar alegre, sem passar do ponto.', 'Expressão de bar, dita com humor. É o estágio anterior a "encher a cara".', NULL),
    ('pedir a saideira', 'pt-BR', 'A última bebida antes de ir embora.', 'Piada corrente é que a saideira nunca é a última de verdade.', NULL),
    ('dar um match', 'pt-BR', 'Quando duas pessoas se curtem no mesmo aplicativo de namoro.', 'Só depois do match é que dá para conversar. "Deu match" também virou expressão para qualquer coincidência feliz.', NULL),
    ('sair do serio', 'pt-BR', 'Perder a paciência.', '"Ele me tirou do sério" atribui a culpa a quem provocou.', 'perder a paciência'),
    ('levar um fora', 'pt-BR', 'Ser rejeitado por alguém.', '"Dar um fora" é o lado de quem rejeita. Também significa cometer uma gafe: "dei um fora na frente de todo mundo".', NULL),
    ('de rolo', 'pt-BR', 'Envolvimento amoroso sem compromisso definido.', 'Menos que namoro e mais que um encontro só. "Rolo" sozinho também significa confusão.', NULL),
    ('pedir em namoro', 'pt-BR', 'Propor formalmente começar um namoro.', 'Continua sendo um marco entre adolescentes, e costuma ser anunciado nas redes depois.', NULL),
    ('termo', 'pt-BR', 'O fim de um relacionamento.', '"Tá em fase de término" descreve o período difícil depois de terminar, não o momento em si.', NULL),
    ('ficar as vezes', 'pt-BR', 'Sair com alguém de vez em quando, sem namorar.', '"Ficante" é a pessoa com quem se tem esse tipo de relação.', NULL),
    ('grude', 'pt-BR', 'Pessoa que quer atenção o tempo todo.', 'Crítica leve, dita entre amigos sobre um namoro que sufoca.', NULL),
    ('cara metade', 'pt-BR', 'A pessoa amada, num tom romântico.', 'Hoje aparece bastante em tom de brincadeira, justamente por soar antiquado.', NULL)
) AS d(norm, idioma, simples, detalhada, formal)
JOIN idioma i ON i.codigo = d.idioma
JOIN giria g ON g.termo_normalizado = d.norm AND g.idioma_id = i.id;

INSERT INTO giria_variacao (giria_id, variacao, variacao_normalizada,
                            variacao_colapsada)
SELECT g.id, v.variacao, v.norm, v.colapsado
FROM (VALUES
    ('la ele', 'pt-BR', 'la ele', 'la ele', 'la ele'),
    ('la ele', 'pt-BR', 'lá ele!', 'la ele', 'la ele'),
    ('la ele', 'pt-BR', 'laele', 'laele', 'laele'),
    ('la ele', 'pt-BR', 'lá nele', 'la nele', 'la nele'),
    ('ai dento', 'pt-BR', 'ai dento', 'ai dento', 'ai dento'),
    ('ai dento', 'pt-BR', 'aí dentro', 'ai dentro', 'ai dentro'),
    ('ai dento', 'pt-BR', 'ai dentro', 'ai dentro', 'ai dentro'),
    ('meu consagrado', 'pt-BR', 'consagrado', 'consagrado', 'consagrado'),
    ('meu consagrado', 'pt-BR', 'minha consagrada', 'minha consagrada', 'minha consagrada'),
    ('e nois', 'pt-BR', 'e nois', 'e nois', 'e nois'),
    ('e nois', 'pt-BR', 'é nós', 'e nos', 'e nos'),
    ('e nois', 'pt-BR', 'nóis', 'nois', 'nois'),
    ('tamo junto', 'pt-BR', 'tamo juntos', 'tamo juntos', 'tamo juntos'),
    ('tamo junto', 'pt-BR', 'tamos junto', 'tamos junto', 'tamos junto'),
    ('salve', 'pt-BR', 'salve salve', 'salve salve', 'salve salve'),
    ('salve', 'pt-BR', 'salvee', 'salvee', 'salvee'),
    ('emendar o feriado', 'pt-BR', 'emendar', 'emendar', 'emendar'),
    ('emendar o feriado', 'pt-BR', 'emendou', 'emendou', 'emendou'),
    ('de segunda a segunda', 'pt-BR', 'segunda a segunda', 'segunda a segunda', 'segunda a segunda'),
    ('daqui a pouco', 'pt-BR', 'daqui a pouquinho', 'daqui a pouquinho', 'daqui a pouquinho'),
    ('daqui a pouco', 'pt-BR', 'daqui apouco', 'daqui apouco', 'daqui apouco'),
    ('ja ja', 'pt-BR', 'ja ja', 'ja ja', 'ja ja'),
    ('ja ja', 'pt-BR', 'jajá', 'jaja', 'jaja'),
    ('de vez em quando', 'pt-BR', 'de vez enquando', 'de vez enquando', 'de vez enquando'),
    ('de vez em quando', 'pt-BR', 'vez ou outra', 'vez ou outra', 'vez ou outra'),
    ('a toa', 'pt-BR', 'a toa', 'a toa', 'a toa'),
    ('a toa', 'pt-BR', 'atoa', 'atoa', 'atoa'),
    ('na correria', 'pt-BR', 'correria', 'correria', 'correria'),
    ('na correria', 'pt-BR', 'na correia', 'na correia', 'na correia'),
    ('madrugar', 'pt-BR', 'madrugou', 'madrugou', 'madrugou'),
    ('madrugar', 'pt-BR', 'virar a noite', 'virar a noite', 'virar a noite'),
    ('gororoba', 'pt-BR', 'gororobinha', 'gororobinha', 'gororobinha'),
    ('matar a larica', 'pt-BR', 'larica', 'larica', 'larica'),
    ('matar a larica', 'pt-BR', 'tá com larica', 'ta com larica', 'ta com larica'),
    ('pedir ifood', 'pt-BR', 'pedir delivery', 'pedir delivery', 'pedir delivery'),
    ('pedir ifood', 'pt-BR', 'pedir por app', 'pedir por app', 'pedir por app'),
    ('encher a cara', 'pt-BR', 'encheu a cara', 'encheu a cara', 'encheu a cara'),
    ('birita', 'pt-BR', 'biritar', 'biritar', 'biritar'),
    ('birita', 'pt-BR', 'biritinha', 'biritinha', 'biritinha'),
    ('tirar o pe do chao', 'pt-BR', 'tirar o pe do chao', 'tirar o pe do chao', 'tirar o pe do chao'),
    ('pedir a saideira', 'pt-BR', 'saideira', 'saideira', 'saideira'),
    ('pedir a saideira', 'pt-BR', 'última saideira', 'ultima saideira', 'ultima saideira'),
    ('dar um match', 'pt-BR', 'deu match', 'deu match', 'deu match'),
    ('dar um match', 'pt-BR', 'match', 'match', 'match'),
    ('sair do serio', 'pt-BR', 'saiu do serio', 'saiu do serio', 'saiu do serio'),
    ('levar um fora', 'pt-BR', 'tomar um fora', 'tomar um fora', 'tomar um fora'),
    ('levar um fora', 'pt-BR', 'dar um fora', 'dar um fora', 'dar um fora'),
    ('de rolo', 'pt-BR', 'rolo', 'rolo', 'rolo'),
    ('de rolo', 'pt-BR', 'tá de rolo', 'ta de rolo', 'ta de rolo'),
    ('pedir em namoro', 'pt-BR', 'pediu em namoro', 'pediu em namoro', 'pediu em namoro'),
    ('termo', 'pt-BR', 'terminou', 'terminou', 'terminou'),
    ('termo', 'pt-BR', 'término', 'termino', 'termino'),
    ('ficar as vezes', 'pt-BR', 'ficar as vezes', 'ficar as vezes', 'ficar as vezes'),
    ('ficar as vezes', 'pt-BR', 'ficante', 'ficante', 'ficante'),
    ('grude', 'pt-BR', 'grudento', 'grudento', 'grudento'),
    ('grude', 'pt-BR', 'grudenta', 'grudenta', 'grudenta'),
    ('cara metade', 'pt-BR', 'cara metade', 'cara metade', 'cara metade'),
    ('cara metade', 'pt-BR', 'minha metade', 'minha metade', 'minha metade')
) AS v(termo_norm, idioma, variacao, norm, colapsado)
JOIN idioma i ON i.codigo = v.idioma
JOIN giria g ON g.termo_normalizado = v.termo_norm AND g.idioma_id = i.id
ON CONFLICT (variacao_normalizada, giria_id) DO NOTHING;

INSERT INTO giria_categoria (giria_id, categoria_id)
SELECT g.id, c.id
FROM (VALUES
    ('la ele', 'pt-BR', 'humor'),
    ('la ele', 'pt-BR', 'rua'),
    ('ai dento', 'pt-BR', 'humor'),
    ('ai dento', 'pt-BR', 'regional'),
    ('meu consagrado', 'pt-BR', 'humor'),
    ('meu consagrado', 'pt-BR', 'rua'),
    ('e nois', 'pt-BR', 'rua'),
    ('e nois', 'pt-BR', 'elogio'),
    ('tamo junto', 'pt-BR', 'rua'),
    ('tamo junto', 'pt-BR', 'familia'),
    ('salve', 'pt-BR', 'rua'),
    ('emendar o feriado', 'pt-BR', 'tempo'),
    ('de segunda a segunda', 'pt-BR', 'tempo'),
    ('de segunda a segunda', 'pt-BR', 'trabalho'),
    ('daqui a pouco', 'pt-BR', 'tempo'),
    ('ja ja', 'pt-BR', 'tempo'),
    ('de vez em quando', 'pt-BR', 'tempo'),
    ('a toa', 'pt-BR', 'tempo'),
    ('a toa', 'pt-BR', 'emocao'),
    ('na correria', 'pt-BR', 'tempo'),
    ('na correria', 'pt-BR', 'trabalho'),
    ('madrugar', 'pt-BR', 'tempo'),
    ('gororoba', 'pt-BR', 'comida'),
    ('gororoba', 'pt-BR', 'critica'),
    ('matar a larica', 'pt-BR', 'comida'),
    ('pedir ifood', 'pt-BR', 'comida'),
    ('encher a cara', 'pt-BR', 'comida'),
    ('encher a cara', 'pt-BR', 'atencao'),
    ('birita', 'pt-BR', 'comida'),
    ('tirar o pe do chao', 'pt-BR', 'comida'),
    ('tirar o pe do chao', 'pt-BR', 'humor'),
    ('pedir a saideira', 'pt-BR', 'comida'),
    ('pedir a saideira', 'pt-BR', 'humor'),
    ('dar um match', 'pt-BR', 'relacionamento'),
    ('dar um match', 'pt-BR', 'redes'),
    ('sair do serio', 'pt-BR', 'relacionamento'),
    ('sair do serio', 'pt-BR', 'emocao'),
    ('levar um fora', 'pt-BR', 'relacionamento'),
    ('de rolo', 'pt-BR', 'relacionamento'),
    ('pedir em namoro', 'pt-BR', 'relacionamento'),
    ('termo', 'pt-BR', 'relacionamento'),
    ('ficar as vezes', 'pt-BR', 'relacionamento'),
    ('grude', 'pt-BR', 'relacionamento'),
    ('grude', 'pt-BR', 'critica'),
    ('cara metade', 'pt-BR', 'relacionamento')
) AS gc(termo_norm, idioma, categoria)
JOIN idioma i ON i.codigo = gc.idioma
JOIN giria g ON g.termo_normalizado = gc.termo_norm AND g.idioma_id = i.id
JOIN categoria c ON c.slug = gc.categoria
ON CONFLICT DO NOTHING;

