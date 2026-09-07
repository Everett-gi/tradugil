-- ---------------------------------------------------------------------------
-- Refresh tokens com rotacao, revogacao e deteccao de reuso (RNF04).
--
-- O access token e um JWT curto (15 min) e nao e guardado em lugar nenhum:
-- ele expira sozinho. O refresh token e o oposto -- vive semanas e precisa
-- poder ser revogado -- entao e opaco e fica aqui.
--
-- Guardamos o HASH do token, nunca o token. Um vazamento desta tabela nao
-- pode dar a ninguem a capacidade de se autenticar como outra pessoa; do
-- mesmo jeito que a senha e guardada com BCrypt, o token e guardado com
-- SHA-256 (aqui basta hash rapido: o token e aleatorio de 256 bits, nao tem
-- entropia baixa como senha escolhida por humano, entao nao ha o que quebrar
-- por forca bruta ou dicionario).
--
-- ROTACAO E DETECCAO DE REUSO
--
-- Cada uso troca o token por um novo e marca o antigo como substituido. Isso
-- da uma propriedade valiosa: se um token JA USADO aparecer de novo, so ha
-- duas explicacoes -- ou o cliente legitimo repetiu (falha de rede), ou
-- alguem roubou o token. Como nao da para distinguir, o sistema assume o
-- pior e revoga a familia inteira, derrubando as sessoes das duas partes.
-- O usuario legitimo faz login de novo; o ladrao perde o acesso.
-- ---------------------------------------------------------------------------

CREATE TABLE token_de_atualizacao (
    id              BIGSERIAL PRIMARY KEY,
    usuario_id      BIGINT      NOT NULL REFERENCES usuario(id) ON DELETE CASCADE,
    hash_do_token   CHAR(64)    NOT NULL UNIQUE,
    -- Identifica a cadeia de tokens que nasceu de um mesmo login. Revogar a
    -- familia derruba a sessao inteira, e nao so o elo apresentado.
    familia         UUID        NOT NULL,
    expira_em       TIMESTAMPTZ NOT NULL,
    -- Preenchido quando o token e trocado por outro. Token com este campo
    -- preenchido que volta a ser apresentado e o sinal de reuso.
    substituido_em  TIMESTAMPTZ,
    revogado_em     TIMESTAMPTZ,
    criado_em       TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_token_familia ON token_de_atualizacao (familia);
CREATE INDEX idx_token_usuario ON token_de_atualizacao (usuario_id);
-- Varredura de expirados: a tabela cresce a cada renovacao e precisa de
-- limpeza periodica.
CREATE INDEX idx_token_expiracao ON token_de_atualizacao (expira_em)
    WHERE revogado_em IS NULL;
