import { useEffect, useState } from "react";
import axios from "axios";

interface Service {
  id: number;
  carId: number;
  description: string;
  serviceDate: string;
  cost: number;
}

function ServicesPage() {
  const [services, setServices] = useState<Service[]>([]);
  const [loading, setLoading] = useState(true);

  const fetchServices = async () => {
    try {
      const response = await axios.get("http://localhost:8080/api/services");
      setServices(response.data);
    } catch (error) {
      console.error("Error fetching services:", error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchServices();
  }, []);

  return (
    <div>
      <h2>Services</h2>

      {loading ? (
        <p>Loading services...</p>
      ) : (
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>Car ID</th>
              <th>Description</th>
              <th>Service Date</th>
              <th>Cost</th>
            </tr>
          </thead>

          <tbody>
            {services.map((service) => (
              <tr key={service.id}>
                <td>{service.id}</td>
                <td>{service.carId}</td>
                <td>{service.description}</td>
                <td>{service.serviceDate}</td>
                <td>{service.cost}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}

export default ServicesPage;