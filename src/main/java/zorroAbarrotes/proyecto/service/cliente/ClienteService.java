package zorroAbarrotes.proyecto.service.cliente;

import zorroAbarrotes.proyecto.model.entity.ClienteEntity;

import java.util.List;

import java.util.Optional;

public interface ClienteService {
    ClienteEntity save(ClienteEntity actor);
    List<ClienteEntity> findAll();
    void deleteById(Long id);
    ClienteEntity findById(Long id);
    Optional<ClienteEntity> findByIdClienteEntity(Long id);
    ClienteEntity findByCorreo(String correo);
    ClienteEntity findByTelefono(String telefono);
    ClienteEntity findByIdentificador(String identificador);
}
