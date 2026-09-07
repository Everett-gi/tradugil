-- ---------------------------------------------------------------------------
-- vocabulario do youtube
--
-- 20 verbetes, 20 sentidos.
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
    ('retenção', 'retencao', 'retencao', 'pt-BR', FALSE, FALSE),
    ('monetizar', 'monetizar', 'monetizar', 'pt-BR', FALSE, FALSE),
    ('strike', 'strike', 'strike', 'en', FALSE, FALSE),
    ('collab', 'collab', 'collab', 'en', FALSE, FALSE),
    ('membro', 'membro', 'membro', 'pt-BR', FALSE, FALSE),
    ('super chat', 'super chat', 'super chat', 'en', FALSE, FALSE),
    ('sininho', 'sininho', 'sininho', 'pt-BR', FALSE, FALSE),
    ('inscrito', 'inscrito', 'inscrito', 'pt-BR', FALSE, FALSE),
    ('playlist do canal', 'playlist do canal', 'playlist do canal', 'pt-BR', FALSE, FALSE),
    ('chorar no algoritmo', 'chorar no algoritmo', 'chorar no algoritmo', 'pt-BR', FALSE, FALSE),
    ('canal secundário', 'canal secundario', 'canal secundario', 'pt-BR', FALSE, FALSE),
    ('deixa o like', 'deixa o like', 'deixa o like', 'pt-BR', FALSE, FALSE),
    ('seção de comentários', 'secao de comentarios', 'secao de comentarios', 'pt-BR', FALSE, FALSE),
    ('fixado', 'fixado', 'fixado', 'pt-BR', FALSE, FALSE),
    ('cortes de live', 'cortes de live', 'cortes de live', 'pt-BR', FALSE, FALSE),
    ('minutagem', 'minutagem', 'minutagem', 'pt-BR', FALSE, FALSE),
    ('on fire', 'on fire', 'on fire', 'en', FALSE, FALSE),
    ('gravar do zero', 'gravar do zero', 'gravar do zero', 'pt-BR', FALSE, FALSE),
    ('roteirizar', 'roteirizar', 'roteirizar', 'pt-BR', FALSE, FALSE),
    ('queda de views', 'queda de views', 'queda de views', 'pt-BR', FALSE, FALSE)
) AS t(termo, norm, colapsado, idioma, nsfw, risco)
JOIN idioma i ON i.codigo = t.idioma
ON CONFLICT (termo_normalizado, idioma_id) DO NOTHING;

INSERT INTO definicao (giria_id, explicacao_simples, explicacao_detalhada,
                       equivalente_formal, fonte_id)
SELECT g.id, d.simples, d.detalhada, d.formal,
       (SELECT id FROM fonte WHERE tipo = 'CURADORIA')
