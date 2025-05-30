package zorroAbarrotes.proyecto.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import zorroAbarrotes.proyecto.model.entity.*;
import zorroAbarrotes.proyecto.model.id.ProductoCarritoId;
import zorroAbarrotes.proyecto.service.PdfService;
import zorroAbarrotes.proyecto.service.carrito.CarritoService;
import zorroAbarrotes.proyecto.service.cliente.ClienteService;
import zorroAbarrotes.proyecto.service.pago.PagoService;
import zorroAbarrotes.proyecto.service.producto.ProductoService;
import zorroAbarrotes.proyecto.service.producto_carrito.ProductoCarritoService;
import zorroAbarrotes.proyecto.service.usuario.UsuarioService;
import zorroAbarrotes.proyecto.service.venta.VentaService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/ventaZorro")
public class VenderController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private CarritoService carritoService;

    @Autowired
    private ProductoCarritoService productoCarritoService;

    @Autowired
    private VentaService ventaService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PagoService pagoService;

    @Autowired
    private PdfService pdfService;

    @Autowired
    private ClienteService clienteService;

    private final String RUTA_IMAGENES = "/imagenes/";

    //quite ("/")
    @GetMapping("/vender")
    public String mostrarVistaVenta(Model model, HttpSession session, Authentication authentication) {
        try {
            String role = "";
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

            for (GrantedAuthority authority : authorities) {
                role = authority.getAuthority();
            }

            // Obtener todos los productos disponibles y filtrar por stock
            List<ProductoEntity> productos = productoService.findAll()
                .stream()
                .filter(p -> p.getStockActual() != null && p.getStockActual() > 0)
                .collect(Collectors.toList());

            // Verificar si algún producto está por debajo del stock mínimo
            List<ProductoEntity> productosBajoStock = productos.stream()
                .filter(p -> p.getStockActual() != null && p.getStockMinimo() != null && 
                        p.getStockActual() < p.getStockMinimo())
                .collect(Collectors.toList());

            // Pasar los productos a la vista
            model.addAttribute("productos", productos);
            model.addAttribute("productosBajoStock", productosBajoStock);

            // Obtener el usuario actual
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated()) {
                model.addAttribute("usuario", auth.getName());
            }

            ClienteEntity cliente = (ClienteEntity) session.getAttribute("clienteReg");
            if (cliente != null && role.equals("ROLE_caja")) {
                System.out.println("diferente nullo");
                model.addAttribute("cliente", cliente);
                return "ventaZorro/vender";
            }else{
                if (role.equals("ROLE_caja") && cliente == null) {
                    return "denegado/cliente-nolog";
                }
            }
            return "ventaZorro/vender";
        } catch (Exception e) {
            e.printStackTrace();
            return "error"; // Página de error genérica
        }
    }

    @GetMapping("/lista-ventasZorro")
    public String listarVentas(
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortField,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            // Configurar paginación y ordenamiento
            Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.fromString(sortDir), sortField)
            );

            // Obtener ventas paginadas con detalles
            Page<VentaEntity> ventas = ventaService.findAllWithDetails(pageable);
            model.addAttribute("ventas", ventas);
            model.addAttribute("currentPage", Optional.of(page));
            model.addAttribute("totalPages", Optional.of(ventas.getTotalPages()));
            model.addAttribute("sortField", sortField);
            model.addAttribute("sortDir", sortDir);
            return "ventaZorro/lista-ventasZorro";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

    @GetMapping("/detalles/{id}")
    public ResponseEntity<Map<String, Object>> getVentaDetalles(@PathVariable Long id) {
        try {
            Optional<VentaEntity> venta = ventaService.findById(id);
            if (venta.isPresent()) {
                VentaEntity ventaData = venta.get();

                // Obtener productos del carrito
                List<ProductoCarritoEntity> productosCarrito =
                    productoCarritoService.findAll().stream()
                        .filter(pc -> pc.getCarrito().equals(ventaData.getCarrito()))
                        .collect(Collectors.toList());

                // Convertir a DTO
                List<Map<String, Object>> productos = productosCarrito.stream()
                    .map(pc -> {
                        Map<String, Object> producto = new HashMap<>();
                        producto.put("nombre", pc.getProducto().getNombre());
                        producto.put("cantidad", pc.getCantidad());
                        producto.put("precioUnitario", pc.getProducto().getPrecio());
                        producto.put("total", pc.getTotal());
                        producto.put("imagen", (RUTA_IMAGENES+pc.getProducto().getImagen()));
                        return producto;
                    })
                    .collect(Collectors.toList());

                // Obtener detalles adicionales del carrito
                CarritoEntity carrito = ventaData.getCarrito();

                return ResponseEntity.ok(Map.of(
                    "idVenta", ventaData.getId(),
                    "cliente", ventaData.getCliente() != null ?
                        ventaData.getCliente().getNombre() + " " +
                        ventaData.getCliente().getApellidoP() : "Sin cliente",
                    "metodoPago", ventaData.getPago().getDescripcion(),
                    "totalVenta", ventaData.getCarrito().getTotal(),
                    "fecha", ventaData.getFechaVenta(),
                    "vendedor", ventaData.getUsuario().getUsername(),
                    "carrito", Map.of(
                        "id", carrito.getId(),
                        "fecha", carrito.getFecha(),
                        "total", carrito.getTotal()
                    ),
                    "productos", productos
                ));
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                Map.of("error", "Error al obtener detalles de venta")
            );
        }
    }

    @DeleteMapping("/eliminar/{id}")
    @Transactional
    public ResponseEntity<Map<String, Object>> eliminarVenta(@PathVariable Long id) {
        try {
            Optional<VentaEntity> venta = ventaService.findById(id);
            if (venta.isPresent()) {
                VentaEntity ventaEntity = venta.get();

                // Delete the venta with cascade
                ventaService.deleteByIdWithCascade(id);

                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Venta eliminada correctamente"
                ));
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Error al eliminar la venta: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/tipos-pago")
    public ResponseEntity<List<PagoEntity>> getTiposPago() {
        List<PagoEntity> pagos = pagoService.findAll();
        return ResponseEntity.ok(pagos);
    }

    @GetMapping("/validar-cliente")
    public ResponseEntity<Map<String, Object>> validarCliente(@RequestParam String identificador) {
        try {
            Optional<ClienteEntity> cliente = Optional.ofNullable(clienteService.findByIdentificador(identificador));

            if (cliente.isPresent()) {
                return ResponseEntity.ok(Map.of(
                        "existe", true,
                        "cliente", cliente.get()
                ));
            } else {
                return ResponseEntity.ok(Map.of(
                        "existe", false
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "existe", false,
                    "message", "Error al validar el cliente"
            ));
        }
    }

    @PutMapping("/modificar/{id}")
    @Transactional
    public ResponseEntity<Map<String, Object>> modificarMetodoPago(@PathVariable Long id, @RequestBody Map<String, Object> datos) {
        try {
            // Log de entrada
            System.out.println("[ModificarVenta] Recibida solicitud para modificar venta ID: " + id);
            System.out.println("[ModificarVenta] Datos recibidos: " + datos);

            // Validar que el ID de venta sea válido
            if (id == null) {
                throw new RuntimeException("ID de venta no válido");
            }

            // Buscar la venta
            Optional<VentaEntity> venta = ventaService.findById(id);
            if (!venta.isPresent()) {
                throw new RuntimeException("Venta no encontrada");
            }

            VentaEntity ventaEntity = venta.get();

            // Obtener el ID del método de pago
            Object idPagoObj = datos.get("idPago");
            if (idPagoObj == null) {
                throw new RuntimeException("Debe proporcionar un ID de método de pago");
            }

            Long idPago = null;
            try {
                idPago = (Long) Long.parseLong(idPagoObj.toString());
            } catch (NumberFormatException e) {
                throw new RuntimeException("ID de método de pago no válido");
            }

            // Buscar el método de pago por ID
            Optional<PagoEntity> pagoOpt = pagoService.findById(idPago);
            if (!pagoOpt.isPresent()) {
                throw new RuntimeException("Método de pago no encontrado");
            }

            PagoEntity pago = pagoOpt.get();

            // Actualizar el método de pago
            ventaEntity.setPago(pago);
            ventaService.save(ventaEntity);

            // Log de éxito
            System.out.println("[ModificarVenta] Método de pago actualizado correctamente para venta ID: " + id);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Método de pago actualizado correctamente",
                "venta", ventaEntity
            ));

        } catch (NumberFormatException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "success", false,
                "message", "Formato del ID de método de pago no válido"
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Error al modificar el método de pago: " + e.getMessage()
            ));
        }
    }

    @PostMapping("/guardar")
    // ¡IMPORTANTE! Cambiar de @RequestParam a @RequestBody para recibir el JSON
    public ResponseEntity<Map<String, Object>> guardarVenta(@RequestBody VentaRequest ventaRequest) {
        CarritoEntity carrito = null;
        VentaEntity savedVenta = null; // Variable para almacenar la venta guardada
        try {
            // Crear nuevo carrito
            carrito = CarritoEntity.builder()
                    .total(ventaRequest.getTotalVenta())
                    .fecha(LocalDateTime.now())
                    .build();
            carrito = carritoService.save(carrito);

            // Crear una lista para ProductoCarritoEntity y asociarla al carrito
            // Esto es crucial para que Hibernate pueda inicializar la colección
            // cuando el carrito se carga posteriormente.
            // Aunque los productos se guardan individualmente, es bueno tener la lista aquí.
            List<ProductoCarritoEntity> productosEnCarrito = new ArrayList<>();


            // Procesar productos del carrito desde el DTO
            for (ProductoVentaDTO productoDto : ventaRequest.getProductos()) {
                ProductoEntity producto = productoService.findById(productoDto.getIdProducto());
                if (producto == null) {
                    throw new RuntimeException("Producto no encontrado: " + productoDto.getIdProducto());
                }

                // Actualizar el stock del producto
                if (producto.getStockActual() != null) {
                    int nuevaCantidad = producto.getStockActual() - productoDto.getCantidad();
                    if (nuevaCantidad < 0) {
                        throw new RuntimeException("No hay suficiente stock para el producto: " + producto.getNombre());
                    }
                    producto.setStockActual(nuevaCantidad);
                    productoService.save(producto);
                } else {
                    throw new RuntimeException("El producto " + producto.getNombre() + " no tiene stock registrado");
                }

                // Crear producto_carrito
                ProductoCarritoEntity productoCarrito = ProductoCarritoEntity.builder()
                        .id(ProductoCarritoId.builder()
                                .idCarrito(carrito.getId())
                                .idProducto(productoDto.getIdProducto())
                                .build())
                        .carrito(carrito)
                        .producto(producto)
                        .cantidad(productoDto.getCantidad())
                        .total(Double.valueOf(productoDto.getPrecioUnitario() * productoDto.getCantidad()))
                        .build();
                productoCarritoService.save(productoCarrito);
                productosEnCarrito.add(productoCarrito); // Añadir a la lista local
            }

            // Establecer la lista de productos en el carrito (aunque ya esté persistido, ayuda a la coherencia del objeto en memoria)
            carrito.setProductosCarrito(productosEnCarrito);
            carritoService.save(carrito); // Guardar el carrito nuevamente para asegurar la relación (aunque cascade debería manejarlo)


            // Obtener usuario actual
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UsuarioEntity usuario = usuarioService.findByUsername(auth.getName());
            if (usuario == null) {
                throw new RuntimeException("Usuario no encontrado");
            }

            // Obtener método de pago
            // Ahora se obtiene directamente del ventaRequest
            PagoEntity pago = pagoService.findById(ventaRequest.getMetodoPago())
                    .orElseThrow(() -> new RuntimeException("Método de pago no encontrado"));

            ClienteEntity cliente = null;
            String numeroCuenta = ventaRequest.getNumeroCuenta();
            if (numeroCuenta != null && !numeroCuenta.trim().isEmpty()) {
                // Buscar por número de cuenta, correo o teléfono
                cliente = clienteService.findByIdentificador(numeroCuenta);// Si no se encuentra, cliente será null
            }

            // Crear venta
            VentaEntity venta = VentaEntity.builder()
                    .fechaVenta(LocalDateTime.now())
                    .usuario(usuario)
                    .pago(pago)
                    .carrito(carrito)
                    .cliente(cliente) // Puede ser null si no se encontró cliente
                    .build();

            // Guardar la venta
            savedVenta = ventaService.save(venta); // Almacenar la venta guardada

            // --- INICIO: LOGS ADICIONALES PARA DEPURACIÓN EN CONTROLLER ---
            System.out.println("--- Depuración en VenderController.guardarVenta ---");
            System.out.println("Venta guardada con ID: " + savedVenta.getId());
            System.out.println("Carrito asociado a la venta guardada: " + savedVenta.getCarrito());
            if (savedVenta.getCarrito() != null) {
                System.out.println("Lista de productos en el carrito de la venta guardada (antes de PDF): " + savedVenta.getCarrito().getProductosCarrito());
                if (savedVenta.getCarrito().getProductosCarrito() != null) {
                    System.out.println("Tamaño de la lista de productos en el carrito de la venta guardada: " + savedVenta.getCarrito().getProductosCarrito().size());
                    for (ProductoCarritoEntity pc : savedVenta.getCarrito().getProductosCarrito()) {
                        System.out.println("  - ProductoCarrito ID: " + pc.getId().getIdProducto() + ", Cantidad: " + pc.getCantidad() + ", Total: " + pc.getTotal());
                        if (pc.getProducto() != null) {
                            System.out.println("    - Producto Nombre: " + pc.getProducto().getNombre() + ", Precio: " + pc.getProducto().getPrecio());
                        } else {
                            System.out.println("    - Producto asociado es NULL");
                        }
                    }
                } else {
                    System.out.println("La lista de productos del carrito es NULL en la venta guardada.");
                }
            } else {
                System.out.println("El objeto Carrito es NULL en la venta guardada.");
            }
            System.out.println("--- FIN Depuración en VenderController ---");
            // --- FIN: LOGS ADICIONALES PARA DEPURACIÓN EN CONTROLLER ---


            if (savedVenta != null) {
                // Si hay cliente registrado, generar y enviar PDF
                if (savedVenta.getCliente() != null && savedVenta.getCliente().getCorreo() != null && !savedVenta.getCliente().getCorreo().isEmpty()) {
                    pdfService.enviarPdfPorCorreo(savedVenta); // Pasar la venta guardada
                }
                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "message", "Venta guardada correctamente",
                        "envioPdf", savedVenta.getCliente() != null && savedVenta.getCliente().getCorreo() != null && !savedVenta.getCliente().getCorreo().isEmpty()
                ));
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Error al guardar la venta"
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Si ocurre un error, intentar eliminar el carrito si ya se creó para evitar datos huérfanos
            if (carrito != null && carrito.getId() != null) {
                try {
                    carritoService.deleteById(carrito.getId());
                    System.err.println("Carrito ID " + carrito.getId() + " eliminado debido a un error en la venta.");
                } catch (Exception ex) {
                    System.err.println("Error al intentar eliminar el carrito ID " + carrito.getId() + " después de un fallo en la venta: " + ex.getMessage());
                }
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Error interno al procesar la venta: " + e.getMessage()
            ));
        }
    }
}

