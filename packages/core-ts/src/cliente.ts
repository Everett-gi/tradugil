import type {
  CategoriaResumo,
  ContribuicaoResposta,
  Credenciais,
  DecisaoDeModeracao,
  ErroDaApi,
  NovaContribuicao,
  ParDeTokens,
  GiriaCompleta,
  GiriaResumo,
  Pagina,
  PedidoDeTraducao,
  RespostaDeTraducao,
} from './tipos.js';

/**
 * Cliente da API. Um só para o site, o PWA, o popup da extensão e a
 * interface do desktop: os quatro falam com o mesmo servidor e não deveriam
 * manter quatro versões do mesmo `fetch`.
 */

export class ErroDeApi extends Error {
  readonly status: number;
  readonly codigo: string;

  constructor(status: number, codigo: string, mensagem: string) {
    super(mensagem);
    this.name = 'ErroDeApi';
    this.status = status;
    this.codigo = codigo;
  }

  /** Verdadeiro quando o termo simplesmente não existe no dicionário. */
  get naoEncontrado(): boolean {
    return this.status === 404;
  }
}

/** Falha de rede: o cliente offline usa para decidir cair no dicionário local. */
export class ErroDeRede extends Error {
  constructor(causa: unknown) {
    super('Não foi possível falar com o servidor.');
    this.name = 'ErroDeRede';
    this.cause = causa;
  }
}

export interface OpcoesDoCliente {
  baseUrl: string;
  /**
   * Teto de espera. Sem ele, uma rede ruim deixa a interface presa em
   * "carregando" para sempre, e o público do produto não sabe que pode
   * desistir e tentar de novo.
   */
  timeoutMs?: number;
}

const TIMEOUT_PADRAO_MS = 8000;

export class ClienteTradugil {
  private readonly baseUrl: string;
  private readonly timeoutMs: number;

  constructor(opcoes: OpcoesDoCliente) {
    this.baseUrl = opcoes.baseUrl.replace(/\/+$/, '');
    this.timeoutMs = opcoes.timeoutMs ?? TIMEOUT_PADRAO_MS;
  }

