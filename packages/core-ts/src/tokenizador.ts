import { normalizar } from './normalizador.js';

/**
 * Quebra o texto em palavras e monta os candidatos a gíria.
 *
 * PORTE de `api/.../traducao/Tokenizador.java`. Os casos de teste são os
 * mesmos: o destaque das gírias precisa cair exatamente nas mesmas posições
 * no site, na extensão e no aplicativo.
 */

/**
 * Sequências de letras ou dígitos. A pontuação fica de fora de propósito:
 * é ruído para a busca, e o intervalo devolvido continua apontando para a
 * posição correta no texto original mesmo assim.
 */
const PALAVRA = /[\p{Alphabetic}\p{Nd}]+/gu;

/**
 * Maior verbete de várias palavras que vale procurar ("no cap", "dar ruim").
 * Três é o teto porque a cada palavra a mais o número de candidatos cresce
 * junto com o custo da consulta, e verbetes de quatro palavras são raros o
 * bastante para não pagarem esse preço.
 */
export const MAXIMO_DE_PALAVRAS = 3;

/**
 * Um trecho do texto original e sua forma de busca.
 *
 * `fim` é exclusivo — mesma convenção de `String.prototype.slice`, para o
 * cliente marcar o trecho com `texto.slice(inicio, fim)` sem ajustar nada.
 */
export interface Trecho {
  readonly original: string;
  readonly normalizado: string;
  readonly inicio: number;
  readonly fim: number;
}

export function tokenizar(texto: string | null | undefined): Trecho[] {
  const palavras: Trecho[] = [];
  if (!texto || !texto.trim()) {
    return palavras;
  }
  // A regex é global e tem estado (lastIndex); recriar a cada chamada evita
  // que duas chamadas seguidas comecem da posição em que a anterior parou.
  const padrao = new RegExp(PALAVRA.source, PALAVRA.flags);
  let achado: RegExpExecArray | null;
  while ((achado = padrao.exec(texto)) !== null) {
    const bruto = achado[0];
    palavras.push({
      original: bruto,
      normalizado: normalizar(bruto),
      inicio: achado.index,
      fim: achado.index + bruto.length,
    });
  }
  return palavras;
}

/**
 * Monta os candidatos, das sequências mais longas para as mais curtas.
 *
 * A ordem é o que garante o casamento guloso: quando o texto tem "dar ruim",
 * o candidato de duas palavras aparece antes de "dar" e "ruim" sozinhos, e
 * vence. Sem isso o usuário veria "ruim" explicado como adjetivo comum, em
 * vez da expressão que ele leu.
 */
export function candidatos(palavras: readonly Trecho[]): Trecho[] {
  const lista: Trecho[] = [];
  for (let tamanho = MAXIMO_DE_PALAVRAS; tamanho >= 1; tamanho--) {
    for (let i = 0; i + tamanho <= palavras.length; i++) {
      const janela = palavras.slice(i, i + tamanho);
      const normalizado = janela.map((p) => p.normalizado).join(' ');
      if (!normalizado.trim()) {
        continue;
      }
      lista.push({
        original: normalizado,
        normalizado,
        inicio: janela[0]!.inicio,
        fim: janela[janela.length - 1]!.fim,
      });
    }
  }
  return lista;
}
