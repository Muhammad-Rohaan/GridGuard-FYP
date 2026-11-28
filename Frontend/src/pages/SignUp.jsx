import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import AuthShell from "../components/AuthShell.jsx";
import { apiSignup } from "../services/auth.js";
import { User, Mail, Lock, Eye, EyeOff } from "../components/icons.jsx";

const strengthOf = (p) =>
  [
    p.length >= 8,
    /[A-Z]/.test(p),
    /[0-9]/.test(p),
    /[^A-Za-z0-9]/.test(p),
  ].filter(Boolean).length;

const STRENGTH = ["EMPTY", "WEAK", "FAIR", "GOOD", "STRONG"];

export default function SignUp() {
  const nav = useNavigate();
  const [f, setF] = useState({
    name: "",
    email: "",
    password: "",
    confirm: "",
    phone: "",
    affiliation: "",
  });
  const [show, setShow] = useState(false);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const set = (k) => (e) => setF({ ...f, [k]: e.target.value });
  const level = strengthOf(f.password);

  const submit = async (e) => {
    e.preventDefault();
    setError("");
    if (f.name.trim().length < 3)
      return setError("Please enter your full name.");
    if (!/^\S+@\S+\.\S+$/.test(f.email))
      return setError("Please enter a valid email address.");
    if (
      !(
        f.password.length > 0 &&
        /[A-Za-z]/.test(f.password) &&
        /[0-9]/.test(f.password)
      )
    )
      return setError("Password must be entered.");
    if (f.password !== f.confirm) return setError("Passwords do not match.");
    setLoading(true);
    try {
      await apiSignup({
        name: f.name.trim(),
        email: f.email.trim(),
        password: f.password,
        phone: f.phone || null,
        affiliation: f.affiliation || null,
      });
      nav("/login", { state: { registered: true } });
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <AuthShell
      title="Create your account"
      subtitle="Get started by entering your details below."
    >
      {error && <div className="alert alert-error">{error}</div>}
      <form onSubmit={submit} noValidate>
        <div className="field">
          <label htmlFor="name">Full Name</label>
          <div className="input-wrap">
            <User className="lead-icon" />
            <input
              id="name"
              type="text"
              placeholder="Enter your full name"
              value={f.name}
              onChange={set("name")}
              autoComplete="name"
            />
          </div>
        </div>

        <div className="field">
          <label htmlFor="email">Email address</label>
          <div className="input-wrap">
            <Mail className="lead-icon" />
            <input
              id="email"
              type="email"
              placeholder="you@example.com"
              value={f.email}
              onChange={set("email")}
              autoComplete="email"
            />
          </div>
        </div>

        <div className="field-row">
          <div className="field">
            <label htmlFor="password">Password</label>
            <div className="input-wrap">
              <Lock className="lead-icon" />
              <input
                id="password"
                type={show ? "text" : "password"}
                placeholder="Create password"
                value={f.password}
                onChange={set("password")}
                autoComplete="new-password"
              />
              <button
                type="button"
                className="eye"
                onClick={() => setShow(!show)}
                aria-label={show ? "Hide password" : "Show password"}
              >
                {show ? <EyeOff /> : <Eye />}
              </button>
            </div>
          </div>

          <div className="field">
            <label htmlFor="confirm">Confirm password</label>
            <div className="input-wrap">
              <Lock className="lead-icon" />
              <input
                id="confirm"
                type={show ? "text" : "password"}
                placeholder="Repeat password"
                value={f.confirm}
                onChange={set("confirm")}
                autoComplete="new-password"
              />
            </div>
          </div>
        </div>

        <div className="strength" data-level={level}>
          <div className="strength-bars">
            {[1, 2, 3, 4].map((n) => (
              <span key={n} className={n <= level ? "active" : ""} />
            ))}
          </div>
          <span>{STRENGTH[level]}</span>
        </div>

        <button className="btn-primary" type="submit" disabled={loading}>
          {loading ? "Create account..." : "Create account"}
        </button>
      </form>

      <p className="switch-line">
        Already have an account? <Link to="/login">Sign in</Link>
      </p>
    </AuthShell>
  );
}
