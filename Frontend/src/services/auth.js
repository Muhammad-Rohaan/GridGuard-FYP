// Authentication is maintained by the backend's HttpOnly JWT cookie.
const API_BASE = import.meta.env.VITE_API_BASE_URL || '/api';

async function request(path, body) {
  const response = await fetch(`${API_BASE}${path}`, {
    method: body === undefined ? 'GET' : 'POST',
    credentials: 'include',
    headers: body === undefined ? {} : { 'Content-Type': 'application/json' },
    ...(body === undefined ? {} : { body: JSON.stringify(body) }),
  });
  const contentType = response.headers.get('content-type') || '';
  const data = contentType.includes('application/json')
    ? await response.json()
    : await response.text();

  if (!response.ok) {
    const message = typeof data === 'string' ? data : data.message || data.error;
    throw new Error(message || `Request failed (${response.status}). Is the grid server online?`);
  }
  return data;
}

export async function apiLogin(email, password) {
  await request('/auth/login', { email, password });
  const user = await apiCurrentUser();
  return { user };
}

export async function apiSignup(payload) {
  return request('/auth/register-user', {
    fullName: payload.name,
    email: payload.email,
    password: payload.password,
  });
}

export async function apiCurrentUser() {
  return request('/auth/me');
}

export async function apiLogout() {
  return request('/auth/logout', {});
}
