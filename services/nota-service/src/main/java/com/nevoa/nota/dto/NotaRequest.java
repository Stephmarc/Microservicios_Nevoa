package com.nevoa.nota.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "Datos para crear o actualizar una nota")
public record NotaRequest(
    @Schema(description = "ID del usuario propietario de la nota", example = "1")
    @NotNull(message = "El usuario es obligatorio")
    Long usuarioId,

    @Schema(description = "Titulo de la nota", example = "Ideas para exposicion")
    @NotBlank(message = "El titulo es obligatorio")
    String titulo,

    @Schema(description = "Categoria de la nota", example = "Proyecto")
    String categoria,

    @Schema(description = "Contenido de la nota", example = "Explicar Eureka, Gateway, Config Server y Docker")
    @NotBlank(message = "El contenido es obligatorio")
    String contenido
) {
}
