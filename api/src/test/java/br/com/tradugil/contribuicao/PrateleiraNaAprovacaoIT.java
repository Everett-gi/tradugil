package br.com.tradugil.contribuicao;

import br.com.tradugil.TestecomBanco;
import br.com.tradugil.comum.erro.RegraDeNegocioException;
import br.com.tradugil.contribuicao.Contribuicao.StatusDeContribuicao;
import br.com.tradugil.contribuicao.ContribuicaoDtos.ContribuicaoResposta;
import br.com.tradugil.contribuicao.ContribuicaoDtos.DecisaoDeModeracao;
import br.com.tradugil.contribuicao.ContribuicaoDtos.NovaContribuicao;
import br.com.tradugil.dicionario.RepositorioDeGiria;
import br.com.tradugil.dicionario.ServicoDeDicionario;
import br.com.tradugil.identidade.RepositorioDeUsuario;
import br.com.tradugil.identidade.Usuario;
import br.com.tradugil.identidade.Usuario.Papel;
import br.com.tradugil.traducao.Normalizador;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * A prateleira do catálogo, escolhida na aprovação.
 *
 * <h2>O que estava errado</h2>
 *
 * <p>O verbete vindo da comunidade nascia sem categoria. Nada quebrava, nada
 * aparecia no log, os testes de publicação continuavam verdes: ele era
 * encontrado pela busca e pelo {@code /traduzir} normalmente.</p>
 *
 * <p>E nunca aparecia no catálogo, que é organizado por prateleira. O
 * catálogo é justamente por onde chega quem não sabe o que procurar, que é o
 * público que o produto existe para atender. Um verbete invisível para esse
 * público é meio verbete, e a falha era silenciosa: só apareceria para quem
 * abrisse o catálogo procurando o termo que acabou de aprovar.</p>
 *
 * <p>Adivinhar a categoria pelo texto foi descartado de propósito: prateleira
 * errada é pior que prateleira nenhuma, porque manda a pessoa procurar no
 * lugar errado e ainda parece que funcionou.</p>
 */
@TestecomBanco
class PrateleiraNaAprovacaoIT {

    @Autowired
    private ServicoDeContribuicao servico;

    @Autowired
    private ServicoDeDicionario dicionario;

    @Autowired
    private RepositorioDeUsuario usuarios;

    @Autowired
    private RepositorioDeGiria girias;

    @Test
    @DisplayName("o verbete aprovado entra na prateleira escolhida")
    void aprovacaoPoeNaPrateleira() {
        Long autor = criar(Papel.USER);
        Long moderador = criar(Papel.MODERATOR);
        String termo = termoSo();

        ContribuicaoResposta proposta = servico.propor(autor, new NovaContribuicao(
                termo, "pt-BR", "Uma explicação de teste com tamanho suficiente."));
        servico.decidir(moderador, proposta.id(), new DecisaoDeModeracao(true, null, "gaming"));

        assertThat(dicionario.porTermo(termo, "pt-BR").categorias())
                .as("sem isto o verbete existe na busca e some do catálogo")
                .contains("gaming");
    }

    @Test
    @DisplayName("aprovar sem prateleira é recusado, e a proposta continua na fila")
    void aprovarSemPrateleiraERecusado() {
        Long autor = criar(Papel.USER);
        Long moderador = criar(Papel.MODERATOR);
        String termo = termoSo();

        ContribuicaoResposta proposta = servico.propor(autor, new NovaContribuicao(
                termo, "pt-BR", "Uma explicação de teste com tamanho suficiente."));

        assertThatThrownBy(() -> servico.decidir(
                moderador, proposta.id(), new DecisaoDeModeracao(true, null, null)))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining("prateleira");

        /*
         * A PARTE QUE IMPORTA DESTE TESTE.
         *
         * A publicação acontece ANTES de marcar como aprovada, e é por isso
         * que a proposta continua pendente aqui. Na ordem inversa ela sairia
         * da fila marcada como aprovada sem nunca ter virado verbete, que é
         * exatamente o beco sem saída que a publicação veio fechar: quem
         * contribuiu veria "aprovada" e não acharia o termo.
         */
        assertThat(servico.minhasPropostas(autor).getFirst().status())
                .as("recusada a aprovação, a proposta tem de continuar na fila")
                .isEqualTo(StatusDeContribuicao.PENDENTE);

        /*
         * BUSCA EXATA, E NÃO localizar().
         *
         * A primeira versão usava localizar() e falhou na CI. Não por bug do
         * código: localizar() termina numa busca por semelhança, de propósito,
         * e os termos que os testes desta classe geram compartilham o prefixo
         * "termoprateleira". Um verbete publicado por outro teste da mesma
         * classe tem trigramas suficientes em comum e volta como palpite.
         *
         * Para "este termo não virou verbete" a pergunta é exata, e usar a
         * busca tolerante para respondê-la mistura duas coisas: o teste
         * passaria a depender de quantos termos parecidos existem no banco.
         */
        assertThat(girias.findByTermoNormalizadoAndIdiomaCodigo(
                Normalizador.normalizar(termo), "pt-BR"))
                .as("aprovação recusada não pode deixar o verbete criado para trás")
                .isEmpty();
    }

