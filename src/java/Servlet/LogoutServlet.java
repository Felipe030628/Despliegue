package Servlet;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Servlet encargado de gestionar el cierre de sesión seguro del usuario.
 * Invalida la sesión actual, elimina las cookies de autenticación de la sesión
 * y aplica políticas de restricción de almacenamiento en caché.
 */
@WebServlet(name = "LogoutServlet", urlPatterns = {"/Logout"})
public class LogoutServlet extends HttpServlet {

    private void cerrarSesion(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        Cookie cookie = new Cookie("JSESSIONID", "");
        String contextPath = request.getContextPath();
        cookie.setPath(contextPath.isEmpty() ? "/" : contextPath);
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        cookie.setSecure(request.isSecure());
        response.addCookie(cookie);

        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        response.sendRedirect(contextPath + "/Login.jsp?status=sesion_cerrada");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        cerrarSesion(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        cerrarSesion(request, response);
    }
}