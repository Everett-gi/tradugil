import type { GiriaDetectada } from '@tradugil/core';

interface Props {
  texto: string;
  girias: GiriaDetectada[];
  selecionada: number | null;
  aoSelecionar: (indice: number) => void;
}

/**
 * Mostra o texto original com cada gíria marcada e clicável.
 *
 * É a tela da Júlia: colar a conversa inteira e ver de uma vez tudo que não
 * entendeu, sem precisar consultar termo por termo.
 *
 * O recorte usa `posicao` direto, sem ajuste, porque o servidor devolve
 * intervalo semiaberto: a mesma convenção de `slice`. Os testes do
 * tokenizador nos dois lados garantem que isso continue verdade.
 */
export function TextoDestacado({ texto, girias, selecionada, aoSelecionar }: Props) {
  const pedacos: React.ReactNode[] = [];
  let cursor = 0;

  girias.forEach((giria, indice) => {
    const [inicio, fim] = giria.posicao;

    // Defesa contra posição fora do texto: um cliente desatualizado ou uma
    // resposta inesperada não podem quebrar a renderização inteira.
    if (inicio < cursor || fim > texto.length || inicio >= fim) {
      return;
    }

    if (inicio > cursor) {
      pedacos.push(texto.slice(cursor, inicio));
    }

    pedacos.push(
      <button
        key={`${indice}-${inicio}`}
        type="button"
        className="giria-marcada"
        data-risco={giria.riscoMenor}
        aria-expanded={selecionada === indice}
        onClick={() => aoSelecionar(indice)}
      >
        {texto.slice(inicio, fim)}
        <span className="apenas-leitor">
          {' '}:
          gíria{giria.riscoMenor ? ', termo de atenção' : ''}. Toque para ver
          o significado.
        </span>
      </button>,
    );
    cursor = fim;
  });

  if (cursor < texto.length) {
    pedacos.push(texto.slice(cursor));
  }

  return (
    <p className="texto-analisado" lang="pt-BR">
      {pedacos}
    </p>
  );
}
