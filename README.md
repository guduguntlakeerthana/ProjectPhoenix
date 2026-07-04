# DevFlow AI

DevFlow AI is an AI-powered developer productivity platform designed to help software engineers and teams manage projects, track tasks, write documentation, take markdown notes, integrate Git repositories, monitor dashboard analytics, and collaborate using AI assistance.

## Directory Structure

This project is organized as a unified full-stack monorepo:

```text
ProjectPhoenix/
├── backend/            # Spring Boot 3.x REST API (Java 17, Maven)
├── frontend/           # Angular 21 Standalone Application (TypeScript)
├── docs/               # Architecture diagrams and API documentation
├── database/           # PostgreSQL configuration and migration plans
├── deployment/         # Docker Compose and deployment manifests
├── assets/             # Images and branding assets
├── ai/                 # AI service models and helper modules
└── ui/                 # Design assets and style references
```

## Tech Stack & Configuration

### Backend
- **Framework**: Spring Boot 3.x
- **Language**: Java 17
- **Database**: PostgreSQL (Spring Data JPA)
- **Security**: Spring Security with stateless JWT Authentication
- **Port**: `9091`

### Frontend
- **Framework**: Angular 21 (Standalone Components)
- **Language**: TypeScript
- **Styling**: Vanilla CSS (Developer dark-mode design system)
- **Port**: `4300`

### Database
- **Engine**: PostgreSQL
- **Port**: `5432`
- **Database Name**: `devflowai`

---

## Setup & Running the Workspace

### 1. Database Configuration
Ensure PostgreSQL is running locally on port `5432` and create a database named `devflowai`. Update the database credentials in `backend/src/main/resources/application.properties` if needed:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/devflowai
spring.datasource.username=postgres
spring.datasource.password=yourpassword
```

### 2. Run the Backend REST API
Navigate to the `backend/` directory and run using Maven:
```bash
mvn spring-boot:run
```
The REST API will launch on [http://localhost:9091](http://localhost:9091).

### 3. Run the Frontend App
Navigate to the `frontend/devflow-frontend/` directory, install packages, and start the development server:
```bash
npm install
npm start
```
The client app will launch on [http://localhost:4300](http://localhost:4300).
