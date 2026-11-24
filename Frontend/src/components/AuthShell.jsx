export default function AuthShell({ title, subtitle, children }) {
  return (
    <div className="auth-root">
      <div className="auth-wrap">
        <aside className="brand-panel">
          <h1>GridGuard <span>AI</span></h1>
        </aside>
        <section className="auth-card">
          <span className="corner c-tl" /><span className="corner c-br" />
          <h2 className="card-title">{title}</h2>
          <p className="card-sub">{subtitle}</p>
          {children}
        </section>
      </div>
    </div>
  );
}
