package com.dental.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "historiales_clinicos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistorialClinico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "odontologo_id", nullable = false)
    private Odontologo odontologo;

    @Column(name = "fecha_atencion", nullable = false)
    @Builder.Default
    private LocalDateTime fechaAtencion = LocalDateTime.now();

    @Column(length = 1000)
    private String diagnostico;

    @Column(length = 1000)
    private String tratamiento;

    @Column(length = 1000)
    private String observaciones;

    @Column(name = "proximo_control")
    private LocalDateTime proximoControl;

    @ElementCollection
    @CollectionTable(name = "historial_imagenes",
                     joinColumns = @JoinColumn(name = "historial_id"))
    @Column(name = "ruta_imagen", length = 300)
    private List<String> rutasImagenes;
}
