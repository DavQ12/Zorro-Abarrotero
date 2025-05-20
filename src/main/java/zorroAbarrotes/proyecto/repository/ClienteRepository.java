package zorroAbarrotes.proyecto.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import zorroAbarrotes.proyecto.model.entity.ClienteEntity;

import java.util.Optional;

public interface ClienteRepository extends JpaRepository<ClienteEntity, Long> {
    Optional<ClienteEntity> findByNombre(String nombre);
    
    Optional<ClienteEntity> findByCorreo(String correo);
    
    @Query("SELECT c FROM ClienteEntity c WHERE c.nombre = :login OR c.correo = :login")
    Optional<ClienteEntity> findByNombreOrCorreo(@Param("login") String login);
}
