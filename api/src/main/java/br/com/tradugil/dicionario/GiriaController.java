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
            summary = "Busca e navegação no dicionário",
            description = """
                    Com `q` preenchido é busca, e tolera erro de digitação:
                    'crinje' encontra 'cringe'.

                    Com `q` vazio e `categoria` preenchida vira navegação: lista
                    a categoria inteira em ordem alfabética. É o que o catálogo
                    usa para abrir uma prateleira.

                    `modoFamilia` ausente equivale a ligado, igual ao
                    `/traduzir`. Num endereço que se navega, o padrão precisa
                    ser o seguro: aqui a pessoa esbarra no conteúdo em vez de
                    procurá-lo.""")
    @GetMapping
    public ResponseEntity<Pagina<GiriaResumo>> buscar(
            @RequestParam(name = "q", defaultValue = "") String consulta,
            @RequestParam(name = "idioma", required = false) String idioma,
            @RequestParam(name = "categoria", required = false) String categoria,
            @RequestParam(name = "modoFamilia", required = false) Boolean modoFamilia,
            @RequestParam(name = "pagina", defaultValue = "0") int pagina,
            @RequestParam(name = "tamanho", defaultValue = "20") int tamanho) {
        return ResponseEntity.ok()
                .cacheControl(CacheHttp.DE_LEITURA_PUBLICA)
                .body(servico.pesquisar(consulta, idioma, categoria,
                        familiaLigado(modoFamilia), pagina, tamanho));
    }

    /** Ausente equivale a ligado: o padrão protege o caso em que errar custa caro. */
    private static boolean familiaLigado(Boolean modoFamilia) {
        return modoFamilia == null || modoFamilia;
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
        return ResponseEntity.ok()
                .cacheControl(CacheHttp.DE_LEITURA_PUBLICA)
                .body(servico.porTermo(termo, idioma));
    }
}
