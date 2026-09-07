package br.com.tradugiria.dicionario;

import br.com.tradugiria.comum.erro.RecursoNaoEncontradoException;
import br.com.tradugiria.dicionario.GiriaDtos.GiriaCompleta;
import br.com.tradugiria.dicionario.GiriaDtos.GiriaResumo;
import br.com.tradugiria.dicionario.GiriaDtos.Pagina;
import br.com.tradugiria.traducao.Normalizador;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ServicoDeDicionario {

    /**
     * Semelhança mínima para um termo entrar no resultado. Abaixo de 0,3 o
     * pg_trgm começa a casar palavras que só compartilham um trigrama — "pog"
     * traria "pogo" e "pogba". Acima de 0,45 ele deixa de perdoar o erro de
     * digitação de uma letra, que é justamente o caso que o nível 2 existe
     * para resolver.
     */
    private static final double LIMIAR_DE_SEMELHANCA = 0.35;

    private static final int TAMANHO_MAXIMO_DA_PAGINA = 50;

    private final RepositorioDeGiria repositorio;

    public ServicoDeDicionario(RepositorioDeGiria repositorio) {
        this.repositorio = repositorio;
    }

    /**
     * Nível 1 da cascata para o verbete completo. O cache é em processo
     * (Caffeine): o free tier não comporta um Redis, e o conteúdo de um
     * verbete só muda quando a curadoria mexe nele.
     */
    @Cacheable(cacheNames = "verbetes", key = "#termo + '|' + #idioma")
    @Transactional(readOnly = true)
    public GiriaCompleta porTermo(String termo, String idioma) {
        String normalizado = Normalizador.normalizar(termo);
        return localizar(normalizado, idioma)
                .map(GiriaCompleta::de)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Não encontramos esse termo no dicionário."));
    }

    @Transactional(readOnly = true)
    public Pagina<GiriaResumo> pesquisar(String consulta, String idioma,
                                         int pagina, int tamanho) {
        String normalizado = Normalizador.normalizar(consulta);
        int limite = Math.min(Math.max(tamanho, 1), TAMANHO_MAXIMO_DA_PAGINA);
        int paginaSegura = Math.max(pagina, 0);

        // Pede um item a mais do que cabe na página: se ele vier, existe
        // próxima página. Evita um COUNT sobre o índice trigram, que é caro
        // e cujo total ninguém exibe.
        List<Giria> achados = repositorio.pesquisar(
                normalizado,
                normalizado + "%",
                idioma,
                LIMIAR_DE_SEMELHANCA,
                limite + 1,
                paginaSegura * limite);

        boolean temMais = achados.size() > limite;
        List<GiriaResumo> itens = achados.stream()
                .limit(limite)
                .map(GiriaResumo::de)
                .toList();

        return new Pagina<>(itens, paginaSegura, limite, temMais);
    }

    /**
     * Busca em três tentativas, da mais barata para a mais cara: forma exata,
     * forma com repetições de ênfase colapsadas e, por fim, semelhança
     * trigram. Parar na primeira que acerta é o que mantém o p95 baixo.
     */
    @Transactional(readOnly = true)
    public java.util.Optional<Giria> localizar(String normalizado, String idioma) {
        if (normalizado.isBlank()) {
            return java.util.Optional.empty();
        }

        java.util.Optional<Giria> exata = idioma == null
                ? repositorio.findByTermoNormalizado(normalizado).stream().findFirst()
                : repositorio.findByTermoNormalizadoAndIdiomaCodigo(normalizado, idioma);
        if (exata.isPresent()) {
            return exata;
        }

        // Colapsado contra colapsado: o usuário escreveu "kkkkkkk" e o
        // verbete guardado é "kkk". Comparar a forma reduzida do usuário com
        // a forma original do banco nunca casaria.
        java.util.Optional<Giria> porEnfase = repositorio
                .findByTermoColapsado(Normalizador.colapsarRepeticoes(normalizado))
                .stream()
                .findFirst();
        if (porEnfase.isPresent()) {
            return porEnfase;
        }

        return repositorio
                .buscarPorSemelhanca(normalizado, LIMIAR_DE_SEMELHANCA, 1)
                .stream()
                .findFirst();
    }
}
