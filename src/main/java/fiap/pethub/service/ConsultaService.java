package fiap.pethub.service;

import fiap.pethub.client.LembreteClient;
import fiap.pethub.client.LembreteRequest;
import fiap.pethub.dto.request.ConsultaRequest;
import fiap.pethub.dto.response.ConsultaResponse;
import fiap.pethub.entity.Consulta;
import fiap.pethub.entity.Pet;
import fiap.pethub.entity.UnidadeVeterinario;
import fiap.pethub.entity.Veterinario;
import fiap.pethub.enums.StatusConsulta;
import fiap.pethub.exception.ResourceNotFoundException;
import fiap.pethub.mapper.ConsultaMapper;
import fiap.pethub.repository.ConsultaRepository;
import fiap.pethub.repository.PetRepository;
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
public class ConsultaService {

    private final ConsultaRepository repository;
    private final PetRepository petRepository;
    private final VeterinarioRepository veterinarioRepository;
    private final UnidadeVeterinarioRepository unidadeRepository;
    private final ConsultaMapper mapper;
    private final LembreteClient lembreteClient;

    public Page<ConsultaResponse> findAll(Long petId, Long veterinarioId, StatusConsulta status, Pageable pageable) {
        if (petId != null && status != null) {
            return repository.findByPetIdAndStatus(petId, status, pageable).map(mapper::toResponse);
        }
        if (petId != null) {
            return repository.findByPetId(petId, pageable).map(mapper::toResponse);
        }
        if (veterinarioId != null) {
            return repository.findByVeterinarioId(veterinarioId, pageable).map(mapper::toResponse);
        }
        if (status != null) {
            return repository.findByStatus(status, pageable).map(mapper::toResponse);
        }
        return repository.findAll(pageable).map(mapper::toResponse);
    }

    @Cacheable(value = "consultas", key = "#id")
    public ConsultaResponse findById(Long id) {
        return mapper.toResponse(findEntityById(id));
    }

    @Transactional
    @CacheEvict(value = "consultas", allEntries = true)
    public ConsultaResponse create(ConsultaRequest request) {
        Pet pet = petRepository.findById(request.getPetId())
                .orElseThrow(() -> new ResourceNotFoundException("Pet não encontrado com id: " + request.getPetId()));
        Veterinario veterinario = veterinarioRepository.findById(request.getVeterinarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Veterinário não encontrado com id: " + request.getVeterinarioId()));

        Consulta entity = mapper.toEntity(request);
        entity.setPet(pet);
        entity.setVeterinario(veterinario);

        if (request.getUnidadeId() != null) {
            UnidadeVeterinario unidade = unidadeRepository.findById(request.getUnidadeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Unidade não encontrada com id: " + request.getUnidadeId()));
            entity.setUnidade(unidade);
        }

        Consulta saved = repository.save(entity);

        // Notifica tutor via API C#
        lembreteClient.criarLembrete(LembreteRequest.builder()
                .tutorId(pet.getTutor().getId())
                .petId(pet.getId())
                .tipo("CONSULTA")
                .dataAgendada(saved.getDataHora().toLocalDate())
                .mensagem("Consulta agendada para " + pet.getNome() + " em " + saved.getDataHora())
                .build());

        return mapper.toResponse(saved);
    }

    @Transactional
    @CacheEvict(value = "consultas", key = "#id")
    public ConsultaResponse update(Long id, ConsultaRequest request) {
        Consulta entity = findEntityById(id);
        mapper.updateEntity(request, entity);
        if (request.getUnidadeId() != null) {
            UnidadeVeterinario unidade = unidadeRepository.findById(request.getUnidadeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Unidade não encontrada com id: " + request.getUnidadeId()));
            entity.setUnidade(unidade);
        }
        return mapper.toResponse(repository.save(entity));
    }

    @Transactional
    @CacheEvict(value = "consultas", key = "#id")
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Consulta não encontrada com id: " + id);
        }
        repository.deleteById(id);
    }

    private Consulta findEntityById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Consulta não encontrada com id: " + id));
    }
}

