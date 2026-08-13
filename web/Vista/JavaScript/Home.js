// BarStock — interacciones de la página de inicio
document.addEventListener('DOMContentLoaded', function () {

    var prefersReducedMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches;

    // --- Revelado al hacer scroll -------------------------------------
    var revealItems = document.querySelectorAll('.reveal');

    if (prefersReducedMotion || !('IntersectionObserver' in window)) {
        revealItems.forEach(function (el) { el.classList.add('is-visible'); });
    } else {
        var revealObserver = new IntersectionObserver(function (entries) {
            entries.forEach(function (entry) {
                if (entry.isIntersecting) {
                    entry.target.classList.add('is-visible');
                    revealObserver.unobserve(entry.target);
                }
            });
        }, { threshold: 0.15 });

        revealItems.forEach(function (el) { revealObserver.observe(el); });
    }

    // --- Contadores de la franja de estadísticas -----------------------
    var counters = document.querySelectorAll('.stat-number');

    function animateCounter(el) {
        var target = parseInt(el.getAttribute('data-count'), 10) || 0;
        if (prefersReducedMotion) {
            el.textContent = target;
            return;
        }
        var duration = 1200;
        var start = null;

        function step(timestamp) {
            if (!start) { start = timestamp; }
            var progress = Math.min((timestamp - start) / duration, 1);
            var eased = 1 - Math.pow(1 - progress, 3);
            el.textContent = Math.floor(eased * target);
            if (progress < 1) {
                window.requestAnimationFrame(step);
            } else {
                el.textContent = target;
            }
        }
        window.requestAnimationFrame(step);
    }

    if (counters.length && 'IntersectionObserver' in window) {
        var counterObserver = new IntersectionObserver(function (entries) {
            entries.forEach(function (entry) {
                if (entry.isIntersecting) {
                    animateCounter(entry.target);
                    counterObserver.unobserve(entry.target);
                }
            });
        }, { threshold: 0.6 });

        counters.forEach(function (el) { counterObserver.observe(el); });
    } else {
        counters.forEach(function (el) { el.textContent = el.getAttribute('data-count'); });
    }

});
