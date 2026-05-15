package fiap.pethub.service;

import fiap.pethub.config.PasswordUtil;
import fiap.pethub.dto.request.VeterinarioRequest;
import fiap.pethub.dto.response.VeterinarioResponse;
import fiap.pethub.entity.Veterinario;
import fiap.pethub.exception.ResourceNotFoundException;
import fiap.pethub.mapper.VeterinarioMapper;
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
public class VeterinarioService {

    private final VeterinarioRepository repository;
    private final VeterinarioMapper mapper;

    public Page<VeterinarioResponse> findAll(String nome, Boolean ativo, Pageable pageable) {
        if (nome != null) {
            return repository.findByNomeContainingIgnoreCase(nome, pageable).map(mapper::toResponse);
        }
        if (Boolean.TRUE.equals(ativo)) {
            return repository.findByAtivoTrue(pageable).map(mapper::toResponse);
        }
        return repository.findAll(pageable).map(mapper::toResponse);
    }

    @Cacheable(value = "veterinarios", key = "#id")
    public VeterinarioResponse findById(Long id) {
        return mapper.toResponse(findEntityById(id));
    }

    @Transactional
    @CacheEvict(value = "veterinarios", allEntries = true)
    public VeterinarioResponse create(VeterinarioRequest request) {
        Veterinario entity = mapper.toEntity(request);
        entity.setSenha(PasswordUtil.encode(request.getSenha()));
        entity.setAtivo(request.getAtivo() != null ? request.getAtivo() : true);
        return mapper.toResponse(repository.save(entity));
    }

    @Transactional
    @CacheEvict(value = "veterinarios", key = "#id")
    public VeterinarioResponse update(Long id, VeterinarioRequest request) {
        Veterinario entity = findEntityById(id);
        mapper.updateEntity(request, entity);
        if (request.getSenha() != null && !request.getSenha().isBlank()) {
            entity.setSenha(PasswordUtil.encode(request.getSenha()));
        }
        if (request.getAtivo() != null) {
            entity.setAtivo(request.getAtivo());
        }
        return mapper.toResponse(repository.save(entity));
    }

    @Transactional
    @CacheEvict(value = "veterinarios", key = "#id")
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Veterinário não encontrado com id: " + id);
        }
        repository.deleteById(id);
    }

    private Veterinario findEntityById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Veterinário não encontrado com id: " + id));
    }
}
