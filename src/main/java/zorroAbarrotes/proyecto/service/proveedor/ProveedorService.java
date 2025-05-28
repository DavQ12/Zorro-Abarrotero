package zorroAbarrotes.proyecto.service.proveedor;

import zorroAbarrotes.proyecto.model.entity.ProveedorEntity;

import java.util.List;

public interface ProveedorService {
    ProveedorEntity save(ProveedorEntity actor);
    List<ProveedorEntity> findAll();
    void deleteById(Long id);
    ProveedorEntity findById(Long id);
    ProveedorEntity findByCorreo(String correo);
    ProveedorEntity findByTelefono(String telefono);
}
