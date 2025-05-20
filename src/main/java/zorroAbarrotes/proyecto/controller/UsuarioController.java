package zorroAbarrotes.proyecto.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import zorroAbarrotes.proyecto.model.entity.ClienteEntity;
import zorroAbarrotes.proyecto.model.entity.RolEntity;
import zorroAbarrotes.proyecto.model.entity.UsuarioEntity;
import zorroAbarrotes.proyecto.repository.RolRepository;
import zorroAbarrotes.proyecto.service.cliente.ClienteService;
import zorroAbarrotes.proyecto.service.usuario.UsuarioService;

import java.util.List;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    @Qualifier("clienteService")
    private ClienteService clienteService;

    @GetMapping("/")
    public String indexUsuarios() {
        System.out.println("DEBUG: Accediendo a indexUsuarios()");
        return "plantillas/index";
    }
    
    @GetMapping("/lista")
    public String listarUsuarios(Model model) {
        System.out.println("DEBUG: Accediendo a listarUsuarios()");
        List<UsuarioEntity> usuarios = usuarioService.findAll();
        model.addAttribute("lista", usuarios);
        model.addAttribute("contenido", "Listado de Usuarios");
        return "usuarios/lista-usuarios";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevoUsuario(Model model) {
        UsuarioEntity usuario = new UsuarioEntity();
        List<RolEntity> roles = rolRepository.findAll();
        
        model.addAttribute("usuario", usuario);
        model.addAttribute("roles", roles);
        model.addAttribute("contenido", "Nuevo Usuario");
        return "usuarios/registro-usuario";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditarUsuario(@PathVariable Long id, Model model, RedirectAttributes flash) {
        System.out.println("DEBUG: Editando usuario con ID: " + id);
        UsuarioEntity usuario = usuarioService.findById(id);
        
        if (usuario == null) {
            flash.addFlashAttribute("error", "El usuario no existe en la base de datos");
            return "redirect:/usuarios/lista";
        }
        
        List<RolEntity> roles = rolRepository.findAll();
        
        model.addAttribute("usuario", usuario);
        model.addAttribute("roles", roles);
        model.addAttribute("contenido", "Editar Usuario");
        return "usuarios/registro-usuario";
    }

    @PostMapping("/guardar")
    public String guardarUsuario(
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam("usuario") String nombreUsuario,
            @RequestParam("correo") String correo,
            @RequestParam("contrasena") String contrasena,
            @RequestParam(value = "rolId", required = false) Long rolId,
            @RequestParam(value = "nombre", required = false) String nombre,
            @RequestParam(value = "apellidop", required = false) String apellidoPaterno,
            @RequestParam(value = "apellidom", required = false) String apellidoMaterno,
            @RequestParam(value = "telefono", required = false) String telefono,
            @RequestParam(value = "num_cuenta", required = false) String numCuenta,
            Model model, RedirectAttributes flash) {
        
        System.out.println("DEBUG: Guardando usuario con parámetros directos: " + nombreUsuario + ", correo: " + correo + ", rolId: " + rolId);
        
        try {
            // Verificar si es un cliente o un usuario normal
            if (rolId != null && rolId == 3) { // 3 es el ID del rol cliente
                // Es un cliente, guardar en la tabla cliente
                ClienteEntity cliente = new ClienteEntity();
                cliente.setNombre(nombre);
                cliente.setApellidoP(apellidoPaterno);
                cliente.setApellidoM(apellidoMaterno);
                cliente.setCorreo(correo);
                cliente.setTelefono(telefono);
                cliente.setNumCuenta(numCuenta);
                cliente.setContrasena(contrasena);
                
                // Guardar el cliente
                System.out.println("DEBUG: Guardando cliente: " + cliente.getNombre() + " con correo: " + cliente.getCorreo());
                System.out.println("DEBUG: ClienteService: " + clienteService);
                clienteService.save(cliente);
                
                // Mensaje de éxito
                flash.addFlashAttribute("success", "Cliente creado con éxito");
                return "redirect:/usuarios/lista";
            } else {
                // Es un usuario normal, guardar en la tabla usuario
                UsuarioEntity usuario;
                if (id != null) {
                    // Estamos editando un usuario existente
                    usuario = usuarioService.findById(id);
                    if (usuario == null) {
                        flash.addFlashAttribute("error", "El usuario no existe en la base de datos");
                        return "redirect:/usuarios/lista";
                    }
                } else {
                    // Estamos creando un nuevo usuario
                    usuario = new UsuarioEntity();
                }
                
                // Asignar los valores del formulario
                usuario.setUsuario(nombreUsuario);
                usuario.setCorreo(correo);
                usuario.setContrasena(contrasena);
                
                // Asignar el rol si se proporcionó un ID de rol válido
                if (rolId != null) {
                    RolEntity rol = rolRepository.findById(rolId).orElse(null);
                    if (rol != null) {
                        usuario.setRol(rol);
                        System.out.println("DEBUG: Rol asignado: " + rol.getDescripcion());
                    } else {
                        flash.addFlashAttribute("error", "El rol seleccionado no existe");
                        return "redirect:/usuarios/nuevo";
                    }
                } else {
                    flash.addFlashAttribute("error", "Debe seleccionar un rol");
                    return "redirect:/usuarios/nuevo";
                }
                
                // Guardar el usuario
                usuarioService.save(usuario);
                
                // Mensaje de éxito
                String mensaje = id == null ? "Usuario creado con éxito" : "Usuario actualizado con éxito";
                flash.addFlashAttribute("success", mensaje);
                return "redirect:/usuarios/lista";
            }
            
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();
            flash.addFlashAttribute("error", "Error al guardar el usuario: " + e.getMessage());
            
            // En caso de error, volver al formulario
            return id == null ? "redirect:/usuarios/nuevo" : "redirect:/usuarios/editar/" + id;
        }
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarUsuario(@PathVariable Long id, RedirectAttributes flash) {
        try {
            usuarioService.deleteById(id);
            flash.addFlashAttribute("success", "Usuario eliminado con éxito");
        } catch (Exception e) {
            flash.addFlashAttribute("error", "Error al eliminar el usuario: " + e.getMessage());
        }
        
        return "redirect:/usuarios/lista";
    }
}
