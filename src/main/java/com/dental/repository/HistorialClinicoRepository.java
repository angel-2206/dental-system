package com.dental.repository;

import com.dental.model.HistorialClinico;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HistorialClinicoRepository extends JpaRepository<HistorialClinico, Long> {
    List<HistorialClinico> findByPacienteId(Long pacienteId);
    List<HistorialClinico> findByOdontologoId(Long odontologoId);
}
