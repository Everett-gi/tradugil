package br.com.tradugil.identidade;

import br.com.tradugil.identidade.Usuario.Papel;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class IdentidadeDtos {

    private IdentidadeDtos() {
    }

    public record NovoUsuario(
            @NotBlank(message = "Informe o e-mail.")
            @Email(message = "E-mail inválido.")
            @Size(max = 160, message = "E-mail longo demais.")
            String email,

            @NotBlank(message = "Informe a senha.")
            /*
             * O teto era 72 porque o BCrypt ignora o que passa disso, e uma
             * senha mais longa daria falsa sensação de proteção. Deixou de ser
             * verdade quando a senha passou a ser resumida por HMAC antes do
             * BCrypt: o hash tem sempre 32 bytes, qualquer que seja a entrada,
             * e o truncamento não alcança mais ninguém.
             *
             * Os 200 que ficaram existem por outro motivo, que continua de pé:
             * sem teto nenhum, alguém envia um megabyte e o servidor paga o
             * BCrypt em cima disso.
             *
             * O número precisa bater com TAMANHO_MAXIMO_DA_SENHA no serviço.
             * Enquanto era 72 aqui e 200 lá, a validação do serviço era
             * inalcançável por HTTP: o pedido morria antes de chegar nela.
             */
            @Size(min = 8, max = 200, message = "A senha deve ter entre 8 e 200 caracteres.")
            String senha
    ) {
    }

    public record Credenciais(
            @NotBlank(message = "Informe o e-mail.")
            String email,

            @NotBlank(message = "Informe a senha.")
            String senha
    ) {
    }

    public record PedidoDeRenovacao(
            @NotBlank(message = "Informe o token de atualização.")
            String refreshToken
    ) {
    }

    /**
     * @param accessToken       JWT curto, enviado no cabeçalho Authorization
     * @param refreshToken      token opaco, guardado pelo cliente para renovar
     * @param expiraEmSegundos  validade do access token
     * @param papel             o que esta conta pode fazer
     */
    public record ParDeTokens(
            String accessToken,
            String refreshToken,
            long expiraEmSegundos,
            Papel papel
    ) {
    }

    public record UsuarioResposta(Long id, String email, Papel papel) {
        public static UsuarioResposta de(Usuario usuario) {
            return new UsuarioResposta(usuario.getId(), usuario.getEmail(), usuario.getPapel());
        }
    }
}
