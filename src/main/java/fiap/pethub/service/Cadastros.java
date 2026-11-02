package fiap.pethub.service;

import fiap.pethub.entity.Consulta;
import fiap.pethub.entity.Pet;
import fiap.pethub.entity.Veterinario;
import fiap.pethub.exception.ResourceNotFoundException;
import fiap.pethub.repository.ConsultaRepository;
import fiap.pethub.repository.PetRepository;
import fiap.pethub.repository.VeterinarioRepository;
import fiap.pethub.security.EscopoDoUsuario;
import org.springframework.data.domain.Page;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Buscas e regras de escopo que todo serviço do prontuário repete: achar o pet, a consulta ou o
 * veterinário referenciado (404 quando não existe), conferir a posse do pet e listar respeitando
 * o perfil. Fica num lugar só para a mensagem e a regra não divergirem entre os serviços.
 *
 * Recebe o repositório como argumento (em vez de ser um bean) para os serviços continuarem
 * testáveis com os mesmos mocks de repositório.
 */
public final class Cadastros {

    private Cadastros() {
    }

    public static Pet pet(PetRepository repositorio, Long id) {
        return repositorio.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pet não encontrado com id: " + id));
    }

    public static Consulta consulta(ConsultaRepository repositorio, Long id) {
        return repositorio.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Consulta não encontrada com id: " + id));
    }

    public static Veterinario veterinario(VeterinarioRepository repositorio, Long id) {
        return repositorio.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Veterinário não encontrado com id: " + id));
    }

    /** Filtrar por um pet alheio não pode revelar sequer que ele existe. */
    public static void exigirPosseDoPet(EscopoDoUsuario escopo, PetRepository repositorio, Long petId) {
        escopo.exigirPosse(pet(repositorio, petId).getResponsavelId());
    }

    /** Listagem sem filtro: o veterinário vê tudo; o responsável, só o que é dos pets dele. */
    public static <T> Supplier<Page<T>> listarNoEscopo(EscopoDoUsuario escopo,
                                                       Supplier<Page<T>> todos,
                                                       Function<Long, Page<T>> doResponsavel) {
        return () -> escopo.ehVeterinario() ? todos.get() : doResponsavel.apply(escopo.idDoResponsavel());
    }
}
