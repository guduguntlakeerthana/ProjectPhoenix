# DevFlow AI Deployment Manual

DevFlow AI can be built and deployed locally, inside Docker containers, or mapped to live cloud VM hosting targets.

---

## 1. Production Docker Execution

The cleanest way to boot DevFlow AI is using **Docker Compose**:

### Prerequisites
Verify that Docker Desktop is installed and running on your target machine.

### Commands
1.  **Orchestration Command**:
    From the Project root folder `ProjectPhoenix/`, run:
    ```bash
    docker-compose up --build -d
    ```
2.  **Verify Containment Status**:
    ```bash
    docker-compose ps
    ```
3.  **Check Output Logs**:
    ```bash
    docker-compose logs -f backend
    ```

---

## 2. Ports Configuration

When Docker Compose runs, the following ports are mapped on the local host machine:

*   **Angular Client Web App**: `http://localhost:4200`
*   **Spring Boot REST API**: `http://localhost:9091`
*   **PostgreSQL Engine**: `localhost:5432` (accessible via PgAdmin/DBeaver)

---

## 3. Production Environment Variables Checklist

Before initiating containers on host VM nodes, configure these environment variables in your deployment setup (e.g. inside a `.env` file in the project root):

```properties
DB_PASSWORD=your_secure_db_pass
JWT_SECRET=your_high_entropy_256_bit_jwt_signature_key
SMTP_HOST=smtp.sendgrid.net
SMTP_PORT=587
SMTP_USERNAME=apikey
SMTP_PASSWORD=your_smtp_key_credentials
GEMINI_API_KEY=AIzaSyYourGoogleAIStudioKey
```

---

## 4. Multi-Stage Dockerfile Internals

### Backend Build Flow
- **Stage 1 (Maven Builder)**: Downloads project libraries and packages the source code using `maven:3.9.6-eclipse-temurin-17-alpine`, skipping tests for speed.
- **Stage 2 (Runtime JRE)**: Copies only the compiled `.jar` file to an optimized `eclipse-temurin:17-jre-alpine` runtime image, reducing container weight and security attack surface.

### Frontend Nginx Build Flow
- **Stage 1 (Node compiler)**: Installs packages using clean-install `npm ci` and runs the production compiler target `npm run build`.
- **Stage 2 (Nginx Web Server)**: Copies compiled JS/HTML bundles from `dist/` directly to `/usr/share/nginx/html/` and overrides `/etc/nginx/conf.d/default.conf` to proxy path navigation dynamically to `index.html`.
