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
            if (mensajeExito != null) { 
        %>
            <div class="alert alert-premium-success text-center" role="alert">
                <%= mensajeExito %>
            </div>
        <% } if (mensajeError != null) { %>
            <div class="alert alert-premium-danger text-center" role="alert">
                <%= mensajeError %>
            </div>
        <% } %>

        <form action="Registro" method="POST">
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
                    <select name="txttipoDocumento" class="form-select form-control-premium" required>
    <option value="">Seleccione un documento...</option>
    <c:forEach var="doc" items="${listaTiposDoc}">
        <option value="${doc.idTipoDocumento}">${doc.nombre_documento}</option>
    </c:forEach>
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

            <div class="mb-3">
                <label class="form-label-premium">Cargo en el Bar (Rol)</label>
                <select name="txtrol" class="form-select form-control-premium" required>
                    <option value="1">Administrador (Control Total)</option>
                    <option value="2">Mesero (Ventas y Pedidos)</option>
                </select>
            </div>

            <div class="mb-4">
                <label class="form-label-premium">Contraseña</label>
                <input type="password" name="txtpass" class="form-control form-control-premium" required>
            </div>

            <button type="submit" name="accion" value="Registrar" class="btn-gold-premium">
                Registrar Usuario
            </button>
            
            <div class="text-center">
                <a href="Login.jsp" class="link-footer">
                    ¿Ya tienes cuenta? <span>Inicia sesión aquí</span>
                </a>
            </div>
        </form>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script src="Vista/JavaScript/Registro.js"></script>
</body>
</html>