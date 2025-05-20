package zorroAbarrotes.proyecto.service.rol;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import zorroAbarrotes.proyecto.model.entity.RolEntity;
import zorroAbarrotes.proyecto.repository.RolRepository;
import java.util.List;
import java.util.Optional;

@Service
public class RolServiceImpl implements RolService {
    @Autowired
    RolRepository rolRepository;

    @Override
    public RolEntity save(RolEntity rol) {
        return rolRepository.save(rol);
    }

    @Override
    public List<RolEntity> findAll() {
        return rolRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        rolRepository.deleteById(id);
    }

    @Override
    public RolEntity findById(Long id) {
        Optional<RolEntity> rol = rolRepository.findById(id);
        return rol.orElse(null);
    }
}
