package zorroAbarrotes.proyecto.service.proveedor;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import zorroAbarrotes.proyecto.model.entity.ProveedorEntity;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProveedorServiceImplTest {

    @Autowired
    ProveedorService proveedorService;

    @Test
    void save(){

        ProveedorEntity proveedor = ProveedorEntity.builder()
                .nombre("ProveedorTest2")
                .correo("CorreoTestProveedor@gmail.com")
                .telefono("1234567891")
                .empresa("EmpresaTest2")
                .direccion("DireccionTest2")
                .build();

        proveedorService.save(proveedor);
    }
}