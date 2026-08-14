import styles from './LogEntry.module.css';

export default function LogEntry({ dotColor, timestamp, text }) {
  return (
    <div className={styles.entry}>
      <span className={styles.dot} style={{ background: dotColor }} />
      <span className={styles.timestamp}>{timestamp}</span>
      <span className={styles.text}>{text}</span>
    </div>
  );
}
