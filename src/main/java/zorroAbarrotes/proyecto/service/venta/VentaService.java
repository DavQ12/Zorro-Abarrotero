package zorroAbarrotes.proyecto.service.venta;

import zorroAbarrotes.proyecto.model.entity.VentaEntity;

import java.util.List;

public interface VentaService {
    VentaEntity save(VentaEntity actor);
    List<VentaEntity> findAll();
    void deleteById(Long id);
    VentaEntity findById(Long id);
}
