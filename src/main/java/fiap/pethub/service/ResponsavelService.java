package fiap.pethub.service;

import fiap.pethub.config.PasswordUtil;
import fiap.pethub.dto.request.ResponsavelContatoRequest;
import fiap.pethub.dto.request.ResponsavelEnderecoRequest;
import fiap.pethub.dto.request.ResponsavelRequest;
import fiap.pethub.dto.response.DeleteResponse;
import fiap.pethub.dto.response.ResponsavelContatoResponse;
import fiap.pethub.dto.response.ResponsavelEnderecoResponse;
import fiap.pethub.dto.response.ResponsavelResponse;
import fiap.pethub.entity.Responsavel;
import fiap.pethub.entity.ResponsavelContato;
import fiap.pethub.entity.ResponsavelEndereco;
import fiap.pethub.exception.ResourceNotFoundException;
import fiap.pethub.mapper.ResponsavelMapper;
import fiap.pethub.repository.ResponsavelContatoRepository;
import fiap.pethub.repository.ResponsavelEnderecoRepository;
import fiap.pethub.repository.ResponsavelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ResponsavelService {

    private final ResponsavelRepository repository;
    private final ResponsavelContatoRepository contatoRepository;
    private final ResponsavelEnderecoRepository enderecoRepository;
    private final ResponsavelMapper mapper;

    // ─── Responsavel ──────────────────────────────────────────────────────────

    public Page<ResponsavelResponse> findAll(String nome, Boolean ativo, Pageable pageable) {
        if (nome != null && ativo != null)
            return repository.findByNomeContainingIgnoreCaseAndAtivo(nome, ativo, pageable).map(mapper::toResponse);
        if (nome != null)
            return repository.findByNomeContainingIgnoreCase(nome, pageable).map(mapper::toResponse);
        if (ativo != null)
            return repository.findByAtivo(ativo, pageable).map(mapper::toResponse);
        return repository.findAll(pageable).map(mapper::toResponse);
    }

    @Cacheable(value = "responsaveis", key = "#id")
    public ResponsavelResponse findById(Long id) {
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Responsável não encontrado com id: " + id));
    }

    @Cacheable(value = "responsaveis", key = "#cpf")
    public ResponsavelResponse findByCpf(String cpf) {
        return repository.findByCpf(cpf)
                .map(mapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Responsável não encontrado com CPF: " + cpf));
    }

    @Transactional
    @CacheEvict(value = "responsaveis", allEntries = true)
    public ResponsavelResponse create(ResponsavelRequest request) {
        Responsavel responsavel = mapper.toEntity(request);
        responsavel.setSenha(PasswordUtil.encode(request.getSenha()));
        responsavel.setAtivo(request.getAtivo() != null ? request.getAtivo() : true);
        responsavel.setCreatedAt(LocalDateTime.now());
        return mapper.toResponse(repository.save(responsavel));
    }

    @Transactional
    @CacheEvict(value = "responsaveis", key = "#id")
    public ResponsavelResponse update(Long id, ResponsavelRequest request) {
        Responsavel responsavel = findEntityById(id);
        mapper.updateEntity(request, responsavel);
        if (request.getSenha() != null && !request.getSenha().isBlank()) {
            responsavel.setSenha(PasswordUtil.encode(request.getSenha()));
        }
        return mapper.toResponse(repository.save(responsavel));
    }

    @Transactional
    @CacheEvict(value = "responsaveis", key = "#id")
    public DeleteResponse delete(Long id) {
        repository.findById(id)
                .ifPresentOrElse(
                        repository::delete,
                        () -> { throw new ResourceNotFoundException("Responsável não encontrado com id: " + id); }
                );
        return DeleteResponse.of("Responsável", id);
    }

    // ─── Contatos ─────────────────────────────────────────────────────────────

    @Cacheable(value = "responsaveis", key = "'contatos-' + #responsavelId + #pageable")
    public Page<ResponsavelContatoResponse> findContatos(Long responsavelId, Pageable pageable) {
        ensureResponsavelExists(responsavelId);
        return contatoRepository.findByResponsavelId(responsavelId, pageable)
                .map(mapper::toContatoResponse);
    }

    @Cacheable(value = "responsaveis", key = "'contato-' + #contatoId")
    public ResponsavelContatoResponse findContatoById(Long responsavelId, Long contatoId) {
        ensureResponsavelExists(responsavelId);
        return contatoRepository.findById(contatoId)
                .map(mapper::toContatoResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Contato não encontrado com id: " + contatoId));
    }

    @Transactional
    @CacheEvict(value = "responsaveis", key = "'contatos-' + #responsavelId")
    public ResponsavelContatoResponse addContato(Long responsavelId, ResponsavelContatoRequest request) {
        Responsavel responsavel = findEntityById(responsavelId);
        ResponsavelContato contato = mapper.contatoToEntity(request);
        contato.setResponsavel(responsavel);
        contato.setPrincipal(request.getPrincipal() != null ? request.getPrincipal() : false);
        return mapper.toContatoResponse(contatoRepository.save(contato));
    }

    @Transactional
    @CacheEvict(value = "responsaveis", allEntries = true)
    public ResponsavelContatoResponse updateContato(Long responsavelId, Long contatoId, ResponsavelContatoRequest request) {
        ensureResponsavelExists(responsavelId);
        ResponsavelContato contato = contatoRepository.findById(contatoId)
                .orElseThrow(() -> new ResourceNotFoundException("Contato não encontrado com id: " + contatoId));
        mapper.updateContato(request, contato);
        return mapper.toContatoResponse(contatoRepository.save(contato));
    }

    @Transactional
    @CacheEvict(value = "responsaveis", allEntries = true)
    public DeleteResponse deleteContato(Long responsavelId, Long contatoId) {
        ensureResponsavelExists(responsavelId);
        contatoRepository.findById(contatoId)
                .ifPresentOrElse(
                        contatoRepository::delete,
                        () -> { throw new ResourceNotFoundException("Contato não encontrado com id: " + contatoId); }
                );
        return DeleteResponse.of("Contato", contatoId);
    }

    // ─── Endereços ────────────────────────────────────────────────────────────

    @Cacheable(value = "responsaveis", key = "'enderecos-' + #responsavelId + #pageable")
    public Page<ResponsavelEnderecoResponse> findEnderecos(Long responsavelId, Pageable pageable) {
        ensureResponsavelExists(responsavelId);
        return enderecoRepository.findByResponsavelId(responsavelId, pageable)
                .map(mapper::toEnderecoResponse);
    }

    @Cacheable(value = "responsaveis", key = "'endereco-' + #enderecoId")
    public ResponsavelEnderecoResponse findEnderecoById(Long responsavelId, Long enderecoId) {
        ensureResponsavelExists(responsavelId);
        return enderecoRepository.findById(enderecoId)
                .map(mapper::toEnderecoResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Endereço não encontrado com id: " + enderecoId));
    }

    @Transactional
    @CacheEvict(value = "responsaveis", key = "'enderecos-' + #responsavelId")
    public ResponsavelEnderecoResponse addEndereco(Long responsavelId, ResponsavelEnderecoRequest request) {
        Responsavel responsavel = findEntityById(responsavelId);
        ResponsavelEndereco endereco = mapper.enderecoToEntity(request);
        endereco.setResponsavel(responsavel);
        endereco.setPrincipal(request.getPrincipal() != null ? request.getPrincipal() : false);
        return mapper.toEnderecoResponse(enderecoRepository.save(endereco));
    }

    @Transactional
    @CacheEvict(value = "responsaveis", allEntries = true)
    public ResponsavelEnderecoResponse updateEndereco(Long responsavelId, Long enderecoId, ResponsavelEnderecoRequest request) {
        ensureResponsavelExists(responsavelId);
        ResponsavelEndereco endereco = enderecoRepository.findById(enderecoId)
                .orElseThrow(() -> new ResourceNotFoundException("Endereço não encontrado com id: " + enderecoId));
        mapper.updateEndereco(request, endereco);
        return mapper.toEnderecoResponse(enderecoRepository.save(endereco));
    }

    @Transactional
    @CacheEvict(value = "responsaveis", allEntries = true)
    public DeleteResponse deleteEndereco(Long responsavelId, Long enderecoId) {
        ensureResponsavelExists(responsavelId);
        enderecoRepository.findById(enderecoId)
                .ifPresentOrElse(
                        enderecoRepository::delete,
                        () -> { throw new ResourceNotFoundException("Endereço não encontrado com id: " + enderecoId); }
                );
        return DeleteResponse.of("Endereço", enderecoId);
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    public Responsavel findEntityById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Responsável não encontrado com id: " + id));
    }

    private void ensureResponsavelExists(Long responsavelId) {
        if (!repository.existsById(responsavelId))
            throw new ResourceNotFoundException("Responsável não encontrado com id: " + responsavelId);
    }
}

