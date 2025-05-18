package zorroAbarrotes.proyecto.service.cliente;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import zorroAbarrotes.proyecto.model.entity.ClienteEntity;
import zorroAbarrotes.proyecto.repository.ClienteRepository;
import java.util.List;
import java.util.Optional;

@Service
public class ClienteServiceImpl implements ClienteService {
    @Autowired
    ClienteRepository clienteRepository;

    @Override
    public ClienteEntity save(ClienteEntity actor) {
        return clienteRepository.save(actor);
    }

    @Override
    public List<ClienteEntity> findAll() {
        return clienteRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        clienteRepository.deleteById(id);
    }

    @Override
    public ClienteEntity findById(Long id) {
        Optional<ClienteEntity> actor = clienteRepository.findById(id);
        return actor.orElse(null);
    }
}
