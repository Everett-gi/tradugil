package br.com.tradugil.dicionario;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Idioma de um verbete. Tabela, e não enum, porque acrescentar um idioma é
 * decisão de produto que não deveria exigir recompilar e reimplantar a API.
 */
@Entity
@Table(name = "idioma")
public class Idioma {

    @Id
    private Short id;

    /** Tag BCP 47 usada também pelos clientes: 'pt-BR', 'en'. */
    @Column(nullable = false, length = 5)
    private String codigo;

    @Column(nullable = false, length = 40)
    private String nome;

    protected Idioma() {
    }

    public Short getId() {
        return id;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNome() {
        return nome;
    }
}
