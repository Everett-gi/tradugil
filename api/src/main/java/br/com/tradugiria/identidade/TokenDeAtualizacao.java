package br.com.tradugiria.identidade;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Refresh token opaco, guardado como hash.
 *
 * <p>O access token é um JWT curto que expira sozinho e não é guardado. Este
 * é o oposto: vive semanas e precisa poder ser revogado, então precisa
 * existir do lado do servidor.</p>
 *
 * <p>Guardamos o hash, nunca o token: um vazamento desta tabela não pode dar
 * a ninguém a capacidade de se autenticar como outra pessoa.</p>
 */
@Entity
@Table(name = "token_de_atualizacao")
public class TokenDeAtualizacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    /** SHA-256 em hexadecimal: sempre 64 caracteres. */
    @Column(name = "hash_do_token", nullable = false, length = 64, columnDefinition = "bpchar")
    private String hashDoToken;

    /**
     * A cadeia de tokens nascida de um mesmo login. Revogar a família derruba
     * a sessão inteira, e não apenas o elo apresentado — é o que torna a
     * detecção de reuso útil.
     */
    @Column(nullable = false)
    private UUID familia;

    @Column(name = "expira_em", nullable = false)
    private OffsetDateTime expiraEm;

    /** Preenchido quando o token é trocado por outro na rotação. */
    @Column(name = "substituido_em")
    private OffsetDateTime substituidoEm;

    @Column(name = "revogado_em")
    private OffsetDateTime revogadoEm;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private OffsetDateTime criadoEm;

    protected TokenDeAtualizacao() {
    }

    public TokenDeAtualizacao(Usuario usuario, String hashDoToken, UUID familia,
                              OffsetDateTime expiraEm) {
        this.usuario = usuario;
        this.hashDoToken = hashDoToken;
        this.familia = familia;
        this.expiraEm = expiraEm;
    }

    /** Se pode ser trocado por um novo par de tokens agora. */
    public boolean estaValido(OffsetDateTime agora) {
        return revogadoEm == null && substituidoEm == null && expiraEm.isAfter(agora);
    }

    /**
     * Token já trocado que volta a ser apresentado.
     *
     * <p>Só há duas explicações: o cliente legítimo repetiu por falha de
     * rede, ou alguém roubou o token. Não dá para distinguir, então o sistema
     * assume o pior.</p>
     */
    public boolean foiReutilizado(OffsetDateTime agora) {
        return substituidoEm != null && expiraEm.isAfter(agora);
    }

    public void marcarSubstituido(OffsetDateTime quando) {
        this.substituidoEm = quando;
    }

    public void revogar(OffsetDateTime quando) {
        if (this.revogadoEm == null) {
            this.revogadoEm = quando;
        }
    }

    public Long getId() {
        return id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public UUID getFamilia() {
        return familia;
    }

    public OffsetDateTime getExpiraEm() {
        return expiraEm;
    }

    public OffsetDateTime getRevogadoEm() {
        return revogadoEm;
    }

    public OffsetDateTime getSubstituidoEm() {
        return substituidoEm;
    }
}
