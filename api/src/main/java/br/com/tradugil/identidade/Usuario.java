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

import java.time.Duration;
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

    /**
     * Hash da senha, com o nome do algoritmo como prefixo
     * ({@code {pimenta}$2a$12$...}). Nunca sai desta classe, nunca aparece em
     * log e nunca entra numa resposta.
     */
    @Column(name = "senha_hash", nullable = false, length = 120)
    private String senhaHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private Papel papel;

    /**
     * Tentativas de login malsucedidas seguidas. Zera no primeiro acerto.
     *
     * <p>Fica no banco, e não em memória, porque memória zera ao reiniciar e
     * reiniciar é algo que qualquer um consegue provocar. Fica por conta, e
     * não por IP, porque força bruta distribuída troca de IP a cada tentativa;
     * o que ela não consegue trocar é a conta que está atacando.</p>
     */
    @Column(name = "tentativas_falhas", nullable = false)
    private short tentativasFalhas;

    @Column(name = "bloqueado_ate")
    private OffsetDateTime bloqueadoAte;

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

    /** Regrava o hash no formato atual, sem tocar em mais nada. */
    public void trocarHashDaSenha(String novo) {
        this.senhaHash = novo;
    }

    public boolean estaBloqueado(OffsetDateTime agora) {
        return bloqueadoAte != null && bloqueadoAte.isAfter(agora);
    }

    /**
     * Registra uma tentativa errada e devolve até quando a conta fica travada,
     * ou nulo se ainda não chegou no limite.
     *
     * <p>A espera dobra a cada novo bloqueio da mesma sequência: um minuto,
     * dois, quatro, e assim por diante até o teto. Um erro de digitação custa
     * um minuto; dez mil tentativas passam a custar dias. É a mesma curva que
     * inviabiliza o ataque sem punir quem só errou a senha.</p>
     */
    public OffsetDateTime registrarFalha(OffsetDateTime agora, int limite,
                                         Duration esperaBase, Duration esperaMaxima) {
        tentativasFalhas = (short) Math.min(tentativasFalhas + 1, Short.MAX_VALUE);
        if (tentativasFalhas < limite) {
            return null;
        }

        // Quantos bloqueios já aconteceram nesta sequência. O primeiro, na
        // tentativa de número `limite`, tem expoente zero.
        int bloqueiosAnteriores = (tentativasFalhas - limite) / limite;
        long fator = 1L << Math.min(bloqueiosAnteriores, 20);

        Duration espera = esperaBase.multipliedBy(fator);
        if (espera.compareTo(esperaMaxima) > 0) {
            espera = esperaMaxima;
        }
        bloqueadoAte = agora.plus(espera);
        return bloqueadoAte;
    }

    public void registrarAcerto() {
        tentativasFalhas = 0;
        bloqueadoAte = null;
    }

    public short getTentativasFalhas() {
        return tentativasFalhas;
    }

    public OffsetDateTime getBloqueadoAte() {
        return bloqueadoAte;
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
