package zorroAbarrotes.proyecto.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zorroAbarrotes.proyecto.model.entity.ProductoCarritoEntity;
import zorroAbarrotes.proyecto.model.entity.UsuarioEntity;
import zorroAbarrotes.proyecto.model.id.ProductoCarritoId;

public interface ProductoCarritoRepository extends JpaRepository<ProductoCarritoEntity, ProductoCarritoId> {
}
