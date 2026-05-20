package fiap.pethub.service;

import fiap.pethub.dto.request.LeituraWearableRequest;
import fiap.pethub.dto.response.DeleteResponse;
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

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class LeituraWearableService {

    private final LeituraWearableRepository repository;
    private final PetRepository petRepository;
    private final LeituraWearableMapper mapper;

    public Page<LeituraWearableResponse> findAll(Long petId, Boolean apenasAnomalias, Pageable pageable) {
        return Stream.<Map.Entry<Boolean, Supplier<Page<LeituraWearable>>>>of(
                Map.entry(petId != null && Boolean.TRUE.equals(apenasAnomalias), () -> repository.findByPetIdAndAnomaliaDetectadaTrue(petId, pageable)),
                Map.entry(petId != null, () -> repository.findByPetId(petId, pageable))
        )
                .filter(Map.Entry::getKey)
                .findFirst()
                .map(Map.Entry::getValue)
                .map(Supplier::get)
                .orElseGet(() -> repository.findAll(pageable))
                .map(mapper::toResponse);
    }

    @Cacheable(value = "leituras", key = "#id")
    public LeituraWearableResponse findById(Long id) {
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Leitura não encontrada com id: " + id));
    }

    @Transactional
    @CacheEvict(value = "leituras", allEntries = true)
    public LeituraWearableResponse create(LeituraWearableRequest request) {
        LeituraWearable entity = buildLeituraEntity(request);
        return mapper.toResponse(repository.save(entity));
    }

    @Transactional
    @CacheEvict(value = "leituras", key = "#id")
    public LeituraWearableResponse update(Long id, LeituraWearableRequest request) {
        LeituraWearable entity = findEntityById(id);
        mapper.updateEntity(request, entity);
        return mapper.toResponse(repository.save(entity));
    }

    @Transactional
    @CacheEvict(value = "leituras", key = "#id")
    public DeleteResponse delete(Long id) {
        repository.findById(id)
                .ifPresentOrElse(
                        repository::delete,
                        () -> { throw new ResourceNotFoundException("Leitura não encontrada com id: " + id); }
                );
        return DeleteResponse.of("Leitura wearable", id);
    }

    private LeituraWearable buildLeituraEntity(LeituraWearableRequest request) {
        LeituraWearable entity = mapper.toEntity(request);
        entity.setPet(findPet(request.getPetId()));
        entity.setAnomaliaDetectada(entity.getAnomaliaDetectada() != null ? entity.getAnomaliaDetectada() : false);
        return entity;
    }

    private Pet findPet(Long petId) {
        return petRepository.findById(petId)
                .orElseThrow(() -> new ResourceNotFoundException("Pet não encontrado com id: " + petId));
    }

    private LeituraWearable findEntityById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leitura não encontrada com id: " + id));
    }
}

