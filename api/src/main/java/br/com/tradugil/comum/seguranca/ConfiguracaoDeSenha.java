package br.com.tradugil.comum.seguranca;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Map;

/**
 * Como as senhas são guardadas, e como trocar isso depois sem obrigar
 * ninguém a redefinir a senha.
 *
 * <p>O hash gravado carrega o nome do algoritmo como prefixo:
 * {@code {pimenta}$2a$12$...}. Sem esse prefixo, o formato fica congelado
 * para sempre: não dá para saber, olhando uma linha do banco, com o quê ela
 * foi gerada, e qualquer troca de algoritmo viraria "todo mundo perdeu a
 * senha".</p>
 *
 * <p>O {@link DelegatingPasswordEncoder} lê o prefixo para conferir e usa
 * sempre o algoritmo atual para gravar. Um hash antigo continua funcionando e
 * é regravado no formato novo no próximo login bem sucedido, um usuário de
 * cada vez, sem migração em massa e sem ninguém perceber.</p>
 */
@Configuration
public class ConfiguracaoDeSenha {

    private static final Logger log = LoggerFactory.getLogger(ConfiguracaoDeSenha.class);

    /** 256 bits, o tamanho do bloco do HMAC-SHA256. */
    private static final int BYTES_MINIMOS_DA_PIMENTA = 32;

    /** O que é usado para gravar. Trocar aqui muda só as senhas novas. */
    private static final String ALGORITMO_ATUAL = "pimenta";

    private final byte[] pimenta;

    public ConfiguracaoDeSenha(@Value("${TRADUGIL_PIMENTA_DE_SENHA:}") String configurada) {
        if (configurada == null || configurada.isBlank()) {
            /*
             * Pimenta aleatória desta execução. Em desenvolvimento é o que se
             * quer: nada a configurar para rodar. Em produção significa que
             * reiniciar invalida todas as senhas, e por isso o aviso é alto.
             *
             * A escolha deliberada é NÃO derrubar a aplicação aqui, ao
             * contrário do que é feito com o segredo do JWT. Um segredo de JWT
             * ausente deixa qualquer um forjar token; uma pimenta ausente só
             * atrapalha quem já tem conta. Impedir a subida por causa disso
             * transformaria uma conta esquecida num servidor que não sobe.
             */
            byte[] gerada = new byte[BYTES_MINIMOS_DA_PIMENTA];
            new SecureRandom().nextBytes(gerada);
            this.pimenta = gerada;
            log.warn("TRADUGIL_PIMENTA_DE_SENHA não definida: usando uma pimenta aleatória "
                    + "desta execução. Nenhuma senha já cadastrada vai funcionar depois de "
                    + "reiniciar. Defina a variável em produção e nunca mais a mude. "
                    + "Gere uma com: openssl rand -base64 32");
        } else {
            byte[] bytes = configurada.getBytes(StandardCharsets.UTF_8);
            if (bytes.length < BYTES_MINIMOS_DA_PIMENTA) {
                throw new IllegalStateException(
                        "TRADUGIL_PIMENTA_DE_SENHA precisa ter ao menos "
                                + BYTES_MINIMOS_DA_PIMENTA + " bytes. "
                                + "Gere uma com: openssl rand -base64 32");
            }
            this.pimenta = bytes;
        }
    }

    @Bean
    public PasswordEncoder codificadorDeSenha() {
        Map<String, PasswordEncoder> algoritmos = Map.of(
                ALGORITMO_ATUAL, new CodificadorComPimenta(pimenta),
                // Fica registrado para conferir hashes gravados antes de a
                // pimenta existir. Não é escolhido para gravar nada novo.
                "bcrypt", new BCryptPasswordEncoder());

        DelegatingPasswordEncoder codificador =
                new DelegatingPasswordEncoder(ALGORITMO_ATUAL, algoritmos);

        /*
         * Hashes gravados antes de existir prefixo nenhum começam direto com
         * "$2a$". Sem esta linha o Delegating não sabe o que fazer com eles e
         * recusa todos: quem tinha conta antes desta mudança não conseguiria
         * mais entrar.
         */
        codificador.setDefaultPasswordEncoderForMatches(new BCryptPasswordEncoder());
        return codificador;
    }
}
