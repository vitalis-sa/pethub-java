package fiap.pethub.repository;

import fiap.pethub.entity.Consulta;
import fiap.pethub.enums.StatusConsulta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsultaRepository extends JpaRepository<Consulta, Long> {
    Page<Consulta> findByPetId(Long petId, Pageable pageable);
    Page<Consulta> findByVeterinarioId(Long veterinarioId, Pageable pageable);
    Page<Consulta> findByStatus(StatusConsulta status, Pageable pageable);
    Page<Consulta> findByPetIdAndStatus(Long petId, StatusConsulta status, Pageable pageable);
}

