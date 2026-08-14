import LogEntry from './LogEntry';
import styles from './LiveSyncPanel.module.css';

export default function LiveSyncPanel({ entries = [] }) {
  return (
    <aside className={styles.panel}>
      <div className={styles.statusLine}>
        <span className={styles.statusDot} />
        <span className={styles.statusText}>Connected — live sync</span>
      </div>

      <div className={styles.logHeader}>LIVE SYNC LOG</div>

      <div className={styles.logList}>
        {entries.map((entry) => (
          <LogEntry
            key={entry.id}
            dotColor={entry.dot}
            timestamp={entry.time}
            text={entry.text}
          />
        ))}
      </div>

      <button type="button" className={styles.viewAll}>
        View full log ›
      </button>
    </aside>
  );
}
