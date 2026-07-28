package Servlet;

import Controlador.UsuariosDAO;
import Modelo.Usuarios;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "ServletLogin", urlPatterns = {"/ServletLogin"})
public class ServletLogin extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String accion = request.getParameter("accion");
        if ("ingresar".equals(accion)) {
            String correo = request.getParameter("txtCorreo");
            String pass = request.getParameter("txtContrasena");

            UsuariosDAO dao = new UsuariosDAO();
            Usuarios user = dao.validarLogin(correo, pass);

            if (user != null) {
                HttpSession session = request.getSession();
                session.setAttribute("usuarioLogueado", user);
                response.sendRedirect(request.getContextPath() + "/Panel.jsp");
            } else {
                request.setAttribute("mensaje", "Credenciales incorrectas.");
                request.getRequestDispatcher("/Login.jsp").forward(request, response);
            }
        }
    }
}