package Servlet;

import Modelo.Usuarios;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Filtro de seguridad central de la aplicación. Hace dos cosas:
 *
 * 1) AUTENTICACIÓN: exige que exista una sesión con "usuarioLogueado" para
 *    poder acceder a las páginas/endpoints privados (Panel, Productos,
 *    Pedidos, Mesas, Movimientos, Empleados, y los servlets que los
 *    alimentan). Si no hay sesión válida, se corta el acceso ahí mismo:
 *    nunca se llega a ejecutar el servlet/JSP protegido.
 *
 * 2) "NO GUARDAR EN CACHÉ": en toda respuesta privada se añaden cabeceras
 *    Cache-Control/Pragma/Expires que le dicen al navegador que NO debe
 *    guardar esa página en su caché ni en el historial de "atrás/adelante"
 *    (bfcache). Así, si el usuario cierra sesión y luego pulsa la flecha
 *    de "atrás", el navegador está obligado a volver a pedir la página al
 *    servidor en lugar de mostrar la copia local ya cargada; y al llegar
 *    aquí, este mismo filtro comprueba que la sesión sigue sin existir y lo
 *    manda de vuelta al login.
 *
 * También aplica un segundo nivel de control por ROL: las páginas y
 * acciones de administración de empleados solo las puede usar un usuario
 * con idRol == 1 (Administrador); un Empleado autenticado pero sin ese rol
 * es rechazado aunque tenga sesión válida.
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

    // Rutas dentro de /Vista/ accesibles SIN haber iniciado sesión todavía
    // (recursos estáticos + flujo de registro / recuperación de contraseña,
    // que ocurren antes de tener sesión).
    private static final String[] RUTAS_PUBLICAS_VISTA = {
        "/Vista/Css/",
        "/Vista/JavaScript/",
        "/Vista/RecuperarContrasena.jsp",
        "/Vista/VerificarCodigo.jsp",
        "/Vista/VerificarCodigoRecuperacion.jsp",
        "/Vista/NuevaContrasena.jsp"
    };

    // Acciones de /UsuariosCont que se usan ANTES de iniciar sesión (login,
    // registro y verificación de cuenta desde la web y desde la app Flutter)
    private static final String[] ACCIONES_PUBLICAS_USUARIOSCONT = {
        "loginjson",
        "tiposdocumentojson",
        "verificarcodigojson",
        "verificarcodigo",
        "registrar",
        "registrarjson"
    };

    // Páginas y acciones de administración de empleados: solo rol Administrador (idRol == 1)
    private static final String[] RUTAS_SOLO_ADMIN_VISTA = {
        "/Vista/Empleados.jsp",
        "/Vista/EditarEmpleado.jsp"
    };
    private static final String[] ACCIONES_SOLO_ADMIN_USUARIOSCONT = {
        "listar", "cargar", "eliminar", "cambiarestado", "actualizar",
        "listarjson", "eliminarjson", "cambiarestadojson", "actualizarjson"
    };

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String contextPath = request.getContextPath();
        String path = request.getRequestURI().substring(contextPath.length());
        String accion = request.getParameter("accion");

        // Cabeceras "no-cache" en TODA respuesta que pase por rutas privadas,
        // se conceda o no finalmente el acceso: es justamente lo que evita
        // que el botón "atrás" del navegador muestre una versión cacheada.
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
            for (String publica : RUTAS_PUBLICAS_VISTA) {
                if (path.startsWith(publica)) {
                    return true;
                }
            }
            return false;
        }

        if (path.equals("/UsuariosCont")) {
            if (accion == null) {
                return false;
            }
            String accionLower = accion.toLowerCase();
            for (String publica : ACCIONES_PUBLICAS_USUARIOSCONT) {
                if (publica.equals(accionLower)) {
                    return true;
                }
            }
            return false;
        }

        // El resto de servlets mapeados (/Producto, /Mesas, /Movimiento,
        // /Pedido, /DashboardData, /BuscarGlobal) son siempre privados.
        return false;
    }

    private boolean requiereAdmin(String path, String accion) {
        for (String rutaAdmin : RUTAS_SOLO_ADMIN_VISTA) {
            if (path.equals(rutaAdmin)) {
                return true;
            }
        }
        if (path.equals("/UsuariosCont") && accion != null) {
            String accionLower = accion.toLowerCase();
            for (String accionAdmin : ACCIONES_SOLO_ADMIN_USUARIOSCONT) {
                if (accionAdmin.equals(accionLower)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void denegarAcceso(HttpServletRequest request, HttpServletResponse response,
            String path, String accion, String mensaje) throws IOException {

        // Los servlets que solo devuelven JSON (usados por el Panel vía fetch/AJAX
        // y por la app Flutter) deben responder con un 401 en JSON en vez de
        // redirigir a una página HTML, para que el cliente pueda tratarlo bien.
        boolean esServletJsonPuro = path.equals("/DashboardData") || path.equals("/BuscarGlobal")
                || path.equals("/Producto") || path.equals("/Mesas")
                || path.equals("/Movimiento") || path.equals("/Pedido");
        boolean esAccionJson = accion != null && accion.toLowerCase().endsWith("json");

        if (esServletJsonPuro || esAccionJson) {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().print("{\"success\":false,\"error\":\"" + mensaje.replace("\"", "'") + "\"}");
        } else {
            response.sendRedirect(request.getContextPath() + "/Login.jsp?status=sesion_requerida");
        }
    }

    @Override
    public void init(jakarta.servlet.FilterConfig filterConfig) {
        // No requiere inicialización especial
    }

    @Override
    public void destroy() {
        // No requiere liberar recursos
    }
}
