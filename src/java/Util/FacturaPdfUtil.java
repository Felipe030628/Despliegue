package Util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import Modelo.DetallePedido;
import Modelo.Pedidos;

/**
 * Utilidad técnica de bajo nivel para la generación binaria de facturas en formato PDF 1.4.
 * Genera el documento sin dependencias externas (iText, OpenPDF, etc.), asegurando
 * alta eficiencia y compatibilidad nativa con codificación WinAnsiEncoding.
 */
public final class FacturaPdfUtil {

    private static final String CODIFICACION = "ISO-8859-1";

    private static final float MARGEN_IZQUIERDO = 50f;
    private static final float ANCHO_PAGINA = 595f;  // Formato A4 en puntos (72 dpi)
    private static final float ALTO_PAGINA = 842f;

    private static final float COLUMNA_PRODUCTO = 50f;
    private static final float COLUMNA_PRECIO = 300f;
    private static final float COLUMNA_CANTIDAD = 390f;
    private static final float COLUMNA_SUBTOTAL = 460f;

    private FacturaPdfUtil() {
        // Constructor privado para evitar la instanciación de clase utilitaria
    }

    /**
     * Genera la representación en arreglo de bytes de una factura PDF a partir de un pedido y sus detalles.
     *
     * @param pedido   Objeto Pedidos con la información de cabecera.
     * @param detalles Lista de DetallePedido con las líneas del pedido.
     * @return Arreglo de bytes del archivo PDF generado.
     * @throws IOException Si ocurre un error al procesar el flujo de datos en memoria.
     */
    public static byte[] generarFactura(Pedidos pedido, List<DetallePedido> detalles) throws IOException {
        StringBuilder stream = new StringBuilder();
        stream.append("BT\n");

        float coordenadaY = 790f;

        coordenadaY = construirEncabezado(stream, pedido, coordenadaY);
        coordenadaY = construirTablaTransacciones(stream, detalles, coordenadaY, pedido);
        construirPiePagina(stream, coordenadaY);

        stream.append("ET\n");

        return ensamblarEstructuraPdf(stream.toString());
    }

    private static float construirEncabezado(StringBuilder stream, Pedidos pedido, float y) {
        y = escribirTexto(stream, MARGEN_IZQUIERDO, y, "F2", 20, "BarStock");
        y = escribirTexto(stream, MARGEN_IZQUIERDO, y - 4, "F1", 10, "Factura de Pedido");
        y -= 10;

        y = escribirTexto(stream, MARGEN_IZQUIERDO, y, "F1", 11, "Pedido N°: " + pedido.getIdPedido());
        y = escribirTexto(stream, MARGEN_IZQUIERDO, y, "F1", 11, "Cliente: " + sanitizarCadena(pedido.getCliente()));
        y = escribirTexto(stream, MARGEN_IZQUIERDO, y, "F1", 11, "Mesa: " + sanitizarCadena(pedido.getMesa()));
        y = escribirTexto(stream, MARGEN_IZQUIERDO, y, "F1", 11, "Fecha: " + sanitizarCadena(pedido.getFecha()));
        y = escribirTexto(stream, MARGEN_IZQUIERDO, y, "F1", 11, "Estado: " + sanitizarCadena(pedido.getEstado()));
        y -= 14;

        dibujarLineaHorizontal(stream, MARGEN_IZQUIERDO, y, ANCHO_PAGINA - MARGEN_IZQUIERDO, y);
        y -= 16;

        escribirTexto(stream, COLUMNA_PRODUCTO, y, "F2", 10, "Producto");
        escribirTexto(stream, COLUMNA_PRECIO, y, "F2", 10, "Precio Unit.");
        escribirTexto(stream, COLUMNA_CANTIDAD, y, "F2", 10, "Cant.");
        y = escribirTexto(stream, COLUMNA_SUBTOTAL, y, "F2", 10, "Subtotal");
        y -= 4;

        dibujarLineaHorizontal(stream, MARGEN_IZQUIERDO, y, ANCHO_PAGINA - MARGEN_IZQUIERDO, y);
        return y - 16;
    }

    private static float construirTablaTransacciones(StringBuilder stream, List<DetallePedido> detalles, float y, Pedidos pedido) {
        double totalCalculado = 0;

        if (detalles != null) {
            for (DetallePedido detalle : detalles) {
                if (y < 90) {
                    escribirTexto(stream, MARGEN_IZQUIERDO, y, "F1", 9, "(continúa...)");
                    break;
                }
                escribirTexto(stream, COLUMNA_PRODUCTO, y, "F1", 10, sanitizarCadena(detalle.getNombreProducto()));
                escribirTexto(stream, COLUMNA_PRECIO, y, "F1", 10, formatearMoneda(detalle.getPrecioUnitario()));
                escribirTexto(stream, COLUMNA_CANTIDAD, y, "F1", 10, String.valueOf(detalle.getCantidad()));
                escribirTexto(stream, COLUMNA_SUBTOTAL, y, "F1", 10, formatearMoneda(detalle.getSubtotal()));

                totalCalculado += detalle.getSubtotal();
                y -= 16;
            }
        }

        y -= 4;
        dibujarLineaHorizontal(stream, MARGEN_IZQUIERDO, y, ANCHO_PAGINA - MARGEN_IZQUIERDO, y);
        y -= 20;

        double totalFinal = (pedido != null && pedido.getTotal() > 0) ? pedido.getTotal() : totalCalculado;
        return escribirTexto(stream, COLUMNA_CANTIDAD, y, "F2", 12, "TOTAL: " + formatearMoneda(totalFinal));
    }

