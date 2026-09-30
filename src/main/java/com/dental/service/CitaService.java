package com.dental.service;

import com.dental.model.Cita;
import com.dental.repository.CitaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CitaService {

    private final CitaRepository citaRepository;

    @Transactional
    public Cita solicitar(Cita cita) {
        cita.setEstado(Cita.EstadoCita.SOLICITADA);
        return citaRepository.save(cita);
    }

    @Transactional
    public Cita aceptar(Long id, String obs) {
        Cita c = obtenerPorId(id);
        c.setEstado(Cita.EstadoCita.ACEPTADA);
        c.setObservacionesRecepcion(obs);
        return citaRepository.save(c);
    }

    @Transactional
    public Cita rechazar(Long id, String obs) {
        Cita c = obtenerPorId(id);
        c.setEstado(Cita.EstadoCita.RECHAZADA);
        c.setObservacionesRecepcion(obs);
        return citaRepository.save(c);
    }

    @Transactional
    public Cita cancelar(Long id) {
        Cita c = obtenerPorId(id);
        c.setEstado(Cita.EstadoCita.CANCELADA);
        return citaRepository.save(c);
    }

    @Transactional
    public Cita reprogramar(Long id, LocalDateTime nuevaFecha) {
        Cita c = obtenerPorId(id);
        c.setFechaHora(nuevaFecha);
        c.setEstado(Cita.EstadoCita.REPROGRAMADA);
        return citaRepository.save(c);
    }

    public List<Cita> listarPorPaciente(Long pacienteId)   { return citaRepository.findByPacienteId(pacienteId); }
    public List<Cita> listarPorOdontologo(Long odId)       { return citaRepository.findByOdontologoId(odId); }
    public List<Cita> listarSolicitadas()                  { return citaRepository.findByEstado(Cita.EstadoCita.SOLICITADA); }
    public List<Cita> listarTodas()                        { return citaRepository.findAll(); }

    public Cita obtenerPorId(Long id) {
        return citaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada: " + id));
    }
}
