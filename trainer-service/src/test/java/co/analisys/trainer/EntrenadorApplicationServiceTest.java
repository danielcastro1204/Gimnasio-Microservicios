package co.analisys.trainer;

import co.analisys.trainer.application.dto.EntrenadorDTO;
import co.analisys.trainer.application.service.EntrenadorApplicationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class EntrenadorApplicationServiceTest {

    @Autowired
    private EntrenadorApplicationService entrenadorApplicationService;

    @Test
    void agregaUnEntrenadorCorrectamente() {
        EntrenadorDTO dto = new EntrenadorDTO(null, "Sofía Torres", "Pilates");
        EntrenadorDTO guardado = entrenadorApplicationService.agregarEntrenador(dto);

        assertNotNull(guardado.getId());
        assertTrue(entrenadorApplicationService.existeEntrenador(guardado.getId()));
    }

    @Test
    void indicaCuandoUnEntrenadorNoExiste() {
        assertFalse(entrenadorApplicationService.existeEntrenador(9999L));
    }
}
