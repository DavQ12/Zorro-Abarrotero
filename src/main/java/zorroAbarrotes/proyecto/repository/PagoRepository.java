package zorroAbarrotes.proyecto.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zorroAbarrotes.proyecto.model.entity.PagoEntity;

public interface PagoRepository extends JpaRepository<PagoEntity, Long> {
}
