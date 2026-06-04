package com.nevoa.habito.controller;

import com.nevoa.habito.dto.HabitoRequest;
import com.nevoa.habito.dto.HabitoResponse;
import com.nevoa.habito.service.HabitoService;
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

@Tag(name = "Habitos", description = "Operaciones de habitos separados por usuario.")
@RestController
@RequestMapping("/api/v1/habitos")
public class HabitoController {
    private final HabitoService service;

    @Value("${server.port:0}")
    private String port;

    @Value("${spring.application.name:habito-service}")
    private String serviceName;

    @Value("${spring.profiles.active:${SPRING_PROFILES_ACTIVE:dev}}")
    private String profile;

    public HabitoController(HabitoService service) {
        this.service = service;
    }

    @Operation(summary = "Verificar instancia de habito-service", description = "Devuelve estado, host, puerto y perfil activo del microservicio.")
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

    @Operation(summary = "Listar habitos", description = "Endpoint administrativo usado para revision tecnica. Para uso funcional debe preferirse /usuario/{usuarioId}.")
    @GetMapping
    public List<HabitoResponse> listar() {
        return service.listar();
    }

    @Operation(summary = "Listar habitos de un usuario", description = "Devuelve solo los registros pertenecientes al usuario indicado.")
    @GetMapping("/usuario/{usuarioId}")
    public List<HabitoResponse> listarPorUsuario(@Parameter(description = "ID del usuario propietario") @PathVariable Long usuarioId) {
        return service.listarPorUsuario(usuarioId);
    }

    @Operation(summary = "Obtener registro por usuario e ID", description = "Valida que el registro pertenezca al usuario indicado antes de devolverlo.")
    @GetMapping("/usuario/{usuarioId}/{id}")
    public HabitoResponse obtenerPorUsuario(@PathVariable Long usuarioId, @PathVariable Long id) {
        return service.obtenerPorUsuario(usuarioId, id);
    }

    @Operation(summary = "Obtener registro por ID", description = "Endpoint administrativo. No valida usuario propietario.")
    @GetMapping("/{id}")
    public HabitoResponse obtener(@PathVariable Long id) {
        return service.obtener(id);
    }

    @Operation(summary = "Crear registro", description = "Crea un registro usando el usuarioId enviado en el cuerpo.")
    @PostMapping
    public ResponseEntity<HabitoResponse> crear(@Valid @RequestBody HabitoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(request));
    }

    @Operation(summary = "Crear registro para un usuario", description = "Crea un registro asociado al usuario indicado en la ruta. Ruta recomendada para mantener datos separados por usuario.")
    @PostMapping("/usuario/{usuarioId}")
    public ResponseEntity<HabitoResponse> crearParaUsuario(@PathVariable Long usuarioId, @Valid @RequestBody HabitoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crearParaUsuario(usuarioId, request));
    }

    @Operation(summary = "Actualizar registro", description = "Actualiza un registro por ID. Uso administrativo.")
    @PutMapping("/{id}")
    public HabitoResponse actualizar(@PathVariable Long id, @Valid @RequestBody HabitoRequest request) {
        return service.actualizar(id, request);
    }

    @Operation(summary = "Actualizar registro de un usuario", description = "Actualiza un registro solo si pertenece al usuario indicado.")
    @PutMapping("/usuario/{usuarioId}/{id}")
    public HabitoResponse actualizarParaUsuario(@PathVariable Long usuarioId, @PathVariable Long id, @Valid @RequestBody HabitoRequest request) {
        return service.actualizarParaUsuario(usuarioId, id, request);
    }

    @Operation(summary = "Eliminar registro", description = "Elimina un registro por ID. Uso administrativo.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Eliminar registro de un usuario", description = "Elimina un registro solo si pertenece al usuario indicado.")
    @DeleteMapping("/usuario/{usuarioId}/{id}")
    public ResponseEntity<Void> eliminarParaUsuario(@PathVariable Long usuarioId, @PathVariable Long id) {
        service.eliminarParaUsuario(usuarioId, id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Marcar habito como cumplido", description = "Marca el habito como cumplido hoy. Uso administrativo.")
    @PatchMapping("/{id}/cumplir")
    public HabitoResponse cumplir(@PathVariable Long id) {
        return service.cumplir(id);
    }

    @Operation(summary = "Marcar habito de usuario como cumplido", description = "Marca el habito como cumplido solo si pertenece al usuario indicado.")
    @PatchMapping("/usuario/{usuarioId}/{id}/cumplir")
    public HabitoResponse cumplirParaUsuario(@PathVariable Long usuarioId, @PathVariable Long id) {
        return service.cumplirParaUsuario(usuarioId, id);
    }

}
