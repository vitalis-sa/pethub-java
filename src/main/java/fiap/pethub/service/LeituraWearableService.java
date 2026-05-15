package fiap.pethub.service;

import fiap.pethub.dto.request.LeituraWearableRequest;
import fiap.pethub.dto.response.LeituraWearableResponse;
import fiap.pethub.entity.LeituraWearable;
import fiap.pethub.entity.Pet;
import fiap.pethub.exception.ResourceNotFoundException;
import fiap.pethub.mapper.LeituraWearableMapper;
import fiap.pethub.repository.LeituraWearableRepository;
import fiap.pethub.repository.PetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LeituraWearableService {

    private final LeituraWearableRepository repository;
    private final PetRepository petRepository;
    private final LeituraWearableMapper mapper;

    public Page<LeituraWearableResponse> findAll(Long petId, Boolean apenasAnomalias, Pageable pageable) {
        if (petId != null && Boolean.TRUE.equals(apenasAnomalias)) {
            return repository.findByPetIdAndAnomaliaDetectadaTrue(petId, pageable).map(mapper::toResponse);
        }
        if (petId != null) return repository.findByPetId(petId, pageable).map(mapper::toResponse);
        return repository.findAll(pageable).map(mapper::toResponse);
    }

    @Cacheable(value = "leituras", key = "#id")
    public LeituraWearableResponse findById(Long id) {
        return mapper.toResponse(findEntityById(id));
    }

    @Transactional
    @CacheEvict(value = "leituras", allEntries = true)
    public LeituraWearableResponse create(LeituraWearableRequest request) {
        Pet pet = petRepository.findById(request.getPetId())
                .orElseThrow(() -> new ResourceNotFoundException("Pet não encontrado com id: " + request.getPetId()));
        LeituraWearable entity = mapper.toEntity(request);
        entity.setPet(pet);
        if (entity.getAnomaliaDetectada() == null) entity.setAnomaliaDetectada(false);
        return mapper.toResponse(repository.save(entity));
    }

    @Transactional
    @CacheEvict(value = "leituras", key = "#id")
    public void delete(Long id) {
        if (!repository.existsById(id)) throw new ResourceNotFoundException("Leitura não encontrada com id: " + id);
        repository.deleteById(id);
    }

    private LeituraWearable findEntityById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leitura não encontrada com id: " + id));
    }
}

