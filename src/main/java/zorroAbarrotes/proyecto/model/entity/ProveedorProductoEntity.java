package zorroAbarrotes.proyecto.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import zorroAbarrotes.proyecto.model.id.ProveedorProductoId;

@Entity(name = "proveedor_producto")
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
}
