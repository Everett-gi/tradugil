import type { GiriaDetectada } from '@tradugil/core';
import { falar, vozDisponivel } from '../voz.js';

interface Props {
  giria: GiriaDetectada;
}

/**
 * A explicação de um termo.
 *
 * Três rótulos são obrigatórios aqui, e nenhum é decoração:
 *
 * - Origem IA: a seção 7.1 exige que toda resposta gerada por IA apareça
 *   marcada como não verificada. Sem isso, um palpite do modelo passaria por
 *   verbete revisado.
 * - Termo de atenção: é o alerta do Modo Família que o Roberto precisa ver.
 * - Origem externa: fonte de terceiros pode ter definição de baixa qualidade.
 */
export function CartaoDaGiria({ giria }: Props) {
  const podeFalar = vozDisponivel();
  const textoParaOuvir = [
    giria.termo,
    giria.explicacao,
    giria.equivalenteFormal ? `Em linguagem formal: ${giria.equivalenteFormal}.` : '',
  ]
    .filter(Boolean)
    .join('. ');

  return (
    <article className="cartao" aria-label={`Significado de ${giria.termo}`}>
      <h3 className="cartao-titulo">
        <span>{giria.termo}</span>
        {podeFalar && (
          <button
            type="button"
            className="botao"
            onClick={() => falar(textoParaOuvir)}
            aria-label={`Ouvir a explicação de ${giria.termo}`}
          >
            <span aria-hidden="true">🔊</span> Ouvir
          </button>
        )}
      </h3>

      {giria.origem === 'IA' && (
        <p>
          <span className="etiqueta etiqueta-ia">
            Explicação gerada por inteligência artificial: ainda não conferida
            por uma pessoa
          </span>
        </p>
      )}

      {giria.origem === 'EXTERNA' && (
        <p>
          <span className="etiqueta">Explicação vinda de outra fonte da internet</span>
        </p>
      )}

      <p className="explicacao">{giria.explicacao ?? 'Ainda não temos uma explicação para este termo.'}</p>

      {giria.equivalenteFormal && (
        <p className="equivalente">
          Em outras palavras: <strong>{giria.equivalenteFormal}</strong>
        </p>
      )}

      {giria.riscoMenor && (
        <p className="aviso-risco">
          <span aria-hidden="true">⚠️ </span>
          Este termo costuma aparecer em conversas que merecem atenção. Se você
          acompanha o uso de redes de um adolescente, pode valer uma conversa
          sobre o assunto.
        </p>
      )}
    </article>
  );
}
