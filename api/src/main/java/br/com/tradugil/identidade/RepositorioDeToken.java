package br.com.tradugil.identidade;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

public interface RepositorioDeToken extends JpaRepository<TokenDeAtualizacao, Long> {

    Optional<TokenDeAtualizacao> findByHashDoToken(String hashDoToken);

    /**
     * Derruba a cadeia inteira nascida de um mesmo login.
     *
     * <p>Chamado quando um token ja trocado reaparece. Revogar so o elo
     * apresentado nao adiantaria: quem roubou ja tem o proximo da cadeia.</p>
     */
    @Modifying
    @Query("""
            UPDATE TokenDeAtualizacao t
               SET t.revogadoEm = :agora
             WHERE t.familia = :familia AND t.revogadoEm IS NULL
            """)
    int revogarFamilia(@Param("familia") UUID familia,
                       @Param("agora") OffsetDateTime agora);

    /** Sair de todos os aparelhos. */
    @Modifying
    @Query("""
            UPDATE TokenDeAtualizacao t
               SET t.revogadoEm = :agora
             WHERE t.usuario.id = :usuarioId AND t.revogadoEm IS NULL
            """)
    int revogarDoUsuario(@Param("usuarioId") Long usuarioId,
                         @Param("agora") OffsetDateTime agora);

    /**
     * Limpeza dos que ja nao servem para nada.
     *
     * <p>A tabela cresce a cada renovacao: sem varredura, um usuario ativo
     * deixa centenas de linhas mortas por ano.</p>
     */
    @Modifying
    @Query("DELETE FROM TokenDeAtualizacao t WHERE t.expiraEm < :limite")
    int apagarExpiradosAntesDe(@Param("limite") OffsetDateTime limite);
}
