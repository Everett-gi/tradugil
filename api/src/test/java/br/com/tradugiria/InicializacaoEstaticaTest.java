package br.com.tradugiria;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * Força a inicialização estática das classes que declaram constantes de
 * coleção.
 *
 * <p>Existe por causa de um bug real: {@code PALAVRAS_IGNORADAS} tinha o
 * termo "nos" repetido, e {@code Set.of} rejeita duplicatas com
 * {@code IllegalArgumentException} no carregamento da classe. Como nenhum
 * teste de unidade tocava nessa classe, o erro só apareceu quando a CI subiu
 * o contexto do Spring — e, em produção, teria impedido a aplicação inteira
 * de iniciar por causa de uma palavra digitada duas vezes.</p>
 *
 * <p>Este teste roda em milissegundos e não precisa de banco: erro de
 * inicialização estática é barato de detectar e caro de descobrir tarde.</p>
 */
class InicializacaoEstaticaTest {

    @ParameterizedTest(name = "{0} inicializa sem erro")
    @DisplayName("as constantes estáticas carregam")
    @ValueSource(strings = {
            "br.com.tradugiria.traducao.ServicoDeTraducao",
            "br.com.tradugiria.traducao.Normalizador",
            "br.com.tradugiria.traducao.Tokenizador",
            "br.com.tradugiria.dicionario.ServicoDeDicionario",
            "br.com.tradugiria.comum.ConfiguracaoDeCache",
            "br.com.tradugiria.comum.seguranca.ConfiguracaoDeSeguranca",
    })
    void classeInicializaSemErro(String nomeDaClasse) {
        assertThatCode(() ->
                Class.forName(nomeDaClasse, true, getClass().getClassLoader()))
                .doesNotThrowAnyException();
    }
}
