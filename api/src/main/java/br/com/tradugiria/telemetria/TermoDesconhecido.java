package br.com.tradugiria.telemetria;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

/**
 * Nível 5 da cascata: o termo que ninguém soube explicar.
 *
 * <p>É a única telemetria persistida do produto, e o que impede o dicionário
 * de envelhecer — a fila ordenada por ocorrências diz à curadoria quais
 * gírias novas as pessoas estão realmente encontrando esta semana.</p>
 *
 * <p><b>O que esta tabela deliberadamente não guarda:</b> a frase em que o
 * termo apareceu, o IP, o identificador do usuário e o horário exato de cada
 * consulta. Guardar qualquer um deles transformaria a fila de curadoria em um
 * registro de conversas privadas lidas da tela alheia. Só sobrevivem o termo
 * e um contador — nada aqui remete a uma pessoa.</p>
 */
@Entity
@Table(name = "termo_desconhecido")
public class TermoDesconhecido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "termo_normalizado", nullable = false, length = 80)
    private String termoNormalizado;

    @Column(name = "idioma_provavel", length = 5)
    private String idiomaProvavel;

    @Column(nullable = false)
    private int ocorrencias;

    @Column(name = "ultima_ocorrencia", nullable = false)
    private OffsetDateTime ultimaOcorrencia;

    protected TermoDesconhecido() {
    }

    public Long getId() {
        return id;
    }

    public String getTermoNormalizado() {
        return termoNormalizado;
    }

    public String getIdiomaProvavel() {
        return idiomaProvavel;
    }

    public int getOcorrencias() {
        return ocorrencias;
    }

    public OffsetDateTime getUltimaOcorrencia() {
        return ultimaOcorrencia;
    }
}
