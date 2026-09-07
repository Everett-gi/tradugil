import type { ParDeTokens, Papel } from './tipos.js';

/**
 * Guarda a sessão e renova o access token sozinha.
 *
 * <h2>Onde cada token fica, e por quê</h2>
 *
 * O **access token vive só em memória**. Ele vale 15 minutos e vai em toda
 * requisição; guardá-lo em disco não traz benefício nenhum e amplia a
 * superfície: qualquer script que rode na página passaria a lê-lo do
 * `localStorage` mesmo depois de fechar a aba.
 *
 * O **refresh token vai para o `localStorage`**, e isso é uma escolha com
 * custo. O ideal seria um cookie `httpOnly`, que JavaScript nenhum consegue
 * ler, mas cookie traz CSRF de volta e a API é consumida também pelo
 * aplicativo Android e pela extensão, que não têm cookie. Um único formato
 * para os três clientes vale mais do que a proteção extra num deles.
 *
 * A mitigação real não está aqui: está na rotação com detecção de reuso do
 * servidor. Um refresh token roubado e usado derruba a família inteira, e o
 * dono legítimo percebe na próxima renovação, que falha.
 *
 * <h2>Uma renovação por vez</h2>
 *
 * Três requisições que estouram o token ao mesmo tempo disparariam três
 * renovações em paralelo. Como cada renovação invalida o token anterior, a
 * segunda e a terceira chegariam com um token já substituído, o servidor
 * leria isso como reuso e **derrubaria a sessão de um usuário legítimo**.
 *
 * Por isso `emAndamento` guarda a promessa em curso: quem chegar durante uma
 * renovação espera a mesma, em vez de começar outra.
 */

const CHAVE = 'tradugil.sessao';

export interface EstadoDaSessao {
  papel: Papel;
  /** Momento, em ms, a partir do qual o access token deixa de valer. */
  expiraEm: number;
}

export class Sessao {
  private accessToken: string | null = null;
  private refreshToken: string | null = null;
  private papel: Papel | null = null;
  private expiraEm = 0;
  private emAndamento: Promise<void> | null = null;

  private readonly renovar: (refreshToken: string) => Promise<ParDeTokens>;
  private readonly aoMudar: () => void;

  constructor(
    renovar: (refreshToken: string) => Promise<ParDeTokens>,
    aoMudar: () => void = () => {},
  ) {
    this.renovar = renovar;
    this.aoMudar = aoMudar;
    this.carregar();
  }

  /* ------------------------------------------------------------ estado --- */

  get autenticado(): boolean {
    return this.refreshToken !== null;
  }

  get papelAtual(): Papel | null {
    return this.papel;
  }

  /** Verdadeiro para quem pode ver a fila de moderação. */
  get podeModerar(): boolean {
    return this.papel === 'MODERATOR' || this.papel === 'ADMIN';
  }

  /* ------------------------------------------------------------ tokens --- */

  guardar(par: ParDeTokens): void {
    this.accessToken = par.accessToken;
    this.refreshToken = par.refreshToken;
    this.papel = par.papel;
    // Renova 30 segundos antes do vencimento, para uma requisição não sair
    // com um token que expira no caminho.
    this.expiraEm = Date.now() + (par.expiraEmSegundos - 30) * 1000;
    this.persistir();
    this.aoMudar();
  }

  encerrar(): void {
    this.accessToken = null;
    this.refreshToken = null;
    this.papel = null;
    this.expiraEm = 0;
    try {
      localStorage.removeItem(CHAVE);
    } catch {
      // Modo privativo ou armazenamento bloqueado. A sessão em memória já
      // foi limpa, que é o que importa para o resto desta aba.
    }
    this.aoMudar();
  }

  /** O token de renovação, para o serviço poder avisar o servidor no logout. */
  get tokenDeRenovacao(): string | null {
    return this.refreshToken;
  }

  /**
   * Devolve um access token válido, renovando se preciso.
   *
   * Nulo quando não há sessão, ou quando a renovação falhou: nesse caso a
   * sessão já foi encerrada, porque um refresh recusado significa que ela
   * acabou, por expiração ou por revogação.
   */
  async token(): Promise<string | null> {
    if (!this.refreshToken) return null;
    if (this.accessToken && Date.now() < this.expiraEm) return this.accessToken;

    if (!this.emAndamento) {
      this.emAndamento = this.renovarAgora().finally(() => {
        this.emAndamento = null;
      });
    }
    await this.emAndamento;
    return this.accessToken;
  }

  private async renovarAgora(): Promise<void> {
    const atual = this.refreshToken;
    if (!atual) return;
    try {
      this.guardar(await this.renovar(atual));
    } catch {
      this.encerrar();
    }
  }

  /* ------------------------------------------------------- persistência --- */

  private persistir(): void {
    try {
      localStorage.setItem(
        CHAVE,
        JSON.stringify({ refreshToken: this.refreshToken, papel: this.papel }),
      );
    } catch {
      // Sem armazenamento, a sessão vale só enquanto a aba estiver aberta.
      // É degradação aceitável: melhor isso do que recusar o login.
    }
  }

  private carregar(): void {
    try {
      const bruto = localStorage.getItem(CHAVE);
      if (!bruto) return;
      const salvo = JSON.parse(bruto) as { refreshToken?: string; papel?: Papel };
      if (typeof salvo.refreshToken === 'string') {
        this.refreshToken = salvo.refreshToken;
        this.papel = salvo.papel ?? 'USER';
        /*
         * O access token NÃO é restaurado: ele nunca foi gravado. A primeira
         * chamada que precisar de token vai renovar, o que também serve de
         * verificação de que a sessão continua válida do lado do servidor.
         */
      }
    } catch {
      // JSON corrompido ou armazenamento indisponível: começa deslogado.
    }
  }
}
