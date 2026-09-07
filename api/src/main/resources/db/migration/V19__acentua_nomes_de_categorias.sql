-- ---------------------------------------------------------------------------
-- Acentua os nomes das categorias.
--
-- Ate agora esses nomes nao apareciam para ninguem: a API devolvia so o slug
-- ('gaming', 'musica'), e o nome ficava parado no banco. O catalogo muda
-- isso, porque e ele quem imprime "Musica e funk" na tela.
--
-- Escrever "Emocao" para quem esta tentando entender o que uma giria
-- significa e errado pelo mesmo motivo que "role" sem acento era, na V18: o
-- produto existe para ser lido por quem tem dificuldade com texto, e
-- portugues errado atrapalha exatamente essa pessoa.
--
-- So o nome de exibicao muda. O slug e a chave usada pela API, pelo pacote
-- offline e pelos arquivos de curadoria; mexer nele quebraria dicionarios ja
-- sincronizados nos aparelhos.
--
-- As dez categorias que ja estavam certas ('Jogos', 'Redes sociais',
-- 'Linguagem de rua', 'Ambiente escolar', 'Regionalismo', 'Trabalho e
-- dinheiro', 'Namoro e afeto', 'Humor e ironia', 'Esporte' e 'Comida e
-- bebida') ficam de fora: nao ha nada para corrigir nelas.
-- ---------------------------------------------------------------------------

UPDATE categoria SET nome = 'Transmissões ao vivo'     WHERE slug = 'streaming';
UPDATE categoria SET nome = 'Abreviação de digitação'  WHERE slug = 'abreviacao';
UPDATE categoria SET nome = 'Família e casa'           WHERE slug = 'familia';
UPDATE categoria SET nome = 'Música e funk'            WHERE slug = 'musica';
UPDATE categoria SET nome = 'Elogio e aprovação'       WHERE slug = 'elogio';
UPDATE categoria SET nome = 'Crítica e desaprovação'   WHERE slug = 'critica';
UPDATE categoria SET nome = 'Emoção e reação'          WHERE slug = 'emocao';
UPDATE categoria SET nome = 'Descrição de pessoas'     WHERE slug = 'descricao';
UPDATE categoria SET nome = 'Ações e verbos'           WHERE slug = 'acao';
UPDATE categoria SET nome = 'Tempo e frequência'       WHERE slug = 'tempo';
UPDATE categoria SET nome = 'Termos que pedem atenção' WHERE slug = 'atencao';
