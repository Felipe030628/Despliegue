package Servlet;

import Controlador.ProductosDAO;
import Controlador.CategoriaDAO;
import Modelo.Productos;
import Modelo.Categorias;
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

@WebServlet(name = "Producto", urlPatterns = {"/Producto"})
public class Producto extends HttpServlet {

    ProductosDAO dao = new ProductosDAO();
    CategoriaDAO catDao = new CategoriaDAO();

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String accion = request.getParameter("accion");
        if (accion == null) accion = "listar";

        switch (accion) {
            case "listar":
                // 1. Enviamos los productos para la tabla
                request.setAttribute("lista", dao.listarProductos());
                
                // 2. ENVIAMOS LAS CATEGORÍAS PARA EL SELECT
                List<Categorias> listaCategorias = catDao.listar();
                request.setAttribute("listaCategorias", listaCategorias);
                
                // 3. Redirigimos a la vista
                request.getRequestDispatcher("Vista/Productos.jsp").forward(request, response);
                break;

            // ---------- NUEVO: listado en JSON para la app Flutter (solo consulta) ----------
            case "listarJson": {
                response.setContentType("application/json;charset=UTF-8");
                List<Productos> listaProd = dao.listarProductos();
                List<Categorias> cats = catDao.listar();
                Map<Integer, String> catMap = new HashMap<>();
                for (Categorias c : cats) {
                    catMap.put(c.getIdCategorias(), c.getNombre_categoria());
                }

                StringBuilder sb = new StringBuilder("[");
                for (int i = 0; i < listaProd.size(); i++) {
                    Productos p = listaProd.get(i);
                    if (i > 0) sb.append(",");
                    int stock = dao.obtenerStock(p.getId());
                    String nomCat = catMap.getOrDefault(p.getIdCategoria(), "");
                    sb.append("{")
                      .append("\"id\":").append(p.getId()).append(",")
                      .append("\"nombre\":").append(JsonUtil.str(p.getNombre())).append(",")
                      .append("\"precio\":").append(p.getPrecio()).append(",")
                      .append("\"fecha_vencimiento\":").append(JsonUtil.str(p.getFecha_vencimiento())).append(",")
                      .append("\"idCategoria\":").append(p.getIdCategoria()).append(",")
                      .append("\"nomCategoria\":").append(JsonUtil.str(nomCat)).append(",")
                      .append("\"stock\":").append(stock)
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
                    int id = Integer.parseInt(request.getParameter("id"));
                    dao.eliminarProducto(id);
                    response.getWriter().print("{\"success\":true}");
                } catch (Exception e) {
                    response.getWriter().print("{\"success\":false,\"error\":" + JsonUtil.str(e.getMessage()) + "}");
                }
                break;
            }

            case "nuevo":
                List<Categorias> listaCatNuevo = catDao.listar();
                request.setAttribute("listaCategorias", listaCatNuevo);
                request.getRequestDispatcher("Vista/FormularioProducto.jsp").forward(request, response);
                break;

            case "guardar":
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

                    dao.registrarProducto(p);
                } catch (Exception e) {
                    System.err.println("Error al guardar producto: " + e.getMessage());
                }
                response.sendRedirect("Producto?accion=listar");
                break;

            case "cargar":
                try {
                    int id = Integer.parseInt(request.getParameter("id"));
                    Productos p = dao.listarPorId(id);
                    request.setAttribute("producto", p);
                    
                    List<Categorias> listaCatCargar = catDao.listar();
                    request.setAttribute("listaCategorias", listaCatCargar);
                    
                    request.getRequestDispatcher("Vista/EditarProducto.jsp").forward(request, response);
                } catch (Exception e) {
                    System.err.println("Error al cargar producto: " + e.getMessage());
                    response.sendRedirect("Producto?accion=listar");
                }
                break;

            case "actualizar":
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

                    dao.actualizarProducto(p);
                } catch (Exception e) {
                    System.err.println("Error al actualizar producto: " + e.getMessage());
                }
                response.sendRedirect("Producto?accion=listar");
                break;

            case "eliminar":
                try {
                    int id = Integer.parseInt(request.getParameter("id"));
                    dao.eliminarProducto(id);
                } catch (Exception e) {
                    System.err.println("Error al eliminar producto en Servlet: " + e.getMessage());
                }
                response.sendRedirect("Producto?accion=listar");
                break;

            default:
                response.sendRedirect("Producto?accion=listar");
                break;
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }
}
