package br.com.tradugil.contribuicao;

import br.com.tradugil.contribuicao.Contribuicao.StatusDeContribuicao;
import br.com.tradugil.dicionario.Categoria;
import br.com.tradugil.dicionario.Definicao;
import br.com.tradugil.dicionario.Giria;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;
import java.util.List;

public final class ContribuicaoDtos {

    private ContribuicaoDtos() {
    }

    public record NovaContribuicao(
            @NotBlank(message = "Informe o termo.")
            @Size(max = 80, message = "O termo pode ter no maximo 80 caracteres.")
            String termo,

            @NotBlank(message = "Informe o idioma.")
            String idioma,

            @NotBlank(message = "Escreva a explicacao.")
            @Size(min = 10, max = 2000,
                    message = "A explicacao deve ter entre 10 e 2000 caracteres.")
            String explicacaoProposta
    ) {
    }

    /**
     * @param aprovar   true aprova, false rejeita
     * @param motivo    obrigatorio na rejeicao: sem ele, quem contribuiu nao
     *                  aprende nada e reenvia a mesma proposta
     * @param categoria slug da prateleira do catalogo. Obrigatorio quando o
     *                  verbete resultante ficaria sem nenhuma: sem prateleira
     *                  ele existe na busca e some do catalogo, que e onde
     *                  quem nao sabe o que procurar chega
     */
    public record DecisaoDeModeracao(
            boolean aprovar,
            @Size(max = 200, message = "O motivo pode ter no maximo 200 caracteres.")
            String motivo,
            @Size(max = 30, message = "Slug de categoria invalido.")
            String categoria
    ) {
    }

    public record ContribuicaoResposta(
            Long id,
            String termo,
            String idioma,
            String explicacaoProposta,
            StatusDeContribuicao status,
            String motivoRejeicao,
            OffsetDateTime criadoEm
    ) {
        public static ContribuicaoResposta de(Contribuicao c) {
            return new ContribuicaoResposta(
                    c.getId(),
                    c.getTermo(),
                    c.getIdioma().getCodigo(),
                    c.getExplicacaoProposta(),
                    c.getStatus(),
                    c.getMotivoRejeicao(),
                    c.getCriadoEm());
        }
    }

    /**
     * Uma linha da fila de moderacao, com o que o dicionario ja diz sobre o
     * termo.
     *
     * <h2>Por que a fila precisa de um DTO proprio</h2>
     *
     * <p>Quem modera decidia no escuro. A tela mostrava termo, idioma e o
     * texto proposto, e nada mais. Aprovar sem saber que o verbete ja existe
     * com tres sentidos, um deles dizendo quase a mesma coisa, e como o
     * dicionario ganhou explicacoes repetidas antes: foi preciso uma migracao
     * de limpeza (V29 e V33) para desfazer o estrago.</p>
     *
     * <p>O que o servidor sabe e a tela nao sabia: se o termo ja existe, quais
     * sentidos ele ja tem, e em que prateleiras ele esta. Agora vai junto.</p>
     *
     * <p>Nao substitui {@link ContribuicaoResposta}: a lista que o autor ve
     * das proprias propostas nao deve carregar o dicionario inteiro do termo,
     * e sao publicos diferentes.</p>
     *
     * @param noDicionario null quando o termo ainda nao existe
     */
    public record ItemDaFila(
            Long id,
            String termo,
            String idioma,
            String explicacaoProposta,
            OffsetDateTime criadoEm,
            VerbeteExistente noDicionario
    ) {
        public static ItemDaFila de(Contribuicao c, Giria existente) {
            return new ItemDaFila(
                    c.getId(),
                    c.getTermo(),
                    c.getIdioma().getCodigo(),
                    c.getExplicacaoProposta(),
                    c.getCriadoEm(),
                    existente == null ? null : VerbeteExistente.de(existente));
        }
    }

    /**
     * O que o dicionario ja tem para o termo proposto.
     *
     * @param sentidos   as explicacoes ja publicadas, para a comparacao ser
     *                   visual e imediata em vez de exigir abrir outra aba
     * @param categorias os slugs das prateleiras onde o verbete ja esta. Lista
     *                   vazia significa que ele existe e nao aparece no
     *                   catalogo, e a aprovacao vai exigir uma escolha
     */
    public record VerbeteExistente(
            List<String> sentidos,
            List<String> categorias
    ) {
        static VerbeteExistente de(Giria giria) {
            return new VerbeteExistente(
                    giria.definicoesAprovadas().stream()
                            .map(Definicao::getExplicacaoSimples)
                            .toList(),
                    giria.getCategorias().stream()
                            .map(Categoria::getSlug)
                            .toList());
        }
    }
}
