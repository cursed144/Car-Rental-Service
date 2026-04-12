# Car Rental / Fleet Management System

## Overview

This project is a Car Rental / Fleet Management System built with Spring
Boot and React + TypeScript.

The system provides a REST API and a frontend interface for managing: -
cars - rentals - services - users

There are two roles: 
- USER 
-- can rent cars and view their rentals 
- ADMIN -- manages cars, rentals, services, and users

------------------------------------------------------------------------

## Technologies Used

Backend: 
- Spring Boot 
- Spring Data JPA (Hibernate) 
- Spring Security (JWT)
- Liquibase 
- OpenAPI (Swagger) 
- MapStruct 
- Lombok 
- Actuator
- DevTools

Frontend: 
- React 
- TypeScript 
- Vite 
- Axios

Build Tool: - Gradle

------------------------------------------------------------------------

## Database Design

Tables: - users - roles - cars - car_details - rentals - services -
car_images - car_service

Relationships:
- User → Rentals (One-to-Many)
- Car → Rentals (One-to-Many) 
- Car → Services (Many-to-Many)
- Car → Images (One-to-Many)
- Car → CarDetails (One-to-One)

------------------------------------------------------------------------

## Features

User: 
- browse cars 
- create rental 
- view rentals 
- cancel rental

Admin: 
- CRUD cars 
- CRUD services 
- manage rentals 
- manage users 
- attach/detach services - add car images

------------------------------------------------------------------------

## Security

-   JWT authentication
-   Role-based access (ADMIN / USER)
-   Refresh tokens
-   Rate limiting

------------------------------------------------------------------------

## Development and Production Profiles

Dev:
- H2 console enabled 
- Swagger enabled 
- SQL logging enabled
- CSRF disabled

Prod: 
- H2 disabled
- Swagger disabled 
- SQL logging disabled
- CSRF enabled

------------------------------------------------------------------------

## Running the Project

### Backend

Run with:

    ./gradlew bootRun

Dev profile:

    ./gradlew bootRun --args='--spring.profiles.active=dev'

Prod profile:

    ./gradlew bootRun --args='--spring.profiles.active=prod'

Runs on: http://localhost:8080

------------------------------------------------------------------------

### Frontend

    cd frontend
    npm install
    npm run dev

Runs on: http://localhost:5173

------------------------------------------------------------------------

## REST API Overview

Auth: - /api/auth/login - /api/auth/register - /api/auth/refresh -
/api/auth/logout

Cars: - CRUD - attach/detach services

Images: - add/list/delete

Rentals: - CRUD

Services: - CRUD

Users: - CRUD

------------------------------------------------------------------------

## Frontend Pages

-   Home
-   Cars
-   Rentals
-   Services
-   Users

------------------------------------------------------------------------

## Notes

-   Liquibase handles DB schema
-   DTO + MapStruct used
-   JWT for authentication
-   Dev/Prod separation implemented

------------------------------------------------------------------------

## Screenshots

1. Home page 
2. Cars page 
3. Rentals page 
4. Services page 
5. Users page 
6. Not Logged in view 
7. Logged in view
8. Car examples
9. Service examples
10. Rented car
