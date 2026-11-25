export default function AuthShell({ title, subtitle, children }) {
  return (
    <div className="auth-root">
      <div className="auth-wrap">
        <header className="brand-panel">
          <h1>GridGuard <span>AI</span></h1>
        </header>
        <section className="auth-card">
          <span className="corner c-tl" /><span className="corner c-br" />
          <h2 className="card-title">{title}</h2>
          {subtitle && <p className="card-sub">{subtitle}</p>}
          {children}
        </section>
      </div>
    </div>
  );
}
