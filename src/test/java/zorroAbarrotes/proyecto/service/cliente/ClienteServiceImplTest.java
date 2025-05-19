package zorroAbarrotes.proyecto.service.cliente;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import zorroAbarrotes.proyecto.model.entity.ClienteEntity;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ClienteServiceImplTest {

    @Autowired
    ClienteServiceImpl clienteService;

    @Test
    void saveCliente() {
        ClienteEntity cliente = ClienteEntity.builder()
                .nombre("Test2")
                .apellidoP("Apellido P")
                .apellidoM("Apellido M")
                .correo("Correo2@gmail.com")
                .telefono("1234567891")
                .numCuenta("123456789")
                .contrasena("Contrasena")
                .build();

        clienteService.save(cliente);

    }

}