package zorroAbarrotes.proyecto.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import zorroAbarrotes.proyecto.model.id.ProductoCarritoId;

@Entity(name = "producto_carrito")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductoCarritoEntity {

    @EmbeddedId
    private ProductoCarritoId id;

    @Column(name = "cantidad")
    private Integer cantidad;

    @Column(name = "total")
    private Double total;

}
