package br.com.tradugil.dicionario;

import br.com.tradugil.TestecomBanco;
import br.com.tradugil.dicionario.PacoteDtos.Pacote;
import br.com.tradugil.dicionario.PacoteDtos.VerbeteDoPacote;
import br.com.tradugil.traducao.Normalizador;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@TestecomBanco
class ServicoDePacoteIT {

    @Autowired
    private ServicoDePacote servico;

    @Test
    @DisplayName("o pacote traz o dicionário inteiro")
    void trazODicionarioInteiro() {
        Pacote pacote = servico.pacoteCompleto();

        assertThat(pacote.qtdVerbetes()).isGreaterThanOrEqualTo(60);
        assertThat(pacote.verbetes()).hasSize(pacote.qtdVerbetes());
        assertThat(pacote.versao()).isNotBlank();
    }

    @Test
    @DisplayName("todo verbete do pacote explica alguma coisa")
    void todoVerbeteExplica() {
        // Verbete sem explicação gravado no aparelho é pior que ausente: ele
        // aparece no destaque e não diz nada quando a pessoa toca.
        assertThat(servico.pacoteCompleto().verbetes())
                .allSatisfy(v -> assertThat(v.explicacaoSimples()).isNotBlank());
    }

    @Test
    @DisplayName("as chaves de busca do pacote batem com o que o cliente calcula")
    void chavesBatemComOCliente() {
        // O cliente offline vai procurar pela forma que ele mesmo normaliza.
        // Se o pacote trouxesse outra chave, o termo entraria no aparelho e
        // nunca seria encontrado: sem erro nenhum que denunciasse.
        assertThat(servico.pacoteCompleto().verbetes()).allSatisfy(v -> {
            assertThat(v.termoNormalizado()).isEqualTo(Normalizador.normalizar(v.termo()));
            assertThat(v.termoColapsado())
                    .isEqualTo(Normalizador.colapsarRepeticoes(v.termoNormalizado()));
        });
    }

    @Test
    @DisplayName("as variações vêm junto, com as duas formas de busca")
    void variacoesVemJunto() {
        VerbeteDoPacote pog = servico.pacoteCompleto().verbetes().stream()
                .filter(v -> v.termoNormalizado().equals("pog"))
                .findFirst()
                .orElseThrow();

        assertThat(pog.variacoes())
                .extracting(PacoteDtos.VariacaoDoPacote::normalizada)
                .contains("pogchamp");
    }

    @Test
    @DisplayName("os metadados batem com o pacote e são muito menores")
    void metadadosBatemComOPacote() {
        var meta = servico.metadados();
        Pacote pacote = servico.pacoteCompleto();

        assertThat(meta.versao()).isEqualTo(pacote.versao());
        assertThat(meta.qtdVerbetes()).isEqualTo(pacote.qtdVerbetes());
    }

    @Test
    @DisplayName("a versão muda quando o conteúdo muda, e só então")
    void versaoDerivaDoConteudo() {
        String antes = servico.pacoteCompleto().versao();
        String denovo = servico.pacoteCompleto().versao();

        // Duas leituras seguidas sem edição não podem gerar versões
        // diferentes: o cliente rebaixaria o dicionário inteiro à toa.
        assertThat(denovo).isEqualTo(antes);
    }

    @Test
    @DisplayName("o pacote vem ordenado, para o download ser reproduzível")
    void pacoteVemOrdenado() {
        assertThat(servico.pacoteCompleto().verbetes())
                .extracting(VerbeteDoPacote::termoNormalizado)
                .isSorted();
    }
}
