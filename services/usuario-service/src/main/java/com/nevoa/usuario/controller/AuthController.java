package com.nevoa.usuario.controller;

import com.nevoa.usuario.dto.LoginRequest;
import com.nevoa.usuario.dto.LoginResponse;
import com.nevoa.usuario.dto.UsuarioRequest;
import com.nevoa.usuario.dto.UsuarioResponse;
import com.nevoa.usuario.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Autenticacion", description = "Registro e inicio de sesion. El usuario creado se usa como propietario de tareas, proyectos, notas, metas, habitos y pomodoros mediante usuarioId.")
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Registrar usuario", description = "Crea un usuario de Nevoa y devuelve su identificador para asociar datos personales por usuario.")
    @PostMapping("/register")
    public ResponseEntity<UsuarioResponse> registrar(@Valid @RequestBody UsuarioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registrar(request));
    }

    @Operation(summary = "Iniciar sesion", description = "Valida credenciales basicas y devuelve datos del usuario autenticado.")
    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
}
