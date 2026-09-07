package br.com.tradugil.comum.seguranca;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.List;

/**
 * Assinatura e verificação do access token.
 *
 * <p>HMAC com chave simétrica, e não par de chaves: a API é a única que emite
 * e a única que verifica. Um par assimétrico só teria valor se outro serviço
 * precisasse validar o token sem poder emiti-lo: o que não é o caso, e
 * traria gestão de chaves para um projeto que roda em uma instância só.</p>
 */
@Configuration
public class ConfiguracaoDeJwt {

    private static final Logger log = LoggerFactory.getLogger(ConfiguracaoDeJwt.class);

    /** HS256 exige pelo menos 256 bits de chave. */
    private static final int BYTES_MINIMOS_DA_CHAVE = 32;

    private final byte[] chave;

    public ConfiguracaoDeJwt(@Value("${TRADUGIL_JWT_SEGREDO:}") String segredo) {
        if (segredo == null || segredo.isBlank()) {
            // Chave aleatória a cada inicialização. Em desenvolvimento é o
            // que se quer; em produção significa que toda reinicialização
            // desloga todo mundo: daí o aviso alto, e não silêncio.
            byte[] gerada = new byte[BYTES_MINIMOS_DA_CHAVE];
            new SecureRandom().nextBytes(gerada);
            this.chave = gerada;
            log.warn("TRADUGIL_JWT_SEGREDO não definido: usando chave aleatória desta "
                    + "execução. Todos os tokens emitidos deixam de valer ao reiniciar. "
                    + "Defina a variável em produção.");
        } else {
            byte[] bytes = segredo.getBytes(StandardCharsets.UTF_8);
            if (bytes.length < BYTES_MINIMOS_DA_CHAVE) {
                // Falhar na subida, e não aceitar uma chave fraca em silêncio:
                // um segredo curto derruba a segurança do token inteiro, e
                // descobrir isso depois é descobrir tarde demais.
                throw new IllegalStateException(
                        "TRADUGIL_JWT_SEGREDO precisa ter ao menos "
                                + BYTES_MINIMOS_DA_CHAVE + " bytes. Gere um com: "
                                + "openssl rand -base64 48");
            }
            this.chave = bytes;
        }
    }

    @Bean
    public JwtEncoder codificadorDeJwt() {
        return new NimbusJwtEncoder(new ImmutableSecret<>(chaveSecreta()));
    }

    @Bean
    public JwtDecoder decodificadorDeJwt(
            @Value("${tradugil.jwt.emissor:tradugil}") String emissor) {
        NimbusJwtDecoder decodificador = NimbusJwtDecoder.withSecretKey(chaveSecreta())
                // Algoritmo fixo, e não "o que o token disser". Aceitar o
                // cabeçalho do token como fonte da verdade sobre o algoritmo é
                // a família de ataques "alg confusion", incluindo o clássico
                // alg=none. Aqui só HS256 passa, venha o que vier escrito.
                .macAlgorithm(MacAlgorithm.HS256)
                .build();

        /*
         * O padrão do Nimbus valida só as datas. O emissor era escrito no
         * token e nunca conferido, o que deixava valer qualquer token assinado
         * com esta chave, tenha sido emitido para o que for. Enquanto a chave
         * serve a um sistema só isso é teórico; deixa de ser no dia em que a
         * mesma chave for reaproveitada em outro lugar, e nesse dia ninguém
         * lembra de voltar aqui.
         */
        decodificador.setJwtValidator(JwtValidators.createDefaultWithIssuer(emissor));
        return decodificador;
    }

    /**
     * Traduz o papel do token para a autoridade que o Spring Security entende.
     *
     * <p>O padrão do Spring lê o campo {@code scope}; aqui o papel vem em
     * {@code papel} e ganha o prefixo {@code ROLE_}, que é o que
     * {@code hasRole} espera encontrar.</p>
     */
    @Bean
    public JwtAuthenticationConverter conversorDeAutenticacao() {
        JwtAuthenticationConverter conversor = new JwtAuthenticationConverter();
        conversor.setJwtGrantedAuthoritiesConverter(jwt -> {
            String papel = jwt.getClaimAsString("papel");
            return papel == null
                    ? List.<GrantedAuthority>of()
                    : List.<GrantedAuthority>of(new SimpleGrantedAuthority("ROLE_" + papel));
        });
        return conversor;
    }

    private SecretKeySpec chaveSecreta() {
        return new SecretKeySpec(chave, "HmacSHA256");
    }
}
