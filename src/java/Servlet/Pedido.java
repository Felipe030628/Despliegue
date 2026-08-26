package Servlet;

import Controlador.DetallePedidoDAO;
import Controlador.MesaDAO;
import Controlador.PedidoDAO;
import Controlador.ProductosDAO;
import Modelo.DetallePedido;
import Modelo.Pedidos;
import Modelo.Productos;
import Util.FacturaPdfUtil;
import Util.JsonUtil;
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

            // ---------- NUEVO: listado en JSON para la app Flutter (solo consulta) ----------
            case "listarJson": {
                response.setContentType("application/json;charset=UTF-8");
                List<Pedidos> lista = dao.listarPedidos();
                StringBuilder sb = new StringBuilder("[");
                for (int i = 0; i < lista.size(); i++) {
                    Pedidos p = lista.get(i);
                    if (i > 0) sb.append(",");
                    sb.append("{")
                      .append("\"idPedido\":").append(p.getIdPedido()).append(",")
                      .append("\"cliente\":").append(JsonUtil.str(p.getCliente())).append(",")
                      .append("\"mesa\":").append(JsonUtil.str(p.getMesa())).append(",")
                      .append("\"fecha\":").append(JsonUtil.str(p.getFecha())).append(",")
                      .append("\"estado\":").append(JsonUtil.str(p.getEstado())).append(",")
                      .append("\"total\":").append(p.getTotal())
                      .append("}");
                }
                sb.append("]");
                response.getWriter().print(sb.toString());
                break;
            }

            // ---------- NUEVO: detalle de un pedido (cabecera + líneas) en JSON ----------
            case "detalleJson": {
                response.setContentType("application/json;charset=UTF-8");
                try {
                    int id = Integer.parseInt(request.getParameter("id"));
                    Pedidos p = dao.listarPorId(id);
                    List<DetallePedido> detalles = daoDetalle.listarPorPedido(id);

                    StringBuilder det = new StringBuilder("[");
                    for (int i = 0; i < detalles.size(); i++) {
                        DetallePedido d = detalles.get(i);
                        if (i > 0) det.append(",");
                        det.append("{")
                           .append("\"idProducto\":").append(d.getIdProducto()).append(",")
                           .append("\"nombreProducto\":").append(JsonUtil.str(d.getNombreProducto())).append(",")
                           .append("\"cantidad\":").append(d.getCantidad()).append(",")
                           .append("\"precioUnitario\":").append(d.getPrecioUnitario()).append(",")
                           .append("\"subtotal\":").append(d.getSubtotal())
                           .append("}");
                    }
                    det.append("]");

                    String json = "{"
                            + "\"idPedido\":" + p.getIdPedido() + ","
                            + "\"cliente\":" + JsonUtil.str(p.getCliente()) + ","
                            + "\"mesa\":" + JsonUtil.str(p.getMesa()) + ","
                            + "\"fecha\":" + JsonUtil.str(p.getFecha()) + ","
                            + "\"estado\":" + JsonUtil.str(p.getEstado()) + ","
                            + "\"total\":" + p.getTotal() + ","
                            + "\"detalles\":" + det
                            + "}";
                    response.getWriter().print(json);
                } catch (Exception e) {
                    response.getWriter().print("{\"error\":" + JsonUtil.str(e.getMessage()) + "}");
                }
                break;
            }

            // ---------- NUEVO: eliminar devolviendo JSON en vez de redirigir ----------
            case "eliminarJson": {
                response.setContentType("application/json;charset=UTF-8");
                try {
                    int idPedido = Integer.parseInt(request.getParameter("id"));
                    dao.eliminarPedido(idPedido);
                    response.getWriter().print("{\"success\":true}");
                } catch (Exception e) {
                    response.getWriter().print("{\"success\":false,\"error\":" + JsonUtil.str(e.getMessage()) + "}");
                }
                break;
            }

            case "guardar":
                try {
                    String cliente = request.getParameter("cliente");
                    String mesa = request.getParameter("mesa");
                    String fecha = request.getParameter("fecha");
                    String estado = "Pendiente";

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
