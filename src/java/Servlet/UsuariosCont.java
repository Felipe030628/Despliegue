package Servlet;

import Controlador.UsuariosDAO;
import Modelo.Usuarios;
import Modelo.TiposDocumentos;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import Controlador.CorreoUtil;

@WebServlet(name = "UsuariosCont", urlPatterns = {"/UsuariosCont"})
public class UsuariosCont extends HttpServlet {

    UsuariosDAO dao = new UsuariosDAO();
    Usuarios u = new Usuarios();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String accion = request.getParameter("accion");

        if (accion != null) {
            switch (accion) {
                case "listar":
                    List<Usuarios> lista = dao.listarUsuarios();
                    request.setAttribute("listaUsuarios", lista);
                    request.getRequestDispatcher("Vista/Empleados.jsp").forward(request, response);
                    break;
                    
                case "cargar":
                    int id = Integer.parseInt(request.getParameter("id"));
                    Usuarios usuario = dao.listarPorId(id);
                    List<TiposDocumentos> listaTiposDoc = dao.listarTiposDocumentoMayores(); 
                    
                    request.setAttribute("usuario", usuario);
                    request.setAttribute("listaTiposDoc", listaTiposDoc); 
                    request.getRequestDispatcher("Vista/EditarEmpleado.jsp").forward(request, response);
                    break;
                    
                case "eliminar":
                    int idEliminar = Integer.parseInt(request.getParameter("id"));
                    dao.eliminar(idEliminar);
                    response.sendRedirect("UsuariosCont?accion=listar");
                    break;
                    
                case "cambiarEstado":
                    int idEstado = Integer.parseInt(request.getParameter("id"));
                    int nuevoEstado = Integer.parseInt(request.getParameter("activo"));
                    dao.cambiarEstado(idEstado, nuevoEstado);
                    response.sendRedirect("UsuariosCont?accion=listar");
                    break;
                    
                case "verificarCodigo":
                    String correoV = (String) request.getSession().getAttribute("correoVerificar");
                    String codigoIngresado = request.getParameter("txtcodigo");
                    
                    boolean valido = dao.validarYActivarCuenta(correoV, codigoIngresado);
                    if (valido) {
                        request.getSession().removeAttribute("correoVerificar");
                        response.sendRedirect("Login.jsp?status=verificado");
                    } else {
                        response.sendRedirect("Vista/VerificarCodigo.jsp?status=error_codigo");
                    }
                    break;

                // ---------- Acciones JSON para la app Flutter ----------

                case "loginJson":
                    response.setContentType("application/json;charset=UTF-8");
                    String correoLogin = request.getParameter("correo");
                    String contrasenaLogin = request.getParameter("contrasena");

                    Usuarios userLogin = dao.validarLogin(correoLogin, contrasenaLogin);
                    if (userLogin == null) {
                        response.getWriter().print(jsonError("Correo o contraseña incorrectos"));
                    } else if (userLogin.getActivo() != 1) {
                        response.getWriter().print(jsonError("Tu cuenta aún no está verificada. Revisa tu correo."));
                    } else {
                        response.getWriter().print("{\"success\":true,\"usuario\":" + jsonUsuario(userLogin) + "}");
                    }
                    break;

                case "tiposDocumentoJson":
                    response.setContentType("application/json;charset=UTF-8");
                    List<TiposDocumentos> tipos = dao.listarTiposDocumentoMayores();
                    StringBuilder sbTipos = new StringBuilder("[");
                    for (int i = 0; i < tipos.size(); i++) {
                        TiposDocumentos t = tipos.get(i);
                        if (i > 0) sbTipos.append(",");
                        sbTipos.append("{\"id\":").append(t.getIdTipoDocumento())
                               .append(",\"nombre\":\"").append(escaparJson(t.getNombre_documento())).append("\"}");
                    }
                    sbTipos.append("]");
                    response.getWriter().print(sbTipos.toString());
                    break;

                case "verificarCodigoJson":
                    response.setContentType("application/json;charset=UTF-8");
                    String correoVer = request.getParameter("correo");
                    String codigoVer = request.getParameter("codigo");

                    boolean validoJson = dao.validarYActivarCuenta(correoVer, codigoVer);
                    if (validoJson) {
                        Usuarios userVer = dao.buscarPorEmail(correoVer);
                        response.getWriter().print("{\"success\":true,\"usuario\":" + jsonUsuario(userVer) + "}");
                    } else {
                        response.getWriter().print(jsonError("Código incorrecto o expirado"));
                    }
                    break;
            }
        } else {
            response.sendRedirect("Vista/Panel.jsp");
        }
    }

    // ---------- Helpers JSON (usados por las acciones *Json) ----------

    private String jsonError(String mensaje) {
        return "{\"success\":false,\"error\":\"" + escaparJson(mensaje) + "\"}";
    }

    private String jsonUsuario(Usuarios u) {
        return "{"
                + "\"idUsuarios\":" + u.getIdUsuarios() + ","
                + "\"nombre\":\"" + escaparJson(u.getNombre()) + "\","
                + "\"apellido\":\"" + escaparJson(u.getApellido()) + "\","
                + "\"correo\":\"" + escaparJson(u.getCorreo()) + "\","
                + "\"telefono\":\"" + escaparJson(u.getTelefono()) + "\","
                + "\"direccion\":\"" + escaparJson(u.getDireccion()) + "\""
                + "}";
    }

    private String escaparJson(String texto) {
        if (texto == null) return "";
        return texto.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String accion = request.getParameter("accion");

        if (accion != null) {
            if (accion.equalsIgnoreCase("Registrar")) {
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

                    // Validación de contraseña y aceptación de términos (defensa en servidor)
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
                    // Rol fijo: esta aplicación es de uso exclusivamente administrativo.
                    int idRol = 1;
                    
                    u.setNombre(nombre);
                    u.setApellido(apellido);
                    u.setCorreo(correo);
                    u.setFecha_nacimiento(fechaNac);
                    u.setIdTipoDocumento(idTipoDocumento);
                    u.setNombre_documento(numDoc);
                    u.setTelefono(telefono);
                    u.setDireccion(direccion);
                    u.setIdRol(idRol);
                    u.setContrasena(contrasena);
                    
                    int res = dao.registrar(u);
                    
                    if (res > 0) {
                        // 1. Generar un código aleatorio de 6 dígitos
                        String codigoVerificacion = String.format("%06d", new java.util.Random().nextInt(999999));
                        
                        // 2. Guardarlo en la BD asociado al correo
                        dao.actualizarCodigoVerificacion(correo, codigoVerificacion);
                        
                        // 3. Guardar el correo en sesión y enviar el correo en un hilo independiente (evita congelamientos)
                        request.getSession().setAttribute("correoVerificar", correo);
                        
                        new Thread(() -> {
                            try {
                                CorreoUtil.enviarCorreo(correo, codigoVerificacion);
                            } catch (Exception e) {
                                System.out.println("Aviso: No se pudo enviar el correo de fondo: " + e.getMessage());
                            }
                        }).start();
                        
                        // 4. Redirigir de inmediato a la vista de verificación sin quedarse bloqueado
                        response.sendRedirect("Vista/VerificarCodigo.jsp?status=enviado");
                        
                    } else {
                        response.sendRedirect("Registro.jsp?status=error");
                    }
                } catch (Exception e) {
                    System.out.println("❌ Error en doPost Registrar: " + e.getMessage());
                    response.sendRedirect("Registro.jsp?status=error_sistema");
                }
                
            } else if (accion.equalsIgnoreCase("registrarJson")) {
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

                    if (nombre == null || nombre.trim().isEmpty()
                            || apellido == null || apellido.trim().isEmpty()
                            || correo == null || correo.trim().isEmpty()
                            || fechaNac == null || fechaNac.trim().isEmpty()
                            || numDoc == null || numDoc.trim().isEmpty()
                            || idTipoStr == null || idTipoStr.trim().isEmpty()
                            || contrasena == null || contrasena.length() < 8) {
                        response.getWriter().print(jsonError(
                                "Completa todos los campos obligatorios. La contraseña debe tener al menos 8 caracteres."));
                        return;
                    }
                    if (numDoc.length() < 7 || numDoc.length() > 20) {
                        response.getWriter().print(jsonError("El número de documento debe tener entre 7 y 20 caracteres."));
                        return;
                    }

                    Usuarios nuevo = new Usuarios();
                    nuevo.setNombre(nombre);
                    nuevo.setApellido(apellido);
                    nuevo.setCorreo(correo);
                    nuevo.setFecha_nacimiento(fechaNac);
                    nuevo.setIdTipoDocumento(Integer.parseInt(idTipoStr));
                    nuevo.setNombre_documento(numDoc);
                    nuevo.setTelefono(telefono);
                    nuevo.setDireccion(direccion);
                    nuevo.setIdRol(1); // Rol fijo: app de uso administrativo.
                    nuevo.setContrasena(contrasena);

                    int res = dao.registrar(nuevo);

                    if (res > 0) {
                        String codigoVerificacion = String.format("%06d", new java.util.Random().nextInt(999999));
                        dao.actualizarCodigoVerificacion(correo, codigoVerificacion);

                        new Thread(() -> {
                            try {
                                CorreoUtil.enviarCorreo(correo, codigoVerificacion);
                            } catch (Exception e) {
                                System.out.println("Aviso: No se pudo enviar el correo de fondo: " + e.getMessage());
                            }
                        }).start();

                        response.getWriter().print(
                                "{\"success\":true,\"correo\":\"" + escaparJson(correo)
                                        + "\",\"mensaje\":\"Te enviamos un código de verificación a tu correo\"}");
                    } else {
                        response.getWriter().print(jsonError(
                                "No se pudo completar el registro. Verifica que el correo o documento no estén ya registrados."));
                    }
                } catch (NumberFormatException e) {
                    response.getWriter().print(jsonError("El tipo de documento seleccionado no es válido."));
                } catch (Exception e) {
                    String errorMsg = e.getMessage();
                    if (errorMsg != null && errorMsg.contains("Duplicate entry")) {
                        response.getWriter().print(jsonError("El correo o número de documento ya están registrados."));
                    } else {
                        System.out.println("❌ Error en doPost RegistrarJson: " + errorMsg);
                        response.getWriter().print(jsonError("Error en el sistema al registrar."));
                    }
                }

            } else if (accion.equalsIgnoreCase("actualizar")) {
                try {
                    int idUsuarios = Integer.parseInt(request.getParameter("idUsuarios"));
                    String nombre = request.getParameter("nombre");
                    String apellido = request.getParameter("apellido");
                    String telefono = request.getParameter("telefono");
                    String direccion = request.getParameter("direccion");

                    // Nota de seguridad: correo, contraseña y documento son datos esenciales
                    // y NO se actualizan aquí, aunque el cliente los envíe en el formulario.
                    // - correo/documento: identifican al usuario y su acceso al sistema.
                    // - contraseña: solo se cambia mediante el flujo de recuperación por correo.
                    Usuarios usuAct = new Usuarios();
                    usuAct.setIdUsuarios(idUsuarios);
                    usuAct.setNombre(nombre);
                    usuAct.setApellido(apellido);
                    usuAct.setTelefono(telefono);
                    usuAct.setDireccion(direccion);

                    dao.actualizarDatosBasicos(usuAct);
                    response.sendRedirect("UsuariosCont?accion=listar");
                    
                } catch (Exception e) {
                    System.out.println("❌ Error en doPost Actualizar: " + e.getMessage());
                    response.sendRedirect("UsuariosCont?accion=listar&status=error_actualizar");
                }
            }
        }
    }
}