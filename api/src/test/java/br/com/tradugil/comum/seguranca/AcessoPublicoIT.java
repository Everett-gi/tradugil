package br.com.tradugil.comum.seguranca;

import br.com.tradugil.TestecomBanco;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Prova que a leitura do dicionário continua aberta e que o resto continua
 * fechado.
 *
 * <p>Existe por um erro concreto: o catálogo ganhou
 * {@code GET /api/v1/categorias}, o endereço não entrou na lista de
 * liberados, e o {@code anyRequest().denyAll()} do fim da configuração
 * devolveu 401 para todo mundo. O padrão fechado funcionou como devia; o que
 * faltou foi alguém perceber antes de a tela quebrar.</p>
 *
 * <p>A metade de baixo é a mais importante das duas. Uma linha
 * {@code permitAll} larga demais, escrita para consertar um 401 como aquele,
 * abriria a contribuição e a moderação sem que nada ficasse vermelho.</p>
 */
@TestecomBanco
@AutoConfigureMockMvc
class AcessoPublicoIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("as prateleiras do catálogo respondem sem token")
    void categoriasSaoPublicas() throws Exception {
        mockMvc.perform(get("/api/v1/categorias")).andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/categorias?modoFamilia=false"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("a busca e a navegação por categoria respondem sem token")
    void buscaEPrateleiraSaoPublicas() throws Exception {
        mockMvc.perform(get("/api/v1/girias?q=cringe")).andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/girias?categoria=gaming")).andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/girias/cringe")).andExpect(status().isOk());
    }

    @Test
    @DisplayName("traduzir responde sem token")
    void traduzirEPublico() throws Exception {
        mockMvc.perform(post("/api/v1/traduzir")
                        .contentType("application/json")
                        .content("{\"texto\":\"que cringe\"}"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("contribuir e moderar continuam exigindo conta")
    void escritaContinuaFechada() throws Exception {
        mockMvc.perform(post("/api/v1/contribuicoes")
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/v1/moderacao/contribuicoes"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("endereço desconhecido não passa pelo denyAll")
    void enderecoDesconhecidoEhNegado() throws Exception {
        // O 404 do Spring só apareceria depois da autorização. Aqui a
        // resposta esperada é a da regra final, e não a de rota inexistente:
        // é isso que faz um endpoint novo nascer fechado.
        mockMvc.perform(get("/api/v1/inventado"))
                .andExpect(status().isUnauthorized());
    }
}
