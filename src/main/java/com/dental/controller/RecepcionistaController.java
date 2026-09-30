package com.dental.controller;

import com.dental.model.Cita;
import com.dental.repository.OdontologoRepository;
import com.dental.repository.PacienteRepository;
import com.dental.service.CitaService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/recepcionista")
@RequiredArgsConstructor
public class RecepcionistaController {

    private final CitaService citaService;
    private final PacienteRepository pacienteRepo;
    private final OdontologoRepository odontologoRepo;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("citasSolicitadas", citaService.listarSolicitadas().size());
        model.addAttribute("todasCitas",       citaService.listarTodas().size());
        return "recepcionista/dashboard";
    }

    @GetMapping("/citas/solicitadas")
    public String solicitadas(Model model) {
        model.addAttribute("citas", citaService.listarSolicitadas());
        return "recepcionista/citas-solicitadas";
    }

    @GetMapping("/citas")
    public String todas(Model model) {
        model.addAttribute("citas", citaService.listarTodas());
        return "recepcionista/citas";
    }

    @PostMapping("/citas/{id}/aceptar")
    public String aceptar(@PathVariable Long id,
                          @RequestParam(required = false, defaultValue = "Cita confirmada") String observaciones,
                          RedirectAttributes ra) {
        citaService.aceptar(id, observaciones);
        ra.addFlashAttribute("exito", "Cita aceptada.");
        return "redirect:/recepcionista/citas/solicitadas";
    }

    @PostMapping("/citas/{id}/rechazar")
    public String rechazar(@PathVariable Long id,
                           @RequestParam(required = false, defaultValue = "Horario no disponible") String observaciones,
                           RedirectAttributes ra) {
        citaService.rechazar(id, observaciones);
        ra.addFlashAttribute("exito", "Cita rechazada.");
        return "redirect:/recepcionista/citas/solicitadas";
    }

    @PostMapping("/citas/{id}/cancelar")
    public String cancelar(@PathVariable Long id, RedirectAttributes ra) {
        citaService.cancelar(id);
        ra.addFlashAttribute("exito", "Cita cancelada.");
        return "redirect:/recepcionista/citas";
    }

    @GetMapping("/citas/{id}/reprogramar")
    public String mostrarReprogramar(@PathVariable Long id, Model model) {
        model.addAttribute("cita", citaService.obtenerPorId(id));
        return "recepcionista/reprogramar-cita";
    }

    @PostMapping("/citas/{id}/reprogramar")
    public String reprogramar(@PathVariable Long id,
                              @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime nuevaFecha,
                              RedirectAttributes ra) {
        citaService.reprogramar(id, nuevaFecha);
        ra.addFlashAttribute("exito", "Cita reprogramada.");
        return "redirect:/recepcionista/citas";
    }

    @GetMapping("/citas/nueva")
    public String nuevaCita(Model model) {
        model.addAttribute("cita",        new Cita());
        model.addAttribute("pacientes",   pacienteRepo.findAll());
        model.addAttribute("odontologos", odontologoRepo.findAll());
        return "recepcionista/nueva-cita";
    }

    @PostMapping("/citas/nueva")
    public String crearCita(@ModelAttribute Cita cita, RedirectAttributes ra) {
        citaService.solicitar(cita);
        ra.addFlashAttribute("exito", "Cita programada.");
        return "redirect:/recepcionista/citas";
    }

    @GetMapping("/pacientes")
    public String pacientes(Model model) {
        model.addAttribute("pacientes", pacienteRepo.findAll());
        return "recepcionista/pacientes";
    }
}
