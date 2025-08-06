# Advertisement Platform

A platform for managing and targeting advertisements based on geolocation, biographical data, and user mood.

## Architecture

The application consists of two main components:

1. **Backend**: A Spring Boot application that provides REST APIs for managing advertisements and implementing targeting logic.
2. **Frontend**: A Next.js application that provides a user interface for managing advertisements and targeting.

## Prerequisites

- Docker and Docker Compose
- Java 21 (for local development)
- Node.js (for local development)

## Running with Docker Compose

The easiest way to run the application is using Docker Compose:

```bash
# Build and start the containers
docker-compose up -d

# View logs
docker-compose logs -f

# Stop the containers
docker-compose down
```

The application will be available at:
- Frontend: http://localhost:3000
- Backend API: http://localhost:8080/api
- H2 Database Console: http://localhost:8080/api/h2-console (JDBC URL: jdbc:h2:mem:addb, Username: sa, Password: password)

## Development Setup

### Backend (Spring Boot)

```bash
# Run the backend
./mvnw spring-boot:run
```

### Frontend (Next.js)

```bash
# Navigate to the frontend directory
cd frontend

# Install dependencies
npm install

# Run the development server
npm run dev
```

## IntelliJ IDEA Launchers

The project includes several run configurations for IntelliJ IDEA:

1. **AdPlatformApplication**: Runs the Spring Boot backend application.
2. **Frontend Dev**: Runs the Next.js frontend development server.
3. **Full Stack Application**: Compound configuration that runs both the backend and frontend together.
4. **Docker Compose**: Runs the application using Docker Compose.

To use these launchers:
1. Open the project in IntelliJ IDEA.
2. Select the desired run configuration from the dropdown menu in the top-right corner.
3. Click the green "Run" button or press Shift+F10.

## Features

- Create, read, update, and delete advertisements
- Target advertisements based on:
  - Geolocation (country, region, city, proximity)
  - Biographical data (age, gender, occupation, education, interests)
  - User mood and context (emotional state, time of day, day of week, season)
- Source advertisements from local storage or YouTube

## API Documentation

The backend provides the following REST APIs:

- `GET /api/advertisements`: Get all advertisements
- `GET /api/advertisements/active`: Get all active advertisements
- `GET /api/advertisements/{id}`: Get advertisement by ID
- `POST /api/advertisements`: Create a new advertisement
- `PUT /api/advertisements/{id}`: Update an existing advertisement
- `DELETE /api/advertisements/{id}`: Delete an advertisement
- `GET /api/advertisements/source/{source}`: Get advertisements by source
- `GET /api/advertisements/search?title={title}`: Search advertisements by title
- `POST /api/advertisements/targeted`: Get targeted advertisements based on user context
- `GET /api/advertisements/geo-targeted`: Get advertisements targeted by geolocation
- `GET /api/advertisements/mood-targeted`: Get advertisements targeted by mood
