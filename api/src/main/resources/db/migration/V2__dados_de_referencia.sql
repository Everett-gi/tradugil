-- Dados de referencia: mudam por decisao de produto, nao por uso.
-- Ficam em migracao (e nao em seed opcional) porque as chaves estrangeiras
-- de giria e definicao dependem deles para qualquer insercao funcionar.

INSERT INTO idioma (codigo, nome) VALUES
    ('pt-BR', 'Portugues do Brasil'),
    ('en',    'Ingles');

INSERT INTO categoria (slug, nome) VALUES
    ('gaming',    'Jogos'),
    ('streaming', 'Transmissoes ao vivo'),
    ('redes',     'Redes sociais'),
    ('rua',       'Linguagem de rua'),
    ('escolar',   'Ambiente escolar'),
    ('regional',  'Regionalismo'),
    ('abreviacao','Abreviacao de digitacao');

-- A origem de cada definicao e visivel ao usuario. Uma explicacao vinda da
-- IA ou do Urban Dictionary nao pode se passar por verbete revisado.
INSERT INTO fonte (tipo, descricao) VALUES
    ('CURADORIA',  'Escrita e revisada pela curadoria do TraduGiria'),
    ('COMUNIDADE', 'Enviada por usuario e aprovada na moderacao'),
    ('EXTERNA',    'Importada de fonte externa, exibida com rotulo de origem'),
    ('IA',         'Gerada por IA, ainda nao verificada por pessoa');
