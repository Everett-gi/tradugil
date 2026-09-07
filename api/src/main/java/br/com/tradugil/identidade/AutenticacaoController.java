package br.com.tradugil.identidade;

import br.com.tradugil.identidade.IdentidadeDtos.Credenciais;
import br.com.tradugil.identidade.IdentidadeDtos.NovoUsuario;
import br.com.tradugil.identidade.IdentidadeDtos.ParDeTokens;
import br.com.tradugil.identidade.IdentidadeDtos.PedidoDeRenovacao;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Autenticação", description = "Conta, sessão e renovação de tokens")
public class AutenticacaoController {

    private final ServicoDeAutenticacao servico;

    public AutenticacaoController(ServicoDeAutenticacao servico) {
        this.servico = servico;
    }

    @Operation(
            summary = "Cria uma conta",
            description = """
                    A conta existe apenas para contribuir e moderar. Consultar
                    o dicionário nunca exige cadastro.""")
    @PostMapping("/registro")
    public ResponseEntity<ParDeTokens> registrar(@Valid @RequestBody NovoUsuario novo) {
        return ResponseEntity.status(HttpStatus.CREATED).body(servico.registrar(novo));
    }

    @Operation(summary = "Entra com e-mail e senha")
    @PostMapping("/login")
    public ResponseEntity<ParDeTokens> entrar(@Valid @RequestBody Credenciais credenciais) {
        return ResponseEntity.ok(servico.autenticar(credenciais));
    }

    @Operation(
            summary = "Renova o par de tokens",
            description = """
                    Cada uso troca o token por um novo. Apresentar um token já
                    usado encerra a sessão inteira por segurança — é o sinal de
                    que ele pode ter sido roubado.""")
    @PostMapping("/refresh")
    public ResponseEntity<ParDeTokens> renovar(@Valid @RequestBody PedidoDeRenovacao pedido) {
        return ResponseEntity.ok(servico.renovar(pedido.refreshToken()));
    }

    @Operation(summary = "Sai deste aparelho")
    @PostMapping("/logout")
    public ResponseEntity<Void> sair(@Valid @RequestBody PedidoDeRenovacao pedido) {
        servico.encerrar(pedido.refreshToken());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Sai de todos os aparelhos")
    @PostMapping("/logout-geral")
    public ResponseEntity<Void> sairDeTudo(@AuthenticationPrincipal Jwt jwt) {
        servico.encerrarTudo(Long.valueOf(jwt.getSubject()));
        return ResponseEntity.noContent().build();
    }
}
