package com.dental.controller;

import com.dental.model.Odontologo;
import com.dental.model.Paciente;
import com.dental.model.Usuario;
import com.dental.repository.OdontologoRepository;
import com.dental.repository.PacienteRepository;
import com.dental.service.CitaService;
import com.dental.service.HistorialClinicoService;
import com.dental.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UsuarioService usuarioService;
    private final CitaService citaService;
    private final HistorialClinicoService historialService;
    private final OdontologoRepository odontologoRepo;
    private final PacienteRepository pacienteRepo;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalUsuarios",    usuarioService.listarTodos().size());
        model.addAttribute("pendientes",       usuarioService.listarPendientes().size());
        model.addAttribute("totalCitas",       citaService.listarTodas().size());
        model.addAttribute("citasSolicitadas", citaService.listarSolicitadas().size());
        model.addAttribute("totalHistoriales", historialService.listarTodos().size());
        return "admin/dashboard";
    }

    @GetMapping("/usuarios/pendientes")
    public String listarPendientes(Model model) {
        model.addAttribute("usuarios", usuarioService.listarPendientes());
        model.addAttribute("roles", Usuario.Rol.values());
        return "admin/usuarios-pendientes";
    }

    @GetMapping("/usuarios")
    public String listarTodos(Model model) {
        model.addAttribute("usuarios", usuarioService.listarTodos());
        model.addAttribute("roles", Usuario.Rol.values());
        return "admin/usuarios";
    }

    /**
     * ✅ CORREGIDO:
     * Al aprobar un usuario, además de activar su cuenta y asignar el rol,
     * se crea automáticamente el perfil correspondiente en la tabla correcta:
     *  - ODONTOLOGO  → crea registro en tabla 'odontologos'
     *  - PACIENTE    → crea registro en tabla 'pacientes' (si no existe ya)
     *  - RECEPCIONISTA / ADMIN → no necesitan tabla de perfil extra
     */
    @PostMapping("/usuarios/{id}/aprobar")
    public String aprobar(@PathVariable Long id,
                          @RequestParam Usuario.Rol rol,
                          RedirectAttributes ra) {

        // 1. Activar usuario y asignar rol
        Usuario usuario = usuarioService.aprobarYAsignarRol(id, rol);

        // 2. Crear perfil según el rol asignado
        switch (rol) {
            case ODONTOLOGO -> {
                // Solo crear si aún no tiene perfil
                if (odontologoRepo.findByUsuarioId(usuario.getId()).isEmpty()) {
                    Odontologo odontologo = Odontologo.builder()
                            .usuario(usuario)
                            .especialidad("Odontología General")
                            .build();
                    odontologoRepo.save(odontologo);
                }
            }
            case PACIENTE -> {
                // Puede que ya exista desde el registro, si no lo crea
                if (pacienteRepo.findByUsuarioId(usuario.getId()).isEmpty()) {
                    Paciente paciente = Paciente.builder()
                            .usuario(usuario)
                            .esNuevo(true)
                            .build();
                    pacienteRepo.save(paciente);
                }
            }
            // RECEPCIONISTA y ADMIN no necesitan tabla de perfil adicional
            default -> { }
        }

        ra.addFlashAttribute("exito",
                "Usuario aprobado con rol: " + rol.name() + " y perfil creado correctamente.");
        return "redirect:/admin/usuarios/pendientes";
    }

    @PostMapping("/usuarios/{id}/desactivar")
    public String desactivar(@PathVariable Long id, RedirectAttributes ra) {
        usuarioService.desactivar(id);
        ra.addFlashAttribute("exito", "Usuario desactivado.");
        return "redirect:/admin/usuarios";
    }
}
