package com.nevoa.tarea.controller;

import com.nevoa.tarea.dto.TareaRequest;
import com.nevoa.tarea.dto.TareaResponse;
import com.nevoa.tarea.service.TareaService;
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

@Tag(name = "Tareas", description = "Operaciones de tareas separadas por usuario. Las tareas pueden asociarse opcionalmente a un proyecto mediante proyectoId.")
@RestController
@RequestMapping("/api/v1/tareas")
public class TareaController {
    private final TareaService service;

    @Value("${server.port:0}")
    private String port;

    @Value("${spring.application.name:tarea-service}")
    private String serviceName;

    @Value("${spring.profiles.active:${SPRING_PROFILES_ACTIVE:dev}}")
    private String profile;

    public TareaController(TareaService service) {
        this.service = service;
    }

    @Operation(summary = "Verificar instancia de tarea-service", description = "Devuelve estado, host, puerto y perfil activo del microservicio.")
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

    @Operation(summary = "Listar todas las tareas", description = "Endpoint administrativo usado para revision tecnica. Para uso funcional debe preferirse /usuario/{usuarioId}.")
    @GetMapping
    public List<TareaResponse> listar() {
        return service.listar();
    }

    @Operation(summary = "Listar tareas de un usuario", description = "Devuelve solo las tareas pertenecientes al usuario indicado.")
    @GetMapping("/usuario/{usuarioId}")
    public List<TareaResponse> listarPorUsuario(@Parameter(description = "ID del usuario propietario") @PathVariable Long usuarioId) {
        return service.listarPorUsuario(usuarioId);
    }

    @Operation(summary = "Obtener tarea por usuario e ID", description = "Valida que la tarea pertenezca al usuario indicado antes de devolverla.")
    @GetMapping("/usuario/{usuarioId}/{id}")
    public TareaResponse obtenerPorUsuario(@PathVariable Long usuarioId, @PathVariable Long id) {
        return service.obtenerPorUsuario(usuarioId, id);
    }

    @Operation(summary = "Obtener tarea por ID", description = "Endpoint administrativo. No valida usuario propietario.")
    @GetMapping("/{id}")
    public TareaResponse obtener(@PathVariable Long id) {
        return service.obtener(id);
    }

    @Operation(summary = "Crear tarea", description = "Crea una tarea usando el usuarioId enviado en el cuerpo de la solicitud.")
    @PostMapping
    public ResponseEntity<TareaResponse> crear(@Valid @RequestBody TareaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(request));
    }

    @Operation(summary = "Crear tarea para un usuario", description = "Crea una tarea asociada al usuario indicado en la ruta. Esta ruta es recomendada para mantener datos separados por usuario.")
    @PostMapping("/usuario/{usuarioId}")
    public ResponseEntity<TareaResponse> crearParaUsuario(@PathVariable Long usuarioId, @Valid @RequestBody TareaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crearParaUsuario(usuarioId, request));
    }

    @Operation(summary = "Actualizar tarea", description = "Actualiza una tarea por ID sin validar usuario propietario. Uso administrativo.")
    @PutMapping("/{id}")
    public TareaResponse actualizar(@PathVariable Long id, @Valid @RequestBody TareaRequest request) {
        return service.actualizar(id, request);
    }

    @Operation(summary = "Actualizar tarea de un usuario", description = "Actualiza una tarea solo si pertenece al usuario indicado.")
    @PutMapping("/usuario/{usuarioId}/{id}")
    public TareaResponse actualizarParaUsuario(@PathVariable Long usuarioId, @PathVariable Long id, @Valid @RequestBody TareaRequest request) {
        return service.actualizarParaUsuario(usuarioId, id, request);
    }

    @Operation(summary = "Eliminar tarea", description = "Elimina una tarea por ID. Uso administrativo.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Eliminar tarea de un usuario", description = "Elimina una tarea solo si pertenece al usuario indicado.")
    @DeleteMapping("/usuario/{usuarioId}/{id}")
    public ResponseEntity<Void> eliminarParaUsuario(@PathVariable Long usuarioId, @PathVariable Long id) {
        service.eliminarParaUsuario(usuarioId, id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Completar tarea", description = "Marca una tarea como completada por ID. Uso administrativo.")
    @PatchMapping("/{id}/completar")
    public TareaResponse completar(@PathVariable Long id) {
        return service.completar(id);
    }

    @Operation(summary = "Completar tarea de un usuario", description = "Marca una tarea como completada solo si pertenece al usuario indicado.")
    @PatchMapping("/usuario/{usuarioId}/{id}/completar")
    public TareaResponse completarParaUsuario(@PathVariable Long usuarioId, @PathVariable Long id) {
        return service.completarParaUsuario(usuarioId, id);
    }
}
