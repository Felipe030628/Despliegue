<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Gestión de Inventario | BarStock</title>
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

            <p class="sidebar-eyebrow">Panel</p>
            <ul class="sidebar-menu">
                <li>
                    <a href="${pageContext.request.contextPath}/Vista/Panel.jsp"><i class="bi bi-sliders"></i> Dashboard</a>
                </li>
            </ul>

            <p class="sidebar-eyebrow">Gestión</p>
            <ul class="sidebar-menu">
                <!-- Menú Desplegable: Inventario -->
                <li class="nav-item dropdown">
                    <a class="dropdown-toggle" href="#" role="button" data-bs-toggle="collapse"
                       data-bs-target="#menuInventario" aria-expanded="true">
                        <i class="bi bi-box-seam"></i> Inventario
                    </a>
                    <ul class="collapse show" id="menuInventario">
                        <li>
                            <a href="${pageContext.request.contextPath}/Producto?accion=listar" class="active">Productos</a>
                        </li>
                        <li>
                            <a href="${pageContext.request.contextPath}/Movimiento?accion=listar">
                                <i class="bi bi-arrow-left-right"></i> Movimientos Stock
                            </a>
                        </li>
                    </ul>
                </li>

                <!-- Menú Desplegable: Gestión de Pedidos -->
                <li class="nav-item dropdown">
                    <a class="dropdown-toggle" href="#" role="button" data-bs-toggle="collapse" data-bs-target="#menuPedidos">
                        <i class="bi bi-cart-check"></i> Pedidos
                    </a>
                    <ul class="collapse" id="menuPedidos">
                        <li>
                            <a href="${pageContext.request.contextPath}/Pedido?accion=listar">
                                <i class="bi bi-cart"></i> Pedidos
                            </a>
                        </li>
                        <li>
                            <a href="${pageContext.request.contextPath}/Mesas?accion=listar">
                                <i class="bi bi-ui-checks-grid"></i> Mesas
                            </a>
                        </li>
                    </ul>
                </li>

                <li>
                    <a href="${pageContext.request.contextPath}/UsuariosCont?accion=listar">
                        <i class="bi bi-people"></i> Empleados
                    </a>
                </li>
            </ul>

            <ul class="sidebar-menu mt-5">
                <li>
                    <a href="${pageContext.request.contextPath}/index.jsp" class="logout-link">
                        <i class="bi bi-power"></i> Cerrar Sesión
                    </a>
                </li>
            </ul>
        </aside>

        <main class="main-content">
            <div class="page-header">
                <p class="eyebrow">Inventario / Productos</p>
                <h1>Gestión de Productos</h1>
                <p>Registra, edita y controla las existencias de tu barra.</p>
            </div>

            <div class="content-card">
                <h5>Registrar Nuevo Producto</h5>
                <form action="${pageContext.request.contextPath}/Producto?accion=guardar" method="POST" class="row g-3" id="formProducto">
                    <div class="col-md-3">
                        <label class="form-label">Nombre</label>
                        <input type="text" name="nombre" class="form-control" placeholder="Ej. Ron Añejo 750ml" required>
                    </div>
                    <div class="col-md-2">
                        <label class="form-label">Precio</label>
                        <input type="number" step="0.01" name="precio" class="form-control" placeholder="0.00" required>
                    </div>
                    <div class="col-md-3">
                        <label class="form-label">Vencimiento</label>
                        <input type="date" name="fecha_vencimiento" class="form-control" required>
                    </div>
                    <div class="col-md-2">
                        <label class="form-label">Categoría</label>
                        <select name="idCategoria" class="form-select" required>
                            <option value="">Seleccione...</option>
                            <c:forEach var="cat" items="${listaCategorias}">
                                <option value="${cat.idCategorias}">${cat.nombre_categoria}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="col-md-2 d-flex align-items-end">
                        <button type="submit" class="btn btn-gold w-100">
                            <i class="bi bi-check2"></i> Guardar
                        </button>
                    </div>
                </form>
            </div>

            <div class="content-card">
                <h5>Catálogo Actual</h5>
                <c:choose>
                    <c:when test="${empty lista}">
                        <div class="empty-state">
                            <i class="bi bi-inboxes"></i>
                            Aún no hay productos registrados.
                        </div>
                    </c:when>
                    <c:otherwise>
                        <table class="table table-striped table-hover">
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Nombre</th>
                                    <th>Precio</th>
                                    <th>Vencimiento</th>
                                    <th>Acciones</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="p" items="${lista}">
                                    <tr>
                                        <td>${p.id}</td>
                                        <td>${p.nombre}</td>
                                        <td>$ ${p.precio}</td>
                                        <td><span class="badge-venc">${p.fecha_vencimiento}</span></td>
                                        <td>
                                            <a href="${pageContext.request.contextPath}/Producto?accion=cargar&id=${p.id}"
                                               class="btn-icon edit" title="Editar">
                                                <i class="bi bi-pencil-fill"></i>
                                            </a>
                                            <a href="${pageContext.request.contextPath}/Producto?accion=eliminar&id=${p.id}"
                                               class="btn-icon delete" title="Eliminar"
                                               data-confirm-message="¿Estás seguro de que deseas eliminar este producto?">
                                                <i class="bi bi-trash-fill"></i>
                                            </a>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </c:otherwise>
                </c:choose>
            </div>
        </main>
    </div>
    <%@ include file="confirmmodal.jsp" %>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script src="${pageContext.request.contextPath}/Vista/Js/Productos.js"></script>
</body>
</html>
