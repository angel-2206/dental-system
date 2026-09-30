package com.dental.service;

import com.dental.model.Usuario;
import com.dental.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Usuario registrar(Usuario usuario) {
        if (usuarioRepository.existsByUsername(usuario.getUsername())) {
            throw new RuntimeException("El nombre de usuario ya está en uso.");
        }
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new RuntimeException("El correo electrónico ya está registrado.");
        }
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuario.setEstado(Usuario.EstadoCuenta.PENDIENTE);
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario aprobarYAsignarRol(Long usuarioId, Usuario.Rol rol) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.setRol(rol);
        usuario.setEstado(Usuario.EstadoCuenta.ACTIVO);
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario desactivar(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.setEstado(Usuario.EstadoCuenta.INACTIVO);
        return usuarioRepository.save(usuario);
    }

    public List<Usuario> listarPendientes() {
        return usuarioRepository.findByEstado(Usuario.EstadoCuenta.PENDIENTE);
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Usuario buscarPorUsername(String username) {
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));
    }
}
