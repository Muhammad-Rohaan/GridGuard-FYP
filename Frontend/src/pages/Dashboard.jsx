import { useEffect, useRef, useState } from 'react';
import { Link, useNavigate, useOutletContext } from 'react-router-dom';
import { apiLogout } from '../services/auth.js';
import './dashboard.css';

// Data describing the electicity network.
const NODES = [
  { id: 'substation', label: 'North Substation', type: 'Substation', x: 0.22, y: 0.52, voltage: '132 kV', status: 'Online', color: '#27d8ed' },
  { id: 'residential', label: 'Residential Area', type: 'Residential area', x: 0.77, y: 0.52, voltage: '11 kV', status: 'Online', color: '#54d69a' },
];

// Connection between substation -> area
const CONNECTIONS = [['residential', 'substation']];
const FLOW_CYCLE_MS = 10000;
const FLOW_DOT_COUNT = 3;
const LIVE_POLL_INTERVAL_MS = 5000;
const MIN_MAP_ZOOM = 1;
const MAX_MAP_ZOOM = 2.4;
const MAP_ZOOM_STEP = 0.2;

async function fetchLiveData(signal) {
  const apiBase = import.meta.env.VITE_API_BASE_URL || '/api';
  const response = await fetch(`${apiBase}/live`, {
    credentials: 'include',
    signal,
  });
  const contentType = response.headers.get('content-type') || '';
  const payload = contentType.includes('application/json')
    ? await response.json()
    : await response.text();

  if (!response.ok) {
    const message = typeof payload === 'string' ? payload : payload.message || payload.error;
    throw new Error(message || `Live data request failed (${response.status}).`);
  }

  const data = payload?.data;
  const substation = data?.substations?.[0];
  const area = substation?.areas?.[0];
  if (payload?.success !== true || !substation || !area) {
    throw new Error(payload?.message || 'Live data response is missing substation or area readings.');
  }

  return { simTime: data.simTime, substation, area };
}

function formatLoad(load) {
  return Number.isFinite(load) ? `${load.toFixed(2)} kW` : '—';
}

function splitSimulationDateTime(simTime) {
  const match = typeof simTime === 'string'
    ? simTime.match(/^(\d{4}-\d{2}-\d{2})T(\d{2}:\d{2}(?::\d{2})?)/)
    : null;
  return match ? { date: match[1], time: match[2] } : null;
}

