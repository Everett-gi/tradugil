package br.com.tradugiria.contribuicao;

import br.com.tradugiria.contribuicao.ContribuicaoDtos.ContribuicaoResposta;
import br.com.tradugiria.contribuicao.ContribuicaoDtos.NovaContribuicao;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/contribuicoes")
@Tag(name = "Contribuicoes", description = "Propor girias novas para o dicionario")
public class ContribuicaoController {

    private final ServicoDeContribuicao servico;

    public ContribuicaoController(ServicoDeContribuicao servico) {
        this.servico = servico;
    }

    @Operation(
            summary = "Propoe uma giria nova",
            description = """
                    A proposta entra como PENDENTE. Nada chega ao dicionario
                    sem passar por uma pessoa da moderacao.""")
    @PostMapping
    public ResponseEntity<ContribuicaoResposta> propor(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody NovaContribuicao nova) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(servico.propor(Long.valueOf(jwt.getSubject()), nova));
    }

    @Operation(summary = "Lista as minhas propostas e o que aconteceu com cada uma")
    @GetMapping("/minhas")
    public ResponseEntity<List<ContribuicaoResposta>> minhas(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(servico.minhasPropostas(Long.valueOf(jwt.getSubject())));
    }
}
