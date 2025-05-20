package zorroAbarrotes.proyecto.service.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import zorroAbarrotes.proyecto.model.entity.ClienteEntity;
import zorroAbarrotes.proyecto.model.entity.UsuarioEntity;
import zorroAbarrotes.proyecto.repository.ClienteRepository;
import zorroAbarrotes.proyecto.repository.UsuarioRepository;

import java.util.Collections;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private ClienteRepository clienteRepository;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        System.out.println("Intentando autenticar usuario: " + login);
        
        // Primero intentar buscar en la tabla de usuarios
        Optional<UsuarioEntity> usuarioOpt = usuarioRepository.findByUsuario(login);
        if (!usuarioOpt.isEmpty()) {
            // Usuario encontrado en la tabla de usuarios
            UsuarioEntity usuario = usuarioOpt.get();
            System.out.println("Usuario encontrado en tabla usuario: " + usuario.getUsuario());
            
            // Verificar si el rol es nulo
            if (usuario.getRol() == null) {
                System.out.println("ADVERTENCIA: El rol del usuario es nulo");
                throw new UsernameNotFoundException("El rol del usuario es nulo: " + login);
            }
            
            // Crear una autoridad basada en el rol del usuario
            String rolDescripcion = usuario.getRol().getDescripcion();
            SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + rolDescripcion);
            System.out.println("Autoridad creada: " + authority.getAuthority());
            
            return User.builder()
                    .username(usuario.getUsuario())
                    .password(usuario.getContrasena())
                    .authorities(Collections.singletonList(authority))
                    .build();
        }
        
        // Si no se encuentra en usuarios, buscar en la tabla de clientes
        System.out.println("Buscando cliente por nombre: " + login);
        Optional<ClienteEntity> clienteOpt = clienteRepository.findByNombre(login);
        
        // Mostrar todos los clientes para depuración
        System.out.println("Clientes disponibles:");
        clienteRepository.findAll().forEach(c -> {
            System.out.println("  - Cliente: " + c.getNombre() + ", Correo: " + c.getCorreo());
        });
        
        if (clienteOpt.isEmpty()) {
            System.out.println("No se encontró cliente por nombre, intentando por correo...");
            clienteOpt = clienteRepository.findByCorreo(login);
        }
        
        // Si todavía no se encuentra, intentar con la consulta combinada
        if (clienteOpt.isEmpty()) {
            System.out.println("No se encontró cliente por correo, intentando búsqueda combinada...");
            try {
                clienteOpt = clienteRepository.findByNombreOrCorreo(login);
            } catch (Exception e) {
                System.out.println("Error en la búsqueda combinada: " + e.getMessage());
            }
        }
        
        if (!clienteOpt.isEmpty()) {
            // Cliente encontrado
            ClienteEntity cliente = clienteOpt.get();
            System.out.println("Cliente encontrado: " + cliente.getNombre());
            System.out.println("Contraseña del cliente: " + cliente.getContrasena());
            
            // Para clientes, asignar el rol CLIENTE por defecto
            SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_CLIENTE");
            System.out.println("Autoridad asignada a cliente: " + authority.getAuthority());
            
            // Usar el login original como nombre de usuario para mantener consistencia
            // con lo que el usuario ingresó en el formulario
            String username = login;
            System.out.println("Usando nombre de usuario para autenticación: " + username);
            
            return User.builder()
                    .username(username) // Usar el login original
                    .password(cliente.getContrasena())
                    .authorities(Collections.singletonList(authority))
                    .build();
        }
        
        // Si no se encuentra ni en usuarios ni en clientes
        System.out.println("No se encontró el usuario/cliente: " + login);
        throw new UsernameNotFoundException("No se encontró el usuario o cliente: " + login);
    }
}
