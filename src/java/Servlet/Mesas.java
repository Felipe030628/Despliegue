package Servlet;

import Controlador.MesaDAO;
import Modelo.Mesa;
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
                // CORREGIDO: Redirige correctamente a "Mesas" en lugar de "MesasCont"
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