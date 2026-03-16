import { useEffect, useState } from "react";
import axios from "axios";

interface Car {
  id?: number;
  brand: string;
  model: string;
  year: number;
  licensePlate: string;
  available: boolean;
}

const API = "http://localhost:8080/api/cars";

function CarsPage() {
  const [cars, setCars] = useState<Car[]>([]);
  const [loading, setLoading] = useState(true);

  const [newCar, setNewCar] = useState<Car>({
    brand: "",
    model: "",
    year: 2024,
    licensePlate: "",
    available: true
  });

  const fetchCars = async () => {
    try {
      const res = await axios.get(API);
      setCars(res.data);
    } catch (e) {
      console.error("Failed to load cars", e);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchCars();
  }, []);

  const createCar = async () => {
    try {
      await axios.post(API, newCar);
      setNewCar({
        brand: "",
        model: "",
        year: 2024,
        licensePlate: "",
        available: true
      });
      fetchCars();
    } catch (e) {
      console.error("Create failed", e);
    }
  };

  const deleteCar = async (id?: number) => {
    if (!id) return;

    try {
      await axios.delete(`${API}/${id}`);
      fetchCars();
    } catch (e) {
      console.error("Delete failed", e);
    }
  };

  return (
    <div>
      <h2>Cars</h2>

      <h3>Add Car</h3>

      <div style={{ display: "flex", gap: 10, marginBottom: 20 }}>
        <input
          placeholder="Brand"
          value={newCar.brand}
          onChange={(e) =>
            setNewCar({ ...newCar, brand: e.target.value })
          }
        />

        <input
          placeholder="Model"
          value={newCar.model}
          onChange={(e) =>
            setNewCar({ ...newCar, model: e.target.value })
          }
        />

        <input
          type="number"
          placeholder="Year"
          value={newCar.year}
          onChange={(e) =>
            setNewCar({ ...newCar, year: Number(e.target.value) })
          }
        />

        <input
          placeholder="License Plate"
          value={newCar.licensePlate}
          onChange={(e) =>
            setNewCar({ ...newCar, licensePlate: e.target.value })
          }
        />

        <button onClick={createCar}>Add</button>
      </div>

      {loading ? (
        <p>Loading cars...</p>
      ) : (
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>Brand</th>
              <th>Model</th>
              <th>Year</th>
              <th>License Plate</th>
              <th>Available</th>
              <th>Action</th>
            </tr>
          </thead>

          <tbody>
            {cars.map((car) => (
              <tr key={car.id}>
                <td>{car.id}</td>
                <td>{car.brand}</td>
                <td>{car.model}</td>
                <td>{car.year}</td>
                <td>{car.licensePlate}</td>
                <td>{car.available ? "Yes" : "No"}</td>
                <td>
                  <button onClick={() => deleteCar(car.id)}>
                    Delete
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}

export default CarsPage;