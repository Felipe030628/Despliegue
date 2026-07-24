<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login | BarStock Premium</title>
    
    <link href="https://fonts.googleapis.com/css2?family=Great+Vibes&family=Montserrat:wght@300;400;700&display=swap" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="Vista/Css/Login.css">
</head>
<body>

    <div class="login-card">
        <h1 class="brand-title">BarStock</h1>
        <span class="subtitle">Inventory Management</span>
        
        <%-- Bloque de errores --%>
        <% 
            String errorMsg = (String) request.getAttribute("mensaje");
            if (errorMsg == null) { 
                errorMsg = (String) request.getAttribute("error"); 
            }
            if (errorMsg != null) { 
        %>
            <div class="error-toast" style="color: red; margin-bottom: 10px;">
                <%= errorMsg %>
            </div>
        <% } %>

        <%-- FORMULARIO CORREGIDO: Nombres de input coinciden con el Servlet --%>
        <form action="ServletLogin" method="POST">
            <input type="hidden" name="accion" value="ingresar">

            <div class="input-group-custom">
                <label>Correo Electrónico</label>
                <input type="email" name="txtCorreo" placeholder="usuario@barstock.com" required>
            </div>
            
            <div class="input-group-custom">
                <label>Contraseña</label>
                <input type="password" name="txtContrasena" placeholder="••••••••" required>
            </div>

            <button type="submit" class="btn-gold">Entrar al Sistema</button>
        </form>
        
        <div class="footer-links">
            <a href="Registro.jsp">¿No tienes cuenta? <span class="gold-span">Regístrate aquí</span></a>
        </div>
    </div>

</body>
</html>