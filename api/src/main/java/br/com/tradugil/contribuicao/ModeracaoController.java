package br.com.tradugil.contribuicao;

import br.com.tradugil.contribuicao.Contribuicao.StatusDeContribuicao;
import br.com.tradugil.contribuicao.ContribuicaoDtos.ContribuicaoResposta;
import br.com.tradugil.contribuicao.ContribuicaoDtos.DecisaoDeModeracao;
import br.com.tradugil.contribuicao.ContribuicaoDtos.ItemDaFila;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/moderacao/contribuicoes")
@Tag(name = "Moderacao", description = "Fila de revisao das contribuicoes da comunidade")
public class ModeracaoController {

    private final ServicoDeContribuicao servico;

    public ModeracaoController(ServicoDeContribuicao servico) {
        this.servico = servico;
    }

    @Operation(
            summary = "Fila de moderacao",
            description = """
                    Cada item vem com o que o dicionario ja diz sobre o termo:
                    os sentidos ja publicados e as prateleiras em que ele
                    esta. Sem isso quem modera decide no escuro, e foi assim
                    que o dicionario ganhou explicacoes repetidas antes.

                    `noDicionario` nulo significa termo novo.""")
    @GetMapping
    public ResponseEntity<List<ItemDaFila>> fila(
            @RequestParam(name = "status", required = false) StatusDeContribuicao status,
            @RequestParam(name = "pagina", defaultValue = "0") int pagina,
            @RequestParam(name = "tamanho", defaultValue = "20") int tamanho) {
        return ResponseEntity.ok(servico.fila(status, pagina, tamanho));
    }

    @Operation(
            summary = "Aprova ou rejeita uma proposta",
            description = """
                    A decisao e a trilha de auditoria sao gravadas juntas. O
                    registro de auditoria e append-only: o proprio banco recusa
                    alteracao e remocao.

                    Ao aprovar, `categoria` e o slug da prateleira do catalogo.
                    E obrigatorio quando o verbete resultante ficaria sem
                    nenhuma: sem prateleira ele existe na busca e some do
                    catalogo. Os slugs validos vem de `GET /api/v1/categorias`.""")
    @PatchMapping("/{id}")
    public ResponseEntity<ContribuicaoResposta> decidir(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long id,
            @Valid @RequestBody DecisaoDeModeracao decisao) {
        return ResponseEntity.ok(
                servico.decidir(Long.valueOf(jwt.getSubject()), id, decisao));
    }
}
