-- ---------------------------------------------------------------------------
-- Segunda passada na limpeza de definicoes repetidas.
--
-- A V29 apagou so as linhas identicas nos tres campos de texto, de proposito:
-- era a regra que nao podia errar. Sobraram seis casos em que o resumo e o
-- equivalente formal sao iguais e o DETALHE difere.
--
-- Li os seis antes de escrever esta migracao. Nenhum e um sentido diferente:
-- sao dois rascunhos do mesmo verbete, escritos em lotes diferentes, em que
-- uma versao e mais completa que a outra. Exemplos:
--
--   flex     "De 'flex', flexionar o musculo. 'Flexar' e ostentar."
--            contra a mesma frase + a explicacao do "weird flex but ok"
--
--   burnout  "...pela OMS como fenomeno ocupacional."
--            contra a mesma frase + "desde 2019"
--
--   climao   a versao curta nao traz a origem ("aumentativo de clima")
--
-- A REGRA
--
-- Entre linhas com o mesmo verbete, o mesmo resumo e o mesmo equivalente
-- formal, fica a de DETALHE MAIS LONGO. Empate resolve pelo menor id.
--
-- Detalhe mais longo, e nao menor id, porque nos seis casos a versao longa
-- contem a curta e acrescenta: manter a curta jogaria fora curadoria escrita.
--
-- POR QUE ISTO NAO PODE VIRAR REGRA PERMANENTE
--
-- Exigir resumo E equivalente formal iguais e conservador, mas ainda e uma
-- heuristica: dois sentidos de verdade com o mesmo resumo seriam unificados
-- por engano. Nao ha nenhum assim hoje, e nao deve haver amanha, porque a
-- checagem do gerador passou a bloquear termo repetido entre arquivos, que e
-- a origem de todos estes casos.
-- ---------------------------------------------------------------------------

DELETE FROM definicao d
USING definicao mantida
WHERE d.giria_id = mantida.giria_id
  AND d.explicacao_simples IS NOT DISTINCT FROM mantida.explicacao_simples
  AND d.equivalente_formal IS NOT DISTINCT FROM mantida.equivalente_formal
  AND d.status             IS NOT DISTINCT FROM mantida.status
  AND d.id <> mantida.id
  -- A que fica: detalhe mais longo. COALESCE porque length(NULL) e NULL, e
  -- uma comparacao com NULL seria desconhecida e nao apagaria nada.
  AND (
        COALESCE(length(d.explicacao_detalhada), 0)
          < COALESCE(length(mantida.explicacao_detalhada), 0)
        OR (
          COALESCE(length(d.explicacao_detalhada), 0)
            = COALESCE(length(mantida.explicacao_detalhada), 0)
          AND d.id > mantida.id
        )
      );
