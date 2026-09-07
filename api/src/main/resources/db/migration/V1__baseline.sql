-- ---------------------------------------------------------------------------
-- Tradugil - esquema inicial.
--
-- Duas decisoes estruturais atravessam este arquivo:
--
-- 1) Termo e definicao sao tabelas separadas. Uma mesma giria tem varios
--    sentidos, registros e origens, e cada sentido carrega fonte, status de
--    moderacao e votos proprios. Colapsar isso em uma tabela so impediria
--    aprovar um sentido e rejeitar outro do mesmo termo.
--
-- 2) Nao existe tabela de historico de consultas. O produto le a tela do
--    usuario; guardar o que foi lido criaria um banco de conversas privadas.
--    A unica telemetria persistida e a contagem agregada em
--    termo_desconhecido, sem contexto, sem IP e sem vinculo com usuario.
-- ---------------------------------------------------------------------------

-- Busca tolerante a erro de digitacao (nivel 2 da cascata) sem servidor de
-- busca dedicado. O free tier nao comporta um Elasticsearch.
CREATE EXTENSION IF NOT EXISTS pg_trgm;

CREATE TABLE idioma (
    id     SMALLSERIAL PRIMARY KEY,
    codigo VARCHAR(5)  NOT NULL UNIQUE,   -- 'pt-BR', 'en'
    nome   VARCHAR(40) NOT NULL
);

CREATE TABLE categoria (
    id   SMALLSERIAL PRIMARY KEY,
    slug VARCHAR(30) NOT NULL UNIQUE,     -- gaming, streaming, rua...
    nome VARCHAR(60) NOT NULL
);

CREATE TABLE fonte (
    id        SMALLSERIAL PRIMARY KEY,
    tipo      VARCHAR(20)  NOT NULL,
    descricao VARCHAR(120),
    CONSTRAINT ck_fonte_tipo
        CHECK (tipo IN ('CURADORIA', 'COMUNIDADE', 'EXTERNA', 'IA'))
);

CREATE TABLE giria (
    id                BIGSERIAL PRIMARY KEY,
    termo             VARCHAR(80) NOT NULL,
    -- Normalizado em Java, nao no banco: a mesma funcao de normalizacao roda
    -- no cliente offline (Room/IndexedDB), onde nao ha Postgres. Delegar ao
    -- unaccent() faria cliente e servidor divergirem na primeira acentuacao.
    termo_normalizado VARCHAR(80) NOT NULL,
    idioma_id         SMALLINT    NOT NULL REFERENCES idioma(id),
    -- Conteudo improprio: ocultado quando o Modo Familia esta ligado.
    nsfw              BOOLEAN     NOT NULL DEFAULT FALSE,
    -- Termo associado a comportamento de risco: gera alerta ao responsavel.
    risco_menor       BOOLEAN     NOT NULL DEFAULT FALSE,
    criado_em         TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (termo_normalizado, idioma_id)
);

CREATE INDEX idx_giria_trgm ON giria USING gin (termo_normalizado gin_trgm_ops);

-- Apelidos e variacoes de escrita ('mds', 'mdsss', 'meu deus do ceu').
-- Sem isso, cada variacao viraria um verbete solto e a curadoria precisaria
-- manter a mesma explicacao duplicada em N linhas.
CREATE TABLE giria_variacao (
    id                 BIGSERIAL PRIMARY KEY,
    giria_id           BIGINT      NOT NULL REFERENCES giria(id) ON DELETE CASCADE,
    variacao           VARCHAR(80) NOT NULL,
    variacao_normalizada VARCHAR(80) NOT NULL,
    UNIQUE (variacao_normalizada, giria_id)
);

CREATE INDEX idx_variacao_trgm ON giria_variacao
    USING gin (variacao_normalizada gin_trgm_ops);

