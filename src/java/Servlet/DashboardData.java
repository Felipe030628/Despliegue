package Servlet;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import Controlador.MovimientoStockDAO;
import Controlador.PedidoDAO;
import Controlador.ProductosDAO;

/**
 * Endpoint de datos para el panel de control principal (Dashboard).
 * Proporciona indicadores clave (KPIs), métricas semanales, distribución por categorías 
 * y ranking de productos en formato JSON.
 */
@WebServlet(name = "DashboardData", urlPatterns = {"/DashboardData"})
public class DashboardData extends HttpServlet {

    private static final int UMBRAL_STOCK_CRITICO = 5;
    private static final int TOP_PRODUCTOS_LIMITE = 5;
    private static final ZoneId ZONA_NEGOCIO = ZoneId.of("America/Bogota");

    private final PedidoDAO pedidoDAO = new PedidoDAO();
    private final ProductosDAO productosDAO = new ProductosDAO();
    private final MovimientoStockDAO movimientoStockDAO = new MovimientoStockDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");

        try {
            double totalCaja = pedidoDAO.obtenerTotalCajaHoy();
            int variedad = productosDAO.contarTotalMarcas();
            int stockCritico = movimientoStockDAO.contarProductosCriticos(UMBRAL_STOCK_CRITICO);

            String jsonVentasSemana = construirJsonVentasSemana();
            String jsonCategorias = construirJsonCategorias();
            String jsonTopProductos = construirJsonTopProductos();

            String jsonFinal = String.format(Locale.US,
                "{"
                    + "\"caja\":%.2f,"
                    + "\"critico\":%d,"
                    + "\"variedad\":%d,"
                    + "\"ventasSemana\":%s,"
                    + "\"categorias\":%s,"
                    + "\"topProductos\":[%s]"
                + "}",
                totalCaja, stockCritico, variedad,
                jsonVentasSemana,
                jsonCategorias,
                jsonTopProductos
            );

            response.getWriter().print(jsonFinal);

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().print("{\"error\":\"Error al procesar datos\"}");
        }
    }

    private String construirJsonVentasSemana() {
        Map<String, Double> ventasPorDia = pedidoDAO.obtenerVentasUltimos7Dias();
        LocalDate hoy = LocalDate.now(ZONA_NEGOCIO);

        List<String> labels = new ArrayList<>();
        List<String> data = new ArrayList<>();

        for (int i = 6; i >= 0; i--) {
            LocalDate dia = hoy.minusDays(i);
            String clave = dia.toString();
            double total = ventasPorDia.getOrDefault(clave, 0.0);
            String etiqueta = capitalizar(dia.getDayOfWeek().getDisplayName(TextStyle.SHORT, new Locale("es", "ES")));

            labels.add("\"" + escaparJson(etiqueta) + "\"");
            data.add(String.format(Locale.US, "%.2f", total));
        }

        return String.format("{\"labels\":[%s],\"data\":[%s]}",
                String.join(",", labels),
                String.join(",", data)
        );
    }

    private String construirJsonCategorias() {
        List<Map<String, Object>> categorias = productosDAO.obtenerDistribucionCategorias();

        List<String> labels = new ArrayList<>();
        List<String> data = new ArrayList<>();

        for (Map<String, Object> cat : categorias) {
            String nombreCat = String.valueOf(cat.get("categoria"));
            labels.add("\"" + escaparJson(nombreCat) + "\"");
            data.add(String.valueOf(cat.get("total")));
        }

        return String.format("{\"labels\":[%s],\"data\":[%s]}",
                String.join(",", labels),
                String.join(",", data)
        );
    }

    private String construirJsonTopProductos() {
        List<Map<String, Object>> topProductos = movimientoStockDAO.obtenerTopProductos(TOP_PRODUCTOS_LIMITE);

        return topProductos.stream().map(prod -> {
            String nombre = String.valueOf(prod.get("nombre"));
            int cantidad = ((Number) prod.get("cantidad")).intValue();
            double monto = ((Number) prod.get("monto")).doubleValue();

            return String.format(Locale.US,
                "{\"nombre\":\"%s\",\"cantidad\":%d,\"monto\":%.2f}",
                escaparJson(nombre), cantidad, monto
            );
        }).collect(Collectors.joining(","));
    }

    private String capitalizar(String texto) {
        if (texto == null || texto.isEmpty()) {
            return texto;
        }
        return Character.toUpperCase(texto.charAt(0)) + texto.substring(1);
    }

    private String escaparJson(String texto) {
        if (texto == null) {
            return "";
        }
        return texto.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\b", "\\b")
                    .replace("\f", "\\f")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t");
    }
}