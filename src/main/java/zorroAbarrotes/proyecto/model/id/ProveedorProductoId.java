package zorroAbarrotes.proyecto.model.id;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProveedorProductoId implements Serializable {

    @Column(name = "id_proveedor")
    private Long idProveedor;

    @Column(name = "id_producto")
    private Long idProducto;
}
