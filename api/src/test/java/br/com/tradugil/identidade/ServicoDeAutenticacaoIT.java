package br.com.tradugil.identidade;

import br.com.tradugil.TestecomBanco;
import br.com.tradugil.comum.erro.RegraDeNegocioException;
import br.com.tradugil.identidade.IdentidadeDtos.Credenciais;
import br.com.tradugil.identidade.IdentidadeDtos.NovoUsuario;
import br.com.tradugil.identidade.IdentidadeDtos.ParDeTokens;
import br.com.tradugil.identidade.Usuario.Papel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@TestecomBanco
class ServicoDeAutenticacaoIT {

    @Autowired
    private ServicoDeAutenticacao servico;

    @Autowired
    private RepositorioDeUsuario repositorioDeUsuario;

    @Autowired
    private RepositorioDeToken repositorioDeToken;

    @Test
    @DisplayName("registro cria a conta como USER e já devolve os tokens")
    void registroCriaContaComoUser() {
        String email = emailNovo();
        ParDeTokens tokens = servico.registrar(new NovoUsuario(email, "senhaboa123"));

        assertThat(tokens.accessToken()).isNotBlank();
        assertThat(tokens.refreshToken()).isNotBlank();
        assertThat(tokens.papel()).isEqualTo(Papel.USER);
        assertThat(repositorioDeUsuario.findByEmail(email)).isPresent();
    }

    @Test
    @DisplayName("a senha nunca é guardada em texto")
    void senhaNaoEGuardadaEmTexto() {
        String email = emailNovo();
        servico.registrar(new NovoUsuario(email, "senhaboa123"));

        String guardado = repositorioDeUsuario.findByEmail(email).orElseThrow().getSenhaHash();

        assertThat(guardado).doesNotContain("senhaboa123");
        // BCrypt: o prefixo identifica o algoritmo e o custo.
        assertThat(guardado).startsWith("$2");
    }

    @Test
    @DisplayName("o e-mail é normalizado para minúsculas")
    void emailNormalizado() {
        String email = emailNovo();
        servico.registrar(new NovoUsuario(email.toUpperCase(java.util.Locale.ROOT), "senhaboa123"));

        // Sem isso, "Maria@x.com" e "maria@x.com" viram duas contas e a
        // pessoa não consegue entrar com o que ela acha que cadastrou.
        assertThat(repositorioDeUsuario.findByEmail(email)).isPresent();
    }

    @Test
    @DisplayName("cadastro repetido não revela que o e-mail já existe")
    void cadastroRepetidoNaoRevelaExistencia() {
        String email = emailNovo();
        servico.registrar(new NovoUsuario(email, "senhaboa123"));

        assertThatThrownBy(() -> servico.registrar(new NovoUsuario(email, "outrasenha123")))
                .isInstanceOf(RegraDeNegocioException.class)
                // A mensagem não pode dizer "e-mail já cadastrado": isso
                // transformaria o registro num oráculo de quem tem conta aqui.
                .hasMessageNotContainingAny("já cadastrado", "já existe", "em uso");
    }

    @Test
    @DisplayName("senha errada e e-mail inexistente dão a mesma resposta")
    void senhaErradaEEmailInexistenteSaoIguais() {
        String email = emailNovo();
        servico.registrar(new NovoUsuario(email, "senhaboa123"));

        String mensagemSenhaErrada = capturarMensagem(
                () -> servico.autenticar(new Credenciais(email, "senhaerrada")));
        String mensagemEmailInexistente = capturarMensagem(
                () -> servico.autenticar(new Credenciais(emailNovo(), "senhaboa123")));

        assertThat(mensagemSenhaErrada).isEqualTo(mensagemEmailInexistente);
    }

    @Test
    @DisplayName("login com a senha certa devolve tokens novos")
    void loginFunciona() {
        String email = emailNovo();
        servico.registrar(new NovoUsuario(email, "senhaboa123"));

        ParDeTokens tokens = servico.autenticar(new Credenciais(email, "senhaboa123"));

        assertThat(tokens.accessToken()).isNotBlank();
        assertThat(tokens.expiraEmSegundos()).isEqualTo(900);
    }

