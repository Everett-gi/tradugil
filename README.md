# Tradugil

Tradutor de gírias e linguagem da internet, em português e inglês.

Explica o que significa "pog", "rizz", "mds" ou "cringe" para quem está fora
da cultura digital — pessoas idosas que não entendem as mensagens dos netos,
pais que acompanham o uso de redes pelos filhos e quem simplesmente não
acompanha a velocidade com que a linguagem online muda.

> Especificação técnica completa em
> [`docs/Tradugil_Especificacao_Tecnica_v1.docx`](docs/Tradugil_Especificacao_Tecnica_v1.docx).

## Estado atual

| Fase | Entrega | Situação |
|------|---------|----------|
| F0 | Monorepo, esquema, seed inicial, API de consulta | Código pronto; falta subir contra o banco |
| F1 | PWA instalável, lógica compartilhada, cascata com IA | Pronta; falta subir contra o banco |
| F2 | Android: bolha flutuante, OCR local, Modo Família | App compila com dicionário offline; falta a leitura de tela |

**Distribuição do Android:** APK baixável pelo site. A publicação na Google
Play fica para quando houver orçamento — nada da arquitetura muda por isso,
só o canal de entrega.

## Como funciona

Toda consulta percorre uma cascata, do mais barato e rápido ao mais caro.
Parar cedo é o que mantém a latência baixa e o custo de IA perto de zero:

| Nível | Onde | Custo | Situação |
|-------|------|-------|----------|
| 0 | Dicionário local no dispositivo | zero, offline | Pronto na web e no Android, com pacote de sincronização |
| 1 | Cache em memória da API (Caffeine) | zero | Pronto |
| 2 | PostgreSQL curado, com busca tolerante a erro de digitação | baixo | Pronto |
| 3 | Fontes externas, sempre com rótulo de origem | baixo | Não iniciado |
| 4 | IA generativa, rotulada como não verificada | pago | Pronto |
| 5 | Fila de termos desconhecidos → curadoria | — | Pronto |

O nível 5 é o que impede o dicionário de envelhecer: todo termo que ninguém
soube explicar vira item de uma fila anônima que alimenta a curadoria.

### A camada de IA e o texto do usuário

O nível 4 é o ponto mais sensível da aplicação, porque o texto que chega nele
foi lido da tela do usuário e é **dado não confiável por definição**: pode
conter instruções escritas de propósito para o modelo, plantadas por quem
escreveu a mensagem que a vítima está tentando entender. O atacante não
precisa de acesso nenhum ao sistema — basta mandar uma mensagem para alguém
que usa o Tradugil.

São quatro camadas de defesa, e nenhuma delas basta sozinha:

1. **Delimitação** — termo e trecho vão dentro de tags, e o prompt diz que o
   conteúdo delas é objeto de análise, nunca instrução.
2. **Neutralização do delimitador** — as tags são removidas do texto do
   usuário antes de montar o prompt. Sem isso, bastaria escrever a tag de
   fechamento no meio da mensagem para sair da área de dados.
3. **Esquema de saída** — a resposta é obrigada a ser um JSON de campos
   fixos. Uma injeção bem-sucedida não tem campo de texto livre por onde sair.
4. **Validação semântica** — o esquema garante o formato, não o conteúdo.
   Tamanho, faixa de confiança e coerência são conferidos do lado de cá.

E a defesa que não está no prompt: a resposta **nunca** é apresentada como
verbete revisado. Chega à interface marcada como gerada por IA e não
verificada. Mesmo que todas as camadas falhem, o pior resultado é um texto
errado rotulado como não confiável — não um texto errado com a autoridade do
dicionário.

As defesas são testadas em `DefesaContraPromptInjectionTest`, que roda sem
chave de API.

**Sem `ANTHROPIC_API_KEY` configurada, a camada fica desligada e a API
atende normalmente pelos níveis 0 a 2.** Isso não é um modo degradado a
evitar: é o comportamento correto em desenvolvimento e se a verba acabar.

## Contas e moderação

Conta existe **apenas para contribuir e moderar**. Consultar o dicionário
nunca exige cadastro — exigir login para entender uma mensagem afastaria
exatamente o público que o produto quer atender.

**Sessão:** access token JWT de 15 minutos e refresh token opaco de 30 dias.
A divisão importa: um JWT não pode ser revogado — uma vez emitido, vale até
expirar mesmo que a conta seja banida no minuto seguinte. A revogação de
verdade acontece no refresh token, que fica no banco; os 15 minutos são o
quanto de dano um access token vazado consegue causar.

