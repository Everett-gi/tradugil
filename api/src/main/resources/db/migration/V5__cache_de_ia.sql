-- ---------------------------------------------------------------------------
-- Cache persistente das respostas da IA (secao 7.3 do documento).
--
-- Tres mecanismos mantem o custo de IA perto de zero, e este e o principal:
-- a mesma pergunta nunca e paga duas vezes. Uma resposta aprovada pela
-- curadoria vira verbete e sai da IA de vez.
--
-- PRIVACIDADE: a chave guarda o hash do contexto, nunca o contexto.
--
-- O contexto e o trecho ao redor do termo -- lido da tela do usuario, pode
-- ser um pedaco de conversa privada. Guardar o texto para poder reusar a
-- resposta transformaria o cache em um arquivo de conversas alheias, que e
-- exatamente o que a secao 8 proibe. O hash cumpre a unica funcao de que o
-- cache precisa (saber se e a mesma pergunta) sem guardar a pergunta.
-- ---------------------------------------------------------------------------

CREATE TABLE resposta_ia (
    id                   BIGSERIAL PRIMARY KEY,
    termo_normalizado    VARCHAR(80) NOT NULL,
    -- SHA-256 do contexto em hexadecimal. Contexto ausente tem hash proprio,
    -- para "termo sem contexto" e "termo com contexto" nao se confundirem.
    hash_do_contexto     CHAR(64)    NOT NULL,
    e_giria              BOOLEAN     NOT NULL,
    explicacao_simples   TEXT,
    explicacao_detalhada TEXT,
    equivalente_formal   VARCHAR(160),
    nsfw                 BOOLEAN     NOT NULL DEFAULT FALSE,
    risco_menor          BOOLEAN     NOT NULL DEFAULT FALSE,
    confianca            REAL        NOT NULL,
    -- Marcado quando a curadoria promove esta resposta a verbete oficial.
    -- A partir dai o termo resolve no nivel 2 e nunca mais custa nada.
    promovida            BOOLEAN     NOT NULL DEFAULT FALSE,
    criado_em            TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (termo_normalizado, hash_do_contexto)
);

-- Fila de revisao da curadoria: o que a IA respondeu com confianca alta e
-- ainda ninguem promoveu a verbete.
CREATE INDEX idx_resposta_ia_para_curadoria
    ON resposta_ia (confianca DESC, criado_em)
    WHERE promovida = FALSE AND e_giria = TRUE;
