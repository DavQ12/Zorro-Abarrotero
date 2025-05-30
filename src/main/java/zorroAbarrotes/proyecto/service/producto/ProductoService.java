package zorroAbarrotes.proyecto.service.producto;

import zorroAbarrotes.proyecto.model.entity.ProductoEntity;

import java.util.List;

public interface ProductoService {
    ProductoEntity save(ProductoEntity actor);
    List<ProductoEntity> findAll();
    void deleteById(Long id);
    ProductoEntity findById(Long id);
    List<ProductoEntity> findByImagenNotNull();
}
