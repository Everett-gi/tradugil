package br.com.tradugil.comum.erro;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;

import static org.assertj.core.api.Assertions.assertThat;

class TratadorGlobalDeErrosTest {

    private final TratadorGlobalDeErros tratador = new TratadorGlobalDeErros();

    /**
     * Corpo ilegível é erro de quem chamou, não do servidor.
     *
     * <p>Antes deste tratamento, um JSON quebrado ou enviado em codificação
     * errada caía no handler genérico e virava 500. Isso é errado em dois
     * sentidos: culpa o servidor por um erro do cliente, e enche o log de
     * erro de aplicação com problema alheio, escondendo as falhas reais.</p>
     */
    @Test
    @DisplayName("JSON ilegível devolve 400, e não 500")
    void jsonIlegivelDevolve400() {
        ResponseEntity<ErroResposta> resposta = tratador.corpoIlegivel(
                new HttpMessageNotReadableException("Invalid UTF-8 middle byte 0x20", null, null));

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(resposta.getBody()).isNotNull();
        assertThat(resposta.getBody().erro()).isEqualTo("CORPO_INVALIDO");
    }

    @Test
    @DisplayName("a mensagem devolvida não repete o conteúdo recebido")
    void naoVazaOConteudoRecebido() {
        // O corpo da requisição pode ser o texto que o usuário mandou
        // traduzir, lido da tela dele. Repeti-lo na resposta ou no log é
        // exatamente o que a seção 8 do documento proíbe.
        String segredo = "conversa privada que nao pode vazar";

        ResponseEntity<ErroResposta> resposta = tratador.corpoIlegivel(
                new HttpMessageNotReadableException(segredo, null, null));

        assertThat(resposta.getBody().mensagem()).doesNotContain(segredo);
    }

    @Test
    @DisplayName("erro inesperado não devolve a mensagem interna")
    void erroInesperadoNaoVazaMensagemInterna() {
        String interno = "senha=abc123 no host interno db-prod-01";

        ResponseEntity<ErroResposta> resposta =
                tratador.inesperado(new IllegalStateException(interno));

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(resposta.getBody().mensagem()).doesNotContain(interno);
        assertThat(resposta.getBody().mensagem()).doesNotContain("senha");
    }
}
