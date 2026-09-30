package com.dental.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "odontologos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Odontologo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario;

    @Column(length = 100)
    private String especialidad;

    @Column(name = "num_colegiatura", unique = true, length = 30)
    private String numColegiatura;

    @Column(length = 20)
    private String telefono;

    @Column(name = "horario_disponible", length = 500)
    private String horarioDisponible;

    @OneToMany(mappedBy = "odontologo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Cita> citas;
}
