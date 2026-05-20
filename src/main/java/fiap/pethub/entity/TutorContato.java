package fiap.pethub.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "TB_TUTOR_CONTATO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TutorContato {

    @Id
    @Column(name = "ID", insertable = false, updatable = false)
    private Long id;

    @Column(name = "TUTOR_ID", insertable = false, updatable = false)
    private Long tutorId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TUTOR_ID", insertable = false, updatable = false)
    private Tutor tutor;

    @Column(name = "TIPO", insertable = false, updatable = false)
    private String tipo;

    @Column(name = "VALOR", insertable = false, updatable = false)
    private String valor;

    @Column(name = "PRINCIPAL", insertable = false, updatable = false)
    private Boolean principal;
}

