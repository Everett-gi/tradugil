package br.com.tradugil.identidade;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

/**
 * Emite o access token.
 *
 * <p>Vida curta de propósito: 15 minutos. Um JWT não pode ser revogado — uma
 * vez emitido, vale até expirar, mesmo que a conta seja banida no minuto
 * seguinte. A revogação de verdade acontece no refresh token, que é opaco e
 * fica no banco; a janela de 15 minutos é o quanto de dano um token vazado
 * consegue causar.</p>
 */
@Component
public class EmissorDeJwt {

    private static final Duration VALIDADE = Duration.ofMinutes(15);

    private final JwtEncoder codificador;
    private final String emissor;

    public EmissorDeJwt(JwtEncoder codificador,
                        @Value("${tradugil.jwt.emissor:tradugil}") String emissor) {
        this.codificador = codificador;
        this.emissor = emissor;
    }

    public String emitir(Usuario usuario) {
        Instant agora = Instant.now();
        JwtClaimsSet dados = JwtClaimsSet.builder()
                .issuer(emissor)
                .issuedAt(agora)
                .expiresAt(agora.plus(VALIDADE))
                .subject(String.valueOf(usuario.getId()))
                // O papel viaja no token para a autorização não precisar de
                // uma ida ao banco por requisição. O preço é que a promoção
                // ou o rebaixamento de alguém só valem no próximo access
                // token — no máximo 15 minutos de atraso, aceitável para o
                // que este produto faz.
                .claim("papel", usuario.getPapel().name())
                .claim("email", usuario.getEmail())
                .build();

        return codificador.encode(JwtEncoderParameters.from(
                JwsHeader.with(() -> "HS256").build(), dados)).getTokenValue();
    }

    public long validadeEmSegundos() {
        return VALIDADE.toSeconds();
    }
}
