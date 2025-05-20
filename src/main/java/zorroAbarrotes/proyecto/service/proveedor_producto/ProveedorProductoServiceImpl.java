package zorroAbarrotes.proyecto.service.proveedor_producto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import zorroAbarrotes.proyecto.model.entity.ProveedorProductoEntity;
import zorroAbarrotes.proyecto.model.id.ProveedorProductoId;
import zorroAbarrotes.proyecto.repository.ProveedorProductoRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ProveedorProductoServiceImpl implements ProveedorProductoService {
    @Autowired
    ProveedorProductoRepository proveedorProductoRepository;

    @Override
    public ProveedorProductoEntity save(ProveedorProductoEntity actor) {
        return proveedorProductoRepository.save(actor);
    }

    @Override
    public List<ProveedorProductoEntity> findAll() {
        return proveedorProductoRepository.findAll();
    }

    @Override
    public void deleteById(ProveedorProductoId id) {
        proveedorProductoRepository.deleteById(id);
    }

    @Override
    public ProveedorProductoEntity findById(ProveedorProductoId id) {
        Optional<ProveedorProductoEntity> actor = proveedorProductoRepository.findById(id);
        return actor.orElse(null);
    }
}
