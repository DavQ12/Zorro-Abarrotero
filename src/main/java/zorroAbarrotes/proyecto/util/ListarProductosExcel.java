package zorroAbarrotes.proyecto.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractXlsxView;
import zorroAbarrotes.proyecto.model.entity.ProductoEntity;
import org.apache.poi.util.IOUtils;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Component("productosExcelView")
public class ListarProductosExcel extends AbstractXlsxView {

    @Override
    protected void buildExcelDocument(Map<String, Object> model, Workbook workbook,
                                      HttpServletRequest request, HttpServletResponse response) throws Exception {
        // Configuración del response
        response.setHeader("Content-Disposition", "attachment; filename=\"InventarioZorroAbarrotero.xlsx\"");

        // Crear hoja
        Sheet hoja = workbook.createSheet("Inventario");

        // Estilos
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 12);
        headerFont.setColor(IndexedColors.WHITE.getIndex());

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setBorderBottom(BorderStyle.THIN);

        Font titleFont = workbook.createFont();
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short) 14);

        CellStyle titleStyle = workbook.createCellStyle();
        titleStyle.setFont(titleFont);
        titleStyle.setAlignment(HorizontalAlignment.CENTER);

        CellStyle dataStyle = workbook.createCellStyle();
        dataStyle.setBorderBottom(BorderStyle.THIN);
        dataStyle.setBorderTop(BorderStyle.THIN);
        dataStyle.setBorderLeft(BorderStyle.THIN);
        dataStyle.setBorderRight(BorderStyle.THIN);

        List<String> columnas = List.of("Nombre", "Categoria", "Precio", "Marca", "Stock Mínimo", "Stock Actual");

//        //Configurar imagenes
//        final int IMG_COLUMN = 6; // Columna G (0-based index)
//        final double IMG_DESIRED_WIDTH_CM = 4.0; // Ancho deseado en centímetros
//        final double IMG_DESIRED_HEIGHT_CM = 3.0;
//        final String RUTA_IMAGENES = "/home/angelquintero/ImagenesZorro/";
//
//
//        final int IMG_WIDTH_IN_UNITS = (int)(IMG_DESIRED_WIDTH_CM * 37.795 * 0.8); // Factor de ajuste
//        final int IMG_HEIGHT_IN_UNITS = (int)(IMG_DESIRED_HEIGHT_CM * 37.795 * 0.8);
//
//        // Configurar ancho de columna para imágenes (en unidades de 1/256 de carácter)
//        hoja.setColumnWidth(IMG_COLUMN, IMG_WIDTH_IN_UNITS);

        // Título
        Row titulo = hoja.createRow(0);
        titulo.setHeightInPoints(25);
        Cell celda = titulo.createCell(0);
        celda.setCellValue("INVENTARIO SUCURSAL FES ARAGON");
        celda.setCellStyle(titleStyle);
        hoja.addMergedRegion(new CellRangeAddress(0, 0, 0, 5));

        // Encabezados de columnas
        Row filasDatos = hoja.createRow(2);
        filasDatos.setHeightInPoints(20);

        for (int i = 0; i < columnas.size(); i++) {
            celda = filasDatos.createCell(i);
            celda.setCellValue(columnas.get(i));
            celda.setCellStyle(headerStyle);
            hoja.setColumnWidth(i, 20 * 256); // Ajustar ancho de columnas
        }

        // Datos de productos
        List<ProductoEntity> productos = (List<ProductoEntity>) model.get("productos");
        int numFila = 3;

        // Estilo para precios (formato numérico)
        CellStyle priceStyle = workbook.createCellStyle();
        priceStyle.cloneStyleFrom(dataStyle);
        priceStyle.setDataFormat(workbook.createDataFormat().getFormat("$#,##0.00"));

        for (ProductoEntity producto : productos) {
            filasDatos = hoja.createRow(numFila);
//            filasDatos.setHeightInPoints((float)(IMG_DESIRED_HEIGHT_CM * 28.346));
            filasDatos.setHeightInPoints(18);

            // Nombre
            celda = filasDatos.createCell(0);
            celda.setCellValue(producto.getNombre());
            celda.setCellStyle(dataStyle);

            // Categoría
            celda = filasDatos.createCell(1);
            celda.setCellValue(producto.getCategoria().getDescripcion());
            celda.setCellStyle(dataStyle);

            // Precio
            celda = filasDatos.createCell(2);
            celda.setCellValue(producto.getPrecio());
            celda.setCellStyle(priceStyle);

            // Marca
            celda = filasDatos.createCell(3);
            celda.setCellValue(producto.getMarca());
            celda.setCellStyle(dataStyle);

            // Stock Mínimo
            celda = filasDatos.createCell(4);
            celda.setCellValue(producto.getStockMinimo());
            celda.setCellStyle(dataStyle);

            // Stock Actual
            celda = filasDatos.createCell(5);
            celda.setCellValue(producto.getStockActual());
            celda.setCellStyle(dataStyle);

//            Cell celdaImagen = filasDatos.createCell(IMG_COLUMN);
//            celdaImagen.setCellValue("");
//
//            try {
//                String nombreImagen = producto.getImagen();
//                if (nombreImagen != null && !nombreImagen.isEmpty()) {
//                    Path imagePath = Paths.get(RUTA_IMAGENES + nombreImagen);
//
//                    if (Files.exists(imagePath)) {
//                        byte[] bytes = Files.readAllBytes(imagePath);
//                        int pictureIdx = workbook.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);
//
//                        CreationHelper helper = workbook.getCreationHelper();
//                        Drawing<?> drawing = hoja.createDrawingPatriarch();
//
//                        // Configurar anclaje con posición y tamaño exactos
//                        ClientAnchor anchor = helper.createClientAnchor();
//                        anchor.setCol1(IMG_COLUMN);
//                        anchor.setRow1(numFila);
//                        anchor.setCol2(IMG_COLUMN); // Misma columna para evitar expansión
//                        anchor.setRow2(numFila); // Misma fila para evitar expansión
//
//                        // Insertar la imagen
//                        Picture pict = drawing.createPicture(anchor, pictureIdx);
//
//                        // Ajuste de tamaño PRECISO (en lugar de resize)
//                        // 1. Obtener dimensiones originales de la imagen
//                        ImageInputStream in = ImageIO.createImageInputStream(new ByteArrayInputStream(bytes));
//                        Iterator<ImageReader> readers = ImageIO.getImageReaders(in);
//                        if (readers.hasNext()) {
//                            ImageReader reader = readers.next();
//                            reader.setInput(in);
//                            int width = reader.getWidth(0);
//                            int height = reader.getHeight(0);
//                            reader.dispose();
//
//                            // 2. Calcular relación de aspecto
//                            double aspectRatio = (double)width / height;
//
//                            // 3. Ajustar manteniendo relación de aspecto
//                            if (aspectRatio > 1) {
//                                // Imagen horizontal
//                                pict.resize(IMG_DESIRED_WIDTH_CM / 2.54); // 2.54 cm = 1 pulgada
//                            } else {
//                                // Imagen vertical
//                                pict.resize(IMG_DESIRED_HEIGHT_CM / 2.54 * aspectRatio);
//                            }
//                        }
//                        in.close();
//                    }
//                }
//            } catch (Exception e) {
//                filasDatos.createCell(IMG_COLUMN).setCellValue("Error en imagen");
//                e.printStackTrace();
//            }

            numFila++;
        }

        // Autoajustar columnas al contenido
        for (int i = 0; i < columnas.size()-1; i++) {
            hoja.autoSizeColumn(i);
        }
    }
}

