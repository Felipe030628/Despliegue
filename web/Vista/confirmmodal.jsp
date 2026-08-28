<%-- Modal de confirmaci?n reutilizable: incluir con <%@ include file="ConfirmModal.jsp" %> antes de </body> --%>
<div class="modal fade" id="confirmModal" tabindex="-1" aria-labelledby="confirmModalLabel" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="confirmModalLabel">
            <i class="bi bi-exclamation-triangle-fill text-danger me-2"></i>Confirmar accion
        </h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Cerrar"></button>
      </div>
      <div class="modal-body">
        <p id="confirmModalMessage" class="mb-0">¿Estas seguro?</p>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
        <button type="button" id="confirmModalAcceptBtn" class="btn btn-danger">Si, continuar</button>
      </div>
    </div>
  </div>
</div>
<script src="${pageContext.request.contextPath}/Vista/JavaScript/ConfirmModal.js"></script>
