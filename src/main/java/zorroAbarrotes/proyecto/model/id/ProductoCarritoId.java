package zorroAbarrotes.proyecto.model.id;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductoCarritoId {

    @Column(name = "id_carrito")
    private Long idCarrito;

    @Column(name = "id_producto")
    private Long idProducto;
}
