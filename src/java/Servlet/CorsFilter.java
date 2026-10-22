package Servlet;

import java.io.IOException;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Filtro de configuración CORS (Cross-Origin Resource Sharing) centralizado.
 * Habilita la comunicación cruzada para la aplicación móvil / web (Flutter) y 
 * gestiona las solicitudes de verificación previa (OPTIONS / preflight).
 */
@WebFilter("/*")
public class CorsFilter implements Filter {

    private static final String ALLOWED_ORIGIN = "*";
    private static final String ALLOWED_METHODS = "GET, POST, OPTIONS, PUT, DELETE";
    private static final String ALLOWED_HEADERS = "Content-Type, Authorization, X-Requested-With";
    private static final String MAX_AGE = "3600";

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        response.setHeader("Access-Control-Allow-Origin", ALLOWED_ORIGIN);
        response.setHeader("Access-Control-Allow-Methods", ALLOWED_METHODS);
        response.setHeader("Access-Control-Allow-Headers", ALLOWED_HEADERS);
        response.setHeader("Access-Control-Max-Age", MAX_AGE);

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        chain.doFilter(req, res);
    }

    @Override
    public void init(FilterConfig filterConfig) {
        // Método de inicialización opcional
    }

    @Override
    public void destroy() {
        // Método de liberación de recursos opcional
    }
}