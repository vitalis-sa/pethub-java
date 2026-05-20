package fiap.pethub.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TutorContatoResponse {

    @Schema(description = "ID do contato")
    private Long id;

    @Schema(description = "Tipo do contato", example = "CELULAR")
    private String tipo;

    @Schema(description = "Valor do contato", example = "(11) 99999-9999")
    private String valor;

    @Schema(description = "Indica se é o contato principal")
    private Boolean principal;
}

