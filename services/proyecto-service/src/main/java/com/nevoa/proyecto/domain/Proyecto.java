package com.nevoa.proyecto.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "proyectos")
public class Proyecto {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull(message = "El usuario es obligatorio")
    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;
    @NotBlank(message = "El nombre del proyecto es obligatorio")
    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;
    @Column(name = "descripcion", nullable = true, length = 600)
    private String descripcion;
    @Column(name = "progreso", nullable = false)
    private Integer progreso;
    @Column(name = "total_tareas", nullable = false)
    private Integer totalTareas;
    @Column(name = "tareas_completadas", nullable = false)
    private Integer tareasCompletadas;
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 30)
    private EstadoProyecto estado;
    @Column(name = "creado_en", nullable = false)
    private LocalDateTime creadoEn;
    @Column(name = "actualizado_en", nullable = true)
    private LocalDateTime actualizadoEn;
}
