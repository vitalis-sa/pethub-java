package fiap.pethub.service;

import fiap.pethub.client.LembreteClient;
import fiap.pethub.client.LembreteRequest;
import fiap.pethub.dto.request.PedidoMedicoRequest;
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

@Service
@RequiredArgsConstructor
public class PedidoMedicoService {

    private final PedidoMedicoRepository repository;
    private final ConsultaRepository consultaRepository;
    private final PetRepository petRepository;
    private final PedidoMedicoMapper mapper;
    private final LembreteClient lembreteClient;

    public Page<PedidoMedicoResponse> findAll(Long petId, StatusPedidoMedico status, TipoPedidoMedico tipo, Pageable pageable) {
        if (petId != null && status != null) return repository.findByPetIdAndStatus(petId, status, pageable).map(mapper::toResponse);
        if (petId != null && tipo != null) return repository.findByPetIdAndTipo(petId, tipo, pageable).map(mapper::toResponse);
        if (petId != null) return repository.findByPetId(petId, pageable).map(mapper::toResponse);
        return repository.findAll(pageable).map(mapper::toResponse);
    }

    @Cacheable(value = "pedidos", key = "#id")
    public PedidoMedicoResponse findById(Long id) {
        return mapper.toResponse(findEntityById(id));
    }

    @Transactional
    @CacheEvict(value = "pedidos", allEntries = true)
    public PedidoMedicoResponse create(PedidoMedicoRequest request) {
        Consulta consulta = consultaRepository.findById(request.getConsultaId())
                .orElseThrow(() -> new ResourceNotFoundException("Consulta não encontrada com id: " + request.getConsultaId()));
        Pet pet = petRepository.findById(request.getPetId())
                .orElseThrow(() -> new ResourceNotFoundException("Pet não encontrado com id: " + request.getPetId()));

        PedidoMedico entity = mapper.toEntity(request);
        entity.setConsulta(consulta);
        entity.setPet(pet);
        if (entity.getStatus() == null) entity.setStatus(StatusPedidoMedico.PENDENTE);

        PedidoMedico saved = repository.save(entity);

        // Notifica tutor via API C#
        String tipo = saved.getTipo() == TipoPedidoMedico.EXAME ? "EXAME" : "MEDICAMENTO";
        lembreteClient.criarLembrete(LembreteRequest.builder()
                .tutorId(pet.getTutor().getId())
                .petId(pet.getId())
                .tipo(tipo)
                .dataAgendada(saved.getDataLimite())
                .mensagem(saved.getDescricao() + (saved.getInstrucoes() != null ? " — " + saved.getInstrucoes() : ""))
                .build());

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
    public void delete(Long id) {
        if (!repository.existsById(id)) throw new ResourceNotFoundException("Pedido médico não encontrado com id: " + id);
        repository.deleteById(id);
    }

    private PedidoMedico findEntityById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido médico não encontrado com id: " + id));
    }
}

