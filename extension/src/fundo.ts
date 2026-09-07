/*
 * Service worker da extensao.
 *
 * Concentra tres coisas: o menu de contexto, o atalho de teclado e a unica
 * porta de saida para a rede.
 *
 * POR QUE A REDE PASSA POR AQUI. O script de conteudo roda na origem da
 * pagina visitada. Um fetch feito de la para a API seria uma requisicao
 * entre origens, e o CORS do servidor so libera as origens do proprio
 * produto. O service worker roda na origem da extensao e usa as
 * host_permissions do manifesto, entao a chamada sai daqui.
 *
 * Ha um ganho de seguranca junto: a URL da API nunca e decidida pela pagina
 * visitada. Uma pagina hostil que conseguisse falar com o script de conteudo
 * ainda assim nao escolhe para onde o texto vai.
 *
 * MV3 desliga este worker quando ele fica ocioso. Nada de estado em memoria
 * entre chamadas: o que precisa sobreviver vive em chrome.storage.
 */
import { ClienteTradugil, ErroDeApi, ErroDeRede } from "@tradugil/core";
import type { RespostaDeTraducao } from "@tradugil/core";
import { lerPreferencias } from "./preferencias.js";

const ID_DO_MENU = "tradugil-explicar";

/* -------------------------------------------------------------- menus ---- */

chrome.runtime.onInstalled.addListener(() => {
  // Recriado a cada instalacao e atualizacao. Registrar sem remover antes
  // duplica o item na segunda vez.
  chrome.contextMenus.removeAll(() => {
    chrome.contextMenus.create({
      id: ID_DO_MENU,
      title: 'Explicar "%s" com o Tradugil',
      contexts: ["selection"],
    });
  });
});

chrome.contextMenus.onClicked.addListener((info, aba) => {
  if (info.menuItemId === ID_DO_MENU && aba?.id !== undefined) {
    void pedirExplicacao(aba.id);
  }
});

chrome.commands.onCommand.addListener((comando, aba) => {
  if (comando === "explicar-selecao" && aba?.id !== undefined) {
    void pedirExplicacao(aba.id);
  }
});

/**
 * Injeta o script de conteudo, se preciso, e pede o balao.
 *
 * O manifesto de proposito nao declara script de conteudo nenhum. Declarado,
 * ele rodaria em todo site aberto, inclusive banco e e-mail, e ficaria
 * carregado o tempo todo esperando algo acontecer. Aqui o codigo so entra na
 * pagina depois de um gesto explicito (atalho, menu do botao direito ou
 * popup), que e justamente o que libera a permissao activeTab: aquela aba,
 * naquele momento, e mais nada.
 *
 * Tenta a mensagem primeiro para nao reinjetar em toda invocacao. Se ninguem
 * responde, o script ainda nao esta la; injeta e repete o pedido.
 */
async function pedirExplicacao(idDaAba: number): Promise<void> {
  try {
    await chrome.tabs.sendMessage(idDaAba, { tipo: "explicar-selecao" });
    return;
  } catch {
    // Ninguem ouvindo nesta aba ainda. Segue para a injecao.
  }

  try {
    await chrome.scripting.executeScript({
      target: { tabId: idDaAba },
      files: ["conteudo.js"],
    });
    await chrome.tabs.sendMessage(idDaAba, { tipo: "explicar-selecao" });
  } catch {
    // Paginas internas do navegador, a loja de extensoes e arquivos PDF
    // recusam injecao. Nao ha balao para mostrar o aviso, entao ele sai pelo
    // distintivo do icone, o unico canal que sobra.
    await avisarNoDistintivo(idDaAba);
  }
}

async function avisarNoDistintivo(idDaAba: number): Promise<void> {
  await chrome.action.setBadgeText({ tabId: idDaAba, text: "!" });
  await chrome.action.setBadgeBackgroundColor({ tabId: idDaAba, color: "#a4262c" });
  await chrome.action.setTitle({
    tabId: idDaAba,
    title: "Recarregue a página para o Tradugil funcionar nela.",
  });
}

/* --------------------------------------------------------------- rede ---- */

async function traduzir(texto: string): Promise<RespostaDeTraducao> {
  const preferencias = await lerPreferencias();
  const cliente = new ClienteTradugil({ baseUrl: preferencias.urlDaApi });
  return cliente.traduzir({
    texto,
    idioma: "auto",
    nivel: "SIMPLES",
    modoFamilia: preferencias.modoFamilia,
  });
}

/**
 * Traduz um erro tecnico na frase que a pessoa vai ler dentro do balao.
 *
 * O publico do produto inclui quem nunca ouviu falar em HTTP. "Failed to
 * fetch" nao diz o que fazer; "sem conexao" diz.
 */
function mensagemDeErro(erro: unknown): string {
  if (erro instanceof ErroDeRede) {
    return "Sem conexão com o Tradugil. Verifique a internet e tente de novo.";
  }
  if (erro instanceof ErroDeApi) {
    if (erro.status === 429) {
      return "Muitas consultas seguidas. Espere alguns segundos e tente de novo.";
    }
    if (erro.status >= 500) {
      return "O Tradugil está fora do ar no momento. Tente daqui a pouco.";
    }
    return erro.message;
  }
  return "Não foi possível explicar esse trecho agora.";
}

/*
 * Retornar true mantem o canal aberto para a resposta assincrona. Sem isso o
 * canal fecha na hora e o script de conteudo recebe undefined, que aqui
 * apareceria como um balao vazio em vez de um erro.
 */
chrome.runtime.onMessage.addListener((mensagem, _remetente, responder) => {
  if (mensagem?.tipo !== "traduzir" || typeof mensagem.texto !== "string") {
    return false;
  }

  traduzir(mensagem.texto)
    .then(responder)
    .catch((erro) => responder({ erro: mensagemDeErro(erro) }));

  return true;
});
