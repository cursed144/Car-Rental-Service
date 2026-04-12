import { useEffect, useMemo, useState } from 'react';
import { api, getErrorMessage, type AuthUser } from '../api';
import PageHeader from '../components/PageHeader';
import StatusMessage from '../components/StatusMessage';

type Rental = {
  id: number;
  carId: number;
  userId: number;
  startDate: string;
  endDate: string;
  totalPrice: number | string;
  status: string;
};

type Car = {
  id: number;
  brand: string;
  model: string;
};

type Props = {
  user: AuthUser | null;
};

function RentalsPage({ user }: Props) {
  const isAdmin = user?.role === 'ADMIN';
  const [rentals, setRentals] = useState<Rental[]>([]);
  const [cars, setCars] = useState<Car[]>([]);
  const [loading, setLoading] = useState(true);
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');
  const [statusFilter, setStatusFilter] = useState('ALL');
  const [form, setForm] = useState({
    userId: user?.userId ?? 1,
    carId: 1,
    startDate: '',
    endDate: '',
    status: 'PENDING',
  });

  useEffect(() => {
    if (user?.userId) {
      setForm((current) => ({ ...current, userId: isAdmin ? current.userId : user.userId }));
    }
  }, [user, isAdmin]);

  const fetchData = async () => {
    setLoading(true);
    setError('');
    try {
      const [rentalsResponse, carsResponse] = await Promise.all([
        api.get<Rental[]>('/rentals'),
        api.get<Car[]>('/cars'),
      ]);
      setRentals(rentalsResponse.data);
      setCars(carsResponse.data);
    } catch (err) {
      setError(getErrorMessage(err));
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const visibleRentals = useMemo(() => {
    let list = rentals;

    if (!isAdmin && user?.userId) {
      list = list.filter((rental) => rental.userId === user.userId);
    }

    if (statusFilter !== 'ALL') {
      list = list.filter((rental) => rental.status === statusFilter);
    }

    return list;
  }, [rentals, statusFilter, isAdmin, user]);

  const createRental = async () => {
    setMessage('');
    setError('');
    try {
      await api.post('/rentals', form);
      setMessage('Rental created successfully.');
      setForm((current) => ({ ...current, startDate: '', endDate: '', status: 'PENDING' }));
      await fetchData();
    } catch (err) {
      setError(getErrorMessage(err));
    }
  };

  const deleteRental = async (id: number) => {
    setMessage('');
    setError('');
    try {
      await api.delete(`/rentals/${id}`);
      setMessage('Rental removed.');
      await fetchData();
    } catch (err) {
      setError(getErrorMessage(err));
    }
  };

  const cancelRental = async (rental: Rental) => {
    setMessage('');
    setError('');
    try {
      await api.put(`/rentals/${rental.id}`, {
        userId: rental.userId,
        carId: rental.carId,
        startDate: rental.startDate,
        endDate: rental.endDate,
        status: 'CANCELLED',
      });
      setMessage('Rental cancelled.');
      await fetchData();
    } catch (err) {
      setError(getErrorMessage(err));
    }
  };

  const getCarLabel = (carId: number) => {
    const car = cars.find((item) => item.id === carId);
    return car ? `${car.brand} ${car.model}` : `Car #${carId}`;
  };

  const canUserCancel = (rental: Rental) => {
    return !isAdmin && user?.userId === rental.userId && rental.status !== 'CANCELLED' && rental.status !== 'COMPLETED';
  };

  return (
    <div className="page-stack">
      <PageHeader
        title="Rentals"
        subtitle="Create rentals, filter by status, and manage existing rental records."
        actions={
          <select className="search-input" value={statusFilter} onChange={(e) => setStatusFilter(e.target.value)}>
            <option value="ALL">All statuses</option>
            <option value="PENDING">PENDING</option>
            <option value="ACTIVE">ACTIVE</option>
            <option value="COMPLETED">COMPLETED</option>
            <option value="CANCELLED">CANCELLED</option>
          </select>
        }
      />

      <StatusMessage kind="success" message={message} />
      <StatusMessage kind="error" message={error} />

      <section className="panel-card">
        <h3>Create rental</h3>
        <div className="form-grid two-columns">
          <label>
            User ID
            <input
              type="number"
              value={form.userId}
              disabled={!isAdmin}
              onChange={(e) => setForm({ ...form, userId: Number(e.target.value) })}
            />
          </label>
          <label>
            Car
            <select value={form.carId} onChange={(e) => setForm({ ...form, carId: Number(e.target.value) })}>
              {cars.map((car) => (
                <option key={car.id} value={car.id}>{car.id} - {car.brand} {car.model}</option>
              ))}
            </select>
          </label>
          <label>
            Start date
            <input type="date" value={form.startDate} onChange={(e) => setForm({ ...form, startDate: e.target.value })} />
          </label>
          <label>
            End date
            <input type="date" value={form.endDate} onChange={(e) => setForm({ ...form, endDate: e.target.value })} />
          </label>
          <label>
            Status
            <select value={form.status} onChange={(e) => setForm({ ...form, status: e.target.value })}>
              <option value="PENDING">PENDING</option>
              <option value="ACTIVE">ACTIVE</option>
              <option value="COMPLETED">COMPLETED</option>
              <option value="CANCELLED">CANCELLED</option>
            </select>
          </label>
        </div>
        <button className="button" onClick={createRental}>Create rental</button>
      </section>

      <section className="data-table-card">
        {loading ? (
          <p>Loading rentals...</p>
        ) : visibleRentals.length === 0 ? (
          <p>No rentals found.</p>
        ) : (
          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th>User</th>
                <th>Car</th>
                <th>Start</th>
                <th>End</th>
                <th>Total price</th>
                <th>Status</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {visibleRentals.map((rental) => (
                <tr key={rental.id}>
                  <td>{rental.id}</td>
                  <td>{rental.userId}</td>
                  <td>{getCarLabel(rental.carId)}</td>
                  <td>{rental.startDate}</td>
                  <td>{rental.endDate}</td>
                  <td>{rental.totalPrice}</td>
                  <td><span className={`status-pill ${rental.status.toLowerCase()}`}>{rental.status}</span></td>
                  <td>
                    <div style={{ display: 'flex', gap: '0.5rem', flexWrap: 'wrap' }}>
                      {canUserCancel(rental) ? (
                        <button className="button secondary" onClick={() => cancelRental(rental)}>
                          Cancel
                        </button>
                      ) : null}
                      {isAdmin ? (
                        <button className="button danger" onClick={() => deleteRental(rental.id)}>
                          Delete
                        </button>
                      ) : null}
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </section>
    </div>
  );
}

export default RentalsPage;