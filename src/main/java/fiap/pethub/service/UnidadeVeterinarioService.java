package fiap.pethub.service;

import fiap.pethub.dto.request.UnidadeVeterinarioRequest;
import fiap.pethub.dto.response.UnidadeVeterinarioResponse;
import fiap.pethub.entity.UnidadeVeterinario;
import fiap.pethub.entity.Veterinario;
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

@Service
@RequiredArgsConstructor
public class UnidadeVeterinarioService {

    private final UnidadeVeterinarioRepository repository;
    private final VeterinarioRepository veterinarioRepository;
    private final UnidadeVeterinarioMapper mapper;

    public Page<UnidadeVeterinarioResponse> findAll(Long veterinarioId, Pageable pageable) {
        if (veterinarioId != null) {
            return repository.findByVeterinarioId(veterinarioId, pageable).map(mapper::toResponse);
        }
        return repository.findAll(pageable).map(mapper::toResponse);
    }

    @Cacheable(value = "unidades", key = "#id")
    public UnidadeVeterinarioResponse findById(Long id) {
        return mapper.toResponse(findEntityById(id));
    }

    @Transactional
    @CacheEvict(value = "unidades", allEntries = true)
    public UnidadeVeterinarioResponse create(UnidadeVeterinarioRequest request) {
        Veterinario veterinario = veterinarioRepository.findById(request.getVeterinarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Veterinário não encontrado com id: " + request.getVeterinarioId()));
        UnidadeVeterinario entity = mapper.toEntity(request);
        entity.setVeterinario(veterinario);
        return mapper.toResponse(repository.save(entity));
    }

    @Transactional
    @CacheEvict(value = "unidades", key = "#id")
    public UnidadeVeterinarioResponse update(Long id, UnidadeVeterinarioRequest request) {
        UnidadeVeterinario entity = findEntityById(id);
        mapper.updateEntity(request, entity);
        if (request.getVeterinarioId() != null) {
            Veterinario veterinario = veterinarioRepository.findById(request.getVeterinarioId())
                    .orElseThrow(() -> new ResourceNotFoundException("Veterinário não encontrado com id: " + request.getVeterinarioId()));
            entity.setVeterinario(veterinario);
        }
        return mapper.toResponse(repository.save(entity));
    }

    @Transactional
    @CacheEvict(value = "unidades", key = "#id")
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Unidade não encontrada com id: " + id);
        }
        repository.deleteById(id);
    }

    private UnidadeVeterinario findEntityById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Unidade não encontrada com id: " + id));
    }
}

