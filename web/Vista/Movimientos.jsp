<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Movimientos de Stock | BarStock</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Vista/Css/Global.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Vista/Css/Movimientos.css">
</head>
<body>
    <div class="dashboard-container">
        <aside class="sidebar">
            <div class="sidebar-header"><h3>BarStock</h3>
            <p>Inventario Inteligente</p></div>
            <ul class="sidebar-menu">
    <li>
        <a href="Vista/Panel.jsp"><i class="bi bi-sliders me-2"></i> Dashboard</a>
    </li>

    <!-- Menú Desplegable: Inventario -->
    <li class="nav-item dropdown">
        <a class="dropdown-toggle" href="#" role="button" data-bs-toggle="collapse" data-bs-target="#menuInventario">
            <i class="bi bi-box-seam me-2"></i> Inventario
        </a>
        <ul class="collapse list-unstyled ps-3 show" id="menuInventario">
            <!-- Enlace para acceder a la gestión de productos -->
<li>
    <a href="${pageContext.request.contextPath}/Producto?accion=listar">Productos</a>
</li>
            <li><a href="${pageContext.request.contextPath}/Movimiento?accion=listar"><i class="bi bi-arrow-left-right me-3"></i> Movimientos Stock</a></li>
        </ul>
    </li>

    <!-- Menú Desplegable: Gestión de Pedidos -->
    <li class="nav-item dropdown">
        <a class="dropdown-toggle" href="#" role="button" data-bs-toggle="collapse" data-bs-target="#menuPedidos">
            <i class="bi bi-cart-check me-2"></i> Pedidos
        </a>
        <ul class="collapse list-unstyled ps-3" id="menuPedidos">
            <li><a href="${pageContext.request.contextPath}/Pedido?accion=listar">
        <i class="bi bi-cart me-3"></i> Pedidos
    </a></li>
    <li>
    <a href="${pageContext.request.contextPath}/Mesas?accion=listar">
        <i class="bi bi-ui-checks-grid me-3"></i> Mesas
    </a>
</li>
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

            <!-- STOCK GENERAL: foto actual del inventario, se recalcula con cada
                 movimiento manual y con cada venta/anulación de pedido -->
            <div class="content-card">
                <h5>Stock General</h5>
                <table class="table table-striped align-middle tabla-stock-general">
                    <thead>
                        <tr>
                            <th>Producto</th>
                            <th>Precio</th>
                            <th>Stock Actual</th>
                            <th>Estado</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="s" items="${listaStock}">
                            <tr>
                                <td>${s.nombre}</td>
                                <td>$${s.precio}</td>
                                <td>${s.stock}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${s.stock <= 0}">
                                            <span class="badge-stock" data-status="agotado">Agotado</span>
                                        </c:when>
                                        <c:when test="${s.stock <= 5}">
                                            <span class="badge-stock" data-status="bajo">Stock bajo</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge-stock" data-status="ok">Disponible</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty listaStock}">
                            <tr><td colspan="4" class="text-center">No hay productos registrados todavía.</td></tr>
                        </c:if>
                    </tbody>
                </table>
            </div>

            <div class="content-card">
                <h5>Registrar Movimiento de Stock</h5>
                <form action="${pageContext.request.contextPath}/Movimiento?accion=guardar" method="POST" class="row g-3">
                    <div class="col-md-3">
                        <select name="idProducto" class="form-select" required>
                            <option value="" disabled selected>Seleccione Producto</option>
                            <c:forEach var="p" items="${listaProd}">
                                <option value="${p.id}">${p.nombre}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="col-md-2">
                        <input type="number" name="cantidad" class="form-control" placeholder="Cantidad" required>
                    </div>
                    <div class="col-md-3">
                        <input type="text" name="motivo" class="form-control" value="Entrada" readonly>
                    </div>
                    <div class="col-md-2">
                        <input type="date" name="fecha" class="form-control" required>
                    </div>
                    <div class="col-md-2">
                        <button type="submit" class="btn btn-gold w-100">Registrar</button>
                    </div>
                </form>
            </div>

            <div class="content-card">
                <h5>Historial de Movimientos</h5>
                <table class="table table-striped">
                    <thead>
                        <tr>
                            <th>ID Mov.</th>
                            <th>ID Prod.</th>
                            <th>Cantidad</th>
                            <th>Motivo</th>
                            <th>Fecha</th>
                            <th>Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
    <c:forEach var="m" items="${listaMov}">
        <tr>
            <td>${m.idMovimiento}</td>
            <td>${m.idProducto}</td>
            <td>${m.cantidad}</td>
            <td>${m.motivo}</td>
            <td>${m.fecha}</td>
            <td>
                <button type="button" class="btn btn-warning btn-sm" onclick="location.href='${pageContext.request.contextPath}/Movimiento?accion=cargar&id=${m.idMovimiento}'">
                    <i class="bi bi-pencil-fill"></i>
                </button>
                    <a href="${pageContext.request.contextPath}/Movimiento?accion=eliminar&id=${m.idMovimiento}" 
       class="btn btn-danger btn-sm" 
       title="Eliminar"
       onclick="return confirm('¿Estás seguro de que deseas eliminar este movimiento?');">
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
