package Servlet;

import Controlador.ProductosDAO;
import Controlador.UsuariosDAO;
import Modelo.Productos;
import Modelo.Usuarios;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Endpoint que alimenta la barra de búsqueda global del Panel (top-navbar).
 * Recibe ?termino=... y devuelve, en una sola llamada, coincidencias reales
 * de productos y de empleados directamente desde la base de datos.
 */
@WebServlet(name = "BuscarGlobal", urlPatterns = {"/BuscarGlobal"})
public class BuscarGlobal extends HttpServlet {

    // Máximo de resultados a devolver por categoría, para no saturar el desplegable.
    private static final int LIMITE_RESULTADOS = 6;

    private final ProductosDAO productosDAO = new ProductosDAO();
    private final UsuariosDAO usuariosDAO = new UsuariosDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");

        String termino = request.getParameter("termino");
        if (termino != null) termino = termino.trim();

        // Término vacío o demasiado corto: no consultamos la BD, devolvemos listas vacías.
        if (termino == null || termino.length() < 2) {
            response.getWriter().print("{\"productos\": [], \"empleados\": []}");
            return;
        }

        try {
            // ---- 1. Productos que coincidan por nombre ----
            List<Productos> productos = productosDAO.buscarPorNombre(termino, LIMITE_RESULTADOS);
            StringBuilder productosJson = new StringBuilder();
            for (Productos p : productos) {
                if (productosJson.length() > 0) productosJson.append(",");
                productosJson.append("{")
                    .append("\"id\":").append(p.getId()).append(",")
                    .append("\"nombre\":\"").append(escaparJson(p.getNombre())).append("\",")
                    .append("\"precio\":").append(String.format(Locale.US, "%.2f", p.getPrecio()))
                    .append("}");
            }

            // ---- 2. Empleados que coincidan por nombre, apellido o correo ----
            List<Usuarios> empleados = usuariosDAO.buscarPorNombreOCorreo(termino, LIMITE_RESULTADOS);
            StringBuilder empleadosJson = new StringBuilder();
            for (Usuarios u : empleados) {
                if (empleadosJson.length() > 0) empleadosJson.append(",");
                empleadosJson.append("{")
                    .append("\"id\":").append(u.getIdUsuarios()).append(",")
                    .append("\"nombre\":\"").append(escaparJson(u.getNombre() + " " + (u.getApellido() != null ? u.getApellido() : ""))).append("\",")
                    .append("\"correo\":\"").append(escaparJson(u.getCorreo())).append("\"")
                    .append("}");
            }

            // ---- 3. Ensamblar el JSON final ----
            String json = "{"
                    + "\"productos\": [" + productosJson + "],"
                    + "\"empleados\": [" + empleadosJson + "]"
                    + "}";

            response.getWriter().print(json);

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().print("{\"error\": \"Error al procesar la búsqueda\"}");
        }
    }

    private String escaparJson(String texto) {
        if (texto == null) return "";
        return texto.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
