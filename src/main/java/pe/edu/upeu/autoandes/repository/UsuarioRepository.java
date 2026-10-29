package pe.edu.upeu.autoandes.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import pe.edu.upeu.autoandes.entity.Usuario;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    @EntityGraph(attributePaths = "roles")
    Optional<Usuario> findByUsernameIgnoreCase(String username);
}
