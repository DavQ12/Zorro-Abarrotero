package zorroAbarrotes.proyecto.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import zorroAbarrotes.proyecto.model.entity.ProductoEntity;
import zorroAbarrotes.proyecto.model.entity.UsuarioEntity;

public interface ProductoRepository extends JpaRepository<ProductoEntity, Long> {
    @Query("SELECT p FROM ProductoEntity p WHERE p.imagen IS NOT NULL AND p.imagen != ''")
    List<ProductoEntity> findByImagenNotNullAndImagenNotEmpty();
}
