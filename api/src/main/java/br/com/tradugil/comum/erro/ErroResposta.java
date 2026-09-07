package br.com.tradugil.comum.erro;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Corpo padrão de erro da API. Um formato só, para todos os erros: cada
 * cliente (web, extensão, Android) trata em um lugar apenas.
 *
 * @param status   código HTTP
 * @param erro     identificador estável, em maiúsculas (ex.: TERMO_NAO_ENCONTRADO)
 * @param mensagem texto exibível ao usuário final, em português
 * @param campos   erros de validação por campo, quando houver
 * @param momento  instante do erro, útil para casar com o log do servidor
 */
public record ErroResposta(
        int status,
        String erro,
        String mensagem,
        List<ErroDeCampo> campos,
        OffsetDateTime momento
) {
    public static ErroResposta de(int status, String erro, String mensagem) {
        return new ErroResposta(status, erro, mensagem, List.of(), OffsetDateTime.now());
    }

    public static ErroResposta comCampos(int status, String erro, String mensagem,
                                         List<ErroDeCampo> campos) {
        return new ErroResposta(status, erro, mensagem, campos, OffsetDateTime.now());
    }

    public record ErroDeCampo(String campo, String mensagem) {
    }
}
