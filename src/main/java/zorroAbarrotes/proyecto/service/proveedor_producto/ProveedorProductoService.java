package zorroAbarrotes.proyecto.service.proveedor_producto;

import zorroAbarrotes.proyecto.model.entity.ProveedorProductoEntity;
import zorroAbarrotes.proyecto.model.id.ProveedorProductoId;

import java.util.List;

public interface ProveedorProductoService {
    ProveedorProductoEntity save(ProveedorProductoEntity actor);
    List<ProveedorProductoEntity> findAll();
    void deleteById(ProveedorProductoId id);
    ProveedorProductoEntity findById(ProveedorProductoId id);
}
