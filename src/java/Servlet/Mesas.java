package Servlet;

import Controlador.MesaDAO;
import Modelo.Mesa;
import Util.JsonUtil;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "Mesas", urlPatterns = {"/Mesas"})
public class Mesas extends HttpServlet {

    MesaDAO dao = new MesaDAO();

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String accion = request.getParameter("accion");
        if (accion == null) accion = "listar";

        switch (accion) {
            case "listar":
                List<Mesa> lista = dao.listarMesas();
                request.setAttribute("listaMesas", lista);
                request.getRequestDispatcher("Vista/Mesa.jsp").forward(request, response);
                break;

            // ---------- NUEVO: listado en JSON para la app Flutter (solo consulta) ----------
            case "listarJson": {
                response.setContentType("application/json;charset=UTF-8");
                List<Mesa> listaMesas = dao.listarMesas();
                StringBuilder sb = new StringBuilder("[");
                for (int i = 0; i < listaMesas.size(); i++) {
                    Mesa m = listaMesas.get(i);
                    if (i > 0) sb.append(",");
                    sb.append("{")
                      .append("\"idMesa\":").append(m.getIdMesa()).append(",")
                      .append("\"numero_mesa\":").append(JsonUtil.str(m.getNumero_mesa())).append(",")
                      .append("\"estado\":").append(JsonUtil.str(m.getEstado())).append(",")
                      .append("\"capacidad\":").append(m.getCapacidad())
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
                    int idMesa = Integer.parseInt(request.getParameter("id"));
                    dao.eliminarMesa(idMesa);
                    response.getWriter().print("{\"success\":true}");
                } catch (Exception e) {
                    response.getWriter().print("{\"success\":false,\"error\":" + JsonUtil.str(e.getMessage()) + "}");
                }
                break;
            }

            case "guardar":
                try {
                    String numero = request.getParameter("numero");
                    String capacidadStr = request.getParameter("capacidad");
                    
                    if (numero != null && capacidadStr != null) {
                        int capacidad = Integer.parseInt(capacidadStr);
                        
                        Mesa m = new Mesa();
                        m.setNumero_mesa(numero);
                        m.setEstado("Libre");
                        m.setCapacidad(capacidad);
                        
                        dao.registrarMesa(m);
                    }
                } catch (Exception e) {
                    System.err.println("Error al guardar mesa: " + e.getMessage());
                }
                response.sendRedirect("Mesas?accion=listar");
                break;
                
            case "eliminar":
                try {
                    int idMesa = Integer.parseInt(request.getParameter("id"));
                    dao.eliminarMesa(idMesa);
                } catch (Exception e) {
                    System.err.println("Error al eliminar mesa: " + e.getMessage());
                }
                response.sendRedirect("Mesas?accion=listar");
                break;

            case "cargar":
                try {
                    int id = Integer.parseInt(request.getParameter("id"));
                    Mesa mesa = dao.listarPorId(id);
                    request.setAttribute("mesa", mesa);
                    request.getRequestDispatcher("Vista/EditarMesa.jsp").forward(request, response);
                } catch (Exception e) {
                    System.err.println("Error al cargar mesa: " + e.getMessage());
                    response.sendRedirect("Mesas?accion=listar");
                }
                break;

            case "actualizar":
                try {
                    int idMesa = Integer.parseInt(request.getParameter("idMesa"));
                    String numero = request.getParameter("numero");
                    int capacidad = Integer.parseInt(request.getParameter("capacidad"));
                    String estado = request.getParameter("estado");

                    Mesa m = new Mesa();
                    m.setIdMesa(idMesa);
                    m.setNumero_mesa(numero);
                    m.setCapacidad(capacidad);
                    m.setEstado(estado);

                    dao.actualizarMesa(m);
                } catch (Exception e) {
                    System.err.println("Error al actualizar mesa: " + e.getMessage());
                }
                response.sendRedirect("Mesas?accion=listar");
                break;

            default:
                response.sendRedirect("Mesas?accion=listar");
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
