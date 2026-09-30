package com.dental.repository;

import com.dental.model.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CitaRepository extends JpaRepository<Cita, Long> {
    List<Cita> findByPacienteId(Long pacienteId);
    List<Cita> findByOdontologoId(Long odontologoId);
    List<Cita> findByEstado(Cita.EstadoCita estado);
}
