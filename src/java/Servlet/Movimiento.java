package Servlet;

import Controlador.MovimientoStockDAO;
import Controlador.ProductosDAO;
import Modelo.MovimientosStock;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "Movimiento", urlPatterns = {"/Movimiento"})
public class Movimiento extends HttpServlet {

    MovimientoStockDAO dao = new MovimientoStockDAO();
    ProductosDAO daoProd = new ProductosDAO();

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String accion = request.getParameter("accion");
        if (accion == null) {
            accion = "listar";
        }

        switch (accion) {
            case "listar":
                List<MovimientosStock> lista = dao.listarMovimientos();
                System.out.println("DEBUG: Se encontraron " + lista.size() + " movimientos en la base de datos.");
                
                request.setAttribute("listaMov", lista);
                request.setAttribute("listaProd", daoProd.listarProductos());
                // Stock general: se recalcula en cada carga a partir de todos los
                // movimientos (entradas/salidas manuales + salidas por pedidos).
                request.setAttribute("listaStock", daoProd.listarProductosConStock());
                request.getRequestDispatcher("Vista/Movimientos.jsp").forward(request, response);
                break;

            case "guardar":
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

                    dao.registrarMovimiento(m);
                } catch (Exception e) {
                    System.err.println("Error al procesar datos: " + e.getMessage());
                }

                response.sendRedirect("Movimiento?accion=listar");
                break;

            // --- CASO AGREGADO: Atrapa el clic del botón y carga los datos para editar ---
            case "cargar":
                try {
                    int id = Integer.parseInt(request.getParameter("id"));
                    MovimientosStock m = dao.listarPorId(id);
                    request.setAttribute("movimiento", m);
                    request.setAttribute("listaProd", daoProd.listarProductos());
                    request.getRequestDispatcher("Vista/EditarMovimiento.jsp").forward(request, response);
                } catch (Exception e) {
                    System.err.println("Error al cargar para editar: " + e.getMessage());
                    response.sendRedirect("Movimiento?accion=listar");
                }
                break;

            // --- CASO AGREGADO: Procesa los cambios del formulario de edición ---
            case "actualizar":
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

                    dao.actualizarMovimiento(m);
                } catch (Exception e) {
                    System.err.println("Error al actualizar: " + e.getMessage());
                }

                response.sendRedirect("Movimiento?accion=listar");
                break;
                
                case "eliminar":
    try {
        int idMovimiento = Integer.parseInt(request.getParameter("id"));
        dao.eliminarMovimiento(idMovimiento);
    } catch (Exception e) {
        System.err.println("Error al eliminar movimiento: " + e.getMessage());
    }
    response.sendRedirect("Movimiento?accion=listar");
    break;

            default:
                response.sendRedirect("Movimiento?accion=listar");
                break;
        }
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