package zorroAbarrotes.proyecto.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import zorroAbarrotes.proyecto.model.entity.ClienteEntity;
import zorroAbarrotes.proyecto.service.cliente.ClienteService;

import java.util.Objects;
import java.util.Random;

@Controller
public class ClienteController {

    @Autowired
    private ClienteService clienteService;


    @GetMapping("/clientes/registro")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("cliente", new ClienteEntity());
        return "clientes/registro-cliente";
    }


    //validacion de login de los clientes, manda a la ventana donde el cliente puede agregar productos al carrito
    @PostMapping("/clientes/login")
    public  String loginCliente(@ModelAttribute(value = "cliente") ClienteEntity cliente,
                                RedirectAttributes flash,
                                HttpSession session) {
        if (cliente.getIdentificador()==null || cliente.getIdentificador().isEmpty()){
            flash.addFlashAttribute("errorMessage", "Por favor ingrese un identificador");
            return "redirect:/login";
        }

        ClienteEntity clienteReg;
        clienteReg = clienteService.findByIdentificador(cliente.getIdentificador());
        String pass = cliente.getContrasena();
        //valida si hay cliente o no
        if(clienteReg == null){
            flash.addFlashAttribute("errorMessage", "Cliente no encontrado, vuelve a intentarlo");
            System.out.println("cliente no encontrado");
            return "redirect:/login";
        }else{
            //valida la contraseña sea la correcta
            if(new BCryptPasswordEncoder().matches(pass, clienteReg.getContrasena())){
                session.setAttribute("clienteReg", clienteReg);
                return "redirect:/clientes/inicio";
            }else{
                flash.addFlashAttribute("errorMessage", "Contraseña incorrecta, vuelve a intentarlo");
                return "redirect:/login";
            }
        }

    }

    @GetMapping("/clientes/inicio")
    public String mostrarIncio(Model model, HttpSession session) {
        System.out.println("mostrarIncio");
        System.out.println(session.getAttribute("clienteReg"));
        ClienteEntity cliente = (ClienteEntity) session.getAttribute("clienteReg");
        model.addAttribute("cliente", cliente);
        return "plantillas/index";
    }

    @PostMapping("/clientes/registro-admin")
    public String guardarClienteRegistroAdmin(@Valid @ModelAttribute(value = "cliente") ClienteEntity cliente,
                                         BindingResult result,RedirectAttributes flash,Model model) {

        ClienteEntity clienteReg = null;

        if (cliente.getContrasena().matches(".*\\s+.*")) {
            System.out.println("contrasena incorrecta");
            result.rejectValue("contrasena", "error.contrasena", "La contraseña no debe contener espacios en blanco.");
        }

        if (result.hasErrors()){
            return "clientes/registro-cliente";
        }

        //buscar por correo
        clienteReg = clienteService.findByIdentificador(cliente.getCorreo());
        if(clienteReg!=null){
            flash.addFlashAttribute("errorMessage", "Ya existe un cliente con el correo");
            return "redirect:/clientes/registro";
        }
        //buscar por telefono
        clienteReg = clienteService.findByIdentificador(cliente.getTelefono());
        if(clienteReg!=null){
            flash.addFlashAttribute("errorMessage", "Ya existe un cliente con ese telefono");
            return "redirect:/clientes/registro";
        }

        //logica para registrar al cliente.
        //encriptar contrasena
        String pass = new BCryptPasswordEncoder().encode(cliente.getContrasena());
        cliente.setContrasena(pass);
        //generar el num de cuenta
        String numCuenta;
        do {
            // Formato: C + 8 dígitos aleatorios (ej. C12345678)
            numCuenta = cliente.getNombre() + String.format("%06d", new Random().nextInt(1000000));
        } while(clienteService.findByIdentificador(numCuenta)!=null);

        cliente.setNumCuenta(numCuenta);
        flash.addFlashAttribute("success", "Cliente creado con éxito");
        clienteService.save(cliente);
        return "redirect:/clientes/lista";
    }

    @PostMapping("/clientes/registro")
    public String guardarClienteRegistro(@Valid @ModelAttribute(value = "cliente") ClienteEntity cliente,
                                         BindingResult result,
                                         RedirectAttributes flash,
                                         @RequestParam String contrasena,
                                         Model model) {
        if (result.hasErrors()){
            return "clientes/registro-cliente";
        }

        ClienteEntity clienteReg = null;
        //logica para actualizar un cliente
        if(cliente.getId() != null){

            clienteReg= clienteService.findById(cliente.getId());
            if(contrasena ==""){
                //matiene la contraseña
                cliente.setContrasena(clienteReg.getContrasena());

            }else{

                if (contrasena.matches(".*\\s+.*")) {
                    result.rejectValue("contrasena", "error.contrasena", "La contraseña no debe contener espacios en blanco.");
                }

                if (result.hasErrors()){
                    model.addAttribute("editar", true);
                    return "clientes/registro-cliente";
                }

                if(contrasena.length()<6){

                    flash.addFlashAttribute("errorMessage", "Contraseña debe ser mayor que 6 caracteres");
                    flash.addAttribute("id", cliente.getId());
                    return "redirect:/cliente/editar/{id}";
                }else{
                    // se encripta la nueva contrasena
                    String pass = new BCryptPasswordEncoder().encode(contrasena);
                    cliente.setContrasena(pass);
                }

            }

            clienteReg=clienteService.findByTelefono(cliente.getTelefono());
            if (clienteReg != null && !Objects.equals(clienteReg.getId(), cliente.getId())) {
                flash.addFlashAttribute("errorMessage", "Ya existe cliente con ese telefono.");
                flash.addAttribute("id", cliente.getId());
                return "redirect:/clientes/editar/{id}";
            }

            clienteReg=clienteService.findByCorreo(cliente.getCorreo());
            if (clienteReg != null && !Objects.equals(clienteReg.getId(), cliente.getId())) {
                flash.addFlashAttribute("errorMessage", "Ya existe un cliente con ese correo.");
                flash.addAttribute("id", cliente.getId());
                return "redirect:/clientes/editar/{id}";
            }
            String numCuenta;
            do {
                // Formato: C + 8 dígitos aleatorios (ej. C12345678)
                numCuenta = cliente.getNombre() + String.format("%06d", new Random().nextInt(1000000));
            } while(clienteService.findByIdentificador(numCuenta)!=null);

            cliente.setNumCuenta(numCuenta);
            flash.addFlashAttribute("success", "Cliente actualizado con éxito");
            clienteService.save(cliente);
            return "redirect:/clientes/lista";

        }


        if (cliente.getContrasena().matches(".*\\s+.*")) {
            System.out.println("contrasena incorrecta");
            result.rejectValue("contrasena", "error.contrasena", "La contraseña no debe contener espacios en blanco.");
        }
        if (result.hasErrors()){
            return "clientes/registro-cliente";
        }

        //buscar por correo
        clienteReg = clienteService.findByIdentificador(cliente.getCorreo());
        if(clienteReg!=null){
            flash.addFlashAttribute("errorMessage", "Ya existe un cliente con el correo");
            return "redirect:/clientes/registro";
        }
        //buscar por telefono
        clienteReg = clienteService.findByIdentificador(cliente.getTelefono());
        if(clienteReg!=null){
            flash.addFlashAttribute("errorMessage", "Ya existe un cliente con ese telefono");
            return "redirect:/clientes/registro";
        }

        //logica para registrar al cliente.
        //encriptar contrasena
        String pass = new BCryptPasswordEncoder().encode(cliente.getContrasena());
        cliente.setContrasena(pass);
        //generar el num de cuenta
        String numCuenta;
        do {
            // Formato: C + 8 dígitos aleatorios (ej. C12345678)
            numCuenta = cliente.getNombre() + String.format("%06d", new Random().nextInt(1000000));
        } while(clienteService.findByIdentificador(numCuenta)!=null);

        cliente.setNumCuenta(numCuenta);
        flash.addFlashAttribute("success", "Cliente creado con éxito");
        clienteService.save(cliente);
        return "redirect:/login";
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

//    @PostMapping("/clientes/guardar")
//    public String guardarCliente(ClienteEntity cliente, RedirectAttributes flash) {
//        try {
//            // Validar campos obligatorios
//            if (cliente.getNombre() == null || cliente.getNombre().isEmpty() ||
//                cliente.getApellidoP() == null || cliente.getApellidoP().isEmpty() ||
//                cliente.getCorreo() == null || cliente.getCorreo().isEmpty()) {
//                flash.addFlashAttribute("errorMessage", "Todos los campos obligatorios deben estar completos");
//                return "redirect:/clientes/nuevo";
//            }
//
//            // Si es un cliente existente, buscarlo
//            ClienteEntity clienteExistente = clienteService.findById(cliente.getId());
//            if (clienteExistente != null) {
//                // Validar que el correo no esté registrado por otro cliente
//                if (!cliente.getCorreo().equals(clienteExistente.getCorreo()) &&
//                    clienteService.findByCorreo(cliente.getCorreo()).isPresent()) {
//                    flash.addFlashAttribute("errorMessage", "El correo electrónico ya está registrado en el sistema");
//                    return "redirect:/clientes/editar/" + cliente.getId();
//                }
//
//                // Validar longitud de la contraseña si se proporcionó una nueva
//                if (cliente.getContrasena() != null && !cliente.getContrasena().isEmpty() && cliente.getContrasena().length() < 6) {
//                    flash.addFlashAttribute("errorMessage", "La contraseña debe tener al menos 6 caracteres");
//                    return "redirect:/clientes/editar/" + cliente.getId();
//                }
//
//                // Actualizar los campos
//                clienteExistente.setNombre(cliente.getNombre());
//                clienteExistente.setApellidoP(cliente.getApellidoP());
//                clienteExistente.setApellidoM(cliente.getApellidoM());
//                clienteExistente.setCorreo(cliente.getCorreo());
//                clienteExistente.setTelefono(cliente.getTelefono());
//                clienteExistente.setNumCuenta(cliente.getNumCuenta());
//
//                // Actualizar la contraseña solo si se proporcionó una nueva
//                if (cliente.getContrasena() != null && !cliente.getContrasena().isEmpty()) {
//                    clienteExistente.setContrasena(cliente.getContrasena());
//                }
//
//                clienteService.save(clienteExistente);
//                flash.addFlashAttribute("success", "Cliente actualizado con éxito");
//            } else {
//                // Es un nuevo cliente
//                clienteService.save(cliente);
//                flash.addFlashAttribute("success", "Cliente creado con éxito");
//            }
//            return "redirect:/clientes/lista";
//        } catch (Exception e) {
//            System.out.println("ERROR: " + e.getMessage());
//            flash.addFlashAttribute("errorMessage", "Error al guardar el cliente: " + e.getMessage());
//            return "redirect:/clientes/nuevo";
//        }
//    }

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
