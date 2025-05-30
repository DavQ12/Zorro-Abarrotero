package zorroAbarrotes.proyecto.service.rol;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import zorroAbarrotes.proyecto.model.entity.RolEntity;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RolServiceImplTest {

    @Autowired
    RolService rolService;

    @Test
    void salvarRol() {
        RolEntity rol = RolEntity.builder()
                .descripcion("cajera")
                .build();

        rolService.save(rol);
    }

    @Test
    void buscar(){
        System.out.println(rolService.findAll());
    }

}