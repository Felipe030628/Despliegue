package Servlet;

import Controlador.UsuariosDAO; // Asegúrate de importar tu clase DAO
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import Modelo.Usuarios;

public class ServletLogin extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        // Capturamos los campos que vienen del formulario HTML (asegúrate de que los "name" sean correo y password)
        String correo = request.getParameter("correo");
        String contrasena = request.getParameter("password");
        
        // Instanciamos el DAO donde pusimos el método directo
        UsuariosDAO dao = new UsuariosDAO();
        
        // Validamos el login
        Usuarios user = dao.validarLogin(correo, contrasena);

if (user != null) {
    HttpSession session = request.getSession();
    session.setAttribute("usuarioLogueado", user);
    response.sendRedirect(request.getContextPath() + "/Panel.jsp");
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
