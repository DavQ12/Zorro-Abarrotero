package zorroAbarrotes.proyecto.service.pedido;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import zorroAbarrotes.proyecto.model.entity.PedidoEntity;
import zorroAbarrotes.proyecto.repository.PedidoRepository;

import java.util.List;
import java.util.Optional;

@Service
public class PedidoServiceImpl implements PedidoService {
    @Autowired
    PedidoRepository pedidoRepository;

    @Override
    public PedidoEntity save(PedidoEntity actor) {
        return pedidoRepository.save(actor);
    }

    @Override
    public List<PedidoEntity> findAll() {
        return pedidoRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        pedidoRepository.deleteById(id);
    }

    @Override
    public PedidoEntity findById(Long id) {
        Optional<PedidoEntity> actor = pedidoRepository.findById(id);
        return actor.orElse(null);
    }
}
