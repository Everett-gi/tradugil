-- ---------------------------------------------------------------------------
-- Junta os dois verbetes "feed" num so, com dois sentidos.
--
-- O PROBLEMA
--
-- Existiam duas linhas: "feed" em ingles, do lote de jogos (morrer demais e
-- fortalecer o adversario), e "feed" em portugues, do lote de internet (a
-- sequencia de publicacoes). Sao duas linhas legitimas para o banco, porque a
-- chave unica e (termo_normalizado, idioma_id).
--
-- Mas o /traduzir NAO filtra por idioma: ele resolvia as duas e descartava a
-- segunda por ocupar a mesma posicao no texto. Uma das duas explicacoes ficava
-- inalcancavel, e qual delas dependia da ordem em que o banco devolveu as
-- linhas. Conferido na API: so o sentido de rede social respondia.
--
-- O verbete e o mesmo; o que muda com o contexto e o sentido. E exatamente
-- isso que o esquema de sentidos multiplos existe para representar, e e como
-- "dropar", "bug" e "gostoso" ja estao no dicionario.
--
-- A ORDEM DAS OPERACOES IMPORTA
--
-- Tudo e movido para a linha em portugues ANTES de a linha em ingles ser
-- apagada, para nao existir instante em que a informacao esteja fora do banco.
-- O DELETE no fim leva junto as variacoes e categorias que sobraram, por
-- ON DELETE CASCADE.
-- ---------------------------------------------------------------------------

-- 1. O sentido de jogo passa a ser o segundo sentido do verbete em portugues.
INSERT INTO definicao (giria_id, explicacao_simples, explicacao_detalhada,
                       equivalente_formal, fonte_id, status)
SELECT destino.id, d.explicacao_simples, d.explicacao_detalhada,
       d.equivalente_formal, d.fonte_id, d.status
FROM definicao d
JOIN giria origem  ON origem.id = d.giria_id
JOIN idioma io     ON io.id = origem.idioma_id AND io.codigo = 'en'
JOIN giria destino ON destino.termo_normalizado = 'feed'
JOIN idioma id2    ON id2.id = destino.idioma_id AND id2.codigo = 'pt-BR'
WHERE origem.termo_normalizado = 'feed';

-- 2. As variacoes ("feedar", "feedando") acompanham.
INSERT INTO giria_variacao (giria_id, variacao, variacao_normalizada,
                            variacao_colapsada)
SELECT destino.id, v.variacao, v.variacao_normalizada, v.variacao_colapsada
FROM giria_variacao v
JOIN giria origem  ON origem.id = v.giria_id
JOIN idioma io     ON io.id = origem.idioma_id AND io.codigo = 'en'
JOIN giria destino ON destino.termo_normalizado = 'feed'
JOIN idioma id2    ON id2.id = destino.idioma_id AND id2.codigo = 'pt-BR'
WHERE origem.termo_normalizado = 'feed'
ON CONFLICT (variacao_normalizada, giria_id) DO NOTHING;

-- 3. A categoria de jogos tambem, para o verbete continuar aparecendo na
--    prateleira certa do catalogo.
INSERT INTO giria_categoria (giria_id, categoria_id)
SELECT destino.id, gc.categoria_id
FROM giria_categoria gc
JOIN giria origem  ON origem.id = gc.giria_id
JOIN idioma io     ON io.id = origem.idioma_id AND io.codigo = 'en'
JOIN giria destino ON destino.termo_normalizado = 'feed'
JOIN idioma id2    ON id2.id = destino.idioma_id AND id2.codigo = 'pt-BR'
WHERE origem.termo_normalizado = 'feed'
ON CONFLICT DO NOTHING;

-- 4. Só agora a linha em ingles sai.
DELETE FROM giria g
USING idioma i
WHERE i.id = g.idioma_id
  AND g.termo_normalizado = 'feed'
  AND i.codigo = 'en';
