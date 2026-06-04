package com.nevoa.usuario.dto;

public record LoginResponse(
    Long usuarioId,
    String nombreCompleto,
    String email,
    String mensaje
) {
}
