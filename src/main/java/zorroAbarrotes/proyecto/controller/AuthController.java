package zorroAbarrotes.proyecto.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import zorroAbarrotes.proyecto.model.entity.RolEntity;
import zorroAbarrotes.proyecto.model.entity.UsuarioEntity;
import zorroAbarrotes.proyecto.repository.UsuarioRepository;
import zorroAbarrotes.proyecto.service.rol.RolService;
import zorroAbarrotes.proyecto.service.usuario.UsuarioService;

import java.util.List;
import java.util.Optional;

@Controller
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private RolService rolService;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("contenido", "Bienvenido a Zorro Abarrotero");
        return "plantillas/index";
    }

    @GetMapping("/login")
    public String loginForm() {
        return "login/login";
    }

    @GetMapping("/registro")
    public String registroForm(Model model) {
        UsuarioEntity usuario = new UsuarioEntity();
        model.addAttribute("usuario", usuario);
        return "login/login";
    }

    @PostMapping("/registro")
    public String registro(@ModelAttribute("usuario") UsuarioEntity usuario, RedirectAttributes redirectAttributes) {
        try {
            // Verificar si el usuario ya existe
            Optional<UsuarioEntity> existingUser = usuarioRepository.findByUsuario(usuario.getUsuario());
            if (existingUser.isPresent()) {
                redirectAttributes.addFlashAttribute("errorMessage", "El nombre de usuario ya está en uso");
                return "redirect:/login";
            }
            
            // Buscar rol cliente (asumiendo que existe un rol con ID 2 para clientes)
            RolEntity rolCliente = rolService.findById(2L);
            if (rolCliente == null) {
                // Si no existe, obtener todos los roles
                List<RolEntity> roles = rolService.findAll();
                if (roles.isEmpty()) {
                    // Si no hay roles, crear uno por defecto
                    rolCliente = new RolEntity();
                    rolCliente.setDescripcion("CLIENTE");
                    rolCliente = rolService.save(rolCliente);
                } else {
                    // Usar el primer rol disponible
                    rolCliente = roles.get(0);
                }
            }
            
            usuario.setRol(rolCliente);
            
            // Encriptar contraseña
            usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));
            
            // Guardar usuario
            usuarioService.save(usuario);
            
            return "redirect:/login?registroExitoso";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al registrar el usuario: " + e.getMessage());
            return "redirect:/login";
        }
    }
}
