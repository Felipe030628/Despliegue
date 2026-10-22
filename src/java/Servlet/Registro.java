package Servlet;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import Controlador.UsuariosDAO;
import Modelo.Usuarios;

/**
 * Controlador HTTP encargado del registro de nuevos usuarios en el sistema.
 * Valida los datos recibidos del formulario y procesa el alta mediante UsuariosDAO.
 */
@WebServlet(name = "Registro", urlPatterns = {"/Registro"})
public class Registro extends HttpServlet {

    private static final int LONGITUD_MIN_DOC = 7;
    private static final int LONGITUD_MAX_DOC = 20;
    private static final int LONGITUD_MIN_PASS = 8;
    private static final String VISTA_REGISTRO = "Registro.jsp";

    private final UsuariosDAO usuariosDAO = new UsuariosDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher(VISTA_REGISTRO).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("accion");

        if (accion != null && accion.equalsIgnoreCase("Registrar")) {
            procesarRegistro(request, response);
        } else {
            request.getRequestDispatcher(VISTA_REGISTRO).forward(request, response);
        }
    }

    private void procesarRegistro(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String nombre = request.getParameter("txtnombre");
            String apellido = request.getParameter("txtapellido");
            String correo = request.getParameter("txtemail");
            String fechaNac = obtenerParametroAlternativo(request, "txtfechaNacimiento", "txtfechaNac");
            String telefono = obtenerParametroAlternativo(request, "txttelefono", "txttel");
            String direccion = obtenerParametroAlternativo(request, "txtdireccion", "txtdir");
            String contrasena = obtenerParametroAlternativo(request, "txtpass", "txtcontrasena");
            String numDoc = obtenerParametroAlternativo(request, "txtnumdoc", "txtnumDocumento");
            String idTipoStr = obtenerParametroAlternativo(request, "txtIdTipoDoc", "txttipoDocumento");
            String idRolStr = request.getParameter("txtrol");

            if (esInvalido(nombre) || esInvalido(apellido) || esInvalido(correo)
                    || esInvalido(fechaNac) || esInvalido(numDoc) || esInvalido(contrasena)
                    || esInvalido(idTipoStr) || esInvalido(idRolStr)) {

                redireccionarConMensaje(request, response, "Datos incorrectos: Todos los campos obligatorios deben ser diligenciados.");
                return;
            }

            if (numDoc.length() < LONGITUD_MIN_DOC || numDoc.length() > LONGITUD_MAX_DOC) {
                redireccionarConMensaje(request, response, "Formato incorrecto: El documento debe tener entre " 
                        + LONGITUD_MIN_DOC + " y " + LONGITUD_MAX_DOC + " dígitos.");
                return;
            }

            if (contrasena.length() < LONGITUD_MIN_PASS) {
                redireccionarConMensaje(request, response, "La contraseña debe tener al menos " 
                        + LONGITUD_MIN_PASS + " caracteres.");
                return;
            }

            Usuarios usuario = new Usuarios();
            usuario.setNombre(nombre.trim());
            usuario.setApellido(apellido.trim());
            usuario.setCorreo(correo.trim());
            usuario.setFecha_nacimiento(fechaNac);
            usuario.setIdTipoDocumento(Integer.parseInt(idTipoStr));
            usuario.setNombre_documento(numDoc.trim());
            usuario.setTelefono(telefono != null ? telefono.trim() : null);
            usuario.setDireccion(direccion != null ? direccion.trim() : null);
            usuario.setIdRol(Integer.parseInt(idRolStr));
            usuario.setContrasena(contrasena);

            int res = usuariosDAO.registrar(usuario);

            if (res > 0) {
                request.setAttribute("mensajeExito", "¡Usuario registrado con éxito! Ya puedes iniciar sesión.");
            } else {
                request.setAttribute("mensaje", "No se pudo completar el registro. Compruebe que los datos no existan en el sistema.");
            }
            request.getRequestDispatcher(VISTA_REGISTRO).forward(request, response);

        } catch (NumberFormatException e) {
            redireccionarConMensaje(request, response, "Error de formato: El tipo de documento o rol seleccionados no son válidos.");
        } catch (Exception e) {
            String errorMsg = e.getMessage();
            if (errorMsg != null && errorMsg.contains("Duplicate entry")) {
                redireccionarConMensaje(request, response, "Error de registro: El número de documento o correo electrónico ya se encuentra registrado.");
            } else {
                redireccionarConMensaje(request, response, "Error en el sistema: " + errorMsg);
            }
        }
    }

    private String obtenerParametroAlternativo(HttpServletRequest request, String primario, String secundario) {
        String valor = request.getParameter(primario);
        return valor != null ? valor : request.getParameter(secundario);
    }

    private boolean esInvalido(String valor) {
        return valor == null || valor.trim().isEmpty();
    }

    private void redireccionarConMensaje(HttpServletRequest request, HttpServletResponse response, String mensaje)
            throws ServletException, IOException {
        request.setAttribute("mensaje", mensaje);
        request.getRequestDispatcher(VISTA_REGISTRO).forward(request, response);
    }
}