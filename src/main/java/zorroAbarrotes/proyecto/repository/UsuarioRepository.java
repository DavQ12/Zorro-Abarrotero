package zorroAbarrotes.proyecto.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import zorroAbarrotes.proyecto.model.entity.UsuarioEntity;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Long> {
    Optional<UsuarioEntity> findByUsername (String usuario);
    
    Optional<UsuarioEntity> findByCorreo(String correo);
    //Optional<UsuarioEntity> findUsuarioEntityByUsuario (String usuario);
//    @Query("SELECT u FROM usuario  u WHERE u.usuario = :login OR u.correo = :login")
//    Optional<UsuarioEntity> findByUsuarioOrCorreo(@Param("login") String login);
}
