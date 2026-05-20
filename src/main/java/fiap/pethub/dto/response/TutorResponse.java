package fiap.pethub.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import fiap.pethub.dto.response.TutorEnderecoResponse;
import fiap.pethub.dto.response.TutorContatoResponse;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TutorResponse {

    @Schema(description = "Nome do tutor", example = "João Silva")
    private String nome;

    @Schema(description = "CPF do tutor", example = "12345678901")
    private String cpf;

    @Schema(description = "Email do tutor", example = "joao@email.com")
    private String email;

    @Schema(description = "Indica se o tutor está ativo")
    private Boolean ativo;

    @Schema(description = "Data de criação do cadastro")
    private LocalDateTime createdAt;

    @Schema(description = "Endereços do tutor")
    private List<TutorEnderecoResponse> enderecos;

    @Schema(description = "Contatos do tutor")
    private List<TutorContatoResponse> contatos;
}