    private static void construirPiePagina(StringBuilder stream, float y) {
        y -= 30;
        escribirTexto(stream, MARGEN_IZQUIERDO, y, "F1", 9, "Gracias por su compra - BarStock");
    }

    private static float escribirTexto(StringBuilder stream, float x, float y, String fuente, int tamano, String texto) {
        stream.append("/").append(fuente).append(" ").append(tamano).append(" Tf\n");
        stream.append(String.format(Locale.US, "1 0 0 1 %.2f %.2f Tm\n", x, y));
        stream.append("(").append(escaparSintaxisPdf(texto)).append(") Tj\n");
        return y - (tamano + 6);
    }

    private static void dibujarLineaHorizontal(StringBuilder stream, float x1, float y1, float x2, float y2) {
        stream.append("0.5 w\n");
        stream.append(String.format(Locale.US, "%.2f %.2f m\n", x1, y1));
        stream.append(String.format(Locale.US, "%.2f %.2f l\n", x2, y2));
        stream.append("S\n");
    }

    private static String sanitizarCadena(String texto) {
        return texto == null ? "" : texto;
    }

    private static String formatearMoneda(double valor) {
        return String.format(Locale.US, "$%.2f", valor);
    }

    private static String escaparSintaxisPdf(String texto) {
        if (texto == null) {
            return "";
        }
        return texto.replace("\\", "\\\\").replace("(", "\\(").replace(")", "\\)");
    }

    private static byte[] ensamblarEstructuraPdf(String contenidoStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        List<Integer> offsets = new ArrayList<>();

        escribirBytesAscii(buffer, "%PDF-1.4\n%\u00E2\u00E3\u00CF\u00D3\n");

        byte[] streamBytes = contenidoStream.getBytes(CODIFICACION);

        // Objeto 1: Catálogo de la estructura del PDF
        offsets.add(buffer.size());
        escribirBytesAscii(buffer, "1 0 obj\n<< /Type /Catalog /Pages 2 0 R >>\nendobj\n");

        // Objeto 2: Árbol de Páginas
        offsets.add(buffer.size());
        escribirBytesAscii(buffer, "2 0 obj\n<< /Type /Pages /Kids [3 0 R] /Count 1 >>\nendobj\n");

        // Objeto 3: Definición de la Página actual y Recursos
        offsets.add(buffer.size());
        escribirBytesAscii(buffer, "3 0 obj\n<< /Type /Page /Parent 2 0 R /MediaBox [0 0 " + (int) ANCHO_PAGINA
                + " " + (int) ALTO_PAGINA + "] /Resources << /Font << /F1 5 0 R /F2 6 0 R >> >> "
                + "/Contents 4 0 R >>\nendobj\n");

        // Objeto 4: Flujo de Contenido (Stream de instrucciones gráficas)
        offsets.add(buffer.size());
        escribirBytesAscii(buffer, "4 0 obj\n<< /Length " + streamBytes.length + " >>\nstream\n");
        buffer.write(streamBytes);
        escribirBytesAscii(buffer, "\nendstream\nendobj\n");

        // Objeto 5: Fuente Estándar Helvetica (F1)
        offsets.add(buffer.size());
        escribirBytesAscii(buffer, "5 0 obj\n<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica /Encoding /WinAnsiEncoding >>\nendobj\n");

        // Objeto 6: Fuente Estándar Helvetica Negrita (F2)
        offsets.add(buffer.size());
        escribirBytesAscii(buffer, "6 0 obj\n<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica-Bold /Encoding /WinAnsiEncoding >>\nendobj\n");

        int xrefOffset = buffer.size();
        int totalObjetos = offsets.size() + 1;
        StringBuilder xref = new StringBuilder();
        xref.append("xref\n0 ").append(totalObjetos).append("\n");
        xref.append("0000000000 65535 f \n");
        for (Integer offset : offsets) {
            xref.append(String.format(Locale.US, "%010d 00000 n \n", offset));
        }
        escribirBytesAscii(buffer, xref.toString());

        escribirBytesAscii(buffer, "trailer\n<< /Size " + totalObjetos + " /Root 1 0 R >>\nstartxref\n" + xrefOffset + "\n%%EOF");

        return buffer.toByteArray();
    }

    private static void escribirBytesAscii(ByteArrayOutputStream buffer, String texto) throws IOException {
        buffer.write(texto.getBytes(CODIFICACION));
    }
}