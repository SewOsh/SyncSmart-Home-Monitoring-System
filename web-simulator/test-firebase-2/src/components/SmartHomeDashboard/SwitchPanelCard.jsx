import Toggle from './Toggle';
import styles from './SwitchPanelCard.module.css';

export default function SwitchPanelCard({ device, states, onToggle, onLog }) {
  return (
    <div className={styles.card}>
      <h3 className={styles.title}>{device.title}</h3>
      <div className={styles.switchList}>
        {device.switches.map((sw) => {
          const key = sw.id;
          return (
            <div className={styles.switchRow} key={key}>
              <span className={styles.switchLabel}>{sw.label}</span>
              <Toggle
                on={states[key]}
                onChange={() => {
                  const next = !states[key];
                  onToggle(key);
                  onLog(
                    `${device.title} - ${sw.label} → ${next ? "ON" : "OFF"}`,
                    next ? "var(--log-dot-on)" : "var(--log-dot-info)"
                  );
                }}
                size="small"
              />
            </div>
          );
        })}
      </div>
    </div>
  );
}
