import { useState } from 'react';
import { apiSignup } from '../services/auth.js';

const EMPTY_FORM = { name: '', email: '', password: '' };

export default function AdminTechnicalWorkerManager() {
  const [form, setForm] = useState(EMPTY_FORM);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');
  const [message, setMessage] = useState('');

  const handleSubmit = async (event) => {
    event.preventDefault();
    setSaving(true);
    setError('');
    setMessage('');
    try {
      await apiSignup({
        ...form,
        name: form.name.trim(),
        email: form.email.trim(),
        roles: 'TECHNICAL_WORKER',
      });
      setForm(EMPTY_FORM);
      setMessage('Technical worker account created.');
    } catch (requestError) {
      setError(requestError.message);
    } finally {
      setSaving(false);
    }
  };

  return (
    <section className="substation-card admin-substation-manager" aria-labelledby="technical-worker-title">
      <div className="substation-card-heading">
        <div>
          <p className="eyebrow">ADMINISTRATION</p>
          <h2 id="technical-worker-title">Create technical worker account</h2>
        </div>
      </div>

      {error && <p className="dashboard-error" role="alert">{error}</p>}
      {message && <p className="manager-success" role="status">{message}</p>}

      <form className="substation-form" onSubmit={handleSubmit}>
        <div className="substation-form-grid">
          <label>
            Full name
            <input
              required
              autoComplete="name"
              value={form.name}
              onChange={(event) => setForm({ ...form, name: event.target.value })}
            />
          </label>
          <label>
            Email
            <input
              type="email"
              required
              autoComplete="email"
              value={form.email}
              onChange={(event) => setForm({ ...form, email: event.target.value })}
            />
          </label>
          <label>
            Password
            <input
              type="password"
              required
              minLength="8"
              autoComplete="new-password"
              value={form.password}
              onChange={(event) => setForm({ ...form, password: event.target.value })}
            />
          </label>
        </div>
        <div className="manager-form-actions">
          <button type="submit" className="manager-primary-button" disabled={saving}>
            {saving ? 'Creating account…' : 'Create technical worker'}
          </button>
        </div>
      </form>
    </section>
  );
}
