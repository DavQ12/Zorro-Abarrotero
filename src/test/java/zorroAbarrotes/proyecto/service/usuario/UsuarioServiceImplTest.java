package zorroAbarrotes.proyecto.service.usuario;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import zorroAbarrotes.proyecto.model.entity.RolEntity;
import zorroAbarrotes.proyecto.model.entity.UsuarioEntity;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UsuarioServiceImplTest {

    @Autowired
    UsuarioServiceImpl usuarioService;

    @Test
    void save() {
        RolEntity rol = RolEntity.builder()
                .id(2L)
                .build();

        UsuarioEntity usuario = UsuarioEntity.builder()
                .username("cajera1")
                .contrasena("cajera1")
                .correo("correo@cajera1.com")
                .rol(rol)
                .build();

        usuarioService.save(usuario);
    }

}