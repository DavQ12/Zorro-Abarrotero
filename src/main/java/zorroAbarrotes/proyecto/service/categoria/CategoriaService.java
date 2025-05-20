package zorroAbarrotes.proyecto.service.categoria;

import zorroAbarrotes.proyecto.model.entity.CategoriaEntity;

import java.util.List;

public interface CategoriaService {
    CategoriaEntity save(CategoriaEntity actor);
    List<CategoriaEntity> findAll();
    void deleteById(Long id);
    CategoriaEntity findById(Long id);
}