FROM (VALUES
    ('retencao', 'pt-BR', 'Quanto tempo as pessoas assistem antes de desistir do vídeo.', 'É o número que mais importa para quem vive de vídeo: retenção baixa faz a plataforma parar de recomendar, mesmo que o vídeo tenha muitos cliques.', NULL),
    ('monetizar', 'pt-BR', 'Ganhar dinheiro com um vídeo, pelos anúncios que aparecem nele.', '"Desmonetizado" é quando a plataforma tira os anúncios, geralmente por causa do assunto do vídeo. Para quem vive disso, equivale a trabalhar de graça.', NULL),
    ('strike', 'en', 'Punição aplicada pela plataforma a um canal.', 'Três strikes derrubam o canal inteiro. Muitos vêm de música de fundo com direitos autorais, e não de conteúdo impróprio.', NULL),
    ('collab', 'en', 'Vídeo feito por dois criadores juntos.', 'Serve para cada um apresentar o próprio público ao do outro.', 'colaboração'),
    ('membro', 'pt-BR', 'Quem paga uma mensalidade a um canal em troca de vantagens.', 'É o equivalente ao "sub" da Twitch, com o mesmo funcionamento.', NULL),
    ('super chat', 'en', 'Mensagem paga que fica destacada no chat de uma transmissão.', 'Quanto mais se paga, mais tempo a mensagem fica fixada. É uma das principais fontes de renda de quem transmite ao vivo.', NULL),
    ('sininho', 'pt-BR', 'O botão que avisa quando o canal publica algo novo.', '"Deixa o like e ativa o sininho" é o pedido mais repetido da plataforma. Sem ele, nem quem é inscrito recebe aviso.', NULL),
    ('inscrito', 'pt-BR', 'Quem segue um canal para acompanhar os vídeos novos.', 'O número de inscritos é a medida pública de tamanho de um canal, embora as visualizações digam mais sobre o alcance real.', NULL),
    ('playlist do canal', 'pt-BR', 'Sequência de vídeos organizada pelo criador para assistir em ordem.', 'Serve para séries e cursos, e ajuda a plataforma a emendar um vídeo no outro.', NULL),
    ('chorar no algoritmo', 'pt-BR', 'Culpar a plataforma quando o vídeo vai mal.', 'Piada corrente entre criadores, porque é impossível provar se a culpa foi mesmo do sistema.', NULL),
    ('canal secundario', 'pt-BR', 'Segundo canal do mesmo criador, com conteúdo mais solto.', 'Costuma ter vídeos menos produzidos, cortes e bastidores.', NULL),
    ('deixa o like', 'pt-BR', 'Pedido para curtir o vídeo.', 'Aparece no começo de quase todo vídeo. A curtida conta como engajamento e ajuda o vídeo a ser recomendado.', NULL),
    ('secao de comentarios', 'pt-BR', 'O espaço abaixo do vídeo onde as pessoas escrevem.', 'Muita gente vai direto para lá antes de assistir, procurando saber se o vídeo vale a pena.', NULL),
    ('fixado', 'pt-BR', 'Comentário que o criador colocou no topo da lista.', 'Serve para correção, aviso ou para destacar um comentário engraçado.', NULL),
    ('cortes de live', 'pt-BR', 'Trechos curtos tirados de uma transmissão longa.', 'No Brasil, é assim que a maior parte do público conhece um streamer: pelos cortes, e não pela live inteira.', NULL),
    ('minutagem', 'pt-BR', 'A marcação de tempo que leva direto a um trecho do vídeo.', 'Nos comentários, aparece como "3:47" e vira um link clicável.', NULL),
    ('on fire', 'en', 'Numa sequência muito boa, acertando tudo.', 'Vale para criador com vários vídeos bem-sucedidos seguidos e para jogador numa boa fase.', NULL),
    ('gravar do zero', 'pt-BR', 'Ter que refazer o vídeo inteiro.', 'Acontece quando o áudio falha ou o assunto perde a validade. É o pesadelo de quem produz.', NULL),
    ('roteirizar', 'pt-BR', 'Escrever antes o que vai ser dito no vídeo.', 'Vídeo que parece improvisado quase sempre é roteirizado; a naturalidade é o efeito, não a causa.', NULL),
    ('queda de views', 'pt-BR', 'Quando o número de visualizações despenca.', '"Views" é visualizações. Uma queda súbita costuma significar que a plataforma parou de recomendar o canal.', NULL)
) AS d(norm, idioma, simples, detalhada, formal)
JOIN idioma i ON i.codigo = d.idioma
JOIN giria g ON g.termo_normalizado = d.norm AND g.idioma_id = i.id;

INSERT INTO giria_variacao (giria_id, variacao, variacao_normalizada,
                            variacao_colapsada)
