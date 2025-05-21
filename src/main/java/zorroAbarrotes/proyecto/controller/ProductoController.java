package zorroAbarrotes.proyecto.controller;

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
import zorroAbarrotes.proyecto.service.producto.ProductoService;
import zorroAbarrotes.proyecto.service.categoria.CategoriaService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private CategoriaService categoriaService;

    private final String RUTA_IMAGENES = System.getProperty("user.dir") + "/src/main/resources/static/image/productos/";

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
        List<ProductoEntity> productosConImagenes = productoService.findByImagenNotNull();
        model.addAttribute("productos", productos);
        model.addAttribute("productosConImagenes", productosConImagenes);
        return "productos/lista-productos";
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

    @PostMapping("/productos/guardar")
    public String guardarProducto(@ModelAttribute ProductoEntity producto,
                                 @RequestParam(value = "archivo", required = false) MultipartFile archivo,
                                 BindingResult result,
                                 RedirectAttributes flash) {
        try {
            ProductoEntity productoGuardado = producto;
            
            // Si se subió una nueva imagen
            if (archivo != null && !archivo.isEmpty()) {
                // Validar tamaño del archivo (máximo 5MB)
                if (archivo.getSize() > 5 * 1024 * 1024) {
                    flash.addFlashAttribute("errorMessage", "El archivo es demasiado grande. Máximo permitido: 5MB");
                    return "redirect:/productos/nuevo";
                }

                // Validar tipo de archivo
                String contentType = archivo.getContentType();
                if (contentType == null || 
                    (!contentType.startsWith("image/jpeg") && 
                     !contentType.startsWith("image/png") && 
                     !contentType.startsWith("image/gif"))) {
                    flash.addFlashAttribute("errorMessage", "Formato de archivo no válido. Solo se aceptan JPG, PNG y GIF");
                    return "redirect:/productos/nuevo";
                }

                // Usar el nombre del archivo
                String nombreArchivo = archivo.getOriginalFilename();
                Path rutaArchivo = Paths.get(RUTA_IMAGENES).resolve(nombreArchivo);
                
                // Si el archivo ya existe, no hacer nada
                if (!Files.exists(rutaArchivo)) {
                    // Guardar la imagen
                    Files.createDirectories(rutaArchivo.getParent());
                    Files.copy(archivo.getInputStream(), rutaArchivo);
                }
                
                // Actualizar la ruta de la imagen en el producto
                productoGuardado.setImagen(nombreArchivo);
            }
            
            // Si es edición y no se subió nueva imagen, mantener la imagen existente
            if (producto.getId() != null && archivo == null || archivo.isEmpty()) {
                ProductoEntity productoExistente = productoService.findById(producto.getId());
                if (productoExistente != null && productoExistente.getImagen() != null) {
                    productoGuardado.setImagen(productoExistente.getImagen());
                }
            }

            // Establecer la relación con la categoría
            productoGuardado.setCategoria(producto.getCategoria());
            
            // Guardar el producto
            productoService.save(productoGuardado);
            flash.addFlashAttribute("successMessage", "Producto guardado exitosamente");
            return "redirect:/productos/lista";
        } catch (Exception e) {
            flash.addFlashAttribute("errorMessage", "Error al guardar el producto: " + e.getMessage());
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
