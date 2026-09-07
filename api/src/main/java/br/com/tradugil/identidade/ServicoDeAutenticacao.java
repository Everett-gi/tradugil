package br.com.tradugil.identidade;

import br.com.tradugil.comum.erro.RegraDeNegocioException;
import br.com.tradugil.identidade.IdentidadeDtos.Credenciais;
import br.com.tradugil.identidade.IdentidadeDtos.NovoUsuario;
import br.com.tradugil.identidade.IdentidadeDtos.ParDeTokens;
import br.com.tradugil.identidade.Usuario.Papel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Optional;
import java.util.UUID;

@Service
public class ServicoDeAutenticacao {

    private static final Logger log = LoggerFactory.getLogger(ServicoDeAutenticacao.class);

    private static final Duration VALIDADE_DO_REFRESH = Duration.ofDays(30);

    /** 256 bits de entropia. Não há o que adivinhar. */
    private static final int BYTES_DO_TOKEN = 32;

    private static final int TAMANHO_MINIMO_DA_SENHA = 8;

    private final RepositorioDeUsuario repositorioDeUsuario;
    private final RepositorioDeToken repositorioDeToken;
    private final PasswordEncoder codificador;
    private final EmissorDeJwt emissor;
    private final RevogadorDeSessao revogador;
    private final SecureRandom aleatorio = new SecureRandom();

    public ServicoDeAutenticacao(RepositorioDeUsuario repositorioDeUsuario,
                                 RepositorioDeToken repositorioDeToken,
                                 PasswordEncoder codificador,
                                 EmissorDeJwt emissor,
                                 RevogadorDeSessao revogador) {
        this.repositorioDeUsuario = repositorioDeUsuario;
        this.repositorioDeToken = repositorioDeToken;
        this.codificador = codificador;
        this.emissor = emissor;
        this.revogador = revogador;
    }

    @Transactional
    public ParDeTokens registrar(NovoUsuario novo) {
        String email = novo.email().trim().toLowerCase(java.util.Locale.ROOT);

        if (novo.senha().length() < TAMANHO_MINIMO_DA_SENHA) {
            throw new RegraDeNegocioException("SENHA_CURTA",
                    "A senha precisa ter pelo menos " + TAMANHO_MINIMO_DA_SENHA + " caracteres.");
        }
        if (repositorioDeUsuario.existsByEmail(email)) {
            // O e-mail já existe, mas a mensagem não confirma isso: dizer
            // "e-mail já cadastrado" transforma o registro num oráculo que
            // revela quem tem conta aqui. A mesma frase serve para os dois
            // casos e o usuário legítimo entende pelo fluxo de login.
            throw new RegraDeNegocioException("CADASTRO_INVALIDO",
                    "Não foi possível concluir o cadastro com esses dados.");
        }

        Usuario usuario = repositorioDeUsuario.save(
                new Usuario(email, codificador.encode(novo.senha()), Papel.USER));
        return emitirPar(usuario, UUID.randomUUID());
    }

    @Transactional
    public ParDeTokens autenticar(Credenciais credenciais) {
        String email = credenciais.email().trim().toLowerCase(java.util.Locale.ROOT);
        Optional<Usuario> encontrado = repositorioDeUsuario.findByEmail(email);

        // Compara a senha mesmo quando o usuário não existe, contra um hash
        // descartável. Sem isso, a resposta volta muito mais rápido para
        // e-mail inexistente do que para senha errada, e o tempo de resposta
        // vira um oráculo de quem tem conta.
        String hashParaComparar = encontrado
                .map(Usuario::getSenhaHash)
                .orElse("$2a$10$ignoreignoreignoreignoreignoreignoreignoreignoreignoreign");
        boolean senhaConfere = codificador.matches(credenciais.senha(), hashParaComparar);

        if (encontrado.isEmpty() || !senhaConfere) {
            throw new RegraDeNegocioException("CREDENCIAIS_INVALIDAS",
                    "E-mail ou senha incorretos.");
        }
        return emitirPar(encontrado.get(), UUID.randomUUID());
    }

    /**
     * Troca o refresh token por um par novo.
     *
     * <p>Aqui mora a detecção de reuso: um token já trocado que reaparece
     * significa que ou o cliente legítimo repetiu, ou alguém roubou. Como não
     * dá para distinguir, revoga-se a família inteira: o usuário legítimo
     * refaz o login e o ladrão perde o acesso.</p>
     */
    @Transactional
    public ParDeTokens renovar(String refreshToken) {
        OffsetDateTime agora = OffsetDateTime.now();
        TokenDeAtualizacao guardado = repositorioDeToken
                .findByHashDoToken(hashDe(refreshToken))
                .orElseThrow(() -> new RegraDeNegocioException("SESSAO_INVALIDA",
                        "Sua sessão expirou. Entre novamente."));

        if (guardado.foiReutilizado(agora)) {
            // Em transacao propria: a excecao logo abaixo desfaria a
            // revogacao se ela acontecesse nesta mesma transacao, e a
            // deteccao de reuso ficaria anulada em silencio.
            int derrubados = revogador.revogarFamilia(guardado.getFamilia());
            log.warn("Reuso de refresh token detectado para o usuário {}. "
                            + "{} tokens da família revogados.",
                    guardado.getUsuario().getId(), derrubados);
            throw new RegraDeNegocioException("SESSAO_INVALIDA",
                    "Sua sessão foi encerrada por segurança. Entre novamente.");
        }

        if (!guardado.estaValido(agora)) {
            throw new RegraDeNegocioException("SESSAO_INVALIDA",
                    "Sua sessão expirou. Entre novamente.");
        }

        guardado.marcarSubstituido(agora);
        return emitirPar(guardado.getUsuario(), guardado.getFamilia());
    }

    /** Sair deste aparelho. */
    @Transactional
    public void encerrar(String refreshToken) {
        repositorioDeToken.findByHashDoToken(hashDe(refreshToken))
                .ifPresent(token -> revogador.revogarFamilia(token.getFamilia()));
    }

    /** Sair de todos os aparelhos. */
    @Transactional
    public void encerrarTudo(Long usuarioId) {
        repositorioDeToken.revogarDoUsuario(usuarioId, OffsetDateTime.now());
    }

    private ParDeTokens emitirPar(Usuario usuario, UUID familia) {
        byte[] bruto = new byte[BYTES_DO_TOKEN];
        aleatorio.nextBytes(bruto);
        String refresh = Base64.getUrlEncoder().withoutPadding().encodeToString(bruto);

        repositorioDeToken.save(new TokenDeAtualizacao(
                usuario,
                hashDe(refresh),
                familia,
                OffsetDateTime.now().plus(VALIDADE_DO_REFRESH)));

        return new ParDeTokens(
                emissor.emitir(usuario),
                refresh,
                emissor.validadeEmSegundos(),
                usuario.getPapel());
    }

    /**
     * SHA-256, e não BCrypt: o token é aleatório de 256 bits, não tem a
     * entropia baixa de uma senha escolhida por pessoa. Não há dicionário
     * nem força bruta a atrasar, e BCrypt custaria centenas de milissegundos
     * em cada renovação sem proteger nada a mais.
     */
    private static String hashDe(String token) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                    .digest(token.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 indisponível nesta JVM", e);
        }
    }
}
