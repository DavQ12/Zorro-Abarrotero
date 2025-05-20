package zorroAbarrotes.proyecto.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zorroAbarrotes.proyecto.model.entity.CarritoEntity;
import zorroAbarrotes.proyecto.model.entity.UsuarioEntity;

public interface CarritoRepository extends JpaRepository<CarritoEntity, Long> {
}
