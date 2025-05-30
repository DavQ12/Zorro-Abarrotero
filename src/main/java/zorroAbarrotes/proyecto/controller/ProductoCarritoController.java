package zorroAbarrotes.proyecto.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import zorroAbarrotes.proyecto.model.entity.CarritoEntity;
import zorroAbarrotes.proyecto.model.entity.ProductoCarritoEntity;
import zorroAbarrotes.proyecto.model.entity.ProductoEntity;
import zorroAbarrotes.proyecto.model.id.ProductoCarritoId;
import zorroAbarrotes.proyecto.service.carrito.CarritoService;
import zorroAbarrotes.proyecto.service.producto.ProductoService;
import zorroAbarrotes.proyecto.service.producto_carrito.ProductoCarritoService;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/producto-carrito")
public class ProductoCarritoController {

    private final ProductoCarritoService productoCarritoService;
    private final ProductoService productoService;
    private final CarritoService carritoService;

    @Autowired
    public ProductoCarritoController(ProductoCarritoService productoCarritoService,
                                     ProductoService productoService,
                                     CarritoService carritoService) {
        this.productoCarritoService = productoCarritoService;
        this.productoService = productoService;
        this.carritoService = carritoService;
    }

    @GetMapping("/lista")
    public String listar(Model model) {
        try {
            List<ProductoCarritoEntity> productoCarritos = productoCarritoService.findAll();
            model.addAttribute("productoCarritos", productoCarritos);
            System.out.println("Encontrados " + productoCarritos.size() + " productos en el carrito");
            return "producto-carrito/lista-productoCarrito";
        } catch (Exception e) {
            System.out.println("Error en listar productos del carrito: " + e.getMessage());
            model.addAttribute("error", "Error al cargar los productos del carrito");
            return "error";
        }
    }

    @GetMapping("/lista-productoCarrito")
    public String listarCompleto(Model model) {
        return listar(model);
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        try {
            model.addAttribute("productoCarrito", new ProductoCarritoEntity());
            model.addAttribute("carritos", carritoService.findAll());
            model.addAttribute("productos", productoService.findAll());
            return "producto-carrito/registro-productoCarrito";
        } catch (Exception e) {
            System.out.println("[ERROR] Error en nuevo: " + e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            return "redirect:/producto-carrito/lista";
        }
    }

    @GetMapping("/editar/{idCarrito}/{idProducto}")
    public String mostrarFormularioEditarProductoCarrito(
            @PathVariable("idCarrito") Long idCarrito,
            @PathVariable("idProducto") Long idProducto,
            Model model) {
        try {
            ProductoCarritoId id = new ProductoCarritoId(idCarrito, idProducto);
            Optional<ProductoCarritoEntity> optionalProductoCarrito = productoCarritoService.findById(id);
            
            if (!optionalProductoCarrito.isPresent()) {
                model.addAttribute("errorMessage", "Producto no encontrado");
                return "redirect:/producto-carrito/lista";
            }
            
            model.addAttribute("productoCarrito", optionalProductoCarrito.get());
            model.addAttribute("carritos", carritoService.findAll());
            model.addAttribute("productos", productoService.findAll());
            
            return "producto-carrito/registro-productoCarrito";
        } catch (Exception e) {
            System.out.println("Error al cargar producto para editar: " + e.getMessage());
            model.addAttribute("errorMessage", "Error al cargar el producto");
            return "redirect:/producto-carrito/lista";
        }
    }

    @PostMapping("/guardar")
    public String guardar(@RequestParam(value = "idCarrito", required = false) Long idCarrito,
                          @RequestParam(value = "idProducto", required = false) Long idProducto,
                          @RequestParam(value = "cantidad", required = false) Integer cantidad,
                          @RequestParam(value = "isEditing", required = false) String isEditing,
                          @RequestParam(value = "originalIdCarrito", required = false) Long originalIdCarrito,
                          @RequestParam(value = "originalIdProducto", required = false) Long originalIdProducto,
                          Model model,
                          RedirectAttributes redirectAttributes) {
        try {
            // Si estamos editando, usamos los valores originales
            if ("true".equals(isEditing)) {
                idCarrito = originalIdCarrito;
                idProducto = originalIdProducto;
            }
            
            // Solo validamos cantidad en edición
            if (cantidad == null) {
                throw new RuntimeException("Por favor ingrese una cantidad válida");
            }
            
            // Si estamos editando, primero eliminamos el registro anterior si las claves cambiaron
            if ("true".equals(isEditing) && originalIdCarrito != null && originalIdProducto != null) {
                // Si las claves primarias cambiaron, eliminamos el registro original
                if (!originalIdCarrito.equals(idCarrito) || !originalIdProducto.equals(idProducto)) {
                    ProductoCarritoId originalId = new ProductoCarritoId(originalIdCarrito, originalIdProducto);
                    productoCarritoService.deleteById(originalId);
                }
            }
            
            // Verificar si ya existe este producto en el carrito (para nuevos registros o claves cambiadas)
            ProductoCarritoId newId = new ProductoCarritoId(idCarrito, idProducto);
            ProductoCarritoEntity existingEntity = productoCarritoService.findById(newId).orElse(null);
            
            ProductoCarritoEntity productoCarrito;
            if (existingEntity != null) {
                // Si existe, actualizamos
                productoCarrito = existingEntity;
            } else {
                // Si no existe, creamos uno nuevo
                productoCarrito = new ProductoCarritoEntity();
                productoCarrito.setId(newId);
            }
            
            // Obtener las entidades relacionadas
            Optional<CarritoEntity> carritoOptional = carritoService.findById(idCarrito);
            ProductoEntity producto = productoService.findById(idProducto);
            
            CarritoEntity carrito = carritoOptional.orElseThrow(() -> new RuntimeException("Carrito no encontrado"));
            if (producto == null) {
                throw new RuntimeException("Producto no encontrado");
            }
            
            // Configurar la entidad
            productoCarrito.setCarrito(carrito);
            productoCarrito.setProducto(producto);
            productoCarrito.setCantidad(cantidad);
            productoCarrito.setTotal(producto.getPrecio() * cantidad);
            
            // Guardar
            productoCarritoService.save(productoCarrito);
            
            redirectAttributes.addFlashAttribute("successMessage", "Producto guardado exitosamente");
            return "redirect:/producto-carrito/lista";
            
        } catch (Exception e) {
            System.out.println("Error al guardar producto en carrito: " + e.getMessage());
            e.printStackTrace();
            
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("productoCarrito", new ProductoCarritoEntity());
            model.addAttribute("carritos", carritoService.findAll());
            model.addAttribute("productos", productoService.findAll());
            return "producto-carrito/registro-productoCarrito";
        }
    }

    @PostMapping("/eliminar/{idCarrito}-{idProducto}")
    public String eliminar(@PathVariable("idCarrito") Long idCarrito,
                           @PathVariable("idProducto") Long idProducto,
                           RedirectAttributes redirectAttributes) {
        try {
            ProductoCarritoId id = new ProductoCarritoId();
            id.setIdCarrito(idCarrito);
            id.setIdProducto(idProducto);
            productoCarritoService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Producto eliminado exitosamente");
            return "redirect:/producto-carrito/lista";
        } catch (Exception e) {
            System.out.println("Error al eliminar producto del carrito: " + e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Error al eliminar: " + e.getMessage());
            return "redirect:/producto-carrito/lista";
        }
    }
}