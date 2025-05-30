package zorroAbarrotes.proyecto.service.carrito;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import zorroAbarrotes.proyecto.model.entity.CarritoEntity;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CarritoServiceTest {

    @Autowired
    CarritoService carritoService;

    @Test
    void saveCarrito() {

        CarritoEntity carrito = CarritoEntity.builder()
                .fecha(LocalDateTime.now())
                .total(145.80)
                .build();

        carritoService.save(carrito);
    }

}