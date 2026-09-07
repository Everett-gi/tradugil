package br.com.tradugil.ia;

import java.util.Optional;

/**
 * Nível 4 da cascata.
 *
 * <p>É interface, e não classe única, por causa de {@link IaDesligada}: sem
 * chave de API configurada, a aplicação precisa subir e atender normalmente
 * pelos níveis 0 a 2. Fazer disso um {@code if (chave != null)} espalhado
 * pelo serviço de tradução misturaria a regra de negócio com a configuração
 * de infraestrutura.</p>
 */
public interface ClienteDeIa {

    /**
     * @param termo    forma normalizada do termo desconhecido
     * @param contexto trecho ao redor, ou {@code null}. <b>Dado não
     *                 confiável</b> — ver {@code PromptDeLexicografo}
     * @return vazio quando a IA está desligada, indisponível, sem cota, ou
     *         quando a resposta não passou na validação
     */
    Optional<ExplicacaoDaIa> explicar(String termo, String contexto);

    /** Se há chance de esta chamada resolver algo. Evita trabalho inútil. */
    boolean estaDisponivel();
}
