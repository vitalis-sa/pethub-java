package fiap.pethub.service;

import fiap.pethub.dto.request.ExameRequest;
import fiap.pethub.dto.response.DeleteResponse;
import fiap.pethub.dto.response.ExameResponse;
import fiap.pethub.entity.Consulta;
import fiap.pethub.entity.Exame;
import fiap.pethub.entity.Pet;
import fiap.pethub.exception.ResourceNotFoundException;
import fiap.pethub.mapper.ExameMapper;
import fiap.pethub.repository.ConsultaRepository;
import fiap.pethub.repository.ExameRepository;
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
public class ExameService {

    private final ExameRepository repository;
    private final ConsultaRepository consultaRepository;
    private final PetRepository petRepository;
    private final ExameMapper mapper;

    public Page<ExameResponse> findAll(Long petId, Long consultaId, Pageable pageable) {
        return Stream.<Map.Entry<Boolean, Supplier<Page<Exame>>>>of(
                Map.entry(petId != null,      () -> repository.findByPetId(petId, pageable)),
                Map.entry(consultaId != null, () -> repository.findByConsultaId(consultaId, pageable))
        )
                .filter(Map.Entry::getKey)
                .findFirst()
                .map(Map.Entry::getValue)
                .map(Supplier::get)
                .orElseGet(() -> repository.findAll(pageable))
                .map(mapper::toResponse);
    }

    @Cacheable(value = "exames", key = "#id")
    public ExameResponse findById(Long id) {
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Exame não encontrado com id: " + id));
    }

    @Transactional
    @CacheEvict(value = "exames", allEntries = true)
    public ExameResponse create(ExameRequest request) {
        Exame entity = buildExameEntity(request);
        return mapper.toResponse(repository.save(entity));
    }

    @Transactional
    @CacheEvict(value = "exames", key = "#id")
    public ExameResponse update(Long id, ExameRequest request) {
        Exame entity = findEntityById(id);
        mapper.updateEntity(request, entity);
        return mapper.toResponse(repository.save(entity));
    }

    @Transactional
    @CacheEvict(value = "exames", key = "#id")
    public DeleteResponse delete(Long id) {
        Optional.ofNullable(repository.findById(id).orElse(null))
                .ifPresentOrElse(
                        repository::delete,
                        () -> { throw new ResourceNotFoundException("Exame não encontrado com id: " + id); }
                );
        return DeleteResponse.of("Exame", id);
    }

    private Exame buildExameEntity(ExameRequest request) {
        Exame entity = mapper.toEntity(request);
        entity.setConsulta(findConsulta(request.getConsultaId()));
        entity.setPet(findPet(request.getPetId()));
        return entity;
    }

    private Consulta findConsulta(Long consultaId) {
        return consultaRepository.findById(consultaId)
                .orElseThrow(() -> new ResourceNotFoundException("Consulta não encontrada com id: " + consultaId));
    }

    private Pet findPet(Long petId) {
        return petRepository.findById(petId)
                .orElseThrow(() -> new ResourceNotFoundException("Pet não encontrado com id: " + petId));
    }

    private Exame findEntityById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exame não encontrado com id: " + id));
    }
}
