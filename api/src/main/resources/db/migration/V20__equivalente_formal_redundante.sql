-- ---------------------------------------------------------------------------
-- Apaga o equivalente formal quando ele repete o proprio termo.
--
-- Sao 41 verbetes em que a curadoria preencheu o campo com a mesma palavra
-- do verbete: "aliciamento" com equivalente formal "aliciamento", "acai" com
-- "acai". Na tela isso vira
--
--     aliciamento
--     Quando um adulto se aproxima de uma crianca [...]
--     Em outras palavras: aliciamento
--
-- Uma linha que nao acrescenta nada e manda a pessoa reler o que ja leu. O
-- publico do produto inclui quem tem dificuldade com texto, e para essa
-- pessoa uma frase inutil nao e neutra: e mais uma para decifrar.
--
-- NULL aqui quer dizer "nao existe um jeito mais formal de dizer isto", que e
-- verdade nesses casos: a giria virou a palavra comum. As interfaces ja
-- tratam o campo nulo escondendo a linha, entao nao ha nada a mudar nelas.
--
-- A comparacao ignora caixa mas nao ignora acento, de proposito. Verificado
-- contra os 752 verbetes: os 41 casos batem so com a diferenca de caixa, e
-- nenhum par difere apenas por acento. Ignorar acento tambem apagaria um
-- equivalente legitimo se algum dia existir um par como "cafe" e "cafe" que
-- na verdade sao palavras diferentes.
--
-- A curadoria tambem passou a recusar isso na origem, em
-- curadoria/gerar-migracao.mjs, para o proximo lote nao reintroduzir.
-- ---------------------------------------------------------------------------

UPDATE definicao d
   SET equivalente_formal = NULL
  FROM giria g
 WHERE g.id = d.giria_id
   AND d.equivalente_formal IS NOT NULL
   AND lower(d.equivalente_formal) = lower(g.termo);
