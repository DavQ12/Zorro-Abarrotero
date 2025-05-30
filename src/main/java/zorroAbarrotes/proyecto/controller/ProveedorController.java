package zorroAbarrotes.proyecto.controller;

import jakarta.activation.DataHandler;
import jakarta.activation.FileDataSource;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import zorroAbarrotes.proyecto.model.entity.*;
import zorroAbarrotes.proyecto.model.id.ProveedorProductoId;
import zorroAbarrotes.proyecto.service.pedido.PedidoService;
import zorroAbarrotes.proyecto.service.producto.ProductoService;
import zorroAbarrotes.proyecto.service.proveedor.ProveedorService;
import zorroAbarrotes.proyecto.service.proveedor_producto.ProveedorProductoService;
import zorroAbarrotes.proyecto.service.security.UserDetailsServiceImp;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import zorroAbarrotes.proyecto.service.usuario.UsuarioService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

@Controller
@RequestMapping("proveedor")
public class ProveedorController {

    @Autowired
    ProveedorService proveedorService;

    @Autowired
    ProveedorProductoService proveedorProductoService;
    @Autowired
    private UserDetailsServiceImp userDetailsServiceImp;
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private ProductoService productoService;
    private final String RUTA_IMAGENES = "/home/fercw/ImagenesZorro/";
    //private final String RUTA_IMAGENES = "/home/angelquintero/ImagenesZorro/";
    @Autowired
    private PedidoService pedidoService;

    @GetMapping("alta-proveedor")
    public String altaProveedor(Model model) {
        ProveedorEntity proveedor = new ProveedorEntity();
        model.addAttribute("proveedor", proveedor);
        model.addAttribute("contenido", "Nuevo Proveedor");
        return "proveedores/alta-proveedor";
    }

    @PostMapping("guardar-proveedor")
    public String guardarProveedor(@Valid @ModelAttribute("proveedor") ProveedorEntity proveedor,
                                   BindingResult result, RedirectAttributes flash, Model model) {

        ProveedorEntity proveedorReg = null;
        if (result.hasErrors()) {
            return "proveedores/alta-proveedor";
        }

        if(proveedor.getId() != null) {
            proveedorReg = proveedorService.findByCorreo(proveedor.getCorreo());
            if (proveedorReg != null && !Objects.equals(proveedorReg.getId(), proveedor.getId())) {
                flash.addFlashAttribute("errorMessage", "Ya existe un proveedor con ese correo.");
                flash.addAttribute("id", proveedor.getId());
                return "redirect:/proveedor/editar/{id}";
            }

            proveedorReg = proveedorService.findByTelefono(proveedor.getTelefono());
            if (proveedorReg != null && !Objects.equals(proveedorReg.getId(), proveedor.getId())) {
                flash.addFlashAttribute("errorMessage", "Ya existe un proveedor con ese telefono.");
                flash.addAttribute("id", proveedor.getId());
                return "redirect:/proveedor/editar/{id}";
            }

            proveedorService.save(proveedor);
            flash.addFlashAttribute("success", "Proveedor actualizado con éxito");
            return "redirect:/proveedor/lista-proveedor";
        }

        proveedorReg = proveedorService.findByCorreo(proveedor.getCorreo());
        if (proveedorReg != null) {
            flash.addFlashAttribute("errorMessage", "Ya existe un proveedor con ese correo");
            return "redirect:/proveedor/alta-proveedor";
        }

        proveedorReg = proveedorService.findByTelefono(proveedor.getTelefono());
        if (proveedorReg != null) {
            flash.addFlashAttribute("errorMessage", "Ya existe un proveedor con ese telefono");
            return "redirect:/proveedor/alta-proveedor";
        }
        System.out.println(proveedor);
        proveedorService.save(proveedor);
        flash.addFlashAttribute("success", "Proveedor registrado con éxito");
        return "redirect:/proveedor/lista-proveedor";
    }

    //mostrar a los proveedores para hacer los pedidos
    @GetMapping("lista-pedidos")
    public String listaPedidos(Model model) {
        List<ProveedorProductoEntity> datos = proveedorProductoService.findAll();
        model.addAttribute("datos", datos);
        model.addAttribute("contenido", "Hacer Pedidos");
        return "proveedores/lista-pedido";
    }

