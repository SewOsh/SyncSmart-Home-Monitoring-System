import { Home, Wifi, ChevronDown, User } from 'lucide-react';
import styles from './Header.module.css';

export default function Header() {
  return (
    <header className={styles.header}>
      <div className={styles.left}>
        <div className={styles.logoMark}>
          <Home size={18} color="var(--green)" />
          <Wifi size={12} color="var(--green)" />
        </div>
        <span className={styles.wordmark}>
          <span className={styles.smart}>Smart</span>
          <span className={styles.home}>Home</span>
        </span>
      </div>
      <div className={styles.right}>
        <button type="button" className={styles.selector}>
          My home <ChevronDown size={14} />
        </button>
        <div className={styles.avatar}>
          <User size={18} color="var(--accent)" />
        </div>
      </div>
    </header>
  );
}
