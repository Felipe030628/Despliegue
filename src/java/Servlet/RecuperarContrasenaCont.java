package Servlet;

import Controlador.UsuariosDAO;
import Controlador.CorreoUtil;
import Modelo.Usuarios;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Controla el flujo de "Olvidé mi contraseña":
 * 1) solicitarCodigo -> genera un código de 6 dígitos y lo envía por correo.
 * 2) verificarCodigo -> valida el código ingresado.
 * 3) cambiarContrasena -> guarda la nueva contraseña, solo si el código ya fue validado en esta sesión.
 */
@WebServlet(name = "RecuperarContrasenaCont", urlPatterns = {"/RecuperarContrasenaCont"})
public class RecuperarContrasenaCont extends HttpServlet {

    UsuariosDAO dao = new UsuariosDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("accion");

        if ("verificarCodigo".equals(accion)) {
            HttpSession session = request.getSession();
            String correo = (String) session.getAttribute("correoRecuperar");
            String codigoIngresado = request.getParameter("txtcodigo");

            if (correo == null) {
                response.sendRedirect("Vista/RecuperarContrasena.jsp");
                return;
            }

            boolean valido = dao.validarCodigoRecuperacion(correo, codigoIngresado);
            if (valido) {
                // Marcamos en sesión que este correo ya pasó la verificación,
                // para que el paso de "cambiar contraseña" pueda confiar en él.
                session.setAttribute("correoResetOk", correo);
                session.removeAttribute("correoRecuperar");
                response.sendRedirect("Vista/NuevaContrasena.jsp");
            } else {
                response.sendRedirect("Vista/VerificarCodigoRecuperacion.jsp?status=error_codigo");
            }
        } else {
            response.sendRedirect("Vista/RecuperarContrasena.jsp");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("accion");

        if ("solicitarCodigo".equals(accion)) {
            String correo = request.getParameter("txtcorreo");
            HttpSession session = request.getSession();

            if (correo != null && !correo.trim().isEmpty()) {
                Usuarios usuario = dao.buscarPorEmail(correo);

                // Por seguridad no revelamos si el correo existe o no en la respuesta:
                // solo generamos y enviamos el código cuando sí hay una cuenta asociada.
                if (usuario != null) {
                    String codigo = String.format("%06d", new java.util.Random().nextInt(999999));
                    dao.actualizarCodigoVerificacion(correo, codigo);

                    new Thread(() -> {
                        try {
                            CorreoUtil.enviarCorreoRecuperacion(correo, codigo);
                        } catch (Exception e) {
                            System.out.println("Aviso: No se pudo enviar el correo de recuperación: " + e.getMessage());
                        }
                    }).start();
                }

                session.setAttribute("correoRecuperar", correo);
            }

            response.sendRedirect("Vista/VerificarCodigoRecuperacion.jsp?status=enviado");

        } else if ("cambiarContrasena".equals(accion)) {
            HttpSession session = request.getSession();
            String correo = (String) session.getAttribute("correoResetOk");

            // Si no hay un correo verificado en sesión, no se puede cambiar la contraseña.
            if (correo == null) {
                response.sendRedirect("Vista/RecuperarContrasena.jsp");
                return;
            }

            String pass = request.getParameter("txtpass");
            String passConfirm = request.getParameter("txtpassConfirm");

            if (pass == null || pass.length() < 8) {
                response.sendRedirect("Vista/NuevaContrasena.jsp?status=error_pass_corta");
                return;
            }
            if (!pass.equals(passConfirm)) {
                response.sendRedirect("Vista/NuevaContrasena.jsp?status=error_pass_no_coincide");
                return;
            }

            dao.actualizarContrasena(correo, pass);
            session.removeAttribute("correoResetOk");

            response.sendRedirect("Login.jsp?status=pass_actualizada");
        }
    }
}
