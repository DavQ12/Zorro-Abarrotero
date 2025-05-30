package zorroAbarrotes.proyecto.service.proveedor_producto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import zorroAbarrotes.proyecto.model.entity.ProductoCarritoEntity;
import zorroAbarrotes.proyecto.model.entity.ProductoEntity;
import zorroAbarrotes.proyecto.model.entity.ProveedorEntity;
import zorroAbarrotes.proyecto.model.entity.ProveedorProductoEntity;
import zorroAbarrotes.proyecto.model.id.ProveedorProductoId;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProveedorProductoServiceImplTest {

    @Autowired
    ProveedorProductoService proveedorProductoService;

    @Test
    void save(){

        ProveedorEntity proveedor = ProveedorEntity.builder().id(1L).build();
        ProductoEntity producto = ProductoEntity.builder().id(1L).build();

        ProveedorProductoId id = ProveedorProductoId.builder()
                .idProducto(producto.getId())
                .idProveedor(proveedor.getId())
                .build();

        ProveedorProductoEntity productoCarrito = ProveedorProductoEntity.builder()
                .id(id)
                .costoUnitario(18.50)
                .tiempoEntrega(3)
                .build();

        proveedorProductoService.save(productoCarrito);
    }

}