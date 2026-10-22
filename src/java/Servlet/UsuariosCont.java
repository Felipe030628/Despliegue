package Servlet;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import Controlador.CorreoUtil;
import Controlador.UsuariosDAO;
import Modelo.TiposDocumentos;
import Modelo.Usuarios;
import Util.JsonUtil;

/**
 * Controlador de peticiones HTTP para la gestión integral de usuarios y empleados.
 * Atiende flujos de navegación tradicionales (JSP) y endpoints REST en JSON para la aplicación móvil / web.
 */
@WebServlet(name = "UsuariosCont", urlPatterns = {"/UsuariosCont"})
public class UsuariosCont extends HttpServlet {

    private final UsuariosDAO usuariosDAO = new UsuariosDAO();
    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("accion");

        if (accion == null) {
            response.sendRedirect("Vista/Panel.jsp");
            return;
        }

        switch (accion) {
            case "listar":
                listarUsuariosVista(request, response);
                break;

            case "cargar":
                cargarUsuarioEdicion(request, response);
                break;

            case "eliminar":
                eliminarUsuarioVista(request, response);
                break;

            case "cambiarEstado":
                cambiarEstadoVista(request, response);
                break;

            case "verificarCodigo":
                verificarCodigoVista(request, response);
                break;

            case "loginJson":
                loginJson(request, response);
                break;

            case "tiposDocumentoJson":
                tiposDocumentoJson(response);
                break;

            case "verificarCodigoJson":
                verificarCodigoJson(request, response);
                break;

            case "listarJson":
                listarUsuariosJson(response);
                break;

            case "eliminarJson":
                eliminarUsuarioJson(request, response);
                break;

            case "cambiarEstadoJson":
                cambiarEstadoJson(request, response);
                break;

            default:
                response.sendRedirect("Vista/Panel.jsp");
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("accion");

        if (accion == null) {
            response.sendRedirect("Vista/Panel.jsp");
            return;
        }

        if (accion.equalsIgnoreCase("Registrar")) {
            registrarUsuarioVista(request, response);
        } else if (accion.equalsIgnoreCase("registrarJson")) {
            registrarUsuarioJson(request, response);
        } else if (accion.equalsIgnoreCase("actualizar")) {
            actualizarUsuarioVista(request, response);
        } else if (accion.equalsIgnoreCase("actualizarJson")) {
            actualizarUsuarioJson(request, response);
        } else {
            response.sendRedirect("Vista/Panel.jsp");
        }
    }

    // --------------------------------------------------------------------------------
    // MÉTODOS DOGET: VISTAS JSP Y ACCIONES JSON
    // --------------------------------------------------------------------------------

    private void listarUsuariosVista(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Usuarios> lista = usuariosDAO.listarUsuarios();
        request.setAttribute("listaUsuarios", lista);
        request.getRequestDispatcher("Vista/Empleados.jsp").forward(request, response);
    }

