import { useEffect, useState } from 'react';
import {
  createSubstation,
  deleteSubstation,
  fetchSubstations,
  updateSubstation,
} from '../services/substations.js';

const EMPTY_FORM = {
  code: '',
  name: '',
  subProfileType: '',
  status: '',
  capacityKw: '',
  currentLoadKw: '',
};

// Copies backend fields into the editable form state.
function toFormValues(substation) {
  return {
    code: substation.code ?? '',
    name: substation.name ?? '',
    subProfileType: substation.subProfileType ?? '',
    status: substation.status ?? '',
    capacityKw: substation.capacityKw ?? '',
    currentLoadKw: substation.currentLoadKw ?? '',
  };
}

// Validates and converts form strings into the backend DTO shape.
function toRequestBody(values) {
  const capacityKw = values.capacityKw === '' ? null : Number(values.capacityKw);
  const currentLoadKw = values.currentLoadKw === '' ? null : Number(values.currentLoadKw);
  if (
    (capacityKw !== null && (!Number.isFinite(capacityKw) || capacityKw < 0))
    || (currentLoadKw !== null && (!Number.isFinite(currentLoadKw) || currentLoadKw < 0))
  ) {
    throw new Error('Capacity and current load must be valid non-negative numbers.');
  }

  return {
    code: values.code.trim(),
    name: values.name.trim(),
    subProfileType: values.subProfileType.trim(),
    status: values.status.trim(),
    capacityKw,
    currentLoadKw,
  };
}

// Provides technical workers with the substation create, read, update, and delete UI.
export default function TechnicalSubstationManager() {
  const [substations, setSubstations] = useState([]);
  const [form, setForm] = useState(EMPTY_FORM);
  const [editingId, setEditingId] = useState(null);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');
  const [message, setMessage] = useState('');

  // Reloads the backend list and reports loading errors in the management panel.
  const loadSubstations = async () => {
    setLoading(true);
    setError('');
    try {
      setSubstations(await fetchSubstations());
    } catch (requestError) {
      setError(requestError.message);
    } finally {
      setLoading(false);
    }
  };

  // Fetches the initial backend list when the manager becomes visible.
  useEffect(() => {
    loadSubstations();
  }, []);

  // Clears the editor and returns it to create mode.
  const resetForm = () => {
    setForm(EMPTY_FORM);
    setEditingId(null);
  };

  // Creates a record or saves edits, then refreshes the backend list.
  const handleSubmit = async (event) => {
    event.preventDefault();
    setSaving(true);
    setError('');
    setMessage('');
    try {
      const requestBody = toRequestBody(form);
      if (editingId === null) {
        await createSubstation(requestBody);
        setMessage('Substation created.');
      } else {
        await updateSubstation(editingId, requestBody);
        setMessage('Substation updated.');
      }
      resetForm();
      await loadSubstations();
    } catch (requestError) {
      setError(requestError.message);
    } finally {
      setSaving(false);
    }
  };

  // Confirms and deletes a record, then refreshes the backend list.
  const handleDelete = async (substation) => {
    if (!window.confirm(`Delete ${substation.name}? This cannot be undone.`)) return;

    setError('');
    setMessage('');
    try {
      await deleteSubstation(substation.substationId);
      if (editingId === substation.substationId) resetForm();
      setMessage(`${substation.name} deleted.`);
      await loadSubstations();
    } catch (requestError) {
      setError(requestError.message);
    }
  };

  return (
    <section className="substation-card technical-substation-manager" aria-labelledby="substation-management-title">
      <div className="substation-card-heading">
        <div>
          <p className="eyebrow">TECHNICAL WORKER TOOLS</p>
          <h2 id="substation-management-title">Manage substations</h2>
        </div>
        <button type="button" className="manager-secondary-button" onClick={loadSubstations} disabled={loading}>
          {loading ? 'Refreshing…' : 'Refresh'}
        </button>
      </div>

      {error && <p className="dashboard-error" role="alert">{error}</p>}
      {message && <p className="manager-success" role="status">{message}</p>}

      <form className="substation-form" onSubmit={handleSubmit}>
        <h3>{editingId === null ? 'Add substation' : `Edit substation #${editingId}`}</h3>
        <div className="substation-form-grid">
          <label>
            Code
            <input required value={form.code} onChange={(event) => setForm({ ...form, code: event.target.value })} />
          </label>
          <label>
            Name
            <input required value={form.name} onChange={(event) => setForm({ ...form, name: event.target.value })} />
          </label>
          <label>
            Profile type
            <input required value={form.subProfileType} onChange={(event) => setForm({ ...form, subProfileType: event.target.value })} />
          </label>
          <label>
            Status
            <input required value={form.status} onChange={(event) => setForm({ ...form, status: event.target.value })} />
          </label>
          <label>
            Capacity (kW)
            <input type="number" min="0" step="any" value={form.capacityKw} onChange={(event) => setForm({ ...form, capacityKw: event.target.value })} />
          </label>
          <label>
            Current load (kW)
            <input type="number" min="0" step="any" value={form.currentLoadKw} onChange={(event) => setForm({ ...form, currentLoadKw: event.target.value })} />
          </label>
        </div>
        <div className="manager-form-actions">
          <button type="submit" className="manager-primary-button" disabled={saving}>
            {saving ? 'Saving…' : editingId === null ? 'Create substation' : 'Save changes'}
          </button>
          {editingId !== null && (
            <button type="button" className="manager-secondary-button" onClick={resetForm} disabled={saving}>
              Cancel
            </button>
          )}
        </div>
      </form>

      <div className="managed-substation-list">
        <h3>Registered substations</h3>
        {loading && substations.length === 0 && <p className="manager-empty">Loading substations…</p>}
        {!loading && substations.length === 0 && !error && (
          <p className="manager-empty">No substations are registered yet.</p>
        )}
        {substations.map((substation) => (
          <article className="managed-substation" key={substation.substationId}>
            <div className="managed-substation-details">
              <strong>{substation.name}</strong>
              <span>{substation.code} · {substation.status} · {substation.subProfileType}</span>
              <span>
                ID {substation.substationId}
                {Number.isFinite(substation.capacityKw) ? ` · ${substation.capacityKw} kW capacity` : ''}
              </span>
            </div>
            <div className="managed-substation-actions">
              <button
                type="button"
                className="manager-secondary-button"
                onClick={() => {
                  setEditingId(substation.substationId);
                  setForm(toFormValues(substation));
                  setError('');
                  setMessage('');
                }}
              >
                Edit
              </button>
              <button
                type="button"
                className="manager-danger-button"
                onClick={() => handleDelete(substation)}
              >
                Delete
              </button>
            </div>
          </article>
        ))}
      </div>
    </section>
  );
}
