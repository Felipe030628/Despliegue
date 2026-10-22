package Servlet;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.concurrent.CompletableFuture;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import Controlador.CorreoUtil;
import Controlador.UsuariosDAO;
import Modelo.Usuarios;

/**
 * Controlador HTTP para el flujo de recuperación de contraseña de usuarios.
 * Gestiona la generación de tokens de verificación, validación en sesión
 * y actualización segura de credenciales de acceso.
 */
@WebServlet(name = "RecuperarContrasenaCont", urlPatterns = {"/RecuperarContrasenaCont"})
public class RecuperarContrasenaCont extends HttpServlet {

    private final UsuariosDAO usuariosDAO = new UsuariosDAO();
    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("accion");

        if ("verificarCodigo".equals(accion)) {
            verificarCodigo(request, response);
        } else {
            response.sendRedirect("Vista/RecuperarContrasena.jsp");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("accion");

        if ("solicitarCodigo".equals(accion)) {
            solicitarCodigo(request, response);
        } else if ("cambiarContrasena".equals(accion)) {
            cambiarContrasena(request, response);
        } else {
            response.sendRedirect("Vista/RecuperarContrasena.jsp");
        }
    }

    private void solicitarCodigo(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String correo = request.getParameter("txtcorreo");
        HttpSession session = request.getSession();

        if (correo != null && !correo.trim().isEmpty()) {
            correo = correo.trim();
            Usuarios usuario = usuariosDAO.buscarPorEmail(correo);

            if (usuario != null) {
                String codigo = String.format("%06d", secureRandom.nextInt(1_000_000));
                usuariosDAO.actualizarCodigoVerificacion(correo, codigo);

                final String correoDestino = correo;
                CompletableFuture.runAsync(() -> {
                    try {
                        CorreoUtil.enviarCorreoRecuperacion(correoDestino, codigo);
                    } catch (Exception e) {
                        System.err.println("Error al enviar el correo de recuperación: " + e.getMessage());
                    }
                });
            }

            session.setAttribute("correoRecuperar", correo);
        }

        response.sendRedirect("Vista/VerificarCodigoRecuperacion.jsp?status=enviado");
    }

    private void verificarCodigo(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        String correo = (String) session.getAttribute("correoRecuperar");
        String codigoIngresado = request.getParameter("txtcodigo");

        if (correo == null) {
            response.sendRedirect("Vista/RecuperarContrasena.jsp");
            return;
        }

        boolean valido = usuariosDAO.validarCodigoRecuperacion(correo, codigoIngresado);
        if (valido) {
            session.setAttribute("correoResetOk", correo);
            session.removeAttribute("correoRecuperar");
            response.sendRedirect("Vista/NuevaContrasena.jsp");
        } else {
            response.sendRedirect("Vista/VerificarCodigoRecuperacion.jsp?status=error_codigo");
        }
    }

    private void cambiarContrasena(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        String correo = (String) session.getAttribute("correoResetOk");

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

        usuariosDAO.actualizarContrasena(correo, pass);
        session.removeAttribute("correoResetOk");

        response.sendRedirect("Login.jsp?status=pass_actualizada");
    }
}