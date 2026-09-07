# ADR 0003: Distribuir o Android por APK, não pela Google Play

**Data:** 2026-09-06 · **Situação:** aceita, com data de revisão em aberto

## Contexto

A especificação previa publicação na Google Play. Publicar exige taxa única
de US$ 25 e verificação de identidade do desenvolvedor. Não há orçamento para
isso agora.

## Decisão

O aplicativo é distribuído como **APK baixável pelo site**. A publicação na
loja fica para quando houver verba.

## Consequências

**Bom, e não era óbvio:** fora da loja não existe análise de política. O
`AccessibilityService` (que a especificação apontava como o maior risco de
rejeição, por ser uma API que o Google fiscaliza de perto) deixa de ter esse
obstáculo e pode entrar na v1. O plano B do documento (usar só
`MediaProjection`) deixa de ser necessário.

Some também do caminho crítico a exigência de 12 testadores por 14 dias
contínuos em teste fechado, que valeria para uma conta pessoal nova e
adicionaria semanas de calendário que não são desenvolvimento.

**Ruim:** sem loja não há descoberta orgânica, atualização automática nem a
confiança que o selo da Play dá. Para o público-alvo (pessoas idosas que,
como a persona Marlene, não instalam aplicativos sozinhas com facilidade), "baixe um APK e permita fontes desconhecidas" é uma barreira real, e não uma
inconveniência menor. O PWA instalável cobre parte desse público sem exigir
nada disso.

**Consequência que exige disciplina:** a assinatura passa a ser
responsabilidade do projeto, sem Play App Signing para guardar a chave. O
Android recusa atualizar um app instalado se o APK novo vier assinado com
outra chave: o usuário teria que desinstalar, perdendo o dicionário offline
que já baixou. Um único keystore, guardado fora do repositório e com backup.
Perdê-lo significa nunca mais conseguir atualizar quem já instalou.

## Quando revisar

Quando houver US$ 25 e disposição para a verificação. Nada da arquitetura
muda: o mesmo módulo Gradle gera o App Bundle da loja. O que muda é o canal
de entrega, e o `AccessibilityService` volta a ser um item de risco a ser
declarado e defendido na análise.
