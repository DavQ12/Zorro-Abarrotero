package zorroAbarrotes.proyecto.service.rol;

import zorroAbarrotes.proyecto.model.entity.RolEntity;

import java.util.List;

public interface RolService {
    RolEntity save(RolEntity actor);
    List<RolEntity> findAll();
    void deleteById(Long id);
    RolEntity findById(Long id);
}
