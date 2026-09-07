# ADR 0004: Colapso de ênfase repetida em coluna indexada

**Data:** 2026-09-06 · **Situação:** aceita

## Contexto

Em conversa brasileira, risada se escreve com quantas letras a pessoa quiser:
`kkk`, `kkkk`, `kkkkkkkkkkkk`. O mesmo vale para ênfase em geral: `mdsss`,
`nossaaaa`. Nenhuma tabela de variações consegue listar todas: são infinitas.

A primeira solução foi colapsar runs de 3 ou mais caracteres iguais para 2
antes de buscar: `kkkkkkk` → `kk`. O corte é em dois, e não em um, para não
destruir dígrafos legítimos do português: `carro` não pode virar `caro`.

O furo apareceu ao escrever o teste de integração: o verbete guardado é
`kkk`, e `kkkkkkk` colapsado vira `kk`. **Os dois nunca se encontravam.**
Colapsar só o lado do usuário não adianta: é preciso comparar forma
colapsada com forma colapsada.

Isso não era caso de borda. Era o termo mais frequente que o produto precisa
acertar, silenciosamente quebrado.

## Decisão

`giria.termo_colapsado` e `giria_variacao.variacao_colapsada`, ambas
indexadas, preenchidas com a forma colapsada. A busca compara colapsado com
colapsado.

Colunas reais, e não expressão na consulta, porque precisam de índice: sem
ele, toda consulta com repetição varreria a tabela inteira.

## O problema que isso cria

A migração preenche as colunas com uma expressão SQL
(`regexp_replace(termo_normalizado, '(.)\1{2,}', '\1\1', 'g')`) que **duplica
em SQL a regra escrita em Java**. Isso contraria o
[ADR 0001](0001-normalizacao-em-tres-linguagens.md), que existe justamente
para manter a regra num lugar só.

A duplicação é inevitável: o seed é SQL e precisa preencher a coluna.

O que não é inevitável é a duplicação ficar escondida. `ConsistenciaDoSeedIT`
percorre o dicionário inteiro conferindo, linha a linha, que o valor gravado
pelo SQL é exatamente o que o Java calcularia. Se as duas regras divergirem,
a CI quebra no mesmo dia.

Sem esse teste, o modo de falha seria: alguém ajusta o Java, esquece o SQL, e
meses depois um punhado de verbetes para de ser encontrado: sem erro em
lugar nenhum, sem log, sem exceção. Só um usuário que não acha uma palavra.

## Consequências

**Bom:** `kkkkkkkkk` encontra `kkk`. Duas colunas e dois índices a mais num
dicionário que cabe inteiro em memória: custo desprezível.

**Ruim:** uma regra em dois lugares, para sempre. Mudar o colapso exige mudar
Java, TypeScript, Kotlin e a expressão SQL.

**Mitigação:** o teste de consistência transforma a duplicação escondida em
duplicação vigiada. Não elimina o custo; garante que ele apareça.
