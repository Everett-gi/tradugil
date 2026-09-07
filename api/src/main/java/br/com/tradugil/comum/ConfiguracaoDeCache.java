package br.com.tradugil.comum;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Nível 1 da cascata. Em processo, sem Redis: a infraestrutura alvo é o
 * Always Free, onde um serviço de cache dedicado consumiria a única máquina
 * disponível.
 *
 * <p>O tamanho máximo é o que impede o cache de virar um vazamento de
 * memória lento: sem teto, um bot varrendo o dicionário encheria a heap até
 * o processo morrer.</p>
 */
@Configuration
public class ConfiguracaoDeCache {

    /**
     * Verbete mudado pela curadoria aparece em no máximo uma hora. Vale também
     * para a lista de categorias do catálogo, que muda ainda menos: ela só se
     * altera quando uma migração cria uma categoria nova.
     */
    private static final Duration VALIDADE = Duration.ofHours(1);

    private static final long MAXIMO_DE_VERBETES = 5_000;

    @Bean
    public CacheManager gerenciadorDeCache() {
        // Nomes fixos, e não criação sob demanda: um @Cacheable com nome
        // errado falha na primeira chamada em vez de criar silenciosamente um
        // cache paralelo que ninguém invalida.
        CaffeineCacheManager gerenciador = new CaffeineCacheManager("verbetes", "categorias");
        gerenciador.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(VALIDADE)
                .maximumSize(MAXIMO_DE_VERBETES)
                .recordStats());
        return gerenciador;
    }
}
