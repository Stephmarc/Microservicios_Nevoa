package com.nevoa.proyecto.dto;

import java.time.LocalDateTime;
import com.nevoa.proyecto.domain.*;

public record ProyectoResponse(
    Long id,
    Long usuarioId,
    String nombre,
    String descripcion,
    Integer progreso,
    Integer totalTareas,
    Integer tareasCompletadas,
    EstadoProyecto estado,
    LocalDateTime creadoEn,
    LocalDateTime actualizadoEn
) {
}
