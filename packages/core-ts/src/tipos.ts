/**
 * Formatos trocados com a API. Espelham `TraducaoDtos` e `GiriaDtos` no
 * servidor: mudar um lado sem o outro quebra dicionários já sincronizados
 * nos dispositivos.
 */

export type CodigoDeIdioma = 'pt-BR' | 'en';

export type NivelDeExplicacao = 'SIMPLES' | 'DETALHADA';

/** De qual nível da cascata veio a resposta. A interface é obrigada a exibir. */
export type OrigemDaResposta = 'DICIONARIO' | 'EXTERNA' | 'IA';

export type TipoDeFonte = 'CURADORIA' | 'COMUNIDADE' | 'EXTERNA' | 'IA';

export interface PedidoDeTraducao {
  texto: string;
  idioma?: CodigoDeIdioma | 'auto';
  nivel?: NivelDeExplicacao;
  /** Pista de onde o texto apareceu. Só a camada de IA usa, para desambiguar. */
  contexto?: string;
  /** Ausente equivale a ligado: o padrão protege o caso em que errar custa caro. */
  modoFamilia?: boolean;
}

export interface GiriaDetectada {
  termo: string;
  /** Par [início, fim), pronto para `texto.slice(...)` sem ajuste. */
  posicao: [number, number];
  explicacao: string | null;
  equivalenteFormal: string | null;
  nsfw: boolean;
  riscoMenor: boolean;
  confianca: number;
  origem: OrigemDaResposta;
}

export interface RespostaDeTraducao {
  idiomaDetectado: string | null;
  girias: GiriaDetectada[];
  geradoPorIa: boolean;
  naoResolvidos: string[];
}

export interface ExemploResposta {
  frase: string;
  traducao: string | null;
}

export interface DefinicaoResposta {
  id: number;
  explicacaoSimples: string;
  explicacaoDetalhada: string | null;
  equivalenteFormal: string | null;
  fonte: TipoDeFonte;
  votosUteis: number;
  exemplos: ExemploResposta[];
}

export interface GiriaResumo {
  id: number;
  termo: string;
  idioma: string;
  explicacaoSimples: string | null;
  equivalenteFormal: string | null;
  nsfw: boolean;
  riscoMenor: boolean;
  categorias: string[];
}

export interface GiriaCompleta {
  id: number;
  termo: string;
  idioma: string;
  nsfw: boolean;
  riscoMenor: boolean;
  categorias: string[];
  variacoes: string[];
  definicoes: DefinicaoResposta[];
}

export interface Pagina<T> {
  itens: T[];
  pagina: number;
  tamanho: number;
  temMais: boolean;
}

export interface ErroDaApi {
  status: number;
  erro: string;
  mensagem: string;
  campos: Array<{ campo: string; mensagem: string }>;
  momento: string;
}

/**
 * Prateleira do catálogo.
 *
 * O `slug` é a chave estável, usada na URL e no pacote offline; o `nome` é o
 * que a pessoa lê. A `quantidade` vem junto porque o catálogo precisa dela
 * antes de abrir a categoria: é o que permite esconder prateleira vazia e é
 * o que se mostra ao lado do nome, para ninguém clicar às cegas.
 */
export interface CategoriaResumo {
  slug: string;
  nome: string;
  quantidade: number;
}

/* ------------------------------------------------------------ identidade --- */

export type Papel = 'USER' | 'MODERATOR' | 'ADMIN';

export interface Credenciais {
  email: string;
  senha: string;
}

export interface ParDeTokens {
  accessToken: string;
  refreshToken: string;
  expiraEmSegundos: number;
  papel: Papel;
}

/* ---------------------------------------------------------- contribuicao --- */

export type StatusDeContribuicao = 'PENDENTE' | 'APROVADA' | 'REJEITADA';

export interface NovaContribuicao {
  termo: string;
  idioma: CodigoDeIdioma;
  explicacaoProposta: string;
}

export interface ContribuicaoResposta {
  id: number;
  termo: string;
  idioma: string;
  explicacaoProposta: string;
  status: StatusDeContribuicao;
  /** Preenchido só na rejeição. É o que a pessoa precisa ler para reenviar. */
  motivoRejeicao: string | null;
  criadoEm: string;
}

export interface DecisaoDeModeracao {
  aprovar: boolean;
  /** Obrigatório na rejeição: sem ele, quem contribuiu não aprende nada. */
  motivo?: string;
  /**
   * Slug da prateleira do catálogo, na aprovação.
   *
   * Obrigatório quando o verbete resultante ficaria sem nenhuma: sem
   * prateleira ele é encontrado pela busca e some do catálogo, que é por onde
   * chega quem não sabe o que procurar.
   */
  categoria?: string;
}

/**
 * O que o dicionário já tem para o termo de uma proposta.
 *
 * Existe para quem modera não decidir no escuro. Sem isto a tela mostrava
 * termo, idioma e texto proposto, e nada mais: aprovar sem saber que o
 * verbete já existe com três sentidos, um deles dizendo quase a mesma coisa,
 * é como o dicionário ganhou explicações repetidas antes.
 */
export interface VerbeteExistente {
  /** As explicações já publicadas, para a comparação ser imediata. */
  sentidos: string[];
  /**
   * Slugs das prateleiras onde o verbete já está. Vazio significa que ele
   * existe e não aparece no catálogo, e a aprovação vai exigir uma escolha.
   */
  categorias: string[];
}

/** Uma linha da fila de moderação. `noDicionario` nulo é termo novo. */
export interface ItemDaFila {
  id: number;
  termo: string;
  idioma: string;
  explicacaoProposta: string;
  criadoEm: string;
  noDicionario: VerbeteExistente | null;
}
