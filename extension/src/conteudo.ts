/*
 * Script de conteudo: desenha o balao dentro da pagina.
 *
 * E o requisito RF05, e o mais delicado da extensao por dois motivos.
 *
 * PRIVACIDADE: este script tem acesso ao texto inteiro da pagina. Por isso
 * ele nao e declarado no manifesto: o service worker o injeta sob demanda,
 * so depois de um gesto explicito, e so na aba em questao. Ele tambem nunca
 * le a pagina por conta propria; le apenas o que esta selecionado. Detalhes
 * em PRIVACIDADE.md.
 *
 * ISOLAMENTO: o balao e desenhado dentro de um Shadow DOM. Sem isso, o CSS
 * da pagina visitada vazaria para dentro dele e o balao apareceria quebrado
 * em metade dos sites, de um jeito impossivel de prever ou testar.
 */
import type { RespostaDeTraducao } from "@tradugil/core";

const ID_DO_BALAO = "tradugil-balao";
const TAMANHO_MAXIMO_DA_SELECAO = 2000;

let balao: HTMLElement | null = null;
let raiz: ShadowRoot | null = null;

/* ------------------------------------------------------------- estilos --- */

/*
 * Escrito aqui e nao num arquivo separado porque vai para dentro do Shadow
 * DOM, que nao enxerga folhas de estilo da pagina nem da extensao.
 *
 * As cores sao as mesmas do site. A fonte NAO e: o site carrega Atkinson
 * Hyperlegible do Google, e aqui isso significaria o balao buscar um recurso
 * de terceiros dentro da pagina que a pessoa esta visitando, visivel no
 * monitor de rede dela e registrado por quem serve a fonte. Uma extensao que
 * promete nao observar navegacao nao pode abrir essa conexao. Fica a pilha do
 * sistema, que ja e legivel e nao custa requisicao nenhuma.
 */
const ESTILO = `
  :host { all: initial; }

  .balao {
    position: absolute;
    z-index: 2147483647;
    max-width: 22rem;
    background: #ffffff;
    color: #17282c;
    /* Filete da marca na esquerda em vez de moldura inteira, como nos
       cartoes do site. O balao ja se separa da pagina pela sombra. */
    border: 1px solid #e2d6c8;
    border-left: 5px solid #0a5560;
    border-radius: 14px;
    box-shadow:
      0 2px 4px rgba(23, 40, 44, 0.08),
      0 16px 40px rgba(23, 40, 44, 0.18);
    padding: 0.95rem 1.1rem;
    font-family: system-ui, -apple-system, "Segoe UI", Roboto, sans-serif;
    font-size: 15px;
    line-height: 1.55;
  }

  @media (prefers-color-scheme: dark) {
    .balao {
      background: #18272b;
      color: #e9f1f2;
      border-color: #2b3d41;
      border-left-color: #6fd0dd;
      box-shadow: 0 16px 40px rgba(0, 0, 0, 0.5);
    }
  }

  .cabeca {
    display: flex;
    align-items: baseline;
    justify-content: space-between;
    gap: 0.75rem;
    margin-bottom: 0.4rem;
  }

  .termo { font-weight: 700; font-size: 16px; }

  .fechar {
    all: unset;
    cursor: pointer;
    font-size: 18px;
    line-height: 1;
    padding: 0.1rem 0.3rem;
    border-radius: 4px;
  }
  .fechar:hover { background: rgba(127, 127, 127, 0.18); }
  .fechar:focus-visible { outline: 2px solid #0a5560; }

  .explicacao { margin: 0 0 0.4rem; }

  .formal { margin: 0; font-size: 14px; opacity: 0.8; }

  .aviso {
    margin: 0.6rem 0 0;
    padding: 0.5rem 0.65rem;
    border-radius: 6px;
    font-size: 14px;
    font-weight: 600;
    background: #fdece8;
    border: 1px solid #a3231c;
    color: #7a1a15;
  }

  .rotulo-ia {
    display: inline-block;
    margin-bottom: 0.4rem;
    padding: 0.15rem 0.45rem;
    border-radius: 999px;
    border: 1px solid #5b3a8e;
    background: #f0eaf9;
    color: #422a68;
    font-size: 12px;
    font-weight: 700;
  }

  .mais { margin: 0.5rem 0 0; font-size: 13px; opacity: 0.7; }

  .carregando { font-style: italic; opacity: 0.75; }
`;

