package zorroAbarrotes.proyecto.service.pago;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import zorroAbarrotes.proyecto.model.entity.PagoEntity;
import zorroAbarrotes.proyecto.repository.PagoRepository;

import java.util.List;
import java.util.Optional;

@Service
public class PagoServiceImpl implements PagoService {
    @Autowired
    PagoRepository pagoRepository;

    @Override
    public PagoEntity save(PagoEntity actor) {
        return pagoRepository.save(actor);
    }

    @Override
    public List<PagoEntity> findAll() {
        return pagoRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        pagoRepository.deleteById(id);
    }

    @Override
    public Optional<PagoEntity> findById(Long id) {
        return pagoRepository.findById(id);
    }
}
