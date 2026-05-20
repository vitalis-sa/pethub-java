package fiap.pethub.service;

import fiap.pethub.dto.request.PetRequest;
import fiap.pethub.dto.response.DeleteResponse;
import fiap.pethub.dto.response.PetResponse;
import fiap.pethub.entity.Pet;
import fiap.pethub.entity.Tutor;
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

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class PetService {

    private final PetRepository repository;
    private final TutorRepository tutorRepository;
    private final VeterinarioRepository veterinarioRepository;
    private final PetMapper mapper;

    public Page<PetResponse> findAll(String nome, Long veterinarioId, Pageable pageable) {
        return Stream.<Map.Entry<Boolean, Supplier<Page<Pet>>>>of(
                Map.entry(nome != null,           () -> repository.findByNomeContainingIgnoreCase(nome, pageable)),
                Map.entry(veterinarioId != null,  () -> repository.findByVeterinarioResponsavelId(veterinarioId, pageable))
        )
                .filter(Map.Entry::getKey)
                .findFirst()
                .map(Map.Entry::getValue)
                .map(Supplier::get)
                .orElseGet(() -> repository.findAll(pageable))
                .map(mapper::toResponse);
    }

    public Page<PetResponse> findByTutorCpf(String cpf, Pageable pageable) {
        return repository.findById(findTutorByCpf(cpf).getId())
                .map(pet -> repository.findByTutorId(pet.getTutor().getId(), pageable))
                .orElseGet(() -> repository.findByTutorId(findTutorByCpf(cpf).getId(), pageable))
                .map(mapper::toResponse);
    }

    @Cacheable(value = "pets", key = "#id")
    public PetResponse findById(Long id) {
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Pet não encontrado com id: " + id));
    }

    @Transactional
    @CacheEvict(value = "pets", allEntries = true)
    public PetResponse create(PetRequest request) {
        Pet entity = buildPetEntity(request);
        return mapper.toResponse(repository.save(entity));
    }

    @Transactional
    @CacheEvict(value = "pets", key = "#id")
    public PetResponse update(Long id, PetRequest request) {
        Pet entity = findEntityById(id);
        mapper.updateEntity(request, entity);
        applyTutor(request.getTutorCpf(), entity);
        applyVeterinario(request.getVeterinarioResponsavelId(), entity);
        return mapper.toResponse(repository.save(entity));
    }

    @Transactional
    @CacheEvict(value = "pets", key = "#id")
    public DeleteResponse delete(Long id) {
        repository.findById(id)
                .ifPresentOrElse(
                        repository::delete,
                        () -> { throw new ResourceNotFoundException("Pet não encontrado com id: " + id); }
                );
        return DeleteResponse.of("Pet", id);
    }

    private Pet buildPetEntity(PetRequest request) {
        Pet entity = mapper.toEntity(request);
        entity.setTutor(findTutorByCpf(request.getTutorCpf()));
        applyVeterinario(request.getVeterinarioResponsavelId(), entity);
        return entity;
    }

    private void applyTutor(String cpf, Pet entity) {
        Optional.ofNullable(cpf)
                .map(c -> tutorRepository.findByCpf(c)
                        .orElseThrow(() -> new ResourceNotFoundException("Tutor não encontrado com CPF: " + c)))
                .ifPresent(entity::setTutor);
    }

    private void applyVeterinario(Long veterinarioId, Pet entity) {
        Optional.ofNullable(veterinarioId)
                .map(vid -> veterinarioRepository.findById(vid)
                        .orElseThrow(() -> new ResourceNotFoundException("Veterinário não encontrado com id: " + vid)))
                .ifPresent(entity::setVeterinarioResponsavel);
    }

    private Tutor findTutorByCpf(String cpf) {
        return tutorRepository.findByCpf(cpf)
                .orElseThrow(() -> new ResourceNotFoundException("Tutor não encontrado com CPF: " + cpf));
    }

    private Pet findEntityById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pet não encontrado com id: " + id));
    }
}

