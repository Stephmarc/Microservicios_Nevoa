package com.nevoa.meta.controller;

import com.nevoa.meta.dto.MetaRequest;
import com.nevoa.meta.dto.MetaResponse;
import com.nevoa.meta.service.MetaService;
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

@Tag(name = "Metas", description = "Operaciones de metas separadas por usuario.")
@RestController
@RequestMapping("/api/v1/metas")
public class MetaController {
    private final MetaService service;

    @Value("${server.port:0}")
    private String port;

    @Value("${spring.application.name:meta-service}")
    private String serviceName;

    @Value("${spring.profiles.active:${SPRING_PROFILES_ACTIVE:dev}}")
    private String profile;

    public MetaController(MetaService service) {
        this.service = service;
    }

    @Operation(summary = "Verificar instancia de meta-service", description = "Devuelve estado, host, puerto y perfil activo del microservicio.")
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

    @Operation(summary = "Listar metas", description = "Endpoint administrativo usado para revision tecnica. Para uso funcional debe preferirse /usuario/{usuarioId}.")
    @GetMapping
    public List<MetaResponse> listar() {
        return service.listar();
    }

    @Operation(summary = "Listar metas de un usuario", description = "Devuelve solo los registros pertenecientes al usuario indicado.")
    @GetMapping("/usuario/{usuarioId}")
    public List<MetaResponse> listarPorUsuario(@Parameter(description = "ID del usuario propietario") @PathVariable Long usuarioId) {
        return service.listarPorUsuario(usuarioId);
    }

    @Operation(summary = "Obtener registro por usuario e ID", description = "Valida que el registro pertenezca al usuario indicado antes de devolverlo.")
    @GetMapping("/usuario/{usuarioId}/{id}")
    public MetaResponse obtenerPorUsuario(@PathVariable Long usuarioId, @PathVariable Long id) {
        return service.obtenerPorUsuario(usuarioId, id);
    }

    @Operation(summary = "Obtener registro por ID", description = "Endpoint administrativo. No valida usuario propietario.")
    @GetMapping("/{id}")
    public MetaResponse obtener(@PathVariable Long id) {
        return service.obtener(id);
    }

    @Operation(summary = "Crear registro", description = "Crea un registro usando el usuarioId enviado en el cuerpo.")
    @PostMapping
    public ResponseEntity<MetaResponse> crear(@Valid @RequestBody MetaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(request));
    }

    @Operation(summary = "Crear registro para un usuario", description = "Crea un registro asociado al usuario indicado en la ruta. Ruta recomendada para mantener datos separados por usuario.")
    @PostMapping("/usuario/{usuarioId}")
    public ResponseEntity<MetaResponse> crearParaUsuario(@PathVariable Long usuarioId, @Valid @RequestBody MetaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crearParaUsuario(usuarioId, request));
    }

    @Operation(summary = "Actualizar registro", description = "Actualiza un registro por ID. Uso administrativo.")
    @PutMapping("/{id}")
    public MetaResponse actualizar(@PathVariable Long id, @Valid @RequestBody MetaRequest request) {
        return service.actualizar(id, request);
    }

    @Operation(summary = "Actualizar registro de un usuario", description = "Actualiza un registro solo si pertenece al usuario indicado.")
    @PutMapping("/usuario/{usuarioId}/{id}")
    public MetaResponse actualizarParaUsuario(@PathVariable Long usuarioId, @PathVariable Long id, @Valid @RequestBody MetaRequest request) {
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

    @Operation(summary = "Actualizar progreso de meta", description = "Actualiza el progreso de una meta. Uso administrativo.")
    @PatchMapping("/{id}/progreso")
    public MetaResponse actualizarProgreso(@PathVariable Long id, @RequestParam Integer valor) {
        return service.actualizarProgreso(id, valor);
    }

    @Operation(summary = "Actualizar progreso de meta de un usuario", description = "Actualiza el progreso solo si la meta pertenece al usuario indicado.")
    @PatchMapping("/usuario/{usuarioId}/{id}/progreso")
    public MetaResponse actualizarProgresoParaUsuario(@PathVariable Long usuarioId, @PathVariable Long id, @RequestParam Integer valor) {
        return service.actualizarProgresoParaUsuario(usuarioId, id, valor);
    }

}
