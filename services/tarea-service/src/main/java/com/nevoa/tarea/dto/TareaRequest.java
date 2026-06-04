package com.nevoa.tarea.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import com.nevoa.tarea.domain.*;

@Schema(description = "Datos para crear o actualizar una tarea")
public record TareaRequest(
    @Schema(description = "ID del usuario propietario de la tarea", example = "1")
    @NotNull(message = "El usuario es obligatorio")
    Long usuarioId,

    @Schema(description = "ID del proyecto asociado. Puede ser null si la tarea no pertenece a un proyecto", example = "1", nullable = true)
    Long proyectoId,

    @Schema(description = "Titulo de la tarea", example = "Terminar documentacion")
    @NotBlank(message = "El titulo es obligatorio")
    String titulo,

    @Schema(description = "Descripcion de la tarea", example = "Completar evidencias de Nevoa")
    String descripcion,

    @Schema(description = "Prioridad de la tarea", example = "MEDIA")
    @NotNull(message = "La prioridad es obligatoria")
    Prioridad prioridad,

    @Schema(description = "Fecha limite de la tarea", example = "2026-04-30")
    LocalDate fechaLimite
) {
}
