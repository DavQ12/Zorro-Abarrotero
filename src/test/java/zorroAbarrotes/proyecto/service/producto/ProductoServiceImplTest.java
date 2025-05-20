package zorroAbarrotes.proyecto.service.producto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import zorroAbarrotes.proyecto.model.entity.CategoriaEntity;
import zorroAbarrotes.proyecto.model.entity.ProductoEntity;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProductoServiceImplTest {

    @Autowired
    ProductoService productoService;

    @Test
    void save(){
        CategoriaEntity categoria = CategoriaEntity.builder()
                .id(1L)
                .build();

        ProductoEntity producto = ProductoEntity.builder()
                .nombre("ArrozTest")
                .marca("ArrozerasTest")
                .imagen("ArrozTest.jpg")
                .precio(31.0)
                .stockMinimo(5)
                .stockActual(23)
                .categoria(categoria)
                .build();

        productoService.save(producto);
    }

}