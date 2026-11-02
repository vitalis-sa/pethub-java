package fiap.pethub.service;

import fiap.pethub.dto.request.VacinaTratamentoRequest;
import fiap.pethub.dto.response.DeleteResponse;
import fiap.pethub.dto.response.VacinaTratamentoResponse;
import fiap.pethub.entity.Pet;
import fiap.pethub.entity.VacinaTratamento;
import fiap.pethub.enums.TipoLembrete;
import fiap.pethub.enums.TipoVacinaTratamento;
import fiap.pethub.exception.ResourceNotFoundException;
import fiap.pethub.messaging.LembreteSolicitado;
import fiap.pethub.mapper.VacinaTratamentoMapper;
import fiap.pethub.repository.ConsultaRepository;
import fiap.pethub.repository.PetRepository;
import fiap.pethub.security.EscopoDoUsuario;
import fiap.pethub.repository.VacinaTratamentoRepository;
import fiap.pethub.repository.VeterinarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static fiap.pethub.service.Cadastros.consulta;
import static fiap.pethub.service.Cadastros.pet;
import static fiap.pethub.service.Cadastros.veterinario;
import static fiap.pethub.service.Cadastros.exigirPosseDoPet;
import static fiap.pethub.service.Cadastros.listarNoEscopo;

@Service
@RequiredArgsConstructor
public class VacinaTratamentoService {

    private final VacinaTratamentoRepository repository;
    private final PetRepository petRepository;
    private final VeterinarioRepository veterinarioRepository;
    private final ConsultaRepository consultaRepository;
    private final VacinaTratamentoMapper mapper;
    private final EscopoDoUsuario escopo;
    private final ApplicationEventPublisher eventos;

    @Cacheable(value = "vacinas", key = "'lista-' + #petId + '-' + #tipo + '-' + #pageable + '-' + @escopoDoUsuario.chaveDeCache()")
    public Page<VacinaTratamentoResponse> findAll(Long petId, TipoVacinaTratamento tipo, Pageable pageable) {
        if (petId != null) {
            exigirPosseDoPet(escopo, petRepository, petId);
        }
        return Stream.<Map.Entry<Boolean, Supplier<Page<VacinaTratamento>>>>of(
                Map.entry(petId != null && tipo != null, () -> repository.findByPetIdAndTipo(petId, tipo, pageable)),
                Map.entry(petId != null,                 () -> repository.findByPetId(petId, pageable))
        )
                .filter(Map.Entry::getKey)
                .findFirst()
                .map(Map.Entry::getValue)
                .map(Supplier::get)
                .orElseGet(listarNoEscopo(escopo, () -> repository.findAll(pageable),
                        dono -> repository.findByPetResponsavelId(dono, pageable)))
                .map(mapper::toResponse);
    }

    @Cacheable(value = "vacinas", key = "#id + '-' + @escopoDoUsuario.chaveDeCache()")
    public VacinaTratamentoResponse findById(Long id) {
        VacinaTratamento entidade = findEntityById(id);
        escopo.exigirPosse(entidade.getPet().getResponsavelId());
        return mapper.toResponse(entidade);
    }

    @Transactional
    @CacheEvict(value = "vacinas", allEntries = true)
    public VacinaTratamentoResponse create(VacinaTratamentoRequest request) {
        VacinaTratamento entity = buildVacinaEntity(request);
        VacinaTratamento saved = repository.save(entity);
        notificarProximaDose(saved);
        return mapper.toResponse(saved);
    }

    @Transactional
    @CacheEvict(value = "vacinas", allEntries = true)
    public VacinaTratamentoResponse update(Long id, VacinaTratamentoRequest request) {
        VacinaTratamento entity = findEntityById(id);
        escopo.exigirPosse(entity.getPet().getResponsavelId());
        LocalDate proximaDoseAnterior = entity.getProximaDose();
        mapper.updateEntity(request, entity);
        VacinaTratamento saved = repository.save(entity);
        // O .NET identifica o lembrete por referenciaTipo + referenciaId: reenviar atualiza a data, não duplica.
        if (!Objects.equals(proximaDoseAnterior, saved.getProximaDose())) {
            notificarProximaDose(saved);
        }
        return mapper.toResponse(saved);
    }

    @Transactional
    @CacheEvict(value = "vacinas", allEntries = true)
    public DeleteResponse delete(Long id) {
        VacinaTratamento entidade = findEntityById(id);
        escopo.exigirPosse(entidade.getPet().getResponsavelId());
        repository.delete(entidade);
        if (entidade.getTipo() == TipoVacinaTratamento.MEDICAMENTO) {
            Pet pet = entidade.getPet();
            eventos.publishEvent(LembreteSolicitado.cancelamento(pet.getResponsavelId(), pet.getId(), entidade.getId()));
        }
        return DeleteResponse.of("Vacina/Tratamento", id);
    }

    private VacinaTratamento buildVacinaEntity(VacinaTratamentoRequest request) {
        VacinaTratamento entity = mapper.toEntity(request);
        entity.setPet(pet(petRepository, request.getPetId()));
        entity.setVeterinario(veterinario(veterinarioRepository, request.getVeterinarioId()));
        applyConsulta(request.getConsultaId(), entity);
        return entity;
    }

    private void applyConsulta(Long consultaId, VacinaTratamento entity) {
        Optional.ofNullable(consultaId)
                .map(cid -> consulta(consultaRepository, cid))
                .ifPresent(entity::setConsulta);
    }

    private void notificarProximaDose(VacinaTratamento saved) {
        Optional.ofNullable(saved.getProximaDose())
                .ifPresent(data -> {
                    Pet pet = saved.getPet();
                    eventos.publishEvent(new LembreteSolicitado(
                            pet.getResponsavelId(), pet.getId(), TipoLembrete.VACINA, data,
                            "Próxima dose de " + saved.getNome() + " para " + pet.getNome(),
                            saved.getId(), "VacinaTratamento"));
                });
    }

    private VacinaTratamento findEntityById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vacina/Tratamento não encontrado com id: " + id));
    }
}

