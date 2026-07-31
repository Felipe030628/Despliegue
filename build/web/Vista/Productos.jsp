<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Gestión de Inventario | BarStock</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Vista/Css/Global.css">
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
        <ul class="collapse list-unstyled ps-3" id="menuInventario">
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
            <div class="content-card">
                <h5>Registrar Nuevo Producto</h5>
                <form action="${pageContext.request.contextPath}/Producto?accion=guardar" method="POST" class="row g-3" id="formProducto">
                    <div class="col-md-3"><input type="text" name="nombre" class="form-control" placeholder="Nombre" required></div>
                    <div class="col-md-2"><input type="number" step="0.01" name="precio" class="form-control" placeholder="Precio" required></div>
                    <div class="col-md-3"><input type="date" name="fecha_vencimiento" class="form-control" required></div>
                    <div class="col-md-2">
<select name="idCategoria" class="form-control" required>
    <option value="">Seleccione una categoría...</option>
    <c:forEach var="cat" items="${listaCategorias}">
        <option value="${cat.idCategorias}">${cat.nombre_categoria}</option>
    </c:forEach>
</select>
                    </div>
                    <div class="col-md-2"><button type="submit" class="btn btn-gold w-100">Guardar</button></div>
                </form>
            </div>

            <div class="content-card">
                <table class="table table-striped">
                    <thead><tr><th>ID</th><th>Nombre</th><th>Precio</th><th>Vencimiento</th>
                        <th>Acciones</th></thead>
                    <tbody>
                        <c:forEach var="p" items="${lista}">
                            <tr><td>${p.id}</td><td>${p.nombre}</td><td>$ ${p.precio}</td><td>${p.fecha_vencimiento}</td>
                            <td><a href="${pageContext.request.contextPath}/Producto?accion=cargar&id=${p.id}" class="btn btn-warning btn-sm" title="Editar">
                                        <i class="bi bi-pencil-fill"></i>
                                    </a>
                            <a href="${pageContext.request.contextPath}/Producto?accion=eliminar&id=${p.id}" 
       class="btn btn-danger btn-sm" 
       title="Eliminar"
       onclick="return confirm('¿Estás seguro de que deseas eliminar este producto?');">
        <i class="bi bi-trash-fill"></i>
    </a></td>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </main>
    </div>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script src="${pageContext.request.contextPath}/Vista/Js/Productos.js"></script>
</body>
</html>