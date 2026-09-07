package br.com.tradugil.identidade;

import br.com.tradugil.identidade.Usuario.Papel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

/**
 * Dá o primeiro papel de administrador, por variável de ambiente.
 *
 * <h2>O ciclo que isto resolve</h2>
 *
 * Promover alguém exige ser ADMIN. Sem um primeiro ADMIN vindo de fora da
 * aplicação, ninguém nunca seria promovido e a moderação ficaria trancada
 * para sempre.
 *
 * <h2>Três restrições deliberadas</h2>
 *
 * <p><b>Nunca cria conta.</b> Só promove uma que já existe. Um bootstrap que
 * cria usuário precisaria de uma senha vinda de configuração, e senha em
 * variável de ambiente acaba em log de deploy, em histórico de shell e em
 * captura de tela. O caminho é: a pessoa se cadastra normalmente pelo site,
 * e só depois a variável a promove.</p>
 *
 * <p><b>Nunca rebaixa.</b> Se a conta já é ADMIN, não faz nada. Se for
 * MODERATOR, promove. Tirar o valor da variável não deve derrubar o
 * administrador: essa decisão é do endpoint de administração, com registro de
 * quem fez.</p>
 *
 * <p><b>Roda a cada subida, e é idempotente.</b> Não é migração porque não é
 * esquema: o e-mail do administrador muda por ambiente, e uma migração
 * gravaria o e-mail de desenvolvimento no histórico do banco de produção.</p>
 */
@Configuration
public class PromotorInicialDeAdmin {

    private static final Logger log = LoggerFactory.getLogger(PromotorInicialDeAdmin.class);

    @Bean
    public ApplicationRunner promoverAdminInicial(
            RepositorioDeUsuario repositorio,
            @Value("${TRADUGIL_ADMIN_INICIAL:}") String email) {

        return argumentos -> {
            if (email == null || email.isBlank()) {
                return;
            }
            promover(repositorio, email.trim().toLowerCase(Locale.ROOT));
        };
    }

    @Transactional
    void promover(RepositorioDeUsuario repositorio, String email) {
        repositorio.findByEmail(email).ifPresentOrElse(
                usuario -> {
                    if (usuario.getPapel() == Papel.ADMIN) {
                        return;
                    }
                    usuario.promoverPara(Papel.ADMIN);
                    repositorio.save(usuario);
                    log.warn("Conta {} promovida a ADMIN por TRADUGIL_ADMIN_INICIAL.",
                            usuario.getId());
                },
                /*
                 * Aviso, e não erro. A ordem esperada é a variável ser
                 * configurada antes de a pessoa se cadastrar, e derrubar a
                 * aplicação por isso deixaria o serviço fora do ar esperando
                 * um cadastro que só pode acontecer com ele no ar.
                 *
                 * O e-mail não entra no log: é o único dado pessoal do
                 * sistema, e um log de inicialização costuma ser o mais lido
                 * e o menos protegido de todos.
                 */
                () -> log.warn("TRADUGIL_ADMIN_INICIAL aponta para uma conta que ainda "
                        + "não existe. Cadastre-se pelo site e reinicie para a "
                        + "promoção acontecer."));
    }
}
