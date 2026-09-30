package com.dental.controller;

import com.dental.model.HistorialClinico;
import com.dental.model.Odontologo;
import com.dental.repository.OdontologoRepository;
import com.dental.repository.PacienteRepository;
import com.dental.service.CitaService;
import com.dental.service.HistorialClinicoService;
import com.dental.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/odontologo")
@RequiredArgsConstructor
public class OdontologoController {

    private final HistorialClinicoService historialService;
    private final CitaService citaService;
    private final OdontologoRepository odontologoRepo;
    private final PacienteRepository pacienteRepo;
    private final UsuarioService usuarioService;

    private Odontologo getOdontologoActual(UserDetails u) {
        var usuario = usuarioService.buscarPorUsername(u.getUsername());
        return odontologoRepo.findByUsuarioId(usuario.getId())
                .orElseThrow(() -> new RuntimeException("Perfil de odontólogo no encontrado."));
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails u, Model model) {
        Odontologo o = getOdontologoActual(u);
        model.addAttribute("odontologo",       o);
        model.addAttribute("misCitas",         citaService.listarPorOdontologo(o.getId()));
        model.addAttribute("totalHistoriales", historialService.listarTodos().size());
        return "odontologo/dashboard";
    }

    @GetMapping("/historiales")
    public String historiales(Model model) {
        model.addAttribute("historiales", historialService.listarTodos());
        return "odontologo/historiales";
    }

    @GetMapping("/historiales/{id}")
    public String verHistorial(@PathVariable Long id, Model model) {
        model.addAttribute("historial", historialService.obtenerPorId(id));
        return "odontologo/ver-historial";
    }

    @GetMapping("/historiales/nuevo")
    public String nuevoForm(@AuthenticationPrincipal UserDetails u, Model model) {
        model.addAttribute("historial",  new HistorialClinico());
        model.addAttribute("pacientes",  pacienteRepo.findAll());
        model.addAttribute("odontologo", getOdontologoActual(u));
        return "odontologo/nuevo-historial";
    }

    @PostMapping("/historiales/nuevo")
    public String crearHistorial(@AuthenticationPrincipal UserDetails u,
                                 @ModelAttribute HistorialClinico h,
                                 RedirectAttributes ra) {
        h.setOdontologo(getOdontologoActual(u));
        historialService.crear(h);
        ra.addFlashAttribute("exito", "Historial clínico creado.");
        return "redirect:/odontologo/historiales";
    }

    @GetMapping("/historiales/{id}/editar")
    public String editarForm(@PathVariable Long id, Model model) {
        model.addAttribute("historial", historialService.obtenerPorId(id));
        model.addAttribute("pacientes", pacienteRepo.findAll());
        return "odontologo/editar-historial";
    }

    @PostMapping("/historiales/{id}/editar")
    public String actualizar(@PathVariable Long id,
                             @ModelAttribute HistorialClinico h,
                             RedirectAttributes ra) {
        h.setId(id);
        historialService.actualizar(h);
        ra.addFlashAttribute("exito", "Historial actualizado.");
        return "redirect:/odontologo/historiales/" + id;
    }

    @PostMapping("/historiales/{id}/imagenes")
    public String subirImagenes(@PathVariable Long id,
                                @RequestParam("archivos") List<MultipartFile> archivos,
                                RedirectAttributes ra) throws IOException {
        historialService.agregarImagenes(id, archivos);
        ra.addFlashAttribute("exito", "Imágenes subidas.");
        return "redirect:/odontologo/historiales/" + id;
    }

    @GetMapping("/horario")
    public String horarioForm(@AuthenticationPrincipal UserDetails u, Model model) {
        model.addAttribute("odontologo", getOdontologoActual(u));
        return "odontologo/horario";
    }

    @PostMapping("/horario")
    public String guardarHorario(@AuthenticationPrincipal UserDetails u,
                                 @RequestParam String horarioDisponible,
                                 RedirectAttributes ra) {
        Odontologo o = getOdontologoActual(u);
        o.setHorarioDisponible(horarioDisponible);
        odontologoRepo.save(o);
        ra.addFlashAttribute("exito", "Horario actualizado.");
        return "redirect:/odontologo/dashboard";
    }
}
