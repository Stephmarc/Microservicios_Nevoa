package com.nevoa.usuario.service;

import com.nevoa.usuario.domain.Usuario;
import com.nevoa.usuario.dto.LoginRequest;
import com.nevoa.usuario.dto.LoginResponse;
import com.nevoa.usuario.dto.UsuarioRequest;
import com.nevoa.usuario.dto.UsuarioResponse;
import com.nevoa.usuario.exception.ResourceNotFoundException;
import com.nevoa.usuario.repository.UsuarioRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuthService {
    private final UsuarioRepository repository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthService(UsuarioRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public UsuarioResponse registrar(UsuarioRequest request) {
        if (repository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("El email ya se encuentra registrado");
        }
        Usuario usuario = new Usuario();
        usuario.setNombreCompleto(request.nombreCompleto());
        usuario.setEmail(request.email());
        usuario.setPassword(passwordEncoder.encode(request.password()));
        usuario.setActivo(true);
        usuario.setFechaRegistro(LocalDateTime.now());
        Usuario saved = repository.save(usuario);
        return new UsuarioResponse(saved.getId(), saved.getNombreCompleto(), saved.getEmail(), "PROTEGIDO", saved.getActivo(), saved.getFechaRegistro());
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        Usuario usuario = repository.findByEmail(request.email())
                .orElseThrow(() -> new ResourceNotFoundException("Credenciales invalidas"));
        if (!passwordEncoder.matches(request.password(), usuario.getPassword())) {
            throw new ResourceNotFoundException("Credenciales invalidas");
        }
        if (!Boolean.TRUE.equals(usuario.getActivo())) {
            throw new IllegalArgumentException("El usuario se encuentra inactivo");
        }
        return new LoginResponse(usuario.getId(), usuario.getNombreCompleto(), usuario.getEmail(), "LOGIN_OK");
    }
}
