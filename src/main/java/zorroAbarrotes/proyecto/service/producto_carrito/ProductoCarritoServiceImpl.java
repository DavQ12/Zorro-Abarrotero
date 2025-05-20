package zorroAbarrotes.proyecto.service.producto_carrito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import zorroAbarrotes.proyecto.model.entity.ProductoCarritoEntity;
import zorroAbarrotes.proyecto.model.id.ProductoCarritoId;
import zorroAbarrotes.proyecto.repository.ProductoCarritoRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoCarritoServiceImpl implements ProductoCarritoService {
    @Autowired
    ProductoCarritoRepository productoCarritoRepository;

    @Override
    public ProductoCarritoEntity save(ProductoCarritoEntity actor) {
        return productoCarritoRepository.save(actor);
    }

    @Override
    public List<ProductoCarritoEntity> findAll() {
        return productoCarritoRepository.findAll();
    }

    @Override
    public void deleteById(ProductoCarritoId id) {
        productoCarritoRepository.deleteById(id);
    }

    @Override
    public ProductoCarritoEntity findById(ProductoCarritoId id) {
        Optional<ProductoCarritoEntity> actor = productoCarritoRepository.findById(id);
        return actor.orElse(null);
    }
}