// drawMap() -> Mathematical and graphical engine
function drawMap(canvas, selectedId, time, powerConsumption, zoom) {
  const context = canvas.getContext('2d');
  if (!context) return;

  const width = canvas.clientWidth;
  const height = canvas.clientHeight;
  const ratio = window.devicePixelRatio || 1;
  if (canvas.width !== Math.round(width * ratio) || canvas.height !== Math.round(height * ratio)) {
    canvas.width = Math.round(width * ratio);
    canvas.height = Math.round(height * ratio);
  }
  context.setTransform(ratio, 0, 0, ratio, 0, 0);
  context.clearRect(0, 0, width, height);
  context.fillStyle = '#09141c';
  context.fillRect(0, 0, width, height);

  context.strokeStyle = 'rgba(125, 165, 174, 0.055)';
  context.lineWidth = 1;
  for (let x = 0; x < width; x += 32) {
    context.beginPath();
    context.moveTo(x, 0);
    context.lineTo(x, height);
    context.stroke();
  }
  for (let y = 0; y < height; y += 32) {
    context.beginPath();
    context.moveTo(0, y);
    context.lineTo(width, y);
    context.stroke();
  }

  context.save();
  context.translate(width / 2, height / 2);
  context.scale(zoom, zoom);
  context.translate(-width / 2, -height / 2);

  const point = (node) => ({ x: node.x * width, y: node.y * height });
  const nodeById = Object.fromEntries(NODES.map((node) => [node.id, node]));
  const connectionPoints = ([from, to]) => {
    const start = point(nodeById[from]);
    const end = point(nodeById[to]);
    start.x -= Math.min(83, width * 0.145);
    end.x += 15;
    return { start, end };
  };

  context.save();
  context.setLineDash([3, 10]);
  context.lineCap = 'round';
  context.lineWidth = 18;
  context.strokeStyle = 'rgba(123, 153, 159, 0.055)';
  CONNECTIONS.forEach(([from, to]) => {
    const { start, end } = connectionPoints([from, to]);
    context.beginPath();
    context.moveTo(start.x, start.y);
    context.lineTo(end.x, end.y);
    context.stroke();
  });
  context.restore();

  CONNECTIONS.forEach(([from, to], index) => {
    const { start, end } = connectionPoints([from, to]);
    context.beginPath();
    context.moveTo(start.x, start.y);
    context.lineTo(end.x, end.y);
    context.strokeStyle = 'rgba(52, 211, 153, 0.54)';
    context.lineWidth = 1.5;
    context.setLineDash([5, 6]);
    context.stroke();
    context.setLineDash([]);

    for (let pylonIndex = 1; pylonIndex <= 2; pylonIndex += 1) {
      const progress = pylonIndex / 3;
      const px = start.x + (end.x - start.x) * progress;
      const py = start.y + (end.y - start.y) * progress;
      context.strokeStyle = 'rgba(147, 177, 170, 0.42)';
      context.lineWidth = 1.3;
      context.beginPath();
      context.moveTo(px, py - 17);
      context.lineTo(px, py + 16);
      context.moveTo(px - 8, py - 10);
      context.lineTo(px + 8, py - 10);
      context.moveTo(px - 11, py - 2);
      context.lineTo(px + 11, py - 2);
      context.moveTo(px - 5, py + 7);
      context.lineTo(px + 5, py + 7);
      context.stroke();
    }

    for (let dotIndex = 1; dotIndex < FLOW_DOT_COUNT; dotIndex += 1) {
      const progress = (time / FLOW_CYCLE_MS + dotIndex / FLOW_DOT_COUNT + index * 0.23) % 1;
      const px = start.x + (end.x - start.x) * progress;
      const py = start.y + (end.y - start.y) * progress;
      context.beginPath();
      context.arc(px, py, 3, 0, Math.PI * 2);
      context.fillStyle = '#a4f5c5';
      context.shadowColor = '#54d69a';
      context.shadowBlur = 12;
      context.fill();
      context.shadowBlur = 0;

      if (dotIndex === 1 && Number.isFinite(powerConsumption)) {
        const label = `${powerConsumption.toFixed(2)} kW`;
        context.font = '600 12px Segoe UI, sans-serif';
        context.textAlign = 'center';
        context.textBaseline = 'middle';
        const labelWidth = context.measureText(label).width;
        const labelHeight = 19;
        const labelY = py - 27;
        context.fillStyle = 'rgba(7, 18, 19, 0.9)';
        context.fillRect(px - labelWidth / 2 - 6, labelY, labelWidth + 12, labelHeight);
        context.fillStyle = '#b7eccf';
        context.fillText(label, px, labelY + labelHeight / 2);
      }
    }

    for (let dotIndex = 2; dotIndex < FLOW_DOT_COUNT; dotIndex += 1) {
      const progress = (time / FLOW_CYCLE_MS + dotIndex / FLOW_DOT_COUNT + index * 0.23) % 1;
      const px = start.x + (end.x - start.x) * progress;
      const py = start.y + (end.y - start.y) * progress;
      context.beginPath();
      context.arc(px, py, 3, 0, Math.PI * 2);
      context.fillStyle = '#a4f5c5';
      context.shadowColor = '#54d69a';
      context.shadowBlur = 12;
      context.fill();
      context.shadowBlur = 0;

      if (dotIndex === 2 && Number.isFinite(powerConsumption)) {
        const label = `${powerConsumption.toFixed(2)} kW`;
        context.font = '600 12px Segoe UI, sans-serif';
        context.textAlign = 'center';
        context.textBaseline = 'middle';
        const labelWidth = context.measureText(label).width;
        const labelHeight = 19;
        const labelY = py - 27;
        context.fillStyle = 'rgba(7, 18, 19, 0.9)';
        context.fillRect(px - labelWidth / 2 - 6, labelY, labelWidth + 12, labelHeight);
        context.fillStyle = '#b7eccf';
        context.fillText(label, px, labelY + labelHeight / 2);
      }
    }

    for (let dotIndex = 3; dotIndex < FLOW_DOT_COUNT; dotIndex += 1) {
      const progress = (time / FLOW_CYCLE_MS + dotIndex / FLOW_DOT_COUNT + index * 0.23) % 1;
      const px = start.x + (end.x - start.x) * progress;
      const py = start.y + (end.y - start.y) * progress;
      context.beginPath();
      context.arc(px, py, 3, 0, Math.PI * 2);
      context.fillStyle = '#a4f5c5';
      context.shadowColor = '#54d69a';
      context.shadowBlur = 12;
      context.fill();
      context.shadowBlur = 0;

      if (dotIndex === 3 && Number.isFinite(powerConsumption)) {
        const label = `${powerConsumption.toFixed(2)} kW`;
        context.font = '600 12px Segoe UI, sans-serif';
        context.textAlign = 'center';
        context.textBaseline = 'middle';
        const labelWidth = context.measureText(label).width;
        const labelHeight = 19;
        const labelY = py - 27;
        context.fillStyle = 'rgba(7, 18, 19, 0.9)';
        context.fillRect(px - labelWidth / 2 - 6, labelY, labelWidth + 12, labelHeight);
        context.fillStyle = '#b7eccf';
        context.fillText(label, px, labelY + labelHeight / 2);
      }
    }
  });

  NODES.forEach((node) => {
    const { x, y } = point(node);
    const selected = node.id === selectedId;
    const pulse = 0.5 + Math.sin(time * 0.002 + node.x * 8) * 0.5;

    if (node.id === 'residential') {
      const boxWidth = Math.min(166, width * 0.29);
      const boxHeight = 104;
      const boxX = x - boxWidth / 2;
      const boxY = y - boxHeight / 2;
      context.fillStyle = 'rgba(15, 40, 34, 0.82)';
      context.fillRect(boxX, boxY, boxWidth, boxHeight);
      context.strokeStyle = selected ? node.color : 'rgba(84, 214, 154, 0.72)';
      context.lineWidth = selected ? 2 : 1.3;
      context.strokeRect(boxX, boxY, boxWidth, boxHeight);
      context.fillStyle = 'rgba(84, 214, 154, 0.12)';
      context.fillRect(boxX + 1, boxY + 1, boxWidth - 2, 22);
      context.font = '700 9px Segoe UI, sans-serif';
      context.textAlign = 'left';
      context.textBaseline = 'middle';
      context.fillStyle = '#b7eccf';
      context.fillText('RESIDENTIAL AREA', boxX + 9, boxY + 12);

      const houseY = boxY + 43;
      const houseGap = Math.min(37, (boxWidth - 28) / 3);
      for (let houseIndex = 0; houseIndex < 3; houseIndex += 1) {
        const houseX = boxX + 17 + houseIndex * houseGap;
        context.fillStyle = 'rgba(84, 214, 154, 0.18)';
        context.fillRect(houseX, houseY + 7, 21, 22);
        context.strokeStyle = 'rgba(136, 226, 179, 0.82)';
        context.lineWidth = 1.4;
        context.beginPath();
        context.moveTo(houseX - 2, houseY + 8);
        context.lineTo(houseX + 10, houseY);
        context.lineTo(houseX + 23, houseY + 8);
        context.moveTo(houseX + 10, houseY + 17);
        context.lineTo(houseX + 10, houseY + 29);
        context.stroke();
      }

      context.beginPath();
      context.arc(x, boxY - 5, 3 + pulse * 2, 0, Math.PI * 2);
      context.fillStyle = node.color;
      context.shadowColor = node.color;
      context.shadowBlur = 12;
      context.fill();
      context.shadowBlur = 0;
      return;
    }

    const radius = 15;

    context.beginPath();
    context.arc(x, y, radius + 9 + pulse * 3, 0, Math.PI * 2);
    context.fillStyle = `${node.color}${selected ? '20' : '12'}`;
    context.fill();
    context.beginPath();
    context.arc(x, y, radius + (selected ? 5 : 2), 0, Math.PI * 2);
    context.strokeStyle = selected ? node.color : `${node.color}80`;
    context.lineWidth = selected ? 1.8 : 1;
    context.stroke();
    context.beginPath();
    context.arc(x, y, radius, 0, Math.PI * 2);
    context.fillStyle = '#10242c';
    context.fill();
    context.beginPath();
    context.arc(x, y, radius * 0.38, 0, Math.PI * 2);
    context.fillStyle = node.color;
    context.shadowColor = node.color;
    context.shadowBlur = 13;
    context.fill();
    context.shadowBlur = 0;

    context.font = '600 11px Segoe UI, sans-serif';
    context.textAlign = 'center';
    context.textBaseline = 'top';
    context.fillStyle = selected ? '#f0f7f5' : '#b4c6c6';
    context.fillText('NORTH SUBSTATION', x, y + radius + 14);
  });
  context.restore();
}

