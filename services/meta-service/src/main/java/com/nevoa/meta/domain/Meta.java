package com.nevoa.meta.domain;

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
@Table(name = "metas")
public class Meta {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull(message = "El usuario es obligatorio")
    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;
    @NotBlank(message = "El titulo de la meta es obligatorio")
    @Column(name = "titulo", nullable = false, length = 150)
    private String titulo;
    @Column(name = "descripcion", nullable = true, length = 600)
    private String descripcion;
    @NotNull(message = "La categoria es obligatoria")
    @Enumerated(EnumType.STRING)
    @Column(name = "categoria", nullable = false, length = 30)
    private CategoriaMeta categoria;
    @Column(name = "progreso", nullable = false)
    private Integer progreso;
    @Column(name = "fecha_limite", nullable = true)
    private LocalDate fechaLimite;
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 30)
    private EstadoMeta estado;
    @Column(name = "creada_en", nullable = false)
    private LocalDateTime creadaEn;
    @Column(name = "actualizada_en", nullable = true)
    private LocalDateTime actualizadaEn;
}
