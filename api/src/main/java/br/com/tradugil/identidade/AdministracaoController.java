package br.com.tradugil.identidade;

import br.com.tradugil.comum.erro.RecursoNaoEncontradoException;
import br.com.tradugil.comum.erro.RegraDeNegocioException;
import br.com.tradugil.identidade.IdentidadeDtos.UsuarioResposta;
import br.com.tradugil.identidade.Usuario.Papel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Promover e rebaixar moderadores.
 *
 * <h2>Por que isto faltava</h2>
 *
 * {@code Usuario.promoverPara} existia desde o começo e <b>nenhum endpoint a
 * chamava</b>. O efeito era que ninguém podia virar moderador, e portanto a
 * fila de moderação inteira era inalcançável: contribuição enviada ficava
 * pendente para sempre, porque não existia quem pudesse aprová-la.
 *
 * <h2>Quem é o primeiro administrador</h2>
 *
 * Não é este endpoint: seria um ciclo, já que ele mesmo exige ADMIN. O
 * primeiro sai de {@link PromotorInicialDeAdmin}, por variável de ambiente.
 */
@RestController
@RequestMapping("/api/v1/admin/usuarios")
@Tag(name = "Administração", description = "Gestão de papéis de usuário")
public class AdministracaoController {

    private static final Logger log = LoggerFactory.getLogger(AdministracaoController.class);

    private final RepositorioDeUsuario repositorio;
    private final RevogadorDeSessao revogador;
    private final RepositorioDeToken tokens;

    public AdministracaoController(RepositorioDeUsuario repositorio,
                                   RevogadorDeSessao revogador,
                                   RepositorioDeToken tokens) {
        this.repositorio = repositorio;
        this.revogador = revogador;
        this.tokens = tokens;
    }

    public record TrocaDePapel(@NotNull(message = "Informe o papel.") Papel papel) {
    }

    @Operation(
            summary = "Lista as contas",
            description = "Só o mínimo para escolher quem promover: id, e-mail e papel.")
    @GetMapping
    public ResponseEntity<List<UsuarioResposta>> listar() {
        return ResponseEntity.ok(
                repositorio.findAll().stream().map(UsuarioResposta::de).toList());
    }

    @Operation(
            summary = "Troca o papel de uma conta",
            description = """
                    Rebaixar derruba todas as sessões daquela conta.

                    Sem isso, o access token que a pessoa já tem continuaria
                    valendo com o papel antigo por até 15 minutos, porque o
                    papel viaja dentro do token e não é consultado no banco a
                    cada requisição. Quinze minutos é pouco para uma promoção
                    e é demais para uma remoção feita às pressas.""")
    @PatchMapping("/{id}/papel")
    @Transactional
    public ResponseEntity<UsuarioResposta> trocarPapel(
            @PathVariable Long id,
            @RequestBody TrocaDePapel troca,
            @AuthenticationPrincipal Jwt jwt) {

        Long quemPede = Long.valueOf(jwt.getSubject());

        /*
         * Ninguém rebaixa a si mesmo. Não é paternalismo: é o que impede o
         * único administrador de se remover por engano e deixar o sistema sem
         * ninguém capaz de promover outro, sem nenhum caminho de volta pela
         * aplicação.
         */
        if (quemPede.equals(id) && troca.papel() != Papel.ADMIN) {
            throw new RegraDeNegocioException("REBAIXAMENTO_PROPRIO",
                    "Você não pode remover o próprio acesso de administrador. "
                            + "Peça a outro administrador.");
        }

        Usuario usuario = repositorio.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Conta não encontrada."));

        Papel anterior = usuario.getPapel();
        if (anterior == troca.papel()) {
            return ResponseEntity.ok(UsuarioResposta.de(usuario));
        }

        usuario.promoverPara(troca.papel());
        repositorio.save(usuario);

        // Só na perda de poder. Numa promoção, esperar o token vencer não tem
        // consequência: o papel novo entra em no máximo 15 minutos e nada
        // indevido acontece nesse meio-tempo.
        if (perdeuPoder(anterior, troca.papel())) {
            int derrubadas = tokens.revogarDoUsuario(id, java.time.OffsetDateTime.now());
            log.warn("Conta {} rebaixada de {} para {} por {}. {} sessões derrubadas.",
                    id, anterior, troca.papel(), quemPede, derrubadas);
        } else {
            log.info("Conta {} promovida de {} para {} por {}.",
                    id, anterior, troca.papel(), quemPede);
        }

        return ResponseEntity.ok(UsuarioResposta.de(usuario));
    }

    private static boolean perdeuPoder(Papel antes, Papel depois) {
        return nivel(depois) < nivel(antes);
    }

    private static int nivel(Papel papel) {
        return switch (papel) {
            case USER -> 0;
            case MODERATOR -> 1;
            case ADMIN -> 2;
        };
    }
}
