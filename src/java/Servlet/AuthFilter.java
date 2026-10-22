package Servlet;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import Modelo.Usuarios;

/**
 * Filtro de seguridad centralizado para el control de autenticación, 
 * autorización basada en roles (RBAC) y prevención de almacenamiento en caché.
 */
@WebFilter(urlPatterns = {
    "/Vista/*",
    "/UsuariosCont",
    "/Producto",
    "/Mesas",
    "/Movimiento",
    "/Pedido",
    "/DashboardData",
    "/BuscarGlobal"
})
public class AuthFilter implements Filter {

    private static final Set<String> RUTAS_PUBLICAS_VISTA = new HashSet<>(Arrays.asList(
        "/Vista/Css/",
        "/Vista/JavaScript/",
        "/Vista/RecuperarContrasena.jsp",
        "/Vista/VerificarCodigo.jsp",
        "/Vista/VerificarCodigoRecuperacion.jsp",
        "/Vista/NuevaContrasena.jsp"
    ));

    private static final Set<String> ACCIONES_PUBLICAS_USUARIOSCONT = new HashSet<>(Arrays.asList(
        "loginjson",
        "tiposdocumentojson",
        "verificarcodigojson",
        "verificarcodigo",
        "registrar",
        "registrarjson"
    ));

    private static final Set<String> RUTAS_SOLO_ADMIN_VISTA = new HashSet<>(Arrays.asList(
        "/Vista/Empleados.jsp",
        "/Vista/EditarEmpleado.jsp"
    ));

    private static final Set<String> ACCIONES_SOLO_ADMIN_USUARIOSCONT = new HashSet<>(Arrays.asList(
        "listar", "cargar", "eliminar", "cambiarestado", "actualizar",
        "listarjson", "eliminarjson", "cambiarestadojson", "actualizarjson"
    ));

    private static final Set<String> SERVLETS_JSON_PUROS = new HashSet<>(Arrays.asList(
        "/DashboardData",
        "/BuscarGlobal",
        "/Producto",
        "/Mesas",
        "/Movimiento",
        "/Pedido"
    ));

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String contextPath = request.getContextPath();
        String path = request.getRequestURI().substring(contextPath.length());
        String accion = request.getParameter("accion");

        // Deshabilitar almacenamiento en caché para respuestas dentro del filtro
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        if (esRutaPublica(path, accion)) {
            chain.doFilter(req, res);
            return;
        }

        HttpSession session = request.getSession(false);
        Usuarios usuario = (session != null) ? (Usuarios) session.getAttribute("usuarioLogueado") : null;

        if (usuario == null) {
            denegarAcceso(request, response, path, accion, "Debes iniciar sesión para continuar.");
            return;
        }

        if (requiereAdmin(path, accion) && usuario.getIdRol() != 1) {
            denegarAcceso(request, response, path, accion, "No tienes permisos para acceder a esta sección.");
            return;
        }

        chain.doFilter(req, res);
    }

    private boolean esRutaPublica(String path, String accion) {
        if (path.startsWith("/Vista/")) {
            return RUTAS_PUBLICAS_VISTA.stream().anyMatch(path::startsWith);
        }

        if ("/UsuariosCont".equals(path)) {
            return accion != null && ACCIONES_PUBLICAS_USUARIOSCONT.contains(accion.toLowerCase());
        }

        return false;
    }

    private boolean requiereAdmin(String path, String accion) {
        if (RUTAS_SOLO_ADMIN_VISTA.contains(path)) {
            return true;
        }
        return "/UsuariosCont".equals(path) && accion != null 
                && ACCIONES_SOLO_ADMIN_USUARIOSCONT.contains(accion.toLowerCase());
    }

    private void denegarAcceso(HttpServletRequest request, HttpServletResponse response,
            String path, String accion, String mensaje) throws IOException {

        boolean esServletJsonPuro = SERVLETS_JSON_PUROS.contains(path);
        boolean esAccionJson = accion != null && accion.toLowerCase().endsWith("json");

        if (esServletJsonPuro || esAccionJson) {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            String jsonError = String.format("{\"success\":false,\"error\":\"%s\"}", mensaje.replace("\"", "'"));
            response.getWriter().print(jsonError);
        } else {
            response.sendRedirect(request.getContextPath() + "/Login.jsp?status=sesion_requerida");
        }
    }

    @Override
    public void init(FilterConfig filterConfig) {
        // Inicialización del filtro si fuera requerida
    }

    @Override
    public void destroy() {
        // Limpieza de recursos del filtro si fuera requerida
    }
}