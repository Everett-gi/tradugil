-- ---------------------------------------------------------------------------
-- emotes e chat da twitch
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
    ('OMEGALUL', 'omegalul', 'omegalul', 'en', FALSE, FALSE),
    ('LUL', 'lul', 'lul', 'en', FALSE, FALSE),
    ('PepeLaugh', 'pepelaugh', 'pepelaugh', 'en', FALSE, FALSE),
    ('4Head', '4head', '4head', 'en', FALSE, FALSE),
    ('PepeHands', 'pepehands', 'pepehands', 'en', FALSE, FALSE),
    ('FeelsBadMan', 'feelsbadman', 'feelsbadman', 'en', FALSE, FALSE),
    ('FeelsGoodMan', 'feelsgoodman', 'feelsgoodman', 'en', FALSE, FALSE),
    ('BibleThump', 'biblethump', 'biblethump', 'en', FALSE, FALSE),
    ('NotLikeThis', 'notlikethis', 'notlikethis', 'en', FALSE, FALSE),
    ('PogChamp', 'pogchamp', 'pogchamp', 'en', FALSE, FALSE),
    ('HYPERS', 'hypers', 'hypers', 'en', FALSE, FALSE),
    ('Clap', 'clap', 'clap', 'en', FALSE, FALSE),
    ('catJAM', 'catjam', 'catjam', 'en', FALSE, FALSE),
    ('pepeD', 'peped', 'peped', 'en', FALSE, FALSE),
    ('widepeepoHappy', 'widepeepohappy', 'widepeepohappy', 'en', FALSE, FALSE),
    ('WeirdChamp', 'weirdchamp', 'weirdchamp', 'en', FALSE, FALSE),
    ('modCheck', 'modcheck', 'modcheck', 'en', FALSE, FALSE),
    ('Jebaited', 'jebaited', 'jebaited', 'en', FALSE, FALSE),
    ('ResidentSleeper', 'residentsleeper', 'residentsleeper', 'en', FALSE, FALSE),
    ('DansGame', 'dansgame', 'dansgame', 'en', FALSE, FALSE),
    ('PauseChamp', 'pausechamp', 'pausechamp', 'en', FALSE, FALSE),
    ('5Head', '5head', '5head', 'en', FALSE, FALSE),
    ('Aware', 'aware', 'aware', 'en', FALSE, FALSE),
    ('Chatting', 'chatting', 'chatting', 'en', FALSE, FALSE),
    ('Cinema', 'cinema', 'cinema', 'en', FALSE, FALSE),
    ('Kappa', 'kappa', 'kappa', 'en', FALSE, FALSE),
    ('TriHard', 'trihard', 'trihard', 'en', FALSE, FALSE),
    ('lurkar', 'lurkar', 'lurkar', 'en', FALSE, FALSE),
    ('primeiro', 'primeiro', 'primeiro', 'pt-BR', FALSE, FALSE),
    ('corte', 'corte', 'corte', 'pt-BR', FALSE, FALSE)
) AS t(termo, norm, colapsado, idioma, nsfw, risco)
JOIN idioma i ON i.codigo = t.idioma
ON CONFLICT (termo_normalizado, idioma_id) DO NOTHING;

INSERT INTO definicao (giria_id, explicacao_simples, explicacao_detalhada,
                       equivalente_formal, fonte_id)
SELECT g.id, d.simples, d.detalhada, d.formal,
       (SELECT id FROM fonte WHERE tipo = 'CURADORIA')
