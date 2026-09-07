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
