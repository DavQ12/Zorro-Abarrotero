package zorroAbarrotes.proyecto.service.producto_carrito;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import zorroAbarrotes.proyecto.model.entity.CarritoEntity;
import zorroAbarrotes.proyecto.model.entity.ProductoCarritoEntity;
import zorroAbarrotes.proyecto.model.entity.ProductoEntity;
import zorroAbarrotes.proyecto.model.entity.ProveedorEntity;
import zorroAbarrotes.proyecto.model.id.ProductoCarritoId;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProductoCarritoServiceImplTest {

    @Autowired
    ProductoCarritoService productoCarritoService;

    @Test
    void save() {

        ProductoEntity producto = ProductoEntity.builder()
                .id(2L)
                .build();

        CarritoEntity carrito = CarritoEntity.builder()
                .id(1L)
                .build();

        ProductoCarritoId id = ProductoCarritoId.builder()
                .idCarrito(carrito.getId())
                .idProducto(producto.getId())
                .build();

        ProductoCarritoEntity productoCarrito = ProductoCarritoEntity.builder()
                .id(id)
                .cantidad(3)
                .total(93.0)
                .build();

        productoCarritoService.save(productoCarrito);

    }


}