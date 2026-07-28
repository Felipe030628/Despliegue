package Servlet;

import Controlador.UsuariosDAO; // Asegúrate de importar tu clase DAO
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class ServletLogin extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        // Capturamos los campos que vienen del formulario HTML (asegúrate de que los "name" sean correo y password)
        String correo = request.getParameter("correo");
        String password = request.getParameter("password");
        
        // Instanciamos el DAO donde pusimos el método directo
        UsuariosDAO dao = new UsuariosDAO();
        
        // Validamos el login
        boolean accesoValido = dao.validarLogin(correo, password);
        
        if (accesoValido) {
            // Si las credenciales son correctas, creamos sesión y redirigimos a la página principal
            HttpSession session = request.getSession();
            session.setAttribute("correo", correo);
            response.sendRedirect("principal.jsp"); // Cambia por tu vista principal o menú
        } else {
            // Si falla, redirigimos de vuelta al login con error (o manejas tu atributo de error)
            request.setAttribute("error", "Credenciales incorrectas.");
            request.getRequestDispatcher("login.jsp").forward(request, response); // Cambia por tu vista de login
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