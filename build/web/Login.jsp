<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login | BarStock Premium</title>
    
    <link href="https://fonts.googleapis.com/css2?family=Great+Vibes&family=Montserrat:wght@300;400;700&display=swap" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
    <link rel="stylesheet" href="Vista/Css/Login.css">
</head>
<body>

    <div class="login-shell">

        <!-- Panel decorativo (solo visual, no interviene en la lógica) -->
        <div class="login-brand-panel">
            <div class="brand-panel-inner">
                <span class="brand-eyebrow">Bienvenido de nuevo</span>
                <h1 class="brand-title">BarStock</h1>
                <span class="subtitle">Inventory Management</span>
                <p class="brand-copy">Controla tu inventario, pedidos y equipo desde un solo panel elegante y en tiempo real.</p>
            </div>
        </div>

        <!-- Panel del formulario -->
        <div class="login-card">
            <div class="login-card-header">
                <i class="bi bi-shield-lock login-header-icon"></i>
                <h2 class="form-title">Iniciar Sesión</h2>
                <p class="form-subtitle">Ingresa tus credenciales para continuar</p>
            </div>

            <%-- Bloque de errores --%>
            <%
                String errorMsg = (String) request.getAttribute("mensaje");
                if (errorMsg == null) {
                    errorMsg = (String) request.getAttribute("error");
                }
                if (errorMsg != null) {
            %>
                <div class="error-toast">
                    <i class="bi bi-exclamation-triangle-fill me-2"></i><%= errorMsg %>
                </div>
            <% } %>

            <%-- FORMULARIO CORREGIDO: Nombres de input coinciden con el Servlet --%>
            <form action="${pageContext.request.contextPath}/ServletLogin" method="POST">
                <input type="hidden" name="accion" value="ingresar">

                <div class="input-group-custom">
                    <label>Correo Electrónico</label>
                    <div class="input-icon-wrap">
                        <i class="bi bi-envelope-fill input-icon"></i>
                        <input type="email" name="txtCorreo" placeholder="usuario@barstock.com" required>
                    </div>
                </div>

                <div class="input-group-custom">
                    <label>Contraseña</label>
                    <div class="input-icon-wrap">
                        <i class="bi bi-lock-fill input-icon"></i>
                        <input type="password" name="txtContrasena" placeholder="••••••••" required>
                    </div>
                </div>

                <button type="submit" class="btn-gold">
                    Entrar al Sistema <i class="bi bi-arrow-right-short ms-1"></i>
                </button>
            </form>

            <div class="footer-links">
                <a href="Registro.jsp">¿No tienes cuenta? <span class="gold-span">Regístrate aquí</span></a>
            </div>
        </div>

    </div>

</body>
</html>