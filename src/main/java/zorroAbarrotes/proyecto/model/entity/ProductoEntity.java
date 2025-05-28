package zorroAbarrotes.proyecto.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "producto")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Long id;

    @NotBlank(message = "Escribe un nombre del producto")
    @Column(name = "nombre")
    private String nombre;

    @NotBlank(message = "Escribe una marca del producto")
    @Column(name = "marca")
    private String marca;

    @NotNull(message = "El precio no puede ser vacio")
    @Positive(message = "Precio debe ser mayor a cero")
    @Column(name = "precio")
    private Double precio;

    @Positive(message = "Stock minimo debe ser mayor a cero")
    @NotNull(message = "El stock minimo no puede ser vacio")
    @Min(value = 5,message = "Stock minimo debe ser mayor a 4")
    @Column(name = "stock_minimo")
    private Integer stockMinimo;

    @Positive(message = "Stock actual debe ser mayor a cero")
    @NotNull(message = "El stock actual no puede ser vacio")
    @Column(name = "stock_actual")
    @Min(value = 6, message = "Stock actual minimo 6")
    private Integer stockActual;


    @Column(name = "imagen")
    private String imagen;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "id_categoria", nullable = false)
    @NotNull(message = "Debes seleccionar una categoria")
    private CategoriaEntity categoria;

}
