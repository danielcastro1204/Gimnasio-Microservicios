package co.analisys.analytics.application.service;

import co.analisys.analytics.application.dto.CheckpointDTO;
import co.analisys.analytics.application.dto.EntrenamientoRegistradoDTO;
import co.analisys.analytics.domain.event.DatosEntrenamiento;
import co.analisys.analytics.domain.model.EntrenamientoRegistrado;
import co.analisys.analytics.domain.model.KafkaCheckpoint;
import co.analisys.analytics.domain.repository.EntrenamientoRegistradoRepository;
import co.analisys.analytics.domain.repository.KafkaCheckpointRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Procesa UN registro del log de Kafka y avanza el checkpoint de su
 * particion en la MISMA transaccion: o se guardan ambos o ninguno. Es lo que
 * permite reanudar sin perder ni duplicar datos tras un fallo.
 */
@Service
public class ProcesadorEntrenamientoService {

    private static final Logger log = LoggerFactory.getLogger(ProcesadorEntrenamientoService.class);

    private final EntrenamientoRegistradoRepository entrenamientoRepository;
    private final KafkaCheckpointRepository checkpointRepository;
    private final ObjectMapper objectMapper;

    public ProcesadorEntrenamientoService(EntrenamientoRegistradoRepository entrenamientoRepository,
                                          KafkaCheckpointRepository checkpointRepository,
                                          ObjectMapper objectMapper) {
        this.entrenamientoRepository = entrenamientoRepository;
        this.checkpointRepository = checkpointRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public void procesar(String topic, int particion, long offset, String json) {
        if (!entrenamientoRepository.existsByTopicAndParticionAndOffsetKafka(topic, particion, offset)) {
            try {
                DatosEntrenamiento dato = objectMapper.readValue(json, DatosEntrenamiento.class);
                EntrenamientoRegistrado registro = new EntrenamientoRegistrado();
                registro.setTopic(topic);
                registro.setParticion(particion);
                registro.setOffsetKafka(offset);
                registro.setMiembroId(dato.getMiembroId());
                registro.setEjercicio(dato.getEjercicio());
                registro.setDuracionMinutos(dato.getDuracionMinutos());
                registro.setCaloriasQuemadas(dato.getCaloriasQuemadas());
                registro.setFrecuenciaCardiacaPromedio(dato.getFrecuenciaCardiacaPromedio());
                registro.setFechaEntrenamiento(dato.getFecha());
                registro.setProcesadoEn(LocalDateTime.now());
                entrenamientoRepository.save(registro);
                log.info("[recuperacion] {}-{}@{} -> miembro {} ({})",
                        topic, particion, offset, dato.getMiembroId(), dato.getEjercicio());
            } catch (JsonProcessingException e) {
                // Mensaje corrupto ("poison pill"): reintentarlo para siempre bloquearia la
                // particion. Se registra y se avanza el checkpoint para saltarlo.
                log.warn("[recuperacion] {}-{}@{} no es un JSON valido; se omite: {}",
                        topic, particion, offset, e.getOriginalMessage());
            }
        }
        guardarCheckpoint(topic, particion, offset);
    }

    private void guardarCheckpoint(String topic, int particion, long offset) {
        KafkaCheckpoint checkpoint = checkpointRepository.findByTopicAndParticion(topic, particion)
                .orElseGet(KafkaCheckpoint::new);
        checkpoint.setTopic(topic);
        checkpoint.setParticion(particion);
        checkpoint.setUltimoOffset(offset);
        checkpoint.setActualizado(LocalDateTime.now());
        checkpointRepository.save(checkpoint);
    }

    /** Ultimo offset procesado por particion (vacio si nunca se ha procesado nada). */
    @Transactional(readOnly = true)
    public Map<Integer, Long> cargarCheckpoints(String topic) {
        Map<Integer, Long> resultado = new HashMap<>();
        for (KafkaCheckpoint c : checkpointRepository.findByTopicOrderByParticion(topic)) {
            resultado.put(c.getParticion(), c.getUltimoOffset());
        }
        return resultado;
    }

    @Transactional(readOnly = true)
    public List<CheckpointDTO> listarCheckpoints(String topic) {
        return checkpointRepository.findByTopicOrderByParticion(topic).stream()
                .map(c -> new CheckpointDTO(c.getParticion(), c.getUltimoOffset(), c.getActualizado()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public long contarRegistros() {
        return entrenamientoRepository.count();
    }

    @Transactional(readOnly = true)
    public List<EntrenamientoRegistradoDTO> ultimosRegistros() {
        return entrenamientoRepository.findTop50ByOrderByIdDesc().stream()
                .map(r -> new EntrenamientoRegistradoDTO(r.getParticion(), r.getOffsetKafka(), r.getMiembroId(),
                        r.getEjercicio(), r.getDuracionMinutos(), r.getCaloriasQuemadas(),
                        r.getFechaEntrenamiento(), r.getProcesadoEn()))
                .collect(Collectors.toList());
    }

    /** Simula la perdida total del estado: borra datos y checkpoints. */
    @Transactional
    public void reiniciarEstado() {
        entrenamientoRepository.deleteAllInBatch();
        checkpointRepository.deleteAllInBatch();
    }
}
