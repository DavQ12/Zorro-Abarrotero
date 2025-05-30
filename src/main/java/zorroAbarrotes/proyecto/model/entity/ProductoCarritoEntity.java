package zorroAbarrotes.proyecto.model.entity;

import jakarta.persistence.*;
import lombok.*;
import zorroAbarrotes.proyecto.model.id.ProductoCarritoId;

@Entity
@Table(name = "producto_carrito")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString(exclude = {"carrito", "producto"})
public class ProductoCarritoEntity {

    @EmbeddedId
    private ProductoCarritoId id;

    @Column(name = "cantidad")
    private Integer cantidad;

    @Column(name = "total")
    private Double total;

    @ManyToOne
    @MapsId("idCarrito")
    @JoinColumn(name = "id_carrito")
    private CarritoEntity carrito;

    @ManyToOne
    @MapsId("idProducto")
    @JoinColumn(name = "id_producto")
    private ProductoEntity producto;

}
