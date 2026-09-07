package br.com.tradugiria.dicionario;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Um sentido de uma gíria, com os dois níveis de explicação previstos no
 * RF03. Os níveis não são estilos do mesmo texto: a explicação simples é
 * escrita para quem não conhece a cultura de internet, e a detalhada para
 * quem quer a origem. Gerar uma a partir da outra produziria o pior dos dois.
 */
@Entity
@Table(name = "definicao")
public class Definicao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "giria_id", nullable = false)
    private Giria giria;

    /** Nível 1: uma ou duas frases, sem jargão. */
    @Column(name = "explicacao_simples", nullable = false, columnDefinition = "text")
    private String explicacaoSimples;

    /** Nível 2: origem e nuance. Opcional — nem todo termo tem história. */
    @Column(name = "explicacao_detalhada", columnDefinition = "text")
    private String explicacaoDetalhada;

    @Column(name = "equivalente_formal", length = 160)
    private String equivalenteFormal;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "fonte_id", nullable = false)
    private Fonte fonte;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private StatusDeDefinicao status;

    @Column(name = "votos_uteis", nullable = false)
    private int votosUteis;

    @Column(name = "votos_inuteis", nullable = false)
    private int votosInuteis;

    @UpdateTimestamp
    @Column(name = "atualizado_em", nullable = false)
    private OffsetDateTime atualizadoEm;

    @OneToMany(mappedBy = "definicao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Exemplo> exemplos = new ArrayList<>();

    protected Definicao() {
    }

    public boolean estaAprovada() {
        return status == StatusDeDefinicao.APROVADA;
    }

    public Long getId() {
        return id;
    }

    public Giria getGiria() {
        return giria;
    }

    public String getExplicacaoSimples() {
        return explicacaoSimples;
    }

    public String getExplicacaoDetalhada() {
        return explicacaoDetalhada;
    }

    public String getEquivalenteFormal() {
        return equivalenteFormal;
    }

    public Fonte getFonte() {
        return fonte;
    }

    public StatusDeDefinicao getStatus() {
        return status;
    }

    public int getVotosUteis() {
        return votosUteis;
    }

    public int getVotosInuteis() {
        return votosInuteis;
    }

    public OffsetDateTime getAtualizadoEm() {
        return atualizadoEm;
    }

    public List<Exemplo> getExemplos() {
        return exemplos;
    }

    public enum StatusDeDefinicao {
        PENDENTE, APROVADA, REJEITADA
    }
}
