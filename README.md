# TraduGíria

Tradutor de gírias e linguagem da internet, em português e inglês.

Explica o que significa "pog", "rizz", "mds" ou "cringe" para quem está fora
da cultura digital — pessoas idosas que não entendem as mensagens dos netos,
pais que acompanham o uso de redes pelos filhos e quem simplesmente não
acompanha a velocidade com que a linguagem online muda.

> Especificação técnica completa em
> [`docs/TraduGiria_Especificacao_Tecnica_v1.docx`](docs/TraduGiria_Especificacao_Tecnica_v1.docx).

## Estado atual

| Fase | Entrega | Situação |
|------|---------|----------|
| F0 | Monorepo, esquema, seed inicial, API de consulta | Em andamento |
| F1 | Cascata completa com IA, PWA instalável, extensão | Não iniciada |
| F2 | Android: bolha flutuante, OCR local, Modo Família | Não iniciada |

**Distribuição do Android:** APK baixável pelo site. A publicação na Google
Play fica para quando houver orçamento — nada da arquitetura muda por isso,
só o canal de entrega.

## Como funciona

Toda consulta percorre uma cascata, do mais barato e rápido ao mais caro.
Parar cedo é o que mantém a latência baixa e o custo de IA perto de zero:

| Nível | Onde | Custo | Situação |
|-------|------|-------|----------|
| 0 | Dicionário local no dispositivo | zero, offline | Não iniciado |
| 1 | Cache em memória da API (Caffeine) | zero | Pronto |
| 2 | PostgreSQL curado, com busca tolerante a erro de digitação | baixo | Pronto |
| 3 | Fontes externas, sempre com rótulo de origem | baixo | Não iniciado |
| 4 | IA generativa, rotulada como não verificada | pago | Não iniciado |
| 5 | Fila de termos desconhecidos → curadoria | — | Pronto |

O nível 5 é o que impede o dicionário de envelhecer: todo termo que ninguém
soube explicar vira item de uma fila anônima que alimenta a curadoria.

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
docs/           Especificação e decisões de arquitetura
infra/          Caddy e scripts de implantação
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

## Testes

```bash
cd api && mvn test
```

Os testes de banco rodam contra PostgreSQL real, nunca H2: a busca do nível 2
depende de `pg_trgm`, que não existe no H2 — um teste verde ali não provaria
nada sobre o comportamento em produção.

## Licença

Projeto de portfólio de Gildean Monteiro do Nascimento.
