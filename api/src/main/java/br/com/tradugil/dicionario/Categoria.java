package br.com.tradugil.dicionario;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "categoria")
public class Categoria {

    @Id
    private Short id;

    @Column(nullable = false, length = 30)
    private String slug;

    @Column(nullable = false, length = 60)
    private String nome;

    protected Categoria() {
    }

    public Short getId() {
        return id;
    }

    public String getSlug() {
        return slug;
    }

    public String getNome() {
        return nome;
    }
}
