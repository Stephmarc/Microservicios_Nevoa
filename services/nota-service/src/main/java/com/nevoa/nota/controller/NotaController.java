package com.nevoa.nota.controller;

import com.nevoa.nota.dto.NotaRequest;
import com.nevoa.nota.dto.NotaResponse;
import com.nevoa.nota.service.NotaService;
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

@Tag(name = "Notas", description = "Operaciones de notas separadas por usuario.")
@RestController
@RequestMapping("/api/v1/notas")
public class NotaController {
    private final NotaService service;

    @Value("${server.port:0}")
    private String port;

    @Value("${spring.application.name:nota-service}")
    private String serviceName;

    @Value("${spring.profiles.active:${SPRING_PROFILES_ACTIVE:dev}}")
    private String profile;

    public NotaController(NotaService service) {
        this.service = service;
    }

    @Operation(summary = "Verificar instancia de nota-service", description = "Devuelve estado, host, puerto y perfil activo del microservicio.")
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

    @Operation(summary = "Listar notas", description = "Endpoint administrativo usado para revision tecnica. Para uso funcional debe preferirse /usuario/{usuarioId}.")
    @GetMapping
    public List<NotaResponse> listar() {
        return service.listar();
    }

    @Operation(summary = "Listar notas de un usuario", description = "Devuelve solo los registros pertenecientes al usuario indicado.")
    @GetMapping("/usuario/{usuarioId}")
    public List<NotaResponse> listarPorUsuario(@Parameter(description = "ID del usuario propietario") @PathVariable Long usuarioId) {
        return service.listarPorUsuario(usuarioId);
    }

    @Operation(summary = "Obtener registro por usuario e ID", description = "Valida que el registro pertenezca al usuario indicado antes de devolverlo.")
    @GetMapping("/usuario/{usuarioId}/{id}")
    public NotaResponse obtenerPorUsuario(@PathVariable Long usuarioId, @PathVariable Long id) {
        return service.obtenerPorUsuario(usuarioId, id);
    }

    @Operation(summary = "Obtener registro por ID", description = "Endpoint administrativo. No valida usuario propietario.")
    @GetMapping("/{id}")
    public NotaResponse obtener(@PathVariable Long id) {
        return service.obtener(id);
    }

    @Operation(summary = "Crear registro", description = "Crea un registro usando el usuarioId enviado en el cuerpo.")
    @PostMapping
    public ResponseEntity<NotaResponse> crear(@Valid @RequestBody NotaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(request));
    }

    @Operation(summary = "Crear registro para un usuario", description = "Crea un registro asociado al usuario indicado en la ruta. Ruta recomendada para mantener datos separados por usuario.")
    @PostMapping("/usuario/{usuarioId}")
    public ResponseEntity<NotaResponse> crearParaUsuario(@PathVariable Long usuarioId, @Valid @RequestBody NotaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crearParaUsuario(usuarioId, request));
    }

    @Operation(summary = "Actualizar registro", description = "Actualiza un registro por ID. Uso administrativo.")
    @PutMapping("/{id}")
    public NotaResponse actualizar(@PathVariable Long id, @Valid @RequestBody NotaRequest request) {
        return service.actualizar(id, request);
    }

    @Operation(summary = "Actualizar registro de un usuario", description = "Actualiza un registro solo si pertenece al usuario indicado.")
    @PutMapping("/usuario/{usuarioId}/{id}")
    public NotaResponse actualizarParaUsuario(@PathVariable Long usuarioId, @PathVariable Long id, @Valid @RequestBody NotaRequest request) {
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

}
