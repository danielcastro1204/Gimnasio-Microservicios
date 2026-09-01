package co.analisys.clase.domain.service;

import java.util.Optional;

/**
 * Puerto de dominio (patrón hexagonal): define lo que class-service necesita saber
 * de un entrenador SIN acoplarse a cómo se obtiene esa información.
 * La implementación real (adaptador REST) vive en infrastructure.client.TrainerClient.
 */
public interface EntrenadorVerificationPort {

    boolean existeEntrenador(Long entrenadorId);

    Optional<EntrenadorInfo> obtenerEntrenador(Long entrenadorId);
}
