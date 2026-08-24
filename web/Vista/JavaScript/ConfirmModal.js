// Modal de confirmación propio de la página (reemplaza el confirm() del navegador)
document.addEventListener('DOMContentLoaded', function () {
    var modalEl = document.getElementById('confirmModal');
    if (!modalEl) return;

    var modal = new bootstrap.Modal(modalEl);
    var msgEl = document.getElementById('confirmModalMessage');
    var confirmBtn = document.getElementById('confirmModalAcceptBtn');
    var pendingHref = null;

    document.querySelectorAll('[data-confirm-message]').forEach(function (el) {
        el.addEventListener('click', function (e) {
            e.preventDefault();
            pendingHref = el.getAttribute('href');
            msgEl.textContent = el.getAttribute('data-confirm-message');
            modal.show();
        });
    });

    confirmBtn.addEventListener('click', function () {
        modal.hide();
        if (pendingHref) {
            window.location.href = pendingHref;
        }
    });
});
