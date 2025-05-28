package zorroAbarrotes.proyecto.service.pago;

import zorroAbarrotes.proyecto.model.entity.PagoEntity;

import java.util.List;
import java.util.Optional;

public interface PagoService {
    PagoEntity save(PagoEntity actor);
    List<PagoEntity> findAll();
    void deleteById(Long id);
    Optional<PagoEntity> findById(Long id);
}
