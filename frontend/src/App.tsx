import { useMemo, useState } from 'react';
import { HashRouter, NavLink, Route, Routes } from 'react-router-dom';
import axios from 'axios';
import HomePage from './pages/HomePage';
import CarsPage from './pages/CarsPage';
import RentalsPage from './pages/RentalsPage';
import ServicesPage from './pages/ServicesPage';
import UsersPage from './pages/UsersPage';
import './App.css';
import { api, clearAuth, getErrorMessage, getStoredUser, saveAuth, type AuthUser, type LoginResponse } from './api';

type Credentials = {
  username: string;
  password: string;
};

function App() {
  const [user, setUser] = useState<AuthUser | null>(getStoredUser());
  const [credentials, setCredentials] = useState<Credentials>({ username: '', password: '' });
  const [authMessage, setAuthMessage] = useState('');
  const [authError, setAuthError] = useState('');
  const [authLoading, setAuthLoading] = useState(false);

  const isAdmin = useMemo(() => user?.role === 'ADMIN', [user]);

  const login = async () => {
    setAuthLoading(true);
    setAuthError('');
    setAuthMessage('');

    try {
      const response = await api.post<LoginResponse>('/auth/login', credentials);
      saveAuth(response.data);
      setUser({ userId: response.data.userId, username: response.data.username, role: response.data.role });
      setAuthMessage(`Logged in as ${response.data.username} (${response.data.role}).`);
      setCredentials({ username: '', password: '' });
    } catch (error) {
      setAuthError(getErrorMessage(error));
    } finally {
      setAuthLoading(false);
    }
  };

  const logout = async () => {
    try {
      await api.post('/auth/logout');
    } catch (error) {
      if (!axios.isAxiosError(error)) {
        console.error(error);
      }
    } finally {
      clearAuth();
      setUser(null);
      setAuthMessage('Logged out.');
      setAuthError('');
    }
  };

  return (
    <HashRouter>
      <div className="app-shell">
        <aside className="sidebar">
          <div className="brand-card">
            <p className="eyebrow">Spring Boot + React</p>
            <h1>Car Rental Panel</h1>
            <p className="muted">
              A clean interface for managing cars, rentals, services, and users.
            </p>
          </div>

          <nav className="sidebar-nav">
            <NavLink to="/" end className={({ isActive }) => (isActive ? 'nav-link active' : 'nav-link')}>
              Dashboard
            </NavLink>
            <NavLink to="/cars" className={({ isActive }) => (isActive ? 'nav-link active' : 'nav-link')}>
              Cars
            </NavLink>
            <NavLink to="/rentals" className={({ isActive }) => (isActive ? 'nav-link active' : 'nav-link')}>
              Rentals
            </NavLink>
            <NavLink to="/services" className={({ isActive }) => (isActive ? 'nav-link active' : 'nav-link')}>
              Services
            </NavLink>
            <NavLink to="/users" className={({ isActive }) => (isActive ? 'nav-link active' : 'nav-link')}>
              Users
            </NavLink>
          </nav>

          <div className="auth-card">
            <div className="auth-header">
              <strong>{user ? 'Session' : 'Login'}</strong>
              <span className={`role-badge ${isAdmin ? 'admin' : 'user'}`}>{user?.role ?? 'Guest'}</span>
            </div>

            {user ? (
              <>
                <p className="muted">
                  Signed in as <strong>{user.username}</strong>
                </p>
                <p className="muted small">User ID: {user.userId}</p>
                <button className="button secondary" onClick={logout}>Log out</button>
              </>
            ) : (
              <div className="form-grid compact">
                <label>
                  Username
                  <input
                    value={credentials.username}
                    onChange={(e) => setCredentials({ ...credentials, username: e.target.value })}
                    placeholder="Enter username"
                  />
                </label>
                <label>
                  Password
                  <input
                    type="password"
                    value={credentials.password}
                    onChange={(e) => setCredentials({ ...credentials, password: e.target.value })}
                    placeholder="Enter password"
                  />
                </label>
                <button className="button" onClick={login} disabled={authLoading}>
                  {authLoading ? 'Signing in...' : 'Sign in'}
                </button>
              </div>
            )}

            {authMessage ? <p className="success-text">{authMessage}</p> : null}
            {authError ? <p className="error-text">{authError}</p> : null}
          </div>
        </aside>

        <div className="content-area">
          <div className="topbar">
            <div>
              <p className="eyebrow">Course project frontend</p>
              <p className="topbar-title">Use the menu to manage the system features.</p>
            </div>
            <div className="topbar-user">
              {user ? `Signed in: ${user.username}` : 'Not signed in'}
            </div>
          </div>

          <main className="page-content">
            <Routes>
              <Route path="/" element={<HomePage user={user} />} />
              <Route path="/cars" element={<CarsPage user={user} />} />
              <Route path="/rentals" element={<RentalsPage user={user} />} />
              <Route path="/services" element={<ServicesPage user={user} />} />
              <Route path="/users" element={<UsersPage user={user} />} />
            </Routes>
          </main>
        </div>
      </div>
    </HashRouter>
  );
}

export default App;