    @GetMapping("lista-proveedor")
    public String listaProveedor(Model model) {
        List<ProveedorEntity> lista = proveedorService.findAll();
        model.addAttribute("lista", lista);
        return "proveedores/lista-proveedor";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditarProv(@PathVariable Long id, Model model, RedirectAttributes flash) {
        ProveedorEntity proveedor = proveedorService.findById(id);

        if (proveedor == null) {
            flash.addFlashAttribute("errorMessage", "El proveedor no existe en la base de datos");
            return "redirect:/proveedor/lista-proveedor";
        }

        model.addAttribute("proveedor", proveedor);
        model.addAttribute("contenido", "Editar Proveedor");
        return "proveedores/alta-proveedor";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes flash) {
        try {
            proveedorService.deleteById(id);
            flash.addFlashAttribute("success", "Proveedor eliminado con éxito");
        } catch (Exception e) {
            flash.addFlashAttribute("errorMessage", "No se puede eliminar el proveedor: ");
        }
        return "redirect:/proveedor/lista-proveedor";
    }



    //mandar correo al proveedor
    @PostMapping("hacer-pedido")
    public String hacerPedido(
            @RequestParam Long productoId,
            @RequestParam Long proveedorId,
            @RequestParam Integer cantidad,
            Model model, RedirectAttributes flash
    ){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String usuario = auth.getName();
        //buscamos al proveedor por id para obtener el correo
        ProveedorEntity proveedor = proveedorService.findById(proveedorId);
        ProductoEntity producto = productoService.findById(productoId);
        String correo = proveedor.getCorreo();
        //obtenemos el costo unitario del producto con el proveedor para obtener el total
        ProveedorProductoId id = ProveedorProductoId.builder()
                .idProveedor(proveedorId)
                .idProducto(productoId)
                .build();

        ProveedorProductoEntity proveedorProducto = proveedorProductoService.findById(id);
        //total del pedido segun la cantidad elegida.
        Double costo = proveedorProducto.getCostoUnitario();
        Double total = costo* cantidad;

        //obtenemos el usuario q ha hecho el pedido
        UsuarioEntity usuarioActual = usuarioService.findByUsername(usuario);

        PedidoEntity pedido = new PedidoEntity();
        pedido.setProveedor(proveedor);
        pedido.setProducto(producto);
        pedido.setTotal(total);
        pedido.setCantidadProducto(cantidad);
        pedido.setUsuario(usuarioActual);
        pedido.setFechaPedido(LocalDateTime.now());
        System.out.println("pedido realizado-------------------");
        System.out.println(pedido);
        pedidoService.save(pedido);
        System.out.println("---------------------------");

        //configuracion para mandar el correo
        String password="qbmvmxqwtafzmkqn";
        String username = "angelquinterodev12@gmail.com";
        String fechaEnvio = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
        //propiedades del servicio de gmail
        Properties p = System.getProperties();
        p.setProperty("mail.smtps.host", "smtp.gmail.com");
        p.setProperty("mail.smtps.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        p.setProperty("mail.smtps.socketFactory.fallback", "false");
        p.setProperty("mail.smtp.port", "465");
        p.setProperty("mail.smtp.socketFactory.port", "465");
        p.setProperty("mail.smtps.auth", "true");
        p.setProperty("mail.smtp.ssl.trust", "smtp.gmail.com");
        p.setProperty("mail.smtps.ssl.trust", "smtp.gmail.com");
        p.setProperty("mail.smtp.ssl.quitwait", "false");
        //logica para mandar el correo
        String cadena="<html><body style='font-family: Arial, sans-serif;'>"
                + "<h3>Estimado(a) " + proveedor.getNombre() + ",</h3>"
                + "<p>De <strong>Zorro Abarrotero</strong>, Sucursal <strong>FES Aragón</strong>, se solicita al proveedor lo siguiente:</p>"

                + "<table border='1' cellpadding='10' cellspacing='0' style='border-collapse: collapse;'>"
                + "<tr style='background-color: #f2f2f2;'>"
                + "<th>Empresa</th>"
                + "<th>Producto</th>"
                + "<th>Cantidad</th>"
                + "<th>Costo Unitario</th>"
                + "<th>Total</th>"
                + "</tr>"

                + "<tr>"
                + "<td>" + proveedor.getEmpresa() + "</td>"
                + "<td>" + producto.getNombre() + "</td>"
                + "<td>" + cantidad + "</td>"
                + "<td>$" + String.format("%.2f", costo) + "</td>"
                + "<td>$" + String.format("%.2f", total) + "</td>"
                + "</tr>"
                + "</table>"

                + "<br><p>Enviado el <strong>" + fechaEnvio + "</strong>.</p>"
                + "<p>Gracias por su atención.</p>"
                + "</body></html>";
        try {
            Session session = Session.getInstance(p, null);
            MimeMessage message = new MimeMessage(session);
            MimeBodyPart texto = new MimeBodyPart();
            texto.setContent(cadena, "text/html;charset=utf-8");
            BodyPart adjunto = new MimeBodyPart();
            String r = RUTA_IMAGENES + producto.getImagen();
            adjunto.setDataHandler(new DataHandler(new FileDataSource(r)));
            adjunto.setFileName(producto.getImagen());
            Multipart multiple = new MimeMultipart();
            multiple.addBodyPart(texto);
            multiple.addBodyPart(adjunto);

            //remitente
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(correo, false));
            message.setSubject("Detalles del pedido");
            message.setContent(multiple);
            message.setSentDate(new Date());

            Transport transport = session.getTransport("smtps");
            transport.connect("smtp.gmail.com", username, password);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();

            flash.addFlashAttribute("success", "El correo se mando con exito");
            return "redirect:/proveedor/lista-pedidos";

        }catch (Exception e) {
            flash.addFlashAttribute("errorMessage","Error al mandar correo: " + e);
            return "redirect:/proveedor/lista-pedidos";
        }

    }
}
