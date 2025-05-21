package zorroAbarrotes.proyecto.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import zorroAbarrotes.proyecto.model.entity.ClienteEntity;
import zorroAbarrotes.proyecto.service.cliente.ClienteService;

@Controller
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @GetMapping("/clientes/registro")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("cliente", new ClienteEntity());
        return "clientes/registro-cliente";
    }

    @PostMapping("/clientes/registro")
    public String guardarClienteRegistro(ClienteEntity cliente, RedirectAttributes flash) {
        try {
            clienteService.save(cliente);
            flash.addFlashAttribute("success", "Cliente creado con éxito");
            return "redirect:/clientes/lista";
        } catch (Exception e) {
            flash.addFlashAttribute("errorMessage", "Error al crear el cliente: " + e.getMessage());
            return "redirect:/clientes/registro";
        }
    }

    @GetMapping("/clientes/lista")
    public String listarClientes(Model model) {
        model.addAttribute("clientes", clienteService.findAll());
        return "clientes/lista-clientes";
    }

    @GetMapping("/clientes/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("cliente", new ClienteEntity());
        model.addAttribute("editar", false);
        return "clientes/registro-cliente";
    }

    @PostMapping("/clientes/guardar")
    public String guardarCliente(ClienteEntity cliente, RedirectAttributes flash) {
        try {
            // Validar campos obligatorios
            if (cliente.getNombre() == null || cliente.getNombre().isEmpty() ||
                cliente.getApellidoP() == null || cliente.getApellidoP().isEmpty() ||
                cliente.getCorreo() == null || cliente.getCorreo().isEmpty()) {
                flash.addFlashAttribute("errorMessage", "Todos los campos obligatorios deben estar completos");
                return "redirect:/clientes/nuevo";
            }

            // Si es un cliente existente, buscarlo
            ClienteEntity clienteExistente = clienteService.findById(cliente.getId());
            if (clienteExistente != null) {
                // Validar que el correo no esté registrado por otro cliente
                if (!cliente.getCorreo().equals(clienteExistente.getCorreo()) && 
                    clienteService.findByCorreo(cliente.getCorreo()).isPresent()) {
                    flash.addFlashAttribute("errorMessage", "El correo electrónico ya está registrado en el sistema");
                    return "redirect:/clientes/editar/" + cliente.getId();
                }

                // Validar longitud de la contraseña si se proporcionó una nueva
                if (cliente.getContrasena() != null && !cliente.getContrasena().isEmpty() && cliente.getContrasena().length() < 6) {
                    flash.addFlashAttribute("errorMessage", "La contraseña debe tener al menos 6 caracteres");
                    return "redirect:/clientes/editar/" + cliente.getId();
                }

                // Actualizar los campos
                clienteExistente.setNombre(cliente.getNombre());
                clienteExistente.setApellidoP(cliente.getApellidoP());
                clienteExistente.setApellidoM(cliente.getApellidoM());
                clienteExistente.setCorreo(cliente.getCorreo());
                clienteExistente.setTelefono(cliente.getTelefono());
                clienteExistente.setNumCuenta(cliente.getNumCuenta());
                
                // Actualizar la contraseña solo si se proporcionó una nueva
                if (cliente.getContrasena() != null && !cliente.getContrasena().isEmpty()) {
                    clienteExistente.setContrasena(cliente.getContrasena());
                }

                clienteService.save(clienteExistente);
                flash.addFlashAttribute("success", "Cliente actualizado con éxito");
            } else {
                // Es un nuevo cliente
                clienteService.save(cliente);
                flash.addFlashAttribute("success", "Cliente creado con éxito");
            }
            return "redirect:/clientes/lista";
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            flash.addFlashAttribute("errorMessage", "Error al guardar el cliente: " + e.getMessage());
            return "redirect:/clientes/nuevo";
        }
    }

    @GetMapping("/clientes/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable("id") Long id, Model model) {
        ClienteEntity cliente = clienteService.findById(id);
        if (cliente != null) {
            model.addAttribute("cliente", cliente);
            model.addAttribute("editar", true);
            return "clientes/registro-cliente";
        }
        return "redirect:/clientes/lista";
    }

    @GetMapping("/clientes/eliminar/{id}")
    public String eliminarCliente(@PathVariable("id") Long id, RedirectAttributes flash) {
        try {
            clienteService.deleteById(id);
            flash.addFlashAttribute("success", "Cliente eliminado con éxito");
            return "redirect:/clientes/lista";
        } catch (Exception e) {
            flash.addFlashAttribute("errorMessage", "Error al eliminar el cliente: " + e.getMessage());
            return "redirect:/clientes/lista";
        }
    }
}
