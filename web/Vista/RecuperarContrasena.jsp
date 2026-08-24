<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Recuperar Contraseña | BarStock</title>

    <link href="https://fonts.googleapis.com/css2?family=Great+Vibes&family=Montserrat:wght@300;400;700&display=swap" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="Css/RecuperarContrasena.css?v=2">
</head>
<body>

    <div class="verify-card">
        <h1 class="brand-title">BarStock</h1>
        <span class="subtitle">Recuperar Contraseña</span>

        <div class="verify-icon">🔑</div>
        <p class="verify-text">
            Ingresa el correo electrónico de tu cuenta.<br>
            Te enviaremos un código de 6 dígitos para restablecer tu contraseña.
        </p>

        <form action="../RecuperarContrasenaCont" method="POST">
            <input type="hidden" name="accion" value="solicitarCodigo">
            <div class="input-group-custom">
                <label>Correo Electrónico</label>
                <input type="email" name="txtcorreo" placeholder="usuario@barstock.com" required>
            </div>
            <button type="submit" class="btn-gold">Enviar Código</button>
        </form>

        <div class="footer-links">
            <a href="../Login.jsp">Volver a <span class="gold-span">Iniciar Sesión</span></a>
        </div>
    </div>

</body>
</html>
