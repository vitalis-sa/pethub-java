package fiap.pethub.client;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LembreteRequest {

    @Schema(description = "ID do tutor no sistema C#")
    private Long tutorId;

    @Schema(description = "ID do pet")
    private Long petId;

    @Schema(description = "Tipo do lembrete: VACINA, CONSULTA, EXAME, MEDICAMENTO")
    private String tipo;

    @Schema(description = "Data agendada para o evento")
    private LocalDate dataAgendada;

    @Schema(description = "Mensagem descritiva do lembrete")
    private String mensagem;
}

