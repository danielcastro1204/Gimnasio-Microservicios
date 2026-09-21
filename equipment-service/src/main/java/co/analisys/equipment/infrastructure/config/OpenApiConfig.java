package co.analisys.equipment.infrastructure.config;

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
    public OpenAPI equipmentServiceOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("Equipment Service API")
                .description("Microservicio del bounded context Inventory: alta y consulta del inventario de equipos del gimnasio.")
                .version("v1"));
    }
}
