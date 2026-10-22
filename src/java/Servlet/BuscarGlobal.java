package Servlet;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import Controlador.ProductosDAO;
import Controlador.UsuariosDAO;
import Modelo.Productos;
import Modelo.Usuarios;

/**
 * Endpoint de búsqueda global para la barra de navegación superior.
 * Consulta simultáneamente coincidencias en productos y usuarios/empleados.
 */
@WebServlet(name = "BuscarGlobal", urlPatterns = {"/BuscarGlobal"})
public class BuscarGlobal extends HttpServlet {

    private static final int LIMITE_RESULTADOS = 6;

    private final ProductosDAO productosDAO = new ProductosDAO();
    private final UsuariosDAO usuariosDAO = new UsuariosDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");

        String termino = request.getParameter("termino");
        if (termino != null) {
            termino = termino.trim();
        }

        if (termino == null || termino.length() < 2) {
            response.getWriter().print("{\"productos\": [], \"empleados\": []}");
            return;
        }

        try {
            List<Productos> productos = productosDAO.buscarPorNombre(termino, LIMITE_RESULTADOS);
            List<Usuarios> empleados = usuariosDAO.buscarPorNombreOCorreo(termino, LIMITE_RESULTADOS);

            String productosJson = productos.stream()
                    .map(this::mapProductoToJson)
                    .collect(Collectors.joining(","));

            String empleadosJson = empleados.stream()
                    .map(this::mapEmpleadoToJson)
                    .collect(Collectors.joining(","));

            String jsonFinal = String.format("{\"productos\":[%s],\"empleados\":[%s]}", productosJson, empleadosJson);
            response.getWriter().print(jsonFinal);

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().print("{\"error\":\"Error al procesar la búsqueda\"}");
        }
    }

    private String mapProductoToJson(Productos p) {
        return String.format(Locale.US,
                "{\"id\":%d,\"nombre\":\"%s\",\"precio\":%.2f}",
                p.getId(),
                escaparJson(p.getNombre()),
                p.getPrecio()
        );
    }

    private String mapEmpleadoToJson(Usuarios u) {
        String nombreCompleto = u.getNombre() + (u.getApellido() != null ? " " + u.getApellido() : "");
        return String.format(
                "{\"id\":%d,\"nombre\":\"%s\",\"correo\":\"%s\"}",
                u.getIdUsuarios(),
                escaparJson(nombreCompleto.trim()),
                escaparJson(u.getCorreo())
        );
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