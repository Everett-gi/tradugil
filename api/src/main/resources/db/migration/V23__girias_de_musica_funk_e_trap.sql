-- ---------------------------------------------------------------------------
-- girias de musica funk e trap
--
-- 31 verbetes, 31 sentidos.
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
    ('beat', 'beat', 'beat', 'en', FALSE, FALSE),
    ('montagem', 'montagem', 'montagem', 'pt-BR', FALSE, FALSE),
    ('bregafunk', 'bregafunk', 'bregafunk', 'pt-BR', FALSE, FALSE),
    ('paredão', 'paredao', 'paredao', 'pt-BR', FALSE, FALSE),
    ('puxar o hino', 'puxar o hino', 'puxar o hino', 'pt-BR', FALSE, FALSE),
    ('hino', 'hino', 'hino', 'pt-BR', FALSE, FALSE),
    ('chiclete', 'chiclete', 'chiclete', 'pt-BR', FALSE, FALSE),
    ('álbum', 'album', 'album', 'pt-BR', FALSE, FALSE),
    ('single', 'single', 'single', 'en', FALSE, FALSE),
    ('estourar', 'estourar', 'estourar', 'pt-BR', FALSE, FALSE),
    ('cantar em playback', 'cantar em playback', 'cantar em playback', 'pt-BR', FALSE, FALSE),
    ('afinado', 'afinado', 'afinado', 'pt-BR', FALSE, FALSE),
    ('arrocha', 'arrocha', 'arrocha', 'pt-BR', FALSE, FALSE),
    ('piseiro', 'piseiro', 'piseiro', 'pt-BR', FALSE, FALSE),
    ('bis', 'bis', 'bis', 'pt-BR', FALSE, FALSE),
    ('abrir o show', 'abrir o show', 'abrir o show', 'pt-BR', FALSE, FALSE),
    ('camarote', 'camarote', 'camarote', 'pt-BR', FALSE, FALSE),
    ('pista', 'pista', 'pista', 'pt-BR', FALSE, FALSE),
    ('line-up', 'line up', 'line up', 'en', FALSE, FALSE),
    ('rolê musical', 'role musical', 'role musical', 'pt-BR', FALSE, FALSE),
    ('sample', 'sample', 'sample', 'en', FALSE, FALSE),
    ('vinheta', 'vinheta', 'vinheta', 'pt-BR', FALSE, FALSE),
    ('trap', 'trap', 'trap', 'en', FALSE, FALSE),
    ('drill', 'drill', 'drill', 'en', FALSE, FALSE),
    ('quebrada canta', 'quebrada canta', 'quebrada canta', 'pt-BR', FALSE, FALSE),
    ('acústico', 'acustico', 'acustico', 'pt-BR', FALSE, FALSE),
    ('puxar o coro', 'puxar o coro', 'puxar o coro', 'pt-BR', FALSE, FALSE),
    ('tocar em rádio', 'tocar em radio', 'tocar em radio', 'pt-BR', FALSE, FALSE),
    ('descobrir na fyp', 'descobrir na fyp', 'descobrir na fyp', 'pt-BR', FALSE, FALSE),
    ('som na caixa', 'som na caixa', 'som na caixa', 'pt-BR', FALSE, FALSE),
    ('bailão', 'bailao', 'bailao', 'pt-BR', FALSE, FALSE)
) AS t(termo, norm, colapsado, idioma, nsfw, risco)
JOIN idioma i ON i.codigo = t.idioma
ON CONFLICT (termo_normalizado, idioma_id) DO NOTHING;

INSERT INTO definicao (giria_id, explicacao_simples, explicacao_detalhada,
                       equivalente_formal, fonte_id)
SELECT g.id, d.simples, d.detalhada, d.formal,
       (SELECT id FROM fonte WHERE tipo = 'CURADORIA')
