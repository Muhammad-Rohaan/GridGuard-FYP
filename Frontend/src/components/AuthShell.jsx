export default function AuthShell({ title, subtitle, children }) {
  return (
    <div className="auth-root">
      <div className="auth-wrap">
        <header className="brand-panel">
          <h1>
            GridGuard <span>AI</span>
          </h1>
        </header>

        <section className="auth-card">
          <div className="auth-heading">
            <h2 className="card-title">{title}</h2>
            {subtitle && <p className="card-sub">{subtitle}</p>}
          </div>
          {children}
          <div className="card-foot">GridGuard AI · Secure Account Access</div>
        </section>
      </div>
    </div>
  );
}
