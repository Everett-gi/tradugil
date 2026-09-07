package br.com.tradugil.comum.seguranca;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Teto de requisições por IP, dentro da aplicação.
 *
 * <h2>Por que existe, se o Caddy já limita</h2>
 *
 * <p>O limite da borda é 60 por minuto para tudo em {@code /api/*}. Isso está
 * certo para consulta ao dicionário e errado para login: 60 tentativas de
 * senha por minuto, por IP, é muita coisa. Um único limite não consegue estar
 * certo para os dois, porque são riscos diferentes.</p>
 *
 * <p>E o limite da borda some junto com a borda. Rodar a API atrás de outro
 * proxy, num contêiner exposto por engano, ou em desenvolvimento, deixa o
 * serviço sem teto nenhum. Defesa que depende de uma peça externa estar no
 * lugar certo é defesa que falha em silêncio.</p>
 *
 * <h2>O que ele não faz</h2>
 *
 * <p>A contagem é por instância, em memória. Com várias instâncias atrás de um
 * balanceador, cada uma tem o próprio contador e o teto efetivo multiplica
 * pelo número de instâncias. Isso é aceitável <b>porque não é a única
 * defesa</b>: o teto global por IP é do Caddy, e a proteção de força bruta que
 * precisa ser exata (travamento por conta) está no banco, onde é global por
 * construção. Este filtro é a camada que sobra quando as outras faltam.</p>
 *
 * <p>Quando houver mais de uma instância e isso passar a incomodar, o lugar
 * de resolver é aqui: trocar o Caffeine por um contador compartilhado. A
 * interface do filtro não muda.</p>
 */
@Component
@Order(1)
public class LimitadorDeRequisicoes extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(LimitadorDeRequisicoes.class);

    /**
     * Classes de tráfego, cada uma com o próprio teto por minuto.
     *
     * <p>Os números saem do custo e do risco de cada rota, não de um padrão:
     * ler o dicionário é barato e legítimo em rajada; adivinhar senha não tem
     * uso legítimo em rajada nenhuma.</p>
     */
    private enum Classe {
        /** Login, cadastro e renovação. Dez por minuto já é generoso. */
        AUTENTICACAO(10, Duration.ofMinutes(1)),

        /**
         * Tradução. Custa CPU e, quando o termo é desconhecido, custa dinheiro
         * de IA. O teto é a primeira barreira contra alguém automatizar
         * consultas de termos inventados para gerar fatura.
         */
        TRADUCAO(30, Duration.ofMinutes(1)),

        /**
         * Voto. Público e sem cadastro de propósito, o que significa que a
         * única coisa entre ele e a manipulação de ranking é este teto.
         */
        VOTO(20, Duration.ofMinutes(1)),

        /** Leitura do dicionário e catálogo. Barato, e o uso normal é em rajada. */
        LEITURA(120, Duration.ofMinutes(1));

        private final int teto;
        private final Duration janela;

        Classe(int teto, Duration janela) {
            this.teto = teto;
            this.janela = janela;
        }
    }

    /**
     * Um cache por classe, com expiração igual à janela.
     *
     * <p>É janela fixa, não deslizante: quem gasta a cota no fim de um minuto
     * ganha cota nova no começo do seguinte, e no pior caso passa o dobro do
     * teto na virada. Um algoritmo mais preciso (token bucket) custaria mais
     * estado por IP para uma diferença que não muda o resultado prático: o
     * ataque continua limitado à ordem de grandeza certa.</p>
     */
    private final java.util.Map<Classe, Cache<String, AtomicInteger>> contadores =
            new java.util.EnumMap<>(Classe.class);

    public LimitadorDeRequisicoes() {
        for (Classe classe : Classe.values()) {
            contadores.put(classe, Caffeine.newBuilder()
                    .expireAfterWrite(classe.janela)
                    // Teto de IPs guardados. Sem ele, um ataque de muitos IPs
                    // distintos enche a heap: a defesa viraria o problema.
                    .maximumSize(50_000)
                    .build());
        }
    }

    @Override
    protected void doFilterInternal(HttpServletRequest requisicao,
                                    HttpServletResponse resposta,
                                    FilterChain corrente)
            throws ServletException, IOException {

        Classe classe = classificar(requisicao);
        if (classe == null) {
            corrente.doFilter(requisicao, resposta);
            return;
        }

        String chave = origem(requisicao);
        AtomicInteger contador = contadores.get(classe)
                .get(chave, ignorada -> new AtomicInteger());

        if (contador.incrementAndGet() > classe.teto) {
            // Só a primeira vez de cada estouro vira log. Sem isso, um ataque
            // sustentado gera uma linha por requisição e enche o disco: o log
            // de segurança seria a negação de serviço.
            if (contador.get() == classe.teto + 1) {
                log.warn("Teto de {} por minuto excedido para {} vindo de {}.",
                        classe.teto, classe, mascarar(chave));
            }
            responderComEstouro(resposta, classe);
            return;
        }

        corrente.doFilter(requisicao, resposta);
    }

    private static Classe classificar(HttpServletRequest requisicao) {
        String caminho = requisicao.getRequestURI();
        if (!caminho.startsWith("/api/")) {
            return null;
        }
        if (caminho.startsWith("/api/v1/auth/")) {
            return Classe.AUTENTICACAO;
        }
        if (caminho.startsWith("/api/v1/traduzir")) {
            return Classe.TRADUCAO;
        }
        if (caminho.contains("/votos")) {
            return Classe.VOTO;
        }
        // Contribuir e moderar exigem conta, e a conta já é rastreável. Não
        // ficam sem teto: caem em LEITURA, que serve de piso para tudo.
        return HttpMethod.GET.matches(requisicao.getMethod())
                || caminho.startsWith("/api/v1/")
                ? Classe.LEITURA
                : null;
    }

    /**
     * De onde veio a requisição.
     *
     * <h2>Por que NÃO lê X-Forwarded-For aqui</h2>
     *
     * <p>A primeira versão lia o cabeçalho direto, para não limitar o mundo
     * inteiro junto pelo endereço do proxy. Era um furo: quem falasse direto
     * com a aplicação escrevia o cabeçalho que quisesse e ganhava uma cota
     * nova a cada requisição, o que anula o limitador exatamente contra quem
     * ele existe para conter.</p>
     *
     * <p>Quem faz esse trabalho corretamente é o {@code RemoteIpValve} do
     * Tomcat, ligado em {@code server.tomcat.remoteip}. Ele só reescreve o
     * endereço quando a conexão veio de um proxy confiável (as faixas
     * privadas, onde o Caddy roda), e ignora o cabeçalho quando a requisição
     * chega de fora. Depois dele, {@code getRemoteAddr()} já é o endereço
     * real do cliente, validado.</p>
     *
     * <p>Ler o cabeçalho por conta própria desfazia essa validação. A regra
     * geral: confie no que a camada de transporte apurou, nunca no que o
     * cliente afirmou.</p>
     */
    private static String origem(HttpServletRequest requisicao) {
        String endereco = requisicao.getRemoteAddr();
        // Teto de tamanho porque este valor vira chave de cache. Um IPv6 com
        // zona cabe em 45 caracteres; acima disso, não é endereço.
        if (endereco == null || endereco.isBlank() || endereco.length() > 45) {
            return "desconhecido";
        }
        return endereco;
    }

    /**
     * Esconde o final do endereço no log.
     *
     * <p>IP é dado pessoal sob a LGPD. Para investigar um abuso, o prefixo
     * basta; guardar o endereço inteiro criaria um registro de quem acessou o
     * serviço, que é justamente o que a seção 8 do documento evita.</p>
     */
    private static String mascarar(String endereco) {
        int ultimo = Math.max(endereco.lastIndexOf('.'), endereco.lastIndexOf(':'));
        return ultimo < 0 ? "?" : endereco.substring(0, ultimo + 1) + "x";
    }

    private static void responderComEstouro(HttpServletResponse resposta, Classe classe)
            throws IOException {
        resposta.setStatus(429);
        resposta.setContentType("application/json;charset=UTF-8");
        resposta.setHeader("Retry-After", String.valueOf(classe.janela.toSeconds()));
        resposta.getWriter().write("""
                {"status":429,"erro":"MUITAS_REQUISICOES",\
                "mensagem":"Muitas requisições seguidas. Espere um pouco e tente de novo.",\
                "campos":[]}""");
    }
}
