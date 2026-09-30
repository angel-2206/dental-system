package com.dental.repository;

import com.dental.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByUsername(String username);
    Optional<Usuario> findByEmail(String email);
    List<Usuario> findByEstado(Usuario.EstadoCuenta estado);
    List<Usuario> findByRol(Usuario.Rol rol);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
