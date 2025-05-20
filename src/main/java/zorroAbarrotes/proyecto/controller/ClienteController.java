package zorroAbarrotes.proyecto.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import zorroAbarrotes.proyecto.model.entity.ClienteEntity;
import zorroAbarrotes.proyecto.service.cliente.ClienteService;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @GetMapping("/registro")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("cliente", new ClienteEntity());
        return "clientes/registro-cliente";
    }

    @PostMapping("/registro")
    public String guardarClienteRegistro(ClienteEntity cliente, RedirectAttributes flash) {
        try {
            clienteService.save(cliente);
            flash.addFlashAttribute("success", "Cliente creado con éxito");
            return "redirect:/login?registroExitoso";
        } catch (Exception e) {
            flash.addFlashAttribute("errorMessage", "Error al crear el cliente: " + e.getMessage());
            return "redirect:/clientes/registro";
        }
    }

    @GetMapping("/lista")
    public String listarClientes(Model model) {
        model.addAttribute("clientes", clienteService.findAll());
        return "clientes/lista-clientes";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("cliente", new ClienteEntity());
        return "clientes/registro-cliente";
    }

    @PostMapping("/nuevo")
    public String guardarCliente(ClienteEntity cliente, RedirectAttributes flash) {
        try {
            clienteService.save(cliente);
            flash.addFlashAttribute("success", "Cliente creado con éxito");
            return "redirect:/clientes/lista";
        } catch (Exception e) {
            flash.addFlashAttribute("errorMessage", "Error al crear el cliente: " + e.getMessage());
            return "redirect:/clientes/nuevo";
        }
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        ClienteEntity cliente = clienteService.findById(id);
        if (cliente == null) {
            return "redirect:/clientes/lista";
        }
        model.addAttribute("cliente", cliente);
        return "clientes/registro-cliente";
    }

    @PostMapping("/editar/{id}")
    public String actualizarCliente(@PathVariable Long id, ClienteEntity cliente, RedirectAttributes flash) {
        try {
            // Aseguramos que el ID del cliente coincida
            cliente.setId(id);
            clienteService.save(cliente);
            flash.addFlashAttribute("success", "Cliente actualizado con éxito");
            return "redirect:/clientes/lista";
        } catch (Exception e) {
            flash.addFlashAttribute("errorMessage", "Error al actualizar el cliente: " + e.getMessage());
            return "redirect:/clientes/editar/" + id;
        }
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarCliente(@PathVariable Long id, RedirectAttributes flash) {
        // Verificar si el cliente existe antes de intentar eliminar
        ClienteEntity cliente = clienteService.findById(id);
        if (cliente == null) {
            flash.addFlashAttribute("errorMessage", "El cliente no existe");
            return "redirect:/clientes/lista";
        }

        try {
            clienteService.deleteById(id);
            flash.addFlashAttribute("success", "Cliente eliminado con éxito");
            return "redirect:/clientes/lista";
        } catch (Exception e) {
            flash.addFlashAttribute("errorMessage", "No se puede eliminar el cliente porque está siendo usado en otras transacciones");
            return "redirect:/clientes/lista";
        }
    }
}
