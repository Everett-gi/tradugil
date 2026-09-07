# ADR 0001: A mesma normalização em Java, TypeScript e Kotlin

**Data:** 2026-09-06 · **Situação:** aceita

## Contexto

O Tradugil resolve consultas em dois lugares: no servidor, contra o
PostgreSQL, e no dispositivo, contra um dicionário offline (IndexedDB no
navegador, Room no Android). Os dois precisam transformar o que o usuário
escreveu na mesma chave de busca: "Ranço!", "RANÇO" e "ranco" têm que chegar
todos a `ranco`.

O caminho óbvio seria normalizar no banco, com `unaccent()` e `lower()` do
PostgreSQL. Mas o cliente offline não tem PostgreSQL. Ele precisaria de uma
segunda implementação de qualquer jeito.

## Decisão

A normalização é escrita em **Java puro** e portada para TypeScript e Kotlin,
com **os mesmos casos de teste nas três linguagens**. O banco não normaliza
nada: recebe a chave já pronta.

Diferenças de plataforma que precisaram de atenção explícita:

- `toLowerCase()` usa o locale padrão em Java e em Kotlin. Em turco, `"I"`
  vira `"ı"` sem ponto. As três implementações usam a forma sem locale
  (`Locale.ROOT`), senão o servidor e o celular gerariam chaves diferentes
  para o mesmo termo.
- Java tem `\p{InCombiningDiacriticalMarks}`; o JavaScript não. O porte usa
  o intervalo `̀-ͯ`, que é exatamente o mesmo conjunto, e não
  `\p{Diacritic}`, que cobre mais coisas e divergiria.
- O `range.last` do Kotlin é inclusivo; os outros dois são exclusivos. O
  porte soma 1 para manter a mesma convenção semiaberta.

## Consequências

**Bom:** o mesmo termo resolve igual online e offline. Se as regras
divergissem em um único caractere, o usuário veria o aplicativo esquecer uma
gíria que ele já tinha usado: um bug que aparece só sem internet, no
dispositivo dele, e é quase impossível de reproduzir.

**Ruim:** três cópias da mesma lógica. Mudar uma regra exige mudar as três e
regerar o pacote offline, porque os termos já gravados foram normalizados
pelas regras antigas.

**Mitigação:** os casos de teste são idênticos nas três linguagens e cada um
roda na sua CI. Divergir sem quebrar teste é difícil de propósito.

## Alternativas descartadas

- **Normalizar só no servidor.** Elimina o offline, que é requisito (RNF07) e
  um dos motivos de o produto existir para quem tem plano de dados limitado.
- **Compilar o Java para as outras plataformas** (TeaVM, Kotlin Multiplatform).
  Resolveria a duplicação, mas custa uma cadeia de build inteira para 60
  linhas de expressão regular, num projeto de um desenvolvedor só.
