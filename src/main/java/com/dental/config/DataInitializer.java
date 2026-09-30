package com.dental.config;

import com.dental.model.Usuario;
import com.dental.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * ✅ CORREGIDO: Usa ApplicationRunner en lugar de CommandLineRunner
 *    para garantizar que JPA haya creado las tablas antes de insertar.
 *    Además @Transactional asegura el commit correcto en MySQL.
 *
 *    Credenciales por defecto:
 *    Usuario : admin
 *    Password: admin123
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements ApplicationRunner {

    private final UsuarioRepository usuarioRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        try {
            if (!usuarioRepo.existsByUsername("admin")) {
                Usuario admin = Usuario.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("admin123"))
                        .email("admin@dentalcare.com")
                        .nombreCompleto("Administrador del Sistema")
                        .rol(Usuario.Rol.ADMIN)
                        .estado(Usuario.EstadoCuenta.ACTIVO)
                        .build();
                usuarioRepo.save(admin);
                log.info("========================================================");
                log.info("  ADMIN creado  →  usuario: admin | password: admin123  ");
                log.info("  ¡Cambia la contraseña después del primer inicio!       ");
                log.info("========================================================");
            } else {
                log.info("Admin ya existe — no se crea de nuevo.");
            }
        } catch (Exception e) {
            log.error("Error al crear admin inicial: {}", e.getMessage());
        }
    }
}
