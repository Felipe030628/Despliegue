package Servlet;

import Controlador.MovimientoStockDAO;
import Controlador.PedidoDAO;
import Controlador.ProductosDAO;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Endpoint que alimenta el Panel/Dashboard en tiempo real (polling desde Panel.js).
 * Devuelve TODO lo que el panel necesita en una sola llamada:
 *  - KPIs (caja del día, stock crítico, variedad de marcas)
 *  - Ventas de los últimos 7 días (gráfica de líneas)
 *  - Distribución real de productos por categoría (dona + barras de progreso)
 *  - Top de productos más solicitados según movimientos de stock (salidas)
 */
@WebServlet(name = "DashboardData", urlPatterns = {"/DashboardData"})
public class DashboardData extends HttpServlet {

    // Umbral (unidades) a partir del cual un producto se considera "stock crítico".
    private static final int UMBRAL_STOCK_CRITICO = 5;
    // Cuántos productos mostrar en el ranking de "Más Solicitados".
    private static final int TOP_PRODUCTOS_LIMITE = 5;

    private final PedidoDAO pedidoDAO = new PedidoDAO();
    private final ProductosDAO productosDAO = new ProductosDAO();
    private final MovimientoStockDAO movimientoStockDAO = new MovimientoStockDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");

        try {
            // ---- 1. KPIs ----
            double totalCaja = pedidoDAO.obtenerTotalCajaHoy();
            int variedad = productosDAO.contarTotalMarcas();
            int stockCritico = movimientoStockDAO.contarProductosCriticos(UMBRAL_STOCK_CRITICO);

            // ---- 2. Ventas de los últimos 7 días (rellenando los días sin ventas con 0) ----
            Map<String, Double> ventasPorDia = pedidoDAO.obtenerVentasUltimos7Dias();
            StringBuilder labelsVentas = new StringBuilder();
            StringBuilder dataVentas = new StringBuilder();
            LocalDate hoy = LocalDate.now();
            for (int i = 6; i >= 0; i--) {
                LocalDate dia = hoy.minusDays(i);
                String clave = dia.toString(); // yyyy-MM-dd
                double total = ventasPorDia.getOrDefault(clave, 0.0);
                String etiqueta = capitalizar(dia.getDayOfWeek().getDisplayName(TextStyle.SHORT, new Locale("es", "ES")));

                if (labelsVentas.length() > 0) {
                    labelsVentas.append(",");
                    dataVentas.append(",");
                }
                labelsVentas.append("\"").append(escaparJson(etiqueta)).append("\"");
                dataVentas.append(String.format(Locale.US, "%.2f", total));
            }

            // ---- 3. Distribución real de productos por categoría ----
            List<Map<String, Object>> categorias = productosDAO.obtenerDistribucionCategorias();
            StringBuilder labelsCategorias = new StringBuilder();
            StringBuilder dataCategorias = new StringBuilder();
            for (Map<String, Object> cat : categorias) {
                if (labelsCategorias.length() > 0) {
                    labelsCategorias.append(",");
                    dataCategorias.append(",");
                }
                String nombreCat = String.valueOf(cat.get("categoria"));
                labelsCategorias.append("\"").append(escaparJson(nombreCat)).append("\"");
                dataCategorias.append(cat.get("total"));
            }

            // ---- 4. Top de productos más solicitados (según salidas de stock) ----
            List<Map<String, Object>> topProductos = movimientoStockDAO.obtenerTopProductos(TOP_PRODUCTOS_LIMITE);
            StringBuilder topJson = new StringBuilder();
            for (Map<String, Object> prod : topProductos) {
                if (topJson.length() > 0) topJson.append(",");
                topJson.append("{")
                       .append("\"nombre\":\"").append(escaparJson(String.valueOf(prod.get("nombre")))).append("\",")
                       .append("\"cantidad\":").append(prod.get("cantidad")).append(",")
                       .append("\"monto\":").append(String.format(Locale.US, "%.2f", (Double) prod.get("monto")))
                       .append("}");
            }

            // ---- 5. Ensamblar el JSON final ----
            String json = String.format(Locale.US,
                "{"
                    + "\"caja\": %.2f,"
                    + "\"critico\": %d,"
                    + "\"variedad\": %d,"
                    + "\"ventasSemana\": {\"labels\": [%s], \"data\": [%s]},"
                    + "\"categorias\": {\"labels\": [%s], \"data\": [%s]},"
                    + "\"topProductos\": [%s]"
                + "}",
                totalCaja, stockCritico, variedad,
                labelsVentas.toString(), dataVentas.toString(),
                labelsCategorias.toString(), dataCategorias.toString(),
                topJson.toString()
            );

            response.getWriter().print(json);

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().print("{\"error\": \"Error al procesar datos\"}");
        }
    }

    private String capitalizar(String texto) {
        if (texto == null || texto.isEmpty()) return texto;
        return Character.toUpperCase(texto.charAt(0)) + texto.substring(1);
    }

    private String escaparJson(String texto) {
        if (texto == null) return "";
        return texto.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
