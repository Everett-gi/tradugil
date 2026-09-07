import { useCallback, useEffect, useRef, useState } from 'react';
import type {
  CategoriaResumo,
  GiriaCompleta,
  GiriaResumo,
  NivelDeExplicacao,
} from '@tradugil/core';
import { cliente } from '../api.js';
import { falar, vozDisponivel } from '../voz.js';
import { IconeAtencao, IconeSeta, IconeSom } from './Icone.js';

/**
 * O catálogo: uma janela para folhear o dicionário por assunto.
 *
 * Existe para um caso que a busca não atende. A tela principal parte de
 * alguém que já viu a palavra e quer saber o que ela significa. Quem só ouviu
 * dizer que os filhos "falam outra língua" não tem palavra nenhuma para
 * digitar, e diante de um campo em branco vai embora. O catálogo dá o
 * primeiro passo pronto: escolha um assunto, veja o que existe ali.
 *
 * Usa o {@code <dialog>} nativo em vez de uma div com position: fixed. Não é
 * economia de código: é o navegador quem passa a prender o foco dentro da
 * janela, devolver o foco ao botão que a abriu, fechar no Esc e esconder o
 * resto da página dos leitores de tela. Reimplementar isso à mão é onde
 * modal costuma quebrar para quem navega por teclado.
 */

interface Props {
  modoFamilia: boolean;
  /**
   * O mesmo botao "Detalhada, com a origem" da tela principal. O catalogo
   * respeita a escolha em vez de sempre mostrar a origem: quem marcou
   * "Simples" pediu menos texto, e uma tela que ignora isso torna o botao
   * mentiroso.
   */
  nivel: NivelDeExplicacao;
}

/** Quantos termos por vez. Página grande demais some com o "ver mais". */
const TAMANHO_DA_PAGINA = 30;

type EstadoDaLista =
  | { fase: 'nenhuma' }
  | { fase: 'carregando'; slug: string }
  | { fase: 'pronta'; slug: string; itens: GiriaResumo[]; temMais: boolean }
  | { fase: 'erro'; mensagem: string };

