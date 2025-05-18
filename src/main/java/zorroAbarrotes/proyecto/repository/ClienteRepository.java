package zorroAbarrotes.proyecto.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zorroAbarrotes.proyecto.model.entity.ClienteEntity;
import zorroAbarrotes.proyecto.model.entity.UsuarioEntity;

public interface ClienteRepository extends JpaRepository<ClienteEntity, Long> {
}