FROM (VALUES
    ('beat', 'en', 'A base instrumental de uma música, sobre a qual se canta.', '"Esse beat tá pesado" elogia a batida. Quem produz o beat é o beatmaker.', 'batida'),
    ('montagem', 'pt-BR', 'Estilo de funk feito colando pedaços curtos de vários sons.', 'Nasceu no Rio e explodiu nas redes por volta de 2022. Costuma ser instrumental, rápida e muito repetitiva de propósito.', NULL),
    ('bregafunk', 'pt-BR', 'Mistura de brega com funk, nascida em Pernambuco.', 'Tem passo de dança próprio e batida mais lenta que o funk carioca.', NULL),
    ('paredao', 'pt-BR', 'Muro de caixas de som usado em festas de rua, sobretudo no Nordeste.', '"Vai ter paredão" anuncia festa com som muito alto. Também é o nome do bloco de eliminação num programa de TV, e o contexto separa os dois.', NULL),
    ('puxar o hino', 'pt-BR', 'Começar a cantar uma música que todo mundo sabe.', '"Hino" aqui é a música famosa demais, não o hino nacional.', NULL),
    ('hino', 'pt-BR', 'Música muito boa, que todo mundo canta junto.', '"Essa é um hino" é o elogio máximo a uma faixa. Não tem relação com hino nacional.', NULL),
    ('chiclete', 'pt-BR', 'Música que gruda na cabeça e não sai.', 'Nem sempre é elogio: costuma descrever algo repetitivo e simples que a pessoa se pega cantando sem querer.', NULL),
    ('album', 'pt-BR', 'Conjunto de músicas lançadas juntas pelo mesmo artista.', '"Disco" continua sendo usado mesmo sem existir disco físico nenhum.', NULL),
    ('single', 'en', 'Música lançada sozinha, fora de um álbum.', 'Serve para testar a reação do público antes do disco completo.', 'faixa avulsa'),
    ('estourar', 'pt-BR', 'Ficar famoso de repente.', '"A música estourou" quer dizer que passou a tocar em todo lugar em pouco tempo.', NULL),
    ('cantar em playback', 'pt-BR', 'Fingir que canta enquanto a gravação toca.', 'É acusação em show ao vivo, e prática normal em programa de TV, onde o som ao vivo daria problema.', NULL),
    ('afinado', 'pt-BR', 'Que canta nas notas certas.', '"Desafinado" é o contrário, e é a crítica mais comum a quem canta em karaokê.', NULL),
    ('arrocha', 'pt-BR', 'Estilo musical baiano, romântico e dançado bem juntinho.', 'Surgiu nos anos 2000 na Bahia e se espalhou pelo Nordeste.', NULL),
    ('piseiro', 'pt-BR', 'Estilo de forró eletrônico do Nordeste, muito popular nas redes.', 'Nome vem de "pisar" no ritmo. Estourou nacionalmente por volta de 2020.', NULL),
    ('bis', 'pt-BR', 'Quando o público pede que a música seja repetida.', 'No fim do show, a plateia grita para o artista voltar e tocar mais.', NULL),
    ('abrir o show', 'pt-BR', 'Tocar antes do artista principal.', '"Quem abre" costuma ser um nome menor, e é uma vitrine importante para quem está começando.', NULL),
    ('camarote', 'pt-BR', 'Área separada e mais cara de um show ou de um bloco.', 'Tem estrutura melhor e vista privilegiada. "Ficar na pista" é o contrário.', NULL),
    ('pista', 'pt-BR', 'A parte comum do show, onde o público fica em pé.', 'É o ingresso mais barato e onde a festa costuma ser mais animada.', NULL),
    ('line up', 'en', 'A lista de artistas que vão tocar num festival.', 'Sai antes da venda dos ingressos e é o que decide se as pessoas vão comprar.', 'programação'),
    ('role musical', 'pt-BR', 'O gosto ou o estilo musical de alguém.', '"Não é muito o meu rolê" é um jeito educado de dizer que não gosta daquele estilo.', NULL),
    ('sample', 'en', 'Pedaço de uma música antiga reaproveitado numa nova.', '"Samplear" é usar esse pedaço. É a base de boa parte do rap e do funk.', NULL),
    ('vinheta', 'pt-BR', 'Trecho curto de som que identifica alguém ou alguma coisa.', 'Nas redes, a vinheta repetida de um criador vira marca reconhecível em dois segundos.', NULL),
    ('trap', 'en', 'Estilo derivado do rap, com batida marcada e voz processada.', 'Dominou o rap brasileiro a partir de 2017 e trouxe junto boa parte da gíria nova dos adolescentes.', NULL),
    ('drill', 'en', 'Subgênero do rap, mais sombrio e com letras duras.', 'Nasceu em Chicago e ganhou versão brasileira. A batida é mais lenta e pesada que a do trap.', NULL),
    ('quebrada canta', 'pt-BR', 'Música feita na periferia, sobre a vida na periferia.', 'Expressão usada com orgulho por quem produz fora do circuito das grandes gravadoras.', NULL),
    ('acustico', 'pt-BR', 'Versão da música tocada só com instrumentos, sem produção eletrônica.', 'Costuma ser mais lenta e mais intimista que a original.', NULL),
    ('puxar o coro', 'pt-BR', 'Começar um canto para que os outros acompanhem.', 'Vale em show e em estádio de futebol, que é onde a expressão mais aparece.', NULL),
    ('tocar em radio', 'pt-BR', 'Ter a música incluída na programação das rádios.', 'Continua sendo um marco de sucesso, mesmo com o streaming, porque alcança quem não usa aplicativo de música.', NULL),
    ('descobrir na fyp', 'pt-BR', 'Encontrar uma música pela página de recomendações de um aplicativo.', 'Hoje é o caminho mais comum para uma música desconhecida virar sucesso.', NULL),
    ('som na caixa', 'pt-BR', 'Ligar a música num volume que todo mundo escute.', '"Bota o som na caixa" é o pedido para começar a festa.', NULL),
    ('bailao', 'pt-BR', 'Festa grande com muita dança.', '"Baile" sozinho, em contexto de funk, é o baile funk, que é evento de rua e não salão.', NULL)
) AS d(norm, idioma, simples, detalhada, formal)
JOIN idioma i ON i.codigo = d.idioma
JOIN giria g ON g.termo_normalizado = d.norm AND g.idioma_id = i.id;

