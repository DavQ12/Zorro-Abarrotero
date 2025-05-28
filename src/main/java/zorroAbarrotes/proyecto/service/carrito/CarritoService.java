package zorroAbarrotes.proyecto.service.carrito;

import zorroAbarrotes.proyecto.model.entity.CarritoEntity;

import java.util.List;
import java.util.Optional;

public interface CarritoService {
    List<CarritoEntity> findAll();
    List<CarritoEntity> findAllDisponibles();
    Optional<CarritoEntity> findById(Long id);
    CarritoEntity save(CarritoEntity carrito);
    void deleteById(Long id);
    void deleteByIdWithCascade(Long id);
    void deleteProductoCarritoByCarritoId(Long carritoId);
}
