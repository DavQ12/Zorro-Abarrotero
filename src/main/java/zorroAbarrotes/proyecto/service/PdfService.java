package zorroAbarrotes.proyecto.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import zorroAbarrotes.proyecto.model.entity.ProductoCarritoEntity;
import zorroAbarrotes.proyecto.model.entity.VentaEntity;
import zorroAbarrotes.proyecto.service.venta.VentaService;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class PdfService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private VentaService ventaService;

    private static final Font FONT_TITLE = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24, BaseColor.DARK_GRAY);
    private static final Font FONT_HEADER = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, new BaseColor(245, 124, 0)); // Naranja
    private static final Font FONT_NORMAL = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.DARK_GRAY);
    private static final Font FONT_BOLD = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.BLACK);
    private static final BaseColor COLOR_HEADER_BACKGROUND = new BaseColor(255, 224, 178); // Light orange
    private static final float DEFAULT_PADDING = 8f;
    private static final float HEADER_PADDING = 10f;
    //private final String RUTA_IMAGENES = "/home/fercw/ImagenesZorro/";
    private final String RUTA_IMAGENES = "/home/fercw/ImagenesZorro/";

    public ByteArrayOutputStream generarPdfVenta(VentaEntity venta) throws DocumentException {
        if (venta.getCarrito() == null ||
                venta.getCarrito().getProductosCarrito() == null ||
                venta.getCarrito().getProductosCarrito().isEmpty()) {
            throw new IllegalArgumentException("La venta no tiene productos asociados");
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, baos);

        // Margenes
        document.setMargins(40, 20, 30, 30);
        document.open();

        // Logo central - MEJORADO
        try {
            // Crear una tabla para el logo central
            PdfPTable logoTable = new PdfPTable(1);
            logoTable.setWidthPercentage(100);
            logoTable.getDefaultCell().setBorder(0);
            logoTable.getDefaultCell().setPadding(15);

            // Celda para el logo
            PdfPCell logoCell = new PdfPCell();
            logoCell.setBorder(0);
            logoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            logoCell.setPadding(2);
            logoCell.setPaddingBottom(2); // padding inferior del logo
            
            try {
                Image logo = Image.getInstance("src/main/resources/static/image/zorro1.png");
                // LOGO MÁS GRANDE
                logo.scaleToFit(300, 150);
                logo.setAlignment(Element.ALIGN_CENTER);
                logoCell.addElement(logo);
            } catch (Exception e) {
                Phrase logoText = new Phrase("Logo no disponible", FONT_NORMAL);
                logoText.getFont().setSize(16);
                logoCell.addElement(logoText);
            }

            logoTable.addCell(logoCell);
            document.add(logoTable);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Texto "Zorro Abarrotero" - PEGADO AL LOGO
        Paragraph zorroAbarrotero = new Paragraph("Zorro Abarrotero", 
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 48, new BaseColor(245, 124, 0))); // Aumentado de 40 a 48
        zorroAbarrotero.setAlignment(Element.ALIGN_CENTER);
        zorroAbarrotero.setSpacingBefore(-35); // Espacio negativo para pegarlo más al logo
        zorroAbarrotero.setSpacingAfter(60); // Menos espacio después
        
        document.add(zorroAbarrotero);

        // Título - CENTRADO
        Paragraph titulo = new Paragraph("Factura de Venta", FONT_TITLE);
        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingAfter(15);
        document.add(titulo);

        // INFORMACIÓN COMPACTA Y CENTRADA 
        PdfPTable infoCompactaTable = new PdfPTable(2);
        infoCompactaTable.setWidthPercentage(50); // Más angosto para mayor compactación
        infoCompactaTable.setHorizontalAlignment(Element.ALIGN_CENTER);
        infoCompactaTable.setSpacingAfter(5); // Menos espacio después
        
        // Configuración de bordes y padding más compacto
        float compactPadding = 4f; // Padding reducido
        
        // ID Venta
        PdfPCell labelCell = new PdfPCell(new Phrase("ID Venta:", FONT_BOLD));
        labelCell.setBorder(0);
        labelCell.setPadding(compactPadding);
        labelCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        infoCompactaTable.addCell(labelCell);
        
        PdfPCell valueCell = new PdfPCell(new Phrase(String.valueOf(venta.getId()), FONT_NORMAL));
        valueCell.setBorder(0);
        valueCell.setPadding(compactPadding);
        valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        infoCompactaTable.addCell(valueCell);
        
        // Fecha
        labelCell = new PdfPCell(new Phrase("Fecha:", FONT_BOLD));
        labelCell.setBorder(0);
        labelCell.setPadding(compactPadding);
        labelCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        infoCompactaTable.addCell(labelCell);
        
        valueCell = new PdfPCell(new Phrase(venta.getFechaVenta().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")), FONT_NORMAL));
        valueCell.setBorder(0);
        valueCell.setPadding(compactPadding);
        valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        infoCompactaTable.addCell(valueCell);

        // Información del cliente - INTEGRADA EN LA MISMA TABLA
        if (venta.getCliente() != null) {
            // Nombre
            labelCell = new PdfPCell(new Phrase("Nombre:", FONT_BOLD));
            labelCell.setBorder(0);
            labelCell.setPadding(compactPadding);
            labelCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            infoCompactaTable.addCell(labelCell);
            
            valueCell = new PdfPCell(new Phrase(venta.getCliente().getNombre() + " " +
                    venta.getCliente().getApellidoP() + " " +
                    venta.getCliente().getApellidoM(), FONT_NORMAL));
            valueCell.setBorder(0);
            valueCell.setPadding(compactPadding);
            valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            infoCompactaTable.addCell(valueCell);
            
            // Correo
            if (venta.getCliente().getCorreo() != null) {
                labelCell = new PdfPCell(new Phrase("Correo:", FONT_BOLD));
                labelCell.setBorder(0);
                labelCell.setPadding(compactPadding);
                labelCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                infoCompactaTable.addCell(labelCell);
                
                valueCell = new PdfPCell(new Phrase(venta.getCliente().getCorreo(), FONT_NORMAL));
                valueCell.setBorder(0);
                valueCell.setPadding(compactPadding);
                valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                infoCompactaTable.addCell(valueCell);
            }
            
            // Teléfono
            if (venta.getCliente().getTelefono() != null) {
                labelCell = new PdfPCell(new Phrase("Teléfono:", FONT_BOLD));
                labelCell.setBorder(0);
                labelCell.setPadding(compactPadding);
                labelCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                infoCompactaTable.addCell(labelCell);
                
                valueCell = new PdfPCell(new Phrase(venta.getCliente().getTelefono(), FONT_NORMAL));
                valueCell.setBorder(0);
                valueCell.setPadding(compactPadding);
                valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                infoCompactaTable.addCell(valueCell);
            }
        }
        
        document.add(infoCompactaTable);
        
        // Método de pago - MÁS COMPACTO Y CENTRADO
        Paragraph pagoParrafo = new Paragraph("Método de Pago: " + venta.getPago().getDescripcion(), FONT_BOLD);
        pagoParrafo.setAlignment(Element.ALIGN_CENTER);
        pagoParrafo.setSpacingAfter(10); // Reducido de 15 a 10
        pagoParrafo.setSpacingBefore(5); // Espacio antes reducido
        document.add(pagoParrafo);

        // Tabla de productos 
        Paragraph productosTitulo = new Paragraph("Detalles de la Compra", FONT_HEADER);
        productosTitulo.setAlignment(Element.ALIGN_CENTER);
        productosTitulo.setSpacingAfter(10);
        document.add(productosTitulo);
        
        PdfPTable productosTable = new PdfPTable(5);
        productosTable.setWidthPercentage(100);
        productosTable.setWidths(new float[]{15f, 35f, 15f, 17.5f, 17.5f}); // Mejor distribución de columnas
        
        // Encabezados de la tabla - CENTRADOS
        PdfPCell headerCell = new PdfPCell(new Phrase("Imagen", FONT_BOLD));
        headerCell.setBackgroundColor(COLOR_HEADER_BACKGROUND);
        headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        headerCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        headerCell.setPadding(HEADER_PADDING);
        headerCell.setBorderWidth(1f);
        productosTable.addCell(headerCell);

        headerCell = new PdfPCell(new Phrase("Producto", FONT_BOLD));
        headerCell.setBackgroundColor(COLOR_HEADER_BACKGROUND);
        headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        headerCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        headerCell.setPadding(HEADER_PADDING);
        headerCell.setBorderWidth(1f);
        productosTable.addCell(headerCell);

        headerCell = new PdfPCell(new Phrase("Cantidad", FONT_BOLD));
        headerCell.setBackgroundColor(COLOR_HEADER_BACKGROUND);
        headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        headerCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        headerCell.setPadding(HEADER_PADDING);
        headerCell.setBorderWidth(1f);
        productosTable.addCell(headerCell);

        headerCell = new PdfPCell(new Phrase("Precio Unitario", FONT_BOLD));
        headerCell.setBackgroundColor(COLOR_HEADER_BACKGROUND);
        headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        headerCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        headerCell.setPadding(HEADER_PADDING);
        headerCell.setBorderWidth(1f);
        productosTable.addCell(headerCell);

        headerCell = new PdfPCell(new Phrase("Total", FONT_BOLD));
        headerCell.setBackgroundColor(COLOR_HEADER_BACKGROUND);
        headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        headerCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        headerCell.setPadding(HEADER_PADDING);
        headerCell.setBorderWidth(1f);
        productosTable.addCell(headerCell);

        // Filas de productos - IMÁGENES MÁS GRANDES Y CENTRADAS
        for (ProductoCarritoEntity productoCarrito : venta.getCarrito().getProductosCarrito()) {
            // Celda de imagen - MÁS GRANDE Y CENTRADA
            PdfPCell imageCell = new PdfPCell();
            imageCell.setBorderWidth(1f);
            imageCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            imageCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            imageCell.setPadding(DEFAULT_PADDING);
            imageCell.setMinimumHeight(100f); // Altura mínima para las celdas
            
            try {
                if (productoCarrito.getProducto().getImagen() != null && !productoCarrito.getProducto().getImagen().isEmpty()) {
                    String imagePath = RUTA_IMAGENES + productoCarrito.getProducto().getImagen();
                    try {
                        // Intentar cargar la imagen directamente con el nombre completo
                        Image productImage = Image.getInstance(imagePath);
                        productImage.scaleToFit(70, 70);
                        productImage.setAlignment(Element.ALIGN_CENTER);
                        imageCell.addElement(productImage);
                    } catch (Exception e) {
                        // Si falla, intentar con algunas extensiones comunes
                        boolean imageFound = false;
                        String[] extensions = {".jpg", ".jpeg", ".png", ".webp"};
                        
                        for (String ext : extensions) {
                            try {
                                String fullImagePath = imagePath + ext;
                                Image productImage = Image.getInstance(fullImagePath);
                                productImage.scaleToFit(70, 70);
                                productImage.setAlignment(Element.ALIGN_CENTER);
                                imageCell.addElement(productImage);
                                imageFound = true;
                                break;
                            } catch (Exception ex) {
                                continue;
                            }
                        }
                        
                        if (!imageFound) {
                            Phrase noImage = new Phrase("No disponible", FONT_NORMAL);
                            imageCell.addElement(noImage);
                        }
                    }
                } else {
                    Phrase noImage = new Phrase("No disponible", FONT_NORMAL);
                    imageCell.addElement(noImage);
                }
            } catch (Exception e) {
                Phrase errorImage = new Phrase("No disponible", FONT_NORMAL);
                imageCell.addElement(errorImage);
            }
            productosTable.addCell(imageCell);

            // Nombre del producto - CENTRADO
            PdfPCell nameCell = new PdfPCell(new Phrase(productoCarrito.getProducto().getNombre(), FONT_NORMAL));
            nameCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            nameCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            nameCell.setPadding(DEFAULT_PADDING);
            nameCell.setBorderWidth(1f);
            nameCell.setMinimumHeight(60f);
            productosTable.addCell(nameCell);
            
            // Cantidad - CENTRADA
            PdfPCell quantityCell = new PdfPCell(new Phrase(String.valueOf(productoCarrito.getCantidad()), FONT_NORMAL));
            quantityCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            quantityCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            quantityCell.setPadding(DEFAULT_PADDING);
            quantityCell.setBorderWidth(1f);
            quantityCell.setMinimumHeight(60f);
            productosTable.addCell(quantityCell);
            
            // Precio unitario - CENTRADO
            PdfPCell priceCell = new PdfPCell(new Phrase("$" + String.format("%.2f", productoCarrito.getProducto().getPrecio()), FONT_NORMAL));
            priceCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            priceCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            priceCell.setPadding(DEFAULT_PADDING);
            priceCell.setBorderWidth(1f);
            priceCell.setMinimumHeight(60f);
            productosTable.addCell(priceCell);
            
            // Total - CENTRADO
            PdfPCell totalCell = new PdfPCell(new Phrase("$" + String.format("%.2f", productoCarrito.getTotal()), FONT_NORMAL));
            totalCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            totalCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            totalCell.setPadding(DEFAULT_PADDING);
            totalCell.setBorderWidth(1f);
            totalCell.setMinimumHeight(60f);
            productosTable.addCell(totalCell);
        }
        document.add(productosTable);
        document.add(new Paragraph(Chunk.NEWLINE));

        // Total final - MÁS PROMINENTE Y CENTRADO
        PdfPTable totalTable = new PdfPTable(1);
        totalTable.setWidthPercentage(40);
        totalTable.setHorizontalAlignment(Element.ALIGN_CENTER);
        
        PdfPCell totalCell = new PdfPCell(new Phrase("Total: $" + String.format("%.2f", venta.getCarrito().getTotal()), 
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.BLACK)));
        totalCell.setBorder(1);
        totalCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        totalCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        totalCell.setPadding(15);
        totalCell.setBackgroundColor(new BaseColor(240, 240, 240));
        totalTable.addCell(totalCell);
        document.add(totalTable);

        // Mensaje de agradecimiento - CENTRADO CON MÁS ESTILO
        Paragraph gracias = new Paragraph("\n¡Gracias por su compra!", 
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, new BaseColor(245, 124, 0)));
        gracias.setAlignment(Element.ALIGN_CENTER);
        gracias.setSpacingBefore(20);
        document.add(gracias);

        document.close();
        return baos;
    }

    public void enviarPdfPorCorreo(VentaEntity venta) {
        try {
            if (venta.getCliente() != null && venta.getCliente().getCorreo() != null && !venta.getCliente().getCorreo().isEmpty()) {
                Optional<VentaEntity> ventaOptional = ventaService.findByIdWithAllDetails(venta.getId());
                if (ventaOptional.isPresent()) {
                    VentaEntity ventaConDetalles = ventaOptional.get();

                    if (ventaConDetalles.getCarrito() == null ||
                            ventaConDetalles.getCarrito().getProductosCarrito() == null ||
                            ventaConDetalles.getCarrito().getProductosCarrito().isEmpty()) {
                        System.err.println("Error: No se pudieron cargar los productos del carrito para la venta ID: " + venta.getId());
                        return;
                    }

                    ByteArrayOutputStream pdfContent = generarPdfVenta(ventaConDetalles);

                    MimeMessage message = mailSender.createMimeMessage();
                    MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

                    helper.setTo(ventaConDetalles.getCliente().getCorreo());
                    helper.setSubject("Factura de su compra en Zorro Abarrotes");

                    String htmlContent = "<div style='font-family: Arial, sans-serif; color: #333;'>"
                            + "<h2 style='color: #f57c00; text-align: center;'>Zorro Abarrotes - Factura</h2>"
                            + "<p>Estimado/a <strong>" + ventaConDetalles.getCliente().getNombre() + "</strong>,</p>"
                            + "<p>Gracias por su compra. Adjuntamos su factura detallada.</p>"
                            + "<hr style='border: 1px solid #eee;'>";

                    htmlContent += "<p><strong>ID de Venta:</strong> " + ventaConDetalles.getId() + "</p>";
                    htmlContent += "<p><strong>Fecha:</strong> " + ventaConDetalles.getFechaVenta().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + "</p>";
                    htmlContent += "<p><strong>Total:</strong> <span style='font-weight: bold;'>$" + String.format("%.2f", ventaConDetalles.getCarrito().getTotal()) + "</span></p>";

                    htmlContent += "<hr style='border: 1px dashed #ccc;'>";
                    htmlContent += "<p>Esperamos verlo pronto nuevamente.</p>"
                            + "</div>";

                    helper.setText(htmlContent, true);
                    helper.addAttachment("factura_zorro_" + ventaConDetalles.getId() + ".pdf", new ByteArrayResource(pdfContent.toByteArray()));

                    mailSender.send(message);
                    System.out.println("Correo con factura PDF enviado a: " + ventaConDetalles.getCliente().getCorreo());

                } else {
                    System.err.println("No se encontró la venta con ID: " + venta.getId() + " para enviar el PDF.");
                }
            } else {
                // Si no hay correo del cliente, simplemente no enviamos el PDF
                System.out.println("No se enviará PDF ya que no hay correo del cliente");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error al enviar el correo con la factura PDF: " + e.getMessage());
        }
    }
}