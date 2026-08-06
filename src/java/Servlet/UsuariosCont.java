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
            }
        } else {
            response.sendRedirect("Vista/Panel.jsp");
        }
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
                    String fechaNac = request.getParameter("txtfechaNac");     // Corregido a txtfechaNac
                    String numDoc = request.getParameter("txtnumdoc");         // Corregido a txtnumdoc
                    String telefono = request.getParameter("txttel");         // Corregido a txttel
                    String direccion = request.getParameter("txtdireccion");
                    String contrasena = request.getParameter("txtpass");
                    
                    int idTipoDocumento = Integer.parseInt(request.getParameter("txtIdTipoDoc")); // Corregido a txtIdTipoDoc
                    int idRol = Integer.parseInt(request.getParameter("txtrol"));
                    
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
                        
                        // 3. Enviar el correo electrónico
                        boolean enviado = CorreoUtil.enviarCorreo(correo, codigoVerificacion);
                        
                        if (enviado) {
                            request.getSession().setAttribute("correoVerificar", correo);
                            response.sendRedirect("Vista/VerificarCodigo.jsp?status=enviado");
                        } else {
                            response.sendRedirect("Registro.jsp?status=error_correo");
                        }
                    } else {
                        response.sendRedirect("Registro.jsp?status=error");
                    }
                } catch (Exception e) {
                    System.out.println("❌ Error en doPost Registrar: " + e.getMessage());
                    response.sendRedirect("Registro.jsp?status=error_sistema");
                }
                
            } else if (accion.equalsIgnoreCase("actualizar")) {
                try {
                    int idUsuarios = Integer.parseInt(request.getParameter("idUsuarios"));
                    String nombre = request.getParameter("nombre");
                    String apellido = request.getParameter("apellido");
                    String correo = request.getParameter("correo");
                    String contrasena = request.getParameter("contrasena");
                    int idTipoDocumento = Integer.parseInt(request.getParameter("idTipoDocumento"));
                    String num_documento = request.getParameter("num_documento");
                    String telefono = request.getParameter("telefono");
                    String direccion = request.getParameter("direccion");
                    int idRol = Integer.parseInt(request.getParameter("idRol"));

                    Usuarios usuAct = new Usuarios();
                    usuAct.setIdUsuarios(idUsuarios);
                    usuAct.setNombre(nombre);
                    usuAct.setApellido(apellido);
                    usuAct.setCorreo(correo);
                    usuAct.setContrasena(contrasena);
                    usuAct.setIdTipoDocumento(idTipoDocumento);
                    usuAct.setNombre_documento(num_documento);
                    usuAct.setTelefono(telefono);
                    usuAct.setDireccion(direccion);
                    usuAct.setIdRol(idRol);

                    dao.actualizarUsuario(usuAct);
                    response.sendRedirect("UsuariosCont?accion=listar");
                    
                } catch (Exception e) {
                    System.out.println("❌ Error en doPost Actualizar: " + e.getMessage());
                    response.sendRedirect("UsuariosCont?accion=listar&status=error_actualizar");
                }
            }
        }
    }
}