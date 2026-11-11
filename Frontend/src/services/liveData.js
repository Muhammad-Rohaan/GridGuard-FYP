// Extracts a dataset timestamp's hour and minute for comparison with local time.
function minutesSinceMidnight(recordedAt) {
  const match = typeof recordedAt === 'string'
    ? recordedAt.match(/T(\d{2}):(\d{2})/)
    : null;

  if (!match) {
    throw new Error('A load reading is missing a valid recordedAt timestamp.');
  }

  return Number(match[1]) * 60 + Number(match[2]);
}

// Formats a Date as local ISO-like time without changing its wall-clock fields.
function formatLocalDateTime(date) {
  const pad = (value) => String(value).padStart(2, '0');
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}`
    + `T${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`;
}

// Interpolates the two readings surrounding now and adds bounded visual jitter.
export function getSynchronizedLiveSnapshot(readings, now = new Date(), random = Math.random) {
  if (!Array.isArray(readings) || readings.length === 0) {
    throw new Error('At least one load reading is required.');
  }

  const currentMinute = now.getHours() * 60 + now.getMinutes() + now.getSeconds() / 60;
  let lower = null;
  let upper = null;

  readings.forEach((reading) => {
    if (!Number.isFinite(reading?.loadKw) || !reading?.area?.substation) {
      throw new Error('A load reading is missing its load or network details.');
    }

    const readingMinute = minutesSinceMidnight(reading.recordedAt);
    if (readingMinute <= currentMinute && (!lower || readingMinute > lower.minute)) {
      lower = { reading, minute: readingMinute };
    }
    if (readingMinute > currentMinute && (!upper || readingMinute < upper.minute)) {
      upper = { reading, minute: readingMinute };
    }
  });

  const start = lower || upper;
  const end = lower && upper ? upper : start;
  const progress = start === end
    ? 0
    : (currentMinute - start.minute) / (end.minute - start.minute);
  const interpolatedLoad = start.reading.loadKw
    + (end.reading.loadKw - start.reading.loadKw) * progress;
  const fluctuation = Math.max(Math.abs(interpolatedLoad) * 0.005, 0.01);
  const loadKw = Math.max(0, interpolatedLoad + (random() * 2 - 1) * fluctuation);
  const substation = { ...start.reading.area.substation, currentLoadKw: loadKw };
  const area = { ...start.reading.area, substation, currentLoadKw: loadKw };

  return {
    simTime: formatLocalDateTime(now),
    area,
    substation: area.substation,
  };
}

// Aligns polling with the next wall-clock five-second boundary.
export function millisecondsUntilNextClockTick(now = Date.now(), intervalMs = 5000) {
  return intervalMs - (now % intervalMs);
}
