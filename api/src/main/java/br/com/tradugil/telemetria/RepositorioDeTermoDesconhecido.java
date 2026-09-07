package br.com.tradugil.telemetria;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RepositorioDeTermoDesconhecido extends JpaRepository<TermoDesconhecido, Long> {

    /**
     * Incrementa o contador do termo, criando a linha se for a primeira vez.
     *
     * <p>É um UPSERT nativo, e não um "buscar, decidir, gravar" em Java, por
     * duas razões: requisições simultâneas com o mesmo termo desconhecido
     * violariam a chave única no caminho de leitura e devolveriam erro 500
     * para uma consulta que funcionou; e o caminho de leitura não pode pagar
     * o custo de uma consulta a mais só para alimentar a curadoria.</p>
     */
    @Modifying
    @Transactional
    @Query(value = """
            INSERT INTO termo_desconhecido
                (termo_normalizado, idioma_provavel, ocorrencias, ultima_ocorrencia)
            VALUES (:termo, :idioma, 1, now())
            ON CONFLICT (termo_normalizado) DO UPDATE
                SET ocorrencias = termo_desconhecido.ocorrencias + 1,
                    ultima_ocorrencia = now()
            """, nativeQuery = true)
    void registrar(@Param("termo") String termoNormalizado,
                   @Param("idioma") String idiomaProvavel);

    List<TermoDesconhecido> findTop50ByOrderByOcorrenciasDesc();
}
