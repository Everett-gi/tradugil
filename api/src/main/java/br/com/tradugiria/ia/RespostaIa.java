package br.com.tradugiria.ia;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

/**
 * Uma resposta da IA guardada para não ser paga duas vezes.
 *
 * <p>A chave é o termo mais o <b>hash</b> do contexto. O contexto em si nunca
 * é gravado: ele é um trecho lido da tela do usuário e pode ser um pedaço de
 * conversa privada. Guardá-lo para poder reusar a resposta transformaria o
 * cache num arquivo de conversas alheias — o hash cumpre a única função de
 * que o cache precisa, que é saber se a pergunta é a mesma.</p>
 */
@Entity
@Table(name = "resposta_ia")
public class RespostaIa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "termo_normalizado", nullable = false, length = 80)
    private String termoNormalizado;

    @Column(name = "hash_do_contexto", nullable = false, length = 64)
    private String hashDoContexto;

    @Column(name = "e_giria", nullable = false)
    private boolean eGiria;

    @Column(name = "explicacao_simples", columnDefinition = "text")
    private String explicacaoSimples;

    @Column(name = "explicacao_detalhada", columnDefinition = "text")
    private String explicacaoDetalhada;

    @Column(name = "equivalente_formal", length = 160)
    private String equivalenteFormal;

    @Column(nullable = false)
    private boolean nsfw;

    @Column(name = "risco_menor", nullable = false)
    private boolean riscoMenor;

    @Column(nullable = false)
    private float confianca;

    /** Marcado quando a curadoria promove esta resposta a verbete oficial. */
    @Column(nullable = false)
    private boolean promovida;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private OffsetDateTime criadoEm;

    protected RespostaIa() {
    }

    public RespostaIa(String termoNormalizado, String hashDoContexto, ExplicacaoDaIa origem) {
        this.termoNormalizado = termoNormalizado;
        this.hashDoContexto = hashDoContexto;
        this.eGiria = origem.eGiria();
        this.explicacaoSimples = origem.explicacaoSimples();
        this.explicacaoDetalhada = origem.explicacaoDetalhada();
        this.equivalenteFormal = origem.equivalenteFormal();
        this.nsfw = origem.nsfw();
        this.riscoMenor = origem.riscoMenor();
        this.confianca = (float) origem.confianca();
        this.promovida = false;
    }

    public ExplicacaoDaIa paraExplicacao() {
        return new ExplicacaoDaIa(
                eGiria, explicacaoSimples, explicacaoDetalhada, equivalenteFormal,
                nsfw, riscoMenor, confianca);
    }

    public Long getId() {
        return id;
    }

    public String getTermoNormalizado() {
        return termoNormalizado;
    }

    public boolean isPromovida() {
        return promovida;
    }

    public float getConfianca() {
        return confianca;
    }

    public OffsetDateTime getCriadoEm() {
        return criadoEm;
    }
}
