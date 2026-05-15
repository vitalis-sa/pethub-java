package fiap.pethub.service;

import fiap.pethub.client.LembreteClient;
import fiap.pethub.client.LembreteRequest;
import fiap.pethub.dto.request.VacinaTratamentoRequest;
import fiap.pethub.dto.response.VacinaTratamentoResponse;
import fiap.pethub.entity.Consulta;
import fiap.pethub.entity.Pet;
import fiap.pethub.entity.VacinaTratamento;
import fiap.pethub.entity.Veterinario;
import fiap.pethub.enums.TipoVacinaTratamento;
import fiap.pethub.exception.ResourceNotFoundException;
import fiap.pethub.mapper.VacinaTratamentoMapper;
import fiap.pethub.repository.ConsultaRepository;
import fiap.pethub.repository.PetRepository;
import fiap.pethub.repository.VacinaTratamentoRepository;
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
public class VacinaTratamentoService {

    private final VacinaTratamentoRepository repository;
    private final PetRepository petRepository;
    private final VeterinarioRepository veterinarioRepository;
    private final ConsultaRepository consultaRepository;
    private final VacinaTratamentoMapper mapper;
    private final LembreteClient lembreteClient;

    public Page<VacinaTratamentoResponse> findAll(Long petId, TipoVacinaTratamento tipo, Pageable pageable) {
        if (petId != null && tipo != null) return repository.findByPetIdAndTipo(petId, tipo, pageable).map(mapper::toResponse);
        if (petId != null) return repository.findByPetId(petId, pageable).map(mapper::toResponse);
        return repository.findAll(pageable).map(mapper::toResponse);
    }

    @Cacheable(value = "vacinas", key = "#id")
    public VacinaTratamentoResponse findById(Long id) {
        return mapper.toResponse(findEntityById(id));
    }

    @Transactional
    @CacheEvict(value = "vacinas", allEntries = true)
    public VacinaTratamentoResponse create(VacinaTratamentoRequest request) {
        Pet pet = petRepository.findById(request.getPetId())
                .orElseThrow(() -> new ResourceNotFoundException("Pet não encontrado com id: " + request.getPetId()));
        Veterinario veterinario = veterinarioRepository.findById(request.getVeterinarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Veterinário não encontrado com id: " + request.getVeterinarioId()));

        VacinaTratamento entity = mapper.toEntity(request);
        entity.setPet(pet);
        entity.setVeterinario(veterinario);

        if (request.getConsultaId() != null) {
            Consulta consulta = consultaRepository.findById(request.getConsultaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Consulta não encontrada com id: " + request.getConsultaId()));
            entity.setConsulta(consulta);
        }

        VacinaTratamento saved = repository.save(entity);

        // Lembrete de próxima dose de vacina
        if (saved.getProximaDose() != null) {
            lembreteClient.criarLembrete(LembreteRequest.builder()
                    .tutorId(pet.getTutor().getId())
                    .petId(pet.getId())
                    .tipo("VACINA")
                    .dataAgendada(saved.getProximaDose())
                    .mensagem("Próxima dose de " + saved.getNome() + " para " + pet.getNome())
                    .build());
        }

        return mapper.toResponse(saved);
    }

    @Transactional
    @CacheEvict(value = "vacinas", key = "#id")
    public VacinaTratamentoResponse update(Long id, VacinaTratamentoRequest request) {
        VacinaTratamento entity = findEntityById(id);
        mapper.updateEntity(request, entity);
        return mapper.toResponse(repository.save(entity));
    }

    @Transactional
    @CacheEvict(value = "vacinas", key = "#id")
    public void delete(Long id) {
        if (!repository.existsById(id)) throw new ResourceNotFoundException("Vacina/Tratamento não encontrado com id: " + id);
        repository.deleteById(id);
    }

    private VacinaTratamento findEntityById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vacina/Tratamento não encontrado com id: " + id));
    }
}

