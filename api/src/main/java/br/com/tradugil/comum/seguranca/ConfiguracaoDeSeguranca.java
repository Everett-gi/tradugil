package br.com.tradugil.comum.seguranca;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.CrossOriginOpenerPolicyHeaderWriter;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import org.springframework.security.web.header.writers.CrossOriginResourcePolicyHeaderWriter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter.ReferrerPolicy;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Baseline de segurança da API.
 *
 * <p>A leitura do dicionário é pública de propósito: exigir cadastro para
 * entender uma mensagem afastaria exatamente o público que o produto quer
 * atender. O que exige conta é contribuir e moderar: onde a identidade
 * importa para a trilha de auditoria.</p>
 *
 * <p>A regra final é {@code anyRequest().denyAll()}, e não
 * {@code authenticated()}: um endpoint novo criado sem que ninguém pense em
 * autorização nasce <b>fechado</b>. O modo de falha vira "não funciona e
 * alguém reclama", em vez de "funciona para qualquer um e ninguém percebe".</p>
 */
@Configuration
@EnableWebSecurity
public class ConfiguracaoDeSeguranca {

    private final List<String> origensPermitidas;
    private final boolean documentacaoAberta;

    public ConfiguracaoDeSeguranca(
            @Value("${tradugil.cors.origens:http://localhost:5173}")
            List<String> origensPermitidas,
            @Value("${tradugil.documentacao.publica:false}") boolean documentacaoAberta) {
        this.origensPermitidas = origensPermitidas;
        this.documentacaoAberta = documentacaoAberta;
    }

    @Bean
    public SecurityFilterChain cadeiaDeFiltros(
            HttpSecurity http,
            JwtAuthenticationConverter conversorDeAutenticacao) throws Exception {

        /*
         * A documentação da API descreve toda a superfície do serviço: rotas,
         * formatos, códigos de erro. É material de trabalho para quem
         * desenvolve e material de reconhecimento para quem ataca.
         *
         * Aberta em desenvolvimento, fechada por padrão. Fechada por PADRÃO, e
         * não fechada quando um perfil "producao" estiver ativo: assim o
         * esquecimento resulta em documentação indisponível, e não em
         * documentação publicada sem ninguém ter decidido isso.
         */
        if (documentacaoAberta) {
            http.authorizeHttpRequests(auth -> auth
                    .requestMatchers("/v3/api-docs/**", "/swagger-ui/**",
                            "/swagger-ui.html").permitAll());
        }

        http
                // Sem CSRF porque não há sessão nem cookie: a API é sem estado
                // e será consumida por token. Manter o filtro só bloquearia
                // clientes nativos sem proteger nada.
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .headers(h -> h
                        .frameOptions(f -> f.deny())
                        // Esta API só devolve JSON. Uma política que nega tudo
                        // é a certa: se algum dia uma resposta for renderizada
                        // como HTML por engano, ela não consegue carregar nada.
                        .contentSecurityPolicy(csp ->
                                csp.policyDirectives("default-src 'none'; frame-ancestors 'none'"))
                        // Um ano, e valendo para os subdomínios. Depois da
                        // primeira visita o navegador se recusa a falar HTTP
                        // com este domínio, o que fecha a janela do "primeiro
                        // acesso interceptado antes do redirecionamento".
                        .httpStrictTransportSecurity(hsts -> hsts
                                .includeSubDomains(true)
                                .maxAgeInSeconds(31_536_000))
                        // Nenhum Referer sai daqui. A URL de uma requisição
                        // pode conter o termo consultado, e o termo consultado
                        // é conteúdo da tela de alguém.
                        .referrerPolicy(rp -> rp.policy(
                                ReferrerPolicy.NO_REFERRER))
                        // Nada disto é usado pela API. Negar explicitamente
                        // custa um cabeçalho e fecha a porta antes de alguém
                        // abrir por engano.
                        //
                        // Escrito como StaticHeadersWriter e não pelo atalho
                        // do DSL: o nome do método mudou entre versões do
                        // Spring Security (permissionsPolicy virou
                        // permissionsPolicyHeader), e o cabeçalho literal não
                        // quebra na próxima atualização.
                        .addHeaderWriter(new StaticHeadersWriter("Permissions-Policy",
                                "geolocation=(), camera=(), microphone=(), "
                                        + "payment=(), usb=(), interest-cohort=()"))
                        .crossOriginOpenerPolicy(coop -> coop.policy(
                                CrossOriginOpenerPolicyHeaderWriter
                                        .CrossOriginOpenerPolicy.SAME_ORIGIN))
                        .crossOriginResourcePolicy(corp -> corp.policy(
                                CrossOriginResourcePolicyHeaderWriter
                                        .CrossOriginResourcePolicy.SAME_SITE)))
                .authorizeHttpRequests(auth -> auth
                        // Leitura do dicionário: pública, sem cadastro.
                        .requestMatchers(HttpMethod.GET, "/api/v1/girias/**").permitAll()
                        // As prateleiras do catálogo. Ficam fora de /girias
                        // porque /girias/{termo} engoliria o caminho, então
                        // precisam da própria linha aqui: o denyAll() do fim
                        // devolveu 401 para elas até esta linha existir.
                        .requestMatchers(HttpMethod.GET, "/api/v1/categorias").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/traduzir").permitAll()
                        .requestMatchers(HttpMethod.POST,
                                "/api/v1/girias/definicoes/*/votos").permitAll()
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/dicionario/pacotes/**").permitAll()

                        // Entrar e renovar precisam ser públicos por definição:
                        // é justamente aqui que quem ainda não tem token vem
                        // buscar um.
                        .requestMatchers(HttpMethod.POST,
                                "/api/v1/auth/registro",
                                "/api/v1/auth/login",
                                "/api/v1/auth/refresh",
                                "/api/v1/auth/logout").permitAll()

                        .requestMatchers("/actuator/health").permitAll()

                        // Sair de todos os aparelhos precisa saber quem é você.
                        .requestMatchers(HttpMethod.POST,
                                "/api/v1/auth/logout-geral").authenticated()

                        // Contribuir exige conta; moderar exige papel. Os
                        // papéis não são hierárquicos no Spring Security: um
                        // MODERATOR não é automaticamente um USER, então
                        // quem pode moderar precisa ser listado aqui também
                        // para continuar podendo contribuir.
                        .requestMatchers("/api/v1/contribuicoes/**")
                                .hasAnyRole("USER", "MODERATOR", "ADMIN")
                        .requestMatchers("/api/v1/moderacao/**")
                                .hasAnyRole("MODERATOR", "ADMIN")

                        .anyRequest().denyAll())
                .oauth2ResourceServer(oauth -> oauth
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(conversorDeAutenticacao)));
        return http.build();
    }

    @Bean
    public CorsConfigurationSource fonteDeConfiguracaoCors() {
        CorsConfiguration configuracao = new CorsConfiguration();
        // Lista explícita, nunca "*": a extensão de navegador e o site são
        // origens conhecidas, e liberar qualquer origem deixaria qualquer
        // página da web consumir a cota de IA da API em nome do usuário.
        configuracao.setAllowedOrigins(origensPermitidas);
        configuracao.setAllowedMethods(List.of("GET", "POST", "PATCH", "DELETE", "OPTIONS"));
        configuracao.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuracao.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource fonte = new UrlBasedCorsConfigurationSource();
        fonte.registerCorsConfiguration("/api/**", configuracao);
        return fonte;
    }
}
