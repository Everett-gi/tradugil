import Dexie, { type EntityTable } from 'dexie';
import {
  candidatos,
  colapsarRepeticoes,
  normalizar,
  tokenizar,
  type GiriaDetectada,
  type RespostaDeTraducao,
} from '@tradugiria/core';

/**
 * Nível 0 da cascata: o dicionário que vive no dispositivo.
 *
 * É o que faz o RNF07 valer — a consulta funciona sem conexão, e a Marlene
 * não gasta o plano de dados para entender uma mensagem. Também é o caminho
 * mais rápido: resolver aqui evita a ida à rede por completo.
 *
 * A detecção reusa `tokenizar` e `candidatos` do pacote compartilhado, de
 * modo que o texto é quebrado exatamente como o servidor quebraria. Se esta
 * função divergisse da do servidor, o mesmo texto produziria destaques em
 * posições diferentes conforme houvesse ou não internet.
 */

interface VerbeteLocal {
  termoNormalizado: string;
  termo: string;
  idioma: string;
  explicacaoSimples: string;
  explicacaoDetalhada: string | null;
  equivalenteFormal: string | null;
  nsfw: boolean;
  riscoMenor: boolean;
  /** Variações normalizadas que apontam para este mesmo verbete. */
  variacoes: string[];
}

const bancoLocal = new Dexie('tradugiria') as Dexie & {
  verbetes: EntityTable<VerbeteLocal, 'termoNormalizado'>;
};

bancoLocal.version(1).stores({
  // O índice multiEntry em `variacoes` permite achar o verbete por qualquer
  // apelido sem varrer a tabela inteira.
  verbetes: 'termoNormalizado, idioma, *variacoes',
});

export async function guardarVerbetes(verbetes: VerbeteLocal[]): Promise<void> {
  await bancoLocal.verbetes.bulkPut(verbetes);
}

export async function quantidadeGuardada(): Promise<number> {
  return bancoLocal.verbetes.count();
}

async function procurar(chave: string): Promise<VerbeteLocal | undefined> {
  const direto = await bancoLocal.verbetes.get(chave);
  if (direto) return direto;

  const porVariacao = await bancoLocal.verbetes.where('variacoes').equals(chave).first();
  if (porVariacao) return porVariacao;

  const colapsado = colapsarRepeticoes(chave);
  if (colapsado !== chave) {
    return bancoLocal.verbetes.get(colapsado);
  }
  return undefined;
}

/**
 * Traduz usando apenas o que está no dispositivo.
 *
 * Reproduz o casamento guloso do servidor: candidatos mais longos primeiro,
 * e nenhum caractere reclamado duas vezes — sem isso, "dar ruim" viria
 * acompanhado de "ruim" sozinho e a tela desenharia destaques sobrepostos.
 */
export async function traduzirLocalmente(
  texto: string,
  opcoes: { nivel: 'SIMPLES' | 'DETALHADA'; modoFamilia: boolean },
): Promise<RespostaDeTraducao> {
  const lista = candidatos(tokenizar(texto));
  const detectadas: GiriaDetectada[] = [];
  const naoResolvidos = new Set<string>();
  const ocupado = new Array<boolean>(texto.length).fill(false);

  for (const candidato of lista) {
    if (intervaloOcupado(ocupado, candidato.inicio, candidato.fim)) continue;

    const verbete = await procurar(candidato.normalizado);
    if (!verbete) {
      if (!candidato.normalizado.includes(' ') && candidato.normalizado.length >= 3) {
        naoResolvidos.add(candidato.normalizado);
      }
      continue;
    }

    if (opcoes.modoFamilia && verbete.nsfw) {
      marcarOcupado(ocupado, candidato.inicio, candidato.fim);
      continue;
    }

    detectadas.push({
      termo: verbete.termo,
      posicao: [candidato.inicio, candidato.fim],
      explicacao:
        opcoes.nivel === 'DETALHADA' && verbete.explicacaoDetalhada
          ? verbete.explicacaoDetalhada
          : verbete.explicacaoSimples,
      equivalenteFormal: verbete.equivalenteFormal,
      nsfw: verbete.nsfw,
      riscoMenor: verbete.riscoMenor,
      confianca: 1,
      origem: 'DICIONARIO',
    });
    marcarOcupado(ocupado, candidato.inicio, candidato.fim);
  }

  detectadas.sort((a, b) => a.posicao[0] - b.posicao[0]);

  return {
    idiomaDetectado: null,
    girias: detectadas,
    geradoPorIa: false,
    naoResolvidos: [...naoResolvidos],
  };
}

/**
 * Guarda no dispositivo o que o servidor acabou de resolver.
 *
 * É o que faz o dicionário local crescer com o uso: os termos que a pessoa
 * realmente consulta ficam disponíveis offline na próxima vez, sem precisar
 * baixar o pacote inteiro.
 */
export async function memorizar(resposta: RespostaDeTraducao): Promise<void> {
  const novos: VerbeteLocal[] = resposta.girias
    .filter((g) => g.origem === 'DICIONARIO' && g.explicacao !== null)
    .map((g) => ({
      termoNormalizado: normalizar(g.termo),
      termo: g.termo,
      idioma: resposta.idiomaDetectado ?? 'pt-BR',
      explicacaoSimples: g.explicacao!,
      explicacaoDetalhada: null,
      equivalenteFormal: g.equivalenteFormal,
      nsfw: g.nsfw,
      riscoMenor: g.riscoMenor,
      variacoes: [],
    }));

  if (novos.length > 0) {
    await guardarVerbetes(novos);
  }
}

function intervaloOcupado(ocupado: boolean[], inicio: number, fim: number): boolean {
  for (let i = inicio; i < fim; i++) {
    if (ocupado[i]) return true;
  }
  return false;
}

function marcarOcupado(ocupado: boolean[], inicio: number, fim: number): void {
  for (let i = inicio; i < fim; i++) {
    ocupado[i] = true;
  }
}
