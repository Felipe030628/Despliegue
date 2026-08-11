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
        
        // Obtenemos los datos del formulario de login
        String correo = request.getParameter("txtCorreo");
        String contrasena = request.getParameter("txtContrasena");
        
        // Instanciamos el DAO
        UsuariosDAO dao = new UsuariosDAO();
        
        // Validamos el login mediante correo y contraseña
        Usuarios user = dao.validarLogin(correo, contrasena);

        if (user != null) {
            // Verificamos si el usuario está activo (asumiendo que getEstado() o getActivo() retorna 1 o true)
            // Ajusta "user.getActivo() == 1" según como tengas definido tu campo en el Modelo
            if (user.getActivo() == 1) { 
                HttpSession session = request.getSession();
                session.setAttribute("usuarioLogueado", user);
                response.sendRedirect(request.getContextPath() + "/Vista/Panel.jsp");
            } else {
                // Si está inactivo, bloqueamos el acceso y mandamos mensaje
                request.setAttribute("mensaje", "Su cuenta se encuentra inactiva. Contacte al administrador.");
                request.getRequestDispatcher("/Login.jsp").forward(request, response);
            }
        } else {
            // Credenciales incorrectas
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