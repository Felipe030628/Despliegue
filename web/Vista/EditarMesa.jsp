<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Editar Mesa | BarStock</title>
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
                    <ul class="collapse list-unstyled ps-3 show" id="menuPedidos">
                        <li><a href="${pageContext.request.contextPath}/Pedido?accion=listar"><i class="bi bi-cart me-3"></i> Pedidos</a></li>
                        <li><a href="${pageContext.request.contextPath}/Mesas?accion=listar"><i class="bi bi-ui-checks-grid me-3"></i> Mesas</a></li>
                    </ul>
                </li>

                <li>
                    <a href="${pageContext.request.contextPath}/UsuariosCont?accion=listar">
                        <i class="bi bi-people me-3"></i> Empleados
                    </a>
                </li>

                <li class="mt-5">
                    <a href="${pageContext.request.contextPath}/Mesas?accion=listar" class="logout-link">
                        <i class="bi bi-arrow-left me-2"></i> Volver al Listado
                    </a>
                </li>
            </ul>
        </aside>

        <main class="main-content">
            <div class="content-card">
                <h5 class="mb-4">Editar Mesa</h5>
                
                <form action="${pageContext.request.contextPath}/Mesas?accion=actualizar" method="POST" class="row g-3">
                    
                    <!-- ID Oculto de la Mesa -->
                    <input type="hidden" name="idMesa" value="${mesa.idMesa}">

                    <div class="col-md-5">
                        <label class="form-label">Nombre de la Mesa</label>
                        <input type="text" name="numero" class="form-control" value="${mesa.numero_mesa}" required>
                    </div>
                    
                    <div class="col-md-3">
                        <label class="form-label">Capacidad</label>
                        <input type="number" name="capacidad" class="form-control" value="${mesa.capacidad}" required>
                    </div>

                    <div class="col-md-4 d-flex align-items-end">
                        <button type="submit" class="btn btn-gold w-100">Actualizar Mesa</button>
                    </div>
                    <div class="col-md-4">
    <label class="form-label">Estado de la Mesa</label>
    <select name="estado" class="form-select" required>
        <option value="Libre" ${mesa.estado == 'Libre' ? 'selected' : ''}>Libre</option>
        <option value="Ocupado" ${mesa.estado == 'Ocupado' ? 'selected' : ''}>Ocupado</option>
        <option value="Reservado" ${mesa.estado == 'Reservado' ? 'selected' : ''}>Reservado</option>
    </select>
</div>
                </form>
            </div>
        </main>
    </div>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
