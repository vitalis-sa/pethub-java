package fiap.pethub.service;

import fiap.pethub.dto.request.DiagnosticoRequest;
import fiap.pethub.entity.Diagnostico;
import fiap.pethub.entity.Pet;
import fiap.pethub.mapper.DiagnosticoMapper;
import fiap.pethub.repository.ConsultaRepository;
import fiap.pethub.repository.DiagnosticoRepository;
import fiap.pethub.repository.PetRepository;
import fiap.pethub.security.EscopoDoUsuario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DiagnosticoServiceTest {

    @Mock private DiagnosticoRepository repository;
    @Mock private ConsultaRepository consultaRepository;
    @Mock private PetRepository petRepository;
    @Mock private DiagnosticoMapper mapper;
    @Mock private EscopoDoUsuario escopo;
    @InjectMocks private DiagnosticoService service;

    /**
     * O PUT sem os sinais clínicos booleanos deixava null em colunas NOT NULL, e o banco recusava
     * com "Violação de integridade de dados" (409). A regra do create vale também no update.
     */
    @Test
    void atualizarSemOsSinaisBooleanosGravaFalseEmVezDeNull() {
        Diagnostico existente = Diagnostico.builder().id(7L).pet(Pet.builder().id(1L).responsavelId(3L).build()).build();
        when(repository.findById(7L)).thenReturn(Optional.of(existente));
        when(repository.save(any(Diagnostico.class))).thenAnswer(inv -> inv.getArgument(0));
        DiagnosticoRequest semBooleanos = new DiagnosticoRequest();
        // O mapper copia o request inteiro: o que não veio chega como null.
        doAnswer(inv -> {
            Diagnostico alvo = inv.getArgument(1);
            alvo.setPerdaApetite(true);
            alvo.setVomito(null);
            alvo.setDiarreia(null);
            alvo.setTosse(null);
            alvo.setDificuldadeRespiratoria(null);
            alvo.setClaudicacao(null);
            alvo.setLesoesPele(null);
            alvo.setSecrecaoNasal(null);
            alvo.setSecrecaoOcular(null);
            return null;
        }).when(mapper).updateEntity(eq(semBooleanos), any(Diagnostico.class));

        service.update(7L, semBooleanos);

        verify(repository).save(existente);
        assertThat(existente.getPerdaApetite()).isTrue();
        assertThat(existente.getVomito()).isFalse();
        assertThat(existente.getDiarreia()).isFalse();
        assertThat(existente.getTosse()).isFalse();
        assertThat(existente.getDificuldadeRespiratoria()).isFalse();
        assertThat(existente.getClaudicacao()).isFalse();
        assertThat(existente.getLesoesPele()).isFalse();
        assertThat(existente.getSecrecaoNasal()).isFalse();
        assertThat(existente.getSecrecaoOcular()).isFalse();
    }
}
