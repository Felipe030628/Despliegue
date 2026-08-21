<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Detalle del Pedido | BarStock</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Vista/Css/Global.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Vista/Css/Pedidos.css">
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
                <li class="nav-item dropdown">
                    <a class="dropdown-toggle" href="#" role="button" data-bs-toggle="collapse" data-bs-target="#menuInventario">
                        <i class="bi bi-box-seam me-2"></i> Inventario
                    </a>
                    <ul class="collapse list-unstyled ps-3" id="menuInventario">
                        <li><a href="${pageContext.request.contextPath}/Producto?accion=listar">Productos</a></li>
                        <li><a href="${pageContext.request.contextPath}/Movimiento?accion=listar"><i class="bi bi-arrow-left-right me-3"></i> Movimientos Stock</a></li>
                    </ul>
                </li>
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
                    <a href="${pageContext.request.contextPath}/Pedido?accion=listar" class="logout-link">
                        <i class="bi bi-arrow-left me-2"></i> Volver al Listado
                    </a>
                </li>
            </ul>
        </aside>

        <main class="main-content">
            <div class="content-card">
                <h5>Pedido #${pedido.idPedido}</h5>

                <div class="info-pedido">
                    <div><span>Cliente</span><strong>${pedido.cliente}</strong></div>
                    <div><span>Mesa</span><strong>${pedido.mesa}</strong></div>
                    <div><span>Fecha</span><strong>${pedido.fecha}</strong></div>
                    <div><span>Estado</span><strong>${pedido.estado}</strong></div>
                </div>

                <table class="table table-striped align-middle">
                    <thead>
                        <tr>
                            <th>Producto</th>
                            <th>Precio Unit.</th>
                            <th>Cantidad</th>
                            <th>Subtotal</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="d" items="${detalles}">
                            <tr>
                                <td>${d.nombreProducto}</td>
                                <td>$${d.precioUnitario}</td>
                                <td>${d.cantidad}</td>
                                <td>$${d.subtotal}</td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty detalles}">
                            <tr class="carrito-vacio"><td colspan="4">Este pedido no tiene productos registrados.</td></tr>
                        </c:if>
                    </tbody>
                </table>

                <div class="total-carrito">
                    <span>Total del pedido</span>
                    <strong>$${pedido.total}</strong>
                </div>

                <div class="d-flex justify-content-end mt-3">
                    <button type="button" class="btn btn-gold px-4" onclick="window.open('${pageContext.request.contextPath}/Pedido?accion=factura&id=${pedido.idPedido}', '_blank')">
                        <i class="bi bi-file-earmark-pdf-fill me-1"></i> Descargar Factura PDF
                    </button>
                </div>
            </div>
        </main>
    </div>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
