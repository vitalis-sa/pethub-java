package fiap.pethub.service;

import fiap.pethub.dto.request.PetRequest;
import fiap.pethub.dto.response.PetResponse;
import fiap.pethub.entity.Pet;
import fiap.pethub.entity.Tutor;
import fiap.pethub.entity.Veterinario;
import fiap.pethub.exception.ResourceNotFoundException;
import fiap.pethub.mapper.PetMapper;
import fiap.pethub.repository.PetRepository;
import fiap.pethub.repository.TutorRepository;
import fiap.pethub.repository.VeterinarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PetService {

    private final PetRepository repository;
    private final TutorRepository tutorRepository;
    private final VeterinarioRepository veterinarioRepository;
    private final PetMapper mapper;

    public Page<PetResponse> findAll(String nome, Long veterinarioId, Pageable pageable) {
        if (nome != null) {
            return repository.findByNomeContainingIgnoreCase(nome, pageable).map(mapper::toResponse);
        }
        if (veterinarioId != null) {
            return repository.findByVeterinarioResponsavelId(veterinarioId, pageable).map(mapper::toResponse);
        }
        return repository.findAll(pageable).map(mapper::toResponse);
    }

    public Page<PetResponse> findByTutorCpf(String cpf, Pageable pageable) {
        Tutor tutor = tutorRepository.findByCpf(cpf)
                .orElseThrow(() -> new ResourceNotFoundException("Tutor não encontrado com CPF: " + cpf));
        return repository.findByTutorId(tutor.getId(), pageable).map(mapper::toResponse);
    }

    @Cacheable(value = "pets", key = "#id")
    public PetResponse findById(Long id) {
        return mapper.toResponse(findEntityById(id));
    }

    @Transactional
    @CacheEvict(value = "pets", allEntries = true)
    public PetResponse create(PetRequest request) {
        Tutor tutor = tutorRepository.findByCpf(request.getTutorCpf())
                .orElseThrow(() -> new ResourceNotFoundException("Tutor não encontrado com CPF: " + request.getTutorCpf()));
        Pet entity = mapper.toEntity(request);
        entity.setTutor(tutor);
        if (request.getVeterinarioResponsavelId() != null) {
            Veterinario vet = veterinarioRepository.findById(request.getVeterinarioResponsavelId())
                    .orElseThrow(() -> new ResourceNotFoundException("Veterinário não encontrado com id: " + request.getVeterinarioResponsavelId()));
            entity.setVeterinarioResponsavel(vet);
        }
        return mapper.toResponse(repository.save(entity));
    }

    @Transactional
    @CacheEvict(value = "pets", key = "#id")
    public PetResponse update(Long id, PetRequest request) {
        Pet entity = findEntityById(id);
        mapper.updateEntity(request, entity);
        if (request.getTutorCpf() != null) {
            Tutor tutor = tutorRepository.findByCpf(request.getTutorCpf())
                    .orElseThrow(() -> new ResourceNotFoundException("Tutor não encontrado com CPF: " + request.getTutorCpf()));
            entity.setTutor(tutor);
        }
        if (request.getVeterinarioResponsavelId() != null) {
            Veterinario vet = veterinarioRepository.findById(request.getVeterinarioResponsavelId())
                    .orElseThrow(() -> new ResourceNotFoundException("Veterinário não encontrado com id: " + request.getVeterinarioResponsavelId()));
            entity.setVeterinarioResponsavel(vet);
        }
        return mapper.toResponse(repository.save(entity));
    }

    @Transactional
    @CacheEvict(value = "pets", key = "#id")
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Pet não encontrado com id: " + id);
        }
        repository.deleteById(id);
    }

    private Pet findEntityById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pet não encontrado com id: " + id));
    }
}

