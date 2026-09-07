package br.com.tradugil.dicionario;

import br.com.tradugil.TestecomBanco;
import br.com.tradugil.dicionario.GiriaDtos.CategoriaResumo;
import br.com.tradugil.dicionario.GiriaDtos.GiriaResumo;
import br.com.tradugil.dicionario.GiriaDtos.Pagina;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * O catálogo, contra PostgreSQL real.
 *
 * <p>Precisa ser teste de integração, e não de unidade com repositório
 * simulado: tudo que ele verifica está em SQL nativo. O {@code CAST} do
 * parâmetro booleano, o {@code EXISTS} pela tabela de ligação e o
 * {@code LEFT JOIN} com o filtro no {@code ON} só falham quando um Postgres
 * de verdade lê a consulta. Um mock diria que passou.</p>
 */
@TestecomBanco
class CatalogoIT {

    @Autowired
    private ServicoDeDicionario servico;

    @Test
    @DisplayName("lista as prateleiras com a contagem de cada uma")
    void listaCategorias() {
        List<CategoriaResumo> prateleiras = servico.categorias(true);

        assertThat(prateleiras).isNotEmpty();
        assertThat(prateleiras)
                .extracting(CategoriaResumo::slug)
                .contains("gaming", "rua");
        assertThat(prateleiras)
                .as("categoria vazia não vira prateleira")
                .allSatisfy(c -> assertThat(c.quantidade()).isPositive());
    }

    @Test
    @DisplayName("os nomes das categorias estão acentuados")
    void nomesAcentuados() {
        // A V19 existe por isto. Antes do catálogo esses nomes não apareciam
        // para ninguém e ninguém notava que estavam sem acento; agora são o
        // rótulo que a pessoa lê na tela.
        List<CategoriaResumo> prateleiras = servico.categorias(true);

        assertThat(nomeDe(prateleiras, "musica")).isEqualTo("Música e funk");
        assertThat(nomeDe(prateleiras, "atencao")).isEqualTo("Termos que pedem atenção");
        assertThat(nomeDe(prateleiras, "abreviacao")).isEqualTo("Abreviação de digitação");
    }

    /**
     * Terminações que em português sempre levam acento. Não é uma regra de
     * ortografia completa, e não tenta ser: é o formato exato do erro que
     * apareceu duas vezes neste repositório, escrever o nome inteiro em ASCII.
     */
    private static final List<String> TERMINACOES_SEMPRE_ACENTUADAS =
            List.of("cao", "coes", "encia", "ancia");

    @Test
    @DisplayName("nenhum nome de categoria perdeu um acento pelo caminho")
    void nenhumNomePerdeuAcento() {
        /*
         * Varre em vez de repetir os onze nomes da V19: categoria nova entra
         * por migração, e quem escrever a próxima não vai lembrar de voltar
         * aqui. "Jogos" e "Esporte" não têm acento nenhum e continuam válidos:
         * a checagem é por terminação, não por presença de acento.
         */
        List<String> suspeitos = servico.categorias(false).stream()
                .map(CategoriaResumo::nome)
                .filter(CatalogoIT::terminaEmFormaSemAcento)
                .toList();

        assertThat(suspeitos)
                .as("nome de categoria com terminação que exige acento")
                .isEmpty();
    }

