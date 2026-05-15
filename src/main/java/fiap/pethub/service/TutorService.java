package fiap.pethub.service;

import fiap.pethub.dto.response.TutorResponse;
import fiap.pethub.exception.ResourceNotFoundException;
import fiap.pethub.mapper.TutorMapper;
import fiap.pethub.repository.TutorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TutorService {

    private final TutorRepository repository;
    private final TutorMapper mapper;

    @Cacheable(value = "tutores", key = "#cpf")
    public TutorResponse findByCpf(String cpf) {
        return repository.findByCpf(cpf)
                .map(mapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Tutor não encontrado com CPF: " + cpf));
    }
}

