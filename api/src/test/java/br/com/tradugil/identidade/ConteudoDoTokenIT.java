package br.com.tradugil.identidade;

import br.com.tradugil.TestecomBanco;
import br.com.tradugil.identidade.IdentidadeDtos.NovoUsuario;
import br.com.tradugil.identidade.IdentidadeDtos.ParDeTokens;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * O que vai, e o que não vai, dentro do access token.
 *
 * <p>Um JWT é Base64, não é cifrado. Tudo que entra ali é legível por quem
 * tiver o token em mãos, e o token viaja em toda requisição, fica guardado no
 * navegador e sobra em qualquer lugar por onde um cabeçalho passe. Decidir o
 * que entra é uma decisão de privacidade, não de conveniência.</p>
 */
@TestecomBanco
class ConteudoDoTokenIT {

    @Autowired
    private ServicoDeAutenticacao servico;

    @Autowired
    private JwtDecoder decodificador;

    @Test
    @DisplayName("o token não carrega o e-mail nem nada além do necessário")
    void tokenNaoCarregaDadoPessoal() {
        String email = "teste-" + UUID.randomUUID() + "@exemplo.com";
        ParDeTokens tokens = servico.registrar(new NovoUsuario(email, "senhaboa123"));

        Jwt token = decodificador.decode(tokens.accessToken());

        assertThat(token.getClaims())
                .as("só o mínimo para autorizar: quem é, qual papel, e as datas")
                .containsOnlyKeys("iss", "iat", "exp", "sub", "papel");
        assertThat(token.getSubject()).isNotBlank();
        assertThat(token.getClaimAsString("papel")).isEqualTo("USER");
    }

    @Test
    @DisplayName("o e-mail não aparece nem em texto puro no token inteiro")
    void emailNaoVazaEmNenhumaParte() {
        /*
         * Confere o token bruto, e não só os claims decodificados. Uma
         * verificação por chave nomeada não pegaria o e-mail escondido dentro
         * de outro campo, e o que importa aqui é que a string não esteja lá,
         * qualquer que seja o caminho pelo qual tivesse entrado.
         */
        String email = "teste-" + UUID.randomUUID() + "@exemplo.com";
        ParDeTokens tokens = servico.registrar(new NovoUsuario(email, "senhaboa123"));

        String[] partes = tokens.accessToken().split("\\.");
        String cabecalho = decodificar(partes[0]);
        String corpo = decodificar(partes[1]);

        assertThat(cabecalho + corpo)
                .as("o token inteiro, decodificado")
                .doesNotContain(email)
                .doesNotContain("senhaboa123");
    }

    private static String decodificar(String parte) {
        return new String(Base64.getUrlDecoder().decode(parte), StandardCharsets.UTF_8);
    }
}
