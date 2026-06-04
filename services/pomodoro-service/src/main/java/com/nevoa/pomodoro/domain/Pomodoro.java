package com.nevoa.pomodoro.domain;

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
@Table(name = "pomodoros")
public class Pomodoro {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull(message = "El usuario es obligatorio")
    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;
    @Column(name = "minutos_trabajo", nullable = false)
    private Integer minutosTrabajo;
    @Column(name = "minutos_descanso", nullable = false)
    private Integer minutosDescanso;
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 30)
    private EstadoPomodoro estado;
    @Column(name = "inicio", nullable = true)
    private LocalDateTime inicio;
    @Column(name = "fin", nullable = true)
    private LocalDateTime fin;
    @Column(name = "creado_en", nullable = false)
    private LocalDateTime creadoEn;
}
