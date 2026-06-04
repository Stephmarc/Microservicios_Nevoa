package com.nevoa.pomodoro.service;

import com.nevoa.pomodoro.domain.*;
import com.nevoa.pomodoro.dto.PomodoroRequest;
import com.nevoa.pomodoro.dto.PomodoroResponse;
import com.nevoa.pomodoro.exception.ResourceNotFoundException;
import com.nevoa.pomodoro.repository.PomodoroRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.time.LocalDateTime;

@Service
public class PomodoroService {
    private final PomodoroRepository repository;

    public PomodoroService(PomodoroRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<PomodoroResponse> listar() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<PomodoroResponse> listarPorUsuario(Long usuarioId) {
        return repository.findByUsuarioId(usuarioId).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public PomodoroResponse obtener(Long id) {
        return toResponse(buscarEntidad(id));
    }

    @Transactional(readOnly = true)
    public PomodoroResponse obtenerPorUsuario(Long usuarioId, Long id) {
        return toResponse(buscarEntidadDeUsuario(usuarioId, id));
    }

    @Transactional
    public PomodoroResponse crear(PomodoroRequest request) {
        return crearConUsuario(request.usuarioId(), request);
    }

    @Transactional
    public PomodoroResponse crearParaUsuario(Long usuarioId, PomodoroRequest request) {
        return crearConUsuario(usuarioId, request);
    }

    @Transactional
    public PomodoroResponse actualizar(Long id, PomodoroRequest request) {
        Pomodoro entity = buscarEntidad(id);
        aplicarDatos(entity, request.usuarioId(), request);
        return toResponse(repository.save(entity));
    }

    @Transactional
    public PomodoroResponse actualizarParaUsuario(Long usuarioId, Long id, PomodoroRequest request) {
        Pomodoro entity = buscarEntidadDeUsuario(usuarioId, id);
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
    public PomodoroResponse iniciar(Long id) {
        Pomodoro entity = buscarEntidad(id);
        aplicarInicio(entity);
        return toResponse(repository.save(entity));
    }

    @Transactional
    public PomodoroResponse iniciarParaUsuario(Long usuarioId, Long id) {
        Pomodoro entity = buscarEntidadDeUsuario(usuarioId, id);
        aplicarInicio(entity);
        return toResponse(repository.save(entity));
    }

    @Transactional
    public PomodoroResponse finalizar(Long id) {
        Pomodoro entity = buscarEntidad(id);
        aplicarFinalizacion(entity);
        return toResponse(repository.save(entity));
    }

    @Transactional
    public PomodoroResponse finalizarParaUsuario(Long usuarioId, Long id) {
        Pomodoro entity = buscarEntidadDeUsuario(usuarioId, id);
        aplicarFinalizacion(entity);
        return toResponse(repository.save(entity));
    }

    private void aplicarInicio(Pomodoro entity) {
        entity.setEstado(EstadoPomodoro.EN_PROCESO);
        entity.setInicio(LocalDateTime.now());
    }

    private void aplicarFinalizacion(Pomodoro entity) {
        entity.setEstado(EstadoPomodoro.COMPLETADO);
        entity.setFin(LocalDateTime.now());
    }

    private PomodoroResponse crearConUsuario(Long usuarioId, PomodoroRequest request) {
        Pomodoro entity = new Pomodoro();
        entity.setUsuarioId(usuarioId);
        entity.setMinutosTrabajo(request.minutosTrabajo());
        entity.setMinutosDescanso(request.minutosDescanso());
        entity.setEstado(EstadoPomodoro.PENDIENTE);
        entity.setCreadoEn(LocalDateTime.now());
        return toResponse(repository.save(entity));
    }

    private void aplicarDatos(Pomodoro entity, Long usuarioId, PomodoroRequest request) {
        entity.setUsuarioId(usuarioId);
        entity.setMinutosTrabajo(request.minutosTrabajo());
        entity.setMinutosDescanso(request.minutosDescanso());
    }

    private Pomodoro buscarEntidad(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pomodoro no encontrado con id " + id));
    }

    private Pomodoro buscarEntidadDeUsuario(Long usuarioId, Long id) {
        return repository.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Pomodoro no encontrado con id " + id + " para el usuario " + usuarioId));
    }

    private PomodoroResponse toResponse(Pomodoro entity) {
        return new PomodoroResponse(entity.getId(), entity.getUsuarioId(), entity.getMinutosTrabajo(), entity.getMinutosDescanso(), entity.getEstado(), entity.getInicio(), entity.getFin(), entity.getCreadoEn());
    }
}
