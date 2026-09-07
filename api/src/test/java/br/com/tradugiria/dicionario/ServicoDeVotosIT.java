package br.com.tradugiria.dicionario;

import br.com.tradugiria.TestecomBanco;
import br.com.tradugiria.comum.erro.RecursoNaoEncontradoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@TestecomBanco
class ServicoDeVotosIT {

    @Autowired
    private ServicoDeVotos servico;

    @Autowired
    private ServicoDeDicionario dicionario;

    @Autowired
    private RepositorioDeDefinicao repositorio;

    @Test
    @DisplayName("votar útil incrementa o contador que ordena as definições")
    void votarIncrementaOContador() {
        Long id = umaDefinicaoAprovada();
        int antes = repositorio.findById(id).orElseThrow().getVotosUteis();

        servico.votar(id, true);

        assertThat(repositorio.findById(id).orElseThrow().getVotosUteis())
                .isEqualTo(antes + 1);
    }

    @Test
    @DisplayName("voto negativo vai para o contador separado")
    void votoNegativoVaiParaOContadorSeparado() {
        Long id = umaDefinicaoAprovada();
        int uteisAntes = repositorio.findById(id).orElseThrow().getVotosUteis();
        int inuteisAntes = repositorio.findById(id).orElseThrow().getVotosInuteis();

        servico.votar(id, false);

        Definicao depois = repositorio.findById(id).orElseThrow();
        assertThat(depois.getVotosInuteis()).isEqualTo(inuteisAntes + 1);
        assertThat(depois.getVotosUteis()).isEqualTo(uteisAntes);
    }

    @Test
    @DisplayName("definição inexistente devolve não encontrado")
    void definicaoInexistente() {
        assertThatThrownBy(() -> servico.votar(999_999_999L, true))
                .isInstanceOf(RecursoNaoEncontradoException.class);
    }

    @Test
    @DisplayName("votos simultâneos não se perdem")
    void votosSimultaneosNaoSePerdem() throws Exception {
        Long id = umaDefinicaoAprovada();
        int antes = repositorio.findById(id).orElseThrow().getVotosUteis();

        // É o caso que justifica o UPDATE atômico existir. Com "ler, somar,
        // gravar" em Java, as threads leriam o mesmo valor e gravariam o
        // mesmo resultado — e a maioria dos votos sumiria sem erro nenhum.
        int votantes = 20;
        ExecutorService piscina = Executors.newFixedThreadPool(8);
        CountDownLatch largada = new CountDownLatch(1);
        CountDownLatch chegada = new CountDownLatch(votantes);

        for (int i = 0; i < votantes; i++) {
            piscina.submit(() -> {
                try {
                    largada.await();
                    servico.votar(id, true);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    chegada.countDown();
                }
            });
        }

        largada.countDown();
        assertThat(chegada.await(30, TimeUnit.SECONDS)).isTrue();
        piscina.shutdown();

        assertThat(repositorio.findById(id).orElseThrow().getVotosUteis())
                .as("votos perdidos por corrida entre threads")
                .isEqualTo(antes + votantes);
    }

    @Test
    @DisplayName("o verbete devolvido reflete o voto, sem esperar o cache expirar")
    void verbeteRefleteOVotoNaHora() {
        // Consulta primeiro para o verbete entrar no cache.
        int antes = dicionario.porTermo("cringe", "en").definicoes().getFirst().votosUteis();
        Long id = dicionario.porTermo("cringe", "en").definicoes().getFirst().id();

        servico.votar(id, true);

        // Sem limpar o cache, a pessoa veria o próprio clique não fazer efeito.
        assertThat(dicionario.porTermo("cringe", "en").definicoes().getFirst().votosUteis())
                .isEqualTo(antes + 1);
    }

    private Long umaDefinicaoAprovada() {
        return dicionario.porTermo("pog", "en").definicoes().getFirst().id();
    }
}
