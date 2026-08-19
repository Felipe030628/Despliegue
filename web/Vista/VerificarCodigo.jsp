<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Verificar Código | BarStock Premium</title>

    <link href="https://fonts.googleapis.com/css2?family=Great+Vibes&family=Montserrat:wght@300;400;700&display=swap" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="Css/VerificarCodigo.css">
</head>
<body>

    <div class="verify-card">
        <h1 class="brand-title">BarStock</h1>
        <span class="subtitle">Verificación de Cuenta</span>

        <div class="verify-icon">✉️</div>
        <p class="verify-text">
            Hemos enviado un código de 6 dígitos a tu correo electrónico.<br>
            Ingrésalo a continuación para activar tu cuenta.
        </p>

        <%-- Bloque de mensajes (error / éxito) según el parámetro ?status= --%>
        <%
            String status = request.getParameter("status");
            if ("error_codigo".equals(status)) {
        %>
            <div class="error-toast">
                El código ingresado es incorrecto o ha expirado. Inténtalo de nuevo.
            </div>
        <%
            } else if ("reenviado".equals(status)) {
        %>
            <div class="success-toast">
                Te hemos enviado un nuevo código. Revisa tu bandeja de entrada.
            </div>
        <%
            }
        %>

        <form action="../UsuariosCont" method="GET">
            <input type="hidden" name="accion" value="verificarCodigo">
            <div class="input-group-custom">
                <label>Código de Verificación</label>
                <input type="text" name="txtcodigo" class="codigo-input" placeholder="------" required maxlength="6" inputmode="numeric" pattern="[0-9]{6}" autofocus>
            </div>
            <button type="submit" class="btn-gold">Verificar Cuenta</button>
        </form>

        <div class="footer-links">
            <a href="../Login.jsp">Volver a <span class="gold-span">Iniciar Sesión</span></a>
        </div>
    </div>

</body>
</html>
