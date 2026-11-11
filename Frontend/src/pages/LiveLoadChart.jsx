import { useState } from 'react';

const CHART_WIDTH = 760;
const CHART_HEIGHT = 270;
const PLOT_LEFT = 58;
const PLOT_RIGHT = 744;
const PLOT_TOP = 18;
const PLOT_BOTTOM = 222;
const TIME_RANGES = [
  { label: '1 min', milliseconds: 60_000 },
  { label: '5 min', milliseconds: 300_000 },
  { label: '10 min', milliseconds: 600_000 },
];

// Shows a load value with the unit used throughout the telemetry UI.
function formatLoad(value) {
  return `${value.toFixed(2)} kW`;
}

// Formats a chart sample's timestamp for labels and hover details.
function formatTime(timestamp) {
  return new Date(timestamp).toLocaleTimeString([], {
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
  });
}

// Renders the shared live-load history with range controls and point inspection.
export default function LiveLoadChart({ history }) {
  const [range, setRange] = useState(TIME_RANGES[1]);
  const [selectedIndex, setSelectedIndex] = useState(null);
  const latestTime = history.at(-1)?.timestamp ?? Date.now();
  const visibleHistory = history.filter(
    (point) => point.timestamp >= latestTime - range.milliseconds,
  );
  const selectedPoint = visibleHistory[
    selectedIndex === null ? visibleHistory.length - 1 : Math.min(selectedIndex, visibleHistory.length - 1)
  ];

  const loads = visibleHistory.map((point) => point.loadKw);
  const dataMin = loads.length ? Math.min(...loads) : 0;
  const dataMax = loads.length ? Math.max(...loads) : 1;
  const padding = Math.max((dataMax - dataMin) * 0.15, Math.abs(dataMax) * 0.01, 0.1);
  const minLoad = Math.max(0, dataMin - padding);
  const maxLoad = dataMax + padding;
  // Maps a sample index to its horizontal chart position.
  const xFor = (index) => visibleHistory.length < 2
    ? (PLOT_LEFT + PLOT_RIGHT) / 2
    : PLOT_LEFT + (index / (visibleHistory.length - 1)) * (PLOT_RIGHT - PLOT_LEFT);
  // Maps a load value to its vertical chart position.
  const yFor = (load) => PLOT_BOTTOM - ((load - minLoad) / (maxLoad - minLoad)) * (PLOT_BOTTOM - PLOT_TOP);
  const linePath = visibleHistory.map((point, index) => (
    `${index === 0 ? 'M' : 'L'} ${xFor(index)} ${yFor(point.loadKw)}`
  )).join(' ');
  const areaPath = visibleHistory.length
    ? `${linePath} L ${xFor(visibleHistory.length - 1)} ${PLOT_BOTTOM} L ${xFor(0)} ${PLOT_BOTTOM} Z`
    : '';
  const selectedX = selectedPoint ? xFor(visibleHistory.indexOf(selectedPoint)) : null;
  const selectedY = selectedPoint ? yFor(selectedPoint.loadKw) : null;
  const tooltipX = selectedX === null
    ? 0
    : Math.max(PLOT_LEFT, Math.min(PLOT_RIGHT - 112, selectedX - 56));
  const tooltipY = selectedY === null ? 0 : Math.max(PLOT_TOP, selectedY - 49);

  // Selects the plotted sample closest to the pointer for its tooltip.
  const selectFromPointer = (event) => {
    if (visibleHistory.length < 2) return;
    const bounds = event.currentTarget.getBoundingClientRect();
    const pointerX = ((event.clientX - bounds.left) / bounds.width) * CHART_WIDTH;
    const pointerY = ((event.clientY - bounds.top) / bounds.height) * CHART_HEIGHT;
    let nearestIndex = 0;
    let nearestDistance = Infinity;

    visibleHistory.forEach((point, index) => {
      const distance = Math.hypot(pointerX - xFor(index), pointerY - yFor(point.loadKw));
      if (distance < nearestDistance) {
        nearestDistance = distance;
        nearestIndex = index;
      }
    });

    setSelectedIndex(nearestIndex);
  };

  // Lets keyboard users inspect adjacent chart samples.
  const moveSelection = (event) => {
    if (event.key !== 'ArrowLeft' && event.key !== 'ArrowRight') return;
    event.preventDefault();
    const currentIndex = selectedIndex ?? visibleHistory.length - 1;
    const direction = event.key === 'ArrowLeft' ? -1 : 1;
    setSelectedIndex(Math.max(0, Math.min(visibleHistory.length - 1, currentIndex + direction)));
  };

  return (
    <section className="live-chart-card" aria-labelledby="live-chart-title">
      <div className="live-chart-heading">
        <div>
          <p className="eyebrow">LIVE TELEMETRY</p>
          <h2 id="live-chart-title">Substation load</h2>
        </div>
        <div className="chart-range-controls" role="group" aria-label="Chart time range">
          {TIME_RANGES.map((option) => (
            <button
              key={option.label}
              type="button"
              className={range.label === option.label ? 'is-selected' : ''}
              aria-pressed={range.label === option.label}
              onClick={() => {
                setRange(option);
                setSelectedIndex(null);
              }}
            >
              {option.label}
            </button>
          ))}
        </div>
      </div>

      {selectedPoint && (
        <div className="chart-reading" aria-live="polite">
          <strong>{formatLoad(selectedPoint.loadKw)}</strong>
          <span>{formatTime(selectedPoint.timestamp)}</span>
        </div>
      )}

      <div className="live-chart-plot">
        <svg
          viewBox={`0 0 ${CHART_WIDTH} ${CHART_HEIGHT}`}
          role="group"
          aria-label="Interactive substation load chart. Use left and right arrow keys to inspect readings."
          tabIndex={0}
          onPointerMove={selectFromPointer}
          onPointerLeave={() => setSelectedIndex(null)}
          onKeyDown={moveSelection}
        >
          {[0, 1, 2, 3].map((step) => {
            const y = PLOT_TOP + (step / 3) * (PLOT_BOTTOM - PLOT_TOP);
            const load = maxLoad - (step / 3) * (maxLoad - minLoad);
            return (
              <g key={step}>
                <line x1={PLOT_LEFT} x2={PLOT_RIGHT} y1={y} y2={y} className="chart-grid-line" />
                <text x={PLOT_LEFT - 9} y={y + 4} textAnchor="end" className="chart-axis-label">
                  {load.toFixed(0)}
                </text>
              </g>
            );
          })}
          {areaPath && <path d={areaPath} className="chart-area" />}
          {linePath && <path d={linePath} className="chart-line" />}
          {visibleHistory.map((point, index) => (
            <circle
              key={`${point.timestamp}-${index}`}
              cx={xFor(index)}
              cy={yFor(point.loadKw)}
              r={selectedPoint === point ? 4 : 1.8}
              className={selectedPoint === point ? 'chart-point is-active' : 'chart-point'}
            />
          ))}
          {selectedPoint && (
            <>
              <line x1={selectedX} x2={selectedX} y1={PLOT_TOP} y2={PLOT_BOTTOM} className="chart-crosshair" />
              <circle cx={selectedX} cy={selectedY} r="5" className="chart-selected-point" />
              <g className="chart-tooltip" pointerEvents="none">
                <rect x={tooltipX} y={tooltipY} width="112" height="39" rx="5" />
                <text x={tooltipX + 8} y={tooltipY + 15} className="chart-tooltip-load">
                  {formatLoad(selectedPoint.loadKw)}
                </text>
                <text x={tooltipX + 8} y={tooltipY + 30} className="chart-tooltip-time">
                  {formatTime(selectedPoint.timestamp)}
                </text>
              </g>
            </>
          )}
          <text x={PLOT_LEFT} y="253" className="chart-axis-label">
            {visibleHistory.length ? formatTime(visibleHistory[0].timestamp) : 'Waiting for readings'}
          </text>
          <text x={PLOT_RIGHT} y="253" textAnchor="end" className="chart-axis-label">
            {visibleHistory.length ? formatTime(visibleHistory.at(-1).timestamp) : ''}
          </text>
        </svg>
        {!visibleHistory.length && (
          <p className="chart-empty-state">Waiting for live readings from the dashboard feed…</p>
        )}
      </div>
      <p className="chart-hint">Move across the chart or focus it and use ← / → to inspect values.</p>
    </section>
  );
}
