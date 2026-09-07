package br.com.tradugil.comum.seguranca;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * O limitador por IP.
 *
 * <p>Teste de unidade, sem banco: o filtro não toca em persistência nenhuma, e
 * exigir Postgres aqui só faria a checagem ser pulada na máquina de quem não
 * tem um, que é exatamente onde ela precisa rodar.</p>
 */
class LimitadorDeRequisicoesTest {

    private final LimitadorDeRequisicoes limitador = new LimitadorDeRequisicoes();

    @Test
    @DisplayName("X-Forwarded-For forjado não rende cota nova")
    void cabecalhoForjadoNaoBurlaOTeto() throws Exception {
        /*
         * A FALHA QUE ESTE TESTE FECHA
         *
         * A primeira versão lia X-Forwarded-For direto para descobrir o
         * cliente. Quem falasse com a aplicação sem passar pelo proxy escrevia
         * o cabeçalho que quisesse e ganhava um balde novo a cada requisição:
         * o limitador continuava lá, contando, e não limitava ninguém.
         *
         * Quem valida esse cabeçalho é o RemoteIpValve do Tomcat, que só o
         * respeita quando a conexão vem de um proxy confiável. Depois dele,
         * getRemoteAddr() já é o endereço real. Ler o cabeçalho por conta
         * própria desfazia essa validação.
         *
         * Aqui o endereço de conexão é sempre o mesmo, e o cabeçalho muda a
         * cada chamada, que é exatamente o que o atacante faria.
         */
        int teto = 10;
        int excedentes = 0;

        for (int i = 0; i < teto + 5; i++) {
            MockHttpServletRequest requisicao = pedidoDeLogin();
            requisicao.setRemoteAddr("203.0.113.7");
            requisicao.addHeader("X-Forwarded-For", "10.0.0." + i);

            MockHttpServletResponse resposta = new MockHttpServletResponse();
            limitador.doFilter(requisicao, resposta, mock(FilterChain.class));
            if (resposta.getStatus() == 429) {
                excedentes++;
            }
        }

        assertThat(excedentes)
                .as("as 5 chamadas além do teto precisam ser recusadas, "
                        + "mesmo com o cabeçalho trocando a cada uma")
                .isEqualTo(5);
    }

    @Test
    @DisplayName("endereços diferentes têm cotas independentes")
    void cotaEhPorEndereco() throws Exception {
        // O outro lado da mesma regra: limitar por IP não pode significar
        // limitar todo mundo junto quando o tráfego é legítimo.
        for (int i = 0; i < 10; i++) {
            MockHttpServletRequest primeiro = pedidoDeLogin();
            primeiro.setRemoteAddr("198.51.100.1");
            limitador.doFilter(primeiro, new MockHttpServletResponse(), mock(FilterChain.class));
        }

        MockHttpServletRequest outro = pedidoDeLogin();
        outro.setRemoteAddr("198.51.100.2");
        MockHttpServletResponse resposta = new MockHttpServletResponse();
        limitador.doFilter(outro, resposta, mock(FilterChain.class));

        assertThat(resposta.getStatus()).isNotEqualTo(429);
    }

    @Test
    @DisplayName("consultar o dicionário tem cota separada de tentar entrar")
    void classesDeTrafegoNaoSeMisturam() throws Exception {
        // Um único teto para tudo estaria certo para a leitura do dicionário e
        // errado para o login: 120 tentativas de senha por minuto é muita
        // coisa. Gastar a cota de uma classe não pode fechar a outra.
        for (int i = 0; i < 15; i++) {
            MockHttpServletRequest login = pedidoDeLogin();
            login.setRemoteAddr("192.0.2.50");
            limitador.doFilter(login, new MockHttpServletResponse(), mock(FilterChain.class));
        }

        MockHttpServletRequest leitura = new MockHttpServletRequest("GET", "/api/v1/categorias");
        leitura.setRemoteAddr("192.0.2.50");
        MockHttpServletResponse resposta = new MockHttpServletResponse();
        limitador.doFilter(leitura, resposta, mock(FilterChain.class));

        assertThat(resposta.getStatus()).isNotEqualTo(429);
    }

    @Test
    @DisplayName("a recusa diz quanto tempo esperar")
    void recusaTrazRetryAfter() throws Exception {
        MockHttpServletResponse ultima = new MockHttpServletResponse();
        for (int i = 0; i < 12; i++) {
            MockHttpServletRequest requisicao = pedidoDeLogin();
            requisicao.setRemoteAddr("192.0.2.99");
            ultima = new MockHttpServletResponse();
            limitador.doFilter(requisicao, ultima, mock(FilterChain.class));
        }

        assertThat(ultima.getStatus()).isEqualTo(429);
        // Sem Retry-After, um cliente bem-comportado não tem como saber quando
        // voltar, e a saída dele é tentar de novo na hora, que é o oposto do
        // que a recusa pediu.
        assertThat(ultima.getHeader("Retry-After")).isEqualTo("60");
        assertThat(ultima.getContentAsString()).contains("MUITAS_REQUISICOES");
    }

    private static MockHttpServletRequest pedidoDeLogin() {
        return new MockHttpServletRequest("POST", "/api/v1/auth/login");
    }
}
