import { ESCALA_MAXIMA, ESCALA_MINIMA, type Preferencias } from '../preferencias.js';

interface Props {
  preferencias: Preferencias;
  aumentarFonte: () => void;
  diminuirFonte: () => void;
  alternarContraste: () => void;
}

/**
 * Controles de acessibilidade sempre visíveis, no topo.
 *
 * Ficam fora de menu de propósito: escondê-los atrás de "Configurações"
 * deixaria a função inalcançável justamente para quem mais precisa dela —
 * quem não enxerga bem o suficiente para achar o menu.
 */
export function BarraDeAcessibilidade({
  preferencias,
  aumentarFonte,
  diminuirFonte,
  alternarContraste,
}: Props) {
  const noMaximo = preferencias.escala >= ESCALA_MAXIMA;
  const noMinimo = preferencias.escala <= ESCALA_MINIMA;

  return (
    <div className="barra-acessibilidade" role="group" aria-label="Acessibilidade">
      <button
        type="button"
        className="botao"
        onClick={diminuirFonte}
        disabled={noMinimo}
        aria-label="Diminuir o tamanho da letra"
      >
        A<span aria-hidden="true">−</span>
      </button>
      <button
        type="button"
        className="botao"
        onClick={aumentarFonte}
        disabled={noMaximo}
        aria-label="Aumentar o tamanho da letra"
      >
        A<span aria-hidden="true">+</span>
      </button>
      <button
        type="button"
        className="botao"
        onClick={alternarContraste}
        aria-pressed={preferencias.contraste === 'alto'}
      >
        Alto contraste
      </button>
      {/*
        Mudanças de escala e contraste são visuais e não movem o foco, então
        leitores de tela não anunciariam nada. Este aviso educado resolve.
      */}
      <p className="apenas-leitor" role="status">
        Letra em {Math.round(preferencias.escala * 100)} por cento. Alto
        contraste {preferencias.contraste === 'alto' ? 'ligado' : 'desligado'}.
      </p>
    </div>
  );
}