  async traduzir(pedido: PedidoDeTraducao): Promise<RespostaDeTraducao> {
    return this.requisitar<RespostaDeTraducao>('/traduzir', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(pedido),
    });
  }

  /**
   * Busca e navegação no mesmo método, como na API.
   *
   * Com `consulta` preenchida é busca. Com ela vazia e uma `categoria`, é o
   * catálogo abrindo uma prateleira em ordem alfabética.
   */
  async buscar(
    consulta: string,
    opcoes: {
      idioma?: string;
      categoria?: string;
      modoFamilia?: boolean;
      pagina?: number;
      tamanho?: number;
    } = {},
  ): Promise<Pagina<GiriaResumo>> {
    const parametros = new URLSearchParams({ q: consulta });
    if (opcoes.idioma) parametros.set('idioma', opcoes.idioma);
    if (opcoes.categoria) parametros.set('categoria', opcoes.categoria);
    // Só manda quando é false: ausente já significa ligado na API, e repetir
    // o padrão no cliente é uma segunda cópia da regra para manter em dia.
    if (opcoes.modoFamilia === false) parametros.set('modoFamilia', 'false');
    if (opcoes.pagina !== undefined) parametros.set('pagina', String(opcoes.pagina));
    if (opcoes.tamanho !== undefined) parametros.set('tamanho', String(opcoes.tamanho));
    return this.requisitar<Pagina<GiriaResumo>>(`/girias?${parametros}`);
  }

  /** Prateleiras do catálogo, já sem as vazias. */
  async categorias(modoFamilia = true): Promise<CategoriaResumo[]> {
    const sufixo = modoFamilia ? '' : '?modoFamilia=false';
    return this.requisitar<CategoriaResumo[]>(`/categorias${sufixo}`);
  }

  /* ------------------------------------------------------- identidade --- */

  async registrar(credenciais: Credenciais): Promise<ParDeTokens> {
    return this.requisitar<ParDeTokens>('/auth/registro', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(credenciais),
    });
  }

  async entrar(credenciais: Credenciais): Promise<ParDeTokens> {
    return this.requisitar<ParDeTokens>('/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(credenciais),
    });
  }

  async renovar(refreshToken: string): Promise<ParDeTokens> {
    return this.requisitar<ParDeTokens>('/auth/refresh', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ refreshToken }),
    });
  }

  /**
   * Encerra a sessão no servidor.
   *
   * Avisar importa: sem isto o refresh token continuaria válido por 30 dias
   * mesmo depois de a pessoa clicar em sair, e "sair" viraria só apagar o
   * token do navegador.
   */
  async sair(refreshToken: string): Promise<void> {
    await this.requisitar<void>('/auth/logout', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ refreshToken }),
    });
  }

  /* ------------------------------------------------------ contribuicao --- */

  async propor(
    contribuicao: NovaContribuicao,
    token: string,
  ): Promise<ContribuicaoResposta> {
    return this.requisitar<ContribuicaoResposta>('/contribuicoes', {
      method: 'POST',
      headers: this.comToken(token),
      body: JSON.stringify(contribuicao),
    });
  }

  async minhasContribuicoes(token: string): Promise<ContribuicaoResposta[]> {
    return this.requisitar<ContribuicaoResposta[]>('/contribuicoes/minhas', {
      headers: { Authorization: `Bearer ${token}` },
    });
  }

  /* --------------------------------------------------------- moderacao --- */

  async filaDeModeracao(token: string): Promise<ContribuicaoResposta[]> {
    return this.requisitar<ContribuicaoResposta[]>('/moderacao/contribuicoes', {
      headers: { Authorization: `Bearer ${token}` },
    });
  }

  async decidir(
    id: number,
    decisao: DecisaoDeModeracao,
    token: string,
  ): Promise<ContribuicaoResposta> {
    return this.requisitar<ContribuicaoResposta>(`/moderacao/contribuicoes/${id}`, {
      method: 'PATCH',
      headers: this.comToken(token),
      body: JSON.stringify(decisao),
    });
  }

  private comToken(token: string): Record<string, string> {
    return { 'Content-Type': 'application/json', Authorization: `Bearer ${token}` };
  }

  /* ---------------------------------------------------------- verbetes --- */

  async verbete(termo: string, idioma?: string): Promise<GiriaCompleta> {
    const sufixo = idioma ? `?idioma=${encodeURIComponent(idioma)}` : '';
    return this.requisitar<GiriaCompleta>(
      `/girias/${encodeURIComponent(termo)}${sufixo}`,
    );
  }

  private async requisitar<T>(caminho: string, init: RequestInit = {}): Promise<T> {
    const abortador = new AbortController();
    const relogio = setTimeout(() => abortador.abort(), this.timeoutMs);
    let resposta: Response;
    try {
      resposta = await fetch(`${this.baseUrl}/api/v1${caminho}`, {
        ...init,
        signal: abortador.signal,
      });
    } catch (causa) {
      // Rede caiu ou estourou o tempo. Distinguir de erro da API importa:
      // aqui vale tentar o dicionário local, num 404 não vale.
      throw new ErroDeRede(causa);
    } finally {
      clearTimeout(relogio);
    }

    if (!resposta.ok) {
      throw await this.montarErro(resposta);
    }
    return (await resposta.json()) as T;
  }

  private async montarErro(resposta: Response): Promise<ErroDeApi> {
    try {
      const corpo = (await resposta.json()) as ErroDaApi;
      return new ErroDeApi(resposta.status, corpo.erro, corpo.mensagem);
    } catch {
      // Corpo não era o JSON esperado: um proxy no meio do caminho, por
      // exemplo. O status ainda diz o suficiente para a interface reagir.
      return new ErroDeApi(
        resposta.status,
        'RESPOSTA_INVALIDA',
        'Erro inesperado. Tente novamente em instantes.',
      );
    }
  }
}
