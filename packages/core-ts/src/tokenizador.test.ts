import { describe, expect, it } from 'vitest';
import { candidatos, tokenizar } from './tokenizador.js';

/** Espelho de `TokenizadorTest.java`. */

/** A frase de exemplo da seção 5.2 do documento de especificação. */
const FRASE = 'mano ele clutchou a round, foi mt pog, kekw';

describe('tokenizar', () => {
  it('as posições recortam exatamente a palavra no texto original', () => {
    const palavras = tokenizar(FRASE);

    // O contrato de que a interface depende: slice(inicio, fim) devolve a
    // palavra. Se isto quebrar, o destaque cai no lugar errado.
    for (const palavra of palavras) {
      expect(FRASE.slice(palavra.inicio, palavra.fim)).toBe(palavra.original);
    }

    expect(palavras.map((p) => p.original)).toEqual([
      'mano', 'ele', 'clutchou', 'a', 'round', 'foi', 'mt', 'pog', 'kekw',
    ]);
  });

  it('a vírgula colada não entra no trecho destacado', () => {
    const pog = tokenizar(FRASE).find((p) => p.original === 'pog')!;
    expect(FRASE.slice(pog.inicio, pog.fim)).toBe('pog');
    expect(FRASE.charAt(pog.fim)).toBe(',');
  });

  it('chamadas seguidas não continuam de onde a anterior parou', () => {
    // A regex global tem estado; se ela fosse compartilhada entre chamadas,
    // a segunda começaria no meio do texto e perderia palavras.
    expect(tokenizar(FRASE).length).toBe(tokenizar(FRASE).length);
    expect(tokenizar('pog')).toHaveLength(1);
    expect(tokenizar('pog')).toHaveLength(1);
  });

  it('texto vazio não gera token nem exceção', () => {
    expect(tokenizar('')).toEqual([]);
    expect(tokenizar(null)).toEqual([]);
    expect(tokenizar(undefined)).toEqual([]);
  });
});

describe('candidatos', () => {
  it('vêm do mais longo para o mais curto', () => {
    const lista = candidatos(tokenizar('deu dar ruim'));
    const posicaoDoPar = lista.findIndex((c) => c.normalizado === 'dar ruim');
    const posicaoDoIsolado = lista.findIndex((c) => c.normalizado === 'ruim');

    expect(posicaoDoPar).toBeGreaterThanOrEqual(0);
    // A ordem é o que faz "dar ruim" vencer "ruim" no casamento guloso.
    expect(posicaoDoPar).toBeLessThan(posicaoDoIsolado);
  });

  it('candidatos de várias palavras usam a forma normalizada', () => {
    const lista = candidatos(tokenizar('Deu Dar  Ruim!'));
    expect(lista.map((c) => c.normalizado)).toContain('dar ruim');
  });

  it('as posições de um candidato composto abrangem as duas palavras', () => {
    const texto = 'isso vai dar ruim';
    const par = candidatos(tokenizar(texto)).find((c) => c.normalizado === 'dar ruim')!;
    expect(texto.slice(par.inicio, par.fim)).toBe('dar ruim');
  });

  it('lista vazia não gera candidato', () => {
    expect(candidatos([])).toEqual([]);
  });
});
