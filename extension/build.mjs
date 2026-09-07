/*
 * Empacota a extensao em dist/, pronta para "Carregar sem compactacao".
 *
 * Sao dois formatos de saida diferentes, e a diferenca importa:
 *
 * - conteudo.js sai como IIFE. Scripts de conteudo declarados no manifesto
 *   nao sao modulos ES: o navegador os injeta como script classico, e um
 *   arquivo com import/export no topo simplesmente nao roda.
 * - fundo.js e popup.js saem como ESM, porque o service worker esta
 *   declarado com "type": "module" e o popup carrega com type="module".
 *
 * O esbuild resolve o @tradugil/core direto do TypeScript da pasta
 * packages/core-ts, sem passo de compilacao separado. E o mesmo codigo de
 * normalizacao que o site usa, entao a extensao nao pode divergir dele.
 */
import { cpSync, mkdirSync, rmSync } from "node:fs";
import { dirname, resolve } from "node:path";
import { fileURLToPath } from "node:url";
import esbuild from "esbuild";

const aqui = dirname(fileURLToPath(import.meta.url));
const saida = resolve(aqui, "dist");

rmSync(saida, { recursive: true, force: true });
mkdirSync(saida, { recursive: true });

const comum = {
  bundle: true,
  // Chrome 120 cobre bem mais que o minimo real, e mesmo assim vale: o
  // publico da extensao usa o navegador que veio no computador, atualizado
  // sozinho. Nao ha ganho em transpilar para versoes que ninguem roda.
  target: "chrome120",
  logLevel: "info",
  minify: process.env.NODE_ENV === "production",
  sourcemap: process.env.NODE_ENV === "production" ? false : "inline",
};

await esbuild.build({
  ...comum,
  entryPoints: [resolve(aqui, "src/conteudo.ts")],
  outfile: resolve(saida, "conteudo.js"),
  format: "iife",
});

await esbuild.build({
  ...comum,
  entryPoints: [resolve(aqui, "src/fundo.ts"), resolve(aqui, "src/popup.ts")],
  outdir: saida,
  format: "esm",
});

for (const arquivo of ["manifest.json", "icones", "src/popup.html", "src/popup.css"]) {
  const destino = resolve(saida, arquivo.replace(/^src\//, ""));
  cpSync(resolve(aqui, arquivo), destino, { recursive: true });
}

console.log(`Extensao pronta em ${saida}`);
