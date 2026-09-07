package br.com.tradugil.dicionario;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * O dicionário inteiro, num download só, para o cliente guardar no aparelho.
 *
 * <h2>Por que JSON e não o pacote SQLite que o documento previa</h2>
 *
 * <p>A seção 4.2 previa gerar um arquivo SQLite versionado e servir a URL de
 * download. Isso exige armazenamento de arquivo e um processo que regenere o
 * pacote a cada mudança da curadoria — infraestrutura que o free tier não
 * tem.</p>
 *
 * <p>Na escala real do dicionário, JSON com compressão resolve o mesmo
 * problema: alguns milhares de verbetes dão poucas centenas de kilobytes
 * comprimidos, e o Caddy já comprime. O cliente recebe, grava no Room ou no
 * IndexedDB, e pronto.</p>
 *
 * <p>O ponto em que essa escolha deixa de servir é conhecido: quando o
 * dicionário passar de umas dezenas de milhares de verbetes, ou quando o
 * download completo a cada mudança ficar caro para quem tem plano limitado.
 * Aí vale o pacote binário com sincronização incremental — mas isso é uma
 * troca a fazer com dados de uso, não antes deles.</p>
 */
public final class PacoteDtos {

    private PacoteDtos() {
    }

    /**
     * @param versao       muda sempre que a curadoria altera qualquer verbete.
     *                     O cliente guarda e só volta a baixar quando difere
     * @param geradoEm     quando o conteúdo mudou pela última vez
     * @param qtdVerbetes  para o cliente mostrar progresso
     */
    public record Pacote(
            String versao,
            OffsetDateTime geradoEm,
            int qtdVerbetes,
            List<VerbeteDoPacote> verbetes
    ) {
    }

    /**
     * Um verbete no formato que os clientes offline gravam.
     *
     * <p>Traz {@code nsfw} e {@code riscoMenor} como dados, e não filtrados na
     * origem: o cliente aplica o Modo Família na hora da consulta, do mesmo
     * jeito que o servidor faz. Filtrar aqui pareceria mais seguro, mas
     * obrigaria a rebaixar o dicionário inteiro toda vez que alguém
     * desligasse o modo — e o aparelho ficaria com um dicionário diferente
     * conforme a configuração de quem baixou.</p>
     */
    public record VerbeteDoPacote(
            String termo,
            String termoNormalizado,
            String termoColapsado,
            String idioma,
            String explicacaoSimples,
            String explicacaoDetalhada,
            String equivalenteFormal,
            boolean nsfw,
            boolean riscoMenor,
            List<VariacaoDoPacote> variacoes
    ) {
    }

    public record VariacaoDoPacote(String normalizada, String colapsada) {
    }

    /** Só os metadados, para o cliente decidir se vale baixar o pacote. */
    public record MetadadosDoPacote(
            String versao,
            OffsetDateTime geradoEm,
            int qtdVerbetes
    ) {
    }
}
