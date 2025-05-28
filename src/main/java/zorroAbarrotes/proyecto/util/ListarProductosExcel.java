package zorroAbarrotes.proyecto.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractXlsxView;
import zorroAbarrotes.proyecto.model.entity.ProductoEntity;
import org.apache.poi.util.IOUtils;
import java.io.FileInputStream;
import java.io.InputStream;
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
        List<String> columnas = List.of("Nombre", "Categoria", "Precio", "Marca", "Stock Mínimo", "Stock Actual");

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

            numFila++;
        }

        // Autoajustar columnas al contenido
        for (int i = 0; i < columnas.size(); i++) {
            hoja.autoSizeColumn(i);
        }
    }
}

