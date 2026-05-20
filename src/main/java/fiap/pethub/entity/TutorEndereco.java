package fiap.pethub.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "TB_TUTOR_ENDERECO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TutorEndereco {

    @Id
    @Column(name = "ID", insertable = false, updatable = false)
    private Long id;

    @Column(name = "TUTOR_ID", insertable = false, updatable = false)
    private Long tutorId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TUTOR_ID", insertable = false, updatable = false)
    private Tutor tutor;

    @Column(name = "LOGRADOURO", insertable = false, updatable = false)
    private String logradouro;

    @Column(name = "NUMERO", insertable = false, updatable = false)
    private String numero;

    @Column(name = "COMPLEMENTO", insertable = false, updatable = false)
    private String complemento;

    @Column(name = "BAIRRO", insertable = false, updatable = false)
    private String bairro;

    @Column(name = "CIDADE", insertable = false, updatable = false)
    private String cidade;

    @Column(name = "ESTADO", length = 2, insertable = false, updatable = false)
    private String estado;

    @Column(name = "CEP", length = 8, insertable = false, updatable = false)
    private String cep;

    @Column(name = "PRINCIPAL", insertable = false, updatable = false)
    private Boolean principal;
}

