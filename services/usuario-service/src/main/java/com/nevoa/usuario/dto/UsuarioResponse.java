package com.nevoa.usuario.dto;

import java.time.LocalDateTime;

public record UsuarioResponse(
    Long id,
    String nombreCompleto,
    String email,
    String passwordProtegido,
    Boolean activo,
    LocalDateTime fechaRegistro
) {
}
