package Servlet;

import Controlador.UsuariosDAO;
import Modelo.TiposDocumentos;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "RegistroCont", urlPatterns = {"/RegistroCont"})
public class RegistroCont extends HttpServlet {

    UsuariosDAO dao = new UsuariosDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Buscamos los tipos de documento desde la base de datos
        List<TiposDocumentos> listaTiposDoc = dao.listarTiposDocumentoMayores();
        
        // 2. Los mandamos al request
        request.setAttribute("listaTiposDoc", listaTiposDoc);
        
        // 3. Redirigimos al JSP de registro
        request.getRequestDispatcher("Registro.jsp").forward(request, response);
    }
}