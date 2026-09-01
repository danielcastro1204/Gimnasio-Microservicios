package co.analisys.clase.domain.service;

/**
 * Objeto de valor (Value Object) que representa la vista, desde el punto de vista
 * de class-service, de un entrenador que en realidad pertenece a trainer-service.
 * Es la "traducción" local del recurso remoto (anti-corruption layer).
 */
public class EntrenadorInfo {
    private final Long id;
    private final String nombre;
    private final String especialidad;

    public EntrenadorInfo(Long id, String nombre, String especialidad) {
        this.id = id;
        this.nombre = nombre;
        this.especialidad = especialidad;
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getEspecialidad() { return especialidad; }
}
