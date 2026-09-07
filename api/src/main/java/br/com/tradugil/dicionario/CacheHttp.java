package br.com.tradugil.dicionario;

import org.springframework.http.CacheControl;

import java.util.concurrent.TimeUnit;

/**
 * Política de cache HTTP das leituras públicas do dicionário.
 *
 * <h2>Por que isto importa para escalar</h2>
 *
 * <p>Uma resposta com {@code Cache-Control: public} pode ser guardada e
 * reentregue pelo Caddy, por um CDN e pelo próprio navegador, sem chegar à
 * aplicação. É a única forma de crescimento que <b>reduz</b> o trabalho do
 * servidor em vez de distribuí-lo: um verbete popular passa a custar uma
 * consulta a cada dez minutos, não uma por visita.</p>
 *
 * <p>Custa um cabeçalho. É o melhor retorno por linha de código em todo o
 * caminho de leitura, e é o que permite ao free tier do Neon aguentar um pico
 * de acessos que de outra forma o derrubaria.</p>
 *
 * <h2>Por que dez minutos, e por que public</h2>
 *
 * <p>Dez minutos é curto o bastante para uma correção da curadoria chegar
 * rápido, incluindo a correção de um termo de risco, que é o caso em que
 * atraso custa caro. E é longo o bastante para absorver a rajada de quem abre
 * o catálogo e navega por várias prateleiras seguidas.</p>
 *
 * <p>{@code public} porque estas respostas não dependem de quem pergunta:
 * não há sessão, não há cabeçalho de autorização e o conteúdo é o mesmo para
 * todo mundo. Se algum dia uma resposta de leitura passar a variar por
 * usuário, esta constante deixa de servir para ela, e a troca precisa ser
 * consciente: por isso a política mora aqui, com o nome dizendo a qual caso
 * ela se aplica, em vez de espalhada em cada controlador.</p>
 *
 * <p>{@code stale-while-revalidate} deixa o intermediário devolver a cópia
 * vencida enquanto busca a nova em segundo plano. Quem pergunta no instante
 * do vencimento recebe na hora, em vez de esperar a ida ao banco.</p>
 */
final class CacheHttp {

    private CacheHttp() {
    }

    static final CacheControl DE_LEITURA_PUBLICA = CacheControl
            .maxAge(10, TimeUnit.MINUTES)
            .cachePublic()
            .staleWhileRevalidate(java.time.Duration.ofMinutes(60));
}
