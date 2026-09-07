package br.com.tradugiria.dicionario;

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
     * banco por requisição — em free tier, é o suficiente para estourar o
     * limite de conexões e derrubar o RNF01. Aqui vai tudo em um IN.</p>
     *
     * <p>Casa tanto pelo termo canônico quanto pela variação, porque para
     * quem consulta não há diferença entre "pog" e "pogchamp".</p>
     */
    @Query("""
            SELECT DISTINCT g FROM Giria g
            LEFT JOIN g.variacoes v
            WHERE g.termoNormalizado IN :candidatos
               OR v.variacaoNormalizada IN :candidatos
            """)
    List<Giria> buscarPorCandidatos(@Param("candidatos") Collection<String> candidatos);

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
     * Busca paginável da tela de dicionário, com filtro opcional de idioma.
     * O {@code :idioma IS NULL} evita duas consultas quase idênticas.
     */
    @Query(value = """
            SELECT g.* FROM giria g
            JOIN idioma i ON i.id = g.idioma_id
            WHERE (:idioma IS NULL OR i.codigo = :idioma)
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
                          @Param("limiar") double limiar,
                          @Param("limite") int limite,
                          @Param("deslocamento") int deslocamento);
}