FROM (VALUES
    ('omegalul', 'en', 'Risada enorme. É o "chorei de rir" do chat da Twitch.', 'A imagem é o rosto do humorista espanhol El Risitas, esticado e deformado. O "OMEGA" na frente é o exagero: quando "LUL" não é suficiente, usa-se este. Aparece quando alguém erra feio, passa vergonha e percebe.', 'gargalhada'),
    ('lul', 'en', 'Risada. É o "kkkk" do chat da Twitch.', 'Mostra o rosto do humorista espanhol El Risitas rindo. "LULW" é a versão mais larga, usada para rir mais forte.', NULL),
    ('pepelaugh', 'en', 'O chat rindo de alguma coisa que a pessoa transmitindo ainda não percebeu.', 'A imagem é o sapo Pepe rindo com os olhos apertados. Não é riso qualquer: é o riso de quem sabe o que vem. O chat enche de PepeLaugh quando o inimigo está atrás do streamer e ele não viu. Costuma vir acompanhado de "here it comes".', NULL),
    ('4head', 'en', 'Risada de piada ruim, quase sempre irônica.', 'Mostra o rosto de um jogador profissional rindo, com a testa grande. Usar de verdade é raro: quase sempre é deboche.', NULL),
    ('pepehands', 'en', 'Choro. Tristeza mais forte que a do Sadge.', 'O sapo Pepe chorando com as mãos no rosto. Usado quando algo é comovente de verdade, e também de forma exagerada e cômica.', NULL),
    ('feelsbadman', 'en', 'Que situação ruim. Solidariedade com quem se deu mal.', 'O sapo Pepe de cara fechada e triste. É o emote original da família Pepe, de onde saiu todo o resto.', NULL),
    ('feelsgoodman', 'en', 'Que bom. Satisfação tranquila.', 'O sapo Pepe com um sorriso satisfeito. É o oposto exato do FeelsBadMan.', NULL),
    ('biblethump', 'en', 'Choro, quase sempre exagerado de brincadeira.', 'É o personagem chorão de um jogo chamado The Binding of Isaac. Um dos emotes mais antigos da Twitch.', NULL),
    ('notlikethis', 'en', '"Assim não!" Frustração com uma derrota que parecia evitável.', 'Mostra uma pessoa com a cabeça baixa e a mão na testa. Aparece quando tudo estava indo bem e desandou no fim.', NULL),
    ('pogchamp', 'en', 'Espanto e empolgação com uma jogada incrível.', 'Foi o emote de rosto surpreso mais famoso da Twitch. Deu origem a "pog" e "poggers". A imagem original foi retirada pela plataforma em 2021 e substituída várias vezes; "PogU" é uma das versões que o chat passou a usar no lugar.', NULL),
    ('hypers', 'en', 'Empolgação máxima, hype puro.', 'Mostra um rosto gritando de animação. Aparece em abertura de campeonato e em anúncio muito esperado.', NULL),
    ('clap', 'en', 'Palmas. Aprovação.', '"peepoClap" é a versão com um bonequinho batendo palmas, e costuma ser mais afetuosa que irônica.', NULL),
    ('catjam', 'en', 'O chat curtindo a música que está tocando.', 'Um gato branco balançando a cabeça no ritmo. Vira uma fileira inteira no chat quando a música agrada.', NULL),
    ('peped', 'en', 'Dançando. O chat está gostando do som.', 'O sapo Pepe dançando com fones de ouvido. Primo do catJAM, com o mesmo uso.', NULL),
    ('widepeepohappy', 'en', 'Felicidade grande, quase infantil.', 'Um bonequinho sorrindo de orelha a orelha, esticado na horizontal. É a reação mais afetuosa do chat.', NULL),
    ('weirdchamp', 'en', '"Que estranho." Desaprovação de algo esquisito.', 'Rosto de decepção incrédula. Aparece quando alguém diz uma opinião muito ruim ou faz algo constrangedor.', NULL),
    ('modcheck', 'en', '"Quem perguntou?" Ou "cadê?", procurando alguma coisa.', 'Um bonequinho fazendo sombra com a mão nos olhos, procurando no horizonte. Nasceu para chamar moderador ausente e hoje é usado sobretudo como deboche com quem contou algo que ninguém pediu.', NULL),
    ('jebaited', 'en', '"Caiu na pegadinha."', 'Rosto de um apresentador antigo da Twitch com expressão maliciosa. Usado quando alguém foi enganado de brincadeira.', NULL),
    ('residentsleeper', 'en', '"Que tédio." A transmissão está parada.', 'Uma pessoa dormindo. Veio de uma maratona de jogo em que o jogador adormeceu ao vivo.', NULL),
    ('dansgame', 'en', 'Nojo ou desaprovação.', 'Rosto com expressão de repulsa. É dos emotes mais antigos da plataforma.', NULL),
    ('pausechamp', 'en', 'Suspense. O chat esperando para ver o que vai acontecer.', 'Um rosto congelado no meio de uma reação. Aparece nos segundos antes de um resultado.', NULL),
    ('5head', 'en', '"Que jogada inteligente." Muitas vezes dito com ironia.', 'A imagem tem a testa alargada, sugerindo cérebro grande. Serve tanto para elogiar de verdade quanto para debochar de uma ideia burra.', NULL),
    ('aware', 'en', '"Ele sabe o que está fazendo." Quase sempre irônico.', 'Usado quando a pessoa faz algo estranho de propósito, ou quando o chat quer sugerir que ela não faz ideia do que está fazendo.', NULL),
    ('chatting', 'en', '"Falando demais." Deboche com quem está discursando.', 'O emote mostra um bonequinho de boca aberta sem parar. "Yapping" tem o mesmo sentido e é mais usado fora da Twitch.', NULL),
    ('cinema', 'en', '"Isso foi arte." Elogio exagerado a um momento da transmissão.', 'Dito com ironia na maioria das vezes, sobre um momento absolutamente banal.', NULL),
    ('kappa', 'en', 'Marca que a frase anterior era sarcasmo.', 'O rosto em preto e branco de um antigo funcionário da Twitch. É o emote mais famoso da plataforma e funciona como um "estou brincando" no fim da mensagem.', NULL),
    ('trihard', 'en', 'Empolgação exagerada.', 'Rosto de um streamer com expressão animada. É um dos emotes mais usados da plataforma e também um dos mais usados de forma racista, o que faz muitos canais o bloquearem.', NULL),
    ('lurkar', 'en', 'Assistir à transmissão sem escrever nada no chat.', '"Vou lurkar" avisa que a pessoa continua ali, só calada. É bem-vindo: lurker conta como espectador.', NULL),
    ('primeiro', 'pt-BR', 'Quem escreve para marcar que chegou antes de todo mundo.', 'Vale para o chat da live e para o comentário do vídeo. É motivo de deboche na mesma medida em que é motivo de orgulho.', NULL),
    ('corte', 'pt-BR', 'Trecho curto tirado de uma live longa e publicado sozinho.', 'Existem canais que vivem só de publicar cortes de outras pessoas, com autorização. No Brasil, é assim que a maioria conhece um streamer antes de assistir a uma live inteira.', NULL)
) AS d(norm, idioma, simples, detalhada, formal)
JOIN idioma i ON i.codigo = d.idioma
JOIN giria g ON g.termo_normalizado = d.norm AND g.idioma_id = i.id;

