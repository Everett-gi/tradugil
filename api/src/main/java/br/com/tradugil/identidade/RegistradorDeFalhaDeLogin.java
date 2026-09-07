package br.com.tradugil.identidade;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.OffsetDateTime;

/**
 * Contabiliza uma tentativa de login errada, em transação própria.
 *
 * <p>Existe pelo mesmo motivo que {@link RevogadorDeSessao}, e o erro foi
 * cometido de novo aqui antes de a CI apontar. Registrar a falha e recusar a
 * requisição são duas coisas, e feitas na mesma transação a segunda desfaz a
 * primeira: {@code RegraDeNegocioException} é {@code RuntimeException}, o
 * Spring faz rollback, e o contador que acabou de subir volta a zero.</p>
 *
 * <p>O efeito era pior do que um teste vermelho, como da outra vez: o
 * travamento de conta parecia implementado, tinha código, tinha coluna no
 * banco, e <b>nunca contava nada</b>. Cinco mil tentativas seguidas
 * deixariam o contador em zero, e a defesa contra força bruta distribuída
 * seria puramente decorativa.</p>
 *
 * <p>{@code REQUIRES_NEW} num bean separado, e não uma anotação no próprio
 * serviço de autenticação: o proxy do Spring não intercepta chamadas que um
 * objeto faz a si mesmo, então a anotação lá seria decoração, ou seja, o
 * mesmo bug com aparência de correção.</p>
 */
@Component
public class RegistradorDeFalhaDeLogin {

    private static final Logger log =
            LoggerFactory.getLogger(RegistradorDeFalhaDeLogin.class);

    private final RepositorioDeUsuario repositorio;

    public RegistradorDeFalhaDeLogin(RepositorioDeUsuario repositorio) {
        this.repositorio = repositorio;
    }

    /**
     * Soma uma falha à conta e trava se chegou no limite.
     *
     * <p>Recarrega o usuário pelo id em vez de receber a entidade já
     * carregada: a instância de fora pertence à transação de fora, e alterá-la
     * aqui gravaria pela transação errada, que é justamente a que vai sofrer
     * rollback.</p>
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registrar(Long usuarioId, int limite,
                          Duration esperaBase, Duration esperaMaxima) {
        repositorio.findById(usuarioId).ifPresent(usuario -> {
            OffsetDateTime travadoAte = usuario.registrarFalha(
                    OffsetDateTime.now(), limite, esperaBase, esperaMaxima);
            repositorio.save(usuario);
            if (travadoAte != null) {
                log.warn("Conta {} travada até {} após {} tentativas erradas.",
                        usuarioId, travadoAte, usuario.getTentativasFalhas());
            }
        });
    }

    /** Zera o contador depois de um acerto, também fora da transação de fora. */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void limpar(Long usuarioId) {
        repositorio.findById(usuarioId).ifPresent(usuario -> {
            if (usuario.getTentativasFalhas() > 0 || usuario.getBloqueadoAte() != null) {
                usuario.registrarAcerto();
                repositorio.save(usuario);
            }
        });
    }
}