export function Catalogo({ modoFamilia, nivel }: Props) {
  const janela = useRef<HTMLDialogElement>(null);
  const [aberto, setAberto] = useState(false);
  const [prateleiras, setPrateleiras] = useState<CategoriaResumo[] | null>(null);
  const [erroDasPrateleiras, setErroDasPrateleiras] = useState<string | null>(null);
  const [lista, setLista] = useState<EstadoDaLista>({ fase: 'nenhuma' });
  const [expandido, setExpandido] = useState<string | null>(null);

  /* ------------------------------------------------------------ abrir --- */

  function abrir() {
    setAberto(true);
    janela.current?.showModal();
  }

  function fechar() {
    janela.current?.close();
  }

  // O <dialog> fecha sozinho no Esc e no clique fora, sem passar por
  // fechar(). Ouvir o evento é o que mantém o estado do React sabendo disso.
  useEffect(() => {
    const elemento = janela.current;
    if (!elemento) return;
    const aoFechar = () => setAberto(false);
    elemento.addEventListener('close', aoFechar);
    return () => elemento.removeEventListener('close', aoFechar);
  }, []);

  /* ------------------------------------------------------- prateleiras --- */

  useEffect(() => {
    if (!aberto) return;

    let cancelado = false;
    setErroDasPrateleiras(null);
    cliente
      .categorias(modoFamilia)
      .then((achadas) => {
        if (!cancelado) setPrateleiras(achadas);
      })
      .catch(() => {
        if (!cancelado) {
          setErroDasPrateleiras(
            'Não conseguimos carregar os assuntos agora. O catálogo precisa de internet; a consulta por texto continua funcionando sem ela.',
          );
        }
      });
    return () => {
      cancelado = true;
    };
    // O modo família muda a lista e a contagem de cada prateleira, então
    // recarrega: manter a lista antiga mostraria números que não batem mais.
  }, [aberto, modoFamilia]);

  /* ------------------------------------------------------------ termos --- */

  const abrirPrateleira = useCallback(
    async (slug: string, pagina: number) => {
      if (pagina === 0) {
        setLista({ fase: 'carregando', slug });
        setExpandido(null);
      }
      try {
        const resultado = await cliente.buscar('', {
          categoria: slug,
          modoFamilia,
          pagina,
          tamanho: TAMANHO_DA_PAGINA,
        });
        setLista((anterior) => ({
          fase: 'pronta',
          slug,
          itens:
            pagina > 0 && anterior.fase === 'pronta' && anterior.slug === slug
              ? [...anterior.itens, ...resultado.itens]
              : resultado.itens,
          temMais: resultado.temMais,
        }));
      } catch {
        setLista({
          fase: 'erro',
          mensagem: 'Não conseguimos abrir este assunto agora. Tente de novo em instantes.',
        });
      }
    },
    [modoFamilia],
  );

  // Trocar o modo família com uma prateleira aberta muda o que pode aparecer
  // nela. Sem isto, o conteúdo filtrado continuaria na tela depois de a
  // pessoa ligar o modo família, que é justamente quando ela não deve ver.
  useEffect(() => {
    if (lista.fase === 'pronta' || lista.fase === 'carregando') {
      void abrirPrateleira(lista.slug, 0);
    }
    // A dependência é só o modo família, de propósito. Incluir `lista` aqui
    // faria a lista se recarregar a cada carregamento dela mesma, sem parar.
  }, [modoFamilia]);

  return (
    <>
      <button type="button" className="botao botao-catalogo" onClick={abrir}>
        Ver o catálogo de gírias
      </button>

      <dialog ref={janela} className="catalogo" aria-labelledby="titulo-do-catalogo">
        <div className="catalogo-cabecalho">
          <h2 id="titulo-do-catalogo">Catálogo de gírias</h2>
          <button
            type="button"
            className="botao"
            onClick={fechar}
            aria-label="Fechar o catálogo"
          >
            Fechar
          </button>
        </div>

        <p className="catalogo-explicacao">
          Escolha um assunto e toque numa palavra para ver o que ela significa.
        </p>

        {erroDasPrateleiras && (
          <p className="mensagem mensagem-erro">{erroDasPrateleiras}</p>
        )}

        {prateleiras && (
          <div className="catalogo-assuntos" role="group" aria-label="Assuntos">
            {prateleiras.map((prateleira) => (
              <button
                key={prateleira.slug}
                type="button"
                className="botao chip"
                aria-pressed={
                  lista.fase !== 'nenhuma' &&
                  lista.fase !== 'erro' &&
                  lista.slug === prateleira.slug
                }
                onClick={() => void abrirPrateleira(prateleira.slug, 0)}
              >
                {prateleira.nome}{' '}
                <span className="chip-contagem">{prateleira.quantidade}</span>
              </button>
            ))}
          </div>
        )}

        <div className="catalogo-conteudo" aria-live="polite">
          {lista.fase === 'nenhuma' && !erroDasPrateleiras && (
            <p className="dica">
              {prateleiras
                ? 'Nenhum assunto escolhido ainda.'
                : 'Carregando os assuntos…'}
            </p>
          )}

          {lista.fase === 'carregando' && <p className="dica">Abrindo…</p>}

          {lista.fase === 'erro' && (
            <p className="mensagem mensagem-erro">{lista.mensagem}</p>
          )}

          {lista.fase === 'pronta' && (
            <>
              {lista.itens.length === 0 ? (
                <p className="dica">Nada para mostrar neste assunto.</p>
              ) : (
                <ul className="catalogo-lista">
                  {lista.itens.map((giria) => (
                    <ItemDoCatalogo
                      key={giria.id}
                      giria={giria}
                      nivel={nivel}
                      expandido={expandido === giria.termo}
                      aoAlternar={() =>
                        setExpandido((atual) =>
                          atual === giria.termo ? null : giria.termo,
                        )
                      }
                    />
                  ))}
                </ul>
              )}

              {lista.temMais && (
                <button
                  type="button"
                  className="botao"
                  onClick={() =>
                    void abrirPrateleira(
                      lista.slug,
                      Math.ceil(lista.itens.length / TAMANHO_DA_PAGINA),
                    )
                  }
                >
                  Ver mais
                </button>
              )}
            </>
          )}
        </div>
      </dialog>
    </>
  );
}

