package zorroAbarrotes.proyecto.service.pedido;

import zorroAbarrotes.proyecto.model.entity.PedidoEntity;

import java.util.List;

public interface PedidoService {
    PedidoEntity save(PedidoEntity actor);
    List<PedidoEntity> findAll();
    void deleteById(Long id);
    PedidoEntity findById(Long id);
}