// GridCanvas() -> React component that manages the canvas and user interaction.
function GridCanvas({ selectedId, onSelect, onNodeClick, powerConsumption }) {
  const canvasRef = useRef(null);
  const selectedRef = useRef(selectedId);
  const powerConsumptionRef = useRef(powerConsumption);
  const [zoom, setZoom] = useState(MIN_MAP_ZOOM);
  const zoomRef = useRef(zoom);
  const focusedNodeRef = useRef(selectedId);
  const onSelectRef = useRef(onSelect);
  const onNodeClickRef = useRef(onNodeClick);

  useEffect(() => {
    selectedRef.current = selectedId;
    focusedNodeRef.current = selectedId;
  }, [selectedId]);
  useEffect(() => { powerConsumptionRef.current = powerConsumption; }, [powerConsumption]);
  useEffect(() => { onSelectRef.current = onSelect; }, [onSelect]);
  useEffect(() => { onNodeClickRef.current = onNodeClick; }, [onNodeClick]);

  useEffect(() => {
    const canvas = canvasRef.current;
    const context = canvas?.getContext('2d');
    if (!canvas || !context) return undefined;

    let frame;
    const render = (time) => {
      drawMap(canvas, selectedRef.current, time, powerConsumptionRef.current, zoomRef.current);
      frame = window.requestAnimationFrame(render);
    };
    frame = window.requestAnimationFrame(render);
    const observer = new ResizeObserver(() => drawMap(
      canvas,
      selectedRef.current,
      performance.now(),
      powerConsumptionRef.current,
      zoomRef.current,
    ));
    observer.observe(canvas);

    return () => {
      window.cancelAnimationFrame(frame);
      observer.disconnect();
    };
  }, []);

  const nodeAtPointer = (event) => {
    const canvas = canvasRef.current;
    if (!canvas) return null;
    const bounds = canvas.getBoundingClientRect();
    const x = ((event.clientX - bounds.left) / bounds.width - 0.5) / zoomRef.current + 0.5;
    const y = ((event.clientY - bounds.top) / bounds.height - 0.5) / zoomRef.current + 0.5;
    return NODES.find((item) => {
      if (item.id === 'residential') {
        return Math.abs(item.x - x) * bounds.width < Math.min(83, bounds.width * 0.145)
          && Math.abs(item.y - y) * bounds.height < 52;
      }
      return Math.hypot((item.x - x) * bounds.width, (item.y - y) * bounds.height) < 29;
    });
  };

  const selectAtPointer = (event) => {
    const node = nodeAtPointer(event);
    if (node) {
      focusedNodeRef.current = node.id;
      onNodeClickRef.current(node.id);
    }
  };

  const adjustZoom = (change) => {
    setZoom((currentZoom) => {
      const nextZoom = Math.min(MAX_MAP_ZOOM, Math.max(MIN_MAP_ZOOM, currentZoom + change));
      zoomRef.current = nextZoom;
      return nextZoom;
    });
  };

  return (
    <>
      <canvas
        ref={canvasRef}
        className="grid-map-canvas"
        aria-label={`Grid map. Selected ${NODES.find((node) => node.id === focusedNodeRef.current)?.label}. Use arrow keys to choose a node and Enter to open it.`}
        onClick={selectAtPointer}
        onMouseMove={(event) => {
          event.currentTarget.style.cursor = nodeAtPointer(event) ? 'pointer' : 'crosshair';
        }}
        onKeyDown={(event) => {
          if (event.key === 'Enter' || event.key === ' ') {
            event.preventDefault();
            onNodeClickRef.current(focusedNodeRef.current);
            return;
          }
          if (event.key === 'ArrowLeft' || event.key === 'ArrowRight' || event.key === 'ArrowUp' || event.key === 'ArrowDown') {
            event.preventDefault();
            const currentIndex = NODES.findIndex((node) => node.id === focusedNodeRef.current);
            const direction = event.key === 'ArrowLeft' || event.key === 'ArrowUp' ? -1 : 1;
            const nextNode = NODES[(currentIndex + direction + NODES.length) % NODES.length];
            focusedNodeRef.current = nextNode.id;
            selectedRef.current = nextNode.id;
            onSelectRef.current(nextNode.id);
          }
        }}
        role="group"
        tabIndex={0}
      />
      <div className="map-zoom-controls" role="group" aria-label="Map zoom controls">
        <button
          type="button"
          onClick={() => adjustZoom(MAP_ZOOM_STEP)}
          disabled={zoom >= MAX_MAP_ZOOM}
          aria-label="Zoom in"
          title="Zoom in"
        >
          +
        </button>
        <span aria-live="polite">{Math.round(zoom * 100)}%</span>
        <button
          type="button"
          onClick={() => adjustZoom(-MAP_ZOOM_STEP)}
          disabled={zoom <= MIN_MAP_ZOOM}
          aria-label="Zoom out"
          title="Zoom out"
        >
          −
        </button>
      </div>
    </>
  );
}

