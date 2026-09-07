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
            // O teto existe porque o BCrypt ignora o que passa de 72 bytes:
            // sem ele, o usuário digitaria uma senha longa acreditando estar
            // mais protegido, enquanto só os primeiros 72 bytes contariam.
            @Size(min = 8, max = 72, message = "A senha deve ter entre 8 e 72 caracteres.")
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
