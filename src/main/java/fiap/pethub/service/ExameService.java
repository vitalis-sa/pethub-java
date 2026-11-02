package fiap.pethub.service;

import fiap.pethub.armazenamento.ArmazenamentoService;
import fiap.pethub.armazenamento.PastaDoBucket;
import fiap.pethub.dto.request.ExameRequest;
import fiap.pethub.dto.response.DeleteResponse;
import fiap.pethub.dto.response.ExameResponse;
import fiap.pethub.dto.response.UrlDeDownloadResponse;
import fiap.pethub.dto.response.UrlDeEnvioResponse;
import fiap.pethub.entity.Exame;
import fiap.pethub.exception.ResourceNotFoundException;
import fiap.pethub.mapper.ExameMapper;
import fiap.pethub.repository.ConsultaRepository;
import fiap.pethub.repository.ExameRepository;
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
public class ExameService {

    private final ExameRepository repository;
    private final ConsultaRepository consultaRepository;
    private final PetRepository petRepository;
    private final ExameMapper mapper;
    private final EscopoDoUsuario escopo;
    private final ArmazenamentoService armazenamento;

    @Cacheable(value = "exames", key = "'lista-' + #petId + '-' + #consultaId + '-' + #pageable + '-' + @escopoDoUsuario.chaveDeCache()")
    public Page<ExameResponse> findAll(Long petId, Long consultaId, Pageable pageable) {
        if (petId != null) {
            exigirPosseDoPet(escopo, petRepository, petId);
        }
        return Stream.<Map.Entry<Boolean, Supplier<Page<Exame>>>>of(
                Map.entry(petId != null,      () -> repository.findByPetId(petId, pageable)),
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

    @Cacheable(value = "exames", key = "#id + '-' + @escopoDoUsuario.chaveDeCache()")
    public ExameResponse findById(Long id) {
        Exame exame = findEntityById(id);
        escopo.exigirPosse(exame.getPet().getResponsavelId());
        return mapper.toResponse(exame);
    }

    @Transactional
    @CacheEvict(value = "exames", allEntries = true)
    public ExameResponse create(ExameRequest request) {
        Exame entity = buildExameEntity(request);
        return mapper.toResponse(repository.save(entity));
    }

    @Transactional
    @CacheEvict(value = "exames", allEntries = true)
    public ExameResponse update(Long id, ExameRequest request) {
        Exame entity = findEntityById(id);
        escopo.exigirPosse(entity.getPet().getResponsavelId());
        mapper.updateEntity(request, entity);
        return mapper.toResponse(repository.save(entity));
    }

    @Transactional
    @CacheEvict(value = "exames", allEntries = true)
    public DeleteResponse delete(Long id) {
        Exame entity = findEntityById(id);
        escopo.exigirPosse(entity.getPet().getResponsavelId());
        repository.delete(entity);
        return DeleteResponse.of("Exame", id);
    }

    /** Passo 1 do PDF (veterinário): URL assinada para o app enviar o arquivo direto ao bucket. */
    public UrlDeEnvioResponse prepararEnvioDoArquivo(Long id, String contentType) {
        Exame exame = findEntityById(id);
        return armazenamento.prepararEnvio(PastaDoBucket.EXAMES, String.valueOf(exame.getId()), contentType);
    }

    /** Passo 2: confirma o envio; só então o exame passa a apontar para o arquivo. */
    @Transactional
    @CacheEvict(value = "exames", allEntries = true)
    public ExameResponse confirmarArquivo(Long id, String caminho) {
        Exame exame = findEntityById(id);
        armazenamento.exigirCaminhoDe(PastaDoBucket.EXAMES, String.valueOf(exame.getId()), caminho);
        exame.setArquivoResultado(caminho);
        return mapper.toResponse(repository.save(exame));
    }

    /** Veterinário e responsável dono do pet baixam o PDF; o responsável só vê o que é dele. */
    public UrlDeDownloadResponse urlDoArquivo(Long id) {
        Exame exame = findEntityById(id);
        escopo.exigirPosse(exame.getPet().getResponsavelId());
        if (exame.getArquivoResultado() == null) {
            throw new ResourceNotFoundException("O exame ainda não tem arquivo.");
        }
        return armazenamento.urlDeDownload(exame.getArquivoResultado());
    }

    private Exame buildExameEntity(ExameRequest request) {
        Exame entity = mapper.toEntity(request);
        entity.setConsulta(consulta(consultaRepository, request.getConsultaId()));
        entity.setPet(pet(petRepository, request.getPetId()));
        return entity;
    }

    private Exame findEntityById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exame não encontrado com id: " + id));
    }
}
