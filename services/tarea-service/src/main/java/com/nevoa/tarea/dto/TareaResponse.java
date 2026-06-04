package com.nevoa.tarea.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import com.nevoa.tarea.domain.*;

public record TareaResponse(
    Long id,
    Long usuarioId,
    Long proyectoId,
    String titulo,
    String descripcion,
    Prioridad prioridad,
    EstadoTarea estado,
    LocalDate fechaLimite,
    LocalDateTime creadaEn,
    LocalDateTime actualizadaEn
) {
}
