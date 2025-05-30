package zorroAbarrotes.proyecto.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zorroAbarrotes.proyecto.model.entity.PedidoEntity;
import zorroAbarrotes.proyecto.model.entity.UsuarioEntity;

public interface PedidoRepository extends JpaRepository<PedidoEntity, Long> {
}
