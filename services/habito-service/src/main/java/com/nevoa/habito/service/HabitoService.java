package com.nevoa.habito.service;

import com.nevoa.habito.domain.*;
import com.nevoa.habito.dto.HabitoRequest;
import com.nevoa.habito.dto.HabitoResponse;
import com.nevoa.habito.exception.ResourceNotFoundException;
import com.nevoa.habito.repository.HabitoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.time.LocalDateTime;

@Service
public class HabitoService {
    private final HabitoRepository repository;

    public HabitoService(HabitoRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<HabitoResponse> listar() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<HabitoResponse> listarPorUsuario(Long usuarioId) {
        return repository.findByUsuarioId(usuarioId).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public HabitoResponse obtener(Long id) {
        return toResponse(buscarEntidad(id));
    }

    @Transactional(readOnly = true)
    public HabitoResponse obtenerPorUsuario(Long usuarioId, Long id) {
        return toResponse(buscarEntidadDeUsuario(usuarioId, id));
    }

    @Transactional
    public HabitoResponse crear(HabitoRequest request) {
        return crearConUsuario(request.usuarioId(), request);
    }

    @Transactional
    public HabitoResponse crearParaUsuario(Long usuarioId, HabitoRequest request) {
        return crearConUsuario(usuarioId, request);
    }

    @Transactional
    public HabitoResponse actualizar(Long id, HabitoRequest request) {
        Habito entity = buscarEntidad(id);
        aplicarDatos(entity, request.usuarioId(), request);
        return toResponse(repository.save(entity));
    }

    @Transactional
    public HabitoResponse actualizarParaUsuario(Long usuarioId, Long id, HabitoRequest request) {
        Habito entity = buscarEntidadDeUsuario(usuarioId, id);
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
    public HabitoResponse cumplir(Long id) {
        Habito entity = buscarEntidad(id);
        aplicarCumplimiento(entity);
        return toResponse(repository.save(entity));
    }

    @Transactional
    public HabitoResponse cumplirParaUsuario(Long usuarioId, Long id) {
        Habito entity = buscarEntidadDeUsuario(usuarioId, id);
        aplicarCumplimiento(entity);
        return toResponse(repository.save(entity));
    }

    private void aplicarCumplimiento(Habito entity) {
        if (!Boolean.TRUE.equals(entity.getCumplidoHoy())) {
            entity.setCumplidoHoy(true);
            entity.setRachaActual(entity.getRachaActual() + 1);
            entity.setDiasCompletados(entity.getDiasCompletados() + 1);
            entity.setActualizadoEn(LocalDateTime.now());
        }
    }

    private HabitoResponse crearConUsuario(Long usuarioId, HabitoRequest request) {
        Habito entity = new Habito();
        entity.setUsuarioId(usuarioId);
        entity.setNombre(request.nombre());
        entity.setRachaActual(0);
        entity.setDiasCompletados(0);
        entity.setCumplidoHoy(false);
        entity.setCreadoEn(LocalDateTime.now());
        return toResponse(repository.save(entity));
    }

    private void aplicarDatos(Habito entity, Long usuarioId, HabitoRequest request) {
        entity.setUsuarioId(usuarioId);
        entity.setNombre(request.nombre());
        entity.setActualizadoEn(LocalDateTime.now());
    }

    private Habito buscarEntidad(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Habito no encontrado con id " + id));
    }

    private Habito buscarEntidadDeUsuario(Long usuarioId, Long id) {
        return repository.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Habito no encontrado con id " + id + " para el usuario " + usuarioId));
    }

    private HabitoResponse toResponse(Habito entity) {
        return new HabitoResponse(entity.getId(), entity.getUsuarioId(), entity.getNombre(), entity.getRachaActual(), entity.getDiasCompletados(), entity.getCumplidoHoy(), entity.getCreadoEn(), entity.getActualizadoEn());
    }
}
