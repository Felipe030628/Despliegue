package Servlet;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import Controlador.MovimientoStockDAO;
import Controlador.ProductosDAO;
import Modelo.MovimientosStock;
import Modelo.Productos;
import Util.JsonUtil;

/**
 * Controlador para la gestión de movimientos de inventario y stock.
 * Atiende solicitudes web (JSP) y peticiones JSON para la aplicación móvil / web.
 */
@WebServlet(name = "Movimiento", urlPatterns = {"/Movimiento"})
public class Movimiento extends HttpServlet {

    private final MovimientoStockDAO movimientoDAO = new MovimientoStockDAO();
    private final ProductosDAO productosDAO = new ProductosDAO();

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("accion");
        if (accion == null) {
            accion = "listar";
        }

        switch (accion) {
            case "listar":
                listarMovimientosVista(request, response);
                break;

            case "listarJson":
                listarMovimientosJson(response);
                break;

            case "eliminarJson":
                eliminarMovimientoJson(request, response);
                break;

            case "guardar":
                guardarMovimiento(request, response);
                break;

            case "cargar":
                cargarMovimientoEdicion(request, response);
                break;

            case "actualizar":
                actualizarMovimiento(request, response);
                break;

            case "eliminar":
                eliminarMovimiento(request, response);
                break;

            default:
                response.sendRedirect("Movimiento?accion=listar");
                break;
        }
    }

    private void listarMovimientosVista(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<MovimientosStock> lista = movimientoDAO.listarMovimientos();
        request.setAttribute("listaMov", lista);
        request.setAttribute("listaProd", productosDAO.listarProductos());
        request.setAttribute("listaStock", productosDAO.listarProductosConStock());
        request.getRequestDispatcher("Vista/Movimientos.jsp").forward(request, response);
    }

    private void listarMovimientosJson(HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        List<MovimientosStock> listaMov = movimientoDAO.listarMovimientos();
        List<Productos> productos = productosDAO.listarProductos();

        Map<Integer, String> prodMap = productos.stream()
                .collect(Collectors.toMap(Productos::getId, Productos::getNombre, (e1, e2) -> e1));

        String jsonArray = listaMov.stream()
                .map(m -> mapMovimientoToJson(m, prodMap.getOrDefault(m.getIdProducto(), "")))
                .collect(Collectors.joining(","));

        response.getWriter().print("[" + jsonArray + "]");
    }

    private void eliminarMovimientoJson(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        try {
            int idMovimiento = Integer.parseInt(request.getParameter("id"));
            movimientoDAO.eliminarMovimiento(idMovimiento);
            response.getWriter().print("{\"success\":true}");
        } catch (Exception e) {
            String jsonError = String.format("{\"success\":false,\"error\":%s}", JsonUtil.str(e.getMessage()));
            response.getWriter().print(jsonError);
        }
    }

    private void guardarMovimiento(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String fecha = request.getParameter("fecha");
            int cantidad = Integer.parseInt(request.getParameter("cantidad"));
            String motivo = request.getParameter("motivo");
            int idProducto = Integer.parseInt(request.getParameter("idProducto"));

            MovimientosStock m = new MovimientosStock();
            m.setFecha(fecha);
            m.setCantidad(cantidad);
            m.setMotivo(motivo);
            m.setIdProducto(idProducto);

            movimientoDAO.registrarMovimiento(m);
        } catch (Exception e) {
            System.err.println("Error al guardar movimiento de stock: " + e.getMessage());
        }
        response.sendRedirect("Movimiento?accion=listar");
    }

    private void cargarMovimientoEdicion(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            MovimientosStock m = movimientoDAO.listarPorId(id);
            request.setAttribute("movimiento", m);
            request.setAttribute("listaProd", productosDAO.listarProductos());
            request.getRequestDispatcher("Vista/EditarMovimiento.jsp").forward(request, response);
        } catch (Exception e) {
            System.err.println("Error al cargar movimiento para edición: " + e.getMessage());
            response.sendRedirect("Movimiento?accion=listar");
        }
    }

    private void actualizarMovimiento(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int idMovimiento = Integer.parseInt(request.getParameter("idMovimiento"));
            String fecha = request.getParameter("fecha");
            int cantidad = Integer.parseInt(request.getParameter("cantidad"));
            String motivo = request.getParameter("motivo");
            int idProducto = Integer.parseInt(request.getParameter("idProducto"));

            MovimientosStock m = new MovimientosStock();
            m.setIdMovimiento(idMovimiento);
            m.setFecha(fecha);
            m.setCantidad(cantidad);
            m.setMotivo(motivo);
            m.setIdProducto(idProducto);

            movimientoDAO.actualizarMovimiento(m);
        } catch (Exception e) {
            System.err.println("Error al actualizar movimiento de stock: " + e.getMessage());
        }
        response.sendRedirect("Movimiento?accion=listar");
    }

    private void eliminarMovimiento(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int idMovimiento = Integer.parseInt(request.getParameter("id"));
            movimientoDAO.eliminarMovimiento(idMovimiento);
        } catch (Exception e) {
            System.err.println("Error al eliminar movimiento de stock: " + e.getMessage());
        }
        response.sendRedirect("Movimiento?accion=listar");
    }

    private String mapMovimientoToJson(MovimientosStock m, String nombreProducto) {
        return String.format(
            "{\"idMovimiento\":%d,\"fecha\":%s,\"cantidad\":%d,\"motivo\":%s,\"idProducto\":%d,\"nombreProducto\":%s}",
            m.getIdMovimiento(),
            JsonUtil.str(m.getFecha()),
            m.getCantidad(),
            JsonUtil.str(m.getMotivo()),
            m.getIdProducto(),
            JsonUtil.str(nombreProducto)
        );
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}