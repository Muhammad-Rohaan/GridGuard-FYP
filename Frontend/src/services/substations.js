const API_BASE = import.meta.env.VITE_API_BASE_URL || '/api';
const SUBSTATIONS_PATH = '/technical-worker/substations';

// Sends an authenticated CRUD request and surfaces backend errors.
async function request(path, options = {}) {
  const response = await fetch(`${API_BASE}${SUBSTATIONS_PATH}${path}`, {
    credentials: 'include',
    ...options,
  });
  const contentType = response.headers.get('content-type') || '';
  const payload = response.status === 204
    ? null
    : contentType.includes('application/json')
      ? await response.json()
      : await response.text();

  if (!response.ok) {
    const message = typeof payload === 'string' ? payload : payload?.message || payload?.error;
    throw new Error(message || `Substation request failed (${response.status}).`);
  }
  return payload;
}

// Retrieves the substations available to a technical worker.
export async function fetchSubstations() {
  const payload = await request('/fetch-subs');
  if (!Array.isArray(payload)) {
    throw new Error('Substation response must be an array.');
  }
  return payload;
}

// Creates a substation through the technical-worker API.
export function createSubstation(substation) {
  return request('/register-substation', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(substation),
  });
}

// Persists edits to an existing substation.
export function updateSubstation(id, substation) {
  return request(`/update-substation/${encodeURIComponent(id)}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(substation),
  });
}

// Removes a substation through the technical-worker API.
export function deleteSubstation(id) {
  return request(`/delete-substation/${encodeURIComponent(id)}`, {
    method: 'DELETE',
  });
}