INSERT INTO giria_variacao (giria_id, variacao, variacao_normalizada,
                            variacao_colapsada)
SELECT g.id, v.variacao, v.norm, v.colapsado
FROM (VALUES
    ('beat', 'en', 'beatzinho', 'beatzinho', 'beatzinho'),
    ('beat', 'en', 'bit', 'bit', 'bit'),
    ('montagem', 'pt-BR', 'montagens', 'montagens', 'montagens'),
    ('bregafunk', 'pt-BR', 'brega funk', 'brega funk', 'brega funk'),
    ('bregafunk', 'pt-BR', 'brega-funk', 'brega funk', 'brega funk'),
    ('paredao', 'pt-BR', 'paredao', 'paredao', 'paredao'),
    ('paredao', 'pt-BR', 'paredões', 'paredoes', 'paredoes'),
    ('puxar o hino', 'pt-BR', 'puxou o hino', 'puxou o hino', 'puxou o hino'),
    ('hino', 'pt-BR', 'hinão', 'hinao', 'hinao'),
    ('hino', 'pt-BR', 'hinao', 'hinao', 'hinao'),
    ('chiclete', 'pt-BR', 'musica chiclete', 'musica chiclete', 'musica chiclete'),
    ('album', 'pt-BR', 'album', 'album', 'album'),
    ('album', 'pt-BR', 'disco', 'disco', 'disco'),
    ('album', 'pt-BR', 'lp', 'lp', 'lp'),
    ('single', 'en', 'singles', 'singles', 'singles'),
    ('estourar', 'pt-BR', 'estourou', 'estourou', 'estourou'),
    ('estourar', 'pt-BR', 'estourando', 'estourando', 'estourando'),
    ('cantar em playback', 'pt-BR', 'playback', 'playback', 'playback'),
    ('cantar em playback', 'pt-BR', 'dublar', 'dublar', 'dublar'),
    ('afinado', 'pt-BR', 'afinada', 'afinada', 'afinada'),
    ('afinado', 'pt-BR', 'desafinado', 'desafinado', 'desafinado'),
    ('arrocha', 'pt-BR', 'arrochar', 'arrochar', 'arrochar'),
    ('piseiro', 'pt-BR', 'pisadinha', 'pisadinha', 'pisadinha'),
    ('piseiro', 'pt-BR', 'pise', 'pise', 'pise'),
    ('bis', 'pt-BR', 'pedir bis', 'pedir bis', 'pedir bis'),
    ('abrir o show', 'pt-BR', 'abertura do show', 'abertura do show', 'abertura do show'),
    ('camarote', 'pt-BR', 'camarotes', 'camarotes', 'camarotes'),
    ('pista', 'pt-BR', 'área da pista', 'area da pista', 'area da pista'),
    ('line up', 'en', 'lineup', 'lineup', 'lineup'),
    ('line up', 'en', 'line up', 'line up', 'line up'),
    ('role musical', 'pt-BR', 'role musical', 'role musical', 'role musical'),
    ('sample', 'en', 'samplear', 'samplear', 'samplear'),
    ('sample', 'en', 'sampler', 'sampler', 'sampler'),
    ('vinheta', 'pt-BR', 'vinhetas', 'vinhetas', 'vinhetas'),
    ('trap', 'en', 'trapzinho', 'trapzinho', 'trapzinho'),
    ('quebrada canta', 'pt-BR', 'som da quebrada', 'som da quebrada', 'som da quebrada'),
    ('acustico', 'pt-BR', 'acustico', 'acustico', 'acustico'),
    ('acustico', 'pt-BR', 'versão acústica', 'versao acustica', 'versao acustica'),
    ('puxar o coro', 'pt-BR', 'puxou o coro', 'puxou o coro', 'puxou o coro'),
    ('tocar em radio', 'pt-BR', 'tocar na radio', 'tocar na radio', 'tocar na radio'),
    ('tocar em radio', 'pt-BR', 'rodar na rádio', 'rodar na radio', 'rodar na radio'),
    ('descobrir na fyp', 'pt-BR', 'achei na fyp', 'achei na fyp', 'achei na fyp'),
    ('som na caixa', 'pt-BR', 'botar o som', 'botar o som', 'botar o som'),
    ('bailao', 'pt-BR', 'bailao', 'bailao', 'bailao'),
    ('bailao', 'pt-BR', 'baile', 'baile', 'baile')
) AS v(termo_norm, idioma, variacao, norm, colapsado)
JOIN idioma i ON i.codigo = v.idioma
JOIN giria g ON g.termo_normalizado = v.termo_norm AND g.idioma_id = i.id
ON CONFLICT (variacao_normalizada, giria_id) DO NOTHING;

