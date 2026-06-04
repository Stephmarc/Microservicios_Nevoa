package com.nevoa.usuario.controller;

import com.nevoa.usuario.dto.UsuarioRequest;
import com.nevoa.usuario.dto.UsuarioResponse;
import com.nevoa.usuario.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "Usuarios", description = "Gestion de usuarios base de Nevoa. Cada recurso funcional referencia a un usuario mediante usuarioId.")
@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuarioController {
    private final UsuarioService service;

    @Value("${server.port:0}")
    private String port;

    @Value("${spring.application.name:usuario-service}")
    private String serviceName;

    @Value("${spring.profiles.active:${SPRING_PROFILES_ACTIVE:dev}}")
    private String profile;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    @Operation(summary = "Verificar instancia de usuario-service", description = "Devuelve estado, host, puerto y perfil activo del microservicio.")
    @GetMapping("/instancia")
    public Map<String, Object> instancia() throws UnknownHostException {
        Map<String, Object> response = new HashMap<>();
        response.put("servicio", serviceName);
        response.put("status", "UP");
        response.put("puerto", port);
        response.put("perfil", profile);
        response.put("host", InetAddress.getLocalHost().getHostName());
        return response;
    }

    @Operation(summary = "Listar usuarios", description = "Lista usuarios registrados. Uso de revision tecnica o administracion.")
    @GetMapping
    public List<UsuarioResponse> listar() {
        return service.listar();
    }

    @Operation(summary = "Obtener usuario por ID", description = "Consulta datos basicos de un usuario por su identificador.")
    @GetMapping("/{id}")
    public UsuarioResponse obtener(@Parameter(description = "ID del usuario") @PathVariable Long id) {
        return service.obtener(id);
    }

    @Operation(summary = "Crear usuario", description = "Crea un usuario desde el modulo de usuarios. Para flujo publico se recomienda /api/v1/auth/register.")
    @PostMapping
    public ResponseEntity<UsuarioResponse> crear(@Valid @RequestBody UsuarioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(request));
    }

    @Operation(summary = "Actualizar usuario", description = "Actualiza datos basicos del usuario indicado.")
    @PutMapping("/{id}")
    public UsuarioResponse actualizar(@PathVariable Long id, @Valid @RequestBody UsuarioRequest request) {
        return service.actualizar(id, request);
    }

    @Operation(summary = "Eliminar usuario", description = "Elimina un usuario por ID.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