INSERT INTO giria_variacao (giria_id, variacao, variacao_normalizada,
                            variacao_colapsada)
SELECT g.id, v.variacao, v.norm, v.colapsado
FROM (VALUES
    ('omegalul', 'en', 'omegalol', 'omegalol', 'omegalol'),
    ('omegalul', 'en', 'omega lul', 'omega lul', 'omega lul'),
    ('omegalul', 'en', 'omegalull', 'omegalull', 'omegalull'),
    ('lul', 'en', 'lulw', 'lulw', 'lulw'),
    ('lul', 'en', 'lul w', 'lul w', 'lul w'),
    ('pepelaugh', 'en', 'pepelef', 'pepelef', 'pepelef'),
    ('pepelaugh', 'en', 'pepe laugh', 'pepe laugh', 'pepe laugh'),
    ('pepelaugh', 'en', 'pepelaf', 'pepelaf', 'pepelaf'),
    ('pepelaugh', 'en', 'pepelaugh', 'pepelaugh', 'pepelaugh'),
    ('4head', 'en', 'four head', 'four head', 'four head'),
    ('pepehands', 'en', 'pepe hands', 'pepe hands', 'pepe hands'),
    ('pepehands', 'en', 'pepehands', 'pepehands', 'pepehands'),
    ('feelsbadman', 'en', 'feels bad man', 'feels bad man', 'feels bad man'),
    ('feelsbadman', 'en', 'feelsbad', 'feelsbad', 'feelsbad'),
    ('feelsgoodman', 'en', 'feels good man', 'feels good man', 'feels good man'),
    ('feelsgoodman', 'en', 'feelsgood', 'feelsgood', 'feelsgood'),
    ('biblethump', 'en', 'bible thump', 'bible thump', 'bible thump'),
    ('notlikethis', 'en', 'not like this', 'not like this', 'not like this'),
    ('notlikethis', 'en', 'notlikethis', 'notlikethis', 'notlikethis'),
    ('pogchamp', 'en', 'pogchamp', 'pogchamp', 'pogchamp'),
    ('pogchamp', 'en', 'pog champ', 'pog champ', 'pog champ'),
    ('pogchamp', 'en', 'pogu', 'pogu', 'pogu'),
    ('pogchamp', 'en', 'PogU', 'pogu', 'pogu'),
    ('hypers', 'en', 'hypers', 'hypers', 'hypers'),
    ('hypers', 'en', 'hyper s', 'hyper s', 'hyper s'),
    ('clap', 'en', 'clap', 'clap', 'clap'),
    ('clap', 'en', 'peepoClap', 'peepoclap', 'peepoclap'),
    ('clap', 'en', 'peepo clap', 'peepo clap', 'peepo clap'),
    ('catjam', 'en', 'cat jam', 'cat jam', 'cat jam'),
    ('catjam', 'en', 'catjam', 'catjam', 'catjam'),
    ('peped', 'en', 'pepe d', 'pepe d', 'pepe d'),
    ('peped', 'en', 'peped', 'peped', 'peped'),
    ('widepeepohappy', 'en', 'wide peepo happy', 'wide peepo happy', 'wide peepo happy'),
    ('widepeepohappy', 'en', 'peepoHappy', 'peepohappy', 'peepohappy'),
    ('weirdchamp', 'en', 'weird champ', 'weird champ', 'weird champ'),
    ('weirdchamp', 'en', 'weirdchamp', 'weirdchamp', 'weirdchamp'),
    ('modcheck', 'en', 'mod check', 'mod check', 'mod check'),
    ('modcheck', 'en', 'modcheck', 'modcheck', 'modcheck'),
    ('jebaited', 'en', 'jebaited', 'jebaited', 'jebaited'),
    ('jebaited', 'en', 'jebeited', 'jebeited', 'jebeited'),
    ('residentsleeper', 'en', 'resident sleeper', 'resident sleeper', 'resident sleeper'),
    ('residentsleeper', 'en', 'residentsleeper', 'residentsleeper', 'residentsleeper'),
    ('dansgame', 'en', 'dans game', 'dans game', 'dans game'),
    ('dansgame', 'en', 'dansgame', 'dansgame', 'dansgame'),
    ('pausechamp', 'en', 'pause champ', 'pause champ', 'pause champ'),
    ('pausechamp', 'en', 'pausechamp', 'pausechamp', 'pausechamp'),
    ('5head', 'en', 'five head', 'five head', 'five head'),
    ('5head', 'en', '5 head', '5 head', '5 head'),
    ('5head', 'en', 'galaxy brain', 'galaxy brain', 'galaxy brain'),
    ('aware', 'en', 'unaware', 'unaware', 'unaware'),
    ('aware', 'en', 'aware', 'aware', 'aware'),
    ('chatting', 'en', 'chatting', 'chatting', 'chatting'),
    ('chatting', 'en', 'yapping', 'yapping', 'yapping'),
    ('cinema', 'en', 'cinema', 'cinema', 'cinema'),
    ('cinema', 'en', 'peak cinema', 'peak cinema', 'peak cinema'),
    ('kappa', 'en', 'kappa', 'kappa', 'kappa'),
    ('kappa', 'en', 'kappaHD', 'kappahd', 'kappahd'),
    ('trihard', 'en', 'tri hard', 'tri hard', 'tri hard'),
    ('trihard', 'en', 'trihard', 'trihard', 'trihard'),
    ('lurkar', 'en', 'lurk', 'lurk', 'lurk'),
    ('lurkar', 'en', 'lurker', 'lurker', 'lurker'),
    ('lurkar', 'en', 'lurkando', 'lurkando', 'lurkando'),
    ('primeiro', 'pt-BR', 'first', 'first', 'first'),
    ('primeiro', 'pt-BR', 'primeiro!', 'primeiro', 'primeiro'),
    ('corte', 'pt-BR', 'cortes', 'cortes', 'cortes'),
    ('corte', 'pt-BR', 'canal de cortes', 'canal de cortes', 'canal de cortes')
) AS v(termo_norm, idioma, variacao, norm, colapsado)
JOIN idioma i ON i.codigo = v.idioma
JOIN giria g ON g.termo_normalizado = v.termo_norm AND g.idioma_id = i.id
ON CONFLICT (variacao_normalizada, giria_id) DO NOTHING;

