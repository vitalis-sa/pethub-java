package fiap.pethub.service;

import fiap.pethub.client.LembreteClient;
import fiap.pethub.client.LembreteRequest;
import fiap.pethub.dto.request.PedidoMedicoRequest;
import fiap.pethub.dto.response.DeleteResponse;
import fiap.pethub.dto.response.PedidoMedicoResponse;
import fiap.pethub.entity.Consulta;
import fiap.pethub.entity.PedidoMedico;
import fiap.pethub.entity.Pet;
import fiap.pethub.enums.StatusPedidoMedico;
import fiap.pethub.enums.TipoPedidoMedico;
import fiap.pethub.exception.ResourceNotFoundException;
import fiap.pethub.mapper.PedidoMedicoMapper;
import fiap.pethub.repository.ConsultaRepository;
import fiap.pethub.repository.PedidoMedicoRepository;
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
public class PedidoMedicoService {

    private final PedidoMedicoRepository repository;
    private final ConsultaRepository consultaRepository;
    private final PetRepository petRepository;
    private final PedidoMedicoMapper mapper;
    private final LembreteClient lembreteClient;

    public Page<PedidoMedicoResponse> findAll(Long petId, StatusPedidoMedico status, TipoPedidoMedico tipo, Pageable pageable) {
        return Stream.<Map.Entry<Boolean, Supplier<Page<PedidoMedico>>>>of(
                Map.entry(petId != null && status != null, () -> repository.findByPetIdAndStatus(petId, status, pageable)),
                Map.entry(petId != null && tipo != null,   () -> repository.findByPetIdAndTipo(petId, tipo, pageable)),
                Map.entry(petId != null,                   () -> repository.findByPetId(petId, pageable))
        )
                .filter(Map.Entry::getKey)
                .findFirst()
                .map(Map.Entry::getValue)
                .map(Supplier::get)
                .orElseGet(() -> repository.findAll(pageable))
                .map(mapper::toResponse);
    }

    @Cacheable(value = "pedidos", key = "#id")
    public PedidoMedicoResponse findById(Long id) {
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido médico não encontrado com id: " + id));
    }

    @Transactional
    @CacheEvict(value = "pedidos", allEntries = true)
    public PedidoMedicoResponse create(PedidoMedicoRequest request) {
        PedidoMedico entity = buildPedidoEntity(request);
        PedidoMedico saved = repository.save(entity);
        notificarTutor(saved);
        return mapper.toResponse(saved);
    }

    @Transactional
    @CacheEvict(value = "pedidos", key = "#id")
    public PedidoMedicoResponse update(Long id, PedidoMedicoRequest request) {
        PedidoMedico entity = findEntityById(id);
        mapper.updateEntity(request, entity);
        return mapper.toResponse(repository.save(entity));
    }

    @Transactional
    @CacheEvict(value = "pedidos", key = "#id")
    public DeleteResponse delete(Long id) {
        repository.findById(id)
                .ifPresentOrElse(
                        repository::delete,
                        () -> { throw new ResourceNotFoundException("Pedido médico não encontrado com id: " + id); }
                );
        return DeleteResponse.of("Pedido médico", id);
    }

    private PedidoMedico buildPedidoEntity(PedidoMedicoRequest request) {
        PedidoMedico entity = mapper.toEntity(request);
        entity.setConsulta(findConsulta(request.getConsultaId()));
        entity.setPet(findPet(request.getPetId()));
        entity.setStatus(entity.getStatus() != null ? entity.getStatus() : StatusPedidoMedico.PENDENTE);
        return entity;
    }

    private void notificarTutor(PedidoMedico saved) {
        Pet pet = saved.getPet();
        String tipo = saved.getTipo() == TipoPedidoMedico.EXAME ? "EXAME" : "MEDICAMENTO";
        lembreteClient.criarLembrete(LembreteRequest.builder()
                .tutorId(pet.getTutor().getId())
                .petId(pet.getId())
                .tipo(tipo)
                .dataAgendada(saved.getDataLimite())
                .mensagem(saved.getDescricao() + (saved.getInstrucoes() != null ? " — " + saved.getInstrucoes() : ""))
                .build());
    }

    private Consulta findConsulta(Long consultaId) {
        return consultaRepository.findById(consultaId)
                .orElseThrow(() -> new ResourceNotFoundException("Consulta não encontrada com id: " + consultaId));
    }

    private Pet findPet(Long petId) {
        return petRepository.findById(petId)
                .orElseThrow(() -> new ResourceNotFoundException("Pet não encontrado com id: " + petId));
    }

    private PedidoMedico findEntityById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido médico não encontrado com id: " + id));
    }
}
