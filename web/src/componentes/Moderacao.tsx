import { useCallback, useEffect, useState } from 'react';
import { ErroDeApi, type ContribuicaoResposta } from '@tradugil/core';
import { cliente } from '../api.js';
import { tokenAtual } from '../sessao.js';

/**
 * A fila de moderação.
 *
 * <h2>O que esta tela carrega de responsabilidade</h2>
 *
 * É o único lugar do produto onde alguém decide o que vira verdade para
 * quem consulta. Um verbete aprovado aqui aparece com a mesma autoridade dos
 * mil que a curadoria escreveu, para um público que inclui pessoas idosas e
 * famílias e que não tem como saber a diferença.
 *
 * Por isso duas escolhas de interface:
 *
 * O botão de rejeitar **exige motivo antes de habilitar**. O servidor já
 * recusa rejeição sem motivo, mas descobrir isso por mensagem de erro depois
 * de clicar treina a pessoa a escrever qualquer coisa para passar. Pedir
 * antes deixa claro que o motivo é para o autor ler, e não burocracia.
 *
 * Aprovar e rejeitar ficam **visualmente distantes e diferentes**, e não como
 * dois botões iguais lado a lado. É uma tela de decisão irreversível operada
 * em sequência, dezenas de vezes seguidas, e é assim que se clica no botão
 * errado por automatismo.
 */
export function Moderacao() {
  const [fila, setFila] = useState<ContribuicaoResposta[] | null>(null);
  const [erro, setErro] = useState<string | null>(null);
  const [motivos, setMotivos] = useState<Record<number, string>>({});
  const [ocupado, setOcupado] = useState<number | null>(null);

  const carregar = useCallback(async () => {
    setErro(null);
    const token = await tokenAtual();
    if (!token) {
      setErro('Sua sessão expirou. Entre novamente.');
      return;
    }
    try {
      setFila(await cliente.filaDeModeracao(token));
    } catch (e) {
      setErro(
        e instanceof ErroDeApi
          ? e.message
          : 'Não conseguimos carregar a fila agora.',
      );
      setFila([]);
    }
  }, []);

  useEffect(() => {
    void carregar();
  }, [carregar]);

  async function decidir(id: number, aprovar: boolean) {
    setErro(null);
    setOcupado(id);
    try {
      const token = await tokenAtual();
      if (!token) {
        setErro('Sua sessão expirou. Entre novamente.');
        return;
      }
      await cliente.decidir(id, { aprovar, motivo: motivos[id] }, token);
      // Some da lista local na hora, em vez de recarregar tudo: a fila pode
      // ser longa e recarregar faria a próxima linha pular sob o cursor.
      setFila((atual) => (atual ?? []).filter((c) => c.id !== id));
    } catch (e) {
      setErro(
        e instanceof ErroDeApi ? e.message : 'Não conseguimos registrar a decisão.',
      );
    } finally {
      setOcupado(null);
    }
  }

  if (!fila) {
    return (
      <section className="moderacao">
        <h2>Fila de moderação</h2>
        <p className="dica">Carregando…</p>
      </section>
    );
  }

  return (
    <section className="moderacao">
      <div className="moderacao-cabeca">
        <h2>Fila de moderação</h2>
        {/*
          A fila só carregava ao abrir a tela. Quem modera fica nela por
          muito tempo, e proposta nova chegando não aparecia: a pessoa
          continuaria vendo uma fila vazia enquanto a fila enche.

          Um botão, e não recarga automática: a lista some sob o cursor
          quando se atualiza sozinha, e aqui cada linha tem um campo de
          motivo que pode estar meio escrito.
        */}
        <button type="button" className="botao" onClick={() => void carregar()}>
          Atualizar
        </button>
      </div>
      <p className="dica">
        Toda decisão fica registrada com o seu nome, e o registro não pode ser
        apagado nem alterado depois.
      </p>

      {erro && (
        <p className="mensagem mensagem-erro" role="alert">
          {erro}
        </p>
      )}

      {fila.length === 0 ? (
        <p className="mensagem">Nada pendente por enquanto.</p>
      ) : (
        <ul className="lista-contribuicoes">
          {fila.map((c) => {
            const motivo = (motivos[c.id] ?? '').trim();
            const trabalhando = ocupado === c.id;
            return (
              <li key={c.id} className="contribuicao contribuicao-fila">
                <div className="contribuicao-cabeca">
                  <strong>{c.termo}</strong>
                  <span className="etiqueta">{c.idioma}</span>
                </div>
                <p className="contribuicao-texto">{c.explicacaoProposta}</p>

                <button
                  type="button"
                  className="botao botao-aprovar"
                  disabled={trabalhando}
                  onClick={() => void decidir(c.id, true)}
                >
                  Aprovar e publicar
                </button>

                <div className="rejeitar">
                  <label className="rotulo" htmlFor={`motivo-${c.id}`}>
                    Para recusar, explique o motivo
                  </label>
                  <input
                    id={`motivo-${c.id}`}
                    className="campo campo-linha"
                    value={motivos[c.id] ?? ''}
                    maxLength={200}
                    placeholder="Quem escreveu vai ler isto."
                    onChange={(e) =>
                      setMotivos((m) => ({ ...m, [c.id]: e.target.value }))
                    }
                  />
                  <button
                    type="button"
                    className="botao botao-rejeitar"
                    disabled={trabalhando || motivo.length === 0}
                    onClick={() => void decidir(c.id, false)}
                  >
                    Recusar
                  </button>
                </div>
              </li>
            );
          })}
        </ul>
      )}
    </section>
  );
}
