package com.dental.service;

import com.dental.model.HistorialClinico;
import com.dental.repository.HistorialClinicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

@Service
@RequiredArgsConstructor
public class HistorialClinicoService {

    private final HistorialClinicoRepository historialRepo;

    @Value("${app.upload.dir:uploads/imagenes}")
    private String uploadDir;

    @Transactional
    public HistorialClinico crear(HistorialClinico h)     { return historialRepo.save(h); }

    @Transactional
    public HistorialClinico actualizar(HistorialClinico h) { return historialRepo.save(h); }

    @Transactional
    public HistorialClinico agregarImagenes(Long id, List<MultipartFile> archivos) throws IOException {
        HistorialClinico h = obtenerPorId(id);
        List<String> rutas = h.getRutasImagenes() != null ? new ArrayList<>(h.getRutasImagenes()) : new ArrayList<>();
        Path dir = Paths.get(uploadDir);
        Files.createDirectories(dir);
        for (MultipartFile f : archivos) {
            String nombre = UUID.randomUUID() + "_" + f.getOriginalFilename();
            Files.copy(f.getInputStream(), dir.resolve(nombre), StandardCopyOption.REPLACE_EXISTING);
            rutas.add(nombre);
        }
        h.setRutasImagenes(rutas);
        return historialRepo.save(h);
    }

    public List<HistorialClinico> listarPorPaciente(Long id) { return historialRepo.findByPacienteId(id); }
    public List<HistorialClinico> listarTodos()               { return historialRepo.findAll(); }

    public HistorialClinico obtenerPorId(Long id) {
        return historialRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Historial no encontrado: " + id));
    }
}
