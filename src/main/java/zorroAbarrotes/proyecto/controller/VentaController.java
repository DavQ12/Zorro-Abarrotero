package zorroAbarrotes.proyecto.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import zorroAbarrotes.proyecto.model.entity.*;
import zorroAbarrotes.proyecto.service.carrito.CarritoService;
import zorroAbarrotes.proyecto.service.cliente.ClienteService;
import zorroAbarrotes.proyecto.service.pago.PagoService;
import zorroAbarrotes.proyecto.service.usuario.UsuarioService;
import zorroAbarrotes.proyecto.service.venta.VentaService;

import java.time.LocalDateTime;
import java.util.Optional;


@Controller
@RequestMapping("/ventas")
public class VentaController {

    private final VentaService ventaService;
    private final ClienteService clienteService;
    private final PagoService pagoService;
    private final UsuarioService usuarioService;
    private final CarritoService carritoService;

    @Autowired
    public VentaController(VentaService ventaService,
                           ClienteService clienteService,
                           PagoService pagoService,
                           UsuarioService usuarioService,
                           CarritoService carritoService) {
        this.ventaService = ventaService;
        this.clienteService = clienteService;
        this.pagoService = pagoService;
        this.usuarioService = usuarioService;
        this.carritoService = carritoService;
    }

    @GetMapping({"/lista"})
    public String listar(Model model, 
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "fechaVenta"));
            Page<VentaEntity> ventasPage = ventaService.findAllWithDetails(pageable);
            System.out.println("hola papus: "+ventasPage.getContent());
            // Debug: Log the results
            System.out.println("Total elements: " + ventasPage.getTotalElements());
            System.out.println("Total pages: " + ventasPage.getTotalPages());
            System.out.println("Current page: " + page);
            System.out.println("Content size: " + ventasPage.getContent().size());
            
            model.addAttribute("ventas", ventasPage.getContent());
            model.addAttribute("currentPage", Optional.of(page));
            model.addAttribute("totalPages", Optional.of(ventasPage.getTotalPages()));
            model.addAttribute("totalElements", Optional.of(ventasPage.getTotalElements()));
            model.addAttribute("totalVentas", ventaService.countAll());
            return "ventas/lista-ventas";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar las ventas: " + e.getMessage());
            return "error"; // Página de error
        }
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        try {
            VentaEntity venta = new VentaEntity();
            venta.setFechaVenta(LocalDateTime.now()); // Establecer fecha actual por defecto
            
            // Obtener el primer usuario (temporalmente hasta que implementemos autenticación)
            UsuarioEntity usuario = usuarioService.findAll().get(0);
            venta.setUsuario(usuario);
            
            model.addAttribute("venta", venta);
            model.addAttribute("clientes", clienteService.findAll());
            model.addAttribute("tiposPago", pagoService.findAll());
            model.addAttribute("usuarios", usuarioService.findAll());
            model.addAttribute("carritos", carritoService.findAll());
            model.addAttribute("usuario", usuario);
            
            return "ventas/nueva-venta";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar el formulario: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditarVenta(@PathVariable Long id, Model model) {
        try {
            Optional<VentaEntity> venta = ventaService.findById(id);
            if (venta.isPresent()) {
                model.addAttribute("venta", venta.get());
                model.addAttribute("clientes", clienteService.findAll());
                model.addAttribute("tiposPago", pagoService.findAll());
                model.addAttribute("usuarios", usuarioService.findAll());
                model.addAttribute("carritos", carritoService.findAll());
                return "ventas/nueva-venta";
            } else {
                model.addAttribute("error", "Venta no encontrada");
                return "error";
            }
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar el formulario de edición: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarVenta(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            ventaService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Venta eliminada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar la venta: " + e.getMessage());
        }
        return "redirect:/ventas/lista";
    }

    @PostMapping("/guardar")
    @Transactional
    public String guardar(@ModelAttribute VentaEntity venta, Model model, RedirectAttributes redirectAttributes) {
        try {
            // Log de los datos recibidos
            System.out.println("Datos recibidos del formulario:");
            System.out.println("  Cliente ID: " + (venta.getCliente() != null ? venta.getCliente().getId() : "null"));
            System.out.println("  Pago ID: " + (venta.getPago() != null ? venta.getPago().getId() : "null"));
            System.out.println("  Carrito ID: " + (venta.getCarrito() != null ? venta.getCarrito().getId() : "null"));
            System.out.println("  Usuario ID: " + (venta.getUsuario() != null ? venta.getUsuario().getId() : "null"));

            // Verificar que todos los IDs estén presentes
            if (venta.getCliente() == null || venta.getCliente().getId() == null ||
                venta.getPago() == null || venta.getPago().getId() == null ||
                venta.getCarrito() == null || venta.getCarrito().getId() == null ||
                venta.getUsuario() == null || venta.getUsuario().getId() == null) {
                throw new RuntimeException("Faltan datos requeridos para la venta");
            }

            // Cargar las entidades relacionadas usando los IDs del formulario
            ClienteEntity cliente = clienteService.findByIdClienteEntity(venta.getCliente().getId())
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
            
            PagoEntity pago = pagoService.findById(venta.getPago().getId())
                    .orElseThrow(() -> new RuntimeException("Pago no encontrado"));
            
            CarritoEntity carrito = carritoService.findById(venta.getCarrito().getId())
                    .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));
            
            UsuarioEntity usuario = usuarioService.findByIdCUsuarioEntity(venta.getUsuario().getId())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Verificar que las entidades se cargaron correctamente
            if (cliente == null || pago == null || carrito == null || usuario == null) {
                throw new RuntimeException("Error al cargar las entidades relacionadas");
            }

            // Establecer las relaciones con las entidades cargadas
            venta.setCliente(cliente);
            venta.setPago(pago);
            venta.setCarrito(carrito);
            venta.setUsuario(usuario);

            // Si no se estableció fecha, usar la actual
            if (venta.getFechaVenta() == null) {
                venta.setFechaVenta(LocalDateTime.now());
            }

            // --- ADD THESE LOGS ---
            System.out.println("Venta antes de guardar: " + venta);
            System.out.println("  Cliente: " + venta.getCliente());
            System.out.println("  Pago: " + venta.getPago());
            System.out.println("  Carrito: " + venta.getCarrito());
            System.out.println("  Usuario: " + venta.getUsuario());
            System.out.println("  Fecha Venta: " + venta.getFechaVenta());
            // --- END LOGS ---

            // Guardar la venta
            VentaEntity savedVenta = ventaService.save(venta);
            
            // Verificar que se guardó correctamente
            if (savedVenta == null) {
                throw new RuntimeException("Error al guardar la venta. ID: " + venta.getId());
            }
            
            // Verificar que se generó un ID
            if (savedVenta.getId() == null) {
                throw new RuntimeException("La venta se guardó pero no se generó ID. ID: " + venta.getId());
            }
            
            System.out.println("Venta guardada exitosamente con ID: " + savedVenta.getId());
            redirectAttributes.addFlashAttribute("successMessage", "Venta guardada exitosamente");
            return "redirect:/ventas/lista";
        } catch (Exception e) {
            e.printStackTrace(); // Agregar esto para ver el stack trace completo
            redirectAttributes.addFlashAttribute("errorMessage", "Error al guardar la venta: " + e.getMessage());
            model.addAttribute("venta", venta);
            model.addAttribute("clientes", clienteService.findAll());
            model.addAttribute("tiposPago", pagoService.findAll());
            model.addAttribute("usuarios", usuarioService.findAll());
            model.addAttribute("carritos", carritoService.findAll());
            return "ventas/nueva-venta";
        }
    }



    @GetMapping("/ver/{id}")
    public String verDetalle(@PathVariable Long id, Model model) {
        Optional<VentaEntity> venta = ventaService.findById(id);
        if (!venta.isPresent()) {
            model.addAttribute("errorMessage", "Venta no encontrada");
            return "redirect:/venta/lista";
        }
        
        model.addAttribute("venta", venta.get());
        return "ventas/detalle-venta";
    }


}