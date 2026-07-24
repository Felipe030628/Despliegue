<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Editar Empleado | BarStock</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Vista/Css/Global.css">
</head>
<body>
    <div class="dashboard-container">
        <aside class="sidebar">
            <div class="sidebar-header">
                <h3>BarStock</h3>
                <p>Inventario Inteligente</p>
            </div>
            <ul class="sidebar-menu">
                <li>
                    <a href="Vista/Panel.jsp"><i class="bi bi-sliders me-2"></i> Dashboard</a>
                </li>

                <!-- Menú Desplegable: Inventario -->
                <li class="nav-item dropdown">
                    <a class="dropdown-toggle" href="#" role="button" data-bs-toggle="collapse" data-bs-target="#menuInventario">
                        <i class="bi bi-box-seam me-2"></i> Inventario
                    </a>
                    <ul class="collapse list-unstyled ps-3" id="menuInventario">
                        <li><a href="${pageContext.request.contextPath}/Producto?accion=listar">Productos</a></li>
                        <li><a href="${pageContext.request.contextPath}/Movimiento?accion=listar"><i class="bi bi-arrow-left-right me-3"></i> Movimientos Stock</a></li>
                    </ul>
                </li>

                <!-- Menú Desplegable: Gestión de Pedidos -->
                <li class="nav-item dropdown">
                    <a class="dropdown-toggle" href="#" role="button" data-bs-toggle="collapse" data-bs-target="#menuPedidos">
                        <i class="bi bi-cart-check me-2"></i> Pedidos
                    </a>
                    <ul class="collapse list-unstyled ps-3" id="menuPedidos">
                        <li><a href="${pageContext.request.contextPath}/Pedido?accion=listar"><i class="bi bi-cart me-3"></i> Pedidos</a></li>
                        <li><a href="${pageContext.request.contextPath}/Mesas?accion=listar"><i class="bi bi-ui-checks-grid me-3"></i> Mesas</a></li>
                    </ul>
                </li>

                <li class="active">
                    <a href="${pageContext.request.contextPath}/UsuariosCont?accion=listar">
                        <i class="bi bi-people me-3"></i> Empleados
                    </a>
                </li>

                <li class="mt-5">
                    <a href="${pageContext.request.contextPath}/UsuariosCont?accion=listar" class="logout-link">
                        <i class="bi bi-arrow-left me-2"></i> Volver al Listado
                    </a>
                </li>
            </ul>
        </aside>

        <main class="main-content">
            <div class="content-card">
                <h5 class="mb-4">Editar Empleado</h5>
                
                <form action="${pageContext.request.contextPath}/UsuariosCont" method="POST" class="row g-3">
                    
                    <!-- Campo Oculto de Acción e ID -->
                    <input type="hidden" name="accion" value="actualizar">
                    <input type="hidden" name="idUsuarios" value="${usuario.idUsuarios}">

                    <div class="col-md-6">
                        <label class="form-label">Nombre</label>
                        <input type="text" name="nombre" class="form-control" value="${usuario.nombre}" required>
                    </div>

                    <div class="col-md-6">
                        <label class="form-label">Apellido</label>
                        <input type="text" name="apellido" class="form-control" value="${usuario.apellido}" required>
                    </div>

                    <div class="col-md-6">
                        <label class="form-label">Correo (Inicio de Sesión)</label>
                        <input type="email" name="correo" class="form-control" value="${usuario.correo}" required>
                    </div>

                    <div class="col-md-6">
                        <label class="form-label">Contraseña</label>
                        <input type="text" name="contrasena" class="form-control" value="${usuario.contrasena}" required>
                    </div>

                    <div class="col-md-4">
                        <label class="form-label">Tipo de Documento</label>
                        <select name="idTipoDocumento" class="form-select" required>
                            <option value="">Seleccione...</option>
                            <c:forEach var="td" items="${listaTiposDoc}">
                                <option value="${td.idTipoDocumento}" ${usuario.idTipoDocumento == td.idTipoDocumento ? 'selected' : ''}>
                                    ${td.nombre_documento}
                                </option>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="col-md-4">
                        <label class="form-label">Número de Documento</label>
                        <input type="text" name="num_documento" class="form-control" value="${usuario.nombre_documento}" required>
                    </div>

                    <div class="col-md-4">
                        <label class="form-label">Teléfono</label>
                        <input type="text" name="telefono" class="form-control" value="${usuario.telefono}" required>
                    </div>

                    <div class="col-md-8">
                        <label class="form-label">Dirección</label>
                        <input type="text" name="direccion" class="form-control" value="${usuario.direccion}" required>
                    </div>

                    <div class="col-md-4">
                        <label class="form-label">Rol (ID)</label>
                        <input type="number" name="idRol" class="form-control" value="${usuario.idRol}" required>
                    </div>

                    <div class="col-12 mt-4">
                        <button type="submit" class="btn btn-gold w-100">Actualizar Empleado</button>
                    </div>
                </form>
            </div>
        </main>
    </div>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>