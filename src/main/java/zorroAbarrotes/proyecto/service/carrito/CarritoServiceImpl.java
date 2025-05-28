package zorroAbarrotes.proyecto.service.carrito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zorroAbarrotes.proyecto.model.entity.CarritoEntity;
import zorroAbarrotes.proyecto.repository.CarritoRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CarritoServiceImpl implements CarritoService {
    private final CarritoRepository carritoRepository;

    @Autowired
    public CarritoServiceImpl(CarritoRepository carritoRepository) {
        this.carritoRepository = carritoRepository;
    }

    @Override
    public CarritoEntity save(CarritoEntity carrito) {
        return carritoRepository.save(carrito);
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
    public List<CarritoEntity> findAllDisponibles() {
        return carritoRepository.findAll();
    }

    @Override
    public Optional<CarritoEntity> findById(Long id) {
        return carritoRepository.findById(id);
    }

    @Override
    public void deleteByIdWithCascade(Long id) {
        Optional<CarritoEntity> carrito = carritoRepository.findById(id);
        if (carrito.isPresent()) {
            carritoRepository.deleteByIdWithCascade(id);
        }
    }

    @Override
    @Transactional
    public void deleteProductoCarritoByCarritoId(Long carritoId) {
        carritoRepository.deleteProductoCarritoByCarritoId(carritoId);
    }
}