CREATE TABLE definicao (
    id                   BIGSERIAL PRIMARY KEY,
    giria_id             BIGINT   NOT NULL REFERENCES giria(id) ON DELETE CASCADE,
    explicacao_simples   TEXT     NOT NULL,  -- nivel 1: linguagem acessivel
    explicacao_detalhada TEXT,               -- nivel 2: origem, nuances
    equivalente_formal   VARCHAR(160),
    fonte_id             SMALLINT NOT NULL REFERENCES fonte(id),
    status               VARCHAR(15) NOT NULL DEFAULT 'APROVADA',
    votos_uteis          INT      NOT NULL DEFAULT 0,
    votos_inuteis        INT      NOT NULL DEFAULT 0,
    atualizado_em        TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT ck_definicao_status
        CHECK (status IN ('PENDENTE', 'APROVADA', 'REJEITADA'))
);

CREATE INDEX idx_definicao_giria ON definicao (giria_id) WHERE status = 'APROVADA';

CREATE TABLE exemplo (
    id           BIGSERIAL PRIMARY KEY,
    definicao_id BIGINT NOT NULL REFERENCES definicao(id) ON DELETE CASCADE,
    frase        TEXT   NOT NULL,
    traducao     TEXT                        -- versao em linguagem formal
);

CREATE TABLE giria_categoria (
    giria_id     BIGINT   NOT NULL REFERENCES giria(id) ON DELETE CASCADE,
    categoria_id SMALLINT NOT NULL REFERENCES categoria(id),
    PRIMARY KEY (giria_id, categoria_id)
);

-- Conta e opcional e existe apenas para contribuir e moderar. Consultar o
-- dicionario nunca exige cadastro: exigir login para entender uma mensagem
-- afastaria exatamente o publico que o produto quer atender.
CREATE TABLE usuario (
    id         BIGSERIAL PRIMARY KEY,
    email      VARCHAR(160) NOT NULL UNIQUE,
    senha_hash VARCHAR(100) NOT NULL,       -- BCrypt
    papel      VARCHAR(15)  NOT NULL DEFAULT 'USER',
    criado_em  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT ck_usuario_papel
        CHECK (papel IN ('USER', 'MODERATOR', 'ADMIN'))
);

CREATE TABLE contribuicao (
    id                  BIGSERIAL PRIMARY KEY,
    usuario_id          BIGINT REFERENCES usuario(id),
    termo               VARCHAR(80) NOT NULL,
    idioma_id           SMALLINT    NOT NULL REFERENCES idioma(id),
    explicacao_proposta TEXT        NOT NULL,
    status              VARCHAR(15) NOT NULL DEFAULT 'PENDENTE',
    moderador_id        BIGINT REFERENCES usuario(id),
    motivo_rejeicao     VARCHAR(200),
    criado_em           TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT ck_contribuicao_status
        CHECK (status IN ('PENDENTE', 'APROVADA', 'REJEITADA'))
);

CREATE INDEX idx_contribuicao_fila ON contribuicao (status, criado_em);

-- Telemetria anonima e agregada: o que alimenta a curadoria semanal.
-- Guarda o termo e quantas vezes foi procurado. Nao guarda o contexto em
-- que apareceu, nem quem procurou, nem de onde.
CREATE TABLE termo_desconhecido (
    id                BIGSERIAL PRIMARY KEY,
    termo_normalizado VARCHAR(80) NOT NULL UNIQUE,
    idioma_provavel   VARCHAR(5),
    ocorrencias       INT         NOT NULL DEFAULT 1,
    ultima_ocorrencia TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_desconhecido_fila ON termo_desconhecido (ocorrencias DESC);

-- Sincronizacao offline: cada versao e um SQLite pronto para o cliente
-- baixar de uma vez, em vez de paginar o dicionario inteiro pela API.
CREATE TABLE pacote_dicionario (
    versao       INT PRIMARY KEY,
    gerado_em    TIMESTAMPTZ NOT NULL DEFAULT now(),
    url_download TEXT        NOT NULL,
    qtd_verbetes INT         NOT NULL
);
