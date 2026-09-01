package co.analisys.member;

import co.analisys.member.application.dto.MiembroDTO;
import co.analisys.member.application.service.MiembroApplicationService;
import co.analisys.member.infrastructure.exception.BusinessRuleException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MiembroApplicationServiceTest {

    @Autowired
    private MiembroApplicationService miembroApplicationService;

    @Test
    void registraUnMiembroCorrectamente() {
        MiembroDTO dto = new MiembroDTO(null, "Pedro Gómez", "pedro.gomez.test@email.com", LocalDate.now());
        MiembroDTO guardado = miembroApplicationService.registrarMiembro(dto);

        assertNotNull(guardado.getId());
        assertEquals("Pedro Gómez", guardado.getNombre());
    }

    @Test
    void noPermiteEmailsDuplicados() {
        MiembroDTO dto = new MiembroDTO(null, "Laura Ruiz", "laura.duplicado@email.com", LocalDate.now());
        miembroApplicationService.registrarMiembro(dto);

        MiembroDTO duplicado = new MiembroDTO(null, "Laura Ruiz 2", "laura.duplicado@email.com", LocalDate.now());
        assertThrows(BusinessRuleException.class, () -> miembroApplicationService.registrarMiembro(duplicado));
    }

    @Test
    void listaTodosLosMiembros() {
        List<MiembroDTO> miembros = miembroApplicationService.obtenerTodosMiembros();
        assertNotNull(miembros);
    }
}
