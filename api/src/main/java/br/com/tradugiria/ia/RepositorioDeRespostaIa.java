package br.com.tradugiria.ia;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RepositorioDeRespostaIa extends JpaRepository<RespostaIa, Long> {

    Optional<RespostaIa> findByTermoNormalizadoAndHashDoContexto(String termoNormalizado,
                                                                 String hashDoContexto);

    /**
     * Grava a resposta ignorando quem já tiver chegado antes.
     *
     * <p>É UPSERT, e não {@code save()}, por causa da corrida: duas
     * requisições simultâneas pelo mesmo termo desconhecido violariam a chave
     * única, e essa falha marcaria a transação da consulta como
     * inconsistente — derrubando com erro 500 uma tradução que já tinha
     * resposta útil para dar.</p>
     *
     * <p>{@code DO NOTHING} e não {@code DO UPDATE}: se a outra requisição já
     * gravou, as duas respostas vieram da mesma pergunta e vale a que chegou
     * primeiro. Sobrescrever só gastaria escrita.</p>
     */
    @Modifying
    @Transactional
    @Query(value = """
            INSERT INTO resposta_ia
                (termo_normalizado, hash_do_contexto, e_giria, explicacao_simples,
                 explicacao_detalhada, equivalente_formal, nsfw, risco_menor,
                 confianca, promovida, criado_em)
            VALUES (:termo, :hash, :eGiria, :simples, :detalhada, :formal,
                    :nsfw, :risco, :confianca, FALSE, now())
            ON CONFLICT (termo_normalizado, hash_do_contexto) DO NOTHING
            """, nativeQuery = true)
    void guardar(@Param("termo") String termoNormalizado,
                 @Param("hash") String hashDoContexto,
                 @Param("eGiria") boolean eGiria,
                 @Param("simples") String explicacaoSimples,
                 @Param("detalhada") String explicacaoDetalhada,
                 @Param("formal") String equivalenteFormal,
                 @Param("nsfw") boolean nsfw,
                 @Param("risco") boolean riscoMenor,
                 @Param("confianca") float confianca);

    /**
     * Fila da curadoria: o que a IA respondeu, ninguém revisou ainda, e vale
     * a pena revisar primeiro.
     *
     * <p>Escrita como consulta explícita em vez de derivada do nome do
     * método: a propriedade {@code eGiria} começa com letra minúscula
     * seguida de maiúscula, e a convenção de nomes do Spring Data resolve
     * esse caso de forma ambígua.</p>
     */
    @Query("""
            SELECT r FROM RespostaIa r
            WHERE r.promovida = FALSE AND r.eGiria = TRUE
            ORDER BY r.confianca DESC, r.criadoEm
            """)
    List<RespostaIa> filaDeCuradoria(org.springframework.data.domain.Pageable pagina);
}
