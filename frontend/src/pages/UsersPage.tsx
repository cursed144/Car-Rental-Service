import { useEffect, useState } from 'react';
import { api, getErrorMessage, type AuthUser } from '../api';
import PageHeader from '../components/PageHeader';
import StatusMessage from '../components/StatusMessage';

type UserItem = {
  id: number;
  username: string;
  email: string;
  roleName: string;
};

type Props = {
  user: AuthUser | null;
};

function UsersPage({ user }: Props) {
  const isAdmin = user?.role === 'ADMIN';
  const [users, setUsers] = useState<UserItem[]>([]);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');
  const [form, setForm] = useState({ email: '', username: '', password: '', roleName: 'USER' });

  const fetchUsers = async () => {
    setLoading(true);
    setError('');
    try {
      const response = await api.get<UserItem[]>('/users');
      setUsers(response.data);
    } catch (err) {
      setError(getErrorMessage(err));
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (isAdmin) {
      fetchUsers();
    }
  }, [isAdmin]);

  const createUser = async () => {
    try {
      await api.post('/users', form);
      setMessage('User created successfully.');
      setForm({ email: '', username: '', password: '', roleName: 'USER' });
      fetchUsers();
    } catch (err) {
      setError(getErrorMessage(err));
    }
  };

  if (!isAdmin) {
    return (
      <div className="page-stack">
        <PageHeader title="Users" subtitle="This section is restricted to admins." />
        <div className="panel-card info-card">
          <p className="muted">Sign in as ADMIN to review or create users.</p>
        </div>
      </div>
    );
  }

  return (
    <div className="page-stack">
      <PageHeader title="Users" subtitle="Admin overview of registered users and quick user creation." />
      <StatusMessage kind="success" message={message} />
      <StatusMessage kind="error" message={error} />

      <section className="panel-card">
        <h3>Create user</h3>
        <div className="form-grid two-columns">
          <label>Email<input value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} /></label>
          <label>Username<input value={form.username} onChange={(e) => setForm({ ...form, username: e.target.value })} /></label>
          <label>Password<input type="password" value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} /></label>
          <label>Role
            <select value={form.roleName} onChange={(e) => setForm({ ...form, roleName: e.target.value })}>
              <option value="USER">USER</option>
              <option value="ADMIN">ADMIN</option>
            </select>
          </label>
        </div>
        <button className="button" onClick={createUser}>Create user</button>
      </section>

      <section className="data-table-card">
        {loading ? (
          <p>Loading users...</p>
        ) : (
          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th>Username</th>
                <th>Email</th>
                <th>Role</th>
              </tr>
            </thead>
            <tbody>
              {users.map((item) => (
                <tr key={item.id}>
                  <td>{item.id}</td>
                  <td>{item.username}</td>
                  <td>{item.email}</td>
                  <td>{item.roleName}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </section>
    </div>
  );
}

export default UsersPage;
