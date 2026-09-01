package co.analisys.equipment;

import co.analisys.equipment.application.dto.EquipoDTO;
import co.analisys.equipment.application.service.EquipoApplicationService;
import co.analisys.equipment.infrastructure.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class EquipoApplicationServiceTest {

    @Autowired
    private EquipoApplicationService equipoApplicationService;

    @Test
    void agregaUnEquipoCorrectamente() {
        EquipoDTO dto = new EquipoDTO(null, "Kettlebell", "Set de kettlebells 10kg", 10);
        EquipoDTO guardado = equipoApplicationService.agregarEquipo(dto);

        assertNotNull(guardado.getId());
        assertEquals(10, guardado.getCantidad());
    }

    @Test
    void lanzaExcepcionSiElEquipoNoExiste() {
        assertThrows(ResourceNotFoundException.class, () -> equipoApplicationService.obtenerEquipoPorId(9999L));
    }
}
