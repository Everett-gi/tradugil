package br.com.tradugiria.ia;

import com.anthropic.client.AnthropicClient;
import com.anthropic.client.okhttp.AnthropicOkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Liga ou desliga a camada de IA conforme a chave estiver configurada.
 *
 * <p>A ausência de chave não é erro: é o modo de operação em desenvolvimento
 * e o comportamento correto se a verba acabar. A aplicação sobe igual, os
 * níveis 0 a 2 atendem, e só os termos realmente novos deixam de ser
 * resolvidos.</p>
 */
@Configuration
public class ConfiguracaoDeIa {

    /**
     * Teto diário de chamadas. O padrão é deliberadamente baixo: é mais fácil
     * subir um número depois de ver a fatura do que explicar uma fatura que
     * não deveria ter existido.
     */
    @Value("${tradugiria.ia.cota-diaria:500}")
    private int cotaDiaria;

    @Value("${ANTHROPIC_API_KEY:}")
    private String chave;

    @Bean
    public CotaDeIa cotaDeIa() {
        return new CotaDeIa(cotaDiaria);
    }

    @Bean
    public ClienteDeIa clienteDeIa(CotaDeIa cota) {
        if (chave == null || chave.isBlank()) {
            return new IaDesligada();
        }
        AnthropicClient cliente = AnthropicOkHttpClient.builder()
                .apiKey(chave)
                .build();
        return new ClienteClaude(cliente, cota);
    }
}
