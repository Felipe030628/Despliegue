package Servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import Controlador.DetallePedidoDAO;
import Controlador.MesaDAO;
import Controlador.PedidoDAO;
import Controlador.ProductosDAO;
import Modelo.DetallePedido;
import Modelo.Pedidos;
import Modelo.Productos;
import Util.FacturaPdfUtil;
import Util.JsonUtil;

/**
 * Controlador HTTP para la gestión integral de pedidos, detalles de comanda y facturación.
 * Proporciona soporte para navegación web tradicional (JSP), APIs REST en JSON y generación de PDF.
 */
@WebServlet(name = "Pedido", urlPatterns = {"/Pedido"})
public class Pedido extends HttpServlet {

    private final PedidoDAO pedidoDAO = new PedidoDAO();
    private final MesaDAO mesaDAO = new MesaDAO();
    private final ProductosDAO productosDAO = new ProductosDAO();
    private final DetallePedidoDAO detallePedidoDAO = new DetallePedidoDAO();

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("accion");
        if (accion == null) {
            accion = "listar";
        }

        switch (accion) {
            case "listar":
                listarPedidosVista(request, response);
                break;

            case "listarJson":
                listarPedidosJson(response);
                break;

            case "detalleJson":
                obtenerDetallePedidoJson(request, response);
                break;

            case "eliminarJson":
                eliminarPedidoJson(request, response);
                break;

            case "guardar":
                guardarPedido(request, response);
                break;

            case "cargar":
                cargarPedidoEdicion(request, response);
                break;

            case "ver":
                verPedidoDetalle(request, response);
                break;

            case "factura":
                generarFacturaPdf(request, response);
                break;

            case "eliminar":
                eliminarPedido(request, response);
                break;

            case "actualizar":
                actualizarPedido(request, response);
                break;

            default:
                response.sendRedirect("Pedido?accion=listar");
                break;
        }
    }

    private void listarPedidosVista(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("lista", pedidoDAO.listarPedidos());
        request.setAttribute("listaMesas", mesaDAO.listarMesas());
        request.setAttribute("listaProductos", productosDAO.listarProductosConStock());
        request.getRequestDispatcher("Vista/Pedidos.jsp").forward(request, response);
    }

    private void listarPedidosJson(HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        List<Pedidos> lista = pedidoDAO.listarPedidos();

        String jsonArray = lista.stream()
                .map(this::mapPedidoToJson)
                .collect(Collectors.joining(","));

        response.getWriter().print("[" + jsonArray + "]");
    }

    private void obtenerDetallePedidoJson(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            Pedidos p = pedidoDAO.listarPorId(id);
            List<DetallePedido> detalles = detallePedidoDAO.listarPorPedido(id);

            String detallesJson = detalles.stream()
                    .map(this::mapDetalleToJson)
                    .collect(Collectors.joining(","));

            String jsonFinal = String.format(
                "{\"idPedido\":%d,\"cliente\":%s,\"mesa\":%s,\"fecha\":%s,\"estado\":%s,\"total\":%.2f,\"detalles\":[%s]}",
                p.getIdPedido(),
                JsonUtil.str(p.getCliente()),
                JsonUtil.str(p.getMesa()),
                JsonUtil.str(p.getFecha()),
                JsonUtil.str(p.getEstado()),
                p.getTotal(),
                detallesJson
            );

            response.getWriter().print(jsonFinal);
        } catch (Exception e) {
            String jsonError = String.format("{\"error\":%s}", JsonUtil.str(e.getMessage()));
            response.getWriter().print(jsonError);
        }
    }

    private void eliminarPedidoJson(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        try {
            int idPedido = Integer.parseInt(request.getParameter("id"));
            pedidoDAO.eliminarPedido(idPedido);
            response.getWriter().print("{\"success\":true}");
        } catch (Exception e) {
            String jsonError = String.format("{\"success\":false,\"error\":%s}", JsonUtil.str(e.getMessage()));
            response.getWriter().print(jsonError);
        }
    }

    private void guardarPedido(HttpServletRequest request, HttpServletResponse response) throws IOException {
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
                    if (cantidad <= 0) {
                        continue;
                    }

                    Productos producto = productosDAO.listarPorId(idProd);
                    int stockDisponible = productosDAO.obtenerStock(idProd);

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

            int idGenerado = pedidoDAO.registrarPedidoConDetalle(p, detalles);

            if (idGenerado > 0) {
                mesaDAO.actualizarEstadoPorNumero(mesa, "Ocupado");
            }

        } catch (Exception e) {
            System.err.println("Error al guardar pedido: " + e.getMessage());
        }
        response.sendRedirect("Pedido?accion=listar");
    }

    private void cargarPedidoEdicion(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            Pedidos pedido = pedidoDAO.listarPorId(id);
            request.setAttribute("pedido", pedido);
            request.setAttribute("listaMesas", mesaDAO.listarMesas());
            request.setAttribute("detalles", detallePedidoDAO.listarPorPedido(id));
            request.getRequestDispatcher("Vista/EditarPedido.jsp").forward(request, response);
        } catch (Exception e) {
            System.err.println("Error al cargar pedido para edición: " + e.getMessage());
            response.sendRedirect("Pedido?accion=listar");
        }
    }

    private void verPedidoDetalle(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            Pedidos pedido = pedidoDAO.listarPorId(id);
            request.setAttribute("pedido", pedido);
            request.setAttribute("detalles", detallePedidoDAO.listarPorPedido(id));
            request.getRequestDispatcher("Vista/VerPedido.jsp").forward(request, response);
        } catch (Exception e) {
            System.err.println("Error al visualizar el pedido: " + e.getMessage());
            response.sendRedirect("Pedido?accion=listar");
        }
    }

    private void generarFacturaPdf(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            Pedidos pedido = pedidoDAO.listarPorId(id);
            List<DetallePedido> detalles = detallePedidoDAO.listarPorPedido(id);

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
    }

    private void eliminarPedido(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int idPedido = Integer.parseInt(request.getParameter("id"));
            pedidoDAO.eliminarPedido(idPedido);
        } catch (Exception e) {
            System.err.println("Error al eliminar pedido: " + e.getMessage());
        }
        response.sendRedirect("Pedido?accion=listar");
    }

    private void actualizarPedido(HttpServletRequest request, HttpServletResponse response) throws IOException {
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

            pedidoDAO.actualizarPedido(p);

            if ("Completado".equals(estado) || "Cancelado".equals(estado)) {
                mesaDAO.actualizarEstadoPorNumero(mesa, "Libre");
            }

        } catch (Exception e) {
            System.err.println("Error al actualizar pedido: " + e.getMessage());
        }
        response.sendRedirect("Pedido?accion=listar");
    }

    private String mapPedidoToJson(Pedidos p) {
        return String.format(
            "{\"idPedido\":%d,\"cliente\":%s,\"mesa\":%s,\"fecha\":%s,\"estado\":%s,\"total\":%.2f}",
            p.getIdPedido(),
            JsonUtil.str(p.getCliente()),
            JsonUtil.str(p.getMesa()),
            JsonUtil.str(p.getFecha()),
            JsonUtil.str(p.getEstado()),
            p.getTotal()
        );
    }

    private String mapDetalleToJson(DetallePedido d) {
        return String.format(
            "{\"idProducto\":%d,\"nombreProducto\":%s,\"cantidad\":%d,\"precioUnitario\":%.2f,\"subtotal\":%.2f}",
            d.getIdProducto(),
            JsonUtil.str(d.getNombreProducto()),
            d.getCantidad(),
            d.getPrecioUnitario(),
            d.getSubtotal()
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