import React, { useState, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import Alert from '../components/Alert';

export default function AdminLoginPage() {
  const navigate = useNavigate();
  const location = useLocation();
  const { login, isAuthenticated } = useAuth();

  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);

  // If already authenticated, redirect to Admin Dashboard
  useEffect(() => {
    if (isAuthenticated) {
      navigate('/admin/dashboard', { replace: true });
    }
  }, [isAuthenticated, navigate]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);

    if (!username.trim() || !password) {
      setError('Please provide both username and password');
      return;
    }

    setLoading(true);
    try {
      await login(username.trim(), password);
      // Redirect back to attempted page or to admin dashboard
      const from = location.state?.from?.pathname || '/admin/dashboard';
      navigate(from, { replace: true });
    } catch (err) {
      console.error('Admin login failed:', err);
      setError(err.message || 'Invalid username or password. Please verify credentials.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-page page-container">
      <div className="login-card-wrapper">
        <div className="login-card">
          <div className="login-header text-center">
            <div className="admin-lock-icon">🔐</div>
            <h1 className="login-title">Administrator Portal</h1>
            <p className="login-subtitle">
              Secure administrative access for donor records and blood request coordination.
            </p>
          </div>

          {error && (
            <Alert type="error" message={error} onDismiss={() => setError(null)} />
          )}

          <form onSubmit={handleSubmit} noValidate>
            <div className="form-group">
              <label htmlFor="login-username" className="form-label required">
                Admin Username
              </label>
              <input
                id="login-username"
                type="text"
                className="form-control"
                placeholder="Enter admin username"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                autoComplete="username"
                required
              />
            </div>

            <div className="form-group">
              <label htmlFor="login-password" className="form-label required">
                Password
              </label>
              <input
                id="login-password"
                type="password"
                className="form-control"
                placeholder="Enter password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                autoComplete="current-password"
                required
              />
            </div>

            <button
              type="submit"
              className="btn btn-primary btn-lg btn-block"
              disabled={loading}
            >
              {loading ? 'Authenticating with PBKDF2...' : 'Sign In as Administrator →'}
            </button>
          </form>

          {/* Safe Local Demo Credentials Notice */}
          <div className="demo-credentials-box">
            <h4>Demo Evaluation Credentials:</h4>
            <p>For college examination, use the pre-seeded admin account:</p>
            <ul>
              <li><strong>Username:</strong> <code>admin</code></li>
              <li><strong>Password:</strong> <code>AdminPassword@123</code></li>
            </ul>
            <small className="text-muted">
              Passwords are salted and hashed using Java's built-in <code>PBKDF2WithHmacSHA256</code> (65,536 iterations).
            </small>
          </div>
        </div>
      </div>
    </div>
  );
}
