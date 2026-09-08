import { useCallback, useEffect, useState } from 'react';
import { ErroDeApi, type CategoriaResumo, type ItemDaFila } from '@tradugil/core';
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
 * Por isso quatro escolhas de interface:
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
 *
 * **O que o dicionário já diz aparece junto.** Antes a tela mostrava termo,
 * idioma e o texto proposto, e nada mais. Quem modera não tinha como saber
 * que o verbete já existia com três sentidos, um deles dizendo quase a mesma
 * coisa, sem abrir outra aba e procurar. Foi assim que o dicionário ganhou
 * explicações repetidas, que precisaram de duas migrações para limpar.
 *
 * **A prateleira do catálogo é escolhida aqui.** Antes o verbete da
 * comunidade nascia sem categoria: era encontrado pela busca e nunca aparecia
 * no catálogo, que é por onde chega quem não sabe o que procurar.
 */
export function Moderacao() {
  const [fila, setFila] = useState<ItemDaFila[] | null>(null);
  const [prateleiras, setPrateleiras] = useState<CategoriaResumo[]>([]);
  const [erro, setErro] = useState<string | null>(null);
  const [motivos, setMotivos] = useState<Record<number, string>>({});
  const [escolhas, setEscolhas] = useState<Record<number, string>>({});
  const [ocupado, setOcupado] = useState<number | null>(null);

  const carregar = useCallback(async () => {
    setErro(null);
    const token = await tokenAtual();
    if (!token) {
      setErro('Sua sessão expirou. Entre novamente.');
      return;
    }
    try {
      /*
       * As prateleiras vêm com modoFamilia desligado de propósito. Aqui a
       * lista serve para classificar, não para ler: uma categoria só de
       * verbetes impróprios sumiria da lista e o moderador ficaria sem para
       * onde mandar exatamente o termo que mais precisa ir para lá.
       */
      const [itens, categorias] = await Promise.all([
        cliente.filaDeModeracao(token),
        cliente.categorias(false),
      ]);
      setFila(itens);
      setPrateleiras(categorias);
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
      await cliente.decidir(
        id,
        { aprovar, motivo: motivos[id], categoria: escolhas[id] },
        token,
      );
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
            const jaTemPrateleira = (c.noDicionario?.categorias.length ?? 0) > 0;
            const escolhida = escolhas[c.id] ?? '';
            // O servidor recusa a aprovação que deixaria o verbete fora do
            // catálogo. Espelhar a regra aqui evita que a pessoa descubra
            // isso por mensagem de erro depois de clicar.
            const podeAprovar = jaTemPrateleira || escolhida.length > 0;

            return (
              <li key={c.id} className="contribuicao contribuicao-fila">
                <div className="contribuicao-cabeca">
                  <strong>{c.termo}</strong>
                  <span className="etiqueta">{c.idioma}</span>
                  <span
                    className={
                      c.noDicionario ? 'etiqueta etiqueta-existe' : 'etiqueta etiqueta-novo'
                    }
                  >
                    {c.noDicionario ? 'já no dicionário' : 'termo novo'}
                  </span>
                </div>
                <p className="contribuicao-texto">{c.explicacaoProposta}</p>

                {c.noDicionario && c.noDicionario.sentidos.length > 0 && (
                  <details className="ja-existe">
                    <summary>
                      O que o dicionário já diz ({c.noDicionario.sentidos.length}
                      {c.noDicionario.sentidos.length === 1
                        ? ' sentido'
                        : ' sentidos'}
                      )
                    </summary>
                    <ul className="ja-existe-lista">
                      {c.noDicionario.sentidos.map((sentido) => (
                        <li key={sentido}>{sentido}</li>
                      ))}
                    </ul>
                    <p className="dica">
                      Se a proposta repete um destes, recuse: verbete que mostra
                      a mesma frase duas vezes confunde quem consulta.
                    </p>
                  </details>
                )}

                <div className="prateleira">
                  <label className="rotulo" htmlFor={`prateleira-${c.id}`}>
                    {jaTemPrateleira
                      ? 'Prateleira do catálogo (opcional, já está em ' +
                        c.noDicionario!.categorias.join(', ') +
                        ')'
                      : 'Prateleira do catálogo'}
                  </label>
                  <select
                    id={`prateleira-${c.id}`}
                    className="campo campo-linha"
                    value={escolhida}
                    onChange={(e) =>
                      setEscolhas((p) => ({ ...p, [c.id]: e.target.value }))
                    }
                  >
                    <option value="">
                      {jaTemPrateleira ? 'Manter como está' : 'Escolha uma'}
                    </option>
                    {prateleiras.map((p) => (
                      <option key={p.slug} value={p.slug}>
                        {p.nome}
                      </option>
                    ))}
                  </select>
                  {!jaTemPrateleira && (
                    <p className="dica">
                      Sem prateleira o verbete é encontrado na busca e não
                      aparece no catálogo, que é por onde chega quem não sabe o
                      que procurar.
                    </p>
                  )}
                </div>

                <button
                  type="button"
                  className="botao botao-aprovar"
                  disabled={trabalhando || !podeAprovar}
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
