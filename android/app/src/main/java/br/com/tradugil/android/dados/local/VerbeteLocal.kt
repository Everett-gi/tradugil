package br.com.tradugil.android.dados.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Nível 0 da cascata: o dicionário que vive no aparelho.
 *
 * <p>É o que faz o RNF07 valer: a consulta funciona sem conexão, e a
 * Marlene não gasta o plano de dados para entender uma mensagem. Também é o
 * caminho mais rápido de todos: resolver aqui evita a ida à rede por
 * completo.</p>
 *
 * A chave primária é o termo normalizado, e não um id gerado: o dicionário é
 * um cache reconstruível, não um registro com identidade própria. Assim o
 * `INSERT OR REPLACE` de uma sincronização simplesmente sobrescreve o que já
 * estava, sem precisar procurar antes.
 */
@Entity(
    tableName = "verbete",
    indices = [Index("termoColapsado")],
)
data class VerbeteLocal(
    @PrimaryKey
    @ColumnInfo(name = "termoNormalizado")
    val termoNormalizado: String,

    /**
     * Forma com a ênfase repetida colapsada ("kkkkkkk" e "kkk" viram ambos
     * "kk"). Coluna própria e indexada, espelhando o servidor: sem ela, a
     * busca por ênfase varreria a tabela inteira a cada consulta.
     */
    val termoColapsado: String,

    /** Grafia de exibição, com acento e maiúsculas. */
    val termo: String,

    val idioma: String,
    val explicacaoSimples: String,
    val explicacaoDetalhada: String?,
    val equivalenteFormal: String?,
    val nsfw: Boolean,
    val riscoMenor: Boolean,
)

/**
 * Apelido que aponta para um verbete.
 *
 * Tabela separada, e não uma lista serializada dentro do verbete, porque
 * precisa de índice: a busca pergunta pela variação e quer chegar ao verbete
 * sem ler a tabela toda.
 */
@Entity(
    tableName = "variacao",
    primaryKeys = ["variacaoNormalizada", "termoNormalizado"],
    indices = [Index("termoNormalizado"), Index("variacaoColapsada")],
)
data class VariacaoLocal(
    val variacaoNormalizada: String,
    val variacaoColapsada: String,
    val termoNormalizado: String,
)
