<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Verificar Código | BarStock</title>
        <link href="https://fonts.googleapis.com/css2?family=Great+Vibes&family=Montserrat:wght@300;400;700&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
        <link rel="stylesheet" href="Css/VerificarCodigo.css">
    </head>
    <body>
        <div class="verify-card">
            <i class="bi bi-envelope-paper-heart verify-icon"></i>
            <h1 class="brand-title">BarStock</h1>
            <h2 class="verify-title">Verificación de Cuenta</h2>
            <p class="verify-copy">Hemos enviado un código de 6 dígitos a tu correo electrónico.</p>

            <form action="../UsuariosCont" method="GET">
                <input type="hidden" name="accion" value="verificarCodigo">
                <div class="code-input-wrap">
                    <input type="text" name="txtcodigo" placeholder="------" required maxlength="6" autocomplete="one-time-code" inputmode="numeric">
                </div>
                <button type="submit" class="btn-gold">Verificar Cuenta</button>
            </form>

            <div class="footer-links">
                <a href="../Login.jsp">Volver al <span class="gold-span">inicio de sesión</span></a>
            </div>
        </div>
    </body>
</html>