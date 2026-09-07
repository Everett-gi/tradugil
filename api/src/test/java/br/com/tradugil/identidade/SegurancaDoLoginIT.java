package br.com.tradugil.identidade;

import br.com.tradugil.TestecomBanco;
import br.com.tradugil.comum.erro.RegraDeNegocioException;
import br.com.tradugil.identidade.IdentidadeDtos.Credenciais;
import br.com.tradugil.identidade.IdentidadeDtos.NovoUsuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * As defesas do login.
 *
 * <p>Cada teste aqui existe por causa de um modo de falha concreto, e não por
 * cobertura: são as propriedades que, quando quebram, quebram em silêncio e
 * continuam parecendo que funcionam.</p>
 */
@TestecomBanco
class SegurancaDoLoginIT {

    @Autowired
    private ServicoDeAutenticacao servico;

    @Autowired
    private RepositorioDeUsuario repositorio;

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder codificador;

    /* --------------------------------------------------------- timing --- */

    @Test
    @DisplayName("e-mail inexistente demora tanto quanto senha errada")
    void naoRevelaQuemTemContaPeloTempo() {
        /*
         * O BUG QUE ESTE TESTE PEGA
         *
         * O código comparava a senha contra um hash falso escrito à mão quando
         * o e-mail não existia, para igualar o tempo. O hash tinha 57
         * caracteres onde o BCrypt exige 53, então o Spring o rejeitava pelo
         * formato e devolvia false sem calcular nada. E-mail inexistente
         * respondia em microssegundos; senha errada custava os ~250 ms do
         * BCrypt. A diferença era de três ordens de grandeza, e virava um
         * oráculo de "esta pessoa tem conta aqui".
         *
         * A margem é frouxa de propósito. Comparação de tempo em máquina
         * compartilhada oscila, e um teste que exige igualdade fica vermelho
         * sem motivo. O que se quer pegar é a diferença gritante: se um
         * caminho voltar a não calcular hash nenhum, ele fica dezenas de vezes
         * mais rápido, não 30% mais rápido.
         */
        String email = emailNovo();
        servico.registrar(new NovoUsuario(email, "senhaboa123"));

        long comSenhaErrada = medirMedia(() ->
                tentar(email, "senhaerrada456"));
        long comEmailInexistente = medirMedia(() ->
                tentar("ninguem-" + UUID.randomUUID() + "@exemplo.com", "senhaerrada456"));

        assertThat(comEmailInexistente)
                .as("e-mail inexistente (%d ns) contra senha errada (%d ns)",
                        comEmailInexistente, comSenhaErrada)
                .isGreaterThan(comSenhaErrada / 5);
    }

    /* ------------------------------------------------------ travamento --- */

    @Test
    @DisplayName("a conta trava depois de cinco senhas erradas")
    void travaDepoisDeCincoErros() {
        String email = emailNovo();
        servico.registrar(new NovoUsuario(email, "senhaboa123"));

        for (int i = 0; i < 5; i++) {
            tentar(email, "errada" + i);
        }

        Usuario usuario = repositorio.findByEmail(email).orElseThrow();
        assertThat(usuario.getTentativasFalhas()).isEqualTo((short) 5);
        assertThat(usuario.estaBloqueado(OffsetDateTime.now())).isTrue();
    }

    @Test
    @DisplayName("conta travada recusa até a senha certa, com a mesma mensagem")
    void contaTravadaRecusaSenhaCerta() {
        String email = emailNovo();
        servico.registrar(new NovoUsuario(email, "senhaboa123"));
        for (int i = 0; i < 5; i++) {
            tentar(email, "errada" + i);
        }

        /*
         * A senha certa também é recusada: é isso que impede o atacante de
         * usar o travamento como oráculo. Se a senha certa passasse enquanto
         * as erradas travam, bastaria observar qual tentativa se comporta
         * diferente.
         *
         * E a mensagem é a mesma de senha errada. "Sua conta está bloqueada"
         * confirmaria que a conta existe, que é a informação que o atacante
         * está tentando arrancar enquanto tenta senhas.
         */
        assertThatThrownBy(() -> servico.autenticar(new Credenciais(email, "senhaboa123")))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessage("E-mail ou senha incorretos.");
    }

    @Test
    @DisplayName("acertar a senha zera o contador de falhas")
    void acertoZeraOContador() {
        String email = emailNovo();
        servico.registrar(new NovoUsuario(email, "senhaboa123"));

        tentar(email, "errada1");
        tentar(email, "errada2");
        assertThat(repositorio.findByEmail(email).orElseThrow().getTentativasFalhas())
                .isEqualTo((short) 2);

        servico.autenticar(new Credenciais(email, "senhaboa123"));

        Usuario depois = repositorio.findByEmail(email).orElseThrow();
        assertThat(depois.getTentativasFalhas()).isZero();
        assertThat(depois.getBloqueadoAte()).isNull();
    }

