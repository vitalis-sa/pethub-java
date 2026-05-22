package fiap.pethub.service;

import fiap.pethub.dto.request.LembreteRequest;
import fiap.pethub.dto.response.DeleteResponse;
import fiap.pethub.dto.response.LembreteResponse;
import fiap.pethub.entity.Lembrete;
import fiap.pethub.entity.Pet;
import fiap.pethub.entity.Responsavel;
import fiap.pethub.enums.StatusLembrete;
import fiap.pethub.enums.TipoLembrete;
import fiap.pethub.exception.ResourceNotFoundException;
import fiap.pethub.mapper.LembreteMapper;
import fiap.pethub.repository.LembreteRepository;
import fiap.pethub.repository.PetRepository;
import fiap.pethub.repository.ResponsavelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class LembreteService {

    private final LembreteRepository repository;
    private final ResponsavelRepository responsavelRepository;
    private final PetRepository petRepository;
    private final LembreteMapper mapper;

    public Page<LembreteResponse> findAll(Long responsavelId, Long petId, StatusLembrete status, TipoLembrete tipo, Pageable pageable) {
        return Stream.<Map.Entry<Boolean, Supplier<Page<Lembrete>>>>of(
                Map.entry(responsavelId != null && status != null, () -> repository.findByResponsavelIdAndStatus(responsavelId, status, pageable)),
                Map.entry(responsavelId != null && tipo != null,   () -> repository.findByResponsavelIdAndTipo(responsavelId, tipo, pageable)),
                Map.entry(responsavelId != null,                   () -> repository.findByResponsavelId(responsavelId, pageable)),
                Map.entry(petId != null,                           () -> repository.findByPetId(petId, pageable)),
                Map.entry(status != null,                          () -> repository.findByStatus(status, pageable))
        )
                .filter(Map.Entry::getKey)
                .findFirst()
                .map(Map.Entry::getValue)
                .map(Supplier::get)
                .orElseGet(() -> repository.findAll(pageable))
                .map(mapper::toResponse);
    }

    @Cacheable(value = "lembretes", key = "#id")
    public LembreteResponse findById(Long id) {
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Lembrete não encontrado com id: " + id));
    }

    @Transactional
    @CacheEvict(value = "lembretes", allEntries = true)
    public LembreteResponse create(LembreteRequest request) {
        Lembrete entity = buildLembreteEntity(request);
        return mapper.toResponse(repository.save(entity));
    }

    @Transactional
    @CacheEvict(value = "lembretes", key = "#id")
    public LembreteResponse update(Long id, LembreteRequest request) {
        Lembrete entity = findEntityById(id);
        mapper.updateEntity(request, entity);
        entity.setResponsavel(findResponsavel(request.getResponsavelId()));
        entity.setPet(findPet(request.getPetId()));
        return mapper.toResponse(repository.save(entity));
    }

    @Transactional
    @CacheEvict(value = "lembretes", key = "#id")
    public LembreteResponse updateStatus(Long id, StatusLembrete status) {
        Lembrete entity = findEntityById(id);
        entity.setStatus(status);
        return mapper.toResponse(repository.save(entity));
    }

    @Transactional
    @CacheEvict(value = "lembretes", key = "#id")
    public DeleteResponse delete(Long id) {
        repository.findById(id)
                .ifPresentOrElse(
                        repository::delete,
                        () -> { throw new ResourceNotFoundException("Lembrete não encontrado com id: " + id); }
                );
        return DeleteResponse.of("Lembrete", id);
    }

    /**
     * Método interno usado pelos outros services (VacinaTratamento, Consulta, PedidoMedico)
     * para criar lembretes de forma programática.
     */
    @Transactional
    public LembreteResponse criarLembrete(Long responsavelId, Long petId, TipoLembrete tipo,
                                          LocalDate dataAgendada, String mensagem,
                                          Long referenciaId, String referenciaTipo) {
        Responsavel responsavel = findResponsavel(responsavelId);
        Pet pet = findPet(petId);
        Lembrete lembrete = Lembrete.builder()
                .responsavel(responsavel)
                .pet(pet)
                .tipo(tipo)
                .dataAgendada(dataAgendada)
                .mensagem(mensagem)
                .status(StatusLembrete.PENDENTE)
                .referenciaId(referenciaId)
                .referenciaTipo(referenciaTipo)
                .build();
        return mapper.toResponse(repository.save(lembrete));
    }

    private Lembrete buildLembreteEntity(LembreteRequest request) {
        Lembrete entity = mapper.toEntity(request);
        entity.setResponsavel(findResponsavel(request.getResponsavelId()));
        entity.setPet(findPet(request.getPetId()));
        entity.setStatus(request.getStatus() != null ? request.getStatus() : StatusLembrete.PENDENTE);
        return entity;
    }

    private Responsavel findResponsavel(Long responsavelId) {
        return responsavelRepository.findById(responsavelId)
                .orElseThrow(() -> new ResourceNotFoundException("Responsável não encontrado com id: " + responsavelId));
    }

    private Pet findPet(Long petId) {
        return petRepository.findById(petId)
                .orElseThrow(() -> new ResourceNotFoundException("Pet não encontrado com id: " + petId));
    }

    private Lembrete findEntityById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lembrete não encontrado com id: " + id));
    }
}

