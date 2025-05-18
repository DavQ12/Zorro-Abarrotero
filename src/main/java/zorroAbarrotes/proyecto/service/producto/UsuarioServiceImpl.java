package zorroAbarrotes.proyecto.service.producto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import zorroAbarrotes.proyecto.model.entity.UsuarioEntity;
import zorroAbarrotes.proyecto.repository.UsuarioRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioService {
    @Autowired
    UsuarioRepository usuarioRepository;

    @Override
    public UsuarioEntity save(UsuarioEntity actor) {
        return usuarioRepository.save(actor);
    }

    @Override
    public List<UsuarioEntity> findAll() {
        return usuarioRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        usuarioRepository.deleteById(id);
    }

    @Override
    public UsuarioEntity findById(Long id) {
        Optional<UsuarioEntity> actor = usuarioRepository.findById(id);
        return actor.orElse(null);
    }
}
