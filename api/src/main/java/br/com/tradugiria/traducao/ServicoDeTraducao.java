package br.com.tradugiria.traducao;

import br.com.tradugiria.dicionario.Definicao;
import br.com.tradugiria.dicionario.Giria;
import br.com.tradugiria.dicionario.GiriaVariacao;
import br.com.tradugiria.dicionario.RepositorioDeGiria;
import br.com.tradugiria.telemetria.RepositorioDeTermoDesconhecido;
import br.com.tradugiria.traducao.TraducaoDtos.GiriaDetectada;
import br.com.tradugiria.traducao.TraducaoDtos.NivelDeExplicacao;
import br.com.tradugiria.traducao.TraducaoDtos.OrigemDaResposta;
import br.com.tradugiria.traducao.TraducaoDtos.PedidoDeTraducao;
import br.com.tradugiria.traducao.TraducaoDtos.RespostaDeTraducao;
import br.com.tradugiria.traducao.Tokenizador.Trecho;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Percorre a cascata de resolução descrita na seção 3.2 do documento.
 *
 * <p>Esta classe cobre os níveis 1 e 2 (cache do servidor e banco curado) e
 * alimenta o nível 5 (fila de desconhecidos). Os níveis 3 e 4 — fontes
 * externas e IA — entram na F1 atrás desta mesma interface, para que os
 * clientes não precisem saber de onde a explicação veio além do campo
 * {@code origem}.</p>
 */
@Service
public class ServicoDeTraducao {

    /**
     * Palavras normalizadas que não vale a pena procurar no dicionário.
     *
     * <p>Sem esta lista, colar uma conversa inteira dispara centenas de
     * candidatos por requisição e enche a fila de curadoria de artigos e
     * preposições — que jamais virarão verbete e afogariam os termos que
     * realmente importam.</p>
     */
    private static final Set<String> PALAVRAS_IGNORADAS = Set.of(
            "a", "o", "as", "os", "um", "uma", "de", "da", "do", "das", "dos",
            "em", "no", "na", "nos", "nas", "por", "para", "com", "sem", "que",
            "e", "ou", "se", "ao", "aos", "eu", "ele", "ela", "eles", "elas",
            // "nos" já aparece acima: a contração "nos" e o pronome "nós"
            // colapsam na mesma forma normalizada, e uma entrada cobre as duas.
            "voce", "meu", "minha", "seu", "sua", "isso", "isto", "aquilo",
            "the", "of", "to", "in", "on", "at", "is", "are", "was", "were",
            "and", "or", "it", "he", "she", "they", "you", "we", "my", "your");

    /**
     * Termo curto demais para ir à fila de desconhecidos. "vc", "tb" e o
     * ruído de OCR caem aqui; abaixo de três caracteres quase nada é gíria
     * que a curadoria vá querer escrever.
     */
    private static final int TAMANHO_MINIMO_PARA_FILA = 3;

    private static final double CONFIANCA_DO_DICIONARIO = 1.0;

    private final RepositorioDeGiria repositorioDeGiria;
    private final RepositorioDeTermoDesconhecido repositorioDeDesconhecidos;

    public ServicoDeTraducao(RepositorioDeGiria repositorioDeGiria,
                             RepositorioDeTermoDesconhecido repositorioDeDesconhecidos) {
        this.repositorioDeGiria = repositorioDeGiria;
        this.repositorioDeDesconhecidos = repositorioDeDesconhecidos;
    }

    @Transactional
    public RespostaDeTraducao traduzir(PedidoDeTraducao pedido) {
        List<Trecho> palavras = Tokenizador.tokenizar(pedido.texto());
        List<Trecho> candidatos = Tokenizador.candidatos(palavras);

        Map<String, Giria> encontradas = buscarNoDicionario(candidatos);

        List<GiriaDetectada> detectadas = new ArrayList<>();
        Set<String> naoResolvidos = new LinkedHashSet<>();
        // Um caractere já coberto por um casamento mais longo não pode ser
        // reclamado por outro: sem isso, "dar ruim" viria acompanhado de
        // "ruim" sozinho, e a interface desenharia dois destaques sobrepostos.
        boolean[] ocupado = new boolean[pedido.texto().length()];

        for (Trecho candidato : candidatos) {
            if (intervaloOcupado(ocupado, candidato)) {
                continue;
            }
            Giria giria = encontradas.get(candidato.normalizado());
            if (giria == null) {
                if (ehCandidatoDeCuradoria(candidato)) {
                    naoResolvidos.add(candidato.normalizado());
                }
                continue;
            }
            // Modo Família: o impróprio some, o de risco fica e vem marcado.
            if (pedido.familiaLigado() && giria.isNsfw()) {
                marcarOcupado(ocupado, candidato);
                continue;
            }
            detectadas.add(montarResposta(giria, candidato, pedido.nivelOuPadrao()));
            marcarOcupado(ocupado, candidato);
        }

        registrarDesconhecidos(naoResolvidos, pedido.idiomaOuNulo());

        detectadas.sort((a, b) -> Integer.compare(a.posicao().get(0), b.posicao().get(0)));

        return new RespostaDeTraducao(
                pedido.idiomaOuNulo(),
                detectadas,
                false,
                List.copyOf(naoResolvidos));
    }

