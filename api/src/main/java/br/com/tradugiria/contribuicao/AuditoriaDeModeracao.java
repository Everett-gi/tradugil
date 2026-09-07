package br.com.tradugiria.contribuicao;

import br.com.tradugiria.contribuicao.Contribuicao.StatusDeContribuicao;
import br.com.tradugiria.identidade.Usuario;
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
 * Registro do que aconteceu, nao do que vale agora.
 *
 * <p>A coluna {@code moderador_id} em {@link Contribuicao} guarda o estado
 * atual, e estado atual pode ser sobrescrito: um moderador que aprova algo
 * improprio e depois rejeita para encobrir faz a aprovacao desaparecer. Esta
 * tabela e append-only -- o proprio banco recusa UPDATE e DELETE por
 * gatilho.</p>
 *
 * <p>A entidade nao tem nenhum metodo que altere um registro existente. Isso
 * e proposital: o gatilho e a garantia, e a ausencia de setters e o que faz
 * o codigo nao esbarrar nela por acidente.</p>
 */
@Entity
@Table(name = "auditoria_de_moderacao")
public class AuditoriaDeModeracao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "contribuicao_id", nullable = false)
    private Contribuicao contribuicao;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "moderador_id", nullable = false)
    private Usuario moderador;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private StatusDeContribuicao acao;

    @Column(length = 200)
    private String motivo;

    @CreationTimestamp
    @Column(name = "ocorrido_em", nullable = false, updatable = false)
    private OffsetDateTime ocorridoEm;

    protected AuditoriaDeModeracao() {
    }

    public AuditoriaDeModeracao(Contribuicao contribuicao, Usuario moderador,
                                StatusDeContribuicao acao, String motivo) {
        this.contribuicao = contribuicao;
        this.moderador = moderador;
        this.acao = acao;
        this.motivo = motivo;
    }

    public Long getId() {
        return id;
    }

    public StatusDeContribuicao getAcao() {
        return acao;
    }

    public String getMotivo() {
        return motivo;
    }

    public OffsetDateTime getOcorridoEm() {
        return ocorridoEm;
    }
}
