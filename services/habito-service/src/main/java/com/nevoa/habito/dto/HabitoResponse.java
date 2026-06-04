package com.nevoa.habito.dto;

import java.time.LocalDateTime;

public record HabitoResponse(
    Long id,
    Long usuarioId,
    String nombre,
    Integer rachaActual,
    Integer diasCompletados,
    Boolean cumplidoHoy,
    LocalDateTime creadoEn,
    LocalDateTime actualizadoEn
) {
}
