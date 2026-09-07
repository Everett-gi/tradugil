package br.com.tradugil.dicionario;

import br.com.tradugil.comum.erro.RecursoNaoEncontradoException;
import br.com.tradugil.dicionario.GiriaDtos.CategoriaResumo;
import br.com.tradugil.dicionario.GiriaDtos.GiriaCompleta;
import br.com.tradugil.dicionario.GiriaDtos.GiriaResumo;
import br.com.tradugil.dicionario.GiriaDtos.Pagina;
import br.com.tradugil.traducao.Normalizador;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ServicoDeDicionario {

    /**
     * Semelhança mínima para um termo entrar no resultado. Abaixo de 0,3 o
     * pg_trgm começa a casar palavras que só compartilham um trigrama: "pog"
     * traria "pogo" e "pogba". Acima de 0,45 ele deixa de perdoar o erro de
     * digitação de uma letra, que é justamente o caso que o nível 2 existe
     * para resolver.
     */
    private static final double LIMIAR_DE_SEMELHANCA = 0.35;

    private static final int TAMANHO_MAXIMO_DA_PAGINA = 50;

    private final RepositorioDeGiria repositorio;
    private final RepositorioDeCategoria categorias;

    public ServicoDeDicionario(RepositorioDeGiria repositorio,
                               RepositorioDeCategoria categorias) {
        this.repositorio = repositorio;
        this.categorias = categorias;
    }

    /**
     * Prateleiras do catálogo, com quantos verbetes cada uma tem.
     *
     * <p>Categoria sem nenhum verbete visível fica de fora. Uma prateleira
     * vazia só se revela vazia depois que a pessoa clica e espera, e o
     * catálogo existe justamente para quem não sabe o que procurar.</p>
     *
     * <p>Com o modo família ligado, a contagem é dos verbetes que a pessoa vai
     * conseguir abrir, não do total. Mostrar 40 e listar 31 seria dizer que
     * há algo escondido ali, que é o oposto do que o modo família faz.</p>
     */
    @Cacheable(cacheNames = "categorias", key = "#modoFamilia")
    @Transactional(readOnly = true)
    public List<CategoriaResumo> categorias(boolean modoFamilia) {
        return categorias.contar(!modoFamilia).stream()
                .filter(linha -> linha.getQuantidade() > 0)
                .map(linha -> new CategoriaResumo(
                        linha.getSlug(), linha.getNome(), linha.getQuantidade()))
                .toList();
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

    /**
     * Busca e navegação numa consulta só.
     *
     * <p>Com {@code consulta} preenchida é a busca da tela de dicionário. Com
     * ela vazia e uma {@code categoria}, é o catálogo abrindo uma prateleira,
     * em ordem alfabética. É o mesmo caminho de código de propósito: eram duas
     * consultas com a mesma paginação, o mesmo filtro de idioma e a mesma
     * regra de modo família, e manter as duas em dia seria trabalho repetido
     * com uma chance a mais de divergirem.</p>
     */
    @Transactional(readOnly = true)
    public Pagina<GiriaResumo> pesquisar(String consulta, String idioma, String categoria,
                                         boolean modoFamilia, int pagina, int tamanho) {
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
                vazioComoNulo(categoria),
                !modoFamilia,
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
     * Trata {@code ?categoria=} como "sem filtro".
     *
     * <p>O parâmetro vazio chega como string vazia, não como nulo, e a
     * consulta compara {@code c.slug = :categoria}: sem esta conversão, o
     * catálogo com o filtro limpo devolveria zero resultados em vez de tudo,
     * porque nenhum slug é a string vazia.</p>
     */
    private static String vazioComoNulo(String valor) {
        return valor == null || valor.isBlank() ? null : valor;
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
        String colapsado = Normalizador.colapsarRepeticoes(normalizado);
        java.util.Optional<Giria> porEnfase = repositorio
                .findByTermoColapsado(colapsado)
                .stream()
                .findFirst();
        if (porEnfase.isPresent()) {
            return porEnfase;
        }

        /*
         * Variação de escrita, ANTES da busca por semelhança.
         *
         * A ordem é o ponto: sem este passo, "pepelef" (variação registrada de
         * "PepeLaugh") caía no trigram e voltava "pepeD", que compartilha
         * trigramas suficientes. Uma variação registrada à mão pela curadoria
         * é uma resposta exata, e resposta exata nunca pode perder para
         * palpite.
         */
        java.util.Optional<Giria> porVariacao = repositorio
                .buscarPorVariacao(normalizado, colapsado)
                .stream()
                .findFirst();
        if (porVariacao.isPresent()) {
            return porVariacao;
        }

        return repositorio
                .buscarPorSemelhanca(normalizado, LIMIAR_DE_SEMELHANCA, 1)
                .stream()
                .findFirst();
    }
}
