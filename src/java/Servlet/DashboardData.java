package Servlet;

import Controlador.PedidoDAO;
import Controlador.ProductosDAO;
import java.io.IOException;
import java.util.Locale; // Necesario para asegurar el formato de punto decimal
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "DashboardData", urlPatterns = {"/DashboardData"})
public class DashboardData extends HttpServlet {

    private PedidoDAO pedidoDAO = new PedidoDAO();
    private ProductosDAO productosDAO = new ProductosDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 1. Definir tipo de contenido JSON
        response.setContentType("application/json;charset=UTF-8");
        
        try {
            // 2. Obtener los valores
            double totalCaja = pedidoDAO.obtenerTotalCajaHoy();
            int stockCritico = productosDAO.contarProductosCriticos();
            int variedad = productosDAO.contarTotalMarcas();
            
            // 3. Crear JSON usando Locale.US para asegurar que el decimal sea '.' y no ','
            // Esto soluciona el error: "Expected double-quoted property name"
            String json = String.format(Locale.US, 
                "{\"caja\": %.2f, \"critico\": %d, \"variedad\": %d}", 
                totalCaja, stockCritico, variedad
            );
            
            // 4. Escribir y enviar el JSON puro
            response.getWriter().print(json);
            
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().print("{\"error\": \"Error al procesar datos\"}");
        }
    }
}