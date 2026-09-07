package br.com.tradugiria.contribuicao;

import br.com.tradugiria.TestecomBanco;
import br.com.tradugiria.comum.erro.RegraDeNegocioException;
import br.com.tradugiria.contribuicao.Contribuicao.StatusDeContribuicao;
import br.com.tradugiria.contribuicao.ContribuicaoDtos.ContribuicaoResposta;
import br.com.tradugiria.contribuicao.ContribuicaoDtos.DecisaoDeModeracao;
import br.com.tradugiria.contribuicao.ContribuicaoDtos.NovaContribuicao;
import br.com.tradugiria.identidade.RepositorioDeUsuario;
import br.com.tradugiria.identidade.Usuario;
import br.com.tradugiria.identidade.Usuario.Papel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@TestecomBanco
class ModeracaoIT {

    @Autowired
    private ServicoDeContribuicao servico;

    @Autowired
    private RepositorioDeUsuario repositorioDeUsuario;

    @Autowired
    private RepositorioDeAuditoria repositorioDeAuditoria;

    @Autowired
    private JdbcTemplate jdbc;

    @Test
    @DisplayName("a proposta entra como PENDENTE, nunca direto no dicionário")
    void propostaEntraPendente() {
        Long autor = criarUsuario(Papel.USER);

        ContribuicaoResposta proposta = servico.propor(autor, new NovaContribuicao(
                "bagulho", "pt-BR", "Coisa, objeto indefinido. Serve para qualquer coisa."));

        assertThat(proposta.status()).isEqualTo(StatusDeContribuicao.PENDENTE);
    }

    @Test
    @DisplayName("aprovar grava a decisão e a trilha de auditoria juntas")
    void aprovarGravaAuditoria() {
        Long autor = criarUsuario(Papel.USER);
        Long moderador = criarUsuario(Papel.MODERATOR);

        ContribuicaoResposta proposta = servico.propor(autor, new NovaContribuicao(
                "brotou", "pt-BR", "Apareceu de repente, chegou sem avisar."));
        servico.decidir(moderador, proposta.id(), new DecisaoDeModeracao(true, null));

        var trilha = repositorioDeAuditoria
                .findByContribuicaoIdOrderByOcorridoEm(proposta.id());

        assertThat(trilha).hasSize(1);
        assertThat(trilha.getFirst().getAcao()).isEqualTo(StatusDeContribuicao.APROVADA);
    }

    @Test
    @DisplayName("rejeitar sem motivo é recusado")
    void rejeitarSemMotivoERecusado() {
        Long autor = criarUsuario(Papel.USER);
        Long moderador = criarUsuario(Papel.MODERATOR);

        ContribuicaoResposta proposta = servico.propor(autor, new NovaContribuicao(
                "xablau", "pt-BR", "Palavra usada quando não se lembra do nome da coisa."));

        assertThatThrownBy(() -> servico.decidir(
                moderador, proposta.id(), new DecisaoDeModeracao(false, "  ")))
                .isInstanceOf(RegraDeNegocioException.class);
    }

    @Test
    @DisplayName("a mesma proposta não pode ser decidida duas vezes")
    void naoDecideDuasVezes() {
        Long autor = criarUsuario(Papel.USER);
        Long moderador = criarUsuario(Papel.MODERATOR);

        ContribuicaoResposta proposta = servico.propor(autor, new NovaContribuicao(
                "treteiro", "pt-BR", "Quem vive arrumando confusão com os outros."));
        servico.decidir(moderador, proposta.id(), new DecisaoDeModeracao(true, null));

        // Duas abas abertas na fila fariam dois moderadores decidirem a mesma
        // proposta, e a segunda sobrescreveria a primeira em silêncio.
        assertThatThrownBy(() -> servico.decidir(
                moderador, proposta.id(), new DecisaoDeModeracao(false, "mudei de ideia")))
                .isInstanceOf(RegraDeNegocioException.class);
    }

    @Test
    @DisplayName("o banco recusa alterar um registro de auditoria")
    void auditoriaNaoPodeSerAlterada() {
        Long autor = criarUsuario(Papel.USER);
        Long moderador = criarUsuario(Papel.MODERATOR);

        ContribuicaoResposta proposta = servico.propor(autor, new NovaContribuicao(
                "pilantragem", "pt-BR", "Comportamento desonesto, safadeza."));
        servico.decidir(moderador, proposta.id(), new DecisaoDeModeracao(true, null));

        Long idDaTrilha = repositorioDeAuditoria
                .findByContribuicaoIdOrderByOcorridoEm(proposta.id())
                .getFirst().getId();

        // A mitigação de Repudiation não é convenção: o gatilho recusa. Um
        // moderador que aprovou algo impróprio não consegue apagar o rastro
        // nem por SQL direto.
        assertThatThrownBy(() -> jdbc.update(
                "UPDATE auditoria_de_moderacao SET acao = 'REJEITADA' WHERE id = ?", idDaTrilha))
                .hasMessageContaining("append-only");

        assertThatThrownBy(() -> jdbc.update(
                "DELETE FROM auditoria_de_moderacao WHERE id = ?", idDaTrilha))
                .hasMessageContaining("append-only");
    }

    @Test
    @DisplayName("um usuário não pode entupir a fila de moderação")
    void limiteDePendentesPorUsuario() {
        Long autor = criarUsuario(Papel.USER);

        for (int i = 0; i < 10; i++) {
            servico.propor(autor, new NovaContribuicao(
                    "termo" + i, "pt-BR", "Explicação de teste número " + i + " para a fila."));
        }

        assertThatThrownBy(() -> servico.propor(autor, new NovaContribuicao(
                "termo-extra", "pt-BR", "Mais uma explicação de teste para a fila.")))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining("aguardando revisão");
    }

    @Test
    @DisplayName("termo só de pontuação é recusado")
    void termoSoDePontuacaoERecusado() {
        Long autor = criarUsuario(Papel.USER);

        // Normalizaria para vazio e viraria um verbete que ninguém acharia.
        assertThatThrownBy(() -> servico.propor(autor, new NovaContribuicao(
                "!!!???", "pt-BR", "Uma explicação qualquer com tamanho suficiente.")))
                .isInstanceOf(RegraDeNegocioException.class);
    }

    @Test
    @DisplayName("idioma desconhecido é recusado")
    void idiomaDesconhecidoERecusado() {
        Long autor = criarUsuario(Papel.USER);

        assertThatThrownBy(() -> servico.propor(autor, new NovaContribuicao(
                "hola", "es-ES", "Uma explicação qualquer com tamanho suficiente.")))
                .isInstanceOf(RegraDeNegocioException.class);
    }

    private Long criarUsuario(Papel papel) {
        return repositorioDeUsuario.save(new Usuario(
                "mod-" + UUID.randomUUID() + "@exemplo.com", "$2a$10$hashdeteste", papel)).getId();
    }
}
