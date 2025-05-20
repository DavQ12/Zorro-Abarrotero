package zorroAbarrotes.proyecto.service.carrito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import zorroAbarrotes.proyecto.model.entity.CarritoEntity;
import zorroAbarrotes.proyecto.repository.CarritoRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CarritoServiceImpl implements CarritoService {
    @Autowired
    CarritoRepository carritoRepository;

    @Override
    public CarritoEntity save(CarritoEntity actor) {
        return carritoRepository.save(actor);
    }

    @Override
    public List<CarritoEntity> findAll() {
        return carritoRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        carritoRepository.deleteById(id);
    }

    @Override
    public CarritoEntity findById(Long id) {
        Optional<CarritoEntity> actor = carritoRepository.findById(id);
        return actor.orElse(null);
    }
}
