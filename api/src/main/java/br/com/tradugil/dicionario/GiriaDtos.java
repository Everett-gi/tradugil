package br.com.tradugil.dicionario;

import br.com.tradugil.dicionario.Fonte.TipoDeFonte;

import java.util.List;

/**
 * Formatos de saída do dicionário. Nenhum deles expõe entidade JPA
 * diretamente: os clientes offline gravam esse JSON no dispositivo, então
 * mudar a forma quebra dicionários já sincronizados, e isso precisa ser uma
 * decisão consciente, não o efeito colateral de renomear um campo.
 */
public final class GiriaDtos {

    private GiriaDtos() {
    }

    /** Linha de lista: o mínimo para desenhar um resultado de busca. */
    public record GiriaResumo(
            Long id,
            String termo,
            String idioma,
            String explicacaoSimples,
            String equivalenteFormal,
            boolean nsfw,
            boolean riscoMenor,
            List<String> categorias
    ) {
        public static GiriaResumo de(Giria giria) {
            String explicacao = giria.definicoesAprovadas().stream()
                    .findFirst().map(Definicao::getExplicacaoSimples).orElse(null);
            String formal = giria.definicoesAprovadas().stream()
                    .findFirst().map(Definicao::getEquivalenteFormal).orElse(null);
            return new GiriaResumo(
                    giria.getId(),
                    giria.getTermo(),
                    giria.getIdioma().getCodigo(),
                    explicacao,
                    formal,
                    giria.isNsfw(),
                    giria.isRiscoMenor(),
                    giria.getCategorias().stream().map(Categoria::getSlug).toList());
        }
    }

    /** Verbete completo da tela de detalhe. */
    public record GiriaCompleta(
            Long id,
            String termo,
            String idioma,
            boolean nsfw,
            boolean riscoMenor,
            List<String> categorias,
            List<String> variacoes,
            List<DefinicaoResposta> definicoes
    ) {
        public static GiriaCompleta de(Giria giria) {
            return new GiriaCompleta(
                    giria.getId(),
                    giria.getTermo(),
                    giria.getIdioma().getCodigo(),
                    giria.isNsfw(),
                    giria.isRiscoMenor(),
                    giria.getCategorias().stream().map(Categoria::getSlug).toList(),
                    giria.getVariacoes().stream().map(GiriaVariacao::getVariacao).toList(),
                    giria.definicoesAprovadas().stream().map(DefinicaoResposta::de).toList());
        }
    }

    public record DefinicaoResposta(
            Long id,
            String explicacaoSimples,
            String explicacaoDetalhada,
            String equivalenteFormal,
            TipoDeFonte fonte,
            int votosUteis,
            List<ExemploResposta> exemplos
    ) {
        public static DefinicaoResposta de(Definicao definicao) {
            return new DefinicaoResposta(
                    definicao.getId(),
                    definicao.getExplicacaoSimples(),
                    definicao.getExplicacaoDetalhada(),
                    definicao.getEquivalenteFormal(),
                    definicao.getFonte().getTipo(),
                    definicao.getVotosUteis(),
                    definicao.getExemplos().stream().map(ExemploResposta::de).toList());
        }
    }

    public record ExemploResposta(String frase, String traducao) {
        public static ExemploResposta de(Exemplo exemplo) {
            return new ExemploResposta(exemplo.getFrase(), exemplo.getTraducao());
        }
    }

    /**
     * @param util true para "essa explicação me ajudou", false para o contrário
     */
    public record Voto(boolean util) {
    }

    /** Envelope de página. Sem total: contar exigiria varrer o índice trigram. */
    public record Pagina<T>(List<T> itens, int pagina, int tamanho, boolean temMais) {
    }
}
