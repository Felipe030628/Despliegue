<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Editar Movimiento | BarStock</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Vista/Css/Global.css">
</head>
<body>
    <div class="dashboard-container">
        <!-- Sidebar completo -->
        <aside class="sidebar">
            <div class="sidebar-header">
                <h3>BarStock</h3>
                <p>Inventario Inteligente</p>
            </div>
            <ul class="sidebar-menu">
                <li>
                    <a href="${pageContext.request.contextPath}/Vista/Panel.jsp"><i class="bi bi-sliders me-2"></i> Dashboard</a>
                </li>
                <li class="nav-item dropdown">
                    <a class="dropdown-toggle" href="#" role="button" data-bs-toggle="collapse" data-bs-target="#menuInventario">
                        <i class="bi bi-box-seam me-2"></i> Inventario
                    </a>
                    <ul class="collapse list-unstyled ps-3 show" id="menuInventario">
                        <li><a href="${pageContext.request.contextPath}/Producto?accion=listar">Productos</a></li>
                        <li><a href="${pageContext.request.contextPath}/Movimiento?accion=listar"><i class="bi bi-arrow-left-right me-3"></i> Movimientos Stock</a></li>
                    </ul>
                </li>
                <li class="mt-5">
                    <a href="${pageContext.request.contextPath}/Movimiento?accion=listar" class="logout-link">
                        <i class="bi bi-arrow-left me-2"></i> Volver al Listado
                    </a>
                </li>
            </ul>
        </aside>

        <!-- Contenido Principal -->
        <main class="main-content">
            <div class="content-card">
                <h5 class="mb-4">Editar Movimiento de Stock</h5>
                
                <form action="${pageContext.request.contextPath}/Movimiento?accion=actualizar" method="POST" class="row g-3">
                    
                    <input type="hidden" name="idMovimiento" value="${movimiento.idMovimiento}">

                    <div class="col-md-3">
                        <label class="form-label">Producto</label>
                        <select name="idProducto" class="form-select" required>
                            <option value="" disabled>Seleccione Producto</option>
                            <c:forEach var="p" items="${listaProd}">
                                <option value="${p.id}" ${p.id == movimiento.idProducto ? 'selected' : ''}>
                                    ${p.nombre}
                                </option>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="col-md-2">
                        <label class="form-label">Cantidad</label>
                        <input type="number" name="cantidad" class="form-control" value="${movimiento.cantidad}" required>
                    </div>

                    <div class="col-md-3">
                        <label class="form-label">Motivo</label>
                        <select name="motivo" class="form-select" required>
                            <option value="Entrada" selected>Entrada</option>
                        </select>
                    </div>

                    <div class="col-md-2">
                        <label class="form-label">Fecha</label>
                        <input type="date" name="fecha" class="form-control" value="${movimiento.fecha}" required>
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