package com.nevoa.nota.service;

import com.nevoa.nota.domain.*;
import com.nevoa.nota.dto.NotaRequest;
import com.nevoa.nota.dto.NotaResponse;
import com.nevoa.nota.exception.ResourceNotFoundException;
import com.nevoa.nota.repository.NotaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.time.LocalDateTime;

@Service
public class NotaService {
    private final NotaRepository repository;

    public NotaService(NotaRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<NotaResponse> listar() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<NotaResponse> listarPorUsuario(Long usuarioId) {
        return repository.findByUsuarioId(usuarioId).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public NotaResponse obtener(Long id) {
        return toResponse(buscarEntidad(id));
    }

    @Transactional(readOnly = true)
    public NotaResponse obtenerPorUsuario(Long usuarioId, Long id) {
        return toResponse(buscarEntidadDeUsuario(usuarioId, id));
    }

    @Transactional
    public NotaResponse crear(NotaRequest request) {
        return crearConUsuario(request.usuarioId(), request);
    }

    @Transactional
    public NotaResponse crearParaUsuario(Long usuarioId, NotaRequest request) {
        return crearConUsuario(usuarioId, request);
    }

    @Transactional
    public NotaResponse actualizar(Long id, NotaRequest request) {
        Nota entity = buscarEntidad(id);
        aplicarDatos(entity, request.usuarioId(), request);
        return toResponse(repository.save(entity));
    }

    @Transactional
    public NotaResponse actualizarParaUsuario(Long usuarioId, Long id, NotaRequest request) {
        Nota entity = buscarEntidadDeUsuario(usuarioId, id);
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

    private NotaResponse crearConUsuario(Long usuarioId, NotaRequest request) {
        Nota entity = new Nota();
        entity.setUsuarioId(usuarioId);
        entity.setTitulo(request.titulo());
        entity.setCategoria(request.categoria());
        entity.setContenido(request.contenido());
        entity.setCreadaEn(LocalDateTime.now());
        return toResponse(repository.save(entity));
    }

    private void aplicarDatos(Nota entity, Long usuarioId, NotaRequest request) {
        entity.setUsuarioId(usuarioId);
        entity.setTitulo(request.titulo());
        entity.setCategoria(request.categoria());
        entity.setContenido(request.contenido());
        entity.setActualizadaEn(LocalDateTime.now());
    }

    private Nota buscarEntidad(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nota no encontrado con id " + id));
    }

    private Nota buscarEntidadDeUsuario(Long usuarioId, Long id) {
        return repository.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Nota no encontrado con id " + id + " para el usuario " + usuarioId));
    }

    private NotaResponse toResponse(Nota entity) {
        return new NotaResponse(entity.getId(), entity.getUsuarioId(), entity.getTitulo(), entity.getCategoria(), entity.getContenido(), entity.getCreadaEn(), entity.getActualizadaEn());
    }
}
