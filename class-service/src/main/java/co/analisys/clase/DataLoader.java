package co.analisys.clase;

import co.analisys.clase.domain.model.Clase;
import co.analisys.clase.domain.repository.ClaseRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Carga datos de ejemplo equivalentes a los del monolito original.
 * NOTA: se referencian los entrenadores 1 (Carlos Rodríguez - Yoga) y
 * 2 (Ana Martínez - Spinning) por convención, ya que esos son los ids que
 * carga el DataLoader de trainer-service. En un entorno real, el registro
 * de datos de ejemplo entre servicios se coordinaría con eventos o scripts
 * de arranque; para el alcance de este taller se documenta esta suposición.
 */
@Component
public class DataLoader implements CommandLineRunner {

    private final ClaseRepository claseRepository;

    public DataLoader(ClaseRepository claseRepository) {
        this.claseRepository = claseRepository;
    }

    @Override
    public void run(String... args) {
        Clase clase1 = new Clase();
        clase1.setNombre("Yoga Matutino");
        clase1.setHorario(LocalDateTime.now().plusDays(1).withHour(8).withMinute(0));
        clase1.setCapacidadMaxima(20);
        clase1.setEntrenadorId(1L);
        claseRepository.save(clase1);

        Clase clase2 = new Clase();
        clase2.setNombre("Spinning Vespertino");
        clase2.setHorario(LocalDateTime.now().plusDays(1).withHour(18).withMinute(0));
        clase2.setCapacidadMaxima(15);
        clase2.setEntrenadorId(2L);
        claseRepository.save(clase2);

        System.out.println("[class-service] Datos de ejemplo cargados exitosamente.");
    }
}
