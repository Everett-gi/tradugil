package br.com.tradugiria.dicionario;

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
 * Frase de uso real com a versão em linguagem formal ao lado. Para o público
 * do produto, o exemplo costuma explicar melhor que a definição.
 */
@Entity
@Table(name = "exemplo")
public class Exemplo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "definicao_id", nullable = false)
    private Definicao definicao;

    @Column(nullable = false, columnDefinition = "text")
    private String frase;

    @Column(columnDefinition = "text")
    private String traducao;

    protected Exemplo() {
    }

    public Long getId() {
        return id;
    }

    public String getFrase() {
        return frase;
    }

    public String getTraducao() {
        return traducao;
    }
}
