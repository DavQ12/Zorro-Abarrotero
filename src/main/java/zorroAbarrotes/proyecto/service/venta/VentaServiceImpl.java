package zorroAbarrotes.proyecto.service.venta;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import zorroAbarrotes.proyecto.model.entity.VentaEntity;
import zorroAbarrotes.proyecto.repository.VentaRepository;

import java.util.List;
import java.util.Optional;

@Service
public class VentaServiceImpl implements VentaService {
    @Autowired
    VentaRepository ventaRepository;

    @Override
    public VentaEntity save(VentaEntity actor) {
        return ventaRepository.save(actor);
    }

    @Override
    public List<VentaEntity> findAll() {
        return ventaRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        ventaRepository.deleteById(id);
    }

    @Override
    public VentaEntity findById(Long id) {
        Optional<VentaEntity> actor = ventaRepository.findById(id);
        return actor.orElse(null);
    }
}
