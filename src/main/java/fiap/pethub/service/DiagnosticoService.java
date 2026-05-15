package fiap.pethub.service;

import fiap.pethub.dto.request.DiagnosticoRequest;
import fiap.pethub.dto.response.DiagnosticoResponse;
import fiap.pethub.entity.Consulta;
import fiap.pethub.entity.Diagnostico;
import fiap.pethub.entity.Pet;
import fiap.pethub.exception.ResourceNotFoundException;
import fiap.pethub.mapper.DiagnosticoMapper;
import fiap.pethub.repository.ConsultaRepository;
import fiap.pethub.repository.DiagnosticoRepository;
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
public class DiagnosticoService {

    private final DiagnosticoRepository repository;
    private final ConsultaRepository consultaRepository;
    private final PetRepository petRepository;
    private final DiagnosticoMapper mapper;

    public Page<DiagnosticoResponse> findAll(Long petId, Long consultaId, Pageable pageable) {
        if (petId != null) return repository.findByPetId(petId, pageable).map(mapper::toResponse);
        if (consultaId != null) return repository.findByConsultaId(consultaId, pageable).map(mapper::toResponse);
        return repository.findAll(pageable).map(mapper::toResponse);
    }

    @Cacheable(value = "diagnosticos", key = "#id")
    public DiagnosticoResponse findById(Long id) {
        return mapper.toResponse(findEntityById(id));
    }

    @Transactional
    @CacheEvict(value = "diagnosticos", allEntries = true)
    public DiagnosticoResponse create(DiagnosticoRequest request) {
        Consulta consulta = consultaRepository.findById(request.getConsultaId())
                .orElseThrow(() -> new ResourceNotFoundException("Consulta não encontrada com id: " + request.getConsultaId()));
        Pet pet = petRepository.findById(request.getPetId())
                .orElseThrow(() -> new ResourceNotFoundException("Pet não encontrado com id: " + request.getPetId()));
        Diagnostico entity = mapper.toEntity(request);
        entity.setConsulta(consulta);
        entity.setPet(pet);
        applyBooleanDefaults(entity);
        return mapper.toResponse(repository.save(entity));
    }

    @Transactional
    @CacheEvict(value = "diagnosticos", key = "#id")
    public DiagnosticoResponse update(Long id, DiagnosticoRequest request) {
        Diagnostico entity = findEntityById(id);
        mapper.updateEntity(request, entity);
        return mapper.toResponse(repository.save(entity));
    }

    @Transactional
    @CacheEvict(value = "diagnosticos", key = "#id")
    public void delete(Long id) {
        if (!repository.existsById(id)) throw new ResourceNotFoundException("Diagnóstico não encontrado com id: " + id);
        repository.deleteById(id);
    }

    private void applyBooleanDefaults(Diagnostico d) {
        if (d.getPerdaApetite() == null) d.setPerdaApetite(false);
        if (d.getVomito() == null) d.setVomito(false);
        if (d.getDiarreia() == null) d.setDiarreia(false);
        if (d.getTosse() == null) d.setTosse(false);
        if (d.getDificuldadeRespiratoria() == null) d.setDificuldadeRespiratoria(false);
        if (d.getClaudicacao() == null) d.setClaudicacao(false);
        if (d.getLesoesPele() == null) d.setLesoesPele(false);
        if (d.getSecrecaoNasal() == null) d.setSecrecaoNasal(false);
        if (d.getSecrecaoOcular() == null) d.setSecrecaoOcular(false);
    }

    private Diagnostico findEntityById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Diagnóstico não encontrado com id: " + id));
    }
}

