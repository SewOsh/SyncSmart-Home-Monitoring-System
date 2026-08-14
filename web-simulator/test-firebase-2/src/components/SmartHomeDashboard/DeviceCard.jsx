import { Lightbulb, Plug, Camera, Snowflake, VideoOff } from 'lucide-react';
import Toggle from './Toggle';
import styles from './DeviceCard.module.css';

const ICON_MAP = {
  bulb: Lightbulb,
  plug: Plug,
  camera: Camera,
  snowflake: Snowflake,
};

const CAMERA_VIDEO_SRC = '/footage_sample.mp4';

export default function DeviceCard({ device, isOn, onToggle, remainingSeconds, onCancelTimer }) {
  const Icon = ICON_MAP[device.icon];
  const isCamera = device.type === 'camera';
  const hasTimer = device.hasTimer && remainingSeconds !== undefined;
  const timerUrgent = hasTimer && remainingSeconds <= 300;

  const formatTime = (sec) => {
    const m = String(Math.floor(sec / 60)).padStart(2, '0');
    const s = String(sec % 60).padStart(2, '0');
    return `${m}:${s}`;
  };

  return (
    <div className={`${styles.card} ${isCamera ? styles.cameraCard : ""}`}>
      <div className={styles.header}>
        <div className={styles.iconChip}>
          {Icon && <Icon size={18} color="var(--cyan-icon)" />}
        </div>
        <div className={styles.info}>
          <h3 className={styles.title}>{device.title}</h3>
          {isCamera ? (
            <span className={isOn ? styles.statusOnline : styles.statusOffline}>
              Status: {isOn ? "Online" : "Offline — Connection Lost"}
            </span>
          ) : (
            <span className={styles.status}>
              Status: {isOn ? "ON" : "OFF"}
              {device.offNote && !isOn ? ` (${device.offNote})` : ""}
            </span>
          )}
        </div>
        <Toggle on={isOn} onChange={onToggle} />
      </div>
      {hasTimer && isOn && remainingSeconds > 0 && (
        <div className={styles.countdownRow}>
          <span className={`${styles.countdown} ${timerUrgent ? styles.countdownUrgent : ""}`}>
            {formatTime(remainingSeconds)} remaining
          </span>
          <button type="button" className={styles.cancelBtn} onClick={onCancelTimer}>
            Cancel
          </button>
        </div>
      )}
      {isCamera && isOn && (
        <div className={styles.cameraFeed}>
          <video
            src={CAMERA_VIDEO_SRC}
            autoPlay
            loop
            muted
            playsInline
            className={styles.cameraVideo}
          />
        </div>
      )}
      {isCamera && !isOn && (
        <div className={styles.cameraError}>
          <VideoOff size={48} color="var(--text-muted)" />
          <span className={styles.errorLabel}>No Signal</span>
        </div>
      )}
    </div>
  );
}
