<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Verificar Código - BarStock</title>
        <link rel="stylesheet" href="Css/VerificarCodigo.css"> <!-- O tus estilos -->
    </head>
    <body style="font-family: Arial; display: flex; justify-content: center; align-items: center; height: 100vh; background-color: #f4f4f4;">
        <div style="background: white; padding: 30px; border-radius: 8px; box-shadow: 0px 0px 10px rgba(0,0,0,0.1); width: 350px; text-align: center;">
            <h2>Verificación de Cuenta</h2>
            <p style="color: #666; font-size: 14px;">Hemos enviado un código de 6 dígitos a tu correo electrónico.</p>
            
            <form action="../UsuariosCont" method="GET">
                <input type="hidden" name="accion" value="verificarCodigo">
                <div style="margin-bottom: 15px;">
                    <input type="text" name="txtcodigo" placeholder="Ingresa el código" required maxlength="6" style="padding: 10px; width: 80%; font-size: 18px; text-align: center; letter-spacing: 3px;">
                </div>
                <button type="submit" style="background: #007bff; color: white; border: none; padding: 10px 20px; border-radius: 5px; cursor: pointer; font-size: 16px;">Verificar Cuenta</button>
            </form>
        </div>
    </body>
</html>