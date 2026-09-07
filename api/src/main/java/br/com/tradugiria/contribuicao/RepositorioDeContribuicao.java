package br.com.tradugiria.contribuicao;

import br.com.tradugiria.contribuicao.Contribuicao.StatusDeContribuicao;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RepositorioDeContribuicao extends JpaRepository<Contribuicao, Long> {

    long countByUsuarioIdAndStatus(Long usuarioId, StatusDeContribuicao status);

    List<Contribuicao> findByUsuarioIdOrderByCriadoEmDesc(Long usuarioId);

    List<Contribuicao> findByStatusOrderByCriadoEm(StatusDeContribuicao status, Pageable pagina);
}
