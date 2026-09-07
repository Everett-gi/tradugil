-- ---------------------------------------------------------------------------
-- Categorias novas, para o dicionario grande que entra nas proximas
-- migracoes.
--
-- As sete originais foram pensadas para 63 verbetes. Com mais de mil, elas
-- viram baldes grandes demais: "rua" acabaria com metade do dicionario
-- dentro, e categoria que nao separa nada nao ajuda ninguem a navegar.
--
-- As novas cobrem contextos que aparecem muito nas conversas que o produto
-- precisa explicar, e que nao cabiam em nenhuma das antigas.
-- ---------------------------------------------------------------------------

INSERT INTO categoria (slug, nome) VALUES
    ('trabalho',      'Trabalho e dinheiro'),
    ('relacionamento','Namoro e afeto'),
    ('familia',       'Familia e casa'),
    ('musica',        'Musica e funk'),
    ('humor',         'Humor e ironia'),
    ('elogio',        'Elogio e aprovacao'),
    ('critica',       'Critica e desaprovacao'),
    ('emocao',        'Emocao e reacao'),
    ('descricao',     'Descricao de pessoas'),
    ('acao',          'Acoes e verbos'),
    ('tempo',         'Tempo e frequencia'),
    ('esporte',       'Esporte'),
    ('comida',        'Comida e bebida'),
    ('atencao',       'Termos que pedem atencao')
ON CONFLICT (slug) DO NOTHING;
