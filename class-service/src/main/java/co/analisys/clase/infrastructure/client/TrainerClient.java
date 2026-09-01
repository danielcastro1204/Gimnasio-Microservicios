package co.analisys.clase.infrastructure.client;

import co.analisys.clase.domain.service.EntrenadorInfo;
import co.analisys.clase.domain.service.EntrenadorVerificationPort;
import co.analisys.clase.infrastructure.exception.ServiceUnavailableException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;
import java.util.Optional;

/**
 * Adaptador REST (infrastructure) del puerto EntrenadorVerificationPort.
 * Es el único punto del microservicio class-service que sabe que "trainer-service"
 * existe y cómo hablarle por HTTP. Nunca accede a su base de datos directamente.
 */
@Component
public class TrainerClient implements EntrenadorVerificationPort {

    private final WebClient webClient;

    public TrainerClient(WebClient.Builder webClientBuilder,
                          @Value("${services.trainer-service.url}") String trainerServiceUrl) {
        this.webClient = webClientBuilder.baseUrl(trainerServiceUrl).build();
    }

    @Override
    public boolean existeEntrenador(Long entrenadorId) {
        try {
            Boolean existe = webClient.get()
                    .uri("/api/trainers/{id}/exists", entrenadorId)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .timeout(Duration.ofSeconds(3))
                    .block();
            return Boolean.TRUE.equals(existe);
        } catch (WebClientResponseException.NotFound e) {
            return false;
        } catch (Exception e) {
            throw new ServiceUnavailableException(
                    "trainer-service no está disponible en este momento. No fue posible verificar el entrenador " + entrenadorId);
        }
    }

    @Override
    public Optional<EntrenadorInfo> obtenerEntrenador(Long entrenadorId) {
        try {
            EntrenadorRemoteDTO dto = webClient.get()
                    .uri("/api/trainers/{id}", entrenadorId)
                    .retrieve()
                    .bodyToMono(EntrenadorRemoteDTO.class)
                    .timeout(Duration.ofSeconds(3))
                    .block();
            if (dto == null) {
                return Optional.empty();
            }
            return Optional.of(new EntrenadorInfo(dto.getId(), dto.getNombre(), dto.getEspecialidad()));
        } catch (WebClientResponseException.NotFound e) {
            return Optional.empty();
        } catch (Exception e) {
            // Degradación elegante: si trainer-service no responde, la clase se sigue
            // mostrando pero sin el detalle enriquecido del entrenador.
            return Optional.empty();
        }
    }
}
