package com.nevoa.usuario.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "usuarios")
public class Usuario {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "El nombre completo es obligatorio")
    @Column(name = "nombre_completo", nullable = false, length = 120)
    private String nombreCompleto;
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email no tiene un formato valido")
    @Column(name = "email", nullable = false, length = 120, unique = true)
    private String email;
    @NotBlank(message = "La contraseña es obligatoria")
    @Column(name = "password_hash", nullable = false, length = 255)
    private String password;
    @Column(name = "activo", nullable = false)
    private Boolean activo;
    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;
}
