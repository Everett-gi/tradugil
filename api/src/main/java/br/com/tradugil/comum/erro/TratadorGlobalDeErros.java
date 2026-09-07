package br.com.tradugil.comum.erro;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class TratadorGlobalDeErros {

    private static final Logger log = LoggerFactory.getLogger(TratadorGlobalDeErros.class);

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroResposta> naoEncontrado(RecursoNaoEncontradoException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErroResposta.de(404, "RECURSO_NAO_ENCONTRADO", e.getMessage()));
    }

    @ExceptionHandler(RegraDeNegocioException.class)
    public ResponseEntity<ErroResposta> regraDeNegocio(RegraDeNegocioException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ErroResposta.de(409, e.getCodigo(), e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResposta> validacao(MethodArgumentNotValidException e) {
        List<ErroResposta.ErroDeCampo> campos = e.getBindingResult().getFieldErrors().stream()
                .map(f -> new ErroResposta.ErroDeCampo(f.getField(), f.getDefaultMessage()))
                .toList();
        return ResponseEntity.badRequest().body(ErroResposta.comCampos(
                400, "DADOS_INVALIDOS", "Verifique os campos informados.", campos));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErroResposta> acessoNegado(AccessDeniedException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ErroResposta.de(
                403, "ACESSO_NEGADO", "Você não tem permissão para esta ação."));
    }

    /**
     * Rede de segurança da privacidade: o texto que o usuário mandou traduzir
     * pode ser uma conversa privada lida da tela dele. Se ele aparecesse na
     * mensagem de uma exceção qualquer, seria devolvido ao cliente e gravado
     * no log — exatamente o que a seção 8 do documento proíbe. Por isso a
     * mensagem interna morre aqui, e o cliente recebe um texto genérico.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResposta> inesperado(Exception e) {
        log.error("Erro não tratado", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErroResposta.de(
                500, "ERRO_INTERNO", "Erro inesperado. Tente novamente em instantes."));
    }
}
