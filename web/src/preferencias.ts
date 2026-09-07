import { useCallback, useEffect, useState } from 'react';
import type { NivelDeExplicacao } from '@tradugil/core';

/**
 * Preferências do usuário. Ficam apenas no dispositivo — nunca sobem para o
 * servidor. Segundo o mapeamento LGPD da seção 8.2, acessibilidade e Modo
 * Família não têm base legal de tratamento porque simplesmente não são
 * tratados: são configuração local, sob controle de quem usa.
 */

export type Contraste = 'normal' | 'alto';

export interface Preferencias {
  escala: number;
  contraste: Contraste;
  nivel: NivelDeExplicacao;
  modoFamilia: boolean;
}

const CHAVE = 'tradugil.preferencias';

export const ESCALA_MINIMA = 1;
export const ESCALA_MAXIMA = 1.6;
const PASSO_DA_ESCALA = 0.15;

const PADRAO: Preferencias = {
  escala: 1,
  contraste: 'normal',
  nivel: 'SIMPLES',
  // Ligado por padrão: a escolha protege o caso em que errar custa caro —
  // uma criança ou a Marlene recebendo conteúdo impróprio — em vez do caso
  // em que errar apenas incomoda.
  modoFamilia: true,
};

function carregar(): Preferencias {
  try {
    const bruto = localStorage.getItem(CHAVE);
    if (!bruto) return PADRAO;
    const salvo = JSON.parse(bruto) as Partial<Preferencias>;
    return {
      escala: clamp(salvo.escala ?? PADRAO.escala, ESCALA_MINIMA, ESCALA_MAXIMA),
      contraste: salvo.contraste === 'alto' ? 'alto' : 'normal',
      nivel: salvo.nivel === 'DETALHADA' ? 'DETALHADA' : 'SIMPLES',
      modoFamilia: salvo.modoFamilia ?? PADRAO.modoFamilia,
    };
  } catch {
    // Modo privativo, armazenamento bloqueado ou JSON corrompido. A tela
    // precisa abrir de qualquer jeito — perder a preferência é um incômodo,
    // não abrir é uma falha.
    return PADRAO;
  }
}

function clamp(valor: number, minimo: number, maximo: number): number {
  return Math.min(Math.max(valor, minimo), maximo);
}

export function usePreferencias() {
  const [preferencias, definir] = useState<Preferencias>(carregar);

  useEffect(() => {
    try {
      localStorage.setItem(CHAVE, JSON.stringify(preferencias));
    } catch {
      // Sem armazenamento a preferência vale só para esta sessão.
    }
    const raiz = document.documentElement;
    raiz.style.setProperty('--escala', String(preferencias.escala));
    raiz.dataset.contraste = preferencias.contraste;
  }, [preferencias]);

  const aumentarFonte = useCallback(() => {
    definir((p) => ({
      ...p,
      escala: clamp(
        Number((p.escala + PASSO_DA_ESCALA).toFixed(2)),
        ESCALA_MINIMA,
        ESCALA_MAXIMA,
      ),
    }));
  }, []);

  const diminuirFonte = useCallback(() => {
    definir((p) => ({
      ...p,
      escala: clamp(
        Number((p.escala - PASSO_DA_ESCALA).toFixed(2)),
        ESCALA_MINIMA,
        ESCALA_MAXIMA,
      ),
    }));
  }, []);

  const alternarContraste = useCallback(() => {
    definir((p) => ({ ...p, contraste: p.contraste === 'alto' ? 'normal' : 'alto' }));
  }, []);

  const definirNivel = useCallback((nivel: NivelDeExplicacao) => {
    definir((p) => ({ ...p, nivel }));
  }, []);

  const alternarModoFamilia = useCallback(() => {
    definir((p) => ({ ...p, modoFamilia: !p.modoFamilia }));
  }, []);

  return {
    preferencias,
    aumentarFonte,
    diminuirFonte,
    alternarContraste,
    definirNivel,
    alternarModoFamilia,
  };
}
