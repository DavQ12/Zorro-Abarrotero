package zorroAbarrotes.proyecto.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import zorroAbarrotes.proyecto.model.entity.ProductoCarritoEntity;
import zorroAbarrotes.proyecto.model.entity.UsuarioEntity;
import zorroAbarrotes.proyecto.model.id.ProductoCarritoId;

public interface ProductoCarritoRepository extends JpaRepository<ProductoCarritoEntity, ProductoCarritoId> {
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM producto_carrito WHERE id_carrito = :carritoId", nativeQuery = true)
    void deleteByCarritoId(Long carritoId);
}
