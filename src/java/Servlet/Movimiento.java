package Servlet;

import Controlador.MovimientoStockDAO;
import Controlador.ProductosDAO;
import Modelo.MovimientosStock;
import Modelo.Productos;
import Util.JsonUtil;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
                request.setAttribute("listaStock", daoProd.listarProductosConStock());
                request.getRequestDispatcher("Vista/Movimientos.jsp").forward(request, response);
                break;

            // ---------- NUEVO: listado en JSON para la app Flutter (solo consulta) ----------
            case "listarJson": {
                response.setContentType("application/json;charset=UTF-8");
                List<MovimientosStock> listaMov = dao.listarMovimientos();
                List<Productos> productos = daoProd.listarProductos();
                Map<Integer, String> prodMap = new HashMap<>();
                for (Productos p : productos) {
                    prodMap.put(p.getId(), p.getNombre());
                }

                StringBuilder sb = new StringBuilder("[");
                for (int i = 0; i < listaMov.size(); i++) {
                    MovimientosStock m = listaMov.get(i);
                    if (i > 0) sb.append(",");
                    String nombreProd = prodMap.getOrDefault(m.getIdProducto(), "");
                    sb.append("{")
                      .append("\"idMovimiento\":").append(m.getIdMovimiento()).append(",")
                      .append("\"fecha\":").append(JsonUtil.str(m.getFecha())).append(",")
                      .append("\"cantidad\":").append(m.getCantidad()).append(",")
                      .append("\"motivo\":").append(JsonUtil.str(m.getMotivo())).append(",")
                      .append("\"idProducto\":").append(m.getIdProducto()).append(",")
                      .append("\"nombreProducto\":").append(JsonUtil.str(nombreProd))
                      .append("}");
                }
                sb.append("]");
                response.getWriter().print(sb.toString());
                break;
            }

            // ---------- NUEVO: eliminar devolviendo JSON en vez de redirigir ----------
            case "eliminarJson": {
                response.setContentType("application/json;charset=UTF-8");
                try {
                    int idMovimiento = Integer.parseInt(request.getParameter("id"));
                    dao.eliminarMovimiento(idMovimiento);
                    response.getWriter().print("{\"success\":true}");
                } catch (Exception e) {
                    response.getWriter().print("{\"success\":false,\"error\":" + JsonUtil.str(e.getMessage()) + "}");
                }
                break;
            }

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
