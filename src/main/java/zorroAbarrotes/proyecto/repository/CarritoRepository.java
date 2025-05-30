package zorroAbarrotes.proyecto.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import zorroAbarrotes.proyecto.model.entity.CarritoEntity;
import zorroAbarrotes.proyecto.model.entity.UsuarioEntity;

public interface CarritoRepository extends JpaRepository<CarritoEntity, Long> {
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM carrito WHERE id_carrito = :id", nativeQuery = true)
    void deleteByIdWithCascade(Long id);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM producto_carrito WHERE id_carrito = :carritoId", nativeQuery = true)
    void deleteProductoCarritoByCarritoId(Long carritoId);
}
