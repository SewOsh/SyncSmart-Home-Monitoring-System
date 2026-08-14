import { useState, useEffect, useCallback } from "react";
import { doc, onSnapshot, updateDoc } from "@firebase/firestore";
import { db } from "../firebase";

export function useDeviceToggle() {
  const [states, setStates] = useState({});
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const unsub = onSnapshot(doc(db, "Devices", "devices"), (snapshot) => {
      if (snapshot.exists()) {
        setStates(snapshot.data());
      }
      setLoading(false);
    });

    return () => unsub();
  }, []);

  const toggle = useCallback(async (deviceId) => {
    const ref = doc(db, "Devices", "devices");
    await updateDoc(ref, { [deviceId]: !states[deviceId] });
  }, [states]);

  return { states, toggle, loading };
}
