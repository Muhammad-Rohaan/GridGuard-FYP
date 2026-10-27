# GridGuard AI — React Frontend (Auth Module, Electricity Theme)

## Run
npm install
npm run dev        # http://localhost:5173  ( /signup and /login )

## Java JWT contract (Spring Boot on :5000, proxied via Vite /api)
POST /api/auth/register-user -> 201 (defaults to USER; local tests may pass `roles: "TECHNICAL_WORKER"`)
POST /api/auth/login         -> 200 (sets an HttpOnly JWT cookie)
GET  /api/auth/me            -> 200 { id, name, email, role } (requires the cookie)
POST /api/auth/logout        -> 204 (clears the cookie)

The Vite development server proxies `/api` to `http://localhost:5000`. The JWT is
kept in an HttpOnly cookie and is not exposed to browser JavaScript or local storage.
For HTTPS deployments, set `gridguard.auth.cookie-secure=true`.
The React app displays a separate dashboard for `USER` and `TECHNICAL_WORKER`.
Public signup omits the role and therefore creates a `USER` account. For testing a
technical-worker account through the API, include `"roles": "TECHNICAL_WORKER"` in
the registration JSON. This registration endpoint is public, so role selection is
for local testing only; restrict worker provisioning before production use.

## Structure
src/services/auth.js            JWT cookie-based API client
src/components/ElectricBackground.jsx  animated AC-wave / electron / lightning canvas
src/components/AuthShell.jsx    control-room frame + brand panel
src/pages/Login.jsx, SignUp.jsx themed auth pages (voltage-gauge password strength)
src/App.jsx                     routes + JWT protected route
src/styles/auth.css             full electricity theme