    @Test
    @DisplayName("a renovação troca o refresh token por um novo")
    void renovacaoRotacionaOToken() {
        ParDeTokens primeiro = servico.registrar(new NovoUsuario(emailNovo(), "senhaboa123"));

        ParDeTokens segundo = servico.renovar(primeiro.refreshToken());

        assertThat(segundo.refreshToken()).isNotEqualTo(primeiro.refreshToken());
        assertThat(segundo.accessToken()).isNotBlank();
    }

    /**
     * Este teste pegou um bug de segurança real.
     *
     * <p>A detecção de reuso revogava a família e, logo depois, lançava a
     * exceção que recusa a requisição. Como {@code RegraDeNegocioException} é
     * {@code RuntimeException}, o Spring fazia rollback da transação — e
     * levava a revogação junto. O sistema registrava no log que havia
     * detectado o reuso, devolvia erro ao cliente, e deixava a sessão roubada
     * funcionando normalmente.</p>
     *
     * <p>Por isso a asserção não para no erro devolvido: ela vai ao banco
     * confirmar que a revogação ficou gravada. Um teste que só checasse a
     * exceção teria passado com a mitigação inteira anulada.</p>
     */
    @Test
    @DisplayName("reusar um refresh token já trocado derruba a sessão inteira")
    void reusoDerrubaASessaoInteira() {
        ParDeTokens primeiro = servico.registrar(new NovoUsuario(emailNovo(), "senhaboa123"));
        ParDeTokens segundo = servico.renovar(primeiro.refreshToken());

        // O token antigo reaparece: ou o cliente repetiu, ou alguém roubou.
        // Como não dá para distinguir, o sistema assume o pior.
        assertThatThrownBy(() -> servico.renovar(primeiro.refreshToken()))
                .isInstanceOf(RegraDeNegocioException.class);

        // A revogação precisa ter sobrevivido ao rollback da exceção.
        assertThat(repositorioDeToken.findByHashDoToken(hashDe(segundo.refreshToken())))
                .get()
                .extracting(TokenDeAtualizacao::getRevogadoEm)
                .as("o token da família continua válido: a revogação foi desfeita pelo rollback")
                .isNotNull();

        // E, na prática, o token novo que o ladrão poderia ter não renova mais.
        assertThatThrownBy(() -> servico.renovar(segundo.refreshToken()))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining("Entre novamente");
    }

    @Test
    @DisplayName("token desconhecido não renova nada")
    void tokenDesconhecidoNaoRenova() {
        assertThatThrownBy(() -> servico.renovar("token-que-nunca-existiu"))
                .isInstanceOf(RegraDeNegocioException.class);
    }

    @Test
    @DisplayName("sair encerra a sessão daquele aparelho")
    void sairEncerraASessao() {
        ParDeTokens tokens = servico.registrar(new NovoUsuario(emailNovo(), "senhaboa123"));

        servico.encerrar(tokens.refreshToken());

        assertThatThrownBy(() -> servico.renovar(tokens.refreshToken()))
                .isInstanceOf(RegraDeNegocioException.class);
    }

    @Test
    @DisplayName("senha curta é recusada")
    void senhaCurtaERecusada() {
        assertThatThrownBy(() -> servico.registrar(new NovoUsuario(emailNovo(), "1234")))
                .isInstanceOf(RegraDeNegocioException.class);
    }

    /**
     * Repete o hash do serviço, que é privado de propósito. Duplicar a
     * expressão aqui é aceitável: o teste precisa olhar o banco pela mesma
     * chave, e abrir o método só para o teste enfraqueceria o serviço.
     */
    private static String hashDe(String token) {
        try {
            return java.util.HexFormat.of().formatHex(
                    java.security.MessageDigest.getInstance("SHA-256")
                            .digest(token.getBytes(java.nio.charset.StandardCharsets.UTF_8)));
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    /** E-mail único por teste: os testes compartilham o mesmo banco. */
    private static String emailNovo() {
        return "teste-" + UUID.randomUUID() + "@exemplo.com";
    }

    private static String capturarMensagem(Runnable acao) {
        try {
            acao.run();
            throw new AssertionError("esperava uma exceção");
        } catch (RegraDeNegocioException e) {
            return e.getMessage();
        }
    }
}
