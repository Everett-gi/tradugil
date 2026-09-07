package br.com.tradugil.android.dados.local

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Transaction

@Dao
interface DicionarioDao {

    /**
     * Resolve o texto inteiro numa consulta só.
     *
     * <p>Colar uma conversa produz dezenas de candidatos. Perguntar um por
     * vez seriam dezenas de idas ao SQLite dentro do laço de detecção: no
     * aparelho de quem o produto atende, isso aparece como travamento.</p>
     *
     * <p>Casa pelas quatro formas em que um verbete pode ser alcançado:
     * termo, termo colapsado, variação e variação colapsada. A comparação
     * por ênfase é colapsado contra colapsado: o verbete guardado é "kkk",
     * o usuário escreveu "kkkkkkk", e reduzir só um dos lados nunca os faria
     * encontrar-se.</p>
     */
    @Query(
        """
        SELECT DISTINCT v.* FROM verbete v
        LEFT JOIN variacao a ON a.termoNormalizado = v.termoNormalizado
        WHERE v.termoNormalizado IN (:candidatos)
           OR v.termoColapsado   IN (:colapsados)
           OR a.variacaoNormalizada IN (:candidatos)
           OR a.variacaoColapsada   IN (:colapsados)
        """
    )
    suspend fun buscarPorCandidatos(
        candidatos: List<String>,
        colapsados: List<String>,
    ): List<VerbeteLocal>

    @Query("SELECT COUNT(*) FROM verbete")
    suspend fun quantidade(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardarVerbetes(verbetes: List<VerbeteLocal>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardarVariacoes(variacoes: List<VariacaoLocal>)

    /**
     * Grava verbetes e variações juntos.
     *
     * <p>Numa transação só porque um verbete gravado sem as variações fica
     * pela metade: "pog" resolveria e "pogchamp" não, sem erro nenhum que
     * denunciasse o problema.</p>
     */
    @Transaction
    suspend fun guardar(verbetes: List<VerbeteLocal>, variacoes: List<VariacaoLocal>) {
        guardarVerbetes(verbetes)
        guardarVariacoes(variacoes)
    }
}

@Database(
    entities = [VerbeteLocal::class, VariacaoLocal::class],
    version = 1,
    exportSchema = false,
)
abstract class BancoLocal : RoomDatabase() {
    abstract fun dicionario(): DicionarioDao
}
