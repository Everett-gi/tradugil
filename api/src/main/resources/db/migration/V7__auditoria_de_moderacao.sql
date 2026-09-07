-- ---------------------------------------------------------------------------
-- Trilha de auditoria das acoes de moderacao.
--
-- E a mitigacao de Repudiation do threat model (secao 8.3): "moderador nega
-- ter aprovado conteudo improprio".
--
-- A coluna moderador_id em contribuicao nao resolve isso: ela guarda o
-- estado ATUAL, e estado atual pode ser sobrescrito. Se um moderador aprova
-- algo improprio e depois rejeita para encobrir, a linha de contribuicao
-- passa a dizer apenas "rejeitada" -- a aprovacao desaparece sem deixar
-- rastro. Trilha de auditoria e o registro do que ACONTECEU, nao do que vale
-- agora, e por isso e append-only.
--
-- A imutabilidade nao e so convencao: o gatilho abaixo recusa UPDATE e
-- DELETE. Um moderador com acesso a aplicacao nao consegue apagar o proprio
-- rastro pelo caminho normal.
-- ---------------------------------------------------------------------------

CREATE TABLE auditoria_de_moderacao (
    id              BIGSERIAL PRIMARY KEY,
    contribuicao_id BIGINT      NOT NULL REFERENCES contribuicao(id),
    -- Sem ON DELETE CASCADE de proposito: apagar a conta do moderador nao
    -- pode apagar o registro do que ele fez.
    moderador_id    BIGINT      NOT NULL REFERENCES usuario(id),
    acao            VARCHAR(15) NOT NULL,
    motivo          VARCHAR(200),
    ocorrido_em     TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT ck_auditoria_acao CHECK (acao IN ('APROVADA', 'REJEITADA'))
);

CREATE INDEX idx_auditoria_contribuicao ON auditoria_de_moderacao (contribuicao_id);
CREATE INDEX idx_auditoria_moderador ON auditoria_de_moderacao (moderador_id, ocorrido_em DESC);

CREATE OR REPLACE FUNCTION recusar_alteracao_de_auditoria()
RETURNS TRIGGER AS $$
BEGIN
    RAISE EXCEPTION
        'auditoria_de_moderacao e append-only: % nao e permitido', TG_OP;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_auditoria_imutavel
    BEFORE UPDATE OR DELETE ON auditoria_de_moderacao
    FOR EACH ROW EXECUTE FUNCTION recusar_alteracao_de_auditoria();
