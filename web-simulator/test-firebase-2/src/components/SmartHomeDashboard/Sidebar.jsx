import { Building2, Info } from 'lucide-react';
import NavItem from './NavItem';
import styles from './Sidebar.module.css';

export default function Sidebar({
  floors,
  activeFloor,
  onFloorChange,
  activeRoom,
  onRoomChange,
}) {
  const currentFloor = floors.find((f) => f.id === activeFloor);
  const rooms = currentFloor ? currentFloor.rooms : [];

  return (
    <aside className={styles.sidebar}>
      <div className={styles.section}>
        <div className={styles.sectionLabel}>
          <Building2 size={14} color="var(--text-muted)" />
          <span>Floors</span>
        </div>
        <div className={styles.floorList}>
          {floors.map((floor) => (
            <button
              key={floor.id}
              type="button"
              className={`${styles.floorBtn} ${activeFloor === floor.id ? styles.floorActive : ""}`}
              onClick={() => onFloorChange(floor.id)}
            >
              {floor.label}
            </button>
          ))}
        </div>
      </div>

      <div className={styles.section}>
        <div className={styles.sectionLabel}>Rooms</div>
        <div className={styles.roomList}>
          {rooms.map((room) => (
            <NavItem
              key={room.id}
              icon={room.navIcon}
              label={room.label}
              active={activeRoom === room.id}
              onClick={() => onRoomChange(room.id)}
            />
          ))}
        </div>
      </div>

      <div className={styles.hintBox}>
        <Info size={14} color="var(--accent)" />
        <span>Tap any device to open Device Control (screen 04).</span>
      </div>
    </aside>
  );
}