/* -------------------------------------------------------------- item ----- */

interface PropsDoItem {
  giria: GiriaResumo;
  nivel: NivelDeExplicacao;
  expandido: boolean;
  aoAlternar: () => void;
}

/**
 * Uma linha do catálogo, que abre para mostrar o significado.
 *
 * A explicação curta já veio junto com a lista, então ela aparece na hora,
 * sem esperar nada. O verbete completo (origem da expressão, exemplos,
 * variações de escrita) é buscado depois, em segundo plano, e substitui o
 * que estava ali quando chega. Quem só queria a resposta rápida já a tem; a
 * demora do detalhe não fica no caminho.
 */
function ItemDoCatalogo({ giria, nivel, expandido, aoAlternar }: PropsDoItem) {
  const [completo, setCompleto] = useState<GiriaCompleta | null>(null);
  const podeFalar = vozDisponivel();

  useEffect(() => {
    if (!expandido || completo) return;
    let cancelado = false;
    cliente
      .verbete(giria.termo, giria.idioma)
      .then((verbete) => {
        if (!cancelado) setCompleto(verbete);
      })
      .catch(() => {
        // Silencioso de propósito: a explicação curta já está na tela. Um
        // aviso de erro aqui diria que algo falhou quando a pessoa já está
        // lendo a resposta que veio.
      });
    return () => {
      cancelado = true;
    };
  }, [expandido, completo, giria.termo, giria.idioma]);

  const definicao = completo?.definicoes[0];
  const explicacao =
    definicao?.explicacaoSimples ??
    giria.explicacaoSimples ??
    'Ainda não temos uma explicação para este termo.';
  const formal = definicao?.equivalenteFormal ?? giria.equivalenteFormal;

  const textoParaOuvir = [
    giria.termo,
    explicacao,
    formal ? `Em linguagem formal: ${formal}.` : '',
  ]
    .filter(Boolean)
    .join('. ');

  return (
    <li className="catalogo-item">
      <button
        type="button"
        className="catalogo-termo"
        aria-expanded={expandido}
        onClick={aoAlternar}
      >
        <span className="catalogo-termo-texto">{giria.termo}</span>
        {giria.riscoMenor && (
          <span className="etiqueta etiqueta-atencao">atenção</span>
        )}
        <span className="catalogo-seta">
          <IconeSeta aberta={expandido} />
        </span>
      </button>

      {expandido && (
        <div className="catalogo-detalhe">
          <p className="explicacao">{explicacao}</p>

          {formal && (
            <p className="equivalente">
              Em outras palavras: <strong>{formal}</strong>
            </p>
          )}

          {nivel === 'DETALHADA' && definicao?.explicacaoDetalhada && (
            <p className="catalogo-origem">{definicao.explicacaoDetalhada}</p>
          )}

          {definicao?.exemplos.length ? (
            <ul className="catalogo-exemplos">
              {definicao.exemplos.map((exemplo) => (
                <li key={exemplo.frase}>
                  <q>{exemplo.frase}</q>
                  {exemplo.traducao && <> = {exemplo.traducao}</>}
                </li>
              ))}
            </ul>
          ) : null}

          {completo && completo.variacoes.length > 0 && (
            <p className="catalogo-variacoes">
              Também escrito: {completo.variacoes.join(', ')}
            </p>
          )}

          {giria.riscoMenor && (
            <p className="aviso-risco">
              <IconeAtencao />
              <span>
                Este termo costuma aparecer em conversas que merecem
                atenção. Se você acompanha o uso de redes de um adolescente,
                pode valer uma conversa sobre o assunto.
              </span>
            </p>
          )}

          {podeFalar && (
            <button
              type="button"
              className="botao"
              onClick={() => falar(textoParaOuvir)}
              aria-label={`Ouvir a explicação de ${giria.termo}`}
            >
              <IconeSom /> Ouvir
            </button>
          )}
        </div>
      )}
    </li>
  );
}
