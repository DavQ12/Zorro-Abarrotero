package zorroAbarrotes.proyecto.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "proveedor")
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
    @Pattern(regexp = "^\\d{10}$", message = "Telefono debe tener 10 dígitos")
    @Column(name = "telefono")
    private String telefono;

    @NotBlank
    @Email(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",message = "Debe ser un email valido")
    @Column(name = "correo")
    private String correo;

    @NotBlank
    @Column(name = "empresa")
    private String empresa;

}
