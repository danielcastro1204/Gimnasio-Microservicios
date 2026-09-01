package co.analisys.clase.infrastructure.exception;

/**
 * Se lanza cuando trainer-service no está disponible o responde con error
 * al intentar validar/enriquecer un entrenador.
 */
public class ServiceUnavailableException extends RuntimeException {
    public ServiceUnavailableException(String message) {
        super(message);
    }
}
