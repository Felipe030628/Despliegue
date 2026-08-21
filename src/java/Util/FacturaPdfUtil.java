package Util;

import Modelo.DetallePedido;
import Modelo.Pedidos;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Genera facturas en PDF construyendo a mano la estructura binaria del
 * documento (PDF 1.4), sin depender de ninguna librería externa (iText,
 * OpenPDF, etc.). Así el proyecto no necesita agregar ningún .jar nuevo
 * para poder generar la factura de un pedido.
 */
public class FacturaPdfUtil {

    // WinAnsiEncoding cubre tildes y la ñ usando ISO-8859-1
    private static final String ENC = "ISO-8859-1";

    private static final float MARGEN_IZQ = 50f;
    private static final float ANCHO_PAGINA = 595f;  // A4 en puntos
    private static final float ALTO_PAGINA = 842f;

    private static final float COL_PRODUCTO = 50f;
    private static final float COL_PRECIO = 300f;
    private static final float COL_CANT = 390f;
    private static final float COL_SUBTOTAL = 460f;

    public static byte[] generarFactura(Pedidos pedido, List<DetallePedido> detalles) throws IOException {
        StringBuilder cs = new StringBuilder();
        cs.append("BT\n");

        float y = 790f;

        y = escribir(cs, MARGEN_IZQ, y, "F2", 20, "BarStock");
        y = escribir(cs, MARGEN_IZQ, y - 4, "F1", 10, "Factura de Pedido");
        y -= 10;

        y = escribir(cs, MARGEN_IZQ, y, "F1", 11, "Pedido N°: " + pedido.getIdPedido());
        y = escribir(cs, MARGEN_IZQ, y, "F1", 11, "Cliente: " + safe(pedido.getCliente()));
        y = escribir(cs, MARGEN_IZQ, y, "F1", 11, "Mesa: " + safe(pedido.getMesa()));
        y = escribir(cs, MARGEN_IZQ, y, "F1", 11, "Fecha: " + safe(pedido.getFecha()));
        y = escribir(cs, MARGEN_IZQ, y, "F1", 11, "Estado: " + safe(pedido.getEstado()));
        y -= 14;

        linea(cs, MARGEN_IZQ, y, ANCHO_PAGINA - MARGEN_IZQ, y);
        y -= 16;

        escribir(cs, COL_PRODUCTO, y, "F2", 10, "Producto");
        escribir(cs, COL_PRECIO, y, "F2", 10, "Precio Unit.");
        escribir(cs, COL_CANT, y, "F2", 10, "Cant.");
        y = escribir(cs, COL_SUBTOTAL, y, "F2", 10, "Subtotal");
        y -= 4;

        linea(cs, MARGEN_IZQ, y, ANCHO_PAGINA - MARGEN_IZQ, y);
        y -= 16;

        double totalCalculado = 0;
        if (detalles != null) {
            for (DetallePedido d : detalles) {
                if (y < 90) {
                    // Salvaguarda simple: evita salirse de la página en pedidos enormes.
                    escribir(cs, MARGEN_IZQ, y, "F1", 9, "(continúa...)");
                    break;
                }
                escribir(cs, COL_PRODUCTO, y, "F1", 10, safe(d.getNombreProducto()));
                escribir(cs, COL_PRECIO, y, "F1", 10, formatoMoneda(d.getPrecioUnitario()));
                escribir(cs, COL_CANT, y, "F1", 10, String.valueOf(d.getCantidad()));
                escribir(cs, COL_SUBTOTAL, y, "F1", 10, formatoMoneda(d.getSubtotal()));
                totalCalculado += d.getSubtotal();
                y -= 16;
            }
        }

        y -= 4;
        linea(cs, MARGEN_IZQ, y, ANCHO_PAGINA - MARGEN_IZQ, y);
        y -= 20;

        double totalMostrar = pedido.getTotal() > 0 ? pedido.getTotal() : totalCalculado;
        y = escribir(cs, COL_CANT, y, "F2", 12, "TOTAL: " + formatoMoneda(totalMostrar));

        y -= 30;
        escribir(cs, MARGEN_IZQ, y, "F1", 9, "Gracias por su compra - BarStock");

        cs.append("ET\n");

        return construirPdf(cs.toString());
    }

