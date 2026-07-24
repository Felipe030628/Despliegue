<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Editar Producto | BarStock</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Vista/Css/Global.css">
</head>
<body>
    <div class="dashboard-container">
        <!-- Sidebar (Mantiene tu misma estructura de navegación) -->
        <aside class="sidebar">
            <div class="sidebar-header">
                <h3>BarStock</h3>
                <p>Inventario Inteligente</p>
            </div>
            <ul class="sidebar-menu">
                <li><a href="Vista/Panel.jsp"><i class="bi bi-sliders me-2"></i> Dashboard</a></li>
                <li class="nav-item dropdown">
                    <a class="dropdown-toggle" href="#" role="button" data-bs-toggle="collapse" data-bs-target="#menuInventario">
                        <i class="bi bi-box-seam me-2"></i> Inventario
                    </a>
                    <ul class="collapse list-unstyled ps-3" id="menuInventario">
                        <li><a href="${pageContext.request.contextPath}/Producto?accion=listar">Productos</a></li>
                        <li><a href="${pageContext.request.contextPath}/Movimiento?accion=listar"><i class="bi bi-arrow-left-right me-3"></i> Movimientos Stock</a></li>
                    </ul>
                </li>
                <li class="mt-5">
                    <a href="${pageContext.request.contextPath}/Producto?accion=listar" class="logout-link">
                        <i class="bi bi-arrow-left me-2"></i> Volver
                    </a>
                </li>
            </ul>
        </aside>

        <!-- Contenido Principal: Formulario de Edición -->
        <main class="main-content">
            <div class="content-card">
                <h5 class="mb-4">Editar Producto</h5>
                
                <!-- El formulario apunta a la acción actualizar del Servlet e incluye el ID oculto -->
                <form action="${pageContext.request.contextPath}/Producto?accion=actualizar" method="POST" class="row g-3">
                    
                    <!-- Campo oculto indispensable para que el DAO sepa qué producto modificar -->
                    <input type="hidden" name="id" value="${producto.id}">

                    <div class="col-md-3">
                        <label class="form-label">Nombre</label>
                        <input type="text" name="nombre" class="form-control" value="${producto.nombre}" required>
                    </div>

                    <div class="col-md-2">
                        <label class="form-label">Precio</label>
                        <input type="number" step="0.01" name="precio" class="form-control" value="${producto.precio}" required>
                    </div>

                    <div class="col-md-3">
                        <label class="form-label">Vencimiento</label>
                        <input type="date" name="fecha_vencimiento" class="form-control" value="${producto.fecha_vencimiento}" required>
                    </div>

                    <div class="col-md-2">
                        <label class="form-label">Categoría</label>
                        <select name="idCategoria" class="form-control" required>
                            <option value="">Seleccione...</option>
                            <c:forEach var="cat" items="${listaCategorias}">
                                <!-- Selecciona automáticamente la categoría que ya tenía asignada el producto -->
                                <option value="${cat.idCategorias}" ${cat.idCategorias == producto.idCategoria ? 'selected' : ''}>
                                    ${cat.nombre_categoria}
                                </option>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="col-md-2 d-flex align-items-end">
                        <button type="submit" class="btn btn-gold w-100">Actualizar</button>
                    </div>
                </form>
            </div>
        </main>
    </div>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>