/* -------------------------------------------------------------- balao ---- */

function criarBalao(): { caixa: HTMLElement; conteudo: HTMLElement } {
  if (!balao) {
    balao = document.createElement("div");
    balao.id = ID_DO_BALAO;
    // O Shadow DOM isola o balao do CSS da pagina visitada.
    raiz = balao.attachShadow({ mode: "closed" });

    const estilo = document.createElement("style");
    estilo.textContent = ESTILO;
    raiz.appendChild(estilo);

    const caixa = document.createElement("div");
    caixa.className = "balao";
    caixa.setAttribute("role", "dialog");
    caixa.setAttribute("aria-label", "Explicação da gíria");
    raiz.appendChild(caixa);

    document.documentElement.appendChild(balao);
  }

  const caixa = raiz!.querySelector(".balao") as HTMLElement;
  caixa.replaceChildren();
  return { caixa, conteudo: caixa };
}

function fecharBalao(): void {
  balao?.remove();
  balao = null;
  raiz = null;
}

/**
 * Posiciona o balao logo abaixo da selecao, sem sair da tela.
 *
 * Usa coordenadas absolutas de documento (com o deslocamento da rolagem)
 * para o balao acompanhar a pagina ao rolar, em vez de ficar colado na tela.
 */
function posicionar(caixa: HTMLElement, retangulo: DOMRect): void {
  const margem = 8;
  const topo = retangulo.bottom + window.scrollY + margem;

  caixa.style.top = `${topo}px`;
  caixa.style.left = "0px";

  // Mede depois de inserido: a largura real depende do texto.
  const largura = caixa.offsetWidth;
  const limite = document.documentElement.clientWidth - margem;
  const desejado = retangulo.left + window.scrollX;

  caixa.style.left = `${Math.max(margem, Math.min(desejado, limite - largura))}px`;
}

function montarCabeca(caixa: HTMLElement, titulo: string): void {
  const cabeca = document.createElement("div");
  cabeca.className = "cabeca";

  const termo = document.createElement("span");
  termo.className = "termo";
  termo.textContent = titulo;

  const fechar = document.createElement("button");
  fechar.className = "fechar";
  fechar.setAttribute("aria-label", "Fechar");
  fechar.textContent = "×";
  fechar.addEventListener("click", fecharBalao);

  cabeca.append(termo, fechar);
  caixa.appendChild(cabeca);
}

function mostrarCarregando(retangulo: DOMRect): void {
  const { caixa } = criarBalao();
  montarCabeca(caixa, "Tradugil");

  const p = document.createElement("p");
  p.className = "explicacao carregando";
  p.textContent = "Consultando…";
  caixa.appendChild(p);

  posicionar(caixa, retangulo);
}

function mostrarResposta(resposta: RespostaDeTraducao, retangulo: DOMRect): void {
  const { caixa } = criarBalao();

  if (resposta.girias.length === 0) {
    montarCabeca(caixa, "Tradugil");
    const p = document.createElement("p");
    p.className = "explicacao";
    p.textContent =
      "Não encontramos nenhuma gíria conhecida nesse trecho.";
    caixa.appendChild(p);
    posicionar(caixa, retangulo);
    return;
  }

  const primeira = resposta.girias[0]!;
  montarCabeca(caixa, primeira.termo);

  // A origem IA e obrigatoria na interface (secao 7.1): resposta nao
  // verificada nao pode se passar por verbete revisado.
  if (primeira.origem === "IA") {
    const rotulo = document.createElement("span");
    rotulo.className = "rotulo-ia";
    rotulo.textContent = "gerada por IA, não verificada";
    caixa.appendChild(rotulo);
  }

  const explicacao = document.createElement("p");
  explicacao.className = "explicacao";
  explicacao.textContent = primeira.explicacao ?? "Ainda não temos explicação para este termo.";
  caixa.appendChild(explicacao);

  if (primeira.equivalenteFormal) {
    const formal = document.createElement("p");
    formal.className = "formal";
    formal.textContent = `Em outras palavras: ${primeira.equivalenteFormal}`;
    caixa.appendChild(formal);
  }

  if (primeira.riscoMenor) {
    const aviso = document.createElement("p");
    aviso.className = "aviso";
    aviso.textContent =
      "Este termo costuma aparecer em conversas que merecem atenção. " +
      "Se você acompanha o uso de redes de um adolescente, pode valer uma conversa.";
    caixa.appendChild(aviso);
  }

  if (resposta.girias.length > 1) {
    const mais = document.createElement("p");
    mais.className = "mais";
    mais.textContent =
      `Mais ${resposta.girias.length - 1} no trecho: ` +
      resposta.girias.slice(1).map((g) => g.termo).join(", ");
    caixa.appendChild(mais);
  }

  posicionar(caixa, retangulo);
}

