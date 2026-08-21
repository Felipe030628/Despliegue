<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Verificar Código | BarStock</title>

    <link href="https://fonts.googleapis.com/css2?family=Great+Vibes&family=Montserrat:wght@300;400;700&display=swap" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="Css/RecuperarContrasena.css">
</head>
<body>

    <div class="verify-card">
        <h1 class="brand-title">BarStock</h1>
        <span class="subtitle">Recuperar Contraseña</span>

        <div class="verify-icon">✉️</div>
        <p class="verify-text">
            Si el correo ingresado existe en el sistema, te hemos enviado un código de 6 dígitos.<br>
            Ingrésalo a continuación para continuar.
        </p>

        <%-- Bloque de mensajes (error) según el parámetro ?status= --%>
        <%
            String status = request.getParameter("status");
            if ("error_codigo".equals(status)) {
        %>
            <div class="error-toast">
                El código ingresado es incorrecto o ha expirado. Inténtalo de nuevo.
            </div>
        <%
            }
        %>

        <form action="../RecuperarContrasenaCont" method="GET">
            <input type="hidden" name="accion" value="verificarCodigo">
            <div class="input-group-custom">
                <label>Código de Verificación</label>
                <input type="text" name="txtcodigo" class="codigo-input" placeholder="------" required maxlength="6" inputmode="numeric" pattern="[0-9]{6}" autofocus>
            </div>
            <button type="submit" class="btn-gold">Verificar Código</button>
        </form>

        <div class="footer-links">
            <a href="RecuperarContrasena.jsp">¿No recibiste el código? <span class="gold-span">Solicitar de nuevo</span></a><br><br>
            <a href="../Login.jsp">Volver a <span class="gold-span">Iniciar Sesión</span></a>
        </div>
    </div>

</body>
</html>
