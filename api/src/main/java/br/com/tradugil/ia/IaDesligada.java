package br.com.tradugil.ia;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * O que roda quando não há chave de API configurada.
 *
 * <p>Não é um caso degradado a ser evitado: é o modo normal de operação em
 * desenvolvimento e o comportamento correto se a verba da IA acabar. Os
 * níveis 0 a 2 seguem atendendo — que, pela meta da seção 7.3, são mais de
 * 85% das consultas — e apenas os termos realmente novos deixam de ser
 * resolvidos, indo para a fila de curadoria como sempre.</p>
 */
public class IaDesligada implements ClienteDeIa {

    private static final Logger log = LoggerFactory.getLogger(IaDesligada.class);

    public IaDesligada() {
        log.info("Camada de IA desligada: ANTHROPIC_API_KEY não configurada. "
                + "A API opera normalmente nos níveis 0 a 2 da cascata.");
    }

    @Override
    public Optional<ExplicacaoDaIa> explicar(String termo, String contexto) {
        return Optional.empty();
    }

    @Override
    public boolean estaDisponivel() {
        return false;
    }
}
