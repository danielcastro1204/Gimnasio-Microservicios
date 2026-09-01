package co.analisys.member.domain.service;

import co.analisys.member.domain.model.Miembro;
import co.analisys.member.domain.repository.MiembroRepository;
import co.analisys.member.infrastructure.exception.BusinessRuleException;
import org.springframework.stereotype.Service;

@Service
public class MiembroDomainService {

    private final MiembroRepository miembroRepository;

    public MiembroDomainService(MiembroRepository miembroRepository) {
        this.miembroRepository = miembroRepository;
    }

    public void validarEmailUnico(String email) {
        if (miembroRepository.findByEmail(email).isPresent()) {
            throw new BusinessRuleException("Ya existe un miembro registrado con el email: " + email);
        }
    }
}
