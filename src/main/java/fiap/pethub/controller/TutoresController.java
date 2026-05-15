package fiap.pethub.controller;

import fiap.pethub.dto.response.TutorResponse;
import fiap.pethub.service.TutorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/tutores")
@RequiredArgsConstructor
@Tag(name = "Tutores", description = "Busca de tutores (somente leitura — dados gerenciados pelo backend C#)")
public class TutoresController {

    private final TutorService service;

    @Operation(summary = "Buscar tutor por CPF", description = "Retorna os dados básicos do tutor para vinculação de pets")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Tutor encontrado"),
        @ApiResponse(responseCode = "404", description = "Tutor não encontrado")
    })
    @GetMapping("/buscar")
    public ResponseEntity<TutorResponse> findByCpf(
            @Parameter(description = "CPF do tutor", required = true)
            @RequestParam @NotBlank String cpf) {
        return ResponseEntity.ok(service.findByCpf(cpf));
    }
}

