package Servlet;

import Controlador.DetallePedidoDAO;
import Controlador.MesaDAO;
import Controlador.PedidoDAO;
import Controlador.ProductosDAO;
import Modelo.DetallePedido;
import Modelo.Pedidos;
import Modelo.Productos;
import Util.FacturaPdfUtil;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "Pedido", urlPatterns = {"/Pedido"})
public class Pedido extends HttpServlet {

    PedidoDAO dao = new PedidoDAO();
    MesaDAO daoMesa = new MesaDAO();
    ProductosDAO daoProducto = new ProductosDAO();
    DetallePedidoDAO daoDetalle = new DetallePedidoDAO();

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
                request.setAttribute("listaProductos", daoProducto.listarProductosConStock());
                request.getRequestDispatcher("Vista/Pedidos.jsp").forward(request, response);
                break;

            case "guardar":
                try {
                    String cliente = request.getParameter("cliente");
                    String mesa = request.getParameter("mesa");
                    String fecha = request.getParameter("fecha");
                    String estado = "Pendiente";

                    // Líneas de producto enviadas desde el carrito de la vista (arreglos paralelos)
                    String[] idsProducto = request.getParameterValues("productoId[]");
                    String[] cantidades = request.getParameterValues("cantidad[]");

                    List<DetallePedido> detalles = new ArrayList<>();
                    double total = 0;
                    String errorStock = null;

                    if (idsProducto != null) {
                        for (int i = 0; i < idsProducto.length; i++) {
                            int idProd = Integer.parseInt(idsProducto[i]);
                            int cantidad = Integer.parseInt(cantidades[i]);
                            if (cantidad <= 0) continue;

                            Productos producto = daoProducto.listarPorId(idProd);
                            int stockDisponible = daoProducto.obtenerStock(idProd);

                            if (cantidad > stockDisponible) {
                                errorStock = "Stock insuficiente para \"" + producto.getNombre()
                                        + "\" (disponible: " + stockDisponible + ")";
                                break;
                            }

                            DetallePedido d = new DetallePedido(idProd, cantidad, producto.getPrecio());
                            detalles.add(d);
                            total += d.getSubtotal();
                        }
                    }

                    if (errorStock != null) {
                        request.getSession().setAttribute("errorPedido", errorStock);
                        response.sendRedirect("Pedido?accion=listar");
                        return;
                    }

                    if (detalles.isEmpty()) {
                        request.getSession().setAttribute("errorPedido", "Debe agregar al menos un producto al pedido.");
                        response.sendRedirect("Pedido?accion=listar");
                        return;
                    }

                    Pedidos p = new Pedidos();
                    p.setCliente(cliente);
                    p.setMesa(mesa);
                    p.setFecha(fecha);
                    p.setEstado(estado);
                    p.setTotal(total);

                    int idGenerado = dao.registrarPedidoConDetalle(p, detalles);

                    if (idGenerado > 0) {
                        daoMesa.actualizarEstadoPorNumero(mesa, "Ocupado");
                    }

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
                    request.setAttribute("detalles", daoDetalle.listarPorPedido(id));
                    request.getRequestDispatcher("Vista/EditarPedido.jsp").forward(request, response);
                } catch (Exception e) {
                    System.err.println("Error al cargar pedido para editar: " + e.getMessage());
                    response.sendRedirect("Pedido?accion=listar");
                }
                break;

            case "ver":
                try {
                    int id = Integer.parseInt(request.getParameter("id"));
                    Pedidos pedido = dao.listarPorId(id);
                    request.setAttribute("pedido", pedido);
                    request.setAttribute("detalles", daoDetalle.listarPorPedido(id));
                    request.getRequestDispatcher("Vista/VerPedido.jsp").forward(request, response);
                } catch (Exception e) {
                    System.err.println("Error al ver pedido: " + e.getMessage());
                    response.sendRedirect("Pedido?accion=listar");
                }
                break;

            case "factura":
                try {
                    int id = Integer.parseInt(request.getParameter("id"));
                    Pedidos pedido = dao.listarPorId(id);
                    List<DetallePedido> detalles = daoDetalle.listarPorPedido(id);

                    byte[] pdf = FacturaPdfUtil.generarFactura(pedido, detalles);

                    response.setContentType("application/pdf");
                    response.setContentLength(pdf.length);
                    String nombreArchivo = URLEncoder.encode("Factura_Pedido_" + id + ".pdf", StandardCharsets.UTF_8.toString());
                    response.setHeader("Content-Disposition", "inline; filename=\"" + nombreArchivo + "\"");

                    try (OutputStream os = response.getOutputStream()) {
                        os.write(pdf);
                        os.flush();
                    }
                } catch (Exception e) {
                    System.err.println("Error al generar factura PDF: " + e.getMessage());
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
                        daoMesa.actualizarEstadoPorNumero(mesa, "Libre");
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
