package co.analisys.clase.domain.service;

import co.analisys.clase.domain.model.Clase;
import co.analisys.clase.infrastructure.exception.BusinessRuleException;
import org.springframework.stereotype.Service;

/**
 * Servicio de dominio: reglas de negocio del agregado Clase.
 * La verificación de existencia del entrenador se delega al puerto
 * EntrenadorVerificationPort, que a nivel de infraestructura se resuelve vía REST.
 */
@Service
public class ClaseDomainService {

    private final EntrenadorVerificationPort entrenadorVerificationPort;

    public ClaseDomainService(EntrenadorVerificationPort entrenadorVerificationPort) {
        this.entrenadorVerificationPort = entrenadorVerificationPort;
    }

    public void validarProgramacion(Clase clase) {
        if (clase.getCapacidadMaxima() <= 0) {
            throw new BusinessRuleException("La capacidad máxima debe ser mayor a 0");
        }
        boolean existe = entrenadorVerificationPort.existeEntrenador(clase.getEntrenadorId());
        if (!existe) {
            throw new BusinessRuleException(
                    "No se puede programar la clase: no existe un entrenador con id " + clase.getEntrenadorId());
        }
    }

    /** Las mismas reglas de negocio de la programación aplican al reprogramar una clase existente. */
    public void validarActualizacion(Clase clase) {
        validarProgramacion(clase);
    }
}
