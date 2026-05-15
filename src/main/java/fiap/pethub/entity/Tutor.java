package fiap.pethub.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "TB_TUTOR")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tutor {

    @Id
    @Column(name = "id", insertable = false, updatable = false)
    private Long id;

    @Column(name = "nome", insertable = false, updatable = false)
    private String nome;

    @Column(name = "cpf", insertable = false, updatable = false)
    private String cpf;

    @Column(name = "email", insertable = false, updatable = false)
    private String email;
}

