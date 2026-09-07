package br.com.tradugiria.dicionario;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RepositorioDeIdioma extends JpaRepository<Idioma, Short> {

    Optional<Idioma> findByCodigo(String codigo);
}
