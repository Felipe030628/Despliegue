package Servlet;

import Controlador.UsuariosDAO;
import Modelo.Usuarios;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "Registro", urlPatterns = {"/Registro"})
public class Registro extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("Registro.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String accion = request.getParameter("accion");

        // Validamos la acción sin importar si viene en mayúscula o minúscula
        if (accion != null && accion.equalsIgnoreCase("Registrar")) {
            try {
                // 1. Captura de parámetros con tolerancia a mayúsculas/minúsculas comunes del HTML
                String nombre = request.getParameter("txtnombre");
                String apellido = request.getParameter("txtapellido");
                String correo = request.getParameter("txtemail");
                String fechaNac = request.getParameter("txtfechaNacimiento");
                if (fechaNac == null) {
                    fechaNac = request.getParameter("txtfechaNac"); // Por si acaso lo llamaste corto
                }
                String telefono = request.getParameter("txttelefono");
                if (telefono == null) {
                    telefono = request.getParameter("txttel"); // Segunda opción común
                }
                String direccion = request.getParameter("txtdireccion");
                if (direccion == null) {
                    direccion = request.getParameter("txtdir");
                }
                String contrasena = request.getParameter("txtpass");
                if (contrasena == null) {
                    contrasena = request.getParameter("txtcontrasena");
                }
                String numDoc = request.getParameter("txtnumdoc");
                if (numDoc == null) {
                    numDoc = request.getParameter("txtnumDocumento");
                }
                
                String idTipoStr = request.getParameter("txtIdTipoDoc");
                if (idTipoStr == null) {
                    idTipoStr = request.getParameter("txttipoDocumento");
                }
                String idRolStr = request.getParameter("txtrol");

                // 2. LOG DE DEPURACIÓN: Esto imprimirá en la consola de Glassfish/Tomcat qué campo está llegando NULL
                System.out.println("--- DETECTANDO CAMPOS RECIBIDOS ---");
                System.out.println("nombre: " + nombre);
                System.out.println("apellido: " + apellido);
                System.out.println("correo: " + correo);
                System.out.println("fechaNac: " + fechaNac);
                System.out.println("numDoc: " + numDoc);
                System.out.println("idTipoStr: " + idTipoStr);
                System.out.println("idRolStr: " + idRolStr);
                System.out.println("contrasena: " + contrasena);
                System.out.println("------------------------------------");

                // 3. Validación estricta solo para los campos obligatorios que NO admiten NULL en BD
                if (nombre == null || nombre.trim().isEmpty() || 
                    apellido == null || apellido.trim().isEmpty() ||
                    correo == null || correo.trim().isEmpty() || 
                    fechaNac == null || fechaNac.trim().isEmpty() ||
                    numDoc == null || numDoc.trim().isEmpty() ||
                    contrasena == null || contrasena.trim().isEmpty() || 
                    idTipoStr == null || idRolStr == null) {
                    
                    request.setAttribute("mensaje", "Datos incorrectos: Todos los campos obligatorios deben ser diligenciados.");
                    request.getRequestDispatcher("Registro.jsp").forward(request, response);
                    return;
                }

                // 4. Validación de longitudes básicas para que no rompa el DAO
                if (numDoc.length() < 7 || numDoc.length() > 20) {
                    request.setAttribute("mensaje", "Formato incorrecto: El documento debe tener entre 7 y 20 dígitos.");
                    request.getRequestDispatcher("Registro.jsp").forward(request, response);
                    return;
                }

                // Instanciamos el Modelo y el DAO
                Usuarios u = new Usuarios();
                UsuariosDAO udao = new UsuariosDAO();

                // 5. Llenado del modelo limpio
                u.setNombre(nombre);
                u.setApellido(apellido);
                u.setCorreo(correo);
                u.setFecha_nacimiento(fechaNac);
                u.setIdTipoDocumento(Integer.parseInt(idTipoStr));
                u.setNombre_documento(numDoc);
                u.setTelefono(telefono); // Si llega null o vacío, la BD lo acepta
                u.setDireccion(direccion);
                u.setIdRol(Integer.parseInt(idRolStr));
                u.setContrasena(contrasena);

                // 6. Intentar registrar en Base de Datos
                int res = udao.registrar(u);

                if (res > 0) {
                    request.setAttribute("mensajeExito", "¡Usuario registrado con éxito! Ya puedes iniciar sesión.");
                    request.getRequestDispatcher("Registro.jsp").forward(request, response);
                } else {
                    request.setAttribute("mensaje", "No se pudo completar el registro. Compruebe que los datos no existan en el sistema.");
                    request.getRequestDispatcher("Registro.jsp").forward(request, response);
                }
                
            } catch (NumberFormatException e) {
                request.setAttribute("mensaje", "Error de formato: El tipo de documento o rol seleccionados no son válidos.");
                request.getRequestDispatcher("Registro.jsp").forward(request, response);
            } catch (Exception e) {
                String errorMsg = e.getMessage();
                if (errorMsg != null && errorMsg.contains("Duplicate entry")) {
                    request.setAttribute("mensaje", "Error de registro: El número de documento o correo electrónico ya se encuentra registrado.");
                } else {
                    request.setAttribute("mensaje", "Error en el sistema: " + errorMsg);
                }
                request.getRequestDispatcher("Registro.jsp").forward(request, response);
            }
        }
    }
}