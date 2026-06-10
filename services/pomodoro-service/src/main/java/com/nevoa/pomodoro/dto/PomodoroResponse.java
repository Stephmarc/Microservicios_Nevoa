package com.nevoa.pomodoro.dto;

import java.time.LocalDateTime;
import com.nevoa.pomodoro.domain.*;

public record PomodoroResponse(
    Long id,
    Long usuarioId,
    Integer minutosTrabajo,
    Integer minutosDescanso,
    EstadoPomodoro estado,
    LocalDateTime inicio,
    LocalDateTime fin,
    LocalDateTime creadoEn
) {
}
