import { useEffect, useMemo, useState } from 'react';
import { api, getErrorMessage, type AuthUser } from '../api';
import PageHeader from '../components/PageHeader';
import StatusMessage from '../components/StatusMessage';

type CarImage = {
  id: number;
  fileName: string;
  fileType: string;
};

type Car = {
  id: number;
  brand: string;
  model: string;
  year: number;
  fuelType: string;
  pricePerDay: number | string;
  description: string;
  mileage: number;
  color: string;
  seats: number;
  transmission: string;
  serviceIds?: number[];
  images: CarImage[];
};

type ServiceItem = {
  id: number;
  name: string;
  description?: string;
  price?: number | string;
};

type Props = {
  user: AuthUser | null;
};

const emptyCar = {
  brand: '',
  model: '',
  year: new Date().getFullYear(),
  fuelType: 'PETROL',
  pricePerDay: 50,
  description: '',
  mileage: 0,
  color: '',
  seats: 4,
  transmission: 'Automatic',
};

function CarsPage({ user }: Props) {
  const isAdmin = user?.role === 'ADMIN';
  const [cars, setCars] = useState<Car[]>([]);
  const [services, setServices] = useState<ServiceItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');
  const [search, setSearch] = useState('');
  const [newCar, setNewCar] = useState(emptyCar);
  const [imageForm, setImageForm] = useState({ carId: '', fileName: '', fileType: 'image/png' });
  const [serviceForm, setServiceForm] = useState({ carId: '', serviceId: '' });

  const fetchData = async () => {
    setLoading(true);
    setError('');
    try {
      const [carsResponse, servicesResponse] = await Promise.all([
        api.get<Car[]>('/cars'),
        api.get<ServiceItem[]>('/services'),
      ]);
      setCars(carsResponse.data);
      setServices(servicesResponse.data);
    } catch (err) {
      setError(getErrorMessage(err));
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const servicesById = useMemo(() => {
    return new Map(services.map((service) => [service.id, service]));
  }, [services]);

  const filteredCars = useMemo(() => {
    const query = search.trim().toLowerCase();
    if (!query) return cars;

    return cars.filter((car) => {
      const joinedServices = (car.serviceIds ?? [])
        .map((id) => servicesById.get(id))
        .filter(Boolean)
        .map((service) => `${service?.name ?? ''} ${service?.description ?? ''}`)
        .join(' ')
        .toLowerCase();

      return [car.brand, car.model, car.fuelType, car.color, String(car.year), joinedServices].some((value) =>
        value.toLowerCase().includes(query)
      );
    });
  }, [cars, search, servicesById]);

  const createCar = async () => {
    setMessage('');
    setError('');
    try {
      await api.post('/cars', newCar);
      setMessage('Car added successfully.');
      setNewCar(emptyCar);
      await fetchData();
    } catch (err) {
      setError(getErrorMessage(err));
    }
  };

  const deleteCar = async (id: number) => {
    setMessage('');
    setError('');
    try {
      await api.delete(`/cars/${id}`);
      setMessage('Car deleted.');
      await fetchData();
    } catch (err) {
      setError(getErrorMessage(err));
    }
  };

  const addImage = async () => {
    setMessage('');
    setError('');

    try {
      await api.post(`/cars/${imageForm.carId}/images`, {
        fileName: imageForm.fileName || 'placeholder.png',
        fileType: imageForm.fileType || 'image/png',
        base64Content:
          'iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8/x8AAusB9Wn8m0sAAAAASUVORK5CYII=',
      });

      setMessage('Image placeholder added to the selected car.');
      setImageForm({ carId: '', fileName: '', fileType: 'image/png' });
      await fetchData();
    } catch (err) {
      setError(getErrorMessage(err));
    }
  };

  const attachService = async () => {
    setMessage('');
    setError('');
    try {
      await api.post(`/cars/${serviceForm.carId}/services/${serviceForm.serviceId}`);
      setMessage('Service attached to car.');
      setServiceForm({ carId: '', serviceId: '' });
      await fetchData();
    } catch (err) {
      setError(getErrorMessage(err));
    }
  };

  const detachService = async (carId: number, serviceId: number) => {
    setMessage('');
    setError('');
    try {
      await api.delete(`/cars/${carId}/services/${serviceId}`);
      setMessage('Service detached from car.');
      await fetchData();
    } catch (err) {
      setError(getErrorMessage(err));
    }
  };

  return (
    <div className="page-stack">
      <PageHeader
        title="Cars"
        subtitle="Browse the fleet, search quickly, and manage cars when signed in as admin."
        actions={
          <input
            className="search-input"
            placeholder="Search cars, fuel type, or services..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />
        }
      />

      <StatusMessage kind="success" message={message} />
      <StatusMessage kind="error" message={error} />

      {isAdmin ? (
        <section className="card-grid two-columns">
          <article className="panel-card">
            <h3>Add new car</h3>
            <div className="form-grid two-columns">
              <label>
                Brand
                <input value={newCar.brand} onChange={(e) => setNewCar({ ...newCar, brand: e.target.value })} />
              </label>
              <label>
                Model
                <input value={newCar.model} onChange={(e) => setNewCar({ ...newCar, model: e.target.value })} />
              </label>
              <label>
                Year
                <input type="number" value={newCar.year} onChange={(e) => setNewCar({ ...newCar, year: Number(e.target.value) })} />
              </label>
              <label>
                Fuel type
                <select value={newCar.fuelType} onChange={(e) => setNewCar({ ...newCar, fuelType: e.target.value })}>
                  <option value="PETROL">PETROL</option>
                  <option value="DIESEL">DIESEL</option>
                  <option value="ELECTRIC">ELECTRIC</option>
                  <option value="HYBRID">HYBRID</option>
                </select>
              </label>
              <label>
                Price per day
                <input type="number" value={newCar.pricePerDay} onChange={(e) => setNewCar({ ...newCar, pricePerDay: Number(e.target.value) })} />
              </label>
              <label>
                Mileage
                <input type="number" value={newCar.mileage} onChange={(e) => setNewCar({ ...newCar, mileage: Number(e.target.value) })} />
              </label>
              <label>
                Color
                <input value={newCar.color} onChange={(e) => setNewCar({ ...newCar, color: e.target.value })} />
              </label>
              <label>
                Seats
                <input type="number" value={newCar.seats} onChange={(e) => setNewCar({ ...newCar, seats: Number(e.target.value) })} />
              </label>
              <label>
                Transmission
                <input value={newCar.transmission} onChange={(e) => setNewCar({ ...newCar, transmission: e.target.value })} />
              </label>
              <label className="full-width">
                Description
                <textarea rows={3} value={newCar.description} onChange={(e) => setNewCar({ ...newCar, description: e.target.value })} />
              </label>
            </div>
            <button className="button" onClick={createCar}>Add car</button>
          </article>

          <article className="panel-card">
            <h3>Quick admin tools</h3>
            <div className="form-grid compact">
              <label>
                Car ID for image
                <input type="number" value={imageForm.carId} onChange={(e) => setImageForm({ ...imageForm, carId: e.target.value })} />
              </label>
              <label>
                Image file name
                <input value={imageForm.fileName} onChange={(e) => setImageForm({ ...imageForm, fileName: e.target.value })} placeholder="example.png" />
              </label>
              <label>
                Image file type
                <input value={imageForm.fileType} onChange={(e) => setImageForm({ ...imageForm, fileType: e.target.value })} />
              </label>
              <button className="button secondary" onClick={addImage}>Attach sample image</button>
            </div>

            <div className="form-grid compact top-gap">
              <label>
                Car ID
                <input type="number" value={serviceForm.carId} onChange={(e) => setServiceForm({ ...serviceForm, carId: e.target.value })} />
              </label>
              <label>
                Service
                <select value={serviceForm.serviceId} onChange={(e) => setServiceForm({ ...serviceForm, serviceId: e.target.value })}>
                  <option value="">Select service</option>
                  {services.map((service) => (
                    <option key={service.id} value={service.id}>
                      {service.name}
                    </option>
                  ))}
                </select>
              </label>
              <button className="button secondary" onClick={attachService}>Attach service to car</button>
            </div>
          </article>
        </section>
      ) : (
        <div className="panel-card info-card">
          <p className="muted">You can browse cars as a regular user. Admin tools appear after signing in as ADMIN.</p>
        </div>
      )}

      <section className="card-grid two-columns">
        {loading ? (
          <div className="panel-card">Loading cars...</div>
        ) : filteredCars.length === 0 ? (
          <div className="panel-card">No cars match your search.</div>
        ) : (
          filteredCars.map((car) => {
            const resolvedServices = (car.serviceIds ?? [])
              .map((id) => servicesById.get(id))
              .filter((service): service is ServiceItem => Boolean(service));

            return (
              <article className="panel-card car-card" key={car.id}>
                <div className="card-title-row">
                  <div>
                    <h3>{car.brand} {car.model}</h3>
                    <p className="muted">Car #{car.id} • {car.year} • {car.fuelType}</p>
                  </div>
                  <span className="price-badge">${car.pricePerDay}/day</span>
                </div>

                <p className="muted">{car.description || 'No description provided.'}</p>

                <div className="detail-grid">
                  <div><strong>Mileage:</strong> {car.mileage}</div>
                  <div><strong>Color:</strong> {car.color}</div>
                  <div><strong>Seats:</strong> {car.seats}</div>
                  <div><strong>Transmission:</strong> {car.transmission}</div>
                  <div><strong>Images:</strong> {car.images?.length ?? 0}</div>
                </div>

                <div style={{ marginTop: '1rem' }}>
                  <strong>Services:</strong>
                  {resolvedServices.length > 0 ? (
                    <div style={{ marginTop: '0.75rem', display: 'grid', gap: '0.75rem' }}>
                      {resolvedServices.map((service) => (
                        <div key={service.id} className="panel-card" style={{ padding: '0.75rem' }}>
                          <div className="card-title-row">
                            <div>
                              <strong>{service.name}</strong>
                              <div className="muted small">{service.description || 'No description provided.'}</div>
                            </div>
                            {service.price != null ? (
                              <span className="price-badge">${service.price}</span>
                            ) : null}
                          </div>
                          {isAdmin ? (
                            <div style={{ marginTop: '0.75rem' }}>
                              <button
                                className="button secondary"
                                onClick={() => detachService(car.id, service.id)}
                              >
                                Detach service
                              </button>
                            </div>
                          ) : null}
                        </div>
                      ))}
                    </div>
                  ) : (
                    <div style={{ marginTop: '0.5rem' }} className="muted">
                      No services attached
                    </div>
                  )}
                </div>

                {car.images?.length ? (
                  <div className="chips-row" style={{ marginTop: '1rem' }}>
                    {car.images.map((image) => (
                      <span key={image.id} className="chip">{image.fileName}</span>
                    ))}
                  </div>
                ) : null}

                {isAdmin ? (
                  <button className="button danger" onClick={() => deleteCar(car.id)}>
                    Delete car
                  </button>
                ) : null}
              </article>
            );
          })
        )}
      </section>
    </div>
  );
}

export default CarsPage;