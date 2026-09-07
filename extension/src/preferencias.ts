/*
 * Preferencias da extensao, guardadas em chrome.storage.sync.
 *
 * Ficam num arquivo so porque o popup e o service worker precisam ler as
 * mesmas chaves com os mesmos padroes. Duas copias dessa leitura sao duas
 * chances de o padrao divergir, e o padrao aqui protege o modo familia.
 */

/*
 * Escrito como `type` e nao `interface` de proposito: chrome.storage.sync
 * pede um objeto com assinatura de indice, e no TypeScript so os apelidos de
 * tipo ganham essa assinatura implicitamente. Com `interface`, passar PADRAO
 * direto para o get nao compila.
 */
export type Preferencias = {
  /** Servidor consultado. Trocavel para apontar a extensao ao servidor local. */
  urlDaApi: string;
  /**
   * Ligado por padrao, igual a API. Quem instala a extensao para acompanhar
   * as conversas de um filho nao deveria precisar descobrir um botao antes
   * de o produto funcionar como promete.
   */
  modoFamilia: boolean;
};

export const PADRAO: Preferencias = {
  urlDaApi: "https://tradugil.app",
  modoFamilia: true,
};

/*
 * As unicas origens que a extensao aceita consultar. Vem do manifesto: pedir
 * uma URL fora dessa lista falharia de qualquer jeito, mas falharia com um
 * erro de rede confuso. Validar aqui transforma isso numa mensagem clara, e
 * impede que uma preferencia corrompida mande texto selecionado para um
 * servidor qualquer.
 */
const ORIGENS_PERMITIDAS = ["https://tradugil.app", "http://localhost:8080"];

export function origemPermitida(url: string): boolean {
  try {
    return ORIGENS_PERMITIDAS.includes(new URL(url).origin);
  } catch {
    return false;
  }
}

export async function lerPreferencias(): Promise<Preferencias> {
  const guardado = await chrome.storage.sync.get(PADRAO);
  const url = String(guardado.urlDaApi ?? PADRAO.urlDaApi);
  return {
    urlDaApi: origemPermitida(url) ? url : PADRAO.urlDaApi,
    modoFamilia: guardado.modoFamilia !== false,
  };
}

export async function gravarPreferencias(mudancas: Partial<Preferencias>): Promise<void> {
  await chrome.storage.sync.set(mudancas);
}
