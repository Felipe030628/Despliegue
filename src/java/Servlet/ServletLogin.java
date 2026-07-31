package Servlet;

import Controlador.UsuariosDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import Modelo.Usuarios;

@WebServlet(name = "ServletLogin", urlPatterns = {"/ServletLogin"})
public class ServletLogin extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        // CORREGIDO: Ahora coinciden exactamente con el name del JSP
        String correo = request.getParameter("txtCorreo");
        String contrasena = request.getParameter("txtContrasena");
        
        // Instanciamos el DAO
        UsuariosDAO dao = new UsuariosDAO();
        
        // Validamos el login
        Usuarios user = dao.validarLogin(correo, contrasena);

        if (user != null) {
            HttpSession session = request.getSession();
            session.setAttribute("usuarioLogueado", user);
            response.sendRedirect(request.getContextPath() + "/Vista/Panel.jsp");
        } else {
            request.setAttribute("mensaje", "Credenciales incorrectas.");
            request.getRequestDispatcher("/Login.jsp").forward(request, response);
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