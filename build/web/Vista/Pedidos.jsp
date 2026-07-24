<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Pedidos | BarStock</title>
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
                    <a href="${pageContext.request.contextPath}/index.jsp" class="logout-link">
                        <i class="bi bi-power me-2"></i> Cerrar Sesión
                    </a>
                </li>
            </ul>
        </aside>

        <main class="main-content">
            <div class="content-card">
                <h5>Nuevo Pedido</h5>
                <form action="${pageContext.request.contextPath}/Pedido?accion=guardar" method="POST" class="row g-3">
                    <div class="col-md-3"><input type="text" name="cliente" class="form-control" placeholder="Nombre Cliente" required></div>
                    
                    <div class="col-md-2">
                        <select name="mesa" class="form-select" required>
                            <option value="" disabled selected>Seleccione Mesa</option>
                            <c:forEach var="m" items="${listaMesas}">
                                <option value="${m.numero_mesa}">${m.numero_mesa} (Cap: ${m.capacidad})</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="col-md-3"><input type="datetime-local" name="fecha" class="form-control" required></div>
                    <div class="col-md-2"><input type="number" step="0.01" name="total" class="form-control" placeholder="Total" required></div>
                    <div class="col-md-2"><button type="submit" class="btn btn-gold w-100">Registrar</button></div>
                </form>
            </div>

            <div class="content-card">
                <table class="table table-striped align-middle">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Cliente</th>
                            <th>Mesa</th>
                            <th>Fecha</th>
                            <th>Estado</th>
                            <th>Total</th>
                            <th class="text-center">Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="p" items="${lista}">
                            <tr>
                                <td>${p.idPedido}</td>
                                <td>${p.cliente}</td>
                                <td>${p.mesa}</td>
                                <td>${p.fecha}</td>
                                <td>${p.estado}</td>
                                <td>$${p.total}</td>
                                <td>
    <button type="button" class="btn btn-warning btn-sm" onclick="location.href='${pageContext.request.contextPath}/Pedido?accion=cargar&id=${p.idPedido}'">
        <i class="bi bi-pencil-fill"></i>
    </button>
        <a href="${pageContext.request.contextPath}/Pedido?accion=eliminar&id=${p.idPedido}" 
       class="btn btn-danger btn-sm" 
       title="Eliminar"
       onclick="return confirm('¿Estás seguro de que deseas eliminar este pedido?');">
        <i class="bi bi-trash-fill"></i>
    </a>
</td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </main>
    </div>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>