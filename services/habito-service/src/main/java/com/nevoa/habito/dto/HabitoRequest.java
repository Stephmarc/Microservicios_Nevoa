package com.nevoa.habito.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "Datos para crear o actualizar un habito")
public record HabitoRequest(
    @Schema(description = "ID del usuario propietario del habito", example = "1")
    @NotNull(message = "El usuario es obligatorio")
    Long usuarioId,

    @Schema(description = "Nombre del habito", example = "Leer 30 minutos")
    @NotBlank(message = "El nombre del habito es obligatorio")
    String nombre
) {
}
