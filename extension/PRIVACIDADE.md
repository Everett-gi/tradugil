# Extensão: o que ela vê e o que ela envia

Uma extensão que explica gírias precisa, por definição, de acesso ao texto
das páginas. Esse é o poder mais amplo que o Tradugil pede em qualquer
plataforma, e por isso o desenho dele está escrito aqui, não só no código.

## Ela não roda em toda página

O primeiro rascunho declarava um script de conteúdo em `http://*/*` e
`https://*/*`. Funcionava, e estava errado: o script passaria a rodar em
todo site aberto, inclusive banco e e-mail, e ficaria carregado o tempo
inteiro esperando algo acontecer. O Chrome resumiria isso na instalação como
"ler e alterar todos os seus dados em todos os sites", o que seria uma
descrição honesta.

A versão atual **não declara script de conteúdo nenhum**. O código só entra
na página quando você aciona a extensão, de uma destas três formas:

- o atalho <kbd>Ctrl</kbd> + <kbd>Shift</kbd> + <kbd>E</kbd>
- o item "Explicar ... com o Tradugil" no botão direito
- o próprio popup do ícone

As três contam como gesto explícito para o navegador, e é isso que libera a
permissão `activeTab`: acesso **àquela aba, naquele momento**, e a mais nada.
Numa aba onde você nunca acionou a extensão, ela nunca esteve presente.

## O que sai da sua máquina

Só o trecho que você selecionou. Não a página, não a URL, não o título, não
o histórico. O envio acontece uma vez, no momento do gesto, e a resposta não
fica guardada.

O destino é fixo e conferido em duas camadas: a lista de origens permitidas
em `src/preferencias.ts` e as `host_permissions` do manifesto. Só valem
`https://tradugil.app` e `http://localhost:8080`, o segundo para quem roda o
servidor na própria máquina durante o desenvolvimento. Uma preferência
adulterada apontando para outro servidor é recusada antes da requisição.

A chamada de rede sai do service worker, nunca do script que está dentro da
página. Isso resolve o CORS, e junto resolve uma questão de segurança: a
página visitada não participa da decisão de para onde o texto vai.

## O que fica guardado

Duas preferências, em `chrome.storage.sync`: o endereço do servidor e se o
modo família está ligado. Nenhuma consulta, nenhum termo, nenhum histórico.

## O balão dentro da página

O balão é desenhado num Shadow DOM fechado. O motivo imediato é visual: sem
isso, o CSS de cada site vazaria para dentro dele. O efeito colateral é útil
nos dois sentidos, porque a página também não consegue ler o conteúdo do
balão pelo DOM.

## O que ainda falta

- A extensão só consulta; ainda não deixa contribuir com uma definição.
- Não há dicionário local aqui: sem internet, o balão avisa em vez de
  responder. O aplicativo Android e o PWA já funcionam offline; a extensão
  ainda não.
