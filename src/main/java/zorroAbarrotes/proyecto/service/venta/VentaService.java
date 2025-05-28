package zorroAbarrotes.proyecto.service.venta;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import zorroAbarrotes.proyecto.model.entity.CarritoEntity;
import zorroAbarrotes.proyecto.model.entity.VentaEntity;

import java.util.List;
import java.util.Optional;

public interface VentaService {
    List<VentaEntity> findAll();
    Optional<VentaEntity> findById(Long id);
    VentaEntity save(VentaEntity venta);
    void deleteById(Long id);
    List<VentaEntity> findByCarrito(CarritoEntity carrito);
    List<VentaEntity> findAllWithDetails();
    Page<VentaEntity> findAllWithDetails(Pageable pageable);
    Optional<VentaEntity> findByIdWithAllDetails(Long id);

    Long countAll();
    Long countAllNative();
    void deleteByIdWithCascade(Long id);
}