INSERT INTO giria_categoria (giria_id, categoria_id)
SELECT g.id, c.id
FROM (VALUES
    ('omegalul', 'en', 'streaming'),
    ('omegalul', 'en', 'humor'),
    ('lul', 'en', 'streaming'),
    ('lul', 'en', 'humor'),
    ('pepelaugh', 'en', 'streaming'),
    ('pepelaugh', 'en', 'humor'),
    ('4head', 'en', 'streaming'),
    ('4head', 'en', 'humor'),
    ('pepehands', 'en', 'streaming'),
    ('pepehands', 'en', 'emocao'),
    ('feelsbadman', 'en', 'streaming'),
    ('feelsbadman', 'en', 'emocao'),
    ('feelsgoodman', 'en', 'streaming'),
    ('feelsgoodman', 'en', 'emocao'),
    ('biblethump', 'en', 'streaming'),
    ('biblethump', 'en', 'emocao'),
    ('notlikethis', 'en', 'streaming'),
    ('notlikethis', 'en', 'emocao'),
    ('pogchamp', 'en', 'streaming'),
    ('pogchamp', 'en', 'gaming'),
    ('hypers', 'en', 'streaming'),
    ('clap', 'en', 'streaming'),
    ('catjam', 'en', 'streaming'),
    ('catjam', 'en', 'musica'),
    ('peped', 'en', 'streaming'),
    ('peped', 'en', 'musica'),
    ('widepeepohappy', 'en', 'streaming'),
    ('widepeepohappy', 'en', 'emocao'),
    ('weirdchamp', 'en', 'streaming'),
    ('weirdchamp', 'en', 'critica'),
    ('modcheck', 'en', 'streaming'),
    ('modcheck', 'en', 'humor'),
    ('jebaited', 'en', 'streaming'),
    ('jebaited', 'en', 'humor'),
    ('residentsleeper', 'en', 'streaming'),
    ('residentsleeper', 'en', 'critica'),
    ('dansgame', 'en', 'streaming'),
    ('dansgame', 'en', 'critica'),
    ('pausechamp', 'en', 'streaming'),
    ('5head', 'en', 'streaming'),
    ('5head', 'en', 'elogio'),
    ('aware', 'en', 'streaming'),
    ('aware', 'en', 'humor'),
    ('chatting', 'en', 'streaming'),
    ('chatting', 'en', 'critica'),
    ('cinema', 'en', 'streaming'),
    ('cinema', 'en', 'elogio'),
    ('kappa', 'en', 'streaming'),
    ('kappa', 'en', 'humor'),
    ('trihard', 'en', 'streaming'),
    ('lurkar', 'en', 'streaming'),
    ('primeiro', 'pt-BR', 'streaming'),
    ('primeiro', 'pt-BR', 'humor'),
    ('corte', 'pt-BR', 'streaming'),
    ('corte', 'pt-BR', 'redes')
) AS gc(termo_norm, idioma, categoria)
JOIN idioma i ON i.codigo = gc.idioma
JOIN giria g ON g.termo_normalizado = gc.termo_norm AND g.idioma_id = i.id
JOIN categoria c ON c.slug = gc.categoria
ON CONFLICT DO NOTHING;

