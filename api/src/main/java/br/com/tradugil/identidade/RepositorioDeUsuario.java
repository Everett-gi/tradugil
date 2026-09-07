package br.com.tradugil.identidade;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RepositorioDeUsuario extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);
}
