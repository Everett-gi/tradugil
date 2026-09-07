/*
 * Gera os ícones PNG do PWA sem depender de ferramenta de imagem instalada.
 *
 * Os ícones são desenhados por código, e não commitados como binário opaco,
 * para que a identidade visual possa ser ajustada com um `npm run icones` em
 * vez de exigir um editor gráfico. O PNG é escrito na mão porque o projeto
 * não tem dependência de imagem: só o zlib do próprio Node.
 *
 *   node scripts/gerar-icones.mjs
 */
import { deflateSync } from 'node:zlib';
import { writeFileSync, mkdirSync } from 'node:fs';
import { dirname, join } from 'node:path';
import { fileURLToPath } from 'node:url';

const AQUI = dirname(fileURLToPath(import.meta.url));
const DESTINO = join(AQUI, '..', 'public');

const AZUL = [27, 73, 101, 255];
const BRANCO = [255, 255, 255, 255];
const AMARELO = [255, 209, 102, 255];
const TRANSPARENTE = [0, 0, 0, 0];

/** Retângulo de cantos arredondados: distância de Chebyshev suavizada. */
function dentroDoRetanguloArredondado(x, y, esq, topo, dir, base, raio) {
  if (x < esq || x > dir || y < topo || y > base) return false;
  const dx = Math.max(esq + raio - x, 0, x - (dir - raio));
  const dy = Math.max(topo + raio - y, 0, y - (base - raio));
  return dx * dx + dy * dy <= raio * raio;
}

/** Triângulo da "rabinha" do balão de fala, apontando para baixo e à esquerda. */
function dentroDaRabinha(x, y, pontaX, pontaY, largura, altura) {
  if (y < pontaY - altura || y > pontaY) return false;
  const progresso = (pontaY - y) / altura;
  return x >= pontaX && x <= pontaX + largura * progresso;
}

function desenhar(tamanho) {
  const u = tamanho / 100;
  const pixels = Buffer.alloc(tamanho * tamanho * 4);

  for (let y = 0; y < tamanho; y++) {
    for (let x = 0; x < tamanho; x++) {
      let cor = TRANSPARENTE;

      // Fundo: quadrado arredondado ocupando a tela toda. O ícone maskable
      // do Android recorta as bordas, então nada essencial encosta nelas.
      if (dentroDoRetanguloArredondado(x, y, 0, 0, tamanho - 1, tamanho - 1, 22 * u)) {
        cor = AZUL;
      }

      // Balão grande, branco: a mensagem que não se entende.
      if (
        dentroDoRetanguloArredondado(x, y, 18 * u, 22 * u, 74 * u, 56 * u, 9 * u) ||
        dentroDaRabinha(x, y, 26 * u, 68 * u, 16 * u, 12 * u)
      ) {
        cor = BRANCO;
      }

      // Balão pequeno, amarelo: a explicação que chega em resposta.
      if (
        dentroDoRetanguloArredondado(x, y, 46 * u, 50 * u, 84 * u, 74 * u, 7 * u) ||
        dentroDaRabinha(x, y, 68 * u, 84 * u, 12 * u, 10 * u)
      ) {
        cor = AMARELO;
      }

      pixels.set(cor, (y * tamanho + x) * 4);
    }
  }
  return pixels;
}

function crc32(buffer) {
  let c;
  const tabela = [];
  for (let n = 0; n < 256; n++) {
    c = n;
    for (let k = 0; k < 8; k++) c = c & 1 ? 0xedb88320 ^ (c >>> 1) : c >>> 1;
    tabela[n] = c >>> 0;
  }
  let crc = 0xffffffff;
  for (const byte of buffer) crc = tabela[(crc ^ byte) & 0xff] ^ (crc >>> 8);
  return (crc ^ 0xffffffff) >>> 0;
}

function bloco(tipo, dados) {
  const tamanho = Buffer.alloc(4);
  tamanho.writeUInt32BE(dados.length);
  const corpo = Buffer.concat([Buffer.from(tipo, 'ascii'), dados]);
  const verificacao = Buffer.alloc(4);
  verificacao.writeUInt32BE(crc32(corpo));
  return Buffer.concat([tamanho, corpo, verificacao]);
}

function codificarPng(pixels, tamanho) {
  const cabecalho = Buffer.alloc(13);
  cabecalho.writeUInt32BE(tamanho, 0);
  cabecalho.writeUInt32BE(tamanho, 4);
  cabecalho[8] = 8; // bits por canal
  cabecalho[9] = 6; // RGBA
  cabecalho[10] = 0; // deflate
  cabecalho[11] = 0; // filtro adaptativo
  cabecalho[12] = 0; // sem entrelaçamento

  // Cada linha do PNG é precedida por um byte de filtro; 0 = sem filtro.
  const comFiltro = Buffer.alloc(tamanho * (tamanho * 4 + 1));
  for (let y = 0; y < tamanho; y++) {
    comFiltro[y * (tamanho * 4 + 1)] = 0;
    pixels.copy(
      comFiltro,
      y * (tamanho * 4 + 1) + 1,
      y * tamanho * 4,
      (y + 1) * tamanho * 4,
    );
  }

  return Buffer.concat([
    Buffer.from([0x89, 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a]),
    bloco('IHDR', cabecalho),
    bloco('IDAT', deflateSync(comFiltro, { level: 9 })),
    bloco('IEND', Buffer.alloc(0)),
  ]);
}

mkdirSync(DESTINO, { recursive: true });
for (const tamanho of [192, 512]) {
  const arquivo = join(DESTINO, `icone-${tamanho}.png`);
  writeFileSync(arquivo, codificarPng(desenhar(tamanho), tamanho));
  console.log(`gerado ${arquivo}`);
}