    @Test
    @DisplayName("prateleira inexistente é recusada")
    void prateleiraInexistenteERecusada() {
        Long autor = criar(Papel.USER);
        Long moderador = criar(Papel.MODERATOR);

        ContribuicaoResposta proposta = servico.propor(autor, new NovaContribuicao(
                termoSo(), "pt-BR", "Uma explicação de teste com tamanho suficiente."));

        // Slug vem do cliente. Aceitar um desconhecido criaria um verbete
        // numa prateleira que não existe, que é o mesmo resultado de não ter
        // prateleira nenhuma, só que sem ninguém perceber.
        assertThatThrownBy(() -> servico.decidir(moderador, proposta.id(),
                new DecisaoDeModeracao(true, null, "prateleira-que-nao-existe")))
                .isInstanceOf(RegraDeNegocioException.class);
    }

    @Test
    @DisplayName("um sentido novo em termo que já tem prateleira não exige escolher outra")
    void termoJaClassificadoNaoExigeEscolha() {
        /*
         * A regra olha o resultado, e não o formulário. Exigir categoria em
         * toda aprovação empurraria o moderador a escolher uma qualquer para
         * o botão habilitar, e o verbete acumularia prateleiras erradas.
         */
        Long autor = criar(Papel.USER);
        Long moderador = criar(Papel.MODERATOR);
        String termo = termoSo();

        var primeira = servico.propor(autor, new NovaContribuicao(
                termo, "pt-BR", "O primeiro sentido, escrito por quem contribuiu."));
        servico.decidir(moderador, primeira.id(), new DecisaoDeModeracao(true, null, "gaming"));

        var segunda = servico.propor(autor, new NovaContribuicao(
                termo, "pt-BR", "Um segundo sentido, bem diferente do primeiro."));
        servico.decidir(moderador, segunda.id(), new DecisaoDeModeracao(true, null, null));

        var verbete = dicionario.porTermo(termo, "pt-BR");
        assertThat(verbete.definicoes()).hasSize(2);
        assertThat(verbete.categorias())
                .as("nem perdeu a prateleira que tinha, nem ganhou uma segunda à toa")
                .containsExactly("gaming");
    }

    @Test
    @DisplayName("a mesma prateleira duas vezes não duplica o verbete dentro dela")
    void prateleiraRepetidaNaoDuplica() {
        // Sem a checagem de idempotência, a segunda aprovação inseriria a
        // linha de ligação de novo e o verbete apareceria duas vezes dentro
        // da própria categoria.
        Long autor = criar(Papel.USER);
        Long moderador = criar(Papel.MODERATOR);
        String termo = termoSo();

        var primeira = servico.propor(autor, new NovaContribuicao(
                termo, "pt-BR", "O primeiro sentido, escrito por quem contribuiu."));
        servico.decidir(moderador, primeira.id(), new DecisaoDeModeracao(true, null, "gaming"));

        var segunda = servico.propor(autor, new NovaContribuicao(
                termo, "pt-BR", "Um segundo sentido, bem diferente do primeiro."));
        servico.decidir(moderador, segunda.id(), new DecisaoDeModeracao(true, null, "gaming"));

        assertThat(dicionario.porTermo(termo, "pt-BR").categorias())
                .containsExactly("gaming");
    }

    @Test
    @DisplayName("a fila mostra o que o dicionário já diz sobre o termo")
    void filaTrazOVerbeteExistente() {
        /*
         * Quem modera decidia no escuro: a tela mostrava termo, idioma e o
         * texto proposto, e nada mais. Aprovar sem saber que o verbete já
         * existe com um sentido quase igual é como o dicionário ganhou
         * explicações repetidas, que precisaram das migrações V29 e V33 para
         * limpar.
         */
        Long autor = criar(Papel.USER);
        Long moderador = criar(Papel.MODERATOR);
        String termo = termoSo();

        var primeira = servico.propor(autor, new NovaContribuicao(
                termo, "pt-BR", "O sentido que já está publicado no dicionário."));
        servico.decidir(moderador, primeira.id(), new DecisaoDeModeracao(true, null, "gaming"));

        servico.propor(autor, new NovaContribuicao(
                termo, "pt-BR", "Uma proposta nova para o mesmo termo, ainda pendente."));

        var item = servico.fila(StatusDeContribuicao.PENDENTE, 0, 100).stream()
                .filter(i -> i.termo().equals(termo))
                .findFirst()
                .orElseThrow(() -> new AssertionError("a proposta sumiu da fila"));

        assertThat(item.noDicionario()).isNotNull();
        assertThat(item.noDicionario().sentidos())
                .contains("O sentido que já está publicado no dicionário.");
        assertThat(item.noDicionario().categorias()).contains("gaming");
    }

    @Test
    @DisplayName("termo que ainda não existe vem sem verbete na fila")
    void filaMarcaTermoNovo() {
        Long autor = criar(Papel.USER);
        String termo = termoSo();

        servico.propor(autor, new NovaContribuicao(
                termo, "pt-BR", "Uma explicação de teste com tamanho suficiente."));

        var item = servico.fila(StatusDeContribuicao.PENDENTE, 0, 100).stream()
                .filter(i -> i.termo().equals(termo))
                .findFirst()
                .orElseThrow(() -> new AssertionError("a proposta sumiu da fila"));

        // Nulo, e não uma lista vazia: são estados diferentes. Vazio significa
        // "existe e não tem sentido aprovado nenhum", que é outra coisa.
        assertThat(item.noDicionario()).isNull();
    }

    private static String termoSo() {
        return "termoprateleira" + System.nanoTime();
    }

    private Long criar(Papel papel) {
        return usuarios.save(new Usuario(
                "prateleira-" + UUID.randomUUID() + "@exemplo.com",
                "$2a$10$hashdeteste", papel)).getId();
    }
}
