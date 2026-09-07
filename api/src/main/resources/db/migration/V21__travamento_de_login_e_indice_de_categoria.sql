-- ---------------------------------------------------------------------------
-- Duas coisas sem relacao entre si, numa migracao so porque ambas sao
-- alteracoes pequenas de esquema aplicadas no mesmo trabalho.
--
-- 1. TRAVAMENTO DE LOGIN POR CONTA
--
-- O limite por IP na borda (Caddy) nao resolve forca bruta distribuida: mil
-- IPs tentando dez senhas cada passam folgados por um teto de 60 por minuto
-- por IP, e somam dez mil tentativas contra a mesma conta.
--
-- O contador precisa ficar no banco, e nao em memoria, por dois motivos.
-- Em memoria ele zera a cada reinicializacao, e reiniciar a aplicacao e algo
-- que qualquer um pode provocar. E quando houver mais de uma instancia, cada
-- uma teria o proprio contador, e o atacante ganharia uma cota nova por
-- instancia so alternando conexoes.
--
-- 2. INDICE PARA O CATALOGO
--
-- A chave primaria de giria_categoria e (giria_id, categoria_id), o que serve
-- para "quais categorias esta giria tem" mas nao para "quais girias estao
-- nesta categoria", que e exatamente a pergunta do catalogo. Sem um indice
-- comecando por categoria_id, abrir uma prateleira varre a tabela de ligacao
-- inteira.
-- ---------------------------------------------------------------------------

ALTER TABLE usuario
    ADD COLUMN tentativas_falhas SMALLINT NOT NULL DEFAULT 0,
    ADD COLUMN bloqueado_ate     TIMESTAMPTZ;

-- O hash cresce: passa a carregar o identificador do algoritmo como prefixo
-- ("{pimenta}$2a$12$..."), para que uma troca futura de algoritmo consiga
-- conviver com os hashes antigos em vez de exigir que todo mundo redefina a
-- senha. 100 caracteres ja bastariam; 120 deixa folga para o proximo.
ALTER TABLE usuario
    ALTER COLUMN senha_hash TYPE VARCHAR(120);

COMMENT ON COLUMN usuario.tentativas_falhas IS
    'Tentativas de login malsucedidas seguidas. Zera no primeiro acerto.';
COMMENT ON COLUMN usuario.bloqueado_ate IS
    'Ate quando a conta recusa login. Nulo quando nao ha travamento em curso.';

CREATE INDEX idx_giria_categoria_por_categoria
    ON giria_categoria (categoria_id, giria_id);
