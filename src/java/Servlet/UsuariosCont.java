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
                case "nuevoRegistro":
                    List<TiposDocumentos> listaTiposDocReg = dao.listarTiposDocumentoMayores();
                    request.setAttribute("listaTiposDoc", listaTiposDocReg);
                    request.getRequestDispatcher("Registro.jsp").forward(request, response);
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
                    String fechaNac = request.getParameter("txtfechaNacimiento");
                    String numDoc = request.getParameter("txtnumDocumento");
                    String telefono = request.getParameter("txttelefono");
                    String direccion = request.getParameter("txtdireccion");
                    String contrasena = request.getParameter("txtpass");
                    
                    int idTipoDocumento = Integer.parseInt(request.getParameter("txttipoDocumento"));
                    int idRol = Integer.parseInt(request.getParameter("txtrol"));
                    
                    u.setNombre(nombre);
                    u.setApellido(apellido);
                    u.setCorreo(correo);
                    u.setFecha_nacimiento(fechaNac);
                    u.setIdTipoDocumento(idTipoDocumento);
                    u.setNombre_documento(numDoc); // Corregido a setNum_documento
                    u.setTelefono(telefono);
                    u.setDireccion(direccion);
                    u.setIdRol(idRol);
                    u.setContrasena(contrasena);
                    
                    int res = dao.registrar(u);
                    
                    if (res > 0) {
                        response.sendRedirect("Login.jsp?status=success");
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
                    usuAct.setNombre_documento(num_documento); // Corregido a setNum_documento
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