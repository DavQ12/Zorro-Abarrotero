package zorroAbarrotes.proyecto.service.usuario;

import zorroAbarrotes.proyecto.model.entity.UsuarioEntity;

import java.util.List;
import java.util.Optional;

public interface UsuarioService {
    UsuarioEntity save(UsuarioEntity actor);
    List<UsuarioEntity> findAll();
    void deleteById(Long id);
    UsuarioEntity findById(Long id);
    Optional<UsuarioEntity> findByIdCUsuarioEntity(Long id);
    //UsuarioEntity findByUsername(String username);
    UsuarioEntity findByCorreo(String correo);
    UsuarioEntity findByUsername(String usurio);
}
