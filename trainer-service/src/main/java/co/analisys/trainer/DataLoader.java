package co.analisys.trainer;

import co.analisys.trainer.domain.model.Entrenador;
import co.analisys.trainer.domain.repository.EntrenadorRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final EntrenadorRepository entrenadorRepository;

    public DataLoader(EntrenadorRepository entrenadorRepository) {
        this.entrenadorRepository = entrenadorRepository;
    }

    @Override
    public void run(String... args) {
        Entrenador entrenador1 = new Entrenador();
        entrenador1.setNombre("Carlos Rodríguez");
        entrenador1.setEspecialidad("Yoga");
        entrenadorRepository.save(entrenador1);

        Entrenador entrenador2 = new Entrenador();
        entrenador2.setNombre("Ana Martínez");
        entrenador2.setEspecialidad("Spinning");
        entrenadorRepository.save(entrenador2);

        System.out.println("[trainer-service] Datos de ejemplo cargados exitosamente.");
    }
}
