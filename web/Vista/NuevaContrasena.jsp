<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Nueva Contraseña | BarStock</title>

    <link href="https://fonts.googleapis.com/css2?family=Great+Vibes&family=Montserrat:wght@300;400;700&display=swap" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="Css/RecuperarContrasena.css">
</head>
<body>

    <%-- Si el código todavía no ha sido validado en esta sesión, no debe poder llegar aquí --%>
    <%
        if (session.getAttribute("correoResetOk") == null) {
            response.sendRedirect("RecuperarContrasena.jsp");
            return;
        }
    %>

    <div class="verify-card">
        <h1 class="brand-title">BarStock</h1>
        <span class="subtitle">Recuperar Contraseña</span>

        <div class="verify-icon">🔒</div>
        <p class="verify-text">
            Crea tu nueva contraseña. Debe tener al menos 8 caracteres.
        </p>

        <%-- Bloque de mensajes (error) según el parámetro ?status= --%>
        <%
            String status = request.getParameter("status");
            if ("error_pass_corta".equals(status)) {
        %>
            <div class="error-toast">
                La contraseña debe tener al menos 8 caracteres.
            </div>
        <%
            } else if ("error_pass_no_coincide".equals(status)) {
        %>
            <div class="error-toast">
                Las contraseñas no coinciden.
            </div>
        <%
            }
        %>

        <form action="../RecuperarContrasenaCont" method="POST">
            <input type="hidden" name="accion" value="cambiarContrasena">
            <div class="input-group-custom">
                <label>Nueva Contraseña</label>
                <input type="password" name="txtpass" placeholder="••••••••" required minlength="8">
            </div>
            <div class="input-group-custom">
                <label>Confirmar Contraseña</label>
                <input type="password" name="txtpassConfirm" placeholder="••••••••" required minlength="8">
            </div>
            <button type="submit" class="btn-gold">Guardar Contraseña</button>
        </form>

        <div class="footer-links">
            <a href="../Login.jsp">Volver a <span class="gold-span">Iniciar Sesión</span></a>
        </div>
    </div>

</body>
</html>
