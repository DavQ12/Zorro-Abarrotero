package zorroAbarrotes.proyecto.service.producto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import zorroAbarrotes.proyecto.model.entity.ProductoEntity;
import zorroAbarrotes.proyecto.repository.ProductoRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoServiceImpl implements ProductoService {
    @Autowired
    ProductoRepository productoRepository;

    @Override
    public ProductoEntity save(ProductoEntity actor) {
        return productoRepository.save(actor);
    }

    @Override
    public List<ProductoEntity> findAll() {
        return productoRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        productoRepository.deleteById(id);
    }

    @Override
    public ProductoEntity findById(Long id) {
        Optional<ProductoEntity> actor = productoRepository.findById(id);
        return actor.orElse(null);
    }
}
