package fiap.pethub.service;

import fiap.pethub.dto.request.ExameRequest;
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

@Service
@RequiredArgsConstructor
public class ExameService {

    private final ExameRepository repository;
    private final ConsultaRepository consultaRepository;
    private final PetRepository petRepository;
    private final ExameMapper mapper;

    public Page<ExameResponse> findAll(Long petId, Long consultaId, Pageable pageable) {
        if (petId != null) return repository.findByPetId(petId, pageable).map(mapper::toResponse);
        if (consultaId != null) return repository.findByConsultaId(consultaId, pageable).map(mapper::toResponse);
        return repository.findAll(pageable).map(mapper::toResponse);
    }

    @Cacheable(value = "exames", key = "#id")
    public ExameResponse findById(Long id) {
        return mapper.toResponse(findEntityById(id));
    }

    @Transactional
    @CacheEvict(value = "exames", allEntries = true)
    public ExameResponse create(ExameRequest request) {
        Consulta consulta = consultaRepository.findById(request.getConsultaId())
                .orElseThrow(() -> new ResourceNotFoundException("Consulta não encontrada com id: " + request.getConsultaId()));
        Pet pet = petRepository.findById(request.getPetId())
                .orElseThrow(() -> new ResourceNotFoundException("Pet não encontrado com id: " + request.getPetId()));
        Exame entity = mapper.toEntity(request);
        entity.setConsulta(consulta);
        entity.setPet(pet);
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
    public void delete(Long id) {
        if (!repository.existsById(id)) throw new ResourceNotFoundException("Exame não encontrado com id: " + id);
        repository.deleteById(id);
    }

    private Exame findEntityById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exame não encontrado com id: " + id));
    }
}

