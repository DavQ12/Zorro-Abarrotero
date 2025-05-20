package zorroAbarrotes.proyecto.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "proveedor")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProveedorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_proveedor")
    private Long id;

    @NotBlank
    @Column(name = "nombre")
    private String nombre;

    @NotBlank
    @Column(name = "direccion")
    private String direccion;

    @NotBlank
    @Column(name = "telefono")
    private String telefono;

    @NotBlank
    @Column(name = "correo")
    private String correo;

    @NotBlank
    @Column(name = "empresa")
    private String empresa;

}
