package br.com.tradugiria.comum.seguranca;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Baseline de segurança da API.
 *
 * <p>A leitura do dicionário é pública de propósito: exigir cadastro para
 * entender uma mensagem afastaria exatamente o público que o produto quer
 * atender. O que exige conta é contribuir e moderar — onde a identidade
 * importa para a trilha de auditoria.</p>
 *
 * <p>A autenticação JWT entra na F1, junto com os endpoints de contribuição.
 * Até lá, tudo que é escrita permanece negado por padrão: a regra final
 * {@code anyRequest().denyAll()} garante que um endpoint novo criado sem
 * pensar em autorização nasça fechado, e não aberto.</p>
 */
@Configuration
@EnableWebSecurity
public class ConfiguracaoDeSeguranca {

    private final List<String> origensPermitidas;

    public ConfiguracaoDeSeguranca(
            @org.springframework.beans.factory.annotation.Value(
                    "${tradugiria.cors.origens:http://localhost:5173}")
            List<String> origensPermitidas) {
        this.origensPermitidas = origensPermitidas;
    }

    @Bean
    public SecurityFilterChain cadeiaDeFiltros(HttpSecurity http) throws Exception {
        http
                // Sem CSRF porque não há sessão nem cookie: a API é sem estado
                // e será consumida por token. Manter o filtro só bloquearia
                // clientes nativos sem proteger nada.
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .headers(h -> h
                        .frameOptions(f -> f.deny())
                        .contentSecurityPolicy(csp ->
                                csp.policyDirectives("default-src 'none'; frame-ancestors 'none'")))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/api/v1/girias/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/traduzir").permitAll()
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/dicionario/pacotes/**").permitAll()
                        .requestMatchers("/actuator/health").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**",
                                "/swagger-ui.html").permitAll()
                        .anyRequest().denyAll());
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

    @Bean
    public PasswordEncoder codificadorDeSenha() {
        return new BCryptPasswordEncoder();
    }
}
