package fiap.pethub.repository;

import fiap.pethub.entity.Pet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetRepository extends JpaRepository<Pet, Long> {
    Page<Pet> findByTutorId(Long tutorId, Pageable pageable);
    Page<Pet> findByVeterinarioResponsavelId(Long veterinarioId, Pageable pageable);
    Page<Pet> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
}