    private static String safe(String s) {
        return s == null ? "" : s;
    }

    private static String formatoMoneda(double valor) {
        return String.format(Locale.US, "$%.2f", valor);
    }

    // Escribe una línea de texto y devuelve la coordenada Y para la siguiente línea.
    private static float escribir(StringBuilder cs, float x, float y, String fuente, int tamano, String texto) {
        cs.append("/").append(fuente).append(" ").append(tamano).append(" Tf\n");
        cs.append(String.format(Locale.US, "1 0 0 1 %.2f %.2f Tm\n", x, y));
        cs.append("(").append(escapar(texto)).append(") Tj\n");
        return y - (tamano + 6);
    }

    private static void linea(StringBuilder cs, float x1, float y1, float x2, float y2) {
        cs.append("0.5 w\n");
        cs.append(String.format(Locale.US, "%.2f %.2f m\n", x1, y1));
        cs.append(String.format(Locale.US, "%.2f %.2f l\n", x2, y2));
        cs.append("S\n");
    }

    private static String escapar(String texto) {
        if (texto == null) return "";
        return texto.replace("\\", "\\\\").replace("(", "\\(").replace(")", "\\)");
    }

    // Arma el archivo PDF completo (objetos, tabla xref y trailer) a partir
    // del stream de contenido ya generado.
    private static byte[] construirPdf(String contenidoStream) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        List<Integer> offsets = new ArrayList<>();

        escribirAscii(out, "%PDF-1.4\n%\u00E2\u00E3\u00CF\u00D3\n");

        byte[] streamBytes = contenidoStream.getBytes(ENC);

        // 1: Catálogo
        offsets.add(out.size());
        escribirAscii(out, "1 0 obj\n<< /Type /Catalog /Pages 2 0 R >>\nendobj\n");

        // 2: Páginas
        offsets.add(out.size());
        escribirAscii(out, "2 0 obj\n<< /Type /Pages /Kids [3 0 R] /Count 1 >>\nendobj\n");

        // 3: Página
        offsets.add(out.size());
        escribirAscii(out, "3 0 obj\n<< /Type /Page /Parent 2 0 R /MediaBox [0 0 " + (int) ANCHO_PAGINA
                + " " + (int) ALTO_PAGINA + "] /Resources << /Font << /F1 5 0 R /F2 6 0 R >> >> "
                + "/Contents 4 0 R >>\nendobj\n");

        // 4: Contenido
        offsets.add(out.size());
        escribirAscii(out, "4 0 obj\n<< /Length " + streamBytes.length + " >>\nstream\n");
        out.write(streamBytes);
        escribirAscii(out, "\nendstream\nendobj\n");

        // 5: Fuente Helvetica
        offsets.add(out.size());
        escribirAscii(out, "5 0 obj\n<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica /Encoding /WinAnsiEncoding >>\nendobj\n");

        // 6: Fuente Helvetica-Bold
        offsets.add(out.size());
        escribirAscii(out, "6 0 obj\n<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica-Bold /Encoding /WinAnsiEncoding >>\nendobj\n");

        int xrefOffset = out.size();
        int totalObjs = offsets.size() + 1;
        StringBuilder xref = new StringBuilder();
        xref.append("xref\n0 ").append(totalObjs).append("\n");
        xref.append("0000000000 65535 f \n");
        for (Integer off : offsets) {
            xref.append(String.format(Locale.US, "%010d 00000 n \n", off));
        }
        escribirAscii(out, xref.toString());

        escribirAscii(out, "trailer\n<< /Size " + totalObjs + " /Root 1 0 R >>\nstartxref\n" + xrefOffset + "\n%%EOF");

        return out.toByteArray();
    }

    private static void escribirAscii(ByteArrayOutputStream out, String texto) throws UnsupportedEncodingException, IOException {
        out.write(texto.getBytes(ENC));
    }
}
