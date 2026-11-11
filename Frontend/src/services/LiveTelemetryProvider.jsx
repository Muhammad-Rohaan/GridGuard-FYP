import { createContext, useContext, useEffect, useState } from 'react';
import {
  getSynchronizedLiveSnapshot,
  millisecondsUntilNextClockTick,
} from './liveData.js';

const LiveTelemetryContext = createContext(null);
const LIVE_POLL_INTERVAL_MS = 5000;
const HISTORY_LIMIT = 121;

// Identifies the current local hour so readings are fetched only when it changes.
function currentHourKey(now) {
  return `${now.getFullYear()}-${now.getMonth()}-${now.getDate()}-${now.getHours()}`;
}

// Loads and validates the current-hour readings used by both dashboard views.
async function fetchLiveReadings(signal) {
  const apiBase = import.meta.env.VITE_API_BASE_URL || '/api';
  const response = await fetch(`${apiBase}/live`, { credentials: 'include', signal });
  const contentType = response.headers.get('content-type') || '';
  const payload = contentType.includes('application/json')
    ? await response.json()
    : await response.text();

  if (!response.ok) {
    const message = typeof payload === 'string' ? payload : payload.message || payload.error;
    throw new Error(message || `Live data request failed (${response.status}).`);
  }
  if (!Array.isArray(payload)) {
    throw new Error('Live data response must be an array of load readings.');
  }

  return payload.sort((first, second) => first.recordedAt.localeCompare(second.recordedAt));
}

// Shares clock-synchronized readings, asset details, and chart history across routes.
export function LiveTelemetryProvider({ children }) {
  const [liveData, setLiveData] = useState(null);
  const [liveError, setLiveError] = useState('');
  const [history, setHistory] = useState([]);
  const [assetOverview, setAssetOverview] = useState(null);

  useEffect(() => {
    let active = true;
    let timeout;
    let controller;
    let readings = [];
    let loadedHour;

    // Schedules the next update on the five-second clock boundary.
    const scheduleNextTick = () => {
      timeout = window.setTimeout(
        update,
        millisecondsUntilNextClockTick(Date.now(), LIVE_POLL_INTERVAL_MS),
      );
    };

    // Refreshes hourly data when needed, then publishes one synchronized sample.
    const update = async () => {
      if (!active) return;

      const requestHour = currentHourKey(new Date());
      if (requestHour !== loadedHour) {
        controller?.abort();
        controller = new AbortController();
        try {
          readings = await fetchLiveReadings(controller.signal);
          if (!active) return;
          if (requestHour !== currentHourKey(new Date())) {
            timeout = window.setTimeout(update, 0);
            return;
          }
          loadedHour = requestHour;
          setLiveError('');
        } catch (requestError) {
          if (!active || requestError.name === 'AbortError') return;
          setLiveError(requestError.message);
          scheduleNextTick();
          return;
        }
      }

      const now = new Date();
      if (currentHourKey(now) !== loadedHour) {
        timeout = window.setTimeout(update, 0);
        return;
      }

      if (readings.length === 0) {
        setLiveError('No load readings are available for the current hour.');
      } else {
        const firstReading = readings[0];
        if (firstReading?.area?.substation) {
          setAssetOverview({
            substation: firstReading.area.substation,
            area: firstReading.area,
          });
        }
        const snapshot = getSynchronizedLiveSnapshot(readings, now);
        const point = {
          timestamp: now.getTime(),
          simTime: snapshot.simTime,
          loadKw: snapshot.area.currentLoadKw,
        };
        setLiveData(snapshot);
        setHistory((previous) => [...previous, point].slice(-HISTORY_LIMIT));
        setLiveError('');
      }

      scheduleNextTick();
    };

    update();
    return () => {
      active = false;
      window.clearTimeout(timeout);
      controller?.abort();
    };
  }, []);

  return (
    <LiveTelemetryContext.Provider value={{ liveData, liveError, history, assetOverview }}>
      {children}
    </LiveTelemetryContext.Provider>
  );
}

// Exposes the shared telemetry state to dashboard and substation components.
export function useLiveTelemetry() {
  const telemetry = useContext(LiveTelemetryContext);
  if (!telemetry) {
    throw new Error('useLiveTelemetry must be used within a LiveTelemetryProvider.');
  }
  return telemetry;
}
