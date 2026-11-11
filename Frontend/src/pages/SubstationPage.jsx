import { useState } from 'react';
import { Link, useNavigate, useOutletContext, useParams } from 'react-router-dom';
import { apiLogout } from '../services/auth.js';
import { useLiveTelemetry } from '../services/LiveTelemetryProvider.jsx';
import LiveLoadChart from './LiveLoadChart.jsx';
import TechnicalSubstationManager from './TechnicalSubstationManager.jsx';
import './dashboard.css';

function BoltMark() {
  return <span className="gridguard-mark" aria-hidden="true">ϟ</span>;
}

export default function SubstationPage() {
  const { user } = useOutletContext();
  const { substationId } = useParams();
  const navigate = useNavigate();
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const { liveError, history, assetOverview } = useLiveTelemetry();
  const substation = assetOverview?.substation;
  const area = assetOverview?.area;
  const initials = user.name
    .split(/\s+/)
    .map((part) => part[0])
    .join('')
    .slice(0, 2)
    .toUpperCase();

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

  return (
    <main className="dashboard-page">
      <header className="dashboard-header">
        <Link className="dashboard-brand" to="/" aria-label="GridGuard dashboard">
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

      <div className="dashboard-content substation-content">
        <Link className="back-to-map" to="/">← Back to community map</Link>
        <section className="substation-title">
          <div>
            <p className="eyebrow">GRID ASSET / SUBSTATION</p>
            <h1>{substation?.name || 'North Substation'}</h1>
            <p className="intro-copy">Substation overview and configuration.</p>
          </div>
          <span className={`node-status substation-connection${liveError ? ' is-stale' : ''}`}>
            <span className="status-dot" /> {liveError ? 'LIVE DATA STALE' : substation ? 'LIVE TELEMETRY' : 'CONNECTING'}
          </span>
        </section>

        <section className="substation-card" aria-labelledby="substation-overview">
          <div className="substation-card-heading">
            <div className="substation-symbol"><BoltMark /></div>
            <div>
              <p className="eyebrow">ASSET OVERVIEW</p>
              <h2 id="substation-overview">Substation details</h2>
            </div>
          </div>
          <div className="substation-fields">
            <div className="substation-field">
              <span>Asset ID</span>
              <strong>{substation?.substationId ?? substationId}</strong>
            </div>
            <div className="substation-field">
              <span>Code</span>
              <strong>{substation?.code || 'Waiting for backend data'}</strong>
            </div>
            <div className="substation-field">
              <span>Asset type</span>
              <strong>Substation</strong>
            </div>
            <div className="substation-field">
              <span>Profile type</span>
              <strong>{substation?.subProfileType || 'Waiting for backend data'}</strong>
            </div>
            <div className="substation-field">
              <span>Connected area</span>
              <strong>{area?.name || 'Waiting for backend data'}</strong>
            </div>
            <div className="substation-field">
              <span>Operating status</span>
              <strong>{substation?.status || 'Waiting for backend data'}</strong>
            </div>
            <div className="substation-field">
              <span>Capacity</span>
              <strong>{Number.isFinite(substation?.capacityKw)
                ? `${substation.capacityKw.toFixed(2)} kW`
                : 'Waiting for backend data'}</strong>
            </div>
            <div className="substation-field">
              <span>Stored load</span>
              <strong>{Number.isFinite(substation?.currentLoadKw)
                ? `${substation.currentLoadKw.toFixed(2)} kW`
                : 'Waiting for backend data'}</strong>
            </div>
            <div className="substation-field">
              <span>Area type</span>
              <strong>{area?.type || 'Waiting for backend data'}</strong>
            </div>
          </div>
        </section>
        {user.role === 'TECHNICAL_WORKER' && <TechnicalSubstationManager />}
        <LiveLoadChart history={history} />
        {liveError && <p className="dashboard-error" role="alert">{liveError}</p>}
        {error && <p className="dashboard-error" role="alert">{error}</p>}
      </div>
    </main>
  );
}
