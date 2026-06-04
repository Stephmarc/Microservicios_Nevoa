package com.nevoa.usuario.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "Datos para registrar o actualizar un usuario de Nevoa")
public record UsuarioRequest(
    @Schema(description = "Nombre completo del usuario", example = "Jhoel Midwar Coila Mamani")
    @NotBlank(message = "El nombre completo es obligatorio")
    String nombreCompleto,

    @Schema(description = "Correo electronico unico del usuario", example = "midwar@nevoa.com")
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email no tiene un formato valido")
    String email,

    @Schema(description = "Contrasena del usuario", example = "123456")
    @NotBlank(message = "La contraseña es obligatoria")
    String password
) {
}
