import { useEffect, useState } from "react";
import axios from "axios";

interface Rental {
  id: number;
  carId: number;
  userId: number;
  startDate: string;
  endDate: string;
  status: string;
}

function RentalsPage() {
  const [rentals, setRentals] = useState<Rental[]>([]);
  const [loading, setLoading] = useState(true);

  const fetchRentals = async () => {
    try {
      const response = await axios.get("http://localhost:8080/api/rentals");
      setRentals(response.data);
    } catch (error) {
      console.error("Error fetching rentals:", error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchRentals();
  }, []);

  return (
    <div>
      <h2>Rentals</h2>

      {loading ? (
        <p>Loading rentals...</p>
      ) : (
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>Car ID</th>
              <th>User ID</th>
              <th>Start Date</th>
              <th>End Date</th>
              <th>Status</th>
            </tr>
          </thead>

          <tbody>
            {rentals.map((rental) => (
              <tr key={rental.id}>
                <td>{rental.id}</td>
                <td>{rental.carId}</td>
                <td>{rental.userId}</td>
                <td>{rental.startDate}</td>
                <td>{rental.endDate}</td>
                <td>{rental.status}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}

export default RentalsPage;