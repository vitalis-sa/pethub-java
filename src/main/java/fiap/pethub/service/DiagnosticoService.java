package fiap.pethub.service;

import fiap.pethub.dto.request.DiagnosticoRequest;
import fiap.pethub.dto.response.DeleteResponse;
import fiap.pethub.dto.response.DiagnosticoResponse;
import fiap.pethub.entity.Diagnostico;
import fiap.pethub.exception.ResourceNotFoundException;
import fiap.pethub.mapper.DiagnosticoMapper;
import fiap.pethub.repository.ConsultaRepository;
import fiap.pethub.repository.DiagnosticoRepository;
import fiap.pethub.repository.PetRepository;
import fiap.pethub.security.EscopoDoUsuario;
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

import static fiap.pethub.service.Cadastros.pet;
import static fiap.pethub.service.Cadastros.consulta;
import static fiap.pethub.service.Cadastros.exigirPosseDoPet;
import static fiap.pethub.service.Cadastros.listarNoEscopo;

@Service
@RequiredArgsConstructor
public class DiagnosticoService {

    private final DiagnosticoRepository repository;
    private final ConsultaRepository consultaRepository;
    private final PetRepository petRepository;
    private final DiagnosticoMapper mapper;
    private final EscopoDoUsuario escopo;

    @Cacheable(value = "diagnosticos", key = "'lista-' + #petId + '-' + #consultaId + '-' + #pageable + '-' + @escopoDoUsuario.chaveDeCache()")
    public Page<DiagnosticoResponse> findAll(Long petId, Long consultaId, Pageable pageable) {
        if (petId != null) {
            exigirPosseDoPet(escopo, petRepository, petId);
        }
        return Stream.<Map.Entry<Boolean, Supplier<Page<Diagnostico>>>>of(
                Map.entry(petId != null,     () -> repository.findByPetId(petId, pageable)),
                Map.entry(consultaId != null, () -> repository.findByConsultaId(consultaId, pageable))
        )
                .filter(Map.Entry::getKey)
                .findFirst()
                .map(Map.Entry::getValue)
                .map(Supplier::get)
                .orElseGet(listarNoEscopo(escopo, () -> repository.findAll(pageable),
                        dono -> repository.findByPetResponsavelId(dono, pageable)))
                .map(mapper::toResponse);
    }

    @Cacheable(value = "diagnosticos", key = "#id + '-' + @escopoDoUsuario.chaveDeCache()")
    public DiagnosticoResponse findById(Long id) {
        Diagnostico entidade = findEntityById(id);
        escopo.exigirPosse(entidade.getPet().getResponsavelId());
        return mapper.toResponse(entidade);
    }

    @Transactional
    @CacheEvict(value = "diagnosticos", allEntries = true)
    public DiagnosticoResponse create(DiagnosticoRequest request) {
        Diagnostico entity = buildDiagnosticoEntity(request);
        return mapper.toResponse(repository.save(entity));
    }

    @Transactional
    @CacheEvict(value = "diagnosticos", allEntries = true)
    public DiagnosticoResponse update(Long id, DiagnosticoRequest request) {
        Diagnostico entity = findEntityById(id);
        escopo.exigirPosse(entity.getPet().getResponsavelId());
        mapper.updateEntity(request, entity);
        return mapper.toResponse(repository.save(entity));
    }

    @Transactional
    @CacheEvict(value = "diagnosticos", allEntries = true)
    public DeleteResponse delete(Long id) {
        Diagnostico entidade = findEntityById(id);
        escopo.exigirPosse(entidade.getPet().getResponsavelId());
        repository.delete(entidade);
        return DeleteResponse.of("Diagnóstico", id);
    }

    private Diagnostico buildDiagnosticoEntity(DiagnosticoRequest request) {
        Diagnostico entity = mapper.toEntity(request);
        entity.setConsulta(consulta(consultaRepository, request.getConsultaId()));
        entity.setPet(pet(petRepository, request.getPetId()));
        applyBooleanDefaults(entity);
        return entity;
    }

    private void applyBooleanDefaults(Diagnostico d) {
        d.setPerdaApetite(d.getPerdaApetite() != null ? d.getPerdaApetite() : false);
        d.setVomito(d.getVomito() != null ? d.getVomito() : false);
        d.setDiarreia(d.getDiarreia() != null ? d.getDiarreia() : false);
        d.setTosse(d.getTosse() != null ? d.getTosse() : false);
        d.setDificuldadeRespiratoria(d.getDificuldadeRespiratoria() != null ? d.getDificuldadeRespiratoria() : false);
        d.setClaudicacao(d.getClaudicacao() != null ? d.getClaudicacao() : false);
        d.setLesoesPele(d.getLesoesPele() != null ? d.getLesoesPele() : false);
        d.setSecrecaoNasal(d.getSecrecaoNasal() != null ? d.getSecrecaoNasal() : false);
        d.setSecrecaoOcular(d.getSecrecaoOcular() != null ? d.getSecrecaoOcular() : false);
    }

    private Diagnostico findEntityById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Diagnóstico não encontrado com id: " + id));
    }
}
