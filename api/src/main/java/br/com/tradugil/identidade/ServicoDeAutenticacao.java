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

    /**
     * Teto do tamanho da senha.
     *
     * <p>Não é para limitar quem escolhe uma frase longa: 200 caracteres cabem
     * em qualquer frase-senha razoável. É para impedir que alguém envie um
     * megabyte de texto e faça o servidor calcular BCrypt em cima disso, que é
     * negação de serviço de graça: o custo é todo do servidor.</p>
     */
    private static final int TAMANHO_MAXIMO_DA_SENHA = 200;

    /** Tentativas erradas seguidas antes do primeiro travamento. */
    private static final int TENTATIVAS_ATE_TRAVAR = 5;

    private static final Duration ESPERA_BASE = Duration.ofMinutes(1);

    /**
     * Teto da espera. Sem teto, a curva que dobra chegaria a anos e um
     * atacante conseguiria trancar a conta de alguém para sempre só errando
     * senha de propósito. Uma hora já torna a força bruta inviável.
     */
    private static final Duration ESPERA_MAXIMA = Duration.ofHours(1);

    private final RepositorioDeUsuario repositorioDeUsuario;
    private final RepositorioDeToken repositorioDeToken;
    private final PasswordEncoder codificador;
    private final EmissorDeJwt emissor;
    private final RevogadorDeSessao revogador;
    private final SecureRandom aleatorio = new SecureRandom();

    /**
     * Hash descartável, com que a senha é comparada quando o e-mail não
     * existe. Calculado na subida, com o codificador de verdade, para custar
     * exatamente o mesmo que uma comparação real.
     *
     * <p>Antes disto havia aqui uma constante escrita à mão que <b>não era um
     * hash BCrypt válido</b>: tinha 57 caracteres onde o formato exige 53. O
     * Spring rejeita pelo formato antes de calcular coisa alguma, então a
     * comparação voltava em microssegundos. O efeito era o oposto do
     * pretendido: e-mail inexistente respondia na hora e senha errada demorava
     * os 250 ms do BCrypt, e o tempo de resposta virava exatamente o oráculo
     * de "esta pessoa tem conta aqui" que o código dizia estar evitando.</p>
     */
    private final String hashDescartavel;

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

        byte[] semente = new byte[32];
        new SecureRandom().nextBytes(semente);
        this.hashDescartavel = codificador.encode(
                Base64.getEncoder().encodeToString(semente));
    }

    @Transactional
    public ParDeTokens registrar(NovoUsuario novo) {
        String email = novo.email().trim().toLowerCase(java.util.Locale.ROOT);

        conferirSenha(novo.senha(), email);
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
        OffsetDateTime agora = OffsetDateTime.now();
        Optional<Usuario> encontrado = repositorioDeUsuario.findByEmail(email);

        /*
         * Conta travada não chega a comparar senha. Responde a mesma frase de
         * credencial errada, de propósito: dizer "sua conta está bloqueada"
         * confirmaria que a conta existe, que é justamente o que o atacante
         * está tentando descobrir enquanto tenta senhas.
         */
        if (encontrado.isPresent() && encontrado.get().estaBloqueado(agora)) {
            log.warn("Login recusado para conta travada: usuário {}.",
                    encontrado.get().getId());
            throw credenciaisInvalidas();
        }

        // Compara a senha mesmo quando o e-mail não existe, contra um hash
        // descartável de verdade. Ver o comentário de `hashDescartavel`.
        String hashParaComparar = encontrado
                .map(Usuario::getSenhaHash)
                .orElse(hashDescartavel);
        boolean senhaConfere = codificador.matches(credenciais.senha(), hashParaComparar);

        if (encontrado.isEmpty()) {
            throw credenciaisInvalidas();
        }

        Usuario usuario = encontrado.get();

        if (!senhaConfere) {
            OffsetDateTime travadoAte = usuario.registrarFalha(
                    agora, TENTATIVAS_ATE_TRAVAR, ESPERA_BASE, ESPERA_MAXIMA);
            if (travadoAte != null) {
                log.warn("Conta {} travada até {} após {} tentativas erradas.",
                        usuario.getId(), travadoAte, usuario.getTentativasFalhas());
            }
            throw credenciaisInvalidas();
        }

        usuario.registrarAcerto();

        /*
         * Regrava o hash quando ele está num formato antigo. É o que permite
         * trocar de algoritmo sem pedir a ninguém que redefina a senha: cada
         * conta migra sozinha no primeiro login depois da troca. Só é possível
         * aqui, porque este é o único ponto do sistema em que a senha em claro
         * existe.
         */
        if (codificador.upgradeEncoding(usuario.getSenhaHash())) {
            usuario.trocarHashDaSenha(codificador.encode(credenciais.senha()));
            log.info("Hash da senha do usuário {} regravado no formato atual.",
                    usuario.getId());
        }

        return emitirPar(usuario, UUID.randomUUID());
    }

    /**
     * A mesma exceção para e-mail inexistente, senha errada e conta travada.
     *
     * <p>Três respostas diferentes seriam três formas de perguntar ao servidor
     * quem tem conta aqui. A pessoa legítima que errou a senha entende pela
     * frase; quem está sondando não aprende nada.</p>
     */
    private static RegraDeNegocioException credenciaisInvalidas() {
        return new RegraDeNegocioException("CREDENCIAIS_INVALIDAS",
                "E-mail ou senha incorretos.");
    }

    /**
     * Regras da senha nova.
     *
     * <p>Sem exigência de maiúscula, número e símbolo: essa regra empurra as
     * pessoas para "Senha@123" e o NIST deixou de recomendá-la em 2017 por
     * isso. O que fica são as três checagens que de fato ajudam: comprimento
     * mínimo, teto contra abuso e recusa das senhas mais óbvias.</p>
     */
    private static void conferirSenha(String senha, String email) {
        if (senha == null || senha.length() < TAMANHO_MINIMO_DA_SENHA) {
            throw new RegraDeNegocioException("SENHA_CURTA",
                    "A senha precisa ter pelo menos " + TAMANHO_MINIMO_DA_SENHA
                            + " caracteres.");
        }
        if (senha.length() > TAMANHO_MAXIMO_DA_SENHA) {
            throw new RegraDeNegocioException("SENHA_LONGA",
                    "A senha pode ter no máximo " + TAMANHO_MAXIMO_DA_SENHA
                            + " caracteres.");
        }

        String simplificada = senha.toLowerCase(java.util.Locale.ROOT);
        if (SenhasProibidas.contem(simplificada)) {
            throw new RegraDeNegocioException("SENHA_PREVISIVEL",
                    "Essa senha é uma das mais usadas do mundo e seria adivinhada "
                            + "em segundos. Escolha outra.");
        }

        // A senha ser o próprio e-mail, ou a parte antes do arroba, é a
        // primeira coisa que qualquer ataque tenta.
        String usuario = email.split("@")[0];
        if (simplificada.equals(email) || simplificada.equals(usuario)) {
            throw new RegraDeNegocioException("SENHA_PREVISIVEL",
                    "A senha não pode ser o seu e-mail. Escolha outra.");
        }
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
