import { useEffect, useRef, useState } from 'react';
import type { RespostaDeTraducao } from '@tradugiria/core';
import { traduzir } from './api.js';
import { usePreferencias } from './preferencias.js';
import { BarraDeAcessibilidade } from './componentes/BarraDeAcessibilidade.js';
import { CartaoDaGiria } from './componentes/CartaoDaGiria.js';
import { TextoDestacado } from './componentes/TextoDestacado.js';

type Estado =
  | { fase: 'inicial' }
  | { fase: 'consultando' }
  | { fase: 'pronto'; texto: string; resposta: RespostaDeTraducao; offline: boolean }
  | { fase: 'erro'; mensagem: string };

export function App() {
  const {
    preferencias,
    aumentarFonte,
    diminuirFonte,
    alternarContraste,
    definirNivel,
    alternarModoFamilia,
  } = usePreferencias();

  const [texto, setTexto] = useState('');
  const [estado, setEstado] = useState<Estado>({ fase: 'inicial' });
  const [selecionada, setSelecionada] = useState<number | null>(null);
  const resultadoRef = useRef<HTMLDivElement>(null);

  // Texto vindo da folha de compartilhamento de outro aplicativo (RF20). O
  // PWA instalado no Android aparece em "Compartilhar" e chega aqui.
  useEffect(() => {
    const parametros = new URLSearchParams(window.location.search);
    const compartilhado = parametros.get('text') ?? parametros.get('title');
    if (compartilhado) {
      setTexto(compartilhado);
      // Limpa a URL para o texto compartilhado não ficar no histórico do
      // navegador — pode ser trecho de uma conversa privada.
      window.history.replaceState({}, '', window.location.pathname);
    }
  }, []);

  async function consultar(evento: React.FormEvent) {
    evento.preventDefault();
    const consulta = texto.trim();
    if (!consulta) return;

    setEstado({ fase: 'consultando' });
    setSelecionada(null);
    try {
      const { resposta, offline } = await traduzir({
        texto: consulta,
        nivel: preferencias.nivel,
        modoFamilia: preferencias.modoFamilia,
      });
      setEstado({ fase: 'pronto', texto: consulta, resposta, offline });
    } catch (erro) {
      setEstado({
        fase: 'erro',
        mensagem:
          erro instanceof Error
            ? erro.message
            : 'Não conseguimos consultar agora. Tente novamente em instantes.',
      });
    }
  }

  // Leva o foco ao resultado depois da consulta: sem isso, quem navega por
  // teclado ou leitor de tela continua no botão e não percebe a resposta.
  useEffect(() => {
    if (estado.fase === 'pronto') {
      resultadoRef.current?.focus();
    }
  }, [estado.fase]);

  return (
    <>
      <a className="pular-para-conteudo" href="#conteudo">
        Pular para o conteúdo
      </a>

      <div className="pagina">
        <header className="cabecalho">
          <h1 className="marca">
            <span aria-hidden="true">💬</span> TraduGíria
          </h1>
          <BarraDeAcessibilidade
            preferencias={preferencias}
            aumentarFonte={aumentarFonte}
            diminuirFonte={diminuirFonte}
            alternarContraste={alternarContraste}
          />
        </header>

        <main id="conteudo">
          <p className="subtitulo">
            Não entendeu uma palavra ou uma mensagem inteira? Escreva ou cole
            aqui embaixo que a gente explica em linguagem simples.
          </p>

          <form onSubmit={consultar}>
            <label className="rotulo" htmlFor="campo-texto">
              Palavra ou mensagem
            </label>
            <textarea
              id="campo-texto"
              className="campo"
              value={texto}
              onChange={(e) => setTexto(e.target.value)}
              placeholder="Exemplo: mano, ele clutchou a round, foi mt pog"
              maxLength={5000}
              aria-describedby="dica-privacidade"
            />
            <p className="dica" id="dica-privacidade">
              O que você escreve aqui não é guardado em nenhum lugar.
            </p>

            <div className="grupo-de-opcoes" role="group" aria-label="Como explicar">
              <span className="rotulo-do-grupo" id="rotulo-nivel">
                Nível da explicação
              </span>
              <button
                type="button"
                className="botao"
                aria-pressed={preferencias.nivel === 'SIMPLES'}
                onClick={() => definirNivel('SIMPLES')}
              >
                Simples
              </button>
              <button
                type="button"
                className="botao"
                aria-pressed={preferencias.nivel === 'DETALHADA'}
                onClick={() => definirNivel('DETALHADA')}
              >
                Detalhada, com a origem
              </button>
              <button
                type="button"
                className="botao"
                aria-pressed={preferencias.modoFamilia}
                onClick={alternarModoFamilia}
              >
                Modo Família
              </button>
            </div>

            <button
              type="submit"
              className="botao botao-principal"
              disabled={estado.fase === 'consultando' || !texto.trim()}
            >
              {estado.fase === 'consultando' ? 'Consultando…' : 'Explicar'}
            </button>
          </form>

          {/*
            role="status" faz o leitor de tela anunciar o resultado sem roubar
            o foco de quem está digitando.
          */}
          <div ref={resultadoRef} tabIndex={-1} role="status" aria-live="polite">
            {estado.fase === 'erro' && (
              <p className="mensagem mensagem-erro">{estado.mensagem}</p>
            )}

            {estado.fase === 'pronto' && (
              <>
                {estado.offline && (
                  <p className="aviso-offline">
                    <span aria-hidden="true">📶 </span>
                    {navigator.onLine
                      ? 'Não conseguimos falar com o servidor agora. Mostramos o que já estava salvo no seu aparelho.'
                      : 'Você está sem internet. Mostramos o que já estava salvo no seu aparelho.'}
                  </p>
                )}

                {estado.resposta.girias.length === 0 ? (
                  <p className="mensagem">
                    Não encontramos nenhuma gíria conhecida neste texto. Pode
                    ser que as palavras sejam comuns — ou que ainda não tenhamos
                    esse termo no dicionário.
                  </p>
                ) : (
                  <>
                    <h2>
                      {estado.resposta.girias.length === 1
                        ? 'Encontramos 1 gíria'
                        : `Encontramos ${estado.resposta.girias.length} gírias`}
                    </h2>
                    <TextoDestacado
                      texto={estado.texto}
                      girias={estado.resposta.girias}
                      selecionada={selecionada}
                      aoSelecionar={setSelecionada}
                    />
                    {estado.resposta.girias.map((giria, indice) => (
                      <CartaoDaGiria key={`${giria.termo}-${indice}`} giria={giria} />
                    ))}
                  </>
                )}
              </>
            )}
          </div>
        </main>

        <footer className="rodape">
          <p>
            O TraduGíria não guarda o texto que você consulta. Nada do que
            você escreve aqui fica salvo nos nossos servidores.
          </p>
        </footer>
      </div>
    </>
  );
}
