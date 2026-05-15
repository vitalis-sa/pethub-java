package fiap.pethub.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeituraWearableResponse {

    @Schema(description = "Nome do pet", example = "Rex")
    private String nomePet;

    @Schema(description = "Timestamp da leitura", example = "2026-06-01T10:00:00")
    private LocalDateTime timestamp;

    @Schema(description = "Temperatura corporal em °C", example = "38.5")
    private Double temperaturaCorporal;

    @Schema(description = "Frequência cardíaca em bpm", example = "90")
    private Integer frequenciaCardiaca;

    @Schema(description = "Indica se anomalia foi detectada", example = "false")
    private Boolean anomaliaDetectada;

    @Schema(description = "Tipo da anomalia detectada", example = "Taquicardia")
    private String tipoAnomalia;
}

