package zorroAbarrotes.proyecto.service.producto_carrito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zorroAbarrotes.proyecto.model.entity.ProductoCarritoEntity;
import zorroAbarrotes.proyecto.model.id.ProductoCarritoId;
import zorroAbarrotes.proyecto.repository.ProductoCarritoRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoCarritoServiceImpl implements ProductoCarritoService {
    
    private final ProductoCarritoRepository productoCarritoRepository;

    @Autowired
    public ProductoCarritoServiceImpl(ProductoCarritoRepository productoCarritoRepository) {
        this.productoCarritoRepository = productoCarritoRepository;
    }

    @Override
    public List<ProductoCarritoEntity> findAll() {
        return productoCarritoRepository.findAll();
    }

    @Override
    public Optional<ProductoCarritoEntity> findById(ProductoCarritoId id) {
        return productoCarritoRepository.findById(id);
    }

    @Override
    public ProductoCarritoEntity save(ProductoCarritoEntity productoCarrito) {
        return productoCarritoRepository.save(productoCarrito);
    }

    @Override
    public void deleteById(ProductoCarritoId id) {
        productoCarritoRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteByCarritoId(Long carritoId) {
        productoCarritoRepository.deleteByCarritoId(carritoId);
    }
}
