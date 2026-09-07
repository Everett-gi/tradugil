package br.com.tradugil.contribuicao;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * So leitura e insercao. Nao ha metodo de alteracao nem de remocao de
 * proposito: o banco recusaria por gatilho, e a ausencia aqui evita que
 * alguem escreva o codigo e descubra so em tempo de execucao.
 */
public interface RepositorioDeAuditoria extends JpaRepository<AuditoriaDeModeracao, Long> {

    List<AuditoriaDeModeracao> findByContribuicaoIdOrderByOcorridoEm(Long contribuicaoId);
}
