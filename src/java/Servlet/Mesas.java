package Servlet;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import Controlador.MesaDAO;
import Modelo.Mesa;
import Util.JsonUtil;

/**
 * Controlador de peticiones HTTP para la gestión de mesas.
 * Atiende solicitudes web (JSP) y peticiones JSON para clientes API/móvil.
 */
@WebServlet(name = "Mesas", urlPatterns = {"/Mesas"})
public class Mesas extends HttpServlet {

    private final MesaDAO mesaDAO = new MesaDAO();

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("accion");
        if (accion == null) {
            accion = "listar";
        }

        switch (accion) {
            case "listar":
                listarMesasVista(request, response);
                break;

            case "listarJson":
                listarMesasJson(response);
                break;

            case "eliminarJson":
                eliminarMesaJson(request, response);
                break;

            case "guardar":
                guardarMesa(request, response);
                break;

            case "eliminar":
                eliminarMesa(request, response);
                break;

            case "cargar":
                cargarMesaEdicion(request, response);
                break;

            case "actualizar":
                actualizarMesa(request, response);
                break;

            default:
                response.sendRedirect("Mesas?accion=listar");
                break;
        }
    }

    private void listarMesasVista(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Mesa> lista = mesaDAO.listarMesas();
        request.setAttribute("listaMesas", lista);
        request.getRequestDispatcher("Vista/Mesa.jsp").forward(request, response);
    }

    private void listarMesasJson(HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        List<Mesa> listaMesas = mesaDAO.listarMesas();

        String jsonArray = listaMesas.stream()
                .map(this::mapMesaToJson)
                .collect(Collectors.joining(","));

        response.getWriter().print("[" + jsonArray + "]");
    }

    private void eliminarMesaJson(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        try {
            int idMesa = Integer.parseInt(request.getParameter("id"));
            mesaDAO.eliminarMesa(idMesa);
            response.getWriter().print("{\"success\":true}");
        } catch (Exception e) {
            String jsonError = String.format("{\"success\":false,\"error\":%s}", JsonUtil.str(e.getMessage()));
            response.getWriter().print(jsonError);
        }
    }

    private void guardarMesa(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String numero = request.getParameter("numero");
            String capacidadStr = request.getParameter("capacidad");

            if (numero != null && capacidadStr != null) {
                int capacidad = Integer.parseInt(capacidadStr);

                Mesa mesa = new Mesa();
                mesa.setNumero_mesa(numero);
                mesa.setEstado("Libre");
                mesa.setCapacidad(capacidad);

                mesaDAO.registrarMesa(mesa);
            }
        } catch (Exception e) {
            System.err.println("Error al guardar la mesa: " + e.getMessage());
        }
        response.sendRedirect("Mesas?accion=listar");
    }

    private void eliminarMesa(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int idMesa = Integer.parseInt(request.getParameter("id"));
            mesaDAO.eliminarMesa(idMesa);
        } catch (Exception e) {
            System.err.println("Error al eliminar la mesa: " + e.getMessage());
        }
        response.sendRedirect("Mesas?accion=listar");
    }

    private void cargarMesaEdicion(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            Mesa mesa = mesaDAO.listarPorId(id);
            request.setAttribute("mesa", mesa);
            request.getRequestDispatcher("Vista/EditarMesa.jsp").forward(request, response);
        } catch (Exception e) {
            System.err.println("Error al cargar la mesa: " + e.getMessage());
            response.sendRedirect("Mesas?accion=listar");
        }
    }

    private void actualizarMesa(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int idMesa = Integer.parseInt(request.getParameter("idMesa"));
            String numero = request.getParameter("numero");
            int capacidad = Integer.parseInt(request.getParameter("capacidad"));
            String estado = request.getParameter("estado");

            Mesa mesa = new Mesa();
            mesa.setIdMesa(idMesa);
            mesa.setNumero_mesa(numero);
            mesa.setCapacidad(capacidad);
            mesa.setEstado(estado);

            mesaDAO.actualizarMesa(mesa);
        } catch (Exception e) {
            System.err.println("Error al actualizar la mesa: " + e.getMessage());
        }
        response.sendRedirect("Mesas?accion=listar");
    }

    private String mapMesaToJson(Mesa m) {
        return String.format(
            "{\"idMesa\":%d,\"numero_mesa\":%s,\"estado\":%s,\"capacidad\":%d}",
            m.getIdMesa(),
            JsonUtil.str(m.getNumero_mesa()),
            JsonUtil.str(m.getEstado()),
            m.getCapacidad()
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