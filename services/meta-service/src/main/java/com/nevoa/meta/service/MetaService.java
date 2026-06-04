package com.nevoa.meta.service;

import com.nevoa.meta.domain.*;
import com.nevoa.meta.dto.MetaRequest;
import com.nevoa.meta.dto.MetaResponse;
import com.nevoa.meta.exception.ResourceNotFoundException;
import com.nevoa.meta.repository.MetaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.time.LocalDateTime;

@Service
public class MetaService {
    private final MetaRepository repository;

    public MetaService(MetaRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<MetaResponse> listar() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<MetaResponse> listarPorUsuario(Long usuarioId) {
        return repository.findByUsuarioId(usuarioId).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public MetaResponse obtener(Long id) {
        return toResponse(buscarEntidad(id));
    }

    @Transactional(readOnly = true)
    public MetaResponse obtenerPorUsuario(Long usuarioId, Long id) {
        return toResponse(buscarEntidadDeUsuario(usuarioId, id));
    }

    @Transactional
    public MetaResponse crear(MetaRequest request) {
        return crearConUsuario(request.usuarioId(), request);
    }

    @Transactional
    public MetaResponse crearParaUsuario(Long usuarioId, MetaRequest request) {
        return crearConUsuario(usuarioId, request);
    }

    @Transactional
    public MetaResponse actualizar(Long id, MetaRequest request) {
        Meta entity = buscarEntidad(id);
        aplicarDatos(entity, request.usuarioId(), request);
        return toResponse(repository.save(entity));
    }

    @Transactional
    public MetaResponse actualizarParaUsuario(Long usuarioId, Long id, MetaRequest request) {
        Meta entity = buscarEntidadDeUsuario(usuarioId, id);
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
    public MetaResponse actualizarProgreso(Long id, Integer valor) {
        Meta entity = buscarEntidad(id);
        aplicarProgreso(entity, valor);
        return toResponse(repository.save(entity));
    }

    @Transactional
    public MetaResponse actualizarProgresoParaUsuario(Long usuarioId, Long id, Integer valor) {
        Meta entity = buscarEntidadDeUsuario(usuarioId, id);
        aplicarProgreso(entity, valor);
        return toResponse(repository.save(entity));
    }

    private void aplicarProgreso(Meta entity, Integer valor) {
        int progreso = Math.min(100, Math.max(0, valor == null ? 0 : valor));
        entity.setProgreso(progreso);
        entity.setEstado(progreso >= 100 ? EstadoMeta.COMPLETADA : EstadoMeta.EN_PROCESO);
        entity.setActualizadaEn(LocalDateTime.now());
    }

    private MetaResponse crearConUsuario(Long usuarioId, MetaRequest request) {
        Meta entity = new Meta();
        entity.setUsuarioId(usuarioId);
        entity.setTitulo(request.titulo());
        entity.setDescripcion(request.descripcion());
        entity.setCategoria(request.categoria());
        entity.setFechaLimite(request.fechaLimite());
        entity.setProgreso(0);
        entity.setEstado(EstadoMeta.EN_PROCESO);
        entity.setCreadaEn(LocalDateTime.now());
        return toResponse(repository.save(entity));
    }

    private void aplicarDatos(Meta entity, Long usuarioId, MetaRequest request) {
        entity.setUsuarioId(usuarioId);
        entity.setTitulo(request.titulo());
        entity.setDescripcion(request.descripcion());
        entity.setCategoria(request.categoria());
        entity.setFechaLimite(request.fechaLimite());
        entity.setActualizadaEn(LocalDateTime.now());
    }

    private Meta buscarEntidad(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Meta no encontrado con id " + id));
    }

    private Meta buscarEntidadDeUsuario(Long usuarioId, Long id) {
        return repository.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Meta no encontrado con id " + id + " para el usuario " + usuarioId));
    }

    private MetaResponse toResponse(Meta entity) {
        return new MetaResponse(entity.getId(), entity.getUsuarioId(), entity.getTitulo(), entity.getDescripcion(), entity.getCategoria(), entity.getProgreso(), entity.getFechaLimite(), entity.getEstado(), entity.getCreadaEn(), entity.getActualizadaEn());
    }
}