    private void cargarUsuarioEdicion(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            Usuarios usuario = usuariosDAO.listarPorId(id);
            List<TiposDocumentos> listaTiposDoc = usuariosDAO.listarTiposDocumentoMayores();

            request.setAttribute("usuario", usuario);
            request.setAttribute("listaTiposDoc", listaTiposDoc);
            request.getRequestDispatcher("Vista/EditarEmpleado.jsp").forward(request, response);
        } catch (Exception e) {
            response.sendRedirect("UsuariosCont?accion=listar");
        }
    }

    private void eliminarUsuarioVista(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int idEliminar = Integer.parseInt(request.getParameter("id"));
            usuariosDAO.eliminar(idEliminar);
        } catch (Exception e) {
            System.err.println("Error al eliminar usuario: " + e.getMessage());
        }
        response.sendRedirect("UsuariosCont?accion=listar");
    }

    private void cambiarEstadoVista(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int idEstado = Integer.parseInt(request.getParameter("id"));
            int nuevoEstado = Integer.parseInt(request.getParameter("activo"));
            usuariosDAO.cambiarEstado(idEstado, nuevoEstado);
        } catch (Exception e) {
            System.err.println("Error al cambiar estado de usuario: " + e.getMessage());
        }
        response.sendRedirect("UsuariosCont?accion=listar");
    }

    private void verificarCodigoVista(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String correoV = (String) request.getSession().getAttribute("correoVerificar");
        String codigoIngresado = request.getParameter("txtcodigo");

        boolean valido = usuariosDAO.validarYActivarCuenta(correoV, codigoIngresado);
        if (valido) {
            request.getSession().removeAttribute("correoVerificar");
            response.sendRedirect("Login.jsp?status=verificado");
        } else {
            response.sendRedirect("Vista/VerificarCodigo.jsp?status=error_codigo");
        }
    }

    private void loginJson(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        try {
            String correoLogin = request.getParameter("correo");
            String contrasenaLogin = request.getParameter("contrasena");

            Usuarios userLogin = usuariosDAO.validarLogin(correoLogin, contrasenaLogin);
            if (userLogin == null) {
                response.getWriter().print(jsonError("Correo o contraseña incorrectos"));
            } else if (userLogin.getActivo() != 1) {
                response.getWriter().print(jsonError("Tu cuenta aún no está verificada. Revisa tu correo."));
            } else {
                response.getWriter().print("{\"success\":true,\"usuario\":" + jsonUsuario(userLogin) + "}");
            }
        } catch (Exception e) {
            response.getWriter().print(jsonError("Error en el servidor al iniciar sesión: " + e.getMessage()));
        }
    }

    private void tiposDocumentoJson(HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        try {
            List<TiposDocumentos> tipos = usuariosDAO.listarTiposDocumentoMayores();
            String jsonArray = tipos.stream()
                    .map(t -> String.format("{\"id\":%d,\"nombre\":%s}", t.getIdTipoDocumento(), JsonUtil.str(t.getNombre_documento())))
                    .collect(Collectors.joining(","));

            response.getWriter().print("[" + jsonArray + "]");
        } catch (Exception e) {
            response.getWriter().print("[]");
        }
    }

    private void verificarCodigoJson(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        try {
            String correoVer = request.getParameter("correo");
            String codigoVer = request.getParameter("codigo");

            boolean validoJson = usuariosDAO.validarYActivarCuenta(correoVer, codigoVer);
            if (validoJson) {
                Usuarios userVer = usuariosDAO.buscarPorEmail(correoVer);
                response.getWriter().print("{\"success\":true,\"usuario\":" + jsonUsuario(userVer) + "}");
            } else {
                response.getWriter().print(jsonError("Código incorrecto o expirado"));
            }
        } catch (Exception e) {
            response.getWriter().print(jsonError("Error al verificar código: " + e.getMessage()));
        }
    }

    private void listarUsuariosJson(HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        try {
            List<Usuarios> listaJson = usuariosDAO.listarUsuarios();
            String jsonArray = (listaJson != null)
                    ? listaJson.stream().map(this::jsonUsuario).collect(Collectors.joining(","))
                    : "";

            response.getWriter().print("[" + jsonArray + "]");
        } catch (Exception e) {
            response.getWriter().print("[]");
        }
    }

    private void eliminarUsuarioJson(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        try {
            int idElimJson = Integer.parseInt(request.getParameter("id"));
            usuariosDAO.eliminar(idElimJson);
            response.getWriter().print("{\"success\":true}");
        } catch (Exception e) {
            response.getWriter().print("{\"success\":false,\"error\":" + JsonUtil.str(e.getMessage()) + "}");
        }
    }

    private void cambiarEstadoJson(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        try {
            int idEstadoJson = Integer.parseInt(request.getParameter("id"));
            int nuevoEstadoJson = Integer.parseInt(request.getParameter("activo"));
            boolean ok = usuariosDAO.cambiarEstado(idEstadoJson, nuevoEstadoJson);

            if (ok) {
                response.getWriter().print("{\"success\":true}");
            } else {
                response.getWriter().print(jsonError("No se pudo cambiar el estado del empleado."));
            }
        } catch (Exception e) {
            response.getWriter().print("{\"success\":false,\"error\":" + JsonUtil.str(e.getMessage()) + "}");
        }
    }

    // --------------------------------------------------------------------------------
    // MÉTODOS DOPOST: REGISTRO Y ACTUALIZACIÓN
    // --------------------------------------------------------------------------------

    private void registrarUsuarioVista(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String nombre = request.getParameter("txtnombre");
            String apellido = request.getParameter("txtapellido");
            String correo = request.getParameter("txtemail");
            String fechaNac = request.getParameter("txtfechaNac");
            String numDoc = request.getParameter("txtnumdoc");
            String telefono = request.getParameter("txttel");
            String direccion = request.getParameter("txtdireccion");
            String contrasena = request.getParameter("txtpass");
            String contrasenaConfirm = request.getParameter("txtpassConfirm");
            String terminos = request.getParameter("txtterminos");

            if (contrasena == null || contrasena.length() < 8) {
                response.sendRedirect("Registro.jsp?status=error_pass_corta");
                return;
            }
            if (contrasenaConfirm == null || !contrasena.equals(contrasenaConfirm)) {
                response.sendRedirect("Registro.jsp?status=error_pass_no_coincide");
                return;
            }
            if (terminos == null) {
                response.sendRedirect("Registro.jsp?status=error_terminos");
                return;
            }

            int idTipoDocumento = Integer.parseInt(request.getParameter("txtIdTipoDoc"));
            int idRol = 1;

            Usuarios nuevo = new Usuarios();
            nuevo.setNombre(nombre);
            nuevo.setApellido(apellido);
            nuevo.setCorreo(correo);
            nuevo.setFecha_nacimiento(fechaNac);
            nuevo.setIdTipoDocumento(idTipoDocumento);
            nuevo.setNombre_documento(numDoc);
            nuevo.setTelefono(telefono);
            nuevo.setDireccion(direccion);
            nuevo.setIdRol(idRol);
            nuevo.setContrasena(contrasena);

            int res = usuariosDAO.registrar(nuevo);

            if (res > 0) {
                String codigoVerificacion = String.format("%06d", secureRandom.nextInt(1_000_000));
                usuariosDAO.actualizarCodigoVerificacion(correo, codigoVerificacion);

                request.getSession().setAttribute("correoVerificar", correo);

                final String correoDestino = correo;
                CompletableFuture.runAsync(() -> {
                    try {
                        CorreoUtil.enviarCorreo(correoDestino, codigoVerificacion);
                    } catch (Exception e) {
                        System.err.println("Error enviando correo de confirmación: " + e.getMessage());
                    }
                });

                response.sendRedirect("Vista/VerificarCodigo.jsp?status=enviado");
            } else {
                response.sendRedirect("Registro.jsp?status=error");
            }
        } catch (Exception e) {
            response.sendRedirect("Registro.jsp?status=error_sistema");
        }
    }

    private void registrarUsuarioJson(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        try {
            String nombre = request.getParameter("nombre");
            String apellido = request.getParameter("apellido");
            String correo = request.getParameter("correo");
            String fechaNac = request.getParameter("fechaNacimiento");
            String numDoc = request.getParameter("numeroDocumento");
            String idTipoStr = request.getParameter("idTipoDocumento");
            String telefono = request.getParameter("telefono");
            String direccion = request.getParameter("direccion");
            String contrasena = request.getParameter("contrasena");

            if (esInvalido(nombre) || esInvalido(apellido) || esInvalido(correo)
                    || esInvalido(fechaNac) || esInvalido(numDoc)
                    || esInvalido(idTipoStr) || contrasena == null || contrasena.length() < 8) {

                response.getWriter().print(jsonError("Completa todos los campos obligatorios. La contraseña debe tener al menos 8 caracteres."));
                return;
            }

            if (numDoc.length() < 7 || numDoc.length() > 20) {
                response.getWriter().print(jsonError("El número de documento debe tener entre 7 y 20 caracteres."));
                return;
            }

            Usuarios nuevo = new Usuarios();
            nuevo.setNombre(nombre.trim());
            nuevo.setApellido(apellido.trim());
            nuevo.setCorreo(correo.trim());
            nuevo.setFecha_nacimiento(fechaNac);
            nuevo.setIdTipoDocumento(Integer.parseInt(idTipoStr));
            nuevo.setNombre_documento(numDoc.trim());
            nuevo.setTelefono(telefono);
            nuevo.setDireccion(direccion);
            nuevo.setIdRol(1);
            nuevo.setContrasena(contrasena);

            int res = usuariosDAO.registrar(nuevo);

            if (res > 0) {
                String codigoVerificacion = String.format("%06d", secureRandom.nextInt(1_000_000));
                usuariosDAO.actualizarCodigoVerificacion(correo, codigoVerificacion);

                final String correoDestino = correo;
                CompletableFuture.runAsync(() -> {
                    try {
                        CorreoUtil.enviarCorreo(correoDestino, codigoVerificacion);
                    } catch (Exception e) {
                        System.err.println("Error enviando correo de confirmación: " + e.getMessage());
                    }
                });

                response.getWriter().print("{\"success\":true,\"correo\":" + JsonUtil.str(correo) + ",\"mensaje\":\"Te enviamos un código de verificación a tu correo\"}");
            } else {
                response.getWriter().print(jsonError("No se pudo completar el registro. Verifica que el correo o documento no estén ya registrados."));
            }
        } catch (NumberFormatException e) {
            response.getWriter().print(jsonError("El tipo de documento seleccionado no es válido."));
        } catch (Exception e) {
            String errorMsg = e.getMessage();
            if (errorMsg != null && errorMsg.contains("Duplicate entry")) {
                response.getWriter().print(jsonError("El correo o número de documento ya están registrados."));
            } else {
                response.getWriter().print(jsonError("Error en el sistema al registrar."));
            }
        }
    }

    private void actualizarUsuarioVista(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int idUsuarios = Integer.parseInt(request.getParameter("idUsuarios"));
            String nombre = request.getParameter("nombre");
            String apellido = request.getParameter("apellido");
            String telefono = request.getParameter("telefono");
            String direccion = request.getParameter("direccion");

            Usuarios usuAct = new Usuarios();
            usuAct.setIdUsuarios(idUsuarios);
            usuAct.setNombre(nombre);
            usuAct.setApellido(apellido);
            usuAct.setTelefono(telefono);
            usuAct.setDireccion(direccion);

            usuariosDAO.actualizarDatosBasicos(usuAct);
            response.sendRedirect("UsuariosCont?accion=listar");
        } catch (Exception e) {
            response.sendRedirect("UsuariosCont?accion=listar&status=error_actualizar");
        }
    }

    private void actualizarUsuarioJson(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        try {
            int idUsuariosJson = Integer.parseInt(request.getParameter("idUsuarios"));
            String nombreJson = request.getParameter("nombre");
            String apellidoJson = request.getParameter("apellido");
            String telefonoJson = request.getParameter("telefono");
            String direccionJson = request.getParameter("direccion");

            if (esInvalido(nombreJson) || esInvalido(apellidoJson)) {
                response.getWriter().print(jsonError("Nombre y apellido son obligatorios."));
                return;
            }

            Usuarios usuActJson = new Usuarios();
            usuActJson.setIdUsuarios(idUsuariosJson);
            usuActJson.setNombre(nombreJson.trim());
            usuActJson.setApellido(apellidoJson.trim());
            usuActJson.setTelefono(telefonoJson);
            usuActJson.setDireccion(direccionJson);

            usuariosDAO.actualizarDatosBasicos(usuActJson);

            Usuarios actualizado = usuariosDAO.listarPorId(idUsuariosJson);
            response.getWriter().print("{\"success\":true,\"usuario\":" + jsonUsuario(actualizado) + "}");
        } catch (Exception e) {
            response.getWriter().print("{\"success\":false,\"error\":" + JsonUtil.str(e.getMessage()) + "}");
        }
    }

    // --------------------------------------------------------------------------------
    // HELPERS PRIVADOS
    // --------------------------------------------------------------------------------

    private boolean esInvalido(String valor) {
        return valor == null || valor.trim().isEmpty();
    }

    private String jsonError(String mensaje) {
        return "{\"success\":false,\"error\":" + JsonUtil.str(mensaje) + "}";
    }

    private String jsonUsuario(Usuarios u) {
        return String.format(
            "{\"idUsuarios\":%d,\"nombre\":%s,\"apellido\":%s,\"correo\":%s,\"telefono\":%s,\"direccion\":%s,\"fecha_nacimiento\":%s,\"idTipoDocumento\":%d,\"nombre_documento\":%s,\"idRol\":%d,\"activo\":%d}",
            u.getIdUsuarios(),
            JsonUtil.str(u.getNombre()),
            JsonUtil.str(u.getApellido()),
            JsonUtil.str(u.getCorreo()),
            JsonUtil.str(u.getTelefono()),
            JsonUtil.str(u.getDireccion()),
            JsonUtil.str(u.getFecha_nacimiento()),
            u.getIdTipoDocumento(),
            JsonUtil.str(u.getNombre_documento()),
            u.getIdRol(),
            u.getActivo()
        );
    }
}