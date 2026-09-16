package Servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Cierra la sesión del usuario de forma segura:
 *  - Invalida la sesión en el servidor (session.invalidate()), que es lo que
 *    realmente hace que, aunque el navegador vuelva a pedir la página (por
 *    ejemplo al pulsar "atrás"), el AuthFilter ya no encuentre ningún
 *    usuario logueado y lo redirija de nuevo al Login.
 *  - Borra explícitamente la cookie de sesión (JSESSIONID) en el navegador.
 *  - Añade cabeceras "no-cache" también aquí, por si el propio navegador
 *    intentase reutilizar esta respuesta.
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
        String path = request.getContextPath();
        cookie.setPath(path.isEmpty() ? "/" : path);
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);

        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        response.sendRedirect(request.getContextPath() + "/Login.jsp?status=sesion_cerrada");
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
