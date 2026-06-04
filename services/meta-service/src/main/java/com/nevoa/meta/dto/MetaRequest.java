package com.nevoa.meta.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import com.nevoa.meta.domain.*;

@Schema(description = "Datos para crear o actualizar una meta")
public record MetaRequest(
    @Schema(description = "ID del usuario propietario de la meta", example = "1")
    @NotNull(message = "El usuario es obligatorio")
    Long usuarioId,

    @Schema(description = "Titulo de la meta", example = "Aprender microservicios")
    @NotBlank(message = "El titulo de la meta es obligatorio")
    String titulo,

    @Schema(description = "Descripcion de la meta", example = "Comprender Spring Cloud, Eureka, Gateway y Docker")
    String descripcion,

    @Schema(description = "Categoria de la meta", example = "APRENDIZAJE")
    @NotNull(message = "La categoria es obligatoria")
    CategoriaMeta categoria,

    @Schema(description = "Fecha limite de la meta", example = "2026-05-30")
    LocalDate fechaLimite
) {
}
