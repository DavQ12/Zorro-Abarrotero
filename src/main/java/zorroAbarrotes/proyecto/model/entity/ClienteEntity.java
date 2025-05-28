package zorroAbarrotes.proyecto.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Entity(name = "ClienteEntity")
@Table(name = "cliente")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClienteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente")
    private Long id;

    @NotBlank(message = "Ingresa un nombre")
    @Pattern(
            regexp = "^[\\p{L} ]+$",
            message = "El apellido solo debe contener letras y espacios"
    )
    @Size(min = 4, max = 50, message = "El nombre debe tener entre 4 y 50 caracteres")
    @Column(name = "nombre")
    private String nombre;

    @NotBlank(message = "Ingresa un apellido paterno")
    @Pattern(
            regexp = "^[\\p{L} ]+$",
            message = "El apellido solo debe contener letras y espacios"
    )
    @Size(min = 4, max = 50, message = "El apellido debe tener entre 4 y 50 caracteres")
    @Column(name = "apellidoP")
    private String apellidoP;

    @Column(name = "apellidoM")
    private String apellidoM;

    @NotBlank
    @Email(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",message = "Debe ser un email valido")
    @Column(name = "correo", unique = true)
    private String correo;

    @NotBlank
    @Pattern(regexp = "^\\d{10}$", message = "Telefono debe tener 10 dígitos")
    @Column(name = "telefono", unique = true)
    private String telefono;


    @Column(name = "num_cuenta", unique = true)
    private String numCuenta;

//    @NotBlank
//    @Length(min = 6, message = "Debe tener 6 caracteres minimo")
    @Column(name = "contrasena")
    private String contrasena;

    @Transient
    private String identificador;

}