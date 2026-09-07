package br.com.tradugiria.dicionario;

import br.com.tradugiria.TestecomBanco;
import br.com.tradugiria.traducao.Normalizador;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Guarda de uma duplicação que não dá para evitar.
 *
 * <p>A migração V4 preenche {@code termo_colapsado} com uma expressão SQL que
 * repete, em outra linguagem, a regra de
 * {@link Normalizador#colapsarRepeticoes}. Repetir é inevitável — o seed é
 * SQL e precisa preencher a coluna — mas repetir sem verificação é como as
 * duas regras divergem em silêncio: alguém ajusta o Java, esquece o SQL, e
 * meses depois um punhado de verbetes simplesmente para de ser encontrado,
 * sem erro nenhum em lugar nenhum.</p>
 *
 * <p>Este teste transforma essa duplicação escondida em duplicação vigiada:
 * ele confere linha a linha, para todo o dicionário, que o que o banco gravou
 * é exatamente o que o Java calcularia. Se as regras divergirem, a CI quebra
 * no mesmo dia.</p>
 */
@TestecomBanco
class ConsistenciaDoSeedIT {

    @Autowired
    private RepositorioDeGiria repositorioDeGiria;

    @Test
    @DisplayName("termo_colapsado do banco bate com o que o Java calcula")
    void colapsadoDoBancoBateComOJava() {
        var divergentes = repositorioDeGiria.findAll().stream()
                .filter(g -> !g.getTermoColapsado()
                        .equals(Normalizador.colapsarRepeticoes(g.getTermoNormalizado())))
                .map(g -> "%s: banco='%s' java='%s'".formatted(
                        g.getTermo(),
                        g.getTermoColapsado(),
                        Normalizador.colapsarRepeticoes(g.getTermoNormalizado())))
                .toList();

        assertThat(divergentes)
                .as("a regra SQL da migração V4 divergiu de Normalizador.colapsarRepeticoes")
                .isEmpty();
    }

    @Test
    @DisplayName("variacao_colapsada do banco bate com o que o Java calcula")
    void colapsadoDasVariacoesBateComOJava() {
        var divergentes = repositorioDeGiria.findAll().stream()
                .flatMap(g -> g.getVariacoes().stream())
                .filter(v -> !v.getVariacaoColapsada()
                        .equals(Normalizador.colapsarRepeticoes(v.getVariacaoNormalizada())))
                .map(GiriaVariacao::getVariacao)
                .toList();

        assertThat(divergentes).isEmpty();
    }

    @Test
    @DisplayName("termo_normalizado do seed bate com o que o Java calcula")
    void normalizadoDoSeedBateComOJava() {
        // O seed traz o normalizado escrito à mão ao lado de cada termo. Uma
        // digitação errada ali cria um verbete que existe no banco e nunca é
        // encontrado por ninguém.
        var divergentes = repositorioDeGiria.findAll().stream()
                .filter(g -> !g.getTermoNormalizado().equals(Normalizador.normalizar(g.getTermo())))
                .map(g -> "%s: seed='%s' java='%s'".formatted(
                        g.getTermo(),
                        g.getTermoNormalizado(),
                        Normalizador.normalizar(g.getTermo())))
                .toList();

        assertThat(divergentes)
                .as("verbetes cuja chave de busca não corresponde ao próprio termo")
                .isEmpty();
    }

    @Test
    @DisplayName("todo verbete tem ao menos uma definição aprovada")
    void todoVerbeteTemDefinicao() {
        // Verbete sem definição aparece na busca e não explica nada — pior
        // que não existir, porque o usuário acha que encontrou.
        var mudos = repositorioDeGiria.findAll().stream()
                .filter(g -> g.definicoesAprovadas().isEmpty())
                .map(Giria::getTermo)
                .toList();

        assertThat(mudos).isEmpty();
    }
}
