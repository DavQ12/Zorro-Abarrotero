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
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.validator.constraints.Length;

@Entity
@Table(name = "usuario")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UsuarioEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long id;

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Pattern(
            regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ]+[0-9]+$",
            message = "Usuario debe comenzar con letras y terminar con números (sin espacios)"
    )
    @Size(min = 5, max = 20, message = "Usuario debe tener entre 5 y 20 caracteres")
    @Column(name = "usuario", unique = true, nullable = false)
    private String username;

//    @NotBlank
//    @Length(min = 6, message = "Debe tener 6 caracteres minimo")
    @Column(name = "contrasena")
    private String contrasena;

    @NotBlank
    @Email(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",message = "Debe ser un email valido")
    @Column(name = "correo", unique = true, nullable = false)
    private String correo;


    @ManyToOne
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "id_rol", nullable = false)
    private RolEntity rol;
}
