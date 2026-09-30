package com.dental.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "citas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "odontologo_id", nullable = false)
    private Odontologo odontologo;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @Column(length = 300)
    private String motivo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private EstadoCita estado = EstadoCita.SOLICITADA;

    @Column(name = "observaciones_recepcion", length = 500)
    private String observacionesRecepcion;

    @Column(name = "fecha_solicitud")
    @Builder.Default
    private LocalDateTime fechaSolicitud = LocalDateTime.now();

    public enum EstadoCita {
        SOLICITADA, ACEPTADA, RECHAZADA, REPROGRAMADA, CANCELADA, COMPLETADA
    }
}
