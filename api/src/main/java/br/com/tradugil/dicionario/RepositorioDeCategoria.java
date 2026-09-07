package br.com.tradugil.dicionario;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RepositorioDeCategoria extends JpaRepository<Categoria, Short> {

    /**
     * Categorias com quantos verbetes cada uma tem, para o catálogo.
     *
     * <p>A contagem não é enfeite: é o que permite ao catálogo esconder
     * categoria vazia. Uma prateleira com o nome escrito e nada atrás dela é
     * pior do que prateleira nenhuma, porque quem clica só descobre isso
     * depois de esperar o carregamento.</p>
     *
     * <p>O {@code LEFT JOIN} com o filtro de nsfw dentro do {@code ON}, e não
     * num {@code WHERE}, é deliberado: no {@code WHERE} ele descartaria a
     * linha inteira da categoria sem nenhum verbete visível, e ela sumiria do
     * resultado em vez de aparecer com zero. Aqui quem decide o que fazer com
     * o zero é o serviço, que é onde a regra está escrita.</p>
     *
     * <p>Nativa, e não JPQL, porque a contagem precisa do {@code LEFT JOIN}
     * pela tabela de ligação sem carregar as gírias: em JPQL sobre a coleção
     * mapeada, o Hibernate traria as entidades para contar.</p>
     */
    @Query(value = """
            SELECT c.slug AS slug, c.nome AS nome, COUNT(g.id) AS quantidade
            FROM categoria c
            LEFT JOIN giria_categoria gc ON gc.categoria_id = c.id
            LEFT JOIN giria g ON g.id = gc.giria_id
                 AND (CAST(:incluirNsfw AS BOOLEAN) OR NOT g.nsfw)
            GROUP BY c.slug, c.nome
            ORDER BY c.nome
            """, nativeQuery = true)
    List<ContagemDeCategoria> contar(@Param("incluirNsfw") boolean incluirNsfw);

    /** Projeção do resultado acima. Os nomes casam com os apelidos da consulta. */
    interface ContagemDeCategoria {
        String getSlug();

        String getNome();

        long getQuantidade();
    }
}
