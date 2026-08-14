import { AlertTriangle, X } from 'lucide-react';
import styles from './Toast.module.css';

export default function Toast({ message, type = "warning", visible, onDismiss }) {
  return (
    <div className={`${styles.toast} ${visible ? styles.visible : styles.hidden}`}>
      <div className={styles.iconWrap}>
        <AlertTriangle size={20} color="#fca5a5" />
      </div>
      <span className={styles.message}>{message}</span>
      <button className={styles.closeBtn} onClick={onDismiss} aria-label="Dismiss">
        <X size={16} color="var(--text-muted)" />
      </button>
    </div>
  );
}
