import { useEffect, useState } from 'react';
import { BrowserRouter, Routes, Route, Navigate, useNavigate } from 'react-router-dom';
import Login from './pages/Login.jsx';
import SignUp from './pages/SignUp.jsx';
import { apiCurrentUser, apiLogout } from './services/auth.js';

function Dashboard({ user }) {
  const nav = useNavigate();
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const isTechnicalWorker = user.role === 'TECHNICAL_WORKER';

  const handleLogout = async () => {
    setError('');
    setLoading(true);
    try {
      await apiLogout();
      nav('/login', { replace: true });
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="dash-placeholder">
      <h2>
        ⚡ {isTechnicalWorker ? 'Technical Worker Dashboard' : 'User Dashboard'}
        {' — '}{user.name}
      </h2>
      <p>
        {isTechnicalWorker
          ? 'Your technical worker workspace is ready.'
          : 'Your GridGuard user workspace is ready.'}
      </p>
      {error && <p role="alert">{error}</p>}
      <button className="btn-primary" style={{ width: 'auto' }}
        onClick={handleLogout} disabled={loading}>
        {loading ? 'Disconnecting…' : 'De-energize (Logout)'}
      </button>
    </div>
  );
}

function Protected() {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let active = true;
    apiCurrentUser()
      .then((currentUser) => {
        if (active) setUser(currentUser);
      })
      .catch(() => {
        if (active) setUser(null);
      })
      .finally(() => {
        if (active) setLoading(false);
      });

    return () => { active = false; };
  }, []);

  if (loading) return <div className="dash-placeholder">Checking your session…</div>;
  return user ? <Dashboard user={user} /> : <Navigate to="/login" replace />;
}

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/signup" element={<SignUp />} />
        <Route path="/" element={<Protected />} />
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </BrowserRouter>
  );
}
