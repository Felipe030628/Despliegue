package Servlet;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import Controlador.UsuariosDAO;
import Modelo.TiposDocumentos;

/**
 * Controlador HTTP encargado de preparar el formulario de registro de nuevos usuarios.
 * Consulta y provee el catálogo de tipos de documentos requeridos por la vista.
 */
@WebServlet(name = "RegistroCont", urlPatterns = {"/RegistroCont"})
public class RegistroCont extends HttpServlet {

    private static final String VISTA_REGISTRO = "Registro.jsp";
    private final UsuariosDAO usuariosDAO = new UsuariosDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<TiposDocumentos> listaTiposDoc = usuariosDAO.listarTiposDocumentoMayores();
        request.setAttribute("listaTiposDoc", listaTiposDoc);

        request.getRequestDispatcher(VISTA_REGISTRO).forward(request, response);
    }
}