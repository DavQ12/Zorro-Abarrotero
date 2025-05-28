package zorroAbarrotes.proyecto.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractXlsxView;
import zorroAbarrotes.proyecto.model.entity.ProductoEntity;
import org.apache.poi.util.IOUtils;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Component("login/admin")
public class ListarProductosExcel extends AbstractXlsxView {

    private final String RUTA_IMAGENES = "/home/angelquintero/ImagenesZorro/";
    @Override
    protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setHeader("Content-Disposition", "attachment; filename=\"InventarioZorroAbarrotero.xlsx\"");
        Sheet hoja = workbook.createSheet("Hoja Inventario");

        Row titulo = hoja.createRow(0);
        Cell celda = titulo.createCell(0);
        celda.setCellValue("INVENTARIO SUCURSAL FES ARAGON");

        Row filasDatos = hoja.createRow(2);
        List<String> columnas = List.of("Nombre", "Categoria","Precio","Marca","Stock Minimo", "Stock Actual");
        for (int i = 0; i < columnas.size(); i++) {
            celda = filasDatos.createCell(i);
            celda.setCellValue(columnas.get(i));
        }

        List<ProductoEntity> productos = (List<ProductoEntity>) model.get("productos");
        int numFila=3;
        for (ProductoEntity producto : productos) {
            filasDatos = hoja.createRow(numFila);
            filasDatos.createCell(0).setCellValue(producto.getNombre());
            filasDatos.createCell(1).setCellValue(producto.getCategoria().getDescripcion());
            filasDatos.createCell(2).setCellValue(producto.getPrecio());
            filasDatos.createCell(3).setCellValue(producto.getMarca());
            filasDatos.createCell(4).setCellValue(producto.getStockMinimo());
            filasDatos.createCell(5).setCellValue(producto.getStockActual());
            //motrar imagenes
            //a mejorar: tamaño de imageb
//            try {
//                InputStream is = new FileInputStream(RUTA_IMAGENES + producto.getImagen());
//                String nombreArchivo = producto.getImagen().toLowerCase();
//                int tipoImagen = 0;
//
//                if (nombreArchivo.endsWith(".png")) {
//                    tipoImagen = Workbook.PICTURE_TYPE_PNG;
//                } else if (nombreArchivo.endsWith(".jpg") || nombreArchivo.endsWith(".jpeg")) {
//                    tipoImagen = Workbook.PICTURE_TYPE_JPEG;
//                }
//                byte[] bytes = IOUtils.toByteArray(is);
//                int pictureIdx = workbook.addPicture(bytes, tipoImagen); // o PNG
//                is.close();
//
//                CreationHelper helper = workbook.getCreationHelper();
//                Drawing<?> drawing = hoja.createDrawingPatriarch();
//                ClientAnchor anchor = helper.createClientAnchor();
//
//                anchor.setCol1(6); // columna G
//                anchor.setRow1(numFila); // fila actual
//                anchor.setCol2(7);
//                anchor.setRow2(numFila + 1);
//
//                Picture pict = drawing.createPicture(anchor, pictureIdx);
//                pict.resize(); // tamaño (1.0 = 100%)
//                hoja.setColumnWidth(6, 20 * 256);   // Ajusta el ancho de la columna
//                filasDatos.setHeightInPoints(60);
//            } catch (Exception e) {
//                filasDatos.createCell(6).setCellValue("Imagen no disponible");
//                e.printStackTrace();
//            }
            numFila++;
        }
    }
}

