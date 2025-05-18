package zorroAbarrotes.proyecto.service.usuario;

import zorroAbarrotes.proyecto.model.entity.UsuarioEntity;

import java.util.List;

public interface UsuarioService {
    UsuarioEntity save(UsuarioEntity actor);
    List<UsuarioEntity> findAll();
    void deleteById(Long id);
    UsuarioEntity findById(Long id);
}
