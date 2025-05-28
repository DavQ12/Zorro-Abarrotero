package zorroAbarrotes.proyecto.service.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import zorroAbarrotes.proyecto.model.entity.UsuarioEntity;
import zorroAbarrotes.proyecto.repository.UsuarioRepository;

@Service
public class UserDetailsServiceImp implements UserDetailsService {
    @Autowired
    UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UsuarioEntity usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(()-> new UsernameNotFoundException("El usuario"+username +" no existe. Intenta por correo"));

        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_".concat(usuario.getRol().getDescripcion()));

        return User.builder()
                .username(usuario.getUsername())
                .password(usuario.getContrasena())
                .authorities(authority)
                .build();
    }
}
