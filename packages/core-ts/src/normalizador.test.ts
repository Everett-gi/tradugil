import { describe, expect, it } from 'vitest';
import { colapsarRepeticoes, normalizar } from './normalizador.js';

/**
 * Espelho de `NormalizadorTest.java`. Os casos precisam ser os mesmos, com
 * os mesmos resultados esperados: é assim que se verifica que as duas
 * implementações não divergiram.
 */
describe('normalizar', () => {
  it('remove acento e caixa, chegando à mesma chave', () => {
    expect(normalizar('Ranço')).toBe('ranco');
    expect(normalizar('RANÇO')).toBe('ranco');
    expect(normalizar('ranco')).toBe('ranco');
    expect(normalizar('migué')).toBe('migue');
  });

  it('descarta pontuação sem colar as palavras vizinhas', () => {
    expect(normalizar('pog!')).toBe('pog');
    expect(normalizar('...cringe?')).toBe('cringe');
    expect(normalizar('dar,ruim')).toBe('dar ruim');
  });

  it('preserva o espaço interno dos verbetes de mais de uma palavra', () => {
    expect(normalizar('  Dar   Ruim  ')).toBe('dar ruim');
    expect(normalizar('No Cap')).toBe('no cap');
  });

  it("mantém dígitos, porque '67' é verbete", () => {
    expect(normalizar('67')).toBe('67');
  });

  it('entrada vazia não quebra', () => {
    expect(normalizar(null)).toBe('');
    expect(normalizar(undefined)).toBe('');
    expect(normalizar('   ')).toBe('');
    expect(normalizar('!!!')).toBe('');
  });

  it('trata acento pré-composto e decomposto como o mesmo termo', () => {
    // "ç" pode chegar como um caractere só (U+00E7) ou como "c" + cedilha
    // (U+0063 U+0327), dependendo do teclado e do sistema. O usuário digitou
    // a mesma palavra nos dois casos.
    const preComposto = 'ranço';
    const decomposto = 'ranço';
    expect(preComposto).not.toBe(decomposto);
    expect(normalizar(preComposto)).toBe('ranco');
    expect(normalizar(decomposto)).toBe('ranco');
  });
});

describe('colapsarRepeticoes', () => {
  it('colapsa ênfase até duas letras, não até uma', () => {
    expect(colapsarRepeticoes('mdsss')).toBe('mdss');
    expect(colapsarRepeticoes('kkkkkkkk')).toBe('kk');
    expect(colapsarRepeticoes('aaaa')).toBe('aa');
  });

  it("não destrói dígrafo legítimo: 'carro' não vira 'caro'", () => {
    expect(colapsarRepeticoes('carro')).toBe('carro');
    expect(colapsarRepeticoes('nossa')).toBe('nossa');
    expect(colapsarRepeticoes('kkk')).toBe('kk');
  });

  it('entrada vazia não quebra', () => {
    expect(colapsarRepeticoes(null)).toBe('');
    expect(colapsarRepeticoes('')).toBe('');
  });
});
