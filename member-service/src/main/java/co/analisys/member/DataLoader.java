package co.analisys.member;

import co.analisys.member.domain.model.Miembro;
import co.analisys.member.domain.repository.MiembroRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Carga datos de ejemplo equivalentes a los del monolito original.
 */
@Component
public class DataLoader implements CommandLineRunner {

    private final MiembroRepository miembroRepository;

    public DataLoader(MiembroRepository miembroRepository) {
        this.miembroRepository = miembroRepository;
    }

    @Override
    public void run(String... args) {
        Miembro miembro1 = new Miembro();
        miembro1.setNombre("Juan Pérez");
        miembro1.setEmail("juan@email.com");
        miembro1.setFechaInscripcion(LocalDate.now());
        miembroRepository.save(miembro1);

        Miembro miembro2 = new Miembro();
        miembro2.setNombre("María López");
        miembro2.setEmail("maria@email.com");
        miembro2.setFechaInscripcion(LocalDate.now().minusDays(30));
        miembroRepository.save(miembro2);

        System.out.println("[member-service] Datos de ejemplo cargados exitosamente.");
    }
}
