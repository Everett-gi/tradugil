package br.com.tradugil.identidade;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

/**
 * Conta de usuário.
 *
 * <p>Existe apenas para contribuir e moderar. <b>Consultar o dicionário nunca
 * exige cadastro</b>: exigir login para entender uma mensagem afastaria
 * exatamente o público que o produto quer atender, e criaria um banco de
 * dados pessoais que a arquitetura não precisa ter.</p>
 */
@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 160)
    private String email;

    /** BCrypt. Nunca sai desta classe. */
    @Column(name = "senha_hash", nullable = false, length = 100)
    private String senhaHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private Papel papel;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private OffsetDateTime criadoEm;

    protected Usuario() {
    }

    public Usuario(String email, String senhaHash, Papel papel) {
        this.email = email;
        this.senhaHash = senhaHash;
        this.papel = papel;
    }

    public void promoverPara(Papel novo) {
        this.papel = novo;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getSenhaHash() {
        return senhaHash;
    }

    public Papel getPapel() {
        return papel;
    }

    public OffsetDateTime getCriadoEm() {
        return criadoEm;
    }

    public enum Papel {
        /** Pode propor gírias. É o papel de quem se cadastra. */
        USER,
        /** Pode aprovar e rejeitar contribuições. */
        MODERATOR,
        /** Pode promover moderadores. */
        ADMIN
    }
}
