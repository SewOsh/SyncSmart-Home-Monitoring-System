import { useState, useEffect, useRef } from 'react';
import { doc, updateDoc } from 'firebase/firestore';
import { db } from '../../firebase';
import { FLOORS } from '../../data/roomsData';
import { useDeviceToggle } from '../../hooks/useDeviceToggle';
import Header from './Header';
import Sidebar from './Sidebar';
import DeviceCard from './DeviceCard';
import SwitchPanelCard from './SwitchPanelCard';
import LiveSyncPanel from './LiveSyncPanel';
import Toast from './Toast';
import styles from './SmartHomeDashboard.module.css';

const IRON_SOCKET_ID = 'G02_iron_socket';
const TIMER_DURATION_MS = 60 * 60 * 1000;

const deviceMeta = (() => {
  const map = {};
  FLOORS.forEach((floor) =>
    floor.rooms.forEach((room) =>
      room.devices.forEach((device) => {
        if (device.type === "switchPanel") {
          device.switches.forEach((sw) => {
            map[sw.id] = { title: `${device.title} - ${sw.label}`, type: "toggle" };
          });
        } else {
          map[device.id] = { title: device.title, type: device.type };
        }
      })
    )
  );
  return map;
})();

export default function SmartHomeDashboard() {
  const [activeFloor, setActiveFloor] = useState("ground");
  const [activeRoom, setActiveRoom] = useState("G01");
  const { states, toggle } = useDeviceToggle();
  const [logEntries, setLogEntries] = useState([]);
  const [toast, setToast] = useState(null);
  const [countdowns, setCountdowns] = useState({});
  const statesRef = useRef(states);
  const prevStatesRef = useRef({});
  const localChangesRef = useRef({});
  const toastTimer = useRef(null);

  const addLog = (text, dot) => {
    const time = new Date().toLocaleTimeString();
    setLogEntries((prev) => [{ id: Date.now(), dot, time, text }, ...prev]);
  };

  const showToast = (message, type = "warning") => {
    setToast({ message, type });
    if (toastTimer.current) clearTimeout(toastTimer.current);
    toastTimer.current = setTimeout(() => setToast(null), 4000);
  };

  const dismissToast = () => {
    setToast(null);
    if (toastTimer.current) clearTimeout(toastTimer.current);
  };

  useEffect(() => {
    statesRef.current = states;
  }, [states]);

  useEffect(() => {
    const interval = setInterval(() => {
      const s = statesRef.current;
      const schedule = s[`${IRON_SOCKET_ID}_schedule`];

      if (schedule && typeof schedule === 'number') {
        const remaining = Math.max(0, Math.floor((schedule - Date.now()) / 1000));
        setCountdowns((prev) => {
          if (prev[IRON_SOCKET_ID] !== remaining) {
            return { ...prev, [IRON_SOCKET_ID]: remaining };
          }
          return prev;
        });

        if (remaining <= 0 && s[IRON_SOCKET_ID] === true) {
          const ref = doc(db, "Devices", "devices");
          updateDoc(ref, {
            [IRON_SOCKET_ID]: false,
            [`${IRON_SOCKET_ID}_schedule`]: null,
          });
          showToast("Iron Socket — Safety cutoff activated", "warning");
        }
      } else {
        setCountdowns((prev) => {
          if (prev[IRON_SOCKET_ID] !== undefined) {
            const next = { ...prev };
            delete next[IRON_SOCKET_ID];
            return next;
          }
          return prev;
        });
      }
    }, 1000);

    return () => clearInterval(interval);
  }, []);

  useEffect(() => {
    const prev = prevStatesRef.current;
    if (Object.keys(prev).length === 0) {
      prevStatesRef.current = states;
      return;
    }

    const local = localChangesRef.current;

    Object.entries(states).forEach(([key, value]) => {
      if (typeof value !== "boolean") return;
      if (prev[key] === value) return;

      if (local[key] === value) {
        delete local[key];
        return;
      }

      const meta = deviceMeta[key];
      if (!meta) return;

      if (meta.type === "camera") {
        addLog(
          `${meta.title} → ${value ? "Online" : "Offline"}`,
          value ? "var(--log-dot-on)" : "var(--log-dot-error)"
        );
      } else {
        addLog(
          `${meta.title} → ${value ? "ON" : "OFF"}`,
          value ? "var(--log-dot-on)" : "var(--log-dot-info)"
        );
      }
    });

    prevStatesRef.current = states;
  }, [states]);

  const scrollToRoom = (roomId) => {
    setTimeout(() => {
      document.getElementById(roomId)?.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }, 0);
  };

  const handleRoomChange = (roomId) => {
    setActiveRoom(roomId);
    scrollToRoom(roomId);
  };

  const handleFloorChange = (floorId) => {
    setActiveFloor(floorId);
    const floor = FLOORS.find((f) => f.id === floorId);
    if (floor && floor.rooms.length > 0) {
      setActiveRoom(floor.rooms[0].id);
      scrollToRoom(floor.rooms[0].id);
    }
  };

  const handleToggle = (device) => {
    const next = !states[device.id];
    localChangesRef.current[device.id] = next;

    if (device.hasTimer) {
      const ref = doc(db, "Devices", "devices");
      if (next) {
        updateDoc(ref, {
          [device.id]: true,
          [`${device.id}_schedule`]: Date.now() + TIMER_DURATION_MS,
        });
        setCountdowns((prev) => ({ ...prev, [device.id]: TIMER_DURATION_MS / 1000 }));
      } else {
        updateDoc(ref, {
          [device.id]: false,
          [`${device.id}_schedule`]: null,
        });
        setCountdowns((prev) => {
          const next = { ...prev };
          delete next[device.id];
          return next;
        });
        showToast("Iron Socket — Safety cutoff activated", "warning");
      }

      addLog(
        `${device.title} → ${next ? "ON" : "OFF"}`,
        next ? "var(--log-dot-on)" : "var(--log-dot-info)"
      );
    } else {
      toggle(device.id);

      if (device.type === "camera") {
        addLog(
          `${device.title} → ${next ? "Online" : "Offline"}`,
          next ? "var(--log-dot-on)" : "var(--log-dot-error)"
        );
      } else {
        addLog(
          `${device.title} → ${next ? "ON" : "OFF"}`,
          next ? "var(--log-dot-on)" : "var(--log-dot-info)"
        );
      }
    }
  };

  const handlePanelToggle = (deviceId) => {
    localChangesRef.current[deviceId] = !states[deviceId];
    toggle(deviceId);
  };

  const handleCancelTimer = (deviceId) => {
    const ref = doc(db, "Devices", "devices");
    updateDoc(ref, { [`${deviceId}_schedule`]: null });
    setCountdowns((prev) => {
      const next = { ...prev };
      delete next[deviceId];
      return next;
    });
    addLog("Iron Socket — Timer cancelled", "var(--log-dot-info)");
  };

  const handleLog = (text, dot) => {
    addLog(text, dot);
  };

  const groundFloor = FLOORS.find((f) => f.id === "ground");
  const firstFloor = FLOORS.find((f) => f.id === "first");

  return (
    <div className={styles.dashboard}>
      <Header />
      <div className={styles.body}>
        <Sidebar
          floors={FLOORS}
          activeFloor={activeFloor}
          onFloorChange={handleFloorChange}
          activeRoom={activeRoom}
          onRoomChange={handleRoomChange}
        />
        <main className={styles.content}>
          {groundFloor && (
            <div className={styles.floorGroup}>
              {groundFloor.rooms.map((room) => (
                <section key={room.id} id={room.id} className={styles.roomSection}>
                  <h2 className={styles.roomTitle}>{room.label}</h2>
                  <div className={styles.deviceRow}>
                    {room.devices.map((device) => {
                      if (device.type === "switchPanel") {
                        return (
                          <SwitchPanelCard
                            key={device.id}
                            device={device}
                            states={states}
                            onToggle={handlePanelToggle}
                            onLog={handleLog}
                          />
                        );
                      }
                      return (
                        <DeviceCard
                          key={device.id}
                          device={device}
                          isOn={states[device.id]}
                          onToggle={() => handleToggle(device)}
                          remainingSeconds={device.hasTimer ? countdowns[device.id] : undefined}
                          onCancelTimer={device.hasTimer ? () => handleCancelTimer(device.id) : undefined}
                        />
                      );
                    })}
                  </div>
                </section>
              ))}
            </div>
          )}

          <div className={styles.floorDivider} />

          {firstFloor && (
            <div className={styles.floorGroup}>
              {firstFloor.rooms.map((room) => (
                <section key={room.id} id={room.id} className={styles.roomSection}>
                  <h2 className={styles.roomTitle}>{room.label}</h2>
                  <div className={styles.deviceRow}>
                    {room.devices.map((device) => {
                      if (device.type === "switchPanel") {
                        return (
                          <SwitchPanelCard
                            key={device.id}
                            device={device}
                            states={states}
                            onToggle={handlePanelToggle}
                            onLog={handleLog}
                          />
                        );
                      }
                      return (
                        <DeviceCard
                          key={device.id}
                          device={device}
                          isOn={states[device.id]}
                          onToggle={() => handleToggle(device)}
                          remainingSeconds={device.hasTimer ? countdowns[device.id] : undefined}
                          onCancelTimer={device.hasTimer ? () => handleCancelTimer(device.id) : undefined}
                        />
                      );
                    })}
                  </div>
                </section>
              ))}
            </div>
          )}
        </main>
        <LiveSyncPanel entries={logEntries} />
      </div>

      <Toast
        message={toast?.message}
        type={toast?.type}
        visible={toast !== null}
        onDismiss={dismissToast}
      />
    </div>
  );
}
