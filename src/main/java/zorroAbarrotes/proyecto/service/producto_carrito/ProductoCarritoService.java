package zorroAbarrotes.proyecto.service.producto_carrito;

import zorroAbarrotes.proyecto.model.entity.ProductoCarritoEntity;
import zorroAbarrotes.proyecto.model.id.ProductoCarritoId;

import java.util.List;
import java.util.Optional;

public interface ProductoCarritoService {
    List<ProductoCarritoEntity> findAll();
    Optional<ProductoCarritoEntity> findById(ProductoCarritoId id);
    ProductoCarritoEntity save(ProductoCarritoEntity productoCarrito);
    void deleteById(ProductoCarritoId id);
    void deleteByCarritoId(Long carritoId);
}
