-- ---------------------------------------------------------------------------
-- Fecha um furo na busca por enfase repetida.
--
-- O cliente colapsa "kkkkkkk" para "kk" antes de procurar, mas o verbete
-- guardado e "kkk" -- os dois nunca se encontravam. Colapsar so um dos lados
-- nao resolve: e preciso comparar forma colapsada com forma colapsada.
--
-- "kkkkkkkkk" e onipresente em conversa brasileira, entao isto nao e caso
-- de borda: e o termo mais frequente que o produto precisa acertar.
--
-- ATENCAO: a expressao abaixo duplica em SQL a regra de
-- Normalizador.colapsarRepeticoes. A duplicacao e proposital -- o seed e
-- SQL e precisa preencher a coluna -- mas nao e livre: ConsistenciaDoSeedIT
-- verifica linha a linha que o valor gravado bate com o que o Java calcula.
-- Se as duas regras divergirem, a CI quebra.
-- ---------------------------------------------------------------------------

ALTER TABLE giria ADD COLUMN termo_colapsado VARCHAR(80);

UPDATE giria
   SET termo_colapsado = regexp_replace(termo_normalizado, '(.)\1{2,}', '\1\1', 'g');

ALTER TABLE giria ALTER COLUMN termo_colapsado SET NOT NULL;

CREATE INDEX idx_giria_colapsado ON giria (termo_colapsado);

ALTER TABLE giria_variacao ADD COLUMN variacao_colapsada VARCHAR(80);

UPDATE giria_variacao
   SET variacao_colapsada = regexp_replace(variacao_normalizada, '(.)\1{2,}', '\1\1', 'g');

ALTER TABLE giria_variacao ALTER COLUMN variacao_colapsada SET NOT NULL;

CREATE INDEX idx_variacao_colapsada ON giria_variacao (variacao_colapsada);
