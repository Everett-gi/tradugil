/**
 * Reduz um termo à forma usada como chave de busca.
 *
 * PORTE DE `api/.../traducao/Normalizador.java`. As duas implementações
 * precisam devolver exatamente o mesmo resultado para toda entrada: o
 * servidor resolve pelo Postgres e o cliente offline resolve pelo IndexedDB,
 * e uma divergência de um caractere faz o mesmo termo funcionar online e
 * sumir offline — o usuário vê o aplicativo esquecer uma gíria que já sabia.
 *
 * Os casos de `normalizador.test.ts` são os mesmos de `NormalizadorTest.java`.
 * Ao mudar uma regra aqui, mude lá e regenere o pacote offline: os termos já
 * gravados foram normalizados pelas regras antigas.
 */

/**
 * Marcas de acentuação isoladas pela decomposição NFD.
 * Equivale a `\p{InCombiningDiacriticalMarks}` do Java, que é exatamente
 * este intervalo — escrito assim, e não como `\p{Diacritic}`, porque a
 * propriedade do JavaScript cobre um conjunto maior e divergiria do Java.
 */
const ACENTOS = /[\u0300-\u036f]/g;

/** Tudo que não for letra, dígito ou espaço. Espelha `\p{IsAlphabetic}\p{IsDigit}`. */
const RUIDO = /[^\p{Alphabetic}\p{Nd} ]/gu;

const ESPACOS = /\s+/g;

/** Runs de 3 ou mais caracteres iguais: o "sss" de "mdsss". */
const REPETICOES = /(.)\1{2,}/g;

/**
 * Forma canônica: minúsculas, sem acento, sem pontuação, espaços colapsados.
 * "Ranço!" e "RANCO" chegam ambos a "ranco".
 *
 * Espaços internos são preservados de propósito: "dar ruim" e "no cap" são
 * verbetes de duas palavras, e colapsá-los quebraria a chave do dicionário.
 */
export function normalizar(bruto: string | null | undefined): string {
  if (!bruto || !bruto.trim()) {
    return '';
  }
  const semAcento = bruto.normalize('NFD').replace(ACENTOS, '');
  // `toLowerCase` sem locale, igual ao `Locale.ROOT` do Java: com locale, o
  // "I" turco vira "ı" e o celular geraria uma chave diferente do servidor.
  const limpo = semAcento.toLowerCase().replace(RUIDO, ' ');
  return limpo.replace(ESPACOS, ' ').trim();
}

/**
 * Colapsa repetições de ênfase: "mdssss" vira "mdss", "kkkkkkk" vira "kk".
 *
 * Vive separada de `normalizar` porque é uma tentativa, não a chave. O
 * usuário escreve a ênfase com qualquer número de letras, e a tabela de
 * variações não consegue listar todas — mas aplicar o colapso à chave
 * principal quebraria termos legítimos com letra dobrada.
 *
 * O corte é em duas letras, e não em uma, para não destruir dígrafos do
 * português: "carro" não pode virar "caro".
 */
export function colapsarRepeticoes(normalizado: string | null | undefined): string {
  if (!normalizado) {
    return '';
  }
  return normalizado.replace(REPETICOES, '$1$1');
}
