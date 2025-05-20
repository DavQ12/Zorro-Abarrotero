package zorroAbarrotes.proyecto.service.cliente;

import zorroAbarrotes.proyecto.model.entity.ClienteEntity;

import java.util.List;

public interface ClienteService {
    ClienteEntity save(ClienteEntity actor);
    List<ClienteEntity> findAll();
    void deleteById(Long id);
    ClienteEntity findById(Long id);
}
