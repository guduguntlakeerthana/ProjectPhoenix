# DevFlow AI REST API Reference Manual

Base Endpoint: `http://localhost:9091`

---

## 1. Authentication Endpoints

### Register User
*   **POST** `/api/users/register`
*   **Payload**:
    ```json
    {
      "fullName": "Keerthana Guduguntla",
      "email": "keerthana@devflowai.com",
      "password": "securePassword12"
    }
    ```
*   **Response (200 OK)**:
    ```json
    {
      "token": "jwt_token_string",
      "email": "keerthana@devflowai.com",
      "fullName": "Keerthana Guduguntla",
      "role": "USER"
    }
    ```

### Login User
*   **POST** `/api/auth/login`
*   **Payload**:
    ```json
    {
      "email": "keerthana@devflowai.com",
      "password": "securePassword12"
    }
    ```
*   **Response (200 OK)**: (Same structure as registration response)

---

## 2. Project Workspace API (Authenticated)

### Get Paginated Projects
*   **GET** `/api/projects?page=0&size=6&sortBy=createdAt&direction=desc&search=foo&status=IN_PROGRESS`
*   **Headers**: `Authorization: Bearer <token>`
*   **Response (200 OK)**:
    ```json
    {
      "content": [
        {
          "id": 1,
          "title": "Project Phoenix",
          "description": "DevFlow AI V1 Workspace",
          "techStack": "Angular, Spring Boot",
          "status": "IN_PROGRESS",
          "startDate": "2026-07-01",
          "endDate": "2026-07-30",
          "githubLink": "",
          "liveDemoLink": "",
          "createdAt": "2026-07-14T12:00:00",
          "updatedAt": "2026-07-14T12:00:00"
        }
      ],
      "totalElements": 1,
      "totalPages": 1
    }
    ```

### Create Project
*   **POST** `/api/projects`
*   **Payload**:
    ```json
    {
      "title": "New Platform",
      "description": "SaaS Platform",
      "techStack": "React, NestJS",
      "status": "PENDING",
      "startDate": "2026-08-01",
      "endDate": "2026-12-31"
    }
    ```
*   **Response (200 OK)**: Created project response object.

---

## 3. Tasks & Milestone API (Authenticated)

### Get Tasks
*   **GET** `/api/tasks`
*   **Response (200 OK)**: List of user's task details.

### Update Task Status (Kanban integration)
*   **PUT** `/api/tasks/{id}`
*   **Payload**:
    ```json
    {
      "title": "Setup Docker",
      "description": "Build images",
      "status": "REVIEW",
      "priority": "HIGH",
      "dueDate": "2026-07-20",
      "progress": 50,
      "projectId": 1
    }
    ```

---

## 4. Admin Consolidation API (Admin Only)

### Get User Directory
*   **GET** `/api/admin/users`
*   **Headers**: `Authorization: Bearer <AdminToken>`
*   **Response (200 OK)**: User detail records containing IDs, roles, and emails.

### Update User Role
*   **PUT** `/api/admin/users/{userId}/role?role=ADMIN`

---

## 5. Gemini AI Gateway (Authenticated)

### Ask Assistant
*   **POST** `/api/ai/chat`
*   **Payload**:
    ```json
    {
      "prompt": "Create a sprint breakdown for Project Phoenix"
    }
    ```
*   **Response (200 OK)**:
    ```json
    {
      "response": "### Sprint 1: ...",
      "simulated": true
    }
    ```
