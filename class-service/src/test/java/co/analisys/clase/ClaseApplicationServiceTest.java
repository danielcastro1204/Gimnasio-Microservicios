package co.analisys.clase;

import co.analisys.clase.application.dto.ClaseDTO;
import co.analisys.clase.application.service.ClaseApplicationService;
import co.analisys.clase.domain.service.EntrenadorInfo;
import co.analisys.clase.domain.service.EntrenadorVerificationPort;
import co.analisys.clase.infrastructure.exception.BusinessRuleException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

/**
 * Prueba de integración del caso de uso "programar clase".
 * Se reemplaza (mock) el adaptador REST hacia trainer-service para no depender
 * de que ese microservicio esté físicamente levantado durante el build.
 */
@SpringBootTest
@Transactional
class ClaseApplicationServiceTest {

    @Autowired
    private ClaseApplicationService claseApplicationService;

    @MockBean
    private EntrenadorVerificationPort entrenadorVerificationPort;

    @Test
    void programaUnaClaseCuandoElEntrenadorExisteRemotamente() {
        when(entrenadorVerificationPort.existeEntrenador(anyLong())).thenReturn(true);
        when(entrenadorVerificationPort.obtenerEntrenador(anyLong()))
                .thenReturn(Optional.of(new EntrenadorInfo(1L, "Carlos Rodríguez", "Yoga")));

        ClaseDTO dto = new ClaseDTO(null, "Zumba", LocalDateTime.now().plusDays(2), 25, 1L);
        ClaseDTO guardada = claseApplicationService.programarClase(dto);

        assertNotNull(guardada.getId());
        assertNotNull(guardada.getEntrenador());
        assertEquals("Carlos Rodríguez", guardada.getEntrenador().getNombre());
    }

    @Test
    void rechazaLaClaseCuandoElEntrenadorNoExisteRemotamente() {
        when(entrenadorVerificationPort.existeEntrenador(anyLong())).thenReturn(false);

        ClaseDTO dto = new ClaseDTO(null, "Boxeo", LocalDateTime.now().plusDays(2), 10, 999L);
        assertThrows(BusinessRuleException.class, () -> claseApplicationService.programarClase(dto));
    }
}
