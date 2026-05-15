package fiap.pethub.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
@Entity @Table(name="TB_LEITURA_WEARABLE") @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LeituraWearable {
    @Id @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="leitura_seq") @SequenceGenerator(name="leitura_seq",sequenceName="SQ_LEITURA_WEARABLE",allocationSize=1) private Long id;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="pet_id",nullable=false) private Pet pet;
    @Column(name="timestamp",nullable=false) private LocalDateTime timestamp;
    @Column(name="temperatura_corporal",nullable=false) private Double temperaturaCorporal;
    @Column(name="frequencia_cardiaca",nullable=false) private Integer frequenciaCardiaca;
    @Column(name="anomalia_detectada",nullable=false) @Builder.Default private Boolean anomaliaDetectada=false;
    @Column(name="tipo_anomalia") private String tipoAnomalia;
}

