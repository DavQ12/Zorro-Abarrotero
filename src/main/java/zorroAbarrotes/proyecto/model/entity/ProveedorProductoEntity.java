package zorroAbarrotes.proyecto.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import zorroAbarrotes.proyecto.model.id.ProveedorProductoId;

@Entity
@Table(name = "proveedor_producto")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProveedorProductoEntity {

    @EmbeddedId
    private ProveedorProductoId id;

    @Column(name = "costo_unitario")
    private Double costoUnitario;

    @Column(name = "tiempo_entrega")
    private Integer tiempoEntrega;

    @ManyToOne
    @MapsId("idProducto")
    @JoinColumn(name = "id_producto", nullable = false)
    private ProductoEntity producto;

    @ManyToOne
    @MapsId("idProveedor")
    @JoinColumn(name="id_proveedor", nullable = false)
    private ProveedorEntity proveedor;
}
