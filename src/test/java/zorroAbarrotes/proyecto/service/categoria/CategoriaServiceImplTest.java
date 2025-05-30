package zorroAbarrotes.proyecto.service.categoria;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import zorroAbarrotes.proyecto.model.entity.CategoriaEntity;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CategoriaServiceImplTest {

    @Autowired
    CategoriaService categoriaService;

    @Test
    void salvarCategoria() {

        CategoriaEntity categoria = CategoriaEntity.builder()
                .descripcion("Abarrotes")
                .build();

        categoriaService.save(categoria);
    }
}