INSERT INTO giria_categoria (giria_id, categoria_id)
SELECT g.id, c.id
FROM (VALUES
    ('beat', 'en', 'musica'),
    ('montagem', 'pt-BR', 'musica'),
    ('bregafunk', 'pt-BR', 'musica'),
    ('bregafunk', 'pt-BR', 'regional'),
    ('paredao', 'pt-BR', 'musica'),
    ('paredao', 'pt-BR', 'regional'),
    ('puxar o hino', 'pt-BR', 'musica'),
    ('puxar o hino', 'pt-BR', 'humor'),
    ('hino', 'pt-BR', 'musica'),
    ('hino', 'pt-BR', 'elogio'),
    ('chiclete', 'pt-BR', 'musica'),
    ('album', 'pt-BR', 'musica'),
    ('single', 'en', 'musica'),
    ('estourar', 'pt-BR', 'musica'),
    ('estourar', 'pt-BR', 'redes'),
    ('cantar em playback', 'pt-BR', 'musica'),
    ('cantar em playback', 'pt-BR', 'critica'),
    ('afinado', 'pt-BR', 'musica'),
    ('afinado', 'pt-BR', 'elogio'),
    ('arrocha', 'pt-BR', 'musica'),
    ('arrocha', 'pt-BR', 'regional'),
    ('piseiro', 'pt-BR', 'musica'),
    ('piseiro', 'pt-BR', 'regional'),
    ('bis', 'pt-BR', 'musica'),
    ('abrir o show', 'pt-BR', 'musica'),
    ('camarote', 'pt-BR', 'musica'),
    ('pista', 'pt-BR', 'musica'),
    ('line up', 'en', 'musica'),
    ('role musical', 'pt-BR', 'musica'),
    ('sample', 'en', 'musica'),
    ('vinheta', 'pt-BR', 'musica'),
    ('vinheta', 'pt-BR', 'redes'),
    ('trap', 'en', 'musica'),
    ('drill', 'en', 'musica'),
    ('quebrada canta', 'pt-BR', 'musica'),
    ('quebrada canta', 'pt-BR', 'rua'),
    ('acustico', 'pt-BR', 'musica'),
    ('puxar o coro', 'pt-BR', 'musica'),
    ('puxar o coro', 'pt-BR', 'esporte'),
    ('tocar em radio', 'pt-BR', 'musica'),
    ('descobrir na fyp', 'pt-BR', 'musica'),
    ('descobrir na fyp', 'pt-BR', 'redes'),
    ('som na caixa', 'pt-BR', 'musica'),
    ('bailao', 'pt-BR', 'musica')
) AS gc(termo_norm, idioma, categoria)
JOIN idioma i ON i.codigo = gc.idioma
JOIN giria g ON g.termo_normalizado = gc.termo_norm AND g.idioma_id = i.id
JOIN categoria c ON c.slug = gc.categoria
ON CONFLICT DO NOTHING;

