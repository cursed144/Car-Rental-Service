function HomePage() {
  return (
    <div>
      <h2>Home</h2>
      <p>Welcome to the Car Rental / Fleet Management System frontend.</p>

      <section>
        <h3>About this project</h3>
        <p>
          This is a simple React + TypeScript interface connected to a Spring Boot backend.
          It is made mainly for demonstrating the main system features required for the course
          project.
        </p>
      </section>

      <section>
        <h3>Main sections</h3>
        <ul>
          <li>Cars - view and manage available cars</li>
          <li>Rentals - create and track rentals</li>
          <li>Services - manage maintenance and service records</li>
          <li>Users - view and manage system users</li>
        </ul>
      </section>

      <section>
        <h3>Notes</h3>
        <p>
          The design is intentionally simple because the main focus of the project is the backend,
          database structure, entity relationships, controllers, and CRUD operations.
        </p>
      </section>
    </div>
  );
}

export default HomePage;