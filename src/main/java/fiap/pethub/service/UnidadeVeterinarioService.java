package fiap.pethub.service;

import fiap.pethub.dto.request.UnidadeVeterinarioRequest;
import fiap.pethub.dto.response.DeleteResponse;
import fiap.pethub.dto.response.UnidadeVeterinarioResponse;
import fiap.pethub.entity.UnidadeVeterinario;
import fiap.pethub.exception.ResourceNotFoundException;
import fiap.pethub.mapper.UnidadeVeterinarioMapper;
import fiap.pethub.repository.UnidadeVeterinarioRepository;
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
public class UnidadeVeterinarioService {

    private final UnidadeVeterinarioRepository repository;
    private final VeterinarioRepository veterinarioRepository;
    private final UnidadeVeterinarioMapper mapper;

    public Page<UnidadeVeterinarioResponse> findAll(Long veterinarioId, Pageable pageable) {
        return Stream.<Map.Entry<Boolean, Supplier<Page<UnidadeVeterinario>>>>of(
                Map.entry(veterinarioId != null, () -> repository.findByVeterinarioId(veterinarioId, pageable))
        )
                .filter(Map.Entry::getKey)
                .findFirst()
                .map(Map.Entry::getValue)
                .map(Supplier::get)
                .orElseGet(() -> repository.findAll(pageable))
                .map(mapper::toResponse);
    }

    @Cacheable(value = "unidades", key = "#id")
    public UnidadeVeterinarioResponse findById(Long id) {
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Unidade não encontrada com id: " + id));
    }

    @Transactional
    @CacheEvict(value = "unidades", allEntries = true)
    public UnidadeVeterinarioResponse create(UnidadeVeterinarioRequest request) {
        UnidadeVeterinario entity = buildUnidadeEntity(request);
        return mapper.toResponse(repository.save(entity));
    }

    @Transactional
    @CacheEvict(value = "unidades", key = "#id")
    public UnidadeVeterinarioResponse update(Long id, UnidadeVeterinarioRequest request) {
        UnidadeVeterinario entity = findEntityById(id);
        mapper.updateEntity(request, entity);
        applyVeterinario(request.getVeterinarioId(), entity);
        return mapper.toResponse(repository.save(entity));
    }

    @Transactional
    @CacheEvict(value = "unidades", key = "#id")
    public DeleteResponse delete(Long id) {
        repository.findById(id)
                .ifPresentOrElse(
                        repository::delete,
                        () -> { throw new ResourceNotFoundException("Unidade não encontrada com id: " + id); }
                );
        return DeleteResponse.of("Unidade veterinária", id);
    }

    private UnidadeVeterinario buildUnidadeEntity(UnidadeVeterinarioRequest request) {
        UnidadeVeterinario entity = mapper.toEntity(request);
        applyVeterinario(request.getVeterinarioId(), entity);
        return entity;
    }

    private void applyVeterinario(Long veterinarioId, UnidadeVeterinario entity) {
        Optional.ofNullable(veterinarioId)
                .map(vid -> veterinarioRepository.findById(vid)
                        .orElseThrow(() -> new ResourceNotFoundException("Veterinário não encontrado com id: " + vid)))
                .ifPresent(entity::setVeterinario);
    }

    private UnidadeVeterinario findEntityById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Unidade não encontrada com id: " + id));
    }
}
