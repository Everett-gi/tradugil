import {
  ClienteTradugil,
  ErroDeApi,
  ErroDeRede,
  type PedidoDeTraducao,
  type RespostaDeTraducao,
} from '@tradugil/core';
import { memorizar, traduzirLocalmente } from './dicionarioLocal.js';

/**
 * Caminho relativo: em desenvolvimento o Vite faz proxy para a API, e em
 * produção o Caddy serve as duas coisas na mesma origem. Assim não existe
 * URL de servidor embutida no bundle que precise mudar por ambiente.
 */
const cliente = new ClienteTradugil({ baseUrl: '' });

export interface ResultadoDeTraducao {
  resposta: RespostaDeTraducao;
  /** Verdadeiro quando a resposta veio do dicionário do próprio dispositivo. */
  offline: boolean;
}

/**
 * Decide se vale tentar o dicionário local em vez de mostrar erro.
 *
 * Falha de rede é o caso óbvio. O 5xx não é: quando a API cai, o navegador
 * recebe um 502 do Caddy: uma resposta HTTP perfeitamente válida, não um
 * erro de rede. Sem esta segunda condição, o aplicativo diria "erro
 * inesperado" a um usuário que tem o termo salvo no aparelho e poderia ser
 * atendido na hora.
 *
 * Um 4xx não entra: o pedido é que estava errado, e o dicionário local não
 * resolveria melhor.
 */
function valeTentarLocalmente(erro: unknown): boolean {
  if (erro instanceof ErroDeRede) return true;
  return erro instanceof ErroDeApi && erro.status >= 500;
}

/**
 * Executa a cascata do lado do cliente: tenta o servidor e, se a rede
 * falhar, cai no dicionário local em vez de mostrar erro.
 *
 * A ordem é servidor primeiro, e não cache primeiro, de propósito: o
 * dicionário local pode estar desatualizado, e uma explicação corrigida pela
 * curadoria (especialmente de um termo de risco) precisa chegar. O local é
 * a rede de segurança, não o caminho preferencial.
 */
export async function traduzir(pedido: PedidoDeTraducao): Promise<ResultadoDeTraducao> {
  try {
    const resposta = await cliente.traduzir(pedido);
    // Guarda o que veio para a próxima consulta funcionar sem conexão.
    void memorizar(resposta).catch(() => {
      // Falhar ao gravar o cache não pode derrubar uma consulta que deu certo.
    });
    return { resposta, offline: false };
  } catch (erro) {
    if (!valeTentarLocalmente(erro)) {
      throw erro;
    }
    const resposta = await traduzirLocalmente(pedido.texto, {
      nivel: pedido.nivel ?? 'SIMPLES',
      modoFamilia: pedido.modoFamilia ?? true,
    });
    return { resposta, offline: true };
  }
}

export { cliente };
