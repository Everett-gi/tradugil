package br.com.tradugil.dicionario;

import br.com.tradugil.TestecomBanco;
import br.com.tradugil.comum.erro.RecursoNaoEncontradoException;
import br.com.tradugil.dicionario.GiriaDtos.GiriaCompleta;
import br.com.tradugil.dicionario.GiriaDtos.GiriaResumo;
import br.com.tradugil.dicionario.GiriaDtos.Pagina;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Prova o nível 2 da cascata contra PostgreSQL real, incluindo as migrações
 * e o seed. É o teste que responde "o F0 funciona?" sem depender de um banco
 * de produção.
 */
@TestecomBanco
class ServicoDeDicionarioIT {

    @Autowired
    private ServicoDeDicionario servico;

    @Autowired
    private RepositorioDeGiria repositorio;

    @Test
    @DisplayName("as migrações aplicam e o seed carrega")
    void seedCarregado() {
        assertThat(repositorio.count())
                .as("verbetes do seed inicial")
                .isGreaterThanOrEqualTo(60);
    }

    @Test
    @DisplayName("encontra o verbete pelo termo exato")
    void encontraPorTermoExato() {
        GiriaCompleta verbete = servico.porTermo("cringe", "en");

        assertThat(verbete.termo()).isEqualTo("cringe");
        assertThat(verbete.definicoes()).isNotEmpty();
        assertThat(verbete.definicoes().getFirst().explicacaoSimples())
                .containsIgnoringCase("vergonha alheia");
    }

    @Test
    @DisplayName("erro de digitação ainda encontra o termo (pg_trgm)")
    void toleraErroDeDigitacao() {
        // O caso que justifica a extensão pg_trgm existir no projeto: quem
        // não conhece a palavra também não sabe escrevê-la.
        Pagina<GiriaResumo> achados = servico.pesquisar("crinje", "en", null, true, 0, 10);

        assertThat(achados.itens())
                .extracting(GiriaResumo::termo)
                .contains("cringe");
    }

    @Test
    @DisplayName("acento e caixa não impedem a busca")
    void ignoraAcentoECaixa() {
        assertThat(servico.porTermo("RANÇO", "pt-BR").termo()).isEqualTo("ranço");
        assertThat(servico.porTermo("ranco", "pt-BR").termo()).isEqualTo("ranço");
    }

    @Test
    @DisplayName("verbete de duas palavras é encontrado inteiro")
    void encontraVerbeteComposto() {
        GiriaCompleta verbete = servico.porTermo("dar ruim", "pt-BR");

        assertThat(verbete.termo()).isEqualTo("dar ruim");
        assertThat(verbete.definicoes().getFirst().equivalenteFormal()).isEqualTo("dar errado");
    }

    @Test
    @DisplayName("variação de escrita chega ao mesmo verbete")
    void variacaoApontaParaOVerbete() {
        // "pogchamp" não é verbete próprio: é variação de "pog". Sem isso a
        // curadoria manteria a mesma explicação escrita duas vezes.
        var pogchamp = java.util.List.of("pogchamp");
        assertThat(repositorio.buscarPorCandidatos(pogchamp, pogchamp))
                .extracting(Giria::getTermo)
                .containsExactly("pog");
    }

    @Test
    @DisplayName("variação de escrita ganha da busca por semelhança")
    void variacaoGanhaDaSemelhanca() {
        /*
         * O BUG QUE ESTE TESTE PEGA
         *
         * "pepelef" é a variação registrada de "PepeLaugh": é como o
         * brasileiro escreve o que ouviu. Antes desta correção, a consulta de
         * um termo não olhava a tabela de variações e caía direto no trigram,
         * que devolvia "pepeD" porque as duas palavras compartilham
         * trigramas.
         *
         * Isso é pior do que não encontrar. Quem procurou lia a explicação de
         * outro verbete acreditando ter achado o certo, e nada na tela
         * indicava que a resposta tinha vindo de um palpite.
         *
         * O /traduzir sempre acertou este caso, porque ele consulta as
         * variações. A divergência entre os dois caminhos é justamente o que
         * torna o erro difícil de notar: testar por um lado não prova nada
         * sobre o outro.
         */
        assertThat(servico.porTermo("pepelef", "en").termo()).isEqualTo("PepeLaugh");
        assertThat(servico.porTermo("omegalol", "en").termo()).isEqualTo("OMEGALUL");
        // Sem espaço: só a variação registrada leva a este verbete.
        assertThat(servico.porTermo("laele", "pt-BR").termo()).isEqualTo("lá ele");
    }

    @Test
    @DisplayName("termo inexistente devolve erro de recurso não encontrado")
    void termoInexistente() {
        assertThatThrownBy(() -> servico.porTermo("xyzabc123naoexiste", "pt-BR"))
                .isInstanceOf(RecursoNaoEncontradoException.class);
    }

    @Test
    @DisplayName("a paginação sinaliza que há mais resultados sem contar tudo")
    void paginacaoSinalizaProximaPagina() {
        Pagina<GiriaResumo> primeira = servico.pesquisar("a", null, null, true, 0, 2);

        assertThat(primeira.itens()).hasSizeLessThanOrEqualTo(2);
        assertThat(primeira.pagina()).isZero();
    }
}
