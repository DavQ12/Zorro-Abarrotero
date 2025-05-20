package zorroAbarrotes.proyecto.service.categoria;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import zorroAbarrotes.proyecto.model.entity.CategoriaEntity;
import zorroAbarrotes.proyecto.repository.CategoriaRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CategoriaServiceImpl implements CategoriaService {
    @Autowired
    CategoriaRepository categoriaRepository;

    @Override
    public CategoriaEntity save(CategoriaEntity actor) {
        return categoriaRepository.save(actor);
    }

    @Override
    public List<CategoriaEntity> findAll() {
        return categoriaRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        categoriaRepository.deleteById(id);
    }

    @Override
    public CategoriaEntity findById(Long id) {
        Optional<CategoriaEntity> actor = categoriaRepository.findById(id);
        return actor.orElse(null);
    }
}
