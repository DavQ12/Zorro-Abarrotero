package zorroAbarrotes.proyecto.service.producto_carrito;

import zorroAbarrotes.proyecto.model.entity.ProductoCarritoEntity;
import zorroAbarrotes.proyecto.model.id.ProductoCarritoId;

import java.util.List;

public interface ProductoCarritoService {
    ProductoCarritoEntity save(ProductoCarritoEntity actor);
    List<ProductoCarritoEntity> findAll();
    void deleteById(ProductoCarritoId id);
    ProductoCarritoEntity findById(ProductoCarritoId id);
}
