package zorroAbarrotes.proyecto.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import zorroAbarrotes.proyecto.model.entity.CarritoEntity;
import zorroAbarrotes.proyecto.model.entity.ProductoCarritoEntity;
import zorroAbarrotes.proyecto.model.entity.VentaEntity;
import zorroAbarrotes.proyecto.service.carrito.CarritoService;
import zorroAbarrotes.proyecto.service.producto_carrito.ProductoCarritoService;
import zorroAbarrotes.proyecto.service.venta.VentaService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/carrito")
public class CarritoController {

    private final CarritoService carritoService;
    private final VentaService ventaService;
    private final ProductoCarritoService productoCarritoService;

    private final String RUTA_IMAGENES = "/imagenes/";

    @Autowired
    public CarritoController(CarritoService carritoService, VentaService ventaService, ProductoCarritoService productoCarritoService) {
        this.carritoService = carritoService;
        this.ventaService = ventaService;
        this.productoCarritoService = productoCarritoService;
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

    @GetMapping("/detalles/{id}")
        public ResponseEntity<Map<String, Object>> getCarritoDetalle(@PathVariable Long id) {
            try {
                Optional<CarritoEntity> carrito = carritoService.findById(id);
                System.out.println("Empieza: ----------");
                System.out.println(carrito);
                if (carrito.isEmpty() || !carrito.isPresent()) {
                    return ResponseEntity.notFound().build();
                }else{
                    CarritoEntity carritoDatos = carrito.get();
                    System.out.println("--------------------");
                    System.out.println(carritoDatos);
                    //obtener productos del carrito

                    List<ProductoCarritoEntity> productosCarrito =
                            productoCarritoService.findAll().stream()
                                    .filter(pc -> pc.getCarrito().getId().equals(id))
                                    .collect(Collectors.toList());

                    System.out.println("--------------------");
                    System.out.println(productosCarrito);

                    List<Map<String,Object>> productos = productosCarrito.stream()
                            .map(pc ->{
                                Map<String, Object> producto = new HashMap<>();
                                producto.put("nombre", pc.getProducto().getNombre());
                                producto.put("cantidad", pc.getCantidad());
                                producto.put("precioUnitario", pc.getProducto().getPrecio());
                                producto.put("total", pc.getTotal());
                                producto.put("imagen", (RUTA_IMAGENES+pc.getProducto().getImagen()));
                                return producto;
                            })
                            .collect(Collectors.toList());

                    System.out.println("--------------------");
                    System.out.println(productos);

                    return ResponseEntity.ok(Map.of(
                            "idCarrito", carritoDatos.getId(),
                            "fecha", carritoDatos.getFecha(),
                            "total", carritoDatos.getTotal(),
                            "productos", productos
                    ));

                }
            }catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("error", "Error al obtener detalles del carrito"));
            }
        }


}
