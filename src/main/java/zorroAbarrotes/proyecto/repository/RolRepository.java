package zorroAbarrotes.proyecto.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zorroAbarrotes.proyecto.model.entity.RolEntity;
import zorroAbarrotes.proyecto.model.entity.UsuarioEntity;

public interface RolRepository extends JpaRepository<RolEntity, Long> {
}
