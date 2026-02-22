import { useToast, ToastMessage } from '../../context/ToastContext';
import './Toast.css';

function ToastItem({ toast }: { toast: ToastMessage }) {
  const { removeToast } = useToast();

  const handleClose = () => {
    removeToast(toast.id);
  };

  // Build class names based on type and alertStatus
  const baseClass = 'toast';
  const typeClass = `${baseClass}-${toast.type}`;
  const statusClass = toast.alertStatus ? `${baseClass}-status-${toast.alertStatus.toLowerCase()}` : '';
  const typeClassFinal = `toast toast-${toast.type} ${statusClass}`;

  return (
    <div className={typeClassFinal}>
      <div className="toast-content">
        <strong className="toast-title">{toast.title}</strong>
        {toast.description && (
          <p className="toast-description">{toast.description}</p>
        )}
        {toast.action && (
          <button className="toast-action" onClick={toast.action.onClick}>
            {toast.action.label}
          </button>
        )}
      </div>
      {toast.closable && (
        <button className="toast-close" onClick={handleClose} aria-label="Close">
          ×
        </button>
      )}
    </div>
  );
}

export function ToastContainer() {
  const { toasts } = useToast();

  if (toasts.length === 0) {
    return null;
  }

  return (
    <div className="toast-container">
      {toasts.map((toast) => (
        <ToastItem key={toast.id} toast={toast} />
      ))}
    </div>
  );
}

export default ToastContainer;
