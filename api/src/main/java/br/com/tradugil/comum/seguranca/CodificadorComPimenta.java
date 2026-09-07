package br.com.tradugil.comum.seguranca;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Base64;

/**
 * BCrypt com pimenta: HMAC-SHA256 da senha antes do hash.
 *
 * <h2>O que a pimenta resolve que o sal não resolve</h2>
 *
 * <p>O sal do BCrypt fica <b>junto</b> do hash, no banco. Ele impede que duas
 * senhas iguais gerem hashes iguais e derruba tabelas pré-computadas, mas quem
 * copiar a tabela {@code usuario} leva sal e hash juntos e pode atacar cada
 * linha offline, no ritmo que quiser.</p>
 *
 * <p>A pimenta é uma chave que <b>não está no banco</b>: vive na variável de
 * ambiente do servidor. Um vazamento apenas do banco (backup exposto, réplica
 * mal configurada, SQL injection em outro ponto) passa a render hashes que não
 * dá para atacar, porque falta um segredo de 256 bits que nunca esteve ali.
 * Só serve contra o vazamento parcial, e é exatamente esse o vazamento
 * comum.</p>
 *
 * <h2>Por que HMAC e não concatenação</h2>
 *
 * <p>{@code bcrypt(senha + pimenta)} pareceria equivalente e não é. O BCrypt
 * <b>trunca em 72 bytes</b> silenciosamente: com a pimenta no fim, uma senha
 * longa empurraria parte dela para fora e a pimenta deixaria de participar sem
 * ninguém perceber. O HMAC devolve sempre 32 bytes, que em Base64 viram 44
 * caracteres: bem abaixo do limite, qualquer que seja a senha.</p>
 *
 * <p>O truncamento some junto, e com ele um problema real: sem isto, duas
 * senhas que compartilham os primeiros 72 bytes abrem a mesma conta.</p>
 *
 * <h2>Trocar a pimenta</h2>
 *
 * <p>Trocar invalida todos os hashes existentes: não há como recalcular sem a
 * senha em claro, que ninguém tem. Se um dia for preciso, o caminho é aceitar
 * as duas pimentas durante uma janela e regravar o hash a cada login bem
 * sucedido, do mesmo jeito que {@code upgradeEncoding} já faz aqui para os
 * hashes antigos. Enquanto isso não for necessário, a chave fica parada.</p>
 */
public class CodificadorComPimenta implements PasswordEncoder {

    /**
     * Custo do BCrypt. 12 e não o padrão 10: cada incremento dobra o trabalho,
     * e 12 fica em torno de 250 ms num servidor modesto. É lento o suficiente
     * para atrapalhar quem ataca offline e rápido o suficiente para um login
     * que acontece uma vez a cada 30 dias, que é a validade do refresh token.
     */
    private static final int CUSTO = 12;

    private static final String ALGORITMO = "HmacSHA256";

    private final BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder(CUSTO);
    private final SecretKeySpec pimenta;

    public CodificadorComPimenta(byte[] pimenta) {
        this.pimenta = new SecretKeySpec(pimenta, ALGORITMO);
    }

    @Override
    public String encode(CharSequence senha) {
        return bcrypt.encode(temperar(senha));
    }

    @Override
    public boolean matches(CharSequence senha, String hashGuardado) {
        return bcrypt.matches(temperar(senha), hashGuardado);
    }

    @Override
    public boolean upgradeEncoding(String hashGuardado) {
        return bcrypt.upgradeEncoding(hashGuardado);
    }

    private String temperar(CharSequence senha) {
        try {
            Mac mac = Mac.getInstance(ALGORITMO);
            mac.init(pimenta);
            byte[] digerido = mac.doFinal(senha.toString().getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(digerido);
        } catch (GeneralSecurityException e) {
            // HmacSHA256 é obrigatório em toda JVM. Chegar aqui significa uma
            // instalação quebrada, e seguir em frente gravaria um hash sem
            // pimenta como se estivesse tudo certo.
            throw new IllegalStateException("HMAC-SHA256 indisponível nesta JVM", e);
        }
    }
}
