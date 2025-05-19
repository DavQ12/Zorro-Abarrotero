package zorroAbarrotes.proyecto.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zorroAbarrotes.proyecto.model.entity.ProveedorProductoEntity;
import zorroAbarrotes.proyecto.model.entity.UsuarioEntity;
import zorroAbarrotes.proyecto.model.id.ProveedorProductoId;


public interface ProveedorProductoRepository extends JpaRepository<ProveedorProductoEntity, ProveedorProductoId> {
}
