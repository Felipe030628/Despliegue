package Servlet;

import Controlador.UsuariosDAO;
import Modelo.Usuarios;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;

@WebServlet(name = "ServletLogin", urlPatterns = {"/ServletLogin"})
public class ServletLogin extends HttpServlet {
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
                response.sendRedirect("Vista/Panel.jsp");
            } else {
                request.setAttribute("mensaje", "Credenciales incorrectas.");
                request.getRequestDispatcher("Login.jsp").forward(request, response);
            }
        }
    }
}