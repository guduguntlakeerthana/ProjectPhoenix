# DevFlow AI

DevFlow AI is a production-grade, enterprise-ready developer productivity platform. It empowers software engineering teams with collaborative task tracking, interactive Gantt/Calendar planners, visual Kanban boards, markdown notes, system audit timelines, visual CSS productivity reports, and an AI-powered assistant designed for sprint planning and document drafting.

## Monorepo Directory Structure

```text
ProjectPhoenix/
├── backend/            # Spring Boot 3.x REST API (Java 17, Maven)
│   ├── src/            # Java source files (Entity-Repository-Service-Controller model)
│   └── Dockerfile      # Multi-stage production build configuration
├── frontend/           # Angular 21 Standalone App (TypeScript)
│   ├── src/            # Standalone component hierarchies
│   ├── nginx.conf      # Client server proxy and route routing rules
│   └── Dockerfile      # Multi-stage Angular-Nginx serve configuration
├── docker-compose.yml  # Orchestrator binding Postgres, Backend, and Frontend
└── README.md           # Getting started manual
```

---

## Technical Architecture & Configuration

### Backend Core
- **Framework**: Spring Boot 3.x
- **Platform**: Java 17
- **Database Layer**: Spring Data JPA & PostgreSQL
- **Security Context**: Stateless Spring Security with custom JWT Token Filters
- **Server Port**: `9091`

### Frontend UI
- **Framework**: Angular 21 (Standalone Architecture)
- **Language**: TypeScript
- **Styling**: Harmony-tailored Dark Mode CSS
- **Server Port**: `4300`

---

## Environment Variables Configuration

DevFlow AI isolates credentials using standard environment configurations. Make sure to define the following parameters when launching in production:

| Variable Name | Description | Default / Example Value |
| :--- | :--- | :--- |
| `DB_PASSWORD` | PostgreSQL Database Password | `keerthi12` |
| `JWT_SECRET` | Secret key used for signing authentication tokens | `devflowai-super-secret-key-for-jwt-authentication-2026` |
| `SMTP_HOST` | Outgoing SMTP mail server host | `smtp.gmail.com` |
| `SMTP_PORT` | Outgoing SMTP mail server port | `587` |
| `SMTP_USERNAME`| Username credentials for sending emails | `alerts@devflowai.com` |
| `SMTP_PASSWORD`| Password/App key credentials for emails | `yoursmtppassword` |
| `GEMINI_API_KEY`| API Key for Google Gemini LLM generation | `AIzaSyYourGeminiApiKey` |

---

## Local Development Execution Guide

### 1. Database Initialization
Verify PostgreSQL is active on port `5432` and create a target schema named `devflowai`.

### 2. Launch the Backend API
Navigate to the `backend/` directory, set environment overrides, and trigger compilation:
```bash
cd backend
.\mvnw.cmd spring-boot:run
```
The server will initialize REST controllers on [http://localhost:9091](http://localhost:9091).

### 3. Launch the Client Application
Navigate to `frontend/devflow-frontend/`, install node packages, and start:
```bash
cd frontend/devflow-frontend
npm install
npm run start
```
Open your browser at [http://localhost:4300](http://localhost:4300).

---

## Docker Container Deployment

DevFlow AI can be fully orchestrated using Docker Compose. Make sure Docker Desktop is active on your machine:

1. **Build and Spin Up Containers**:
   ```bash
   docker-compose up --build -d
   ```
2. **Access Points**:
   - Angular UI Frontend: [http://localhost:4300](http://localhost:4300)
   - Spring Boot REST API: [http://localhost:9091](http://localhost:9091)
   - PostgreSQL Database: `localhost:5432` (database `devflowai`)

---

## Core Application Modules

1. **Authentication & Identity**: User registration and login protected by JWT filters and BCrypt hashing.
2. **Project Workspace**: Collaborative repositories with invitation modals and document attachment panels.
3. **Task & Sprint Planners**: Detailed task descriptions, priority triggers, and file download tools.
4. **Kanban Boards**: Status-focused columns (Todo, In Progress, Review, Completed) supporting rapid dropdown status updates.
5. **Interactive Calendars**: Monthly calendar grids highlighting project limits, due items, and sidebar lists.
6. **Activity Log Auditing**: Global and user-specific audit tables tracking create, edit, delete, and login actions.
7. **Productivity Visualizations**: conic-gradient progress circles and horizontal bar progress summaries.
8. **AI planning Assistant**: Generates sprint planning checklists, drafts specs, and supports chat threads.
