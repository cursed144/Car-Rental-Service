import { BrowserRouter, NavLink, Route, Routes } from 'react-router-dom';
import HomePage from './pages/HomePage';
import CarsPage from './pages/CarsPage';
import RentalsPage from './pages/RentalsPage';
import ServicesPage from './pages/ServicesPage';
import UsersPage from './pages/UsersPage';
import './App.css';

function App() {
  return (
    <BrowserRouter>
      <div className="app">
        <header className="header">
          <h1>Car Rental System</h1>
          <p>Simple React + TypeScript frontend for the Spring project</p>
        </header>

        <nav className="navbar">
          <NavLink to="/" end className={({ isActive }) => (isActive ? 'nav-link active' : 'nav-link')}>
            Home
          </NavLink>

          <NavLink to="/cars" className={({ isActive }) => (isActive ? 'nav-link active' : 'nav-link')}>
            Cars
          </NavLink>

          <NavLink
            to="/rentals"
            className={({ isActive }) => (isActive ? 'nav-link active' : 'nav-link')}
          >
            Rentals
          </NavLink>

          <NavLink
            to="/services"
            className={({ isActive }) => (isActive ? 'nav-link active' : 'nav-link')}
          >
            Services
          </NavLink>

          <NavLink to="/users" className={({ isActive }) => (isActive ? 'nav-link active' : 'nav-link')}>
            Users
          </NavLink>
        </nav>

        <main className="main-content">
          <Routes>
            <Route path="/" element={<HomePage />} />
            <Route path="/cars" element={<CarsPage />} />
            <Route path="/rentals" element={<RentalsPage />} />
            <Route path="/services" element={<ServicesPage />} />
            <Route path="/users" element={<UsersPage />} />
          </Routes>
        </main>
      </div>
    </BrowserRouter>
  );
}

export default App;