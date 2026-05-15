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
public class TutorResponse {

    @Schema(description = "Nome do tutor", example = "João Silva")
    private String nome;

    @Schema(description = "Email do tutor", example = "joao@email.com")
    private String email;
}

