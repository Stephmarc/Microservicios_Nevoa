package com.nevoa.proyecto.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "Datos para crear o actualizar un proyecto")
public record ProyectoRequest(
    @Schema(description = "ID del usuario propietario del proyecto", example = "1")
    @NotNull(message = "El usuario es obligatorio")
    Long usuarioId,

    @Schema(description = "Nombre del proyecto", example = "Proyecto Nevoa")
    @NotBlank(message = "El nombre del proyecto es obligatorio")
    String nombre,

    @Schema(description = "Descripcion del proyecto", example = "Sistema distribuido de productividad personal")
    String descripcion
) {
}
