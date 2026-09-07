package br.com.tradugiria.dicionario;

import br.com.tradugiria.comum.erro.RecursoNaoEncontradoException;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Fecha o ciclo que faz o dicionário melhorar sozinho.
 *
 * <p>{@code Giria.definicoesAprovadas()} ordena por utilidade: o sentido que
 * as pessoas marcam como útil sobe e passa a ser o primeiro que aparece na
 * consulta rápida. Sem este serviço, aquele contador nunca mudava e a
 * ordenação era decorativa.</p>
 *
 * <h2>Por que não há proteção contra voto repetido</h2>
 *
 * <p>O endpoint é público — exigir conta para dizer "essa explicação me
 * ajudou" afastaria justamente a Marlene, que é quem mais teria o que dizer.
 * E identificar quem votou exigiria guardar alguma marca da pessoa, o que a
 * seção 8 do documento não permite para quem só está consultando.</p>
 *
 * <p>A consequência é assumida: um bot determinado consegue inflar um
 * contador. O dano é limitado de propósito — o voto <b>só reordena</b>
 * definições que já foram aprovadas por uma pessoa. Ele nunca publica,
 * nunca oculta e nunca altera texto. O pior resultado possível é um sentido
 * legítimo aparecer antes de outro sentido legítimo.</p>
 *
 * <p>Contenção real fica onde ela é barata e não custa privacidade: o limite
 * por IP na borda, no Caddy.</p>
 */
@Service
public class ServicoDeVotos {

    private final RepositorioDeDefinicao repositorio;
    private final CacheManager gerenciadorDeCache;

    public ServicoDeVotos(RepositorioDeDefinicao repositorio, CacheManager gerenciadorDeCache) {
        this.repositorio = repositorio;
        this.gerenciadorDeCache = gerenciadorDeCache;
    }

    @Transactional
    public void votar(Long definicaoId, boolean util) {
        int afetadas = repositorio.registrarVoto(definicaoId, util ? 1 : 0, util ? 0 : 1);

        if (afetadas == 0) {
            // Zero linhas cobre dois casos — não existe, ou não está
            // aprovada. Os dois viram "não encontrado" para quem consulta:
            // dizer "existe mas está pendente" revelaria a fila de moderação
            // a qualquer pessoa.
            throw new RecursoNaoEncontradoException("Definição não encontrada.");
        }

        // O verbete em cache carrega a ordem antiga das definições. Sem
        // limpar, o voto só apareceria quando o cache expirasse — e a pessoa
        // veria o próprio clique não fazer efeito nenhum.
        var cache = gerenciadorDeCache.getCache("verbetes");
        if (cache != null) {
            cache.clear();
        }
    }
}
