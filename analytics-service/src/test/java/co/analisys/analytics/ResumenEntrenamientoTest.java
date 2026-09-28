package co.analisys.analytics;

import co.analisys.analytics.domain.event.DatosEntrenamiento;
import co.analisys.analytics.domain.event.ResumenEntrenamiento;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/** Prueba unitaria pura (sin Spring ni Kafka) de la logica de agregacion del stream processor. */
class ResumenEntrenamientoTest {

    private DatosEntrenamiento sesion(int minutos, int calorias, int fc) {
        return new DatosEntrenamiento(1L, "Spinning", minutos, calorias, fc, LocalDateTime.of(2026, 9, 28, 18, 0));
    }

    @Test
    void acumulaTotalesPromedioYMaximo() {
        ResumenEntrenamiento r = new ResumenEntrenamiento()
                .actualizar(sesion(30, 300, 120))
                .actualizar(sesion(60, 500, 150));

        assertEquals(1L, r.getMiembroId());
        assertEquals(2, r.getTotalSesiones());
        assertEquals(90, r.getTotalMinutos());
        assertEquals(800, r.getTotalCalorias());
        assertEquals(135.0, r.getPromedioFrecuenciaCardiaca(), 0.0001);
        assertEquals(150, r.getFrecuenciaCardiacaMaxima());
    }

    @Test
    void noMutaElResumenOriginal() {
        ResumenEntrenamiento original = new ResumenEntrenamiento();
        ResumenEntrenamiento nuevo = original.actualizar(sesion(30, 300, 120));

        assertNotSame(original, nuevo);
        assertEquals(0, original.getTotalSesiones());
        assertEquals(1, nuevo.getTotalSesiones());
    }
}
