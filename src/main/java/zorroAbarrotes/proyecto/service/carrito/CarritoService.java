package zorroAbarrotes.proyecto.service.carrito;

import zorroAbarrotes.proyecto.model.entity.CarritoEntity;

import java.util.List;

public interface CarritoService {
    CarritoEntity save(CarritoEntity actor);
    List<CarritoEntity> findAll();
    void deleteById(Long id);
    CarritoEntity findById(Long id);
}
