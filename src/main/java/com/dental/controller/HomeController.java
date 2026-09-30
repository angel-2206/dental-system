package com.dental.controller;

import com.dental.model.Paciente;
import com.dental.model.Usuario;
import com.dental.repository.PacienteRepository;
import com.dental.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final UsuarioService usuarioService;
    private final PacienteRepository pacienteRepository;

    @GetMapping({"/", "/index"})
    public String index() {
        return "index";
    }

    @GetMapping("/login")
    public String login(@RequestParam(required = false) String error,
                        @RequestParam(required = false) String logout,
                        Model model) {
        if (error  != null) model.addAttribute("error",  "Usuario/contraseña incorrectos o cuenta pendiente de aprobación.");
        if (logout != null) model.addAttribute("logout", "Sesión cerrada correctamente.");
        return "login";
    }

    @GetMapping("/registro")
    public String mostrarRegistro() {
        return "registro";
    }

    /**
     * ✅ CORREGIDO:
     * Recibe todos los campos del formulario (usuario + datos del paciente)
     * como @RequestParam individuales para evitar problemas de binding con Thymeleaf.
     * Guarda el Usuario y crea el perfil Paciente con todos los datos en un solo paso.
     */
    @PostMapping("/registro")
    public String procesarRegistro(
            // Datos de acceso
            @RequestParam String nombreCompleto,
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String password,
            // Datos del paciente (opcionales)
            @RequestParam(required = false) String dni,
            @RequestParam(required = false) String telefono,
            @RequestParam(required = false) String direccion,
            @RequestParam(required = false) String tipoSangre,
            @RequestParam(required = false) String alergias,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaNacimiento,
            Model model) {

        try {
            // 1. Construir y guardar el Usuario
            Usuario usuario = Usuario.builder()
                    .nombreCompleto(nombreCompleto)
                    .username(username)
                    .email(email)
                    .password(password)
                    .rol(Usuario.Rol.PACIENTE)
                    .build();

            Usuario guardado = usuarioService.registrar(usuario);

            // 2. Crear perfil Paciente con todos los datos del formulario
            Paciente perfil = Paciente.builder()
                    .usuario(guardado)
                    .dni(dni)
                    .telefono(telefono)
                    .direccion(direccion)
                    .tipoSangre(tipoSangre)
                    .alergias(alergias)
                    .fechaNacimiento(fechaNacimiento)
                    .esNuevo(true)
                    .build();

            pacienteRepository.save(perfil);

            model.addAttribute("exito",
                    "¡Registro exitoso! Tu cuenta está pendiente de aprobación por el administrador.");

        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
        }

        return "registro";
    }

    @GetMapping("/acceso-denegado")
    public String accesoDenegado() {
        return "acceso-denegado";
    }
}
