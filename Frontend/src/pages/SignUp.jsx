import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import AuthShell from '../components/AuthShell.jsx';
import { apiSignup } from '../services/auth.js';
import { User, Mail, Lock, Eye, EyeOff } from '../components/icons.jsx';

const strengthOf = (p) => [p.length >= 8, /[A-Z]/.test(p), /[0-9]/.test(p), /[^A-Za-z0-9]/.test(p)].filter(Boolean).length;
const STRENGTH = ['DE-ENERGIZED', 'LOW VOLTAGE', 'STABLE', 'HIGH VOLTAGE', 'SURGE LEVEL'];

export default function SignUp() {
  const nav = useNavigate();
  const [f, setF] = useState({ name: '', email: '', password: '', confirm: '', phone: '', affiliation: '' });
  const [show, setShow] = useState(false);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const set = (k) => (e) => setF({ ...f, [k]: e.target.value });
  const level = strengthOf(f.password);

  const submit = async (e) => {
    e.preventDefault(); setError('');
    if (f.name.trim().length < 3) return setError('Please enter your full name.');
    if (!/^\S+@\S+\.\S+$/.test(f.email)) return setError('Please enter a valid email address.');
    if (!(f.password.length > 0 && /[A-Za-z]/.test(f.password) && /[0-9]/.test(f.password)))
      return setError('Password must be entered.');
    if (f.password !== f.confirm) return setError('Passwords do not match.');
    setLoading(true);
    try {
      await apiSignup({ name: f.name.trim(), email: f.email.trim(), password: f.password, phone: f.phone || null, affiliation: f.affiliation || null });
      nav('/login', { state: { registered: true } });
    } catch (err) { setError(err.message); }
    finally { setLoading(false); }
  };

  return (
    <AuthShell title="Register New User" subtitle="Create an user account">
      {error && <div className="alert alert-error">⚠ {error}</div>}
      <form onSubmit={submit} noValidate>
        <div className="field">
          <label>Full Name</label>
          <div className="input-wrap"><User className="lead-icon" /><input placeholder="e.g., Muhammad Rohan" value={f.name} onChange={set('name')} /></div>
        </div>
        <div className="field">
          <label>Email</label>
          <div className="input-wrap"><Mail className="lead-icon" /><input type="email" placeholder="you@utility.pk" value={f.email} onChange={set('email')} /></div>
        </div>
        <div className="field-row">
          <div className="field">
            <label>Password</label>
            <div className="input-wrap"><Lock className="lead-icon" />
              <input type={show ? 'text' : 'password'} value={f.password} onChange={set('password')} />
              <button type="button" className="eye" onClick={() => setShow(!show)} tabIndex={-1}>{show ? <EyeOff /> : <Eye />}</button>
            </div>
          </div>
          <div className="field">
            <label>Confirm Password</label>
            <div className="input-wrap"><Lock className="lead-icon" /><input type={show ? 'text' : 'password'} value={f.confirm} onChange={set('confirm')} /></div>
          </div>
        </div>
        <div className="strength" data-level={level}>
          <div className="seg" /><div className="seg" /><div className="seg" /><div className="seg" />
          <span className="strength-label">{STRENGTH[level]}</span>
        </div>
        <button className="btn-primary" disabled={loading}>
          {loading ? <span className="spin" /> : null}{loading ? 'Energizing account…' : '⚡ Create User Account'}
        </button>
      </form>
      <p className="switch-line">Already registered? <Link to="/login">Sign in</Link></p>
    </AuthShell>
  );
}
