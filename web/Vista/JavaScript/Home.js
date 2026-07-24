document.addEventListener('DOMContentLoaded', function() {
    // Configuración del Carrusel (4 segundos por imagen)
    const myCarousel = document.querySelector('#carouselBarstock');
    if (myCarousel) {
        new bootstrap.Carousel(myCarousel, {
            interval: 4000,
            pause: 'hover'
        });
    }

    // Scroll suave para los enlaces del menú
    document.querySelectorAll('.nav-link').forEach(link => {
        link.addEventListener('click', function(e) {
            const targetId = this.getAttribute('href');
            if (targetId.startsWith('#')) {
                e.preventDefault();
                const element = document.querySelector(targetId);
                if (element) {
                    window.scrollTo({
                        top: element.offsetTop - 70,
                        behavior: 'smooth'
                    });
                }
            }
        });
    });
});