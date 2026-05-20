package fiap.pethub.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import fiap.pethub.entity.TutorEndereco;
import fiap.pethub.entity.TutorContato;

@Entity
@Table(name = "TB_TUTOR")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tutor {

    @Id
    @Column(name = "ID", insertable = false, updatable = false)
    private Long id;

    @Column(name = "NOME", length = 150, insertable = false, updatable = false)
    private String nome;

    @Column(name = "CPF", length = 11, insertable = false, updatable = false)
    private String cpf;

    @Column(name = "EMAIL", insertable = false, updatable = false)
    private String email;

    @Column(name = "SENHA", insertable = false, updatable = false)
    private String senha;

    @Column(name = "ATIVO", insertable = false, updatable = false)
    private Boolean ativo;

    @Column(name = "CREATED_AT", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "tutor", fetch = FetchType.LAZY)
    private List<TutorEndereco> enderecos;

    @OneToMany(mappedBy = "tutor", fetch = FetchType.LAZY)
    private List<TutorContato> contatos;
}
