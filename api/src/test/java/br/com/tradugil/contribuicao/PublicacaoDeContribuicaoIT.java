package br.com.tradugil.contribuicao;

import br.com.tradugil.TestecomBanco;
import br.com.tradugil.contribuicao.ContribuicaoDtos.ContribuicaoResposta;
import br.com.tradugil.contribuicao.ContribuicaoDtos.DecisaoDeModeracao;
import br.com.tradugil.contribuicao.ContribuicaoDtos.NovaContribuicao;
import br.com.tradugil.dicionario.Fonte.TipoDeFonte;
import br.com.tradugil.dicionario.ServicoDeDicionario;
import br.com.tradugil.identidade.RepositorioDeUsuario;
import br.com.tradugil.identidade.Usuario;
import br.com.tradugil.identidade.Usuario.Papel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * O caminho da contribuição até o dicionário.
 *
 * <p>Existe por causa de um beco sem saída real: aprovar marcava a
 * contribuição como APROVADA, gravava a auditoria e <b>não criava o
 * verbete</b>. Quem contribuía via a própria sugestão aprovada, procurava o
 * termo e não encontrava nada.</p>
 *
 * <p>Nada acusava. Os testes de moderação conferiam o status e a trilha de
 * auditoria, que estavam certos; ninguém conferia o outro lado, que é o
 * dicionário. O nível 5 da cascata existe para o dicionário não envelhecer, e
 * o único caminho de entrada de termo novo terminava num beco.</p>
 */
@TestecomBanco
class PublicacaoDeContribuicaoIT {

    @Autowired
    private ServicoDeContribuicao servico;

    @Autowired
    private ServicoDeDicionario dicionario;

    @Autowired
    private RepositorioDeUsuario usuarios;

    @Autowired
    private PasswordEncoder codificador;

    @Test
    @DisplayName("contribuição aprovada vira verbete, com a origem visível")
    void aprovacaoPublica() {
        Usuario autor = criar(Papel.USER);
        Usuario moderador = criar(Papel.MODERATOR);
        String termo = "termoteste" + System.nanoTime();

        ContribuicaoResposta proposta = servico.propor(autor.getId(),
                new NovaContribuicao(termo, "pt-BR",
                        "Explicação de teste, com tamanho suficiente para passar."));

        servico.decidir(moderador.getId(), proposta.id(), new DecisaoDeModeracao(true, null));

        var verbete = dicionario.porTermo(termo, "pt-BR");
        assertThat(verbete.termo()).isEqualTo(termo);
        assertThat(verbete.definicoes()).hasSize(1);
        /*
         * COMUNIDADE, e não CURADORIA. Uma explicação enviada por usuário não
         * pode se passar por verbete escrito pela curadoria, mesmo depois de
         * revisada: a interface exibe essa origem, e é o que permite a quem
         * lê calibrar a confiança.
         */
        assertThat(verbete.definicoes().getFirst().fonte()).isEqualTo(TipoDeFonte.COMUNIDADE);
    }

    @Test
    @DisplayName("contribuição rejeitada não chega ao dicionário")
    void rejeicaoNaoPublica() {
        Usuario autor = criar(Papel.USER);
        Usuario moderador = criar(Papel.MODERATOR);
        String termo = "termorejeitado" + System.nanoTime();

        ContribuicaoResposta proposta = servico.propor(autor.getId(),
                new NovaContribuicao(termo, "pt-BR",
                        "Explicação de teste, com tamanho suficiente para passar."));

        servico.decidir(moderador.getId(), proposta.id(),
                new DecisaoDeModeracao(false, "Não é uma gíria."));

        assertThat(dicionario.localizar(termo, "pt-BR")).isEmpty();
    }

    @Test
    @DisplayName("termo que já existe ganha um sentido, e não um verbete paralelo")
    void termoExistenteGanhaSentido() {
        /*
         * Dois verbetes para a mesma palavra fazem a resposta depender da
         * ordem das linhas no banco. É o erro do "PogChamp", que a V28 teve
         * de desfazer, e ele poderia entrar de novo por aqui sem esta regra.
         */
        Usuario autor = criar(Papel.USER);
        Usuario moderador = criar(Papel.MODERATOR);

        int sentidosAntes = dicionario.porTermo("cringe", "en").definicoes().size();

        ContribuicaoResposta proposta = servico.propor(autor.getId(),
                new NovaContribuicao("cringe", "en",
                        "Sentido novo escrito por alguém da comunidade, para teste."));
        servico.decidir(moderador.getId(), proposta.id(), new DecisaoDeModeracao(true, null));

        var verbete = dicionario.porTermo("cringe", "en");
        assertThat(verbete.termo()).isEqualTo("cringe");
        assertThat(verbete.definicoes()).hasSize(sentidosAntes + 1);
    }

    @Test
    @DisplayName("explicação repetida não entra duas vezes")
    void naoRepeteExplicacao() {
        Usuario autor = criar(Papel.USER);
        Usuario moderador = criar(Papel.MODERATOR);
        String termo = "termorepetido" + System.nanoTime();
        String explicacao = "A mesma explicação enviada duas vezes, para o teste.";

        var primeira = servico.propor(autor.getId(),
                new NovaContribuicao(termo, "pt-BR", explicacao));
        servico.decidir(moderador.getId(), primeira.id(), new DecisaoDeModeracao(true, null));

        var segunda = servico.propor(autor.getId(),
                new NovaContribuicao(termo, "pt-BR", explicacao));
        servico.decidir(moderador.getId(), segunda.id(), new DecisaoDeModeracao(true, null));

        // Aprovar duas vezes a mesma coisa é erro de moderação, e o pior
        // resultado possível é o verbete mostrar a frase repetida na tela.
        assertThat(dicionario.porTermo(termo, "pt-BR").definicoes()).hasSize(1);
    }

    @Test
    @DisplayName("o verbete publicado é encontrado pela busca normalizada")
    void publicadoEhEncontravel() {
        /*
         * A prova de que as chaves de busca foram calculadas, e não escritas.
         * Uma chave errada entra no banco sem reclamar e o verbete some da
         * busca para sempre: é o mesmo erro silencioso que o gerador de
         * migrações existe para evitar, e criar verbete por código reabriria
         * essa porta se as chaves viessem de fora.
         */
        Usuario autor = criar(Papel.USER);
        Usuario moderador = criar(Papel.MODERATOR);
        String termo = "Termo Com Acentuação " + System.nanoTime();

        var proposta = servico.propor(autor.getId(),
                new NovaContribuicao(termo, "pt-BR",
                        "Explicação de teste, com tamanho suficiente para passar."));
        servico.decidir(moderador.getId(), proposta.id(), new DecisaoDeModeracao(true, null));

        // Procurado sem acento e em minúsculas, como o usuário digitaria.
        assertThat(dicionario.localizar(
                termo.toLowerCase(java.util.Locale.ROOT).replace("ç", "c").replace("ã", "a"),
                "pt-BR"))
                .isPresent();
    }

    private Usuario criar(Papel papel) {
        return usuarios.save(new Usuario(
                "teste-" + UUID.randomUUID() + "@exemplo.com",
                codificador.encode("senhaboa123"),
                papel));
    }
}