// BoltMark() -> Small additional component that displays the lightning symbol.
function BoltMark() {
  return <span className="gridguard-mark" aria-hidden="true">ϟ</span>;
}

/* Dashboard() -> Main page containing the header, statistics, map, details panel, and logout  
functionality. */
export default function Dashboard() {
  const { user } = useOutletContext();
  const navigate = useNavigate();
  const [selectedId, setSelectedId] = useState('residential');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [liveData, setLiveData] = useState(null);
  const [liveError, setLiveError] = useState('');
  const simulationDateTime = splitSimulationDateTime(liveData?.simTime);
  const selectedNode = NODES.find((node) => node.id === selectedId) || NODES[0];
  const selectedLoad = selectedId === 'substation'
    ? liveData?.substation.currentLoadKw
    : liveData?.area.currentLoadKw;
  const connectedNodes = CONNECTIONS
    .filter(([from, to]) => from === selectedId || to === selectedId)
    .map(([from, to]) => NODES.find((node) => node.id === (from === selectedId ? to : from))?.label)
    .filter(Boolean);
  const initials = user.name
    .split(/\s+/)
    .map((part) => part[0])
    .join('')
    .slice(0, 2)
    .toUpperCase();

  useEffect(() => {
    let active = true;
    let timeout;
    let controller;

    const pollLiveData = async () => {
      controller = new AbortController();
      try {
        const nextLiveData = await fetchLiveData(controller.signal);
        if (active) {
          setLiveData(nextLiveData);
          setLiveError('');
        }
      } catch (liveRequestError) {
        if (active && liveRequestError.name !== 'AbortError') {
          setLiveError(liveRequestError.message);
        }
      } finally {
        if (active) {
          timeout = window.setTimeout(pollLiveData, LIVE_POLL_INTERVAL_MS);
        }
      }
    };

    pollLiveData();
    return () => {
      active = false;
      window.clearTimeout(timeout);
      controller?.abort();
    };
  }, []);

  const handleLogout = async () => {
    setError('');
    setLoading(true);
    try {
      await apiLogout();
      navigate('/login', { replace: true });
    } catch (logoutError) {
      setError(logoutError.message);
    } finally {
      setLoading(false);
    }
  };

  const handleNodeClick = (nodeId) => {
    if (nodeId === 'substation') {
      navigate('/substation/north-substation');
      return;
    }
    setSelectedId(nodeId);
  };

  return (
    <main className="dashboard-page">
      <header className="dashboard-header">
        <Link className="dashboard-brand" to="/" aria-label="GridGuard home">
          <BoltMark />
          <span>GRID<span className="brand-accent">GUARD</span></span>
        </Link>
        <div className="header-status"><span className="status-dot" /> GRID SYSTEM NORMAL</div>
        <div className="header-user">
          <div className="user-avatar">{initials || 'U'}</div>
          <div className="user-meta">
            <strong>{user.name}</strong>
            <span>{user.role === 'TECHNICAL_WORKER' ? 'Technical worker' : 'Grid user'}</span>
          </div>
          <button className="logout-button" onClick={handleLogout} disabled={loading}>
            {loading ? 'Signing out…' : 'Sign out'}
          </button>
        </div>
      </header>

      <div className="dashboard-content">
        <section className="dashboard-intro">
          <div>
            <p className="eyebrow">YOUR GRID AT A GLANCE</p>
            <h1>Good to see you, {user.name.split(/\s+/)[0]}.</h1>
            <p className="intro-copy">A live view of the network powering your community.</p>
          </div>
          <div className="network-pill" aria-live="polite">
            <span className="status-dot" />
            {liveError ? 'LIVE DATA STALE' : liveData ? 'NETWORK LIVE' : 'CONNECTING'}
          </div>
        </section>

        <section className="network-summary" aria-label="Network summary">
          <div className="summary-card">
            <span className="summary-icon summary-icon-green">↗</span>
            <div className="simulation-time">
              <span className="summary-label">SIMULATION TIME</span>
              <strong>{simulationDateTime?.time || 'Waiting for data'}</strong>
              {simulationDateTime && <span className="simulation-date">{simulationDateTime.date}</span>}
            </div>
            <span className="summary-live">LIVE</span>
          </div>
          <div className="summary-card">
            <span className="summary-icon">⌘</span>
            <div><span className="summary-label">CONNECTED NODES</span><strong>02 <small>active</small></strong></div>
          </div>
          <div className="summary-card">
            <span className="summary-icon summary-icon-amber">ϟ</span>
            <div><span className="summary-label">AREA LOAD</span><strong>{formatLoad(liveData?.area.currentLoadKw)}</strong></div>
          </div>
        </section>

        <section className="network-section">
          <div className="network-heading">
            <div>
              <p className="eyebrow">DISTRIBUTION NETWORK</p>
              <h2>Community map</h2>
            </div>
            <div className="map-key"><span className="key-line" /> Power flow <span className="key-node" /> Grid node</div>
          </div>

          <div className="network-layout">
            <div className="map-frame">
              <div className="map-topline">
                <span><span className="status-dot" /> GRID OVERVIEW</span>
                <span>RIVERSIDE DISTRICT <span className="map-separator">/</span> ZONE 04</span>
              </div>
              <GridCanvas
                selectedId={selectedId}
                onSelect={setSelectedId}
                onNodeClick={handleNodeClick}
                powerConsumption={liveData?.area.currentLoadKw}
              />
              <div className="map-coordinates">33°41' N&nbsp;&nbsp; 73°03' E</div>
            </div>

            <aside className="node-panel">
              <div className="panel-heading">
                <div><p className="eyebrow">SELECTED ASSET</p><h3>Node details</h3></div>
                <span className="node-status"><span className="status-dot" /> ONLINE</span>
              </div>
              <div className="selected-node">
                <span className="selected-node-icon"><BoltMark /></span>
                <div><strong>{selectedNode.label}</strong><span>{selectedNode.type}</span></div>
              </div>
              <div className="detail-row"><span>Operating status</span><strong className="detail-online">{selectedNode.status}</strong></div>
              <div className="detail-row"><span>Power Consumption</span><strong>{formatLoad(selectedLoad)}</strong></div>
              <div className="detail-row"><span>Connected to</span><strong>{connectedNodes.join(', ')}</strong></div>
              <div className="node-picker">
                <span className="summary-label">NETWORK NODES</span>
                {NODES.map((node) => (
                  <button
                    className={`node-picker-item${selectedId === node.id ? ' is-selected' : ''}`}
                    key={node.id}
                    onClick={() => handleNodeClick(node.id)}
                    aria-pressed={node.id === 'residential' ? selectedId === node.id : undefined}
                    aria-label={node.id === 'substation' ? `Open ${node.label} details` : `Select ${node.label}`}
                  >
                    <span className="picker-dot" style={{ '--node-color': node.color }} />
                    <span>{node.label}</span>
                    <span className="picker-chevron">›</span>
                  </button>
                ))}
              </div>
            </aside>
          </div>
          {error && <p className="dashboard-error" role="alert">{error}</p>}
          {liveError && <p className="dashboard-error" role="alert">{liveError}</p>}
        </section>
        <footer className="dashboard-footer"><span>GRIDGUARD COMMUNITY GRID</span><span>Network data is for demonstration purposes.</span></footer>
      </div>
    </main>
  );
}
