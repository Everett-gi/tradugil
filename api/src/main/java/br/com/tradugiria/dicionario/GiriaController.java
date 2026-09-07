package br.com.tradugiria.dicionario;

import br.com.tradugiria.dicionario.GiriaDtos.GiriaCompleta;
import br.com.tradugiria.dicionario.GiriaDtos.GiriaResumo;
import br.com.tradugiria.dicionario.GiriaDtos.Pagina;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/girias")
@Tag(name = "Dicionário", description = "Consulta ao dicionário de gírias")
public class GiriaController {

    private final ServicoDeDicionario servico;

    public GiriaController(ServicoDeDicionario servico) {
        this.servico = servico;
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

    @Operation(summary = "Verbete completo de um termo")
    @GetMapping("/{termo}")
    public ResponseEntity<GiriaCompleta> porTermo(
            @PathVariable String termo,
            @RequestParam(name = "idioma", required = false) String idioma) {
        return ResponseEntity.ok(servico.porTermo(termo, idioma));
    }
}
