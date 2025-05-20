package zorroAbarrotes.proyecto.service.venta;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import zorroAbarrotes.proyecto.model.entity.*;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class VentaServiceImplTest {

    @Autowired
    VentaService ventaService;

    @Test
    void saveVenta() {

        UsuarioEntity usuario = UsuarioEntity.builder()
                .id(2L)
                .build();

        ClienteEntity cliente = ClienteEntity.builder()
                .id(1L)
                .build();

        PagoEntity pago = PagoEntity.builder().id(1L).build();

        CarritoEntity carrito = CarritoEntity.builder().id(1L).build();

        VentaEntity venta = VentaEntity.builder()
                .usuario(usuario)
                .cliente(cliente)
                .pago(pago)
                .carrito(carrito)
                .fechaVenta(LocalDateTime.now())
                .build();

        System.out.println(venta);
        ventaService.save(venta);
    }

}