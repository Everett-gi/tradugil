package br.com.tradugiria.traducao;

import br.com.tradugiria.traducao.TraducaoDtos.PedidoDeTraducao;
import br.com.tradugiria.traducao.TraducaoDtos.RespostaDeTraducao;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/traduzir")
@Tag(name = "Tradução", description = "Detecção e explicação de gírias em um texto")
public class TraducaoController {

    private final ServicoDeTraducao servico;

    public TraducaoController(ServicoDeTraducao servico) {
        this.servico = servico;
    }

    @Operation(
            summary = "Detecta e explica as gírias de um texto",
            description = """
                    Endpoint principal do produto. Recebe um trecho e devolve
                    cada gíria encontrada com sua posição no texto original.

                    O texto enviado não é armazenado em nenhum momento: ele é
                    processado em memória e descartado com a requisição.""")
    @PostMapping
    public ResponseEntity<RespostaDeTraducao> traduzir(@Valid @RequestBody PedidoDeTraducao pedido) {
        return ResponseEntity.ok(servico.traduzir(pedido));
    }
}
