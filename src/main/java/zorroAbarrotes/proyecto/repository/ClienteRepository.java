package zorroAbarrotes.proyecto.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import zorroAbarrotes.proyecto.model.entity.ClienteEntity;

import java.util.Optional;

public interface ClienteRepository extends JpaRepository<ClienteEntity, Long> {
    @Query("SELECT c FROM ClienteEntity c WHERE c.correo = ?1 OR c.telefono = ?1 OR c.numCuenta = ?1")
    Optional<ClienteEntity> findByIdentificador(String identificador);

    Optional<ClienteEntity> findByCorreo(String correo);
    Optional<ClienteEntity> findByTelefono(String telefono);

}