Cada renovação troca o refresh token e marca o antigo como substituído. Se um
token já usado reaparece, ou o cliente legítimo repetiu ou alguém roubou —
como não dá para distinguir, a sessão inteira é derrubada.

**Nada chega ao dicionário sem passar por uma pessoa.** Sem moderação
obrigatória, o campo de contribuição vira um canal aberto para definições
ofensivas e desinformação, exibidas com a autoridade de um verbete para um
público que inclui pessoas idosas e famílias.

**A trilha de auditoria é append-only, garantida pelo banco.** A coluna
`moderador_id` guarda o estado atual, e estado atual pode ser sobrescrito:
um moderador que aprova algo impróprio e depois rejeita para encobrir faz a
aprovação desaparecer. A tabela `auditoria_de_moderacao` registra o que
aconteceu, e um gatilho recusa `UPDATE` e `DELETE` — inclusive por SQL
direto, o que o teste de integração confirma.

## Privacidade

O produto lê texto da tela do usuário, o que pode incluir conversas privadas.
Três decisões de arquitetura, não de texto jurídico, tratam disso:

- **Nenhum conteúdo de tela é persistido.** Não existe tabela de histórico de
  consultas. O texto enviado percorre a requisição em memória e some com ela.
- **A única telemetria é agregada e anônima:** o termo não encontrado e um
  contador. Sem contexto, sem IP, sem vínculo com usuário.
- **OCR e leitura acontecem no dispositivo.** Só o trecho selecionado sai
  dali.

## Estrutura

```
api/            Spring Boot 3.3 · Java 21 · PostgreSQL · Flyway
web/            React · Vite — site, PWA instalável e popup da extensão
android/        Kotlin · Jetpack Compose
packages/
  core-ts/      Normalização e tokenização compartilhadas com os clientes
docs/
  adr/          Registros de decisão de arquitetura
infra/          Caddy: TLS, proxy, limite de requisição e entrega do APK
```

### Uma regra que atravessa três linguagens

`Normalizador` e `Tokenizador` existem em Java (`api/`), TypeScript
(`packages/core-ts/`) e Kotlin (`android/`). Precisam se comportar de forma
**idêntica**: se divergirem em um caractere, o mesmo termo resolve online e
falha offline, e o usuário vê o aplicativo esquecer uma gíria que já sabia.

Os testes em `api/src/test/.../NormalizadorTest.java` são o contrato. Ao
portar a lógica, porte os casos de teste junto.

## Rodando a API

Requisitos: Java 21, Maven 3.9 e um PostgreSQL com a extensão `pg_trgm`.

```bash
cp .env.exemplo .env
```

Preencha `.env` com a string de conexão e então:

```bash
cd api && mvn spring-boot:run
```

O Flyway aplica o esquema e o seed na primeira subida.

- API: `http://localhost:8080/api/v1`
- Documentação OpenAPI: `http://localhost:8080/swagger-ui.html`

### Experimentando

```bash
curl -s "http://localhost:8080/api/v1/girias?q=crinje"
```

```bash
curl -s -X POST http://localhost:8080/api/v1/traduzir -H "Content-Type: application/json" -d '{"texto":"mano ele clutchou a round, foi mt pog","nivel":"SIMPLES"}'
```

A primeira consulta demonstra a tolerância a erro de digitação: `crinje`
encontra `cringe`.

## Rodando o site

```bash
npm --prefix web run dev
```

Abre em `http://localhost:5173`, com proxy para a API em `:8080`. Sem a API
no ar, o site cai no dicionário local do navegador e avisa disso na tela.

Para gerar o site de produção, com service worker e manifesto do PWA:

```bash
npm --prefix web run build
```

## Rodando o Android

Abra a pasta `android/` no Android Studio. As instruções completas — versões,
assinatura do APK e o que ainda falta — estão em
[`android/README.md`](android/README.md).

## Testes

```bash
cd api && mvn test
```

```bash
npm --prefix packages/core-ts test
```

Os testes Kotlin do domínio rodam junto com o módulo Android, pelo Gradle.

Os testes de banco rodam contra PostgreSQL real, nunca H2: a busca do nível 2
depende de `pg_trgm`, que não existe no H2 — um teste verde ali não provaria
nada sobre o comportamento em produção.

## Licença

Projeto de portfólio de Gildean Monteiro do Nascimento.
