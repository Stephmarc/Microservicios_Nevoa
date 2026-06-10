package com.nevoa.meta.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import com.nevoa.meta.domain.*;

public record MetaResponse(
    Long id,
    Long usuarioId,
    String titulo,
    String descripcion,
    CategoriaMeta categoria,
    Integer progreso,
    LocalDate fechaLimite,
    EstadoMeta estado,
    LocalDateTime creadaEn,
    LocalDateTime actualizadaEn
) {
}
