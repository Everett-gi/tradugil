package br.com.tradugiria.identidade;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Revoga sessões em transação própria.
 *
 * <p>Existe por causa de um bug real, encontrado pelo teste de reuso de
 * token. A detecção de reuso precisa fazer duas coisas: revogar a família e
 * recusar a requisição. Feitas no mesmo método transacional, a segunda
 * desfaz a primeira — {@code RegraDeNegocioException} é
 * {@code RuntimeException}, o Spring faz rollback, e a revogação vai embora
 * junto com ela.</p>
 *
 * <p>O efeito era pior do que um teste vermelho: a mitigação inteira ficava
 * anulada em silêncio. O sistema registrava no log que havia detectado
 * reuso, devolvia erro ao cliente, e deixava a sessão roubada funcionando.</p>
 *
 * <p>{@code REQUIRES_NEW} num bean separado, e não uma anotação no mesmo
 * serviço: o proxy do Spring não intercepta chamadas que um objeto faz a si
 * mesmo, então a anotação ali seria decorativa — o mesmo bug com aparência
 * de correção.</p>
 */
@Component
public class RevogadorDeSessao {

    private final RepositorioDeToken repositorio;

    public RevogadorDeSessao(RepositorioDeToken repositorio) {
        this.repositorio = repositorio;
    }

    /**
     * Derruba a cadeia inteira nascida de um mesmo login.
     *
     * @return quantos tokens foram revogados
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public int revogarFamilia(UUID familia) {
        return repositorio.revogarFamilia(familia, OffsetDateTime.now());
    }
}
