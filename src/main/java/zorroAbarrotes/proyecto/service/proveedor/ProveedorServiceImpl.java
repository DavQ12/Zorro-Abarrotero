package zorroAbarrotes.proyecto.service.proveedor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import zorroAbarrotes.proyecto.model.entity.ProveedorEntity;
import zorroAbarrotes.proyecto.repository.ProveedorRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ProveedorServiceImpl implements ProveedorService {
    @Autowired
    ProveedorRepository proveedorRepository;

    @Override
    public ProveedorEntity save(ProveedorEntity actor) {
        return proveedorRepository.save(actor);
    }

    @Override
    public List<ProveedorEntity> findAll() {
        return proveedorRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        proveedorRepository.deleteById(id);
    }

    @Override
    public ProveedorEntity findById(Long id) {
        Optional<ProveedorEntity> actor = proveedorRepository.findById(id);
        return actor.orElse(null);
    }

    @Override
    public ProveedorEntity findByCorreo(String correo) {
        Optional<ProveedorEntity> proveedor = proveedorRepository.findByCorreo(correo);
        return proveedor.orElse(null);
    }

    @Override
    public ProveedorEntity findByTelefono(String telefono) {
        Optional<ProveedorEntity> proveedor = proveedorRepository.findByTelefono(telefono);

        return proveedor.orElse(null);
    }
}
