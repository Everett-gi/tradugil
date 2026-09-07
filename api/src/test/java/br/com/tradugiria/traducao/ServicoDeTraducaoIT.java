package br.com.tradugiria.traducao;

import br.com.tradugiria.TestecomBanco;
import br.com.tradugiria.telemetria.RepositorioDeTermoDesconhecido;
import br.com.tradugiria.traducao.TraducaoDtos.GiriaDetectada;
import br.com.tradugiria.traducao.TraducaoDtos.NivelDeExplicacao;
import br.com.tradugiria.traducao.TraducaoDtos.OrigemDaResposta;
import br.com.tradugiria.traducao.TraducaoDtos.PedidoDeTraducao;
import br.com.tradugiria.traducao.TraducaoDtos.RespostaDeTraducao;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * O endpoint principal do produto, ponta a ponta contra o banco real.
 */
@TestecomBanco
class ServicoDeTraducaoIT {

    /** A frase de exemplo da seção 5.2 do documento de especificação. */
    private static final String FRASE = "mano ele clutchou a round, foi mt pog, kekw";

    @Autowired
    private ServicoDeTraducao servico;

    @Autowired
    private RepositorioDeTermoDesconhecido repositorioDeDesconhecidos;

    @Test
    @DisplayName("detecta as gírias da frase de exemplo do documento")
    void detectaGiriasDaFraseDeExemplo() {
        RespostaDeTraducao resposta = traduzir(FRASE);

        assertThat(resposta.girias())
                .extracting(GiriaDetectada::termo)
                .contains("mano", "pog", "kekw");
        assertThat(resposta.geradoPorIa()).isFalse();
    }

    @Test
    @DisplayName("as posições recortam exatamente a gíria no texto enviado")
    void posicoesRecortamOTextoEnviado() {
        RespostaDeTraducao resposta = traduzir(FRASE);

        // O contrato de que os três clientes dependem para desenhar o
        // destaque. Se quebrar aqui, quebra na web, no Android e na extensão.
        for (GiriaDetectada giria : resposta.girias()) {
            int inicio = giria.posicao().get(0);
            int fim = giria.posicao().get(1);
            assertThat(FRASE.substring(inicio, fim))
                    .as("trecho de '%s'", giria.termo())
                    .isEqualToIgnoringCase(giria.termo());
        }
    }

    @Test
    @DisplayName("as gírias vêm ordenadas pela posição no texto")
    void giriasOrdenadasPelaPosicao() {
        RespostaDeTraducao resposta = traduzir(FRASE);

        assertThat(resposta.girias())
                .extracting(g -> g.posicao().get(0))
                .isSorted();
    }

    @Test
    @DisplayName("expressão de duas palavras vence as palavras isoladas")
    void expressaoCompostaVence() {
        String texto = "isso vai dar ruim";
        RespostaDeTraducao resposta = traduzir(texto);

        GiriaDetectada achada = resposta.girias().stream()
                .filter(g -> g.termo().equals("dar ruim"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("'dar ruim' não foi detectada"));

        assertThat(texto.substring(achada.posicao().get(0), achada.posicao().get(1)))
                .isEqualTo("dar ruim");

        // E "ruim" sozinho não pode aparecer junto: seriam dois destaques
        // sobrepostos no mesmo trecho da tela.
        assertThat(resposta.girias()).extracting(GiriaDetectada::termo).doesNotContain("ruim");
    }

    @Test
    @DisplayName("repetição de ênfase chega ao verbete: 'kkkkkkk' é 'kkk'")
    void repeticaoDeEnfase() {
        RespostaDeTraducao resposta = traduzir("que engracado kkkkkkk");

        assertThat(resposta.girias()).extracting(GiriaDetectada::termo).contains("kkk");
    }

    @Test
    @DisplayName("o nível detalhado devolve a explicação com a origem")
    void nivelDetalhado() {
        RespostaDeTraducao simples = traduzir("cringe", NivelDeExplicacao.SIMPLES);
        RespostaDeTraducao detalhada = traduzir("cringe", NivelDeExplicacao.DETALHADA);

        String textoSimples = simples.girias().getFirst().explicacao();
        String textoDetalhado = detalhada.girias().getFirst().explicacao();

        assertThat(textoDetalhado).isNotEqualTo(textoSimples);
        assertThat(textoDetalhado).contains("encolher");
    }

    @Test
    @DisplayName("a resposta do dicionário é marcada como vinda do dicionário")
    void origemDicionario() {
        assertThat(traduzir("pog").girias())
                .allSatisfy(g -> assertThat(g.origem()).isEqualTo(OrigemDaResposta.DICIONARIO));
    }

    @Test
    @DisplayName("termo desconhecido entra na fila anônima de curadoria")
    void termoDesconhecidoAlimentaCuradoria() {
        String inventado = "zzqwertyfake";
        traduzir("olha esse " + inventado + " ai");

        assertThat(repositorioDeDesconhecidos.findTop50ByOrderByOcorrenciasDesc())
                .extracting(t -> t.getTermoNormalizado())
                .contains(inventado);
    }

    @Test
    @DisplayName("artigos e preposições não entram na fila de curadoria")
    void palavrasComunsNaoSujamAFila() {
        traduzir("o a de para com que");

        assertThat(repositorioDeDesconhecidos.findTop50ByOrderByOcorrenciasDesc())
                .extracting(t -> t.getTermoNormalizado())
                .doesNotContain("de", "para", "com", "que");
    }

    @Test
    @DisplayName("texto sem gíria devolve lista vazia, não erro")
    void textoSemGiria() {
        RespostaDeTraducao resposta = traduzir("bom dia, como vai a senhora hoje");

        assertThat(resposta.girias()).isEmpty();
    }

    private RespostaDeTraducao traduzir(String texto) {
        return traduzir(texto, NivelDeExplicacao.SIMPLES);
    }

    private RespostaDeTraducao traduzir(String texto, NivelDeExplicacao nivel) {
        return servico.traduzir(new PedidoDeTraducao(texto, "auto", nivel, null, true));
    }
}