// DTO para recibir los datos de la venta desde el frontend
// Este archivo debe estar en tu proyecto Java, por ejemplo:
// zorroAbarrotes.proyecto.controller.VentaRequest.java
class VentaRequest {
    private String numeroCuenta;
    private Long metodoPago;
    private List<ProductoVentaDTO> productos;
    private Double totalVenta;

    // Getters y setters
    public String getNumeroCuenta() {
        return numeroCuenta;
    }

    public void setNumeroCuenta(String numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
    }

    public Long getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(Long metodoPago) {
        this.metodoPago = metodoPago;
    }

    public List<ProductoVentaDTO> getProductos() {
        return productos;
    }

    public void setProductos(List<ProductoVentaDTO> productos) {
        this.productos = productos;
    }

    public Double getTotalVenta() {
        return totalVenta;
    }

    public void setTotalVenta(Double totalVenta) {
        this.totalVenta = totalVenta;
    }
}

// DTO para cada producto dentro de la lista de productos de la venta
// Este archivo debe estar en tu proyecto Java, por ejemplo:
// zorroAbarrotes.proyecto.controller.ProductoVentaDTO.java
class ProductoVentaDTO {
    private Long idProducto;
    private Integer cantidad;
    private Double precioUnitario;

    // Getters y setters
    public Long getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Long idProducto) {
        this.idProducto = idProducto;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(Double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }
}
