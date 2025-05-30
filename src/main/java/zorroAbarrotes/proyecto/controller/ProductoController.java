package zorroAbarrotes.proyecto.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.http.MediaType;
import zorroAbarrotes.proyecto.model.entity.ProductoEntity;
import zorroAbarrotes.proyecto.model.entity.ProveedorEntity;
import zorroAbarrotes.proyecto.model.entity.ProveedorProductoEntity;
import zorroAbarrotes.proyecto.model.id.ProveedorProductoId;
import zorroAbarrotes.proyecto.service.producto.ProductoService;
import zorroAbarrotes.proyecto.service.categoria.CategoriaService;
import zorroAbarrotes.proyecto.service.proveedor.ProveedorService;
import zorroAbarrotes.proyecto.service.proveedor_producto.ProveedorProductoService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.List;

@Controller
@RequestMapping
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private CategoriaService categoriaService;
    @Autowired
    private ProveedorService proveedorService;
    @Autowired
    private ProveedorProductoService proveedorProductoService;

    //private final String RUTA_IMAGENES = "/home/fercw/ImagenesZorro";
    private final String RUTA_IMAGENES = "/home/angelquintero/ImagenesZorro/";

    @GetMapping("/image/productos/{nombreArchivo:.+}")
    public ResponseEntity<Resource> obtenerImagen(@PathVariable String nombreArchivo) {
        try {
            Path rutaArchivo = Paths.get(RUTA_IMAGENES).resolve(nombreArchivo).normalize();
            Resource recurso = new UrlResource(rutaArchivo.toUri());

            if (recurso.exists() && recurso.isReadable()) {
                String contentType = Files.probeContentType(rutaArchivo);
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + recurso.getFilename())
                        .contentType(contentType != null ? MediaType.parseMediaType(contentType) : MediaType.APPLICATION_OCTET_STREAM)
                        .body(recurso);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/productos/lista")
    public String listarProductos(Model model) {
        List<ProductoEntity> productos = productoService.findAll();
        //List<ProductoEntity> productosConImagenes = productoService.findByImagenNotNull();
        model.addAttribute("productos", productos);
        //model.addAttribute("productosConImagenes", productosConImagenes);
        return "productos/lista-productos";
    }

    @GetMapping("/productos/proveedor/{id}")
    public String productoProveedor(@PathVariable Long id, Model model) {
        Long idProducto=id;
        List<ProveedorEntity> proveedores = proveedorService.findAll();
        model.addAttribute("proveedores", proveedores);
        model.addAttribute("idProducto", idProducto);
        return "productos/proveedor-producto";
    }

    @PostMapping("/productos/asignar-proveedor/{id}")
    public String asignarProveedor(
            @PathVariable Long id,
            @RequestParam Long proveedorId,
            @RequestParam String costo,
            Model model,
            RedirectAttributes redirectAttributes) {

        Long idProducto=id;
        try {
            double costoDecimal = Double.parseDouble(costo);
            if (costoDecimal <= 0) {
                redirectAttributes.addFlashAttribute("errorMessage", "El costo debe ser mayor a cero.");
                model.addAttribute("idProducto", idProducto);
                return "redirect:/productos/proveedor/{id}"; // o la URL donde está el formulario
            }

            System.out.println("idProducto = " + idProducto);
            System.out.println("idProveedor  = " + proveedorId);
            // Asignar proveedor con costo válido
            //productoService.asignarProveedor(id, proveedorId, costoDecimal);
            ProveedorProductoId proveedorProductoId = ProveedorProductoId.builder()
                    .idProveedor(proveedorId)
                    .idProducto(idProducto)
                    .build();
            ProductoEntity producto = productoService.findById(idProducto);
            ProveedorEntity proveedor = proveedorService.findById(proveedorId);
            ProveedorProductoEntity proveedorProducto = ProveedorProductoEntity.builder()
                    .id(proveedorProductoId)
                    .costoUnitario(costoDecimal)
                    .proveedor(proveedor)
                    .producto(producto)
                    .build();

            System.out.println("proveedorProducto = " + proveedorProducto);
            proveedorProductoService.save(proveedorProducto);
            redirectAttributes.addFlashAttribute("success", "Proveedor asignado correctamente con costo.");
            return "redirect:/productos/lista";

        } catch (NumberFormatException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "El costo debe ser un número decimal válido.");
            model.addAttribute("idProducto", idProducto);
            return "redirect:/productos/proveedor/{id}"; // o la URL donde está el formulario
        }
    }


    @GetMapping("/productos/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("producto", new ProductoEntity());
        model.addAttribute("categorias", categoriaService.findAll());

        return "productos/registro-producto";
    }

    @GetMapping("/productos/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        ProductoEntity producto = productoService.findById(id);
        if (producto != null) {
            model.addAttribute("producto", producto);
            model.addAttribute("categorias", categoriaService.findAll());
            return "productos/registro-producto";
        }
        return "redirect:/productos/lista";
    }


//    @PostMapping("/productos/guardardos")
//    public String guardar(@Valid @ModelAttribute ProductoEntity producto,
//                                 @RequestParam(value = "archivo", required = false) MultipartFile archivo,
//                                 BindingResult result,
//                                 RedirectAttributes flash) {
//        if (result.hasErrors()) {
//            return "productos/registro-producto";
//        }
//        try {
//            ProductoEntity productoGuardado = producto;
//
//            // Si se subió una nueva imagen
//            if (archivo != null && !archivo.isEmpty()) {
//                // Validar tamaño del archivo (máximo 5MB)
//                if (archivo.getSize() > 5 * 1024 * 1024) {
//                    flash.addFlashAttribute("errorMessage", "El archivo es demasiado grande. Máximo permitido: 5MB");
//                    return "redirect:/productos/nuevo";
//                }
//
//                // Validar tipo de archivo
//                String contentType = archivo.getContentType();
//                if (contentType == null ||
//                    (!contentType.startsWith("image/jpeg") &&
//                     !contentType.startsWith("image/png") &&
//                     !contentType.startsWith("image/jpg"))) {
//                    flash.addFlashAttribute("errorMessage", "Formato de archivo no válido. Solo se aceptan JPG, PNG y jpeg");
//                    return "redirect:/productos/nuevo";
//                }
//
//                //
//
//                // Usar el nombre del archivo
//                String nombreArchivo = archivo.getOriginalFilename();
//                Path rutaArchivo = Paths.get(RUTA_IMAGENES).resolve(nombreArchivo);
//
//                // Si el archivo ya existe, no hacer nada
//                if (!Files.exists(rutaArchivo)) {
//                    // Guardar la imagen
//                    Files.createDirectories(rutaArchivo.getParent());
//                    Files.copy(archivo.getInputStream(), rutaArchivo);
//                }
//
//                // Actualizar la ruta de la imagen en el producto
//                productoGuardado.setImagen(nombreArchivo);
//            }
//
//            // Si es edición y no se subió nueva imagen, mantener la imagen existente
//            if (producto.getId() != null && archivo == null || archivo.isEmpty()) {
//                ProductoEntity productoExistente = productoService.findById(producto.getId());
//                if (productoExistente != null && productoExistente.getImagen() != null) {
//                    productoGuardado.setImagen(productoExistente.getImagen());
//                }
//            }
//
//            // Establecer la relación con la categoría
//            //productoGuardado.setCategoria(producto.getCategoria());
//
//            // Guardar el producto
//            //productoService.save(productoGuardado);
//            System.out.println(productoGuardado);
//            flash.addFlashAttribute("successMessage", "Producto guardado exitosamente");
//            return "redirect:/productos/lista";
//        } catch (Exception e) {
//            flash.addFlashAttribute("errorMessage", "Error al guardar el producto: " + e.getMessage());
//            return "redirect:/productos/nuevo";
//        }
//    }

    //version 2.0
    @PostMapping("/productos/guardar")
    public String guardarProducto(@Valid @ModelAttribute("producto") ProductoEntity producto, BindingResult result,
                                  @RequestParam(value = "archivo", required = false) MultipartFile archivo,
                                  RedirectAttributes flash, Model model) {
        System.out.println("entro al guardar");
        System.out.println(producto);

        if (result.hasErrors()) {
                System.out.println("entra errores");
                model.addAttribute("categorias", categoriaService.findAll());
                return "productos/registro-producto";
        }

        try {
            ProductoEntity productoGuardado = producto;
            System.out.println(productoGuardado);

            // Si se subió una nueva imagen
            if (archivo != null && !archivo.isEmpty()) {
                // Validar tamaño del archivo (máximo 5MB)
                if (archivo.getSize() > 5 * 1024 * 1024) {
                    flash.addFlashAttribute("errorMessage", "El archivo es demasiado grande. Máximo permitido: 5MB");
                    return "redirect:/productos/nuevo";
                }

                // Validar tipo de archivo
                String contentType = archivo.getContentType();
                System.out.println(contentType);
                if (contentType == null ||
                        (!contentType.startsWith("image/jpeg") &&
                                !contentType.startsWith("image/png") &&
                                !contentType.startsWith("image/jpg"))) {
                    flash.addFlashAttribute("errorMessage", "Formato de archivo no válido. Solo se aceptan JPG, PNG y jpeg");
                    return "redirect:/productos/nuevo";
                }

                //guardar la imagenes fuera del proyecto
                String ruta = RUTA_IMAGENES;
                //obtener la extension del proyecto
                String nombreOriginal = archivo.getOriginalFilename();
                if (nombreOriginal == null) {
                    flash.addFlashAttribute("errorMessage", "No se pudo obtener el nombre del archivo");
                    return "redirect:/productos/nuevo";
                }
                
                int index = nombreOriginal.lastIndexOf(".");
                String extension = nombreOriginal.substring(index);
                //nombra el archivo con un timestamp para evitar duplicados
                String nombreImagen = System.currentTimeMillis() + extension;
                Path rutaAbsoluta = Paths.get(ruta, nombreImagen);
                
                // Crear directorio si no existe
                Files.createDirectories(rutaAbsoluta.getParent());
                
                // Guardar el archivo
                Files.copy(archivo.getInputStream(), rutaAbsoluta);
                productoGuardado.setImagen(nombreImagen);
            }

            // Verificar stock mínimo
            if (productoGuardado.getStockActual() != null && 
                productoGuardado.getStockMinimo() != null &&
                productoGuardado.getStockActual() <= productoGuardado.getStockMinimo()) {
                flash.addFlashAttribute("warning", "Stock bajo para el producto: " + productoGuardado.getNombre() + 
                    ". Stock actual: " + productoGuardado.getStockActual() + 
                    ". Stock mínimo: " + productoGuardado.getStockMinimo());
            }

            // Si es edición y no se subió nueva imagen, mantener la imagen existente
            if (producto.getId() != null && archivo == null || archivo.isEmpty()) {
                ProductoEntity productoExistente = productoService.findById(producto.getId());
                if (productoExistente != null && productoExistente.getImagen() != null) {
                    productoGuardado.setImagen(productoExistente.getImagen());
                }
            }

            // Guardar el producto
            productoService.save(productoGuardado);
            flash.addFlashAttribute("success", "Producto guardado exitosamente");
            return "redirect:/productos/lista";
        } catch (Exception e) {
            flash.addFlashAttribute("errorMessage", "No ha seleccionado ninguna imagen");
            return "redirect:/productos/nuevo";
        }
    }

    @PostMapping("/productos/eliminar/{id}")
    public String eliminarProducto(@PathVariable Long id, @RequestParam(value = "_method", required = false) String method, RedirectAttributes flash) {
        try {
            ProductoEntity producto = productoService.findById(id);
            if (producto != null) {
                // Eliminar la imagen si existe
                if (producto.getImagen() != null && !producto.getImagen().isEmpty()) {
                    Path rutaImagen = Paths.get(RUTA_IMAGENES).resolve(producto.getImagen());
                    if (Files.exists(rutaImagen)) {
                        Files.delete(rutaImagen);
                    }
                }
                productoService.deleteById(id);
                flash.addFlashAttribute("success", "Producto eliminado con éxito");
            }
        } catch (Exception e) {
            flash.addFlashAttribute("errorMessage", "Error al eliminar el producto: " + e.getMessage());
        }
        return "redirect:/productos/lista";
    }



    @GetMapping("/productos/imagen/{nombreArchivo:.+}")
    public ResponseEntity<Resource> verImagen(@PathVariable String nombreArchivo) {
        try {
            Path rutaArchivo = Paths.get(RUTA_IMAGENES).resolve(nombreArchivo).normalize();
            Resource recurso = new UrlResource(rutaArchivo.toUri());

            if (recurso.exists() && recurso.isReadable()) {
                String contentType = Files.probeContentType(rutaArchivo);
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + recurso.getFilename())
                        .contentType(contentType != null ? MediaType.parseMediaType(contentType) : MediaType.APPLICATION_OCTET_STREAM)
                        .body(recurso);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
