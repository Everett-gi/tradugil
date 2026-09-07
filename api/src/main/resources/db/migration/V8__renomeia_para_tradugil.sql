-- ---------------------------------------------------------------------------
-- O produto passou a se chamar Tradugil. Este arquivo existe porque uma das
-- migracoes anteriores gravou o nome antigo como DADO, e nao so como
-- comentario: fonte.descricao aparece na interface quando o verbete mostra
-- de onde veio a explicacao.
--
-- POR QUE UMA MIGRACAO NOVA, E NAO EDITAR A V2
--
-- A renomeacao do projeto passou um substituidor por todos os arquivos, e
-- ele alterou o texto dentro das migracoes ja aplicadas. O Flyway recusou
-- subir na hora seguinte -- ele guarda o checksum de cada migracao aplicada
-- e reclama quando o arquivo muda.
--
-- Isso e o Flyway funcionando, nao atrapalhando. Migracao aplicada e
-- registro do que aconteceu com o banco, nao um arquivo de configuracao:
-- editar uma delas faz o historico deixar de descrever a realidade, e o
-- proximo ambiente a subir do zero recebe um schema diferente do que os
-- ambientes antigos receberam, sem ninguem perceber.
--
-- Entao as migracoes antigas voltaram ao conteudo original -- e continuam
-- dizendo "TraduGiria", que era o nome do projeto quando foram escritas --
-- e a mudanca de dado vem aqui, onde pode ser aplicada em qualquer banco
-- que ja exista.
-- ---------------------------------------------------------------------------

UPDATE fonte
   SET descricao = 'Escrita e revisada pela curadoria do Tradugil'
 WHERE tipo = 'CURADORIA';
