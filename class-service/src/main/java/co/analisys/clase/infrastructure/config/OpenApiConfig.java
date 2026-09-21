package co.analisys.clase.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Metadatos globales de la documentación OpenAPI / Swagger UI de este microservicio.
 * Swagger UI queda disponible en /swagger-ui.html y el contrato JSON en /v3/api-docs.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI classServiceOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("Class Service API")
                .description("Microservicio del bounded context Scheduling: programación y consulta de clases. Al programar una clase valida por REST, contra trainer-service, que el entrenador exista.")
                .version("v1"));
    }
}
