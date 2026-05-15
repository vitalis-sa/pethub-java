package fiap.pethub.controller;

import fiap.pethub.dto.request.LeituraWearableRequest;
import fiap.pethub.dto.response.LeituraWearableResponse;
import fiap.pethub.service.LeituraWearableService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/leituras-wearable")
@RequiredArgsConstructor
@Tag(name = "Leituras Wearable", description = "Dados de monitoramento IoT do pet — somente armazenamento, não gera diagnósticos automaticamente")
public class LeiturasWearableController {

    private final LeituraWearableService service;

    @Operation(summary = "Listar leituras wearable", description = "Filtrável por petId e apenasAnomalias")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")})
    @GetMapping
    public ResponseEntity<Page<LeituraWearableResponse>> findAll(
            @Parameter(description = "Filtrar por pet") @RequestParam(required = false) Long petId,
            @Parameter(description = "Retornar apenas leituras com anomalia detectada") @RequestParam(required = false) Boolean apenasAnomalias,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(service.findAll(petId, apenasAnomalias, pageable));
    }

    @Operation(summary = "Buscar leitura por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Leitura encontrada"),
        @ApiResponse(responseCode = "404", description = "Leitura não encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<LeituraWearableResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @Operation(summary = "Registrar leitura wearable", description = "Recebe dados do dispositivo IoT. Quando anomaliaDetectada=true, a leitura é armazenada para alerta ao veterinário.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Leitura registrada"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "404", description = "Pet não encontrado")
    })
    @PostMapping
    public ResponseEntity<LeituraWearableResponse> create(@Valid @RequestBody LeituraWearableRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @Operation(summary = "Remover leitura wearable")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Removido com sucesso"),
        @ApiResponse(responseCode = "404", description = "Leitura não encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