    /* --------------------------------------------------- hash e pimenta --- */

    @Test
    @DisplayName("hash antigo, sem prefixo, continua funcionando e é regravado no login")
    void migraHashAntigoNoLogin() {
        /*
         * A propriedade que garante que trocar de algoritmo não vira "todo
         * mundo perdeu a senha". Sem ela, esta mudança teria deslogado
         * permanentemente qualquer conta criada antes dela.
         */
        String email = emailNovo();
        String senha = "senhaboa123";

        Usuario antigo = repositorio.save(new Usuario(
                email,
                new BCryptPasswordEncoder().encode(senha),
                Usuario.Papel.USER));
        assertThat(antigo.getSenhaHash()).startsWith("$2");

        servico.autenticar(new Credenciais(email, senha));

        assertThat(repositorio.findByEmail(email).orElseThrow().getSenhaHash())
                .as("regravado no formato atual")
                .startsWith("{pimenta}$2a$12$");
    }

    @Test
    @DisplayName("o hash gravado não é BCrypt puro da senha")
    void oHashLevaPimenta() {
        /*
         * Prova que a pimenta participa de verdade. Se algum dia o HMAC sumir
         * do caminho, o hash gravado passaria a bater com um BCrypt puro da
         * senha, e a proteção contra vazamento só do banco teria evaporado
         * sem nenhum outro sintoma.
         */
        String senha = "senhaboa123";
        String gravado = codificador.encode(senha);
        String semPrefixo = gravado.substring("{pimenta}".length());

        assertThat(new BCryptPasswordEncoder().matches(senha, semPrefixo))
                .as("BCrypt puro da senha não pode abrir um hash apimentado")
                .isFalse();
        assertThat(codificador.matches(senha, gravado)).isTrue();
    }

    @Test
    @DisplayName("senhas longas não colidem pelo truncamento do BCrypt")
    void naoTruncaEm72Bytes() {
        /*
         * O BCrypt corta em 72 bytes sem avisar. Sem o HMAC antes, duas senhas
         * que compartilham os primeiros 72 caracteres abririam a mesma conta,
         * e nada no sistema denunciaria isso.
         */
        String base = "a".repeat(72);
        String gravado = codificador.encode(base + "PRIMEIRA");

        assertThat(codificador.matches(base + "SEGUNDA", gravado)).isFalse();
        assertThat(codificador.matches(base + "PRIMEIRA", gravado)).isTrue();
    }

    /* ------------------------------------------------------- senha fraca --- */

    @Test
    @DisplayName("recusa as senhas mais previsíveis do mundo")
    void recusaSenhaPrevisivel() {
        for (String previsivel : new String[] {"senha123", "12345678", "password", "flamengo"}) {
            assertThatThrownBy(() -> servico.registrar(new NovoUsuario(emailNovo(), previsivel)))
                    .as("senha %s", previsivel)
                    .isInstanceOf(RegraDeNegocioException.class)
                    .hasMessageContaining("mais usadas do mundo");
        }
    }

    @Test
    @DisplayName("recusa a senha igual ao e-mail")
    void recusaSenhaIgualAoEmail() {
        String email = emailNovo();
        assertThatThrownBy(() -> servico.registrar(new NovoUsuario(email, email)))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining("não pode ser o seu e-mail");
    }

    @Test
    @DisplayName("recusa senha absurdamente longa em vez de calcular hash nela")
    void recusaSenhaLonga() {
        // Sem teto, alguém manda um megabyte e o servidor paga o BCrypt em
        // cima disso: negação de serviço de graça.
        String enorme = "x".repeat(5_000);
        assertThatThrownBy(() -> servico.registrar(new NovoUsuario(emailNovo(), enorme)))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining("no máximo");
    }

    /* -------------------------------------------------------------- apoio --- */

    private void tentar(String email, String senha) {
        try {
            servico.autenticar(new Credenciais(email, senha));
        } catch (RegraDeNegocioException esperada) {
            // O teste chama isto justamente para provocar a falha.
        }
    }

    /** Mediana de cinco medidas, para uma execução lenta isolada não decidir. */
    private static long medirMedia(Runnable acao) {
        long[] medidas = new long[5];
        for (int i = 0; i < medidas.length; i++) {
            long inicio = System.nanoTime();
            acao.run();
            medidas[i] = System.nanoTime() - inicio;
        }
        java.util.Arrays.sort(medidas);
        return medidas[medidas.length / 2];
    }

    private static String emailNovo() {
        return "teste-" + UUID.randomUUID() + "@exemplo.com";
    }
}
