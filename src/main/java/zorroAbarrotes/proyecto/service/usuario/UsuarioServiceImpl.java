package zorroAbarrotes.proyecto.service.usuario;

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
    public UsuarioEntity save(UsuarioEntity usuario) {
        return usuarioRepository.save(usuario);
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
        Optional<UsuarioEntity> usuario = usuarioRepository.findById(id);
        return usuario.orElse(null);
    }

    @Override
    public Optional<UsuarioEntity> findByIdCUsuarioEntity(Long id) {
        return usuarioRepository.findById(id);
    }

    @Override
    public UsuarioEntity findByCorreo(String correo) {
        Optional<UsuarioEntity> usuario = usuarioRepository.findByCorreo(correo);
        return usuario.orElse(null);
    }

    @Override
    public UsuarioEntity findByUsername(String usurio) {
        Optional<UsuarioEntity> usuario = usuarioRepository.findByUsername(usurio);
        return usuario.orElse(null);
    }


}
