package com.nevoa.habito.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.    time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "habitos")
public class Habito {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull(message = "El usuario es obligatorio")
    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;
    @NotBlank(message = "El nombre del habito es obligatorio")
    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;
    @Column(name = "racha_actual", nullable = false)
    private Integer rachaActual;
    @Column(name = "dias_completados", nullable = false)
    private Integer diasCompletados;
    @Column(name = "cumplido_hoy", nullable = false)
    private Boolean cumplidoHoy;
    @Column(name = "creado_en", nullable = false)
    private LocalDateTime creadoEn;
    @Column(name = "actualizado_en", nullable = true)
    private LocalDateTime actualizadoEn;
}
