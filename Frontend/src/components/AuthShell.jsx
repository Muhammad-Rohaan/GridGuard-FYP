import ElectricBackground from './ElectricBackground.jsx';
import { Bolt } from './icons.jsx';


export default function AuthShell({ title, subtitle, children }) {
  return (
    <div className="auth-root">
      <ElectricBackground />
      <div className="auth-wrap">
        <aside className="brand-panel">
          <div className="logo-hex"><Bolt width="26" height="26" /></div>
          <h1>GridGuard <span>AI</span></h1>
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
