package br.com.tradugiria.dicionario;

import br.com.tradugiria.dicionario.PacoteDtos.MetadadosDoPacote;
import br.com.tradugiria.dicionario.PacoteDtos.Pacote;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequestMapping("/api/v1/dicionario/pacotes")
@Tag(name = "Pacote offline", description = "Dicionário completo para os clientes guardarem")
public class PacoteController {

    private final ServicoDePacote servico;

    public PacoteController(ServicoDePacote servico) {
        this.servico = servico;
    }

    @Operation(
            summary = "Versão do dicionário disponível",
            description = """
                    Poucos bytes, para o cliente comparar com a versão que já
                    tem antes de baixar o pacote inteiro. Importa para quem
                    tem plano de dados limitado.""")
    @GetMapping("/ultimo")
    public ResponseEntity<MetadadosDoPacote> ultimo() {
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(Duration.ofMinutes(30)).cachePublic())
                .body(servico.metadados());
    }

    @Operation(
            summary = "Dicionário completo",
            description = """
                    Todos os verbetes com definição aprovada, para o cliente
                    gravar no aparelho. É o que faz o aplicativo já nascer
                    funcionando sem internet, em vez de precisar aprender a
                    partir das consultas que deram certo.""")
    @GetMapping
    public ResponseEntity<Pacote> completo() {
        Pacote pacote = servico.pacoteCompleto();

        // A ETag deixa o navegador e o Caddy devolverem 304 quando nada
        // mudou: o cliente que já está atualizado não baixa nada.
        return ResponseEntity.ok()
                .eTag("\"" + pacote.versao() + "\"")
                .cacheControl(CacheControl.maxAge(Duration.ofHours(6)).cachePublic())
                .body(pacote);
    }
}