SELECT g.id, v.variacao, v.norm, v.colapsado
FROM (VALUES
    ('retencao', 'pt-BR', 'retencao', 'retencao', 'retencao'),
    ('retencao', 'pt-BR', 'retenção de audiência', 'retencao de audiencia', 'retencao de audiencia'),
    ('monetizar', 'pt-BR', 'monetizado', 'monetizado', 'monetizado'),
    ('monetizar', 'pt-BR', 'monetização', 'monetizacao', 'monetizacao'),
    ('monetizar', 'pt-BR', 'desmonetizar', 'desmonetizar', 'desmonetizar'),
    ('strike', 'en', 'strikes', 'strikes', 'strikes'),
    ('strike', 'en', 'tomou strike', 'tomou strike', 'tomou strike'),
    ('collab', 'en', 'colab', 'colab', 'colab'),
    ('collab', 'en', 'collabs', 'collabs', 'collabs'),
    ('collab', 'en', 'colaboração', 'colaboracao', 'colaboracao'),
    ('membro', 'pt-BR', 'membresia', 'membresia', 'membresia'),
    ('membro', 'pt-BR', 'virar membro', 'virar membro', 'virar membro'),
    ('super chat', 'en', 'superchat', 'superchat', 'superchat'),
    ('super chat', 'en', 'super', 'super', 'super'),
    ('sininho', 'pt-BR', 'ativar o sininho', 'ativar o sininho', 'ativar o sininho'),
    ('sininho', 'pt-BR', 'sino', 'sino', 'sino'),
    ('inscrito', 'pt-BR', 'inscritos', 'inscritos', 'inscritos'),
    ('inscrito', 'pt-BR', 'se inscrever', 'se inscrever', 'se inscrever'),
    ('inscrito', 'pt-BR', 'inscreva-se', 'inscreva se', 'inscreva se'),
    ('playlist do canal', 'pt-BR', 'playlist de videos', 'playlist de videos', 'playlist de videos'),
    ('chorar no algoritmo', 'pt-BR', 'reclamar do algoritmo', 'reclamar do algoritmo', 'reclamar do algoritmo'),
    ('canal secundario', 'pt-BR', 'canal secundario', 'canal secundario', 'canal secundario'),
    ('canal secundario', 'pt-BR', 'canal 2', 'canal 2', 'canal 2'),
    ('deixa o like', 'pt-BR', 'deixe seu like', 'deixe seu like', 'deixe seu like'),
    ('deixa o like', 'pt-BR', 'manda aquele like', 'manda aquele like', 'manda aquele like'),
    ('secao de comentarios', 'pt-BR', 'secao de comentarios', 'secao de comentarios', 'secao de comentarios'),
    ('secao de comentarios', 'pt-BR', 'comentários', 'comentarios', 'comentarios'),
    ('secao de comentarios', 'pt-BR', 'coments', 'coments', 'coments'),
    ('fixado', 'pt-BR', 'comentário fixado', 'comentario fixado', 'comentario fixado'),
    ('fixado', 'pt-BR', 'pinado', 'pinado', 'pinado'),
    ('cortes de live', 'pt-BR', 'canal de corte', 'canal de corte', 'canal de corte'),
    ('cortes de live', 'pt-BR', 'cortou a live', 'cortou a live', 'cortou a live'),
    ('minutagem', 'pt-BR', 'timestamp', 'timestamp', 'timestamp'),
    ('minutagem', 'pt-BR', 'marcação de tempo', 'marcacao de tempo', 'marcacao de tempo'),
    ('on fire', 'en', 'tá on fire', 'ta on fire', 'ta on fire'),
    ('on fire', 'en', 'em chamas', 'em chamas', 'em chamas'),
    ('gravar do zero', 'pt-BR', 'regravar tudo', 'regravar tudo', 'regravar tudo'),
    ('roteirizar', 'pt-BR', 'roteiro', 'roteiro', 'roteiro'),
    ('roteirizar', 'pt-BR', 'roteirizado', 'roteirizado', 'roteirizado'),
    ('queda de views', 'pt-BR', 'caiu as views', 'caiu as views', 'caiu as views'),
    ('queda de views', 'pt-BR', 'views', 'views', 'views')
) AS v(termo_norm, idioma, variacao, norm, colapsado)
JOIN idioma i ON i.codigo = v.idioma
JOIN giria g ON g.termo_normalizado = v.termo_norm AND g.idioma_id = i.id
ON CONFLICT (variacao_normalizada, giria_id) DO NOTHING;

INSERT INTO giria_categoria (giria_id, categoria_id)
SELECT g.id, c.id
FROM (VALUES
    ('retencao', 'pt-BR', 'redes'),
    ('retencao', 'pt-BR', 'streaming'),
    ('monetizar', 'pt-BR', 'redes'),
    ('monetizar', 'pt-BR', 'trabalho'),
    ('strike', 'en', 'redes'),
    ('strike', 'en', 'trabalho'),
    ('collab', 'en', 'redes'),
    ('collab', 'en', 'trabalho'),
    ('membro', 'pt-BR', 'redes'),
    ('membro', 'pt-BR', 'streaming'),
    ('super chat', 'en', 'redes'),
    ('super chat', 'en', 'streaming'),
    ('sininho', 'pt-BR', 'redes'),
    ('inscrito', 'pt-BR', 'redes'),
    ('playlist do canal', 'pt-BR', 'redes'),
    ('chorar no algoritmo', 'pt-BR', 'redes'),
    ('chorar no algoritmo', 'pt-BR', 'humor'),
    ('canal secundario', 'pt-BR', 'redes'),
    ('deixa o like', 'pt-BR', 'redes'),
    ('secao de comentarios', 'pt-BR', 'redes'),
    ('fixado', 'pt-BR', 'redes'),
    ('cortes de live', 'pt-BR', 'redes'),
    ('cortes de live', 'pt-BR', 'streaming'),
    ('minutagem', 'pt-BR', 'redes'),
    ('on fire', 'en', 'redes'),
    ('on fire', 'en', 'elogio'),
    ('gravar do zero', 'pt-BR', 'redes'),
    ('gravar do zero', 'pt-BR', 'trabalho'),
    ('roteirizar', 'pt-BR', 'redes'),
    ('roteirizar', 'pt-BR', 'trabalho'),
    ('queda de views', 'pt-BR', 'redes'),
    ('queda de views', 'pt-BR', 'trabalho')
) AS gc(termo_norm, idioma, categoria)
JOIN idioma i ON i.codigo = gc.idioma
JOIN giria g ON g.termo_normalizado = gc.termo_norm AND g.idioma_id = i.id
JOIN categoria c ON c.slug = gc.categoria
ON CONFLICT DO NOTHING;

