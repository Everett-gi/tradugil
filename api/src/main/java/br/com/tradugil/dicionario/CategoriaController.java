package br.com.tradugil.dicionario;

import br.com.tradugil.dicionario.GiriaDtos.CategoriaResumo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Prateleiras do catálogo.
 *
 * <p>Fica em {@code /api/v1/categorias}, e não em {@code /api/v1/girias/…},
 * por um motivo prático: ali embaixo já existe {@code /girias/{termo}}, e
 * {@code /girias/categorias} colidiria com ele. O Spring resolveria a favor do
 * caminho literal, mas o efeito seria que a palavra "categorias" viraria a
 * única que o dicionário nunca consegue consultar. Categoria é um recurso
 * próprio, e no seu próprio endereço não há armadilha para desarmar.</p>
 */
@RestController
@RequestMapping("/api/v1/categorias")
@Tag(name = "Dicionário", description = "Consulta ao dicionário de gírias")
public class CategoriaController {

    private final ServicoDeDicionario servico;

    public CategoriaController(ServicoDeDicionario servico) {
        this.servico = servico;
    }

    @Operation(
            summary = "Prateleiras do catálogo",
            description = """
                    Categorias com quantos verbetes cada uma tem. Categoria sem
                    nenhum verbete visível não aparece: prateleira vazia só se
                    revela vazia depois que a pessoa clica e espera.

                    Com o modo família ligado, a contagem é dos verbetes que a
                    pessoa consegue abrir, e não do total. Mostrar 40 e listar
                    31 seria anunciar que há algo escondido ali.

                    Para listar os verbetes de uma delas, use
                    `GET /api/v1/girias?categoria={slug}`.""")
    @GetMapping
    public ResponseEntity<List<CategoriaResumo>> listar(
            @RequestParam(name = "modoFamilia", required = false) Boolean modoFamilia) {
        return ResponseEntity.ok(servico.categorias(modoFamilia == null || modoFamilia));
    }
}
