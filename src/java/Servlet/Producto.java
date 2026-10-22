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

import Controlador.CategoriaDAO;
import Controlador.ProductosDAO;
import Modelo.Categorias;
import Modelo.Productos;
import Util.JsonUtil;

/**
 * Controlador de peticiones HTTP para el catálogo de productos.
 * Gestiona operaciones CRUD tanto para las vistas JSP tradicionales
 * como para endpoints en formato JSON dirigidos a aplicaciones móviles/web.
 */
@WebServlet(name = "Producto", urlPatterns = {"/Producto"})
public class Producto extends HttpServlet {

    private final ProductosDAO productosDAO = new ProductosDAO();
    private final CategoriaDAO categoriaDAO = new CategoriaDAO();

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("accion");
        if (accion == null) {
            accion = "listar";
        }

        switch (accion) {
            case "listar":
                listarProductosVista(request, response);
                break;

            case "listarJson":
                listarProductosJson(response);
                break;

            case "eliminarJson":
                eliminarProductoJson(request, response);
                break;

            case "nuevo":
                nuevoFormularioVista(request, response);
                break;

            case "guardar":
                guardarProducto(request, response);
                break;

            case "cargar":
                cargarProductoEdicion(request, response);
                break;

            case "actualizar":
                actualizarProducto(request, response);
                break;

            case "eliminar":
                eliminarProducto(request, response);
                break;

            default:
                response.sendRedirect("Producto?accion=listar");
                break;
        }
    }

    private void listarProductosVista(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("lista", productosDAO.listarProductos());
        request.setAttribute("listaCategorias", categoriaDAO.listar());
        request.getRequestDispatcher("Vista/Productos.jsp").forward(request, response);
    }

    private void listarProductosJson(HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        List<Productos> listaProd = productosDAO.listarProductos();
        List<Categorias> cats = categoriaDAO.listar();

        Map<Integer, String> catMap = cats.stream()
                .collect(Collectors.toMap(Categorias::getIdCategorias, Categorias::getNombre_categoria, (e1, e2) -> e1));

        String jsonArray = listaProd.stream()
                .map(p -> {
                    int stock = productosDAO.obtenerStock(p.getId());
                    String nomCat = catMap.getOrDefault(p.getIdCategoria(), "");
                    return mapProductoToJson(p, nomCat, stock);
                })
                .collect(Collectors.joining(","));

        response.getWriter().print("[" + jsonArray + "]");
    }

    private void eliminarProductoJson(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            productosDAO.eliminarProducto(id);
            response.getWriter().print("{\"success\":true}");
        } catch (Exception e) {
            String jsonError = String.format("{\"success\":false,\"error\":%s}", JsonUtil.str(e.getMessage()));
            response.getWriter().print(jsonError);
        }
    }

    private void nuevoFormularioVista(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("listaCategorias", categoriaDAO.listar());
        request.getRequestDispatcher("Vista/FormularioProducto.jsp").forward(request, response);
    }

    private void guardarProducto(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String nombre = request.getParameter("nombre");
            double precio = Double.parseDouble(request.getParameter("precio"));
            String fecha = request.getParameter("fecha_vencimiento");
            int idCat = Integer.parseInt(request.getParameter("idCategoria"));

            Productos p = new Productos();
            p.setNombre(nombre);
            p.setPrecio(precio);
            p.setFecha_vencimiento(fecha);
            p.setIdCategoria(idCat);

            productosDAO.registrarProducto(p);
        } catch (Exception e) {
            System.err.println("Error al guardar el producto: " + e.getMessage());
        }
        response.sendRedirect("Producto?accion=listar");
    }

    private void cargarProductoEdicion(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            Productos p = productosDAO.listarPorId(id);
            request.setAttribute("producto", p);
            request.setAttribute("listaCategorias", categoriaDAO.listar());
            request.getRequestDispatcher("Vista/EditarProducto.jsp").forward(request, response);
        } catch (Exception e) {
            System.err.println("Error al cargar el producto para edición: " + e.getMessage());
            response.sendRedirect("Producto?accion=listar");
        }
    }

    private void actualizarProducto(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            String nombre = request.getParameter("nombre");
            double precio = Double.parseDouble(request.getParameter("precio"));
            String fecha = request.getParameter("fecha_vencimiento");
            int idCat = Integer.parseInt(request.getParameter("idCategoria"));

            Productos p = new Productos();
            p.setId(id);
            p.setNombre(nombre);
            p.setPrecio(precio);
            p.setFecha_vencimiento(fecha);
            p.setIdCategoria(idCat);

            productosDAO.actualizarProducto(p);
        } catch (Exception e) {
            System.err.println("Error al actualizar el producto: " + e.getMessage());
        }
        response.sendRedirect("Producto?accion=listar");
    }

    private void eliminarProducto(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            productosDAO.eliminarProducto(id);
        } catch (Exception e) {
            System.err.println("Error al eliminar el producto: " + e.getMessage());
        }
        response.sendRedirect("Producto?accion=listar");
    }

    private String mapProductoToJson(Productos p, String nomCategoria, int stock) {
        return String.format(
            "{\"id\":%d,\"nombre\":%s,\"precio\":%.2f,\"fecha_vencimiento\":%s,\"idCategoria\":%d,\"nomCategoria\":%s,\"stock\":%d}",
            p.getId(),
            JsonUtil.str(p.getNombre()),
            p.getPrecio(),
            JsonUtil.str(p.getFecha_vencimiento()),
            p.getIdCategoria(),
            JsonUtil.str(nomCategoria),
            stock
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