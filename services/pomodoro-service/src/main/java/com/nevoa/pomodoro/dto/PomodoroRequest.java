package com.nevoa.pomodoro.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "Datos para crear o actualizar una sesion Pomodoro")
public record PomodoroRequest(
    @Schema(description = "ID del usuario propietario de la sesion", example = "1")
    @NotNull(message = "El usuario es obligatorio")
    Long usuarioId,

    @Schema(description = "Minutos de trabajo", example = "25")
    @Min(value = 1, message = "Los minutos de trabajo deben ser mayores a cero")
    Integer minutosTrabajo,

    @Schema(description = "Minutos de descanso", example = "5")
    @Min(value = 1, message = "Los minutos de descanso deben ser mayores a cero")
    Integer minutosDescanso
) {
}
