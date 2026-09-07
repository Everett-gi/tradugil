package br.com.tradugil.ia;

import com.anthropic.client.AnthropicClient;
import com.anthropic.models.messages.MessageCreateParams;
import com.anthropic.models.messages.StructuredMessageCreateParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Nível 4 da cascata, contra a API da Anthropic.
 *
 * <p>O modelo é o Haiku, conforme a seção 6 do documento de especificação:
 * custo por consulta muito baixo, boa qualidade em português e resposta
 * estruturada. A cascata garante que ele seja chamado raramente: a meta é
 * mais de 85% das consultas resolvidas antes de chegar aqui.</p>
 *
 * <p>A resposta usa saída estruturada: o esquema de {@link ExplicacaoDaIa} é
 * derivado do próprio record, e a API garante que a resposta se encaixe nele.
 * Isso substitui o "peça JSON e torça" por uma garantia de formato, e é
 * também a terceira camada da defesa contra prompt injection descrita em
 * {@link PromptDeLexicografo}, porque uma instrução injetada não tem campo
 * de texto livre por onde escapar.</p>
 */
public class ClienteClaude implements ClienteDeIa {

    private static final Logger log = LoggerFactory.getLogger(ClienteClaude.class);

    /** Ver seção 6 do documento: escolhido por custo por consulta. */
    private static final String MODELO = "claude-haiku-4-5";

    /**
     * Uma explicação de gíria são poucas frases. O teto existe para o caso
     * patológico: se o modelo começar a produzir texto longo, é sinal de
     * que algo deu errado, e pagar por isso não ajuda ninguém.
     */
    private static final long MAXIMO_DE_TOKENS = 1_024L;

    private final AnthropicClient cliente;
    private final CotaDeIa cota;

    public ClienteClaude(AnthropicClient cliente, CotaDeIa cota) {
        this.cliente = cliente;
        this.cota = cota;
        log.info("Camada de IA ligada, modelo {}", MODELO);
    }

    @Override
    public boolean estaDisponivel() {
        return cota.temSaldo();
    }

    @Override
    public Optional<ExplicacaoDaIa> explicar(String termo, String contexto) {
        if (!cota.consumir()) {
            // Cota diária estourada. Do lado do usuário é indistinguível de
            // "a IA não soube": o termo segue para a fila de curadoria e a
            // consulta responde normalmente com o que o dicionário tem.
            log.warn("Cota diária de IA esgotada; termo '{}' não foi consultado.", termo);
            return Optional.empty();
        }

        try {
            StructuredMessageCreateParams<ExplicacaoDaIa> params = MessageCreateParams.builder()
                    .model(MODELO)
                    .maxTokens(MAXIMO_DE_TOKENS)
                    .system(PromptDeLexicografo.sistema())
                    .addUserMessage(PromptDeLexicografo.mensagem(termo, contexto))
                    .outputConfig(ExplicacaoDaIa.class)
                    .build();

            Optional<ExplicacaoDaIa> resposta = cliente.messages().create(params).content()
                    .stream()
                    .flatMap(bloco -> bloco.text().stream())
                    .map(bloco -> bloco.text())
                    .findFirst();

            if (resposta.isEmpty()) {
                return Optional.empty();
            }

            ExplicacaoDaIa explicacao = resposta.get();
            if (!explicacao.ehUtilizavel()) {
                // Quarta camada de defesa: o esquema garantiu o formato, mas
                // não o conteúdo. Descarta em silêncio: do lado de fora é
                // igual a "a IA não soube", e é melhor assim: a alternativa
                // seria exibir justamente o que não passou na validação.
                log.debug("Resposta da IA descartada na validação para o termo '{}'", termo);
                return Optional.empty();
            }
            return Optional.of(explicacao);

        } catch (RuntimeException e) {
            // A IA é a última camada e é opcional por desenho: se ela falhar,
            // a consulta ainda tem tudo que os níveis 0 a 2 encontraram. Uma
            // indisponibilidade da API externa não pode virar erro 500 numa
            // requisição que já tinha resposta útil para dar.
            log.warn("Falha ao consultar a IA para o termo '{}': {}", termo, e.toString());
            return Optional.empty();
        }
    }
}
