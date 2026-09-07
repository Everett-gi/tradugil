package br.com.tradugil.dicionario;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface RepositorioDeGiria extends JpaRepository<Giria, Long> {

    Optional<Giria> findByTermoNormalizadoAndIdiomaCodigo(String termoNormalizado,
                                                          String codigoDoIdioma);

    List<Giria> findByTermoNormalizado(String termoNormalizado);

    /**
     * Resolve o texto inteiro numa consulta só.
     *
     * <p>O {@code /traduzir} recebe uma conversa colada e precisa checar
     * dezenas de candidatos. Consultar um por vez seriam dezenas de idas ao
     * banco por requisição: em free tier, é o suficiente para estourar o
     * limite de conexões e derrubar o RNF01. Aqui vai tudo em um IN.</p>
     *
     * <p>Casa pelo termo canônico, pela variação e pela forma com ênfase
     * colapsada. Os três caminhos existem porque, para quem consulta, não há
     * diferença entre "pog", "pogchamp" e "kkkkkkk", mas no banco são
     * chaves distintas.</p>
     *
     * <p>A comparação por ênfase é colapsado contra colapsado, e não
     * colapsado contra original: o verbete guardado é "kkk", o usuário
     * escreveu "kkkkkkk", e só reduzir um dos lados nunca os faria
     * encontrar-se.</p>
     */
    @Query("""
            SELECT DISTINCT g FROM Giria g
            LEFT JOIN g.variacoes v
            WHERE g.termoNormalizado IN :candidatos
               OR g.termoColapsado IN :colapsados
               OR v.variacaoNormalizada IN :candidatos
               OR v.variacaoColapsada IN :colapsados
            """)
    List<Giria> buscarPorCandidatos(@Param("candidatos") Collection<String> candidatos,
                                    @Param("colapsados") Collection<String> colapsados);

    /** Busca só pela forma com ênfase colapsada. Usada na consulta de um termo. */
    List<Giria> findByTermoColapsado(String termoColapsado);

    /**
     * Nível 2 da cascata: busca tolerante a erro de digitação.
     *
     * <p>Nativa porque {@code similarity()} é do pg_trgm e não existe em JPQL.
     * O limiar entra como parâmetro em vez de usar {@code %} para não depender
     * do {@code pg_trgm.similarity_threshold} da sessão, que é configuração de
     * servidor e mudaria o resultado sem nenhuma alteração de código.</p>
     */
    @Query(value = """
            SELECT g.* FROM giria g
            WHERE similarity(g.termo_normalizado, :termo) >= :limiar
            ORDER BY similarity(g.termo_normalizado, :termo) DESC, g.termo
            LIMIT :limite
            """, nativeQuery = true)
    List<Giria> buscarPorSemelhanca(@Param("termo") String termo,
                                    @Param("limiar") double limiar,
                                    @Param("limite") int limite);

    /**
     * Busca paginável da tela de dicionário e do catálogo, com filtros
     * opcionais de idioma e de categoria. Os {@code IS NULL} evitam quatro
     * consultas quase idênticas.
     *
     * <p>Com o termo vazio ela vira navegação: {@code LIKE '%'} casa tudo, as
     * duas primeiras chaves de ordenação empatam em zero e sobra a ordem
     * alfabética. É assim que o catálogo lista uma categoria inteira, sem
     * precisar de uma segunda consulta com quase a mesma cara.</p>
     *
     * <p>O {@code incluirNsfw} precisa de {@code CAST}: os outros parâmetros
     * têm o tipo deduzido pela comparação em que aparecem, e este apareceria
     * sozinho. Sem o cast, o Postgres recusa a consulta com "could not
     * determine data type of parameter".</p>
     */
    @Query(value = """
            SELECT g.* FROM giria g
            JOIN idioma i ON i.id = g.idioma_id
            WHERE (:idioma IS NULL OR i.codigo = :idioma)
              AND (:categoria IS NULL OR EXISTS (
                     SELECT 1 FROM giria_categoria gc
                     JOIN categoria c ON c.id = gc.categoria_id
                     WHERE gc.giria_id = g.id AND c.slug = :categoria))
              AND (CAST(:incluirNsfw AS BOOLEAN) OR NOT g.nsfw)
              AND (g.termo_normalizado LIKE :prefixo
                   OR similarity(g.termo_normalizado, :termo) >= :limiar)
            ORDER BY
              CASE WHEN g.termo_normalizado = :termo THEN 0 ELSE 1 END,
              similarity(g.termo_normalizado, :termo) DESC,
              g.termo
            LIMIT :limite OFFSET :deslocamento
            """, nativeQuery = true)
    List<Giria> pesquisar(@Param("termo") String termo,
                          @Param("prefixo") String prefixo,
                          @Param("idioma") String idioma,
                          @Param("categoria") String categoria,
                          @Param("incluirNsfw") boolean incluirNsfw,
                          @Param("limiar") double limiar,
                          @Param("limite") int limite,
                          @Param("deslocamento") int deslocamento);
}
