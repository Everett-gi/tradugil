-- ---------------------------------------------------------------------------
-- Remove definicoes duplicadas: a mesma explicacao gravada duas ou tres vezes
-- no mesmo verbete.
--
-- COMO ISSO ACONTECEU
--
-- O INSERT de `giria` tem ON CONFLICT DO NOTHING e nunca duplicou verbete. O
-- INSERT de `definicao` nao tem guarda nenhuma, e de proposito: acrescentar
-- um sentido novo a um termo que ja existe e caso legitimo.
--
-- A consequencia e que um termo repetido em dois lotes de curadoria grava a
-- MESMA explicacao duas vezes. O seed inicial (V3) trouxe uma centena de
-- termos escritos a mao; os lotes seguintes (V10 a V17) repetiram varios sem
-- que ninguem percebesse, porque nao havia checagem nenhuma na epoca.
--
-- O defeito nunca quebrou nada. A migracao aplicou, os testes passaram, e o
-- unico sintoma era um verbete mostrando a mesma frase duas ou tres vezes na
-- tela. "based" tinha tres copias identicas; "mid" tambem.
--
-- O QUE ESTE DELETE FAZ, E O QUE ELE NAO FAZ
--
-- So apaga linhas IDENTICAS nos tres campos de texto, mantendo a de menor id.
-- Uma definicao com o mesmo resumo e detalhe diferente NAO e tocada: pode ser
-- um sentido de verdade, escrito por outra pessoa, e apagar isso seria perder
-- curadoria.
--
-- IS NOT DISTINCT FROM, e nao "=": os campos detalhada e formal aceitam nulo,
-- e em SQL "NULL = NULL" e desconhecido, nao verdadeiro. Com "=" as
-- duplicatas que tem esses campos vazios escapariam, que sao justamente
-- muitas.
--
-- Os exemplos ligados a definicao apagada vao junto (ON DELETE CASCADE). Como
-- as linhas sao identicas, os exemplos tambem sao, e o que fica e o mesmo
-- conteudo sem a repeticao.
--
-- Os votos da linha apagada se perdem. Aceitavel agora, porque o produto
-- ainda nao esta no ar e ninguem votou; nao seria depois, e por isso a
-- checagem que impede novas duplicatas entrou no gerador junto com esta
-- migracao.
-- ---------------------------------------------------------------------------

DELETE FROM definicao d
USING definicao mantida
WHERE d.giria_id = mantida.giria_id
  AND d.explicacao_simples   IS NOT DISTINCT FROM mantida.explicacao_simples
  AND d.explicacao_detalhada IS NOT DISTINCT FROM mantida.explicacao_detalhada
  AND d.equivalente_formal   IS NOT DISTINCT FROM mantida.equivalente_formal
  AND d.status               IS NOT DISTINCT FROM mantida.status
  AND d.id > mantida.id;