    private static boolean terminaEmFormaSemAcento(String nome) {
        for (String palavra : nome.toLowerCase(java.util.Locale.ROOT).split("[^\\p{L}]+")) {
            for (String terminacao : TERMINACOES_SEMPRE_ACENTUADAS) {
                if (palavra.endsWith(terminacao)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Test
    @DisplayName("abrir uma prateleira lista só o que está nela, em ordem alfabética")
    void abreUmaPrateleira() {
        Pagina<GiriaResumo> pagina = servico.pesquisar("", null, "gaming", true, 0, 50);

        assertThat(pagina.itens()).isNotEmpty();
        assertThat(pagina.itens())
                .as("tudo que sai da prateleira pertence a ela")
                .allSatisfy(g -> assertThat(g.categorias()).contains("gaming"));

        /*
         * A comparação olha só os termos de uma palavra em ASCII, e não a
         * lista inteira. Não é preguiça: quem ordena é o Postgres, com o
         * collation do servidor, e Java compara por ponto de código. Os dois
         * discordam justamente fora desse subconjunto. "tóxico" está nesta
         * prateleira e vai para o fim da lista sob collation C, mas para
         * perto de "toxico" sob en_US.UTF-8; e o collation da CI não é o
         * mesmo do Neon. Um teste que amarrasse a ordem completa passaria
         * aqui e quebraria lá, sem que nada de errado tivesse acontecido.
         *
         * Para letras a-z sem espaço as duas ordens coincidem, e é o
         * suficiente para provar o que importa: o ORDER BY está sendo
         * aplicado, e a lista não sai na ordem de inserção.
         */
        List<String> comparaveis = pagina.itens().stream()
                .map(GiriaResumo::termo)
                .filter(termo -> termo.matches("[a-z]+"))
                .toList();

        assertThat(comparaveis).hasSizeGreaterThan(10);
        assertThat(comparaveis).isSorted();
    }

    @Test
    @DisplayName("categoria inexistente devolve página vazia, não o dicionário inteiro")
    void categoriaInexistente() {
        // O modo de falhar que importa: um filtro ignorado silenciosamente
        // devolveria tudo, e a tela mostraria o dicionário inteiro sob o nome
        // de uma prateleira que não existe.
        Pagina<GiriaResumo> pagina = servico.pesquisar("", null, "nao-existe", true, 0, 50);

        assertThat(pagina.itens()).isEmpty();
    }

    @Test
    @DisplayName("categoria em branco significa sem filtro, e não nenhum resultado")
    void categoriaEmBrancoNaoFiltra() {
        Pagina<GiriaResumo> comBranco = servico.pesquisar("", null, "  ", true, 0, 10);
        Pagina<GiriaResumo> semFiltro = servico.pesquisar("", null, null, true, 0, 10);

        assertThat(comBranco.itens())
                .extracting(GiriaResumo::termo)
                .isEqualTo(semFiltro.itens().stream().map(GiriaResumo::termo).toList());
        assertThat(comBranco.itens()).isNotEmpty();
    }

    @Test
    @DisplayName("o modo família não lista verbetes impróprios ao navegar")
    void modoFamiliaEsconde() {
        /*
         * O catálogo é a única tela em que a pessoa esbarra num termo sem ter
         * procurado por ele. Se o filtro do /traduzir não valesse aqui, o modo
         * família estaria ligado e o conteúdo apareceria mesmo assim, numa
         * lista que se percorre com o polegar.
         */
        Pagina<GiriaResumo> comFamilia = servico.pesquisar("", null, null, true, 0, 50);

        assertThat(comFamilia.itens()).allSatisfy(g -> assertThat(g.nsfw()).isFalse());
    }

    @Test
    @DisplayName("a contagem da prateleira bate com o que ela lista, até o fim")
    void contagemBateComOConteudo() {
        /*
         * Contagem e listagem são consultas diferentes, com o filtro de modo
         * família escrito duas vezes: uma no LEFT JOIN da contagem, outra no
         * WHERE da busca. Corrigir uma e esquecer a outra daria uma tela que
         * anuncia 40 e mostra 31, e ninguém repara sem contar na mão. Aqui
         * conta-se na mão, percorrendo todas as páginas.
         */
        CategoriaResumo prateleira = servico.categorias(true).stream()
                .filter(c -> c.slug().equals("gaming"))
                .findFirst()
                .orElseThrow();

        assertThat(prateleira.quantidade()).isPositive();
        assertThat(percorrer("gaming", true)).isEqualTo(prateleira.quantidade());
    }

    @Test
    @DisplayName("o modo família muda a contagem e a listagem juntas")
    void contagemAcompanhaOModoFamilia() {
        for (String slug : List.of("atencao", "rua")) {
            long anunciado = servico.categorias(false).stream()
                    .filter(c -> c.slug().equals(slug))
                    .map(CategoriaResumo::quantidade)
                    .findFirst()
                    .orElseThrow(() -> new AssertionError("categoria ausente: " + slug));

            assertThat(percorrer(slug, false))
                    .as("prateleira %s com o modo família desligado", slug)
                    .isEqualTo(anunciado);
        }
    }

    /** Conta os verbetes de uma prateleira paginando até o fim. */
    private long percorrer(String slug, boolean modoFamilia) {
        long total = 0;
        int pagina = 0;
        Pagina<GiriaResumo> atual;
        do {
            atual = servico.pesquisar("", null, slug, modoFamilia, pagina, 50);
            total += atual.itens().size();
            pagina++;
        } while (atual.temMais() && pagina < 100);
        return total;
    }

    private static String nomeDe(List<CategoriaResumo> prateleiras, String slug) {
        return prateleiras.stream()
                .filter(c -> c.slug().equals(slug))
                .map(CategoriaResumo::nome)
                .findFirst()
                .orElseThrow(() -> new AssertionError("categoria ausente: " + slug));
    }
}
