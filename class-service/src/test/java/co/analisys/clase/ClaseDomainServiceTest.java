package co.analisys.clase;

import co.analisys.clase.domain.model.Clase;
import co.analisys.clase.domain.service.ClaseDomainService;
import co.analisys.clase.domain.service.EntrenadorInfo;
import co.analisys.clase.domain.service.EntrenadorVerificationPort;
import co.analisys.clase.infrastructure.exception.BusinessRuleException;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Prueba unitaria pura de dominio: usa una implementación falsa (fake) del
 * puerto EntrenadorVerificationPort, sin levantar contexto de Spring ni red.
 */
class ClaseDomainServiceTest {

    private final EntrenadorVerificationPort portQueSiExiste = new EntrenadorVerificationPort() {
        @Override
        public boolean existeEntrenador(Long entrenadorId) { return true; }
        @Override
        public Optional<EntrenadorInfo> obtenerEntrenador(Long entrenadorId) {
            return Optional.of(new EntrenadorInfo(entrenadorId, "Entrenador Fake", "Fitness"));
        }
    };

    private final EntrenadorVerificationPort portQueNoExiste = new EntrenadorVerificationPort() {
        @Override
        public boolean existeEntrenador(Long entrenadorId) { return false; }
        @Override
        public Optional<EntrenadorInfo> obtenerEntrenador(Long entrenadorId) { return Optional.empty(); }
    };

    @Test
    void permiteProgramarClaseSiElEntrenadorExiste() {
        ClaseDomainService service = new ClaseDomainService(portQueSiExiste);
        Clase clase = new Clase(null, "Crossfit", LocalDateTime.now().plusDays(1), 10, 1L);
        assertDoesNotThrow(() -> service.validarProgramacion(clase));
    }

    @Test
    void rechazaClaseSiElEntrenadorNoExisteEnTrainerService() {
        ClaseDomainService service = new ClaseDomainService(portQueNoExiste);
        Clase clase = new Clase(null, "Crossfit", LocalDateTime.now().plusDays(1), 10, 999L);
        assertThrows(BusinessRuleException.class, () -> service.validarProgramacion(clase));
    }

    @Test
    void rechazaClaseConCapacidadInvalida() {
        ClaseDomainService service = new ClaseDomainService(portQueSiExiste);
        Clase clase = new Clase(null, "Crossfit", LocalDateTime.now().plusDays(1), 0, 1L);
        assertThrows(BusinessRuleException.class, () -> service.validarProgramacion(clase));
    }
}
