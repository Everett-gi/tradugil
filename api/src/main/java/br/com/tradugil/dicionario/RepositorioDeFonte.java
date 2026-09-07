package br.com.tradugil.dicionario;

import br.com.tradugil.dicionario.Fonte.TipoDeFonte;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * As quatro origens possíveis de uma explicação.
 *
 * <p>São dados de referência, criados pela V2 e nunca alterados em execução.
 * O repositório existe porque a origem precisa ser <b>consultada</b> na hora
 * de publicar uma contribuição aprovada: gravar o id fixo no código
 * funcionaria hoje e quebraria em silêncio num banco recriado do zero, onde a
 * sequência pode gerar outros números.</p>
 */
public interface RepositorioDeFonte extends JpaRepository<Fonte, Short> {

    Optional<Fonte> findFirstByTipo(TipoDeFonte tipo);
}
