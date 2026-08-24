<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Empleados | BarStock</title>
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
                <div class="d-flex justify-content-between align-items-center mb-3">
                    <h5>Lista de Empleados</h5>
                    <a href="Registro.jsp" class="btn btn-gold"><i class="bi bi-person-plus"></i> Nuevo Empleado</a>
                </div>
                
                <table class="table table-striped table-hover">
                    <thead class="table-dark">
                        <tr>
                            <th>ID</th>
                            <th>Nombre Completo</th>
                            <th>Correo</th>
                            <th>Documento</th>
                            <th>Rol</th>
                            <th>Estado</th>
                            <th class="text-center">Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${not empty listaUsuarios}">
                                <c:forEach var="u" items="${listaUsuarios}">
                                    <tr>
                                        <td>${u.idUsuarios}</td>
                                        <td>${u.nombre} ${u.apellido}</td>
                                        <td>${u.correo}</td>
                                        <td>${u.nombre_documento}</td>
                                        <td>
                                            <span class="badge bg-secondary">${u.idRol}</span>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${u.activo == 1}">
                                                    <span class="badge bg-success">Activo</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge bg-danger">Inactivo</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td class="text-center">
                                            <!-- Botón de Editar -->
                                            <a href="${pageContext.request.contextPath}/UsuariosCont?accion=cargar&id=${u.idUsuarios}" class="btn btn-warning btn-sm" title="Editar">
                                                <i class="bi bi-pencil-fill"></i>
                                            </a>
                                            
                                            <!-- Botón de Activar / Inactivar (Baja lógica) -->
                                            <c:choose>
                                                <c:when test="${u.activo == 1}">
                                                    <a href="${pageContext.request.contextPath}/UsuariosCont?accion=cambiarEstado&id=${u.idUsuarios}&activo=0" 
                                                       class="btn btn-danger btn-sm" 
                                                       title="Desactivar"
                                                       data-confirm-message="¿Estás seguro de desactivar a ${u.nombre}? Ya no podrá iniciar sesión.">
                                                        <i class="bi bi-toggle-on"></i>
                                                    </a>
                                                </c:when>
                                                <c:otherwise>
                                                    <a href="${pageContext.request.contextPath}/UsuariosCont?accion=cambiarEstado&id=${u.idUsuarios}&activo=1" 
                                                       class="btn btn-success btn-sm" 
                                                       title="Activar"
                                                       data-confirm-message="¿Deseas activar nuevamente a ${u.nombre}?">
                                                        <i class="bi bi-toggle-off"></i>
                                                    </a>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <tr>
                                    <td colspan="7" class="text-center">No hay empleados registrados.</td>
                                </tr>
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                </table>
            </div>
        </main>
    </div>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <%@ include file="ConfirmModal.jsp" %>
</body>
</html>