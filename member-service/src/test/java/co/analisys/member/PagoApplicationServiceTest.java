package co.analisys.member;

import co.analisys.member.application.dto.CrearPagoRequest;
import co.analisys.member.application.dto.MiembroDTO;
import co.analisys.member.application.dto.PagoDTO;
import co.analisys.member.application.service.MiembroApplicationService;
import co.analisys.member.application.service.PagoApplicationService;
import co.analisys.member.domain.model.EstadoPago;
import co.analisys.member.domain.service.PagoEventPublisherPort;
import co.analisys.member.infrastructure.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Se mockea PagoEventPublisherPort para probar solo la lógica de aplicación
 * (crear el registro en PENDIENTE, validar que el miembro exista) sin
 * depender de que RabbitMQ esté corriendo. El procesamiento real de
 * PagoConsumer/PagoDeadLetterConsumer se prueba manualmente end-to-end con
 * docker compose levantado (ver README).
 */
@SpringBootTest
@Transactional
class PagoApplicationServiceTest {

    @Autowired
    private PagoApplicationService pagoApplicationService;

    @Autowired
    private MiembroApplicationService miembroApplicationService;

    @MockBean
    private PagoEventPublisherPort pagoEventPublisherPort;

    @Test
    void registraUnPagoPendienteYPublicaElEvento() {
        MiembroDTO miembro = miembroApplicationService.registrarMiembro(
                new MiembroDTO(null, "Cliente Pagos Test", "pagos.test@email.com", LocalDate.now()));

        CrearPagoRequest request = new CrearPagoRequest(new BigDecimal("150000"), false);
        PagoDTO pago = pagoApplicationService.registrarPago(miembro.getId(), request);

        assertNotNull(pago.getId());
        assertEquals(EstadoPago.PENDIENTE, pago.getEstado());
        verify(pagoEventPublisherPort).publicarPagoPendiente(pago.getId(), miembro.getId(), false);
    }

    @Test
    void rechazaPagoDeMiembroInexistente() {
        CrearPagoRequest request = new CrearPagoRequest(new BigDecimal("50000"), false);
        assertThrows(ResourceNotFoundException.class,
                () -> pagoApplicationService.registrarPago(999999L, request));
        verifyNoInteractions(pagoEventPublisherPort);
    }

    @Test
    void marcarExitosoYFallidoActualizanElEstado() {
        MiembroDTO miembro = miembroApplicationService.registrarMiembro(
                new MiembroDTO(null, "Cliente Pagos Test 2", "pagos.test2@email.com", LocalDate.now()));
        PagoDTO pago = pagoApplicationService.registrarPago(miembro.getId(),
                new CrearPagoRequest(new BigDecimal("80000"), false));

        pagoApplicationService.marcarExitoso(pago.getId());
        assertEquals(EstadoPago.EXITOSO, pagoApplicationService.obtenerPagoPorId(pago.getId()).getEstado());

        pagoApplicationService.marcarFallido(pago.getId(), "motivo de prueba");
        PagoDTO actualizado = pagoApplicationService.obtenerPagoPorId(pago.getId());
        assertEquals(EstadoPago.FALLIDO, actualizado.getEstado());
        assertEquals("motivo de prueba", actualizado.getDetalle());
    }
}
