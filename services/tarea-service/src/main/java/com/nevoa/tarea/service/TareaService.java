package com.nevoa.tarea.service;

import com.nevoa.tarea.domain.*;
import com.nevoa.tarea.dto.TareaRequest;
import com.nevoa.tarea.dto.TareaResponse;
import com.nevoa.tarea.exception.ResourceNotFoundException;
import com.nevoa.tarea.repository.TareaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.time.LocalDateTime;

@Service
public class TareaService {
    private final TareaRepository repository;

    public TareaService(TareaRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<TareaResponse> listar() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<TareaResponse> listarPorUsuario(Long usuarioId) {
        return repository.findByUsuarioId(usuarioId).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public TareaResponse obtener(Long id) {
        return toResponse(buscarEntidad(id));
    }

    @Transactional(readOnly = true)
    public TareaResponse obtenerPorUsuario(Long usuarioId, Long id) {
        return toResponse(buscarEntidadDeUsuario(usuarioId, id));
    }

    @Transactional
    public TareaResponse crear(TareaRequest request) {
        return crearConUsuario(request.usuarioId(), request);
    }

    @Transactional
    public TareaResponse crearParaUsuario(Long usuarioId, TareaRequest request) {
        return crearConUsuario(usuarioId, request);
    }

    @Transactional
    public TareaResponse actualizar(Long id, TareaRequest request) {
        Tarea entity = buscarEntidad(id);
        aplicarDatos(entity, request.usuarioId(), request);
        return toResponse(repository.save(entity));
    }

    @Transactional
    public TareaResponse actualizarParaUsuario(Long usuarioId, Long id, TareaRequest request) {
        Tarea entity = buscarEntidadDeUsuario(usuarioId, id);
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
    public TareaResponse completar(Long id) {
        Tarea entity = buscarEntidad(id);
        entity.setEstado(EstadoTarea.COMPLETADA);
        entity.setActualizadaEn(LocalDateTime.now());
        return toResponse(repository.save(entity));
    }

    @Transactional
    public TareaResponse completarParaUsuario(Long usuarioId, Long id) {
        Tarea entity = buscarEntidadDeUsuario(usuarioId, id);
        entity.setEstado(EstadoTarea.COMPLETADA);
        entity.setActualizadaEn(LocalDateTime.now());
        return toResponse(repository.save(entity));
    }

    private TareaResponse crearConUsuario(Long usuarioId, TareaRequest request) {
        Tarea entity = new Tarea();
        aplicarDatos(entity, usuarioId, request);
        entity.setEstado(EstadoTarea.PENDIENTE);
        entity.setCreadaEn(LocalDateTime.now());
        return toResponse(repository.save(entity));
    }

    private void aplicarDatos(Tarea entity, Long usuarioId, TareaRequest request) {
        entity.setUsuarioId(usuarioId);
        entity.setProyectoId(request.proyectoId());
        entity.setTitulo(request.titulo());
        entity.setDescripcion(request.descripcion());
        entity.setPrioridad(request.prioridad());
        entity.setFechaLimite(request.fechaLimite());
        entity.setActualizadaEn(LocalDateTime.now());
    }

    private Tarea buscarEntidad(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarea no encontrada con id " + id));
    }

    private Tarea buscarEntidadDeUsuario(Long usuarioId, Long id) {
        return repository.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Tarea no encontrada con id " + id + " para el usuario " + usuarioId));
    }

    private TareaResponse toResponse(Tarea entity) {
        return new TareaResponse(entity.getId(), entity.getUsuarioId(), entity.getProyectoId(), entity.getTitulo(), entity.getDescripcion(), entity.getPrioridad(), entity.getEstado(), entity.getFechaLimite(), entity.getCreadaEn(), entity.getActualizadaEn());
    }
}
