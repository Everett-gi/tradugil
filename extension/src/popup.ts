/*
 * Popup da extensao.
 *
 * Serve para dois casos que o balao nao cobre: consultar uma giria que a
 * pessoa ouviu falar e nao tem selecionada em lugar nenhum, e ajustar as
 * preferencias.
 *
 * Assim como o script de conteudo, o popup nao chama a rede: ele pede ao
 * service worker. Um caminho unico de saida significa um lugar so para
 * conferir o que sai da maquina, e um lugar so para tratar erro.
 */
import type { RespostaDeTraducao } from "@tradugil/core";
import { PADRAO, gravarPreferencias, lerPreferencias, origemPermitida } from "./preferencias.js";

const formulario = document.getElementById("busca") as HTMLFormElement;
const campoDoTermo = document.getElementById("termo") as HTMLInputElement;
const resultado = document.getElementById("resultado") as HTMLElement;
const modoFamilia = document.getElementById("modo-familia") as HTMLInputElement;
const campoDaUrl = document.getElementById("url-da-api") as HTMLInputElement;
const avisoDaUrl = document.getElementById("aviso-da-url") as HTMLElement;

/* ------------------------------------------------------- preferencias ---- */

async function carregarPreferencias(): Promise<void> {
  const preferencias = await lerPreferencias();
  modoFamilia.checked = preferencias.modoFamilia;
  campoDaUrl.value = preferencias.urlDaApi;
}

modoFamilia.addEventListener("change", () => {
  void gravarPreferencias({ modoFamilia: modoFamilia.checked });
});

campoDaUrl.addEventListener("change", () => {
  const url = campoDaUrl.value.trim();
  if (!origemPermitida(url)) {
    // Nao grava e nao apaga o que a pessoa digitou: apagar esconderia o erro
    // de digitacao que ela precisa ver para corrigir.
    avisoDaUrl.hidden = false;
    return;
  }
  avisoDaUrl.hidden = true;
  void gravarPreferencias({ urlDaApi: url });
});

/* ------------------------------------------------------------ consulta --- */

function limpar(): void {
  resultado.replaceChildren();
}

function mostrarTexto(texto: string, classe: string): void {
  limpar();
  const p = document.createElement("p");
  p.className = classe;
  p.textContent = texto;
  resultado.appendChild(p);
}

function montarVerbete(giria: RespostaDeTraducao["girias"][number]): HTMLElement {
  const caixa = document.createElement("article");
  caixa.className = "verbete";

  if (giria.origem === "IA") {
    const rotulo = document.createElement("span");
    rotulo.className = "rotulo-ia";
    rotulo.textContent = "gerada por IA, não verificada";
    caixa.appendChild(rotulo);
  }

  const titulo = document.createElement("h2");
  titulo.textContent = giria.termo;
  caixa.appendChild(titulo);

  const explicacao = document.createElement("p");
  explicacao.textContent = giria.explicacao ?? "Ainda não temos explicação para este termo.";
  caixa.appendChild(explicacao);

  if (giria.equivalenteFormal) {
    const formal = document.createElement("p");
    formal.className = "formal";
    formal.textContent = `Em outras palavras: ${giria.equivalenteFormal}`;
    caixa.appendChild(formal);
  }

  if (giria.riscoMenor) {
    const aviso = document.createElement("p");
    aviso.className = "aviso";
    aviso.textContent =
      "Este termo costuma aparecer em conversas que merecem atenção. " +
      "Se você acompanha o uso de redes de um adolescente, pode valer uma conversa.";
    caixa.appendChild(aviso);
  }

  return caixa;
}

function mostrarResposta(resposta: RespostaDeTraducao): void {
  limpar();

  if (resposta.girias.length === 0) {
    mostrarTexto(
      "Não encontramos essa gíria. Ela pode ser nova ou muito local; " +
        "guardamos o termo para a curadoria olhar.",
      "erro",
    );
    return;
  }

  for (const giria of resposta.girias) {
    resultado.appendChild(montarVerbete(giria));
  }
}

formulario.addEventListener("submit", async (evento) => {
  evento.preventDefault();
  const texto = campoDoTermo.value.trim();
  if (!texto) return;

  mostrarTexto("Consultando…", "carregando");

  try {
    const resposta = await chrome.runtime.sendMessage({ tipo: "traduzir", texto });
    if (resposta?.erro) {
      mostrarTexto(resposta.erro, "erro");
      return;
    }
    mostrarResposta(resposta as RespostaDeTraducao);
  } catch {
    mostrarTexto("Não conseguimos consultar agora. Tente de novo em instantes.", "erro");
  }
});

void carregarPreferencias().catch(() => {
  // Storage indisponivel e raro, mas silenciar deixaria o popup mostrando um
  // modo familia desmarcado que nao corresponde ao que a API vai aplicar.
  modoFamilia.checked = PADRAO.modoFamilia;
  campoDaUrl.value = PADRAO.urlDaApi;
});
