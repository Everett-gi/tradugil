import { useCallback, useEffect, useState } from 'react';
import { ErroDeApi, type ContribuicaoResposta } from '@tradugil/core';
import { cliente } from '../api.js';
import { tokenAtual } from '../sessao.js';

/**
 * Propor uma gíria nova, e acompanhar o que já foi proposto.
 *
 * <h2>O que esta tela precisa deixar claro</h2>
 *
 * Que nada entra no dicionário direto. Sem moderação obrigatória, o campo de
 * contribuição vira um canal aberto para definição ofensiva e desinformação,
 * exibidas com a autoridade de um verbete para um público que inclui pessoas
 * idosas e famílias. O aviso está na tela, e não só na documentação, porque
 * quem escreve precisa saber que uma pessoa vai ler antes.
 *
 * E que a rejeição vem com motivo. O servidor exige motivo para rejeitar
 * justamente para quem contribuiu aprender algo em vez de reenviar a mesma
 * proposta; a tela mostra esse motivo em destaque, que é onde ele serve.
 */
export function Contribuir() {
  const [termo, setTermo] = useState('');
  const [explicacao, setExplicacao] = useState('');
  const [idioma, setIdioma] = useState<'pt-BR' | 'en'>('pt-BR');
  const [minhas, setMinhas] = useState<ContribuicaoResposta[] | null>(null);
  const [erro, setErro] = useState<string | null>(null);
  const [enviando, setEnviando] = useState(false);
  const [enviada, setEnviada] = useState(false);

  const carregar = useCallback(async () => {
    const token = await tokenAtual();
    if (!token) return;
    try {
      setMinhas(await cliente.minhasContribuicoes(token));
    } catch {
      // A lista é secundária: falhar aqui não pode impedir de propor.
      setMinhas([]);
    }
  }, []);

  useEffect(() => {
    void carregar();
  }, [carregar]);

  async function enviar(evento: React.FormEvent) {
    evento.preventDefault();
    setErro(null);
    setEnviada(false);
    setEnviando(true);
    try {
      const token = await tokenAtual();
      if (!token) {
        setErro('Sua sessão expirou. Entre novamente para propor uma gíria.');
        return;
      }
      await cliente.propor(
        { termo: termo.trim(), idioma, explicacaoProposta: explicacao.trim() },
        token,
      );
      setTermo('');
      setExplicacao('');
      setEnviada(true);
      await carregar();
    } catch (e) {
      setErro(
        e instanceof ErroDeApi
          ? e.message
          : 'Não conseguimos enviar agora. Tente de novo em instantes.',
      );
    } finally {
      setEnviando(false);
    }
  }

  return (
    <section className="contribuir">
      <h2>Sugerir uma gíria</h2>
      <p className="dica">
        Conhece um termo que falta aqui? Escreva do jeito que você explicaria
        para alguém da sua família.
      </p>

      <form onSubmit={enviar}>
        <label className="rotulo" htmlFor="contrib-termo">
          A palavra ou expressão
        </label>
        <input
          id="contrib-termo"
          className="campo campo-linha"
          value={termo}
          onChange={(e) => setTermo(e.target.value)}
          maxLength={80}
          required
        />

        <div className="grupo-de-opcoes" role="group" aria-label="Idioma do termo">
          <span className="rotulo-do-grupo">Idioma</span>
          <button
            type="button"
            className="botao"
            aria-pressed={idioma === 'pt-BR'}
            onClick={() => setIdioma('pt-BR')}
          >
            Português
          </button>
          <button
            type="button"
            className="botao"
            aria-pressed={idioma === 'en'}
            onClick={() => setIdioma('en')}
          >
            Inglês
          </button>
        </div>

        <label className="rotulo" htmlFor="contrib-explicacao">
          O que significa
        </label>
        <textarea
          id="contrib-explicacao"
          className="campo"
          value={explicacao}
          onChange={(e) => setExplicacao(e.target.value)}
          minLength={10}
          maxLength={2000}
          required
          placeholder="Exemplo: quando alguém diz algo que dá vergonha alheia."
        />
        <p className="dica">
          Toda sugestão passa por uma pessoa antes de entrar no dicionário.
          Nada é publicado automaticamente.
        </p>

        {erro && (
          <p className="mensagem mensagem-erro" role="alert">
            {erro}
          </p>
        )}
        {enviada && (
          <p className="mensagem" role="status">
            Sugestão enviada. Ela aparece abaixo como <strong>pendente</strong>{' '}
            até alguém da curadoria revisar.
          </p>
        )}

        <button
          type="submit"
          className="botao botao-principal"
          disabled={enviando || !termo.trim() || explicacao.trim().length < 10}
        >
          {enviando ? 'Enviando…' : 'Enviar sugestão'}
        </button>
      </form>

      {minhas && minhas.length > 0 && (
        <>
          <div className="moderacao-cabeca">
            <h3 className="contribuir-subtitulo">Suas sugestões</h3>
            {/*
              A lista só carregava ao abrir a tela, então uma decisão tomada
              enquanto a pessoa estava nela não aparecia: a sugestão ficava
              "em análise" para sempre, mesmo já revisada, e o motivo da
              recusa (que o servidor exige justamente para ela ler) não
              chegava nunca.
            */}
            <button type="button" className="botao" onClick={() => void carregar()}>
              Atualizar
            </button>
          </div>
          <ul className="lista-contribuicoes">
            {minhas.map((c) => (
              <li key={c.id} className="contribuicao">
                <div className="contribuicao-cabeca">
                  <strong>{c.termo}</strong>
                  <span className={`etiqueta etiqueta-${c.status.toLowerCase()}`}>
                    {rotulo(c.status)}
                  </span>
                </div>
                <p className="contribuicao-texto">{c.explicacaoProposta}</p>
                {c.motivoRejeicao && (
                  <p className="contribuicao-motivo">
                    <strong>Motivo:</strong> {c.motivoRejeicao}
                  </p>
                )}
              </li>
            ))}
          </ul>
        </>
      )}
    </section>
  );
}

function rotulo(status: ContribuicaoResposta['status']): string {
  if (status === 'APROVADA') return 'aprovada';
  if (status === 'REJEITADA') return 'não aceita';
  return 'em análise';
}
