package com.nevoa.dashboard.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Tag(name = "Dashboard", description = "Resumen de productividad por usuario. En esta unidad funciona como agregador base; la integracion directa con otros microservicios queda lista para evolucionar con Feign/WebClient.")
@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

    @Value("${server.port:0}")
    private String port;

    @Value("${spring.application.name:dashboard-service}")
    private String serviceName;

    @Value("${spring.profiles.active:${SPRING_PROFILES_ACTIVE:dev}}")
    private String profile;

    @Operation(summary = "Verificar instancia de dashboard-service", description = "Devuelve estado, host, puerto y perfil activo del microservicio.")
    @GetMapping("/instancia")
    public Map<String, Object> instancia() throws UnknownHostException {
        return Map.of(
                "servicio", serviceName,
                "status", "UP",
                "puerto", port,
                "perfil", profile,
                "host", InetAddress.getLocalHost().getHostName()
        );
    }

    @Operation(summary = "Obtener resumen base por usuario", description = "Devuelve indicadores base de productividad asociados al usuario. Preparado para integracion con otros microservicios en la siguiente unidad.")
    @GetMapping("/resumen/{usuarioId}")
    public Map<String, Object> resumen(@PathVariable Long usuarioId) {
        return Map.of(
                "usuarioId", usuarioId,
                "mensaje", "Resumen base de Nevoa preparado para integracion entre microservicios en Unidad 2",
                "fechaConsulta", LocalDateTime.now(),
                "indicadores", List.of(
                        Map.of("nombre", "tareasPendientes", "valor", 0),
                        Map.of("nombre", "proyectosActivos", "valor", 0),
                        Map.of("nombre", "metasEnProceso", "valor", 0),
                        Map.of("nombre", "habitosCumplidosHoy", "valor", 0),
                        Map.of("nombre", "pomodorosCompletados", "valor", 0)
                )
        );
    }
}
