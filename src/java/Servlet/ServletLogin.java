package Servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import Controlador.UsuariosDAO;
import Modelo.Usuarios;

/**
 * Controlador HTTP para el proceso de autenticación de usuarios.
 * Gestiona la validación de credenciales, control de estado de cuenta,
 * prevención de fijación de sesión y establecimiento de la sesión de usuario.
 */
@WebServlet(name = "ServletLogin", urlPatterns = {"/ServletLogin"})
public class ServletLogin extends HttpServlet {

    private static final int TIEMPO_INACTIVIDAD_SEGUNDOS = 30 * 60;
    private static final String VISTA_LOGIN = "/Login.jsp";
    private static final String VISTA_PANEL = "/Vista/Panel.jsp";

    private final UsuariosDAO usuariosDAO = new UsuariosDAO();

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String correo = request.getParameter("txtCorreo");
        String contrasena = request.getParameter("txtContrasena");

        if (correo == null || contrasena == null || correo.trim().isEmpty() || contrasena.trim().isEmpty()) {
            redireccionarConMensaje(request, response, "Debe ingresar el correo y la contraseña.");
            return;
        }

        Usuarios user = usuariosDAO.validarLogin(correo.trim(), contrasena);

        if (user == null) {
            redireccionarConMensaje(request, response, "Credenciales incorrectas.");
            return;
        }

        if (user.getActivo() != 1) {
            redireccionarConMensaje(request, response, "Su cuenta se encuentra inactiva. Contacte al administrador.");
            return;
        }

        iniciarSesionUsuario(request, response, user);
    }

    private void iniciarSesionUsuario(HttpServletRequest request, HttpServletResponse response, Usuarios user)
            throws IOException {

        HttpSession sesionAnterior = request.getSession(false);
        if (sesionAnterior != null) {
            sesionAnterior.invalidate();
        }

        HttpSession nuevaSesion = request.getSession(true);
        nuevaSesion.setAttribute("usuarioLogueado", user);
        nuevaSesion.setMaxInactiveInterval(TIEMPO_INACTIVIDAD_SEGUNDOS);

        response.sendRedirect(request.getContextPath() + VISTA_PANEL);
    }

    private void redireccionarConMensaje(HttpServletRequest request, HttpServletResponse response, String mensaje)
            throws ServletException, IOException {
        request.setAttribute("mensaje", mensaje);
        request.getRequestDispatcher(VISTA_LOGIN).forward(request, response);
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