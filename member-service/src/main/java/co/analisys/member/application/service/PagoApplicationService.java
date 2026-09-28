package co.analisys.member.application.service;

import co.analisys.member.application.dto.CrearPagoRequest;
import co.analisys.member.application.dto.PagoDTO;
import co.analisys.member.domain.model.EstadoPago;
import co.analisys.member.domain.model.Pago;
import co.analisys.member.domain.repository.MiembroRepository;
import co.analisys.member.domain.repository.PagoRepository;
import co.analisys.member.domain.service.PagoEventPublisherPort;
import co.analisys.member.infrastructure.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Orquesta el caso de uso "registrar un pago de membresía". La creación del
 * Pago (estado PENDIENTE) y el procesamiento real (EXITOSO/FALLIDO) están
 * deliberadamente desacoplados: este servicio solo guarda el pago y publica
 * el evento; quien decide el resultado final es PagoConsumer / PagoDeadLetterConsumer,
 * en otro hilo y en otro momento, consumiendo "pagos-queue" / "pagos-dlq".
 */
@Service
public class PagoApplicationService {

    private final PagoRepository pagoRepository;
    private final MiembroRepository miembroRepository;
    private final PagoEventPublisherPort pagoEventPublisherPort;

    public PagoApplicationService(PagoRepository pagoRepository,
                                   MiembroRepository miembroRepository,
                                   PagoEventPublisherPort pagoEventPublisherPort) {
        this.pagoRepository = pagoRepository;
        this.miembroRepository = miembroRepository;
        this.pagoEventPublisherPort = pagoEventPublisherPort;
    }

    @Transactional
    public PagoDTO registrarPago(Long miembroId, CrearPagoRequest request) {
        if (!miembroRepository.existsById(miembroId)) {
            throw new ResourceNotFoundException("No existe un miembro con id: " + miembroId);
        }
        Pago pago = new Pago();
        pago.setMiembroId(miembroId);
        pago.setMonto(request.getMonto());
        pago.setEstado(EstadoPago.PENDIENTE);
        Pago guardado = pagoRepository.save(pago);

        pagoEventPublisherPort.publicarPagoPendiente(guardado.getId(), miembroId, request.isSimularFallo());

        return toDTO(guardado);
    }

    public List<PagoDTO> obtenerTodosPagos() {
        return pagoRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public PagoDTO obtenerPagoPorId(Long id) {
        return toDTO(buscarPagoOFallar(id));
    }

    /** Usado por PagoConsumer: marca el pago como procesado con éxito. */
    @Transactional
    public void marcarExitoso(Long pagoId) {
        Pago pago = buscarPagoOFallar(pagoId);
        pago.setEstado(EstadoPago.EXITOSO);
        pago.setFechaProcesado(LocalDateTime.now());
        pagoRepository.save(pago);
    }

    /** Usado por PagoDeadLetterConsumer: el mensaje llegó a pagos-dlq. */
    @Transactional
    public void marcarFallido(Long pagoId, String detalle) {
        Pago pago = buscarPagoOFallar(pagoId);
        pago.setEstado(EstadoPago.FALLIDO);
        pago.setFechaProcesado(LocalDateTime.now());
        pago.setDetalle(detalle);
        pagoRepository.save(pago);
    }

    private Pago buscarPagoOFallar(Long id) {
        return pagoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un pago con id: " + id));
    }

    private PagoDTO toDTO(Pago pago) {
        return new PagoDTO(pago.getId(), pago.getMiembroId(), pago.getMonto(), pago.getEstado(),
                pago.getFechaCreacion(), pago.getFechaProcesado(), pago.getDetalle());
    }
}
