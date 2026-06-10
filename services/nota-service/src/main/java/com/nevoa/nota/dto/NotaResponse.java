package com.nevoa.nota.dto;

import java.time.LocalDateTime;

public record NotaResponse(
    Long id,
    Long usuarioId,
    String titulo,
    String categoria,
    String contenido,
    LocalDateTime creadaEn,
    LocalDateTime actualizadaEn
) {
}
