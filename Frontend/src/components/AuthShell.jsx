import ElectricBackground from './ElectricBackground.jsx';
import { Bolt } from './icons.jsx';

const FEATURES = [
  'Federated Learning — raw data never leaves the substation',
  'Agentic AI — Forecast · Fault · Security · Decision agents',
  'Explainable AI — SHAP reasons with every alert',
  'Human-in-the-loop — operator approves every action',
];

export default function AuthShell({ title, subtitle, children }) {
  return (
    <div className="auth-root">
      <ElectricBackground />
      <div className="auth-wrap">
        <aside className="brand-panel">
          <div className="logo-hex"><Bolt width="26" height="26" /></div>
          <h1>GridGuard <span>AI</span></h1>
          <p className="tagline">Federated Smart Grid Intelligence Agent</p>
          <div className="live-row">
            <span className="chip chip-ok">A</span><span className="chip chip-ok">B</span>
            <span className="chip chip-warn">C</span><span className="chip chip-crit">D</span>
            <span className="live-label">● LIVE SIMULATION</span>
          </div>
          <ul className="feature-list">
            {FEATURES.map((f) => (<li key={f}><Bolt width="12" height="12" /> {f}</li>))}
          </ul>
          <p className="mono-note">FedAvg · SHAP · JWT-RBAC · WebSocket telemetry</p>
        </aside>
        <section className="auth-card">
          <span className="corner c-tl" /><span className="corner c-br" />
          <h2 className="card-title">{title}</h2>
          <p className="card-sub">{subtitle}</p>
          {children}
          <p className="card-foot">SZABIST · FYP 2026 · Decision support, not grid control</p>
        </section>
      </div>
    </div>
  );
}