function mostrarErro(mensagem: string, retangulo: DOMRect): void {
  const { caixa } = criarBalao();
  montarCabeca(caixa, "Tradugil");
  const p = document.createElement("p");
  p.className = "explicacao";
  p.textContent = mensagem;
  caixa.appendChild(p);
  posicionar(caixa, retangulo);
}

/* ------------------------------------------------------------ selecao ---- */

function selecaoAtual(): { texto: string; retangulo: DOMRect } | null {
  const selecao = window.getSelection();
  if (!selecao || selecao.isCollapsed || selecao.rangeCount === 0) return null;

  const texto = selecao.toString().trim();
  if (!texto || texto.length > TAMANHO_MAXIMO_DA_SELECAO) return null;

  return { texto, retangulo: selecao.getRangeAt(0).getBoundingClientRect() };
}

async function explicarSelecao(): Promise<void> {
  const alvo = selecaoAtual();
  if (!alvo) return;

  mostrarCarregando(alvo.retangulo);

  try {
    /*
     * O pedido vai para o service worker, e nao direto daqui. O script de
     * conteudo roda na origem da pagina visitada, entao uma chamada daqui
     * seria uma requisicao entre origens, barrada pelo CORS da API, que so
     * libera as origens conhecidas do produto.
     */
    const resposta = await chrome.runtime.sendMessage({
      tipo: "traduzir",
      texto: alvo.texto,
    });

    if (resposta?.erro) {
      mostrarErro(resposta.erro, alvo.retangulo);
      return;
    }
    mostrarResposta(resposta as RespostaDeTraducao, alvo.retangulo);
  } catch {
    mostrarErro("Não conseguimos consultar agora. Tente de novo em instantes.", alvo.retangulo);
  }
}

/* ------------------------------------------------------------- eventos --- */

/*
 * Como a injecao e sob demanda, o mesmo arquivo pode ser injetado duas vezes
 * na mesma pagina se dois gestos chegarem juntos. Sem esta marca, a segunda
 * injecao registraria os ouvintes de novo e um unico atalho dispararia duas
 * consultas.
 */
const MARCA_DE_CARGA = "__tradugilAtivo";

function registrarOuvintes(): void {
  chrome.runtime.onMessage.addListener((mensagem, _remetente, responder) => {
    if (mensagem?.tipo !== "explicar-selecao") return false;

    // Responde na hora, sem esperar a consulta terminar. E o sinal de "estou
    // aqui" que o service worker usa para decidir se precisa injetar; se a
    // resposta so viesse no fim da consulta, ele injetaria de novo enquanto
    // a primeira ainda estivesse no ar.
    responder({ recebido: true });
    void explicarSelecao();
    return false;
  });

  // Fecha ao clicar fora ou apertar Esc: o balao nunca deve ficar preso na
  // tela de quem so queria ler a pagina.
  document.addEventListener("mousedown", (evento) => {
    if (balao && evento.target !== balao) fecharBalao();
  });

  document.addEventListener("keydown", (evento) => {
    if (evento.key === "Escape") fecharBalao();
  });
}

const janela = window as unknown as Record<string, boolean | undefined>;
if (!janela[MARCA_DE_CARGA]) {
  janela[MARCA_DE_CARGA] = true;
  registrarOuvintes();
}
