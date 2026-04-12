import type { AuthUser } from '../api';
import PageHeader from '../components/PageHeader';

type Props = {
  user: AuthUser | null;
};

function HomePage({ user }: Props) {
  return (
    <div className="page-stack">
      <PageHeader
        title="Dashboard"
        subtitle="Simple, cleaner navigation for the required project sections."
      />

      <section className="hero-card">
        <div>
          <p className="eyebrow">Fleet management overview</p>
          <h3 className="hero-title">Everything important is one click away.</h3>
          <p className="muted">
            Review cars, create rentals, track service records, and manage users from a more usable layout.
          </p>
        </div>
        <div className="stats-grid">
          <div className="stat-card">
            <span className="stat-value">5</span>
            <span className="stat-label">Main pages</span>
          </div>
          <div className="stat-card">
            <span className="stat-value">CRUD</span>
            <span className="stat-label">Core actions</span>
          </div>
          <div className="stat-card">
            <span className="stat-value">JWT</span>
            <span className="stat-label">Secured API</span>
          </div>
        </div>
      </section>

      <section className="card-grid two-columns">
        <article className="panel-card">
          <h3>How to use it</h3>
          <ol className="feature-list ordered">
            <li>Sign in with an existing account.</li>
            <li>Open Cars to review fleet data and add cars if you are an admin.</li>
            <li>Open Rentals to create a rental and track its status.</li>
            <li>Use Services to manage maintenance entries.</li>
            <li>Admins can review all users in the Users page.</li>
          </ol>
        </article>

        <article className="panel-card">
          <h3>Current session</h3>
          {user ? (
            <div className="detail-list">
              <div><strong>Username:</strong> {user.username}</div>
              <div><strong>Role:</strong> {user.role}</div>
              <div><strong>User ID:</strong> {user.userId}</div>
            </div>
          ) : (
            <p className="muted">You are not logged in yet. Use the login card on the left.</p>
          )}
        </article>
      </section>

      <section className="card-grid three-columns">
        <article className="panel-card compact-card">
          <h3>Cars</h3>
          <p className="muted">Search by brand, model, fuel type, or year. Admins can add cars and attach images.</p>
        </article>
        <article className="panel-card compact-card">
          <h3>Rentals</h3>
          <p className="muted">Create rentals with dates and status, then track pricing and progress.</p>
        </article>
        <article className="panel-card compact-card">
          <h3>Services & Users</h3>
          <p className="muted">Keep service records organized and review registered users when logged in as admin.</p>
        </article>
      </section>
    </div>
  );
}

export default HomePage;
