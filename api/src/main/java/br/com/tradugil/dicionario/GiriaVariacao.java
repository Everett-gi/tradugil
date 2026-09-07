package br.com.tradugil.dicionario;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Apelido ou grafia alternativa que aponta para o mesmo verbete.
 *
 * <p>Existe para que "pogchamp" e "pog" não sejam dois verbetes com a mesma
 * explicação escrita duas vezes — a curadoria corrigiria um e esqueceria o
 * outro. Repetições de ênfase ilimitadas ("kkkkkkkk") não entram aqui: são
 * resolvidas por {@code Normalizador.colapsarRepeticoes}, porque a lista
 * seria infinita.</p>
 */
@Entity
@Table(name = "giria_variacao")
public class GiriaVariacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "giria_id", nullable = false)
    private Giria giria;

    @Column(nullable = false, length = 80)
    private String variacao;

    @Column(name = "variacao_normalizada", nullable = false, length = 80)
    private String variacaoNormalizada;

    /** Forma com a ênfase repetida colapsada. Ver {@code Giria.termoColapsado}. */
    @Column(name = "variacao_colapsada", nullable = false, length = 80)
    private String variacaoColapsada;

    protected GiriaVariacao() {
    }

    public Long getId() {
        return id;
    }

    public Giria getGiria() {
        return giria;
    }

    public String getVariacao() {
        return variacao;
    }

    public String getVariacaoNormalizada() {
        return variacaoNormalizada;
    }

    public String getVariacaoColapsada() {
        return variacaoColapsada;
    }
}
