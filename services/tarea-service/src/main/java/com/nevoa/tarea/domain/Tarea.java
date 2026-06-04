package com.nevoa.tarea.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tareas")
public class Tarea {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull(message = "El usuario es obligatorio")
    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;
    @Column(name = "proyecto_id", nullable = true)
    private Long proyectoId;
    @NotBlank(message = "El titulo es obligatorio")
    @Column(name = "titulo", nullable = false, length = 150)
    private String titulo;
    @Column(name = "descripcion", nullable = true, length = 500)
    private String descripcion;
    @NotNull(message = "La prioridad es obligatoria")
    @Enumerated(EnumType.STRING)
    @Column(name = "prioridad", nullable = false, length = 20)
    private Prioridad prioridad;
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoTarea estado;
    @Column(name = "fecha_limite", nullable = true)
    private LocalDate fechaLimite;
    @Column(name = "creada_en", nullable = false)
    private LocalDateTime creadaEn;
    @Column(name = "actualizada_en", nullable = true)
    private LocalDateTime actualizadaEn;
}
