import { X, AlertTriangle, CheckCircle2, ChevronLeft, ChevronRight } from 'lucide-react';

/** Ventana modal genérica usada por los formularios de creación/edición y las vistas de detalle. */
export function Modal({ title, subtitle, onClose, children, width }) {
  return (
    <div className="modal-overlay" onMouseDown={onClose}>
      <div className="modal-card" style={width ? { maxWidth: width } : undefined} onMouseDown={(e) => e.stopPropagation()}>
        <div className="modal-head">
          <div><h3>{title}</h3>{subtitle && <p>{subtitle}</p>}</div>
          <button type="button" className="modal-close" onClick={onClose} aria-label="Cerrar"><X size={18} /></button>
        </div>
        <div className="modal-body">{children}</div>
      </div>
    </div>
  );
}

/** Diálogo de confirmación para operaciones destructivas (eliminar). */
export function ConfirmDialog({ title, message, confirmLabel = 'Eliminar', onCancel, onConfirm, loading }) {
  return (
    <div className="modal-overlay" onMouseDown={onCancel}>
      <div className="modal-card confirm-card" onMouseDown={(e) => e.stopPropagation()}>
        <div className="confirm-icon"><AlertTriangle size={24} /></div>
        <h3>{title}</h3>
        <p>{message}</p>
        <div className="confirm-actions">
          <button type="button" className="ghost-btn" onClick={onCancel} disabled={loading}>Cancelar</button>
          <button type="button" className="danger-btn" onClick={onConfirm} disabled={loading}>
            {loading ? 'Eliminando...' : confirmLabel}
          </button>
        </div>
      </div>
    </div>
  );
}

/** Controles de paginación server-side (Spring Data Page). */
export function Pagination({ page, totalPages, totalElements, onChange }) {
  if (!totalPages || totalPages <= 1) return null;
  return (
    <div className="pagination">
      <button type="button" disabled={page <= 0} onClick={() => onChange(page - 1)}><ChevronLeft size={16} /></button>
      <span>Página {page + 1} de {totalPages} · {totalElements} registro{totalElements === 1 ? '' : 's'}</span>
      <button type="button" disabled={page >= totalPages - 1} onClick={() => onChange(page + 1)}><ChevronRight size={16} /></button>
    </div>
  );
}

/** Aviso de éxito/error reutilizado en todos los módulos. notice = { type: 'success'|'error', text } */
export function Banner({ notice }) {
  if (!notice) return null;
  const isError = notice.type === 'error';
  return (
    <div className={isError ? 'error-box' : 'notice'}>
      {isError ? <AlertTriangle size={18} /> : <CheckCircle2 size={18} />}
      {notice.text}
    </div>
  );
}

export function EmptyState({ text }) {
  return <p className="empty">{text}</p>;
}
