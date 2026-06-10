package com.nevoa.usuario.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Credenciales para iniciar sesion")
public record LoginRequest(
    @Schema(description = "Correo electronico registrado", example = "midwar@nevoa.com")
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email no tiene un formato valido")
    String email,

    @Schema(description = "Contrasena del usuario", example = "123456")
    @NotBlank(message = "La contraseña es obligatoria")
    String password
) {
}
