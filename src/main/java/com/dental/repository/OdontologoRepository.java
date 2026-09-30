package com.dental.repository;

import com.dental.model.Odontologo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface OdontologoRepository extends JpaRepository<Odontologo, Long> {
    Optional<Odontologo> findByUsuarioId(Long usuarioId);
}
