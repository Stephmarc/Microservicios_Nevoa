package com.nevoa.proyecto.service;

import com.nevoa.proyecto.domain.*;
import com.nevoa.proyecto.dto.ProyectoRequest;
import com.nevoa.proyecto.dto.ProyectoResponse;
import com.nevoa.proyecto.exception.ResourceNotFoundException;
import com.nevoa.proyecto.repository.ProyectoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.time.LocalDateTime;

@Service
public class ProyectoService {
    private final ProyectoRepository repository;

    public ProyectoService(ProyectoRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<ProyectoResponse> listar() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<ProyectoResponse> listarPorUsuario(Long usuarioId) {
        return repository.findByUsuarioId(usuarioId).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ProyectoResponse obtener(Long id) {
        return toResponse(buscarEntidad(id));
    }

    @Transactional(readOnly = true)
    public ProyectoResponse obtenerPorUsuario(Long usuarioId, Long id) {
        return toResponse(buscarEntidadDeUsuario(usuarioId, id));
    }

    @Transactional
    public ProyectoResponse crear(ProyectoRequest request) {
        return crearConUsuario(request.usuarioId(), request);
    }

    @Transactional
    public ProyectoResponse crearParaUsuario(Long usuarioId, ProyectoRequest request) {
        return crearConUsuario(usuarioId, request);
    }

    @Transactional
    public ProyectoResponse actualizar(Long id, ProyectoRequest request) {
        Proyecto entity = buscarEntidad(id);
        aplicarDatos(entity, request.usuarioId(), request);
        return toResponse(repository.save(entity));
    }

    @Transactional
    public ProyectoResponse actualizarParaUsuario(Long usuarioId, Long id, ProyectoRequest request) {
        Proyecto entity = buscarEntidadDeUsuario(usuarioId, id);
        aplicarDatos(entity, usuarioId, request);
        return toResponse(repository.save(entity));
    }

    @Transactional
    public void eliminar(Long id) {
        repository.delete(buscarEntidad(id));
    }

    @Transactional
    public void eliminarParaUsuario(Long usuarioId, Long id) {
        repository.delete(buscarEntidadDeUsuario(usuarioId, id));
    }

    @Transactional
    public ProyectoResponse recalcularProgreso(Long id, Integer totalTareas, Integer tareasCompletadas) {
        Proyecto entity = buscarEntidad(id);
        aplicarProgreso(entity, totalTareas, tareasCompletadas);
        return toResponse(repository.save(entity));
    }

    @Transactional
    public ProyectoResponse recalcularProgresoParaUsuario(Long usuarioId, Long id, Integer totalTareas, Integer tareasCompletadas) {
        Proyecto entity = buscarEntidadDeUsuario(usuarioId, id);
        aplicarProgreso(entity, totalTareas, tareasCompletadas);
        return toResponse(repository.save(entity));
    }

    private void aplicarProgreso(Proyecto entity, Integer totalTareas, Integer tareasCompletadas) {
        entity.setTotalTareas(totalTareas == null ? 0 : totalTareas);
        entity.setTareasCompletadas(tareasCompletadas == null ? 0 : tareasCompletadas);
        int progreso = entity.getTotalTareas() == 0 ? 0 : (entity.getTareasCompletadas() * 100) / entity.getTotalTareas();
        entity.setProgreso(Math.min(100, Math.max(0, progreso)));
        entity.setEstado(entity.getProgreso() >= 100 ? EstadoProyecto.FINALIZADO : EstadoProyecto.ACTIVO);
        entity.setActualizadoEn(LocalDateTime.now());
    }

    private ProyectoResponse crearConUsuario(Long usuarioId, ProyectoRequest request) {
        Proyecto entity = new Proyecto();
        entity.setUsuarioId(usuarioId);
        entity.setNombre(request.nombre());
        entity.setDescripcion(request.descripcion());
        entity.setProgreso(0);
        entity.setTotalTareas(0);
        entity.setTareasCompletadas(0);
        entity.setEstado(EstadoProyecto.ACTIVO);
        entity.setCreadoEn(LocalDateTime.now());
        return toResponse(repository.save(entity));
    }

    private void aplicarDatos(Proyecto entity, Long usuarioId, ProyectoRequest request) {
        entity.setUsuarioId(usuarioId);
        entity.setNombre(request.nombre());
        entity.setDescripcion(request.descripcion());
        entity.setActualizadoEn(LocalDateTime.now());
    }

    private Proyecto buscarEntidad(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proyecto no encontrado con id " + id));
    }

    private Proyecto buscarEntidadDeUsuario(Long usuarioId, Long id) {
        return repository.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Proyecto no encontrado con id " + id + " para el usuario " + usuarioId));
    }

    private ProyectoResponse toResponse(Proyecto entity) {
        return new ProyectoResponse(entity.getId(), entity.getUsuarioId(), entity.getNombre(), entity.getDescripcion(), entity.getProgreso(), entity.getTotalTareas(), entity.getTareasCompletadas(), entity.getEstado(), entity.getCreadoEn(), entity.getActualizadoEn());
    }
}
