package zorroAbarrotes.proyecto.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zorroAbarrotes.proyecto.model.entity.ProveedorEntity;
import zorroAbarrotes.proyecto.model.entity.UsuarioEntity;

import java.util.Optional;

public interface ProveedorRepository extends JpaRepository<ProveedorEntity, Long> {
    Optional<ProveedorEntity> findByCorreo(String correo);
    Optional<ProveedorEntity> findByTelefono(String telefono);
}
