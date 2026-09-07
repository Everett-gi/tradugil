/*
 * Confere se dist/ e uma extensao carregavel.
 *
 * O navegador so valida o manifesto na hora de instalar, e nao ha instalacao
 * na CI. Sem esta conferencia, renomear um arquivo de saida no build.mjs e
 * esquecer do manifesto passa verde e so aparece na maquina de quem for
 * instalar: "Could not load javascript 'fundo.js' for script", com o pacote
 * ja publicado.
 *
 * So verifica o que da para verificar sem navegador: JSON valido, campos
 * obrigatorios presentes e todo caminho citado existindo de fato.
 */
import { existsSync, readFileSync } from "node:fs";
import { dirname, resolve } from "node:path";
import { fileURLToPath } from "node:url";

const aqui = dirname(fileURLToPath(import.meta.url));
const dist = resolve(aqui, "dist");
const problemas = [];

function exigirArquivo(caminho, onde) {
  if (typeof caminho !== "string" || !caminho) {
    problemas.push(`${onde}: caminho ausente ou vazio`);
    return;
  }
  if (!existsSync(resolve(dist, caminho))) {
    problemas.push(`${onde}: "${caminho}" nao existe em dist/`);
  }
}

if (!existsSync(dist)) {
  console.error("dist/ nao existe. Rode `npm run build` antes.");
  process.exit(1);
}

let manifesto;
try {
  manifesto = JSON.parse(readFileSync(resolve(dist, "manifest.json"), "utf8"));
} catch (erro) {
  console.error(`manifest.json invalido: ${erro.message}`);
  process.exit(1);
}

if (manifesto.manifest_version !== 3) {
  problemas.push("manifest_version deve ser 3");
}
for (const campo of ["name", "version", "description"]) {
  if (!manifesto[campo]) problemas.push(`campo obrigatorio ausente: ${campo}`);
}

/*
 * default_locale sem a pasta _locales faz o Chrome recusar a extensao com
 * "Default locale was specified, but _locales subtree is missing". Ja
 * aconteceu neste repositorio.
 */
if (manifesto.default_locale && !existsSync(resolve(dist, "_locales"))) {
  problemas.push("default_locale declarado, mas dist/_locales nao existe");
}

exigirArquivo(manifesto.background?.service_worker, "background.service_worker");
exigirArquivo(manifesto.action?.default_popup, "action.default_popup");

for (const [tamanho, caminho] of Object.entries(manifesto.icons ?? {})) {
  exigirArquivo(caminho, `icons.${tamanho}`);
}

for (const [indice, bloco] of (manifesto.content_scripts ?? []).entries()) {
  for (const arquivo of bloco.js ?? []) {
    exigirArquivo(arquivo, `content_scripts[${indice}].js`);
  }
  for (const arquivo of bloco.css ?? []) {
    exigirArquivo(arquivo, `content_scripts[${indice}].css`);
  }
}

/*
 * O script de conteudo nao esta no manifesto de proposito: e injetado sob
 * demanda pelo service worker (ver PRIVACIDADE.md). Como nada no manifesto o
 * cita, nenhuma checagem acima o alcanca, e apagar a entrada dele do
 * build.mjs passaria despercebido ate alguem apertar o atalho.
 */
exigirArquivo("conteudo.js", "injecao sob demanda");

/*
 * A injecao sob demanda depende destas duas permissoes. Sem "scripting", a
 * chamada falha; sem "activeTab", ela falha em toda pagina onde o usuario
 * nao concedeu acesso, que e o caso normal.
 */
for (const permissao of ["scripting", "activeTab"]) {
  if (!(manifesto.permissions ?? []).includes(permissao)) {
    problemas.push(`permissao "${permissao}" e necessaria para a injecao sob demanda`);
  }
}

/* O popup carrega a folha por caminho relativo; o manifesto nao a menciona. */
exigirArquivo("popup.css", "estilo do popup");

if (problemas.length > 0) {
  console.error("Pacote invalido:");
  for (const problema of problemas) console.error(`  - ${problema}`);
  process.exit(1);
}

console.log(`Pacote conferido: ${manifesto.name} ${manifesto.version}`);
