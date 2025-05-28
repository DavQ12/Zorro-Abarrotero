package zorroAbarrotes.proyecto.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import zorroAbarrotes.proyecto.model.entity.CarritoEntity;
import zorroAbarrotes.proyecto.model.entity.VentaEntity;
import zorroAbarrotes.proyecto.service.carrito.CarritoService;
import zorroAbarrotes.proyecto.service.venta.VentaService;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/carrito")
public class CarritoController {

    private final CarritoService carritoService;
    private final VentaService ventaService;

    @Autowired
    public CarritoController(CarritoService carritoService, VentaService ventaService) {
        this.carritoService = carritoService;
        this.ventaService = ventaService;
    }

    @GetMapping("/lista")
    public String listar(Model model) {
        model.addAttribute("carritos", carritoService.findAll());
        return "carrito/lista-carrito";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("carrito", new CarritoEntity());
        return "carrito/registro-carrito";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditarCarrito(@PathVariable Long id, Model model) {
        Optional<CarritoEntity> optionalCarrito = carritoService.findById(id);
        if (!optionalCarrito.isPresent()) {
            model.addAttribute("errorMessage", "Carrito no encontrado");
            return "redirect:/carrito/lista";
        }
        model.addAttribute("carrito", optionalCarrito.get());
        return "carrito/registro-carrito";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute CarritoEntity carrito) {
        carritoService.save(carrito);
        return "redirect:/carrito/lista";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarCarrito(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        CarritoEntity carrito = carritoService.findById(id).orElse(null);
        if (carrito != null) {
            // Check if carrito is associated with any ventas
            List<VentaEntity> ventasAsociadas = ventaService.findByCarrito(carrito);
            if (ventasAsociadas.isEmpty()) {
                carritoService.deleteById(id);
                redirectAttributes.addFlashAttribute("successMessage", "Carrito eliminado exitosamente");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "No se puede eliminar el carrito porque está asociado con una venta");
            }
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Carrito no encontrado");
        }
        return "redirect:/carrito/lista";
    }
}
