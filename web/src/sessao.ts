import { useCallback, useEffect, useState } from 'react';
import { ErroDeApi, Sessao, type Credenciais, type Papel } from '@tradugil/core';
import { cliente } from './api.js';

/**
 * A sessão do site, e o gancho que a expõe ao React.
 *
 * A instância é única e vive fora do React de propósito. Ela guarda o access
 * token em memória e precisa sobreviver a remontagem de componente: presa a
 * um `useState`, um refresh acidental do componente descartaria o token e
 * dispararia uma renovação sem necessidade, que é justamente o que a
 * detecção de reuso do servidor interpreta como roubo.
 */
const sessao = new Sessao((refreshToken) => cliente.renovar(refreshToken));

/** Quem quer saber quando a sessão muda. O Sessao não conhece React. */
const ouvintes = new Set<() => void>();
function avisar() {
  for (const o of ouvintes) o();
}

export interface EstadoDaConta {
  autenticado: boolean;
  papel: Papel | null;
  podeModerar: boolean;
}

function instantaneo(): EstadoDaConta {
  return {
    autenticado: sessao.autenticado,
    papel: sessao.papelAtual,
    podeModerar: sessao.podeModerar,
  };
}

export function useConta() {
  const [estado, setEstado] = useState<EstadoDaConta>(instantaneo);
  const [erro, setErro] = useState<string | null>(null);
  const [ocupado, setOcupado] = useState(false);

  useEffect(() => {
    const ouvinte = () => setEstado(instantaneo());
    ouvintes.add(ouvinte);
    return () => {
      ouvintes.delete(ouvinte);
    };
  }, []);

  const executar = useCallback(
    async (acao: () => Promise<void>) => {
      setErro(null);
      setOcupado(true);
      try {
        await acao();
      } catch (e) {
        /*
         * A mensagem vem do servidor sempre que ele mandou uma. Ele é a
         * única parte do sistema que sabe por que recusou, e reescrever isso
         * aqui produziria duas versões da mesma regra divergindo com o
         * tempo. As frases da API já são escritas para o usuário final.
         */
        setErro(
          e instanceof ErroDeApi
            ? e.message
            : 'Não conseguimos falar com o servidor. Tente de novo em instantes.',
        );
        throw e;
      } finally {
        setOcupado(false);
      }
    },
    [],
  );

  const entrar = useCallback(
    (credenciais: Credenciais) =>
      executar(async () => {
        sessao.guardar(await cliente.entrar(credenciais));
        avisar();
      }),
    [executar],
  );

  const registrar = useCallback(
    (credenciais: Credenciais) =>
      executar(async () => {
        sessao.guardar(await cliente.registrar(credenciais));
        avisar();
      }),
    [executar],
  );

  const sair = useCallback(async () => {
    const token = sessao.tokenDeRenovacao;
    // Limpa primeiro, avisa o servidor depois. Se a rede falhar, a pessoa
    // continua tendo saído desta máquina, que é o que ela pediu.
    sessao.encerrar();
    avisar();
    if (token) {
      try {
        await cliente.sair(token);
      } catch {
        // O token expira sozinho em 30 dias. Falhar aqui não é motivo para
        // mostrar erro a quem já saiu.
      }
    }
  }, []);

  return { ...estado, erro, ocupado, entrar, registrar, sair, limparErro: () => setErro(null) };
}

/**
 * Um access token válido, ou nulo se a sessão acabou.
 *
 * Todo componente que chama endpoint autenticado passa por aqui, em vez de
 * guardar o token: é este método que renova quando venceu, e uma cópia
 * guardada em outro lugar estaria velha.
 */
export async function tokenAtual(): Promise<string | null> {
  const token = await sessao.token();
  if (!token) avisar();
  return token;
}
