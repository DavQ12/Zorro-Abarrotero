package zorroAbarrotes.proyecto.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zorroAbarrotes.proyecto.model.entity.CategoriaEntity;
import zorroAbarrotes.proyecto.model.entity.UsuarioEntity;

public interface CategoriaRepository extends JpaRepository<CategoriaEntity, Long> {
}
