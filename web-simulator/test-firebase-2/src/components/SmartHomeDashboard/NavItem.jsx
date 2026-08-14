import {
  Sofa,
  UtensilsCrossed,
  DoorClosed,
  BedDouble,
  BookOpen,
  Bath,
} from 'lucide-react';
import styles from './NavItem.module.css';

const ICON_MAP = {
  sofa: Sofa,
  utensils: UtensilsCrossed,
  door: DoorClosed,
  bed: BedDouble,
  book: BookOpen,
  bath: Bath,
};

export default function NavItem({ icon, label, active, onClick }) {
  const Icon = ICON_MAP[icon];
  return (
    <button
      type="button"
      className={`${styles.navItem} ${active ? styles.active : ""}`}
      onClick={onClick}
    >
      <span className={styles.iconWrap}>
        {Icon && <Icon size={16} color="var(--cyan-icon)" />}
      </span>
      <span className={styles.label}>{label}</span>
    </button>
  );
}
