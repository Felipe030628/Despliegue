<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Registro | BarStock</title>
    
    <link href="https://fonts.googleapis.com/css2?family=Great+Vibes&family=Montserrat:wght@300;400;600;700&display=swap" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="Vista/Css/Registro.css">
</head>
<body>

    <div class="register-container">
        <div class="text-center">
            <h1 class="brand-title">BarStock</h1>
            <p class="brand-subtitle">Inventory Management</p>
        </div>
        
        <% 
            String mensajeExito = (String) request.getAttribute("mensajeExito");
            String mensajeError = (String) request.getAttribute("mensaje");
            String status = request.getParameter("status");
            String mensajeStatus = null;
            if (status != null) {
                switch (status) {
                    case "error":
                        mensajeStatus = "No se pudo completar el registro. Verifica los datos ingresados.";
                        break;
                    case "error_sistema":
                        mensajeStatus = "Ocurrió un error inesperado. Inténtalo de nuevo más tarde.";
                        break;
                    case "error_pass_corta":
                        mensajeStatus = "La contraseña debe tener al menos 8 caracteres.";
                        break;
                    case "error_pass_no_coincide":
                        mensajeStatus = "Las contraseñas no coinciden.";
                        break;
                    case "error_terminos":
                        mensajeStatus = "Debes aceptar los Términos y Condiciones para registrarte.";
                        break;
                }
            }
            if (mensajeExito != null) { 
        %>
            <div class="alert alert-premium-success text-center" role="alert">
                <%= mensajeExito %>
            </div>
        <% } if (mensajeError != null) { %>
            <div class="alert alert-premium-danger text-center" role="alert">
                <%= mensajeError %>
            </div>
        <% } if (mensajeStatus != null) { %>
            <div class="alert alert-premium-danger text-center" role="alert">
                <%= mensajeStatus %>
            </div>
        <% } %>

        <form action="UsuariosCont" method="POST">
            <div class="row">
                <div class="col-md-6 mb-3">
                    <label class="form-label-premium">Nombre</label>
                    <input type="text" name="txtnombre" class="form-control form-control-premium" required>
                </div>
                <div class="col-md-6 mb-3">
                    <label class="form-label-premium">Apellido</label>
                    <input type="text" name="txtapellido" class="form-control form-control-premium" required>
                </div>
            </div>

            <div class="mb-3">
                <label class="form-label-premium">Correo Electrónico</label>
                <input type="email" name="txtemail" class="form-control form-control-premium" placeholder="usuario@barstock.com" required>
            </div>

            <div class="mb-3">
                <label class="form-label-premium">Fecha de Nacimiento</label>
                <input type="date" name="txtfechaNac" class="form-control form-control-premium" required>
            </div>

            <div class="row">
                <div class="col-md-5 mb-3">
                    <label class="form-label-premium">Tipo Documento</label>
                    <select name="txtIdTipoDoc" class="form-select form-control-premium">
                        <option value="1">Cédula de Ciudadanía</option>
                        <option value="2">Pasaporte</option>
                        <option value="3">PPT</option>
                    </select>
                </div>
                <div class="col-md-7 mb-3">
                    <label class="form-label-premium">Número de Documento</label>
                    <input type="text" name="txtnumdoc" minlength="7" maxlength="20" class="form-control form-control-premium" required>
                </div>
            </div>

            <div class="row">
                <div class="col-md-5 mb-3">
                    <label class="form-label-premium">Teléfono</label>
                    <input type="tel" name="txttel" minlength="7" maxlength="20" class="form-control form-control-premium" required>
                </div>
                <div class="col-md-7 mb-3">
                    <label class="form-label-premium">Dirección de Residencia</label>
                    <input type="text" name="txtdireccion" class="form-control form-control-premium">
                </div>
            </div>


            <div class="mb-2">
                <label class="form-label-premium">Contraseña</label>
                <div class="input-group-premium">
                    <input type="password" id="txtpass" name="txtpass" class="form-control form-control-premium" minlength="8" required>
                    <button type="button" class="btn-toggle-pass" data-target="txtpass" aria-label="Mostrar contraseña">
                        <i class="bi-eye"></i>
                    </button>
                </div>
                <div class="password-strength-meter">
                    <div id="strengthBar" class="password-strength-bar"></div>
                </div>
                <small id="strengthText" class="strength-text">Introduce una contraseña</small>
            </div>

            <div class="mb-4">
                <label class="form-label-premium">Confirmar Contraseña</label>
                <div class="input-group-premium">
                    <input type="password" id="txtpassConfirm" name="txtpassConfirm" class="form-control form-control-premium" minlength="8" required>
                    <button type="button" class="btn-toggle-pass" data-target="txtpassConfirm" aria-label="Mostrar contraseña">
                        <i class="bi-eye"></i>
                    </button>
                </div>
                <small id="matchText" class="strength-text"></small>
            </div>

            <div class="mb-4 form-check-premium">
                <input type="checkbox" id="txtterminos" name="txtterminos" class="form-check-input-premium" required>
                <label for="txtterminos" class="form-check-label-premium">
                    He leído y acepto los
                    <a href="#" data-bs-toggle="modal" data-bs-target="#modalTerminos" class="terms-link">Términos y Condiciones</a>
                </label>
            </div>

            <button type="submit" name="accion" value="Registrar" id="btnRegistrar" class="btn-gold-premium">
                Registrar Usuario
            </button>
            
            <div class="text-center">
                <a href="Login.jsp" class="link-footer">
                    ¿Ya tienes cuenta? <span>Inicia sesión aquí</span>
                </a>
            </div>
        </form>
    </div>

    <!-- Modal Términos y Condiciones -->
    <div class="modal fade" id="modalTerminos" tabindex="-1" aria-labelledby="modalTerminosLabel" aria-hidden="true">
        <div class="modal-dialog modal-dialog-scrollable">
            <div class="modal-content modal-content-premium">
                <div class="modal-header">
                    <h5 class="modal-title" id="modalTerminosLabel">Términos y Condiciones</h5>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Cerrar"></button>
                </div>
                <div class="modal-body">
                    <p><strong>1. Uso del sistema.</strong> BarStock es una herramienta interna de gestión de inventario, pedidos y personal. El acceso queda restringido al personal autorizado del establecimiento.</p>
                    <p><strong>2. Datos personales.</strong> Los datos suministrados en este formulario (nombre, documento, contacto, rol) se almacenan con el único fin de administrar el acceso y las operaciones del bar, conforme a la normatividad de protección de datos vigente.</p>
                    <p><strong>3. Responsabilidad de la cuenta.</strong> El usuario es responsable de mantener la confidencialidad de su contraseña y de todas las actividades realizadas bajo su cuenta.</p>
                    <p><strong>4. Verificación de cuenta.</strong> El registro se activará únicamente tras confirmar el código enviado al correo electrónico proporcionado.</p>
                    <p><strong>5. Uso adecuado.</strong> Queda prohibido el uso indebido de la plataforma, incluyendo la manipulación no autorizada de inventarios, pedidos o registros de otros usuarios.</p>
                    <p><strong>6. Modificaciones.</strong> Estos términos pueden actualizarse; el uso continuado del sistema implica la aceptación de los cambios.</p>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn-gold-premium modal-btn-close" data-bs-dismiss="modal">Cerrar</button>
                </div>
            </div>
        </div>
    </div>

    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script src="Vista/JavaScript/Registro.js"></script>
</body>
</html>