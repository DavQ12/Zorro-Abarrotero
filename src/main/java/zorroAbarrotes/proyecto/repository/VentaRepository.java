package zorroAbarrotes.proyecto.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zorroAbarrotes.proyecto.model.entity.UsuarioEntity;
import zorroAbarrotes.proyecto.model.entity.VentaEntity;

public interface VentaRepository extends JpaRepository<VentaEntity, Long> {
}
