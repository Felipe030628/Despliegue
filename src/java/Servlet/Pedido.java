package Servlet;

import Controlador.PedidoDAO;
import Modelo.Pedidos;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import Controlador.MesaDAO;

@WebServlet(name = "Pedido", urlPatterns = {"/Pedido"})
public class Pedido extends HttpServlet {

    PedidoDAO dao = new PedidoDAO();
    MesaDAO daoMesa = new MesaDAO();

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String accion = request.getParameter("accion");
        if (accion == null) {
            accion = "listar";
        }

        switch (accion) {
            case "listar":
                request.setAttribute("lista", dao.listarPedidos());
                request.setAttribute("listaMesas", daoMesa.listarMesas());
                request.getRequestDispatcher("Vista/Pedidos.jsp").forward(request, response);
                break;

            case "guardar":
                try {
                    String cliente = request.getParameter("cliente");
                    String mesa = request.getParameter("mesa");
                    String fecha = request.getParameter("fecha");
                    String estado = "Pendiente";
                    double total = Double.parseDouble(request.getParameter("total"));

                    Pedidos p = new Pedidos();
                    p.setCliente(cliente);
                    p.setMesa(mesa);
                    p.setFecha(fecha);
                    p.setEstado(estado);
                    p.setTotal(total);

                    dao.registrarPedido(p);
                    
                    MesaDAO mesaDao = new MesaDAO();
                    mesaDao.actualizarEstadoPorNumero(mesa, "Ocupado");
                    
                } catch (Exception e) {
                    System.err.println("Error al guardar pedido: " + e.getMessage());
                }
                response.sendRedirect("Pedido?accion=listar");
                break;

            case "cargar":
                try {
                    int id = Integer.parseInt(request.getParameter("id"));
                    Pedidos pedido = dao.listarPorId(id);
                    request.setAttribute("pedido", pedido);
                    request.setAttribute("listaMesas", daoMesa.listarMesas());
                    request.getRequestDispatcher("Vista/EditarPedido.jsp").forward(request, response);
                } catch (Exception e) {
                    System.err.println("Error al cargar pedido para editar: " + e.getMessage());
                    response.sendRedirect("Pedido?accion=listar");
                }
                break;
                
            case "eliminar":
                try {
                    int idPedido = Integer.parseInt(request.getParameter("id"));
                    dao.eliminarPedido(idPedido);
                } catch (Exception e) {
                    System.err.println("Error al eliminar pedido: " + e.getMessage());
                }
                response.sendRedirect("Pedido?accion=listar");
                break;

            case "actualizar":
                try {
                    int idPedido = Integer.parseInt(request.getParameter("idPedido"));
                    String cliente = request.getParameter("cliente");
                    String mesa = request.getParameter("mesa");
                    String fecha = request.getParameter("fecha");
                    String estado = request.getParameter("estado");
                    double total = Double.parseDouble(request.getParameter("total"));

                    Pedidos p = new Pedidos();
                    p.setIdPedido(idPedido);
                    p.setCliente(cliente);
                    p.setMesa(mesa);
                    p.setFecha(fecha);
                    p.setEstado(estado);
                    p.setTotal(total);

                    dao.actualizarPedido(p);

                    if ("Completado".equals(estado) || "Cancelado".equals(estado)) {
                        MesaDAO mesaDao = new MesaDAO();
                        mesaDao.actualizarEstadoPorNumero(mesa, "Libre");
                    }

                } catch (Exception e) {
                    System.err.println("Error al actualizar pedido: " + e.getMessage());
                }
                response.sendRedirect("Pedido?accion=listar");
                break;

            default:
                response.sendRedirect("Pedido?accion=listar");
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