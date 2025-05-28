package zorroAbarrotes.proyecto.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "pedido")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PedidoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pedido")
    private Long id;

    @Column(name = "fecha_pedido")
    private LocalDateTime fechaPedido;

    @Column(name = "cantidad_producto")
    private Integer cantidadProducto;

    @Column(name = "total")
    private Double total;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "id_proveedor")
    private ProveedorEntity proveedor;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "id_usuario")
    private UsuarioEntity usuario;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "id_producto")
    private ProductoEntity producto;

}
