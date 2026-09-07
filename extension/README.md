# Tradugil: extensão de navegador

Selecione uma gíria em qualquer página e veja o que ela significa, sem sair
dali. É o requisito RF05 da especificação.

Funciona em Chrome, Edge, Brave, Opera e qualquer navegador baseado em
Chromium. Firefox ainda não: ele usa `browser.*` em vez de `chrome.*` em
partes da API, e o service worker do Manifesto V3 tem diferenças que pedem um
build próprio.

## Instalando na sua máquina

```bash
cd extension && npm install && npm run build
```

Depois, no navegador:

1. Abra `chrome://extensions` (no Edge, `edge://extensions`).
2. Ligue o **Modo do desenvolvedor**, no canto da tela.
3. Clique em **Carregar sem compactação** e escolha a pasta
   `extension/dist`. Não a `extension`: o que é carregável é a saída do
   build, e só ela.

O ícone aparece na barra. Se ele estiver escondido atrás do botão de
extensões, fixe-o: é por ele que se chega às preferências.

## Usando

Três caminhos, todos partindo de um gesto seu:

- Selecione o texto e aperte <kbd>Ctrl</kbd> + <kbd>Shift</kbd> + <kbd>E</kbd>.
- Selecione o texto, clique com o botão direito e escolha
  "Explicar ... com o Tradugil".
- Clique no ícone e digite a gíria, quando ela não está escrita em lugar
  nenhum da tela.

Nos dois primeiros aparece um balão logo abaixo da seleção. <kbd>Esc</kbd> ou
um clique fora fecham.

O atalho pode colidir com o de outra extensão. Se não responder, veja em
`chrome://extensions/shortcuts` qual delas ficou com ele e troque.

## Apontando para o servidor local

O padrão é `https://tradugil.app`. Para desenvolver contra a API rodando na
sua máquina, abra o popup, expanda **Servidor** e troque para
`http://localhost:8080`.

Só esses dois endereços são aceitos, e a recusa é conferida em dois lugares:
a lista em `src/preferencias.ts` e as `host_permissions` do manifesto. Um
terceiro endereço não passa nem que a preferência seja adulterada à mão.

## Privacidade

A extensão pede o poder mais amplo de todo o projeto: acesso ao texto das
páginas. O desenho que limita esse poder está em
[`PRIVACIDADE.md`](PRIVACIDADE.md), e o resumo é que **nenhum código do
Tradugil entra numa página antes de você acionar a extensão nela**.

## Estrutura

| Arquivo | Papel |
|---------|-------|
| `src/fundo.ts` | Service worker: menus, atalho e a única saída de rede |
| `src/conteudo.ts` | Desenha o balão na página, dentro de um Shadow DOM |
| `src/popup.ts` | Consulta avulsa e preferências |
| `src/preferencias.ts` | Leitura e validação do que fica em `storage.sync` |
| `build.mjs` | Empacota em `dist/` com o esbuild |
| `conferir-pacote.mjs` | Verifica que `dist/` é carregável |

`npm run conferir` roda a última: ela abre o manifesto e confere que todo
arquivo citado existe. O navegador só faz essa validação na hora de
instalar, e a CI não instala nada, então sem essa checagem um caminho errado
no manifesto passa verde e só aparece para quem for usar.

## Dois formatos de saída

O `conteudo.js` sai como IIFE e o `fundo.js` e o `popup.js` saem como ESM.
Não é preferência: o script injetado numa página entra como script clássico,
e um arquivo com `import` no topo simplesmente não roda ali. O service
worker, ao contrário, está declarado com `"type": "module"`.

## O que falta

- Contribuir com uma definição pela própria extensão.
- Dicionário local para responder sem internet, como o PWA e o Android já
  fazem.
- Versão para Firefox.
