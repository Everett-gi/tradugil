package br.com.tradugil.dicionario;

import br.com.tradugil.dicionario.GiriaDtos.GiriaCompleta;
import br.com.tradugil.dicionario.GiriaDtos.GiriaResumo;
import br.com.tradugil.dicionario.GiriaDtos.Pagina;
import br.com.tradugil.dicionario.GiriaDtos.Voto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/girias")
@Tag(name = "Dicionário", description = "Consulta ao dicionário de gírias")
public class GiriaController {

    private final ServicoDeDicionario servico;
    private final ServicoDeVotos votos;

    public GiriaController(ServicoDeDicionario servico, ServicoDeVotos votos) {
        this.servico = servico;
        this.votos = votos;
    }

    @Operation(
            summary = "Busca gírias",
            description = "Tolera erro de digitação: 'crinje' encontra 'cringe'.")
    @GetMapping
    public ResponseEntity<Pagina<GiriaResumo>> buscar(
            @RequestParam(name = "q", defaultValue = "") String consulta,
            @RequestParam(name = "idioma", required = false) String idioma,
            @RequestParam(name = "pagina", defaultValue = "0") int pagina,
            @RequestParam(name = "tamanho", defaultValue = "20") int tamanho) {
        return ResponseEntity.ok(servico.pesquisar(consulta, idioma, pagina, tamanho));
    }

    @Operation(
            summary = "Marca uma explicação como útil ou não",
            description = """
                    Público, sem cadastro: exigir conta para dizer "essa
                    explicação me ajudou" afastaria justamente quem mais teria
                    o que dizer.

                    O voto apenas reordena definições já aprovadas por uma
                    pessoa. Nunca publica, oculta ou altera texto.""")
    @PostMapping("/definicoes/{definicaoId}/votos")
    public ResponseEntity<Void> votar(@PathVariable Long definicaoId,
                                      @Valid @RequestBody Voto voto) {
        votos.votar(definicaoId, voto.util());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Verbete completo de um termo")
    @GetMapping("/{termo}")
    public ResponseEntity<GiriaCompleta> porTermo(
            @PathVariable String termo,
            @RequestParam(name = "idioma", required = false) String idioma) {
        return ResponseEntity.ok(servico.porTermo(termo, idioma));
    }
}
