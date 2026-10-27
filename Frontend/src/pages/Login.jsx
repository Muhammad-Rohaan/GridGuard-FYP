import { useState } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import AuthShell from '../components/AuthShell.jsx';
import { apiLogin } from '../services/auth.js';
import { Mail, Lock, Eye, EyeOff } from '../components/icons.jsx';

export default function Login() {
  const nav = useNavigate();
  const loc = useLocation();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [show, setShow] = useState(false);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const registered = loc.state?.registered;

  const submit = async (e) => {
    e.preventDefault(); setError('');
    if (!/^\S+@\S+\.\S+$/.test(email)) return setError('Please enter a valid email address.');
    if (!password) return setError('Password is required.');
    setLoading(true);
    try {
      const { user } = await apiLogin(email, password);
      nav('/', { replace: true, state: { user } });
    } catch (err) { setError(err.message); }
    finally { setLoading(false); }
  };

  return (
    <AuthShell title="Sign-In" subtitle="Authenticate to access the grid control dashboard">
      {registered && <div className="alert alert-success">⚡ Account energized. Please sign in.</div>}
      {error && <div className="alert alert-error">⚠ {error}</div>}
      <form onSubmit={submit} noValidate>
        <div className="field">
          <label>Email</label>
          <div className="input-wrap"><Mail className="lead-icon" />
            <input type="email" placeholder="name@gridguard.ai" value={email} onChange={(e) => setEmail(e.target.value)} autoComplete="email" />
          </div>
        </div>
        <div className="field">
          <label>Password</label>
          <div className="input-wrap"><Lock className="lead-icon" />
            <input type={show ? 'text' : 'password'} placeholder="••••••••" value={password} onChange={(e) => setPassword(e.target.value)} autoComplete="current-password" />
            <button type="button" className="eye" onClick={() => setShow(!show)} tabIndex={-1}>{show ? <EyeOff /> : <Eye />}</button>
          </div>
        </div>
        <button className="btn-primary" disabled={loading}>
          {loading ? <span className="spin" /> : null}{loading ? 'Connecting to grid…' : '⚡ Energize Session'}
        </button>
      </form>
      <p className="switch-line">New User? <Link to="/signup">Request grid access</Link></p>
    </AuthShell>
  );
}
