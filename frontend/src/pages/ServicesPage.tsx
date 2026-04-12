import { useEffect, useState } from 'react';
import { api, getErrorMessage, type AuthUser } from '../api';
import PageHeader from '../components/PageHeader';
import StatusMessage from '../components/StatusMessage';

type ServiceItem = {
  id: number;
  name: string;
  description: string;
  price: number | string;
};

type Props = {
  user: AuthUser | null;
};

function ServicesPage({ user }: Props) {
  const isAdmin = user?.role === 'ADMIN';
  const [services, setServices] = useState<ServiceItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');
  const [form, setForm] = useState({ name: '', description: '', price: 50 });

  const fetchServices = async () => {
    setLoading(true);
    setError('');
    try {
      const response = await api.get<ServiceItem[]>('/services');
      setServices(response.data);
    } catch (err) {
      setError(getErrorMessage(err));
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchServices();
  }, []);

  const createService = async () => {
    setMessage('');
    setError('');
    try {
      await api.post('/services', form);
      setMessage('Service added successfully.');
      setForm({ name: '', description: '', price: 50 });
      await fetchServices();
    } catch (err) {
      setError(getErrorMessage(err));
    }
  };

  const deleteService = async (id: number) => {
    setMessage('');
    setError('');
    try {
      await api.delete(`/services/${id}`);
      setMessage('Service removed.');
      await fetchServices();
    } catch (err) {
      setError(getErrorMessage(err));
    }
  };

  return (
    <div className="page-stack">
      <PageHeader title="Services" subtitle="Manage maintenance and repair service records." />
      <StatusMessage kind="success" message={message} />
      <StatusMessage kind="error" message={error} />

      {isAdmin ? (
        <section className="panel-card">
          <h3>Add service</h3>
          <div className="form-grid two-columns">
            <label>
              Service name
              <input value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} />
            </label>
            <label>
              Price
              <input type="number" value={form.price} onChange={(e) => setForm({ ...form, price: Number(e.target.value) })} />
            </label>
            <label className="full-width">
              Description
              <textarea rows={3} value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} />
            </label>
          </div>
          <button className="button" onClick={createService}>Add service</button>
        </section>
      ) : (
        <div className="panel-card info-card">
          <p className="muted">Service creation is available to admins. Regular users can still review the service list.</p>
        </div>
      )}

      <section className="card-grid two-columns">
        {loading ? (
          <div className="panel-card">Loading services...</div>
        ) : services.length === 0 ? (
          <div className="panel-card">No services found.</div>
        ) : (
          services.map((service) => (
            <article className="panel-card" key={service.id}>
              <div className="card-title-row">
                <h3>{service.name}</h3>
                <span className="price-badge">${service.price}</span>
              </div>
              <p className="muted">{service.description || 'No description provided.'}</p>
              <p className="muted small top-gap">Service ID: {service.id}</p>
              {isAdmin ? (
                <div className="top-gap">
                  <button className="button danger" onClick={() => deleteService(service.id)}>
                    Delete service
                  </button>
                </div>
              ) : null}
            </article>
          ))
        )}
      </section>
    </div>
  );
}

export default ServicesPage;