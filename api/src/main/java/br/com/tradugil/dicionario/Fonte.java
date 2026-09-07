package br.com.tradugil.dicionario;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * De onde veio uma definição. É dado de produto, não detalhe interno: a
 * origem sobe até a interface, porque uma explicação vinda da IA ou de fonte
 * externa não pode se passar por verbete revisado por pessoa.
 */
@Entity
@Table(name = "fonte")
public class Fonte {

    @Id
    private Short id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoDeFonte tipo;

    @Column(length = 120)
    private String descricao;

    protected Fonte() {
    }

    public Short getId() {
        return id;
    }

    public TipoDeFonte getTipo() {
        return tipo;
    }

    public String getDescricao() {
        return descricao;
    }

    public enum TipoDeFonte {
        /** Escrita e revisada pela curadoria. É a única que dispensa ressalva. */
        CURADORIA,
        /** Enviada por usuário e aprovada na moderação. */
        COMUNIDADE,
        /** Importada de fora; exibida com rótulo de origem. */
        EXTERNA,
        /** Gerada por IA. A interface é obrigada a marcar como não verificada. */
        IA
    }
}
