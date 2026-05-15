package fiap.pethub.repository;

import fiap.pethub.entity.LeituraWearable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeituraWearableRepository extends JpaRepository<LeituraWearable, Long> {
    Page<LeituraWearable> findByPetId(Long petId, Pageable pageable);
    Page<LeituraWearable> findByPetIdAndAnomaliaDetectadaTrue(Long petId, Pageable pageable);
}

