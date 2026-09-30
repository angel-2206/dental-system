package com.dental.controller;

import com.dental.model.Cita;
import com.dental.model.Odontologo;
import com.dental.model.Paciente;
import com.dental.repository.OdontologoRepository;
import com.dental.repository.PacienteRepository;
import com.dental.service.CitaService;
import com.dental.service.HistorialClinicoService;
import com.dental.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/paciente")
@RequiredArgsConstructor
public class PacienteController {

    private final CitaService citaService;
    private final HistorialClinicoService historialService;
    private final PacienteRepository pacienteRepo;
    private final OdontologoRepository odontologoRepo;
    private final UsuarioService usuarioService;

    // ── Helper: obtener paciente del usuario logueado ────────────────────────
    private Paciente getPacienteActual(UserDetails u) {
        var usuario = usuarioService.buscarPorUsername(u.getUsername());
        return pacienteRepo.findByUsuarioId(usuario.getId())
                .orElseThrow(() -> new RuntimeException("Perfil de paciente no encontrado."));
    }

    // ── Dashboard ────────────────────────────────────────────────────────────
    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails u, Model model) {
        Paciente p = getPacienteActual(u);
        model.addAttribute("paciente", p);
        model.addAttribute("misCitas", citaService.listarPorPaciente(p.getId()));
        return "paciente/dashboard";
    }

    // ── Mostrar formulario de solicitud ──────────────────────────────────────
    @GetMapping("/citas/solicitar")
    public String mostrarSolicitud(Model model) {
        model.addAttribute("odontologos", odontologoRepo.findAll());
        return "paciente/solicitar-cita";
    }

    /**
     * ✅ CORREGIDO:
     *  - Recibe odontologoId y fechaHora y motivo como parámetros separados.
     *  - Busca el Odontologo REAL desde la base de datos usando su ID.
     *  - Construye la Cita manualmente antes de guardar.
     *  - Así Hibernate nunca recibe un objeto transient (sin persistir).
     */
    @PostMapping("/citas/solicitar")
    public String solicitar(
            @AuthenticationPrincipal UserDetails u,
            @RequestParam("odontologo.id") Long odontologoId,
            @RequestParam("fechaHora") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaHora,
            @RequestParam("motivo") String motivo,
            RedirectAttributes ra) {

        try {
            // Obtener entidades REALES desde la BD
            Paciente paciente = getPacienteActual(u);
            Odontologo odontologo = odontologoRepo.findById(odontologoId)
                    .orElseThrow(() -> new RuntimeException("Odontólogo no encontrado."));

            // Construir la cita con objetos persistidos
            Cita cita = Cita.builder()
                    .paciente(paciente)
                    .odontologo(odontologo)
                    .fechaHora(fechaHora)
                    .motivo(motivo)
                    .estado(Cita.EstadoCita.SOLICITADA)
                    .build();

            citaService.solicitar(cita);
            ra.addFlashAttribute("exito", "✅ Solicitud enviada correctamente. Espera confirmación de la recepcionista.");

        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al solicitar cita: " + e.getMessage());
        }

        return "redirect:/paciente/citas";
    }

    // ── Mis citas ────────────────────────────────────────────────────────────
    @GetMapping("/citas")
    public String misCitas(@AuthenticationPrincipal UserDetails u, Model model) {
        Paciente p = getPacienteActual(u);
        model.addAttribute("citas", citaService.listarPorPaciente(p.getId()));
        return "paciente/mis-citas";
    }

    // ── Cancelar MI cita ─────────────────────────────────────────────────────
    @PostMapping("/citas/{id}/cancelar")
    public String cancelar(@AuthenticationPrincipal UserDetails u,
                           @PathVariable Long id,
                           RedirectAttributes ra) {
        try {
            Cita cita = citaService.obtenerPorId(id);
            // Verificar que la cita pertenece al paciente actual
            if (!cita.getPaciente().getId().equals(getPacienteActual(u).getId())) {
                ra.addFlashAttribute("error", "No puedes cancelar una cita de otro paciente.");
                return "redirect:/paciente/citas";
            }
            citaService.cancelar(id);
            ra.addFlashAttribute("exito", "Cita cancelada correctamente.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al cancelar: " + e.getMessage());
        }
        return "redirect:/paciente/citas";
    }

    // ── Mi historial clínico (solo lectura) ──────────────────────────────────
    @GetMapping("/historial")
    public String historial(@AuthenticationPrincipal UserDetails u, Model model) {
        Paciente p = getPacienteActual(u);
        model.addAttribute("historiales", historialService.listarPorPaciente(p.getId()));
        return "paciente/historial";
    }
}