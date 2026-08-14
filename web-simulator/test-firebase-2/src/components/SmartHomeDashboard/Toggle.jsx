import styles from './Toggle.module.css';

export default function Toggle({ on, onChange, size = "default" }) {
  const trackClass = `${styles.track} ${on ? styles.on : ""} ${size === "small" ? styles.small : ""}`;
  const knobClass = `${styles.knob} ${size === "small" ? styles.knobSmall : ""}`;

  return (
    <button
      type="button"
      role="switch"
      aria-checked={on}
      className={trackClass}
      onClick={onChange}
    >
      <span
        className={knobClass}
        style={{ transform: on ? 'translateX(21px)' : 'translateX(3px)' }}
      />
    </button>
  );
}
