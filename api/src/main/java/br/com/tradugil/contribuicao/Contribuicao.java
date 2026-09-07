package br.com.tradugil.contribuicao;

import br.com.tradugil.dicionario.Idioma;
import br.com.tradugil.identidade.Usuario;
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
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

/**
 * Gíria proposta por um usuário, esperando moderação.
 *
 * <p>Nada aqui chega ao dicionário sem passar por uma pessoa. É a mitigação
 * de Tampering do threat model: sem moderação obrigatória, o campo de
 * contribuição vira um canal aberto para definições ofensivas, golpes e
 * desinformação: exibidos com a autoridade de um verbete, para um público
 * que inclui pessoas idosas e famílias.</p>
 */
@Entity
@Table(name = "contribuicao")
public class Contribuicao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Column(nullable = false, length = 80)
    private String termo;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "idioma_id", nullable = false)
    private Idioma idioma;

    @Column(name = "explicacao_proposta", nullable = false, columnDefinition = "text")
    private String explicacaoProposta;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private StatusDeContribuicao status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "moderador_id")
    private Usuario moderador;

    @Column(name = "motivo_rejeicao", length = 200)
    private String motivoRejeicao;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private OffsetDateTime criadoEm;

    protected Contribuicao() {
    }

    public Contribuicao(Usuario usuario, String termo, Idioma idioma, String explicacaoProposta) {
        this.usuario = usuario;
        this.termo = termo;
        this.idioma = idioma;
        this.explicacaoProposta = explicacaoProposta;
        this.status = StatusDeContribuicao.PENDENTE;
    }

    public boolean estaPendente() {
        return status == StatusDeContribuicao.PENDENTE;
    }

    public void aprovar(Usuario moderador) {
        this.status = StatusDeContribuicao.APROVADA;
        this.moderador = moderador;
        this.motivoRejeicao = null;
    }

    public void rejeitar(Usuario moderador, String motivo) {
        this.status = StatusDeContribuicao.REJEITADA;
        this.moderador = moderador;
        this.motivoRejeicao = motivo;
    }

    public Long getId() {
        return id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public String getTermo() {
        return termo;
    }

    public Idioma getIdioma() {
        return idioma;
    }

    public String getExplicacaoProposta() {
        return explicacaoProposta;
    }

    public StatusDeContribuicao getStatus() {
        return status;
    }

    public Usuario getModerador() {
        return moderador;
    }

    public String getMotivoRejeicao() {
        return motivoRejeicao;
    }

    public OffsetDateTime getCriadoEm() {
        return criadoEm;
    }

    public enum StatusDeContribuicao {
        PENDENTE, APROVADA, REJEITADA
    }
}
