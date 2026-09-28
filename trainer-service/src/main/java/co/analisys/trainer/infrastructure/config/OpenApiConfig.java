package co.analisys.trainer.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Metadatos globales de la documentación OpenAPI / Swagger UI de este microservicio.
 * Swagger UI queda disponible en /swagger-ui.html y el contrato JSON en /v3/api-docs.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI trainerServiceOpenAPI() {
        final String securitySchemeName = "bearerAuth";
        return new OpenAPI().info(new Info()
                .title("Trainer Service API")
                .description("Microservicio del bounded context Coaching: alta y consulta de entrenadores. Expone también la verificación de existencia que usa class-service.")
            .version("v1"))
            .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
            .components(new Components()
                .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                    .name(securitySchemeName)
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")));
    }
}
