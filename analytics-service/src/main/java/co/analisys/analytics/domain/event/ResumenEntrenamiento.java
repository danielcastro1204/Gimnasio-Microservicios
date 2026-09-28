package co.analisys.analytics.domain.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Resultado de la agregacion de Kafka Streams: resumen de entrenamiento de
 * UN miembro dentro de UNA ventana de tiempo (7 dias por defecto). Es el
 * valor que se guarda en el state store "resumen-entrenamiento-store" y el
 * que se publica en el topic "resumen-entrenamiento".
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResumenEntrenamiento {

    private Long miembroId;
    private int totalSesiones;
    private int totalMinutos;
    private int totalCalorias;
    private double promedioFrecuenciaCardiaca;
    private int frecuenciaCardiacaMaxima;
    private LocalDateTime ultimaSesion;
    private LocalDateTime ventanaInicio;
    private LocalDateTime ventanaFin;

    /**
     * Incorpora una sesion al resumen y devuelve el resumen nuevo. Se devuelve
     * una copia (en vez de mutar this) para no alterar objetos que Kafka Streams
     * pueda estar reteniendo en su cache interno.
     */
    public ResumenEntrenamiento actualizar(DatosEntrenamiento dato) {
        ResumenEntrenamiento nuevo = new ResumenEntrenamiento();
        nuevo.miembroId = dato.getMiembroId();
        nuevo.totalSesiones = this.totalSesiones + 1;
        nuevo.totalMinutos = this.totalMinutos + dato.getDuracionMinutos();
        nuevo.totalCalorias = this.totalCalorias + dato.getCaloriasQuemadas();
        nuevo.promedioFrecuenciaCardiaca =
                ((this.promedioFrecuenciaCardiaca * this.totalSesiones) + dato.getFrecuenciaCardiacaPromedio())
                        / nuevo.totalSesiones;
        nuevo.frecuenciaCardiacaMaxima = Math.max(this.frecuenciaCardiacaMaxima, dato.getFrecuenciaCardiacaPromedio());
        nuevo.ultimaSesion = dato.getFecha();
        nuevo.ventanaInicio = this.ventanaInicio;
        nuevo.ventanaFin = this.ventanaFin;
        return nuevo;
    }
}
