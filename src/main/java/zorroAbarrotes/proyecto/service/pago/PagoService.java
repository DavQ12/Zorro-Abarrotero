package zorroAbarrotes.proyecto.service.pago;

import zorroAbarrotes.proyecto.model.entity.PagoEntity;

import java.util.List;

public interface PagoService {
    PagoEntity save(PagoEntity actor);
    List<PagoEntity> findAll();
    void deleteById(Long id);
    PagoEntity findById(Long id);
}
