<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>BarStock | Gestión Premium</title>

    <link href="https://fonts.googleapis.com/css2?family=Great+Vibes&family=Cormorant+Garamond:wght@500;600;700&family=Montserrat:wght@300;400;500;700&display=swap" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">

    <link rel="stylesheet" href="Vista/Css/Home.css">
</head>
<body>

    <!-- RUIDO / TEXTURA AMBIENTE -->
    <div class="bar-atmosphere" aria-hidden="true">
        <span class="ember ember-1"></span>
        <span class="ember ember-2"></span>
        <span class="ember ember-3"></span>
    </div>

    <!-- NAVBAR -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark-premium sticky-top">
        <div class="container">

            <a class="navbar-brand logo-mini" href="#inicio">
                BarStock
            </a>

            <button class="navbar-toggler"
                    type="button"
                    data-bs-toggle="collapse"
                    data-bs-target="#navbarNav">
                <span class="navbar-toggler-icon"></span>
            </button>

            <div class="collapse navbar-collapse" id="navbarNav">

                <ul class="navbar-nav me-auto">

                    <li class="nav-item">
                        <a class="nav-link" href="#inicio">Inicio</a>
                    </li>

                    <li class="nav-item">
                        <a class="nav-link" href="#historia">Historia</a>
                    </li>

                    <li class="nav-item">
                        <a class="nav-link" href="#ficha">Ficha Técnica</a>
                    </li>

                    <li class="nav-item">
                        <a class="nav-link" href="#modulos">Módulos</a>
                    </li>

                    <li class="nav-item">
                        <a class="nav-link" href="#confianza">Confianza</a>
                    </li>

                </ul>

                <div class="d-flex gap-2">

                    <a href="Login.jsp" class="btn-login-nav">
                        INICIAR SESIÓN
                    </a>

                    <a href="RegistroCont"
                       class="btn-login-nav btn-login-nav--outline">
                        REGISTRARSE
                    </a>

                </div>

            </div>
        </div>
    </nav>

    <!-- CARRUSEL -->
    <div id="inicio"
         class="carousel slide carousel-fade"
         data-bs-ride="carousel">

        <div class="carousel-inner">

            <!-- ITEM 1 -->
            <div class="carousel-item active">

                <img src="https://images.unsplash.com/photo-1514362545857-3bc16c4c7d1b?q=80&w=1470"
                     class="d-block w-100"
                     alt="Cocktail">

                <div class="carousel-overlay"></div>

                <div class="carousel-caption">

                    <span class="eyebrow">Software de gestión para bares</span>

                    <h1 class="logo-text">BarStock</h1>

                    <p class="fancy-font">
                        Excelencia en control, pasión por el servicio
                    </p>

                    <h2 class="welcome-msg">
                        BIENVENIDO AL FUTURO DE TU BAR
                    </h2>

                    <a href="#ficha" class="btn-scroll-cue" aria-label="Ver más">
                        <span></span>
                    </a>

                </div>
            </div>

            <!-- ITEM 2 -->
            <div class="carousel-item">

                <img src="https://images.unsplash.com/photo-1470337458703-46ad1756a187?q=80&w=1469"
                     class="d-block w-100"
                     alt="Bar">

                <div class="carousel-overlay"></div>

                <div class="carousel-caption">

                    <span class="eyebrow">Del inventario a la barra, sin fugas</span>

                    <h1 class="logo-text">BarStock</h1>

                    <p class="fancy-font">
                        Inventario Inteligente
                    </p>

                    <h2 class="welcome-msg">
                        CONTROL EN TIEMPO REAL
                    </h2>

                    <a href="#ficha" class="btn-scroll-cue" aria-label="Ver más">
                        <span></span>
                    </a>

                </div>
            </div>

            <!-- ITEM 3 -->
            <div class="carousel-item">

                <img src="https://images.unsplash.com/photo-1470337458703-46ad1756a187?q=80&w=1470&sat=-20"
                     class="d-block w-100"
                     alt="Barra de coctelería">

                <div class="carousel-overlay"></div>

                <div class="carousel-caption">

                    <span class="eyebrow">Diseñado con quienes trabajan la barra</span>

                    <h1 class="logo-text">BarStock</h1>

                    <p class="fancy-font">
                        Precisión de bodega, elegancia de salón
                    </p>

                    <h2 class="welcome-msg">
                        CADA BOTELLA, BAJO CONTROL
                    </h2>

                    <a href="#ficha" class="btn-scroll-cue" aria-label="Ver más">
                        <span></span>
                    </a>

                </div>
            </div>

        </div>

        <button class="carousel-control-prev" type="button" data-bs-target="#inicio" data-bs-slide="prev">
            <span class="custom-arrow custom-arrow-left" aria-hidden="true"></span>
            <span class="visually-hidden">Anterior</span>
        </button>
        <button class="carousel-control-next" type="button" data-bs-target="#inicio" data-bs-slide="next">
            <span class="custom-arrow custom-arrow-right" aria-hidden="true"></span>
            <span class="visually-hidden">Siguiente</span>
        </button>
    </div>

    <!-- BARRA DE INDICADORES -->
    <section class="stats-strip">
        <div class="container">
            <div class="row g-4 text-center">

                <div class="col-6 col-md-3 stat-item reveal">
                    <span class="stat-number" data-count="30">0</span><span class="stat-suffix">%</span>
                    <p class="stat-label">Menos mermas de inventario</p>
                </div>

                <div class="col-6 col-md-3 stat-item reveal">
                    <span class="stat-number" data-count="100">0</span><span class="stat-suffix">%</span>
                    <p class="stat-label">Sincronización en tiempo real</p>
                </div>

                <div class="col-6 col-md-3 stat-item reveal">
                    <span class="stat-number" data-count="3">0</span><span class="stat-suffix">&nbsp;roles</span>
                    <p class="stat-label">Niveles de acceso configurables</p>
                </div>

                <div class="col-6 col-md-3 stat-item reveal">
                    <span class="stat-number" data-count="24">0</span><span class="stat-suffix">/7</span>
                    <p class="stat-label">Disponibilidad del sistema</p>
                </div>

            </div>
        </div>
    </section>

    <!-- FICHA TECNICA -->
    <section id="ficha" class="container my-5 py-5">

        <div class="row align-items-center">

            <div class="col-md-6 reveal">

                <h3 class="gold-text">FICHA TÉCNICA</h3>

                <div class="gold-line"></div>

                <ul class="tech-list text-light">

                    <li>
                        <strong>Propósito:</strong>
                        Gestión de stock y ventas para bares.
                    </li>

                    <li>
                        <strong>Tecnología:</strong>
                        Arquitectura MVC con Java EE y Servlets.
                    </li>

                    <li>
                        <strong>Base de Datos:</strong>
                        MySQL Workbench (Usuarios y Roles).
                    </li>

                    <li>
                        <strong>Interfaz:</strong>
                        Diseño Premium Lounge con Bootstrap 5.
                    </li>

                </ul>

            </div>

            <div class="col-md-6 reveal">

                <div class="tech-card text-center">

                    <span class="tech-card-ring" aria-hidden="true"></span>

                    <h4 class="gold-text">BARSTOCK v1.0</h4>

                    <hr class="border-gold">

                    <p class="small">
                        Sincronización de Inventarios
                    </p>

                    <p class="small text-light-50 mb-0">
                        Compilado y pensado para operar cada noche, sin pausas ni sorpresas de última hora.
                    </p>

                </div>

            </div>

        </div>

    </section>

    <!-- HISTORIA -->
    <section id="historia" class="container my-5 py-5">

        <div class="row align-items-center">

            <div class="col-md-7 reveal">

                <h3 class="gold-text">NUESTRA HISTORIA</h3>

                <div class="gold-line"></div>

                <p class="text-light lead">
                    BarStock nació de la observación directa en el sector nocturno de la ciudad.
                    Notamos que muchos bares aún dependen de procesos manuales y registros en papel.
                </p>

                <div class="timeline-mini">
                    <span class="timeline-dot"></span>
                    <span class="timeline-dot"></span>
                    <span class="timeline-dot"></span>
                </div>

                <div class="problem-box p-4 mb-4">

                    <h5 class="text-danger">La Problemática:</h5>

                    <p class="small text-light">
                        La falta de un control digital genera "fugas" de inventario,
                        errores en el flujo de caja y una desconexión total entre lo que hay
                        en bodega y lo que se vende en la barra.
                        Esto resulta en pérdidas económicas significativas y una mala experiencia
                        para el cliente.
                    </p>

                </div>

                <p class="text-light">
                    <strong>Nuestra base:</strong>
                    Nos inspiramos en la eficiencia de los grandes sistemas logísticos
                    para traer esa misma precisión a los bares locales,
                    facilitando una herramienta robusta pero intuitiva.
                </p>

                <blockquote class="quote-line">
                    <span class="fancy-font">“Lo que no se mide, se pierde entre la barra y la bodega.”</span>
                </blockquote>

            </div>

            <div class="col-md-5 text-center reveal">

                <div class="history-decoration">

                    <span class="fancy-font" style="font-size: 3rem;">
                        Desde la Barra
                    </span>

                    <p class="gold-text">
                        Hacia la Digitalización
                    </p>

                </div>

            </div>

        </div>

    </section>

    <!-- MODULOS -->
    <section id="modulos" class="container my-5 py-5">

        <h3 class="gold-text text-center">
            MÓDULOS DEL SISTEMA
        </h3>

        <p class="text-center text-light-50 module-subtitle mx-auto">
            Cuatro piezas trabajando juntas para que la barra y la bodega hablen el mismo idioma.
        </p>

        <div class="gold-line mx-auto"></div>

        <div class="row mt-5 text-center g-4">

            <div class="col-md-3 col-sm-6 reveal">

                <div class="modulo-box">
                    <span class="modulo-badge">📦</span>
                    <h5>Inventario</h5>

                    <p class="small">
                        Control de botellas y suministros, con alertas antes de que falten.
                    </p>

                </div>

            </div>

            <div class="col-md-3 col-sm-6 reveal">

                <div class="modulo-box">
                    <span class="modulo-badge">👥</span>
                    <h5>Usuarios</h5>

                    <p class="small">
                        Gestión de roles y permisos para cada miembro del equipo.
                    </p>

                </div>

            </div>

            <div class="col-md-3 col-sm-6 reveal">

                <div class="modulo-box">
                    <span class="modulo-badge">💰</span>
                    <h5>Ventas</h5>

                    <p class="small">
                        Registro de consumo en tiempo real, directo desde la barra.
                    </p>

                </div>

            </div>

            <div class="col-md-3 col-sm-6 reveal">

                <div class="modulo-box">
                    <span class="modulo-badge">📊</span>
                    <h5>Reportes</h5>

                    <p class="small">
                        Cierres y estadísticas claras para decidir con datos, no con corazonadas.
                    </p>

                </div>

            </div>

        </div>

    </section>

    <!-- CONFIANZA / TESTIMONIO -->
    <section id="confianza" class="confianza-section py-5">
        <div class="container text-center">

            <span class="quote-mark" aria-hidden="true">&ldquo;</span>

            <p class="fancy-font testimonio-texto">
                Desde que digitalizamos la barra dejamos de perseguir botellas
                y empezamos a leer números
            </p>

            <p class="testimonio-autor gold-text">— Equipo de barra, sector nocturno</p>

        </div>
    </section>

    <!-- CTA -->
    <section class="join-team-section text-center py-5 my-5">

        <div class="container">

            <h3 class="fancy-font CTA-title">
                ¿Quieres ser parte del equipo de BarStock?
            </h3>

            <p class="text-light text-muted-premium mb-4 mx-auto"
               style="max-width: 600px;">

                Regístrate hoy mismo y descubre la manera más ágil,
                elegante y profesional de administrar el inventario
                y las ventas de tu negocio nocturno.

            </p>

            <a href="Registro.jsp" class="btn-gold-cta">
                CREAR MI CUENTA
            </a>

            <div class="cta-tags">
                <span>⚡ Rápido de implementar</span>
                <span>🔒 Roles y accesos seguros</span>
                <span>🥂 Pensado para tu barra</span>
            </div>

        </div>

    </section>

    <!-- FOOTER -->
    <footer class="site-footer text-center pt-5 pb-4 border-top border-secondary mt-5">

        <a class="logo-mini d-inline-block mb-2" href="#inicio">BarStock</a>

        <p class="small text-light-50 footer-tagline mx-auto mb-3">
            Control de inventario y ventas para bares que no quieren perder ni una gota.
        </p>

        <div class="footer-links mb-3">
            <a href="#inicio">Inicio</a>
            <a href="#historia">Historia</a>
            <a href="#ficha">Ficha Técnica</a>
            <a href="#modulos">Módulos</a>
        </div>

        <p class="small text-muted mb-0">
            &copy; 2026 BarStock - Proyecto Académico SENA
        </p>

    </footer>

    <!-- SCRIPTS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script src="Vista/JavaScript/Home.js"></script>

</body>
</html>
