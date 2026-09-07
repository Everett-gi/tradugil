# Registros de decisão de arquitetura

Decisões que custaram discussão e que alguém (inclusive o próprio autor daqui
a seis meses) vai querer entender antes de mudar.

Cada registro tem contexto, decisão e **consequências, incluindo as ruins**.
Um ADR que só lista vantagens não é um registro de decisão: é propaganda.

| # | Decisão | Situação |
|---|---------|----------|
| [0001](0001-normalizacao-em-tres-linguagens.md) | A mesma normalização em Java, TypeScript e Kotlin | Aceita |
| [0002](0002-defesa-contra-prompt-injection.md) | Defesa em quatro camadas contra prompt injection | Aceita |
| [0003](0003-apk-direto-em-vez-de-play-store.md) | Distribuir o Android por APK, não pela Google Play | Aceita, revisão em aberto |
| [0004](0004-colapso-de-repeticao-em-coluna-indexada.md) | Colapso de ênfase repetida em coluna indexada | Aceita |

Três deles (0001, 0002, 0004) documentam decisões que já custaram um bug
encontrado durante o desenvolvimento. Estão aqui para o próximo não custar de
novo.
