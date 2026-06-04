package com.nevoa.usuario.service;

import com.nevoa.usuario.domain.*;
import com.nevoa.usuario.dto.UsuarioRequest;
import com.nevoa.usuario.dto.UsuarioResponse;
import com.nevoa.usuario.exception.ResourceNotFoundException;
import com.nevoa.usuario.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.time.LocalDateTime;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service
public class UsuarioService {
    private final UsuarioRepository repository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UsuarioService(UsuarioRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listar() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponse obtener(Long id) {
        return toResponse(buscarEntidad(id));
    }

    @Transactional
    public UsuarioResponse crear(UsuarioRequest request) {
        Usuario entity = new Usuario();

        entity.setNombreCompleto(request.nombreCompleto());
        entity.setEmail(request.email());
        entity.setPassword(passwordEncoder.encode(request.password()));
        entity.setActivo(true);
        entity.setFechaRegistro(LocalDateTime.now());
        return toResponse(repository.save(entity));
    }

    @Transactional
    public UsuarioResponse actualizar(Long id, UsuarioRequest request) {
        Usuario entity = buscarEntidad(id);

        entity.setNombreCompleto(request.nombreCompleto());
        entity.setEmail(request.email());
        if (request.password() != null && !request.password().isBlank()) {
            entity.setPassword(passwordEncoder.encode(request.password()));
        }

        return toResponse(repository.save(entity));
    }

    @Transactional
    public void eliminar(Long id) {
        Usuario entity = buscarEntidad(id);
        repository.delete(entity);
    }

    private Usuario buscarEntidad(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id " + id));
    }

    private UsuarioResponse toResponse(Usuario entity) {
        return new UsuarioResponse(entity.getId(), entity.getNombreCompleto(), entity.getEmail(), "PROTEGIDO", entity.getActivo(), entity.getFechaRegistro());
    }
}
