package zorroAbarrotes.proyecto.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zorroAbarrotes.proyecto.model.entity.UsuarioEntity;

public interface ProductoRepository extends JpaRepository<UsuarioEntity, Long> {
}
