package br.com.tradugil.dicionario;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RepositorioDeDefinicao extends JpaRepository<Definicao, Long> {

    /**
     * Incrementa o contador direto no banco.
     *
     * <p>UPDATE atomico, e nao "ler, somar, gravar" em Java: dois votos
     * simultaneos na mesma definicao leriam o mesmo valor e gravariam o
     * mesmo resultado, perdendo um dos dois. Aqui o proprio Postgres
     * serializa a soma.</p>
     *
     * <p>Filtra por status na propria clausula: votar em definicao pendente
     * ou rejeitada moveria na fila algo que ainda nao foi publicado.</p>
     *
     * @return 1 se votou, 0 se a definicao nao existe ou nao esta aprovada
     */
    @Modifying
    @Transactional
    @Query(value = """
            UPDATE definicao
               SET votos_uteis   = votos_uteis   + :util,
                   votos_inuteis = votos_inuteis + :inutil
             WHERE id = :id AND status = 'APROVADA'
            """, nativeQuery = true)
    int registrarVoto(@Param("id") Long definicaoId,
                      @Param("util") int incrementoUtil,
                      @Param("inutil") int incrementoInutil);
}
