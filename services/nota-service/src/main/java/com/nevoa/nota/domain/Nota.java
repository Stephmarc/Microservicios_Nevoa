package com.nevoa.nota.domain;

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
@Table(name = "notas")
public class Nota {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull(message = "El usuario es obligatorio")
    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;
    @NotBlank(message = "El titulo es obligatorio")
    @Column(name = "titulo", nullable = false, length = 150)
    private String titulo;
    @Column(name = "categoria", nullable = true, length = 80)
    private String categoria;
    @NotBlank(message = "El contenido es obligatorio")
    @Column(name = "contenido", nullable = false, columnDefinition = "TEXT")
    private String contenido;
    @Column(name = "creada_en", nullable = false)
    private LocalDateTime creadaEn;
    @Column(name = "actualizada_en", nullable = true)
    private LocalDateTime actualizadaEn;
}
