package fiap.pethub.service;

import fiap.pethub.dto.request.VacinaTratamentoRequest;
import fiap.pethub.entity.Pet;
import fiap.pethub.entity.VacinaTratamento;
import fiap.pethub.enums.TipoLembrete;
import fiap.pethub.mapper.VacinaTratamentoMapper;
import fiap.pethub.messaging.LembreteSolicitado;
import fiap.pethub.repository.ConsultaRepository;
import fiap.pethub.repository.PetRepository;
import fiap.pethub.repository.VacinaTratamentoRepository;
import fiap.pethub.repository.VeterinarioRepository;
import fiap.pethub.security.EscopoDoUsuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VacinaTratamentoServiceTest {

    private static final LocalDate DOSE_ORIGINAL = LocalDate.of(2027, 11, 2);

    @Mock private VacinaTratamentoRepository repository;
    @Mock private PetRepository petRepository;
    @Mock private VeterinarioRepository veterinarioRepository;
    @Mock private ConsultaRepository consultaRepository;
    @Mock private VacinaTratamentoMapper mapper;
    @Mock private EscopoDoUsuario escopo;
    @Mock private ApplicationEventPublisher eventos;
    @InjectMocks private VacinaTratamentoService service;

    private VacinaTratamento vacina;
    private final VacinaTratamentoRequest request = new VacinaTratamentoRequest();

    @BeforeEach
    void vacinaComProximaDose() {
        Pet pet = Pet.builder().id(5L).nome("Rex").responsavelId(7L).build();
        vacina = VacinaTratamento.builder().id(31L).nome("V10").pet(pet).proximaDose(DOSE_ORIGINAL).build();
        when(repository.findById(31L)).thenReturn(Optional.of(vacina));
        when(repository.save(any(VacinaTratamento.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    private void mapperGrava(LocalDate proximaDose) {
        doAnswer(inv -> {
            ((VacinaTratamento) inv.getArgument(1)).setProximaDose(proximaDose);
            return null;
        }).when(mapper).updateEntity(eq(request), any(VacinaTratamento.class));
    }

    @Test
    void mudarAProximaDoseRepublicaOLembreteComANovaData() {
        LocalDate novaData = DOSE_ORIGINAL.plusMonths(1);
        mapperGrava(novaData);

        service.update(31L, request);

        ArgumentCaptor<LembreteSolicitado> lembrete = ArgumentCaptor.forClass(LembreteSolicitado.class);
        verify(eventos).publishEvent(lembrete.capture());
        assertThat(lembrete.getValue().tipo()).isEqualTo(TipoLembrete.VACINA);
        assertThat(lembrete.getValue().dataAgendada()).isEqualTo(novaData);
        assertThat(lembrete.getValue().referenciaId()).isEqualTo(31L);
    }

    @Test
    void editarOutroCampoSemMudarAProximaDoseNaoReenviaOLembrete() {
        mapperGrava(DOSE_ORIGINAL);

        service.update(31L, request);

        verify(eventos, never()).publishEvent(any(Object.class));
    }
}
