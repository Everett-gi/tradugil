package br.com.tradugiria.contribuicao;

import br.com.tradugiria.contribuicao.Contribuicao.StatusDeContribuicao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;

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
     * @param aprovar true aprova, false rejeita
     * @param motivo  obrigatorio na rejeicao: sem ele, quem contribuiu nao
     *                aprende nada e reenvia a mesma proposta
     */
    public record DecisaoDeModeracao(
            boolean aprovar,
            @Size(max = 200, message = "O motivo pode ter no maximo 200 caracteres.")
            String motivo
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
}