    /**
     * Nível 2 numa consulta só. O mapa é indexado tanto pelo termo canônico
     * quanto por cada variação, porque o candidato do texto pode ter casado
     * por qualquer um dos dois.
     */
    private Map<String, Giria> buscarNoDicionario(List<Trecho> candidatos) {
        Set<String> chaves = new LinkedHashSet<>();
        Set<String> colapsadas = new LinkedHashSet<>();
        for (Trecho candidato : candidatos) {
            if (!PALAVRAS_IGNORADAS.contains(candidato.normalizado())) {
                chaves.add(candidato.normalizado());
                colapsadas.add(Normalizador.colapsarRepeticoes(candidato.normalizado()));
            }
        }
        if (chaves.isEmpty()) {
            return Map.of();
        }

        // Indexado pelas três formas em que um verbete pode ser alcançado,
        // para o laço de detecção encontrá-lo perguntando pela forma que ele
        // tem em mãos — a normalizada do texto do usuário.
        Map<String, Giria> porChave = new HashMap<>();
        for (Giria giria : repositorioDeGiria.buscarPorCandidatos(chaves, colapsadas)) {
            porChave.put(giria.getTermoNormalizado(), giria);
            porChave.putIfAbsent(giria.getTermoColapsado(), giria);
            for (GiriaVariacao variacao : giria.getVariacoes()) {
                porChave.putIfAbsent(variacao.getVariacaoNormalizada(), giria);
                porChave.putIfAbsent(variacao.getVariacaoColapsada(), giria);
            }
        }

        // O candidato pode ter chegado ao verbete só pela forma colapsada
        // ("kkkkkkk" e "kkk" viram ambos "kk"); o mapa precisa responder
        // também pela forma original, que é a que o laço vai perguntar.
        for (Trecho candidato : candidatos) {
            String original = candidato.normalizado();
            if (porChave.containsKey(original)) {
                continue;
            }
            Giria porEnfase = porChave.get(Normalizador.colapsarRepeticoes(original));
            if (porEnfase != null) {
                porChave.put(original, porEnfase);
            }
        }
        return porChave;
    }

    private GiriaDetectada montarResposta(Giria giria, Trecho trecho, NivelDeExplicacao nivel) {
        Definicao definicao = giria.definicoesAprovadas().stream().findFirst().orElse(null);
        String explicacao = null;
        String formal = null;
        if (definicao != null) {
            // No nível detalhado, cair para a explicação simples quando não há
            // texto detalhado é melhor que devolver nulo: o usuário pediu mais
            // detalhe, não pediu para o verbete sumir.
            explicacao = nivel == NivelDeExplicacao.DETALHADA
                    && definicao.getExplicacaoDetalhada() != null
                    ? definicao.getExplicacaoDetalhada()
                    : definicao.getExplicacaoSimples();
            formal = definicao.getEquivalenteFormal();
        }
        return new GiriaDetectada(
                giria.getTermo(),
                List.of(trecho.inicio(), trecho.fim()),
                explicacao,
                formal,
                giria.isNsfw(),
                giria.isRiscoMenor(),
                CONFIANCA_DO_DICIONARIO,
                OrigemDaResposta.DICIONARIO);
    }

    /**
     * Só palavra isolada vai para a fila. Sequências de duas e três palavras
     * geram combinações demais que nunca serão verbete ("foi mt", "ele a
     * round") e sujariam a curadoria.
     */
    private boolean ehCandidatoDeCuradoria(Trecho candidato) {
        String termo = candidato.normalizado();
        return !termo.contains(" ")
                && termo.length() >= TAMANHO_MINIMO_PARA_FILA
                && !PALAVRAS_IGNORADAS.contains(termo);
    }

    private void registrarDesconhecidos(Set<String> termos, String idioma) {
        for (String termo : termos) {
            repositorioDeDesconhecidos.registrar(termo, idioma);
        }
    }

    private boolean intervaloOcupado(boolean[] ocupado, Trecho trecho) {
        for (int i = trecho.inicio(); i < trecho.fim(); i++) {
            if (ocupado[i]) {
                return true;
            }
        }
        return false;
    }

    private void marcarOcupado(boolean[] ocupado, Trecho trecho) {
        for (int i = trecho.inicio(); i < trecho.fim(); i++) {
            ocupado[i] = true;
        }
    }
}
