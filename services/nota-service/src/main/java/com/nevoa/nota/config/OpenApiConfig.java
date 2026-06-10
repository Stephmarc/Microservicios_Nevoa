package com.nevoa.nota.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Nevoa - Nota Service API",
                version = "1.0.0",
                description = "Gestion de notas e ideas por usuario. Todos los recursos funcionales se asocian mediante usuarioId para mantener datos separados por usuario.",
                contact = @Contact(name = "Equipo Nevoa", email = "midwarcoila@gmail.com"),
                license = @License(name = "Uso academico")
        ),
        servers = {
                @Server(url = "/", description = "Servicio directo o via API Gateway")
        }
)
public class OpenApiConfig {
}
