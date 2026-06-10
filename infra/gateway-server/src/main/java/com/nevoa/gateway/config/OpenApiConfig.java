package com.nevoa.gateway.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Nevoa - API Gateway Swagger",
                version = "1.0.0",
                description = "Documentacion centralizada de los microservicios de Nevoa. Cada API mantiene recursos separados por usuario mediante usuarioId.",
                contact = @Contact(name = "Equipo Nevoa", email = "midwarcoila@gmail.com"),
                license = @License(name = "Uso academico")
        ),
        servers = {
                @Server(url = "/", description = "API Gateway")
        }
)
public class OpenApiConfig {
}
