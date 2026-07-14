# DevFlow AI Database Design

DevFlow AI stores structured relation records using PostgreSQL. The relational mappings are designed for high integrity, isolation, and quick query lookups.

---

## 1. Entity Relationship Schema

Below is the database table configuration model:

```mermaid
erDiagram
    users ||--o{ projects : owns
    users ||--o{ attachments : uploads
    users ||--o{ notifications : receives
    users ||--o{ audit_logs : triggers
    projects ||--o{ tasks : contains
    projects ||--o{ project_members : has
    projects ||--o{ notes : contains
    projects ||--o{ docs : contains
    projects ||--o{ attachments : has
    tasks ||--o{ attachments : has
    
    users {
        bigint id PK
        varchar email UK
        varchar password
        varchar full_name
        varchar role
        timestamp created_at
        timestamp updated_at
    }
    
    projects {
        bigint id PK
        varchar title
        text description
        varchar tech_stack
        varchar status
        date start_date
        date end_date
        varchar github_link
        varchar live_demo_link
        timestamp created_at
        timestamp updated_at
        bigint user_id FK
    }

    tasks {
        bigint id PK
        varchar title
        text description
        varchar status
        varchar priority
        date due_date
        integer progress
        timestamp created_at
        timestamp updated_at
        bigint project_id FK
        bigint user_id FK
    }

    attachments {
        bigint id PK
        varchar file_name
        varchar file_type
        bigint file_size
        varchar file_path
        timestamp created_at
        bigint project_id FK
        bigint task_id FK
        bigint user_id FK
    }

    project_members {
        bigint id PK
        varchar email
        varchar role
        timestamp invited_at
        bigint project_id FK
    }

    audit_logs {
        bigint id PK
        varchar action
        text details
        timestamp created_at
        bigint user_id FK
    }
```

---

## 2. Table Column Specifications & Indexes

### Table `users`
*   `email`: Configured with `UNIQUE` index. Lookups during logins are $O(1)$.
*   `role`: Stores role values (`USER`, `ADMIN`).

### Table `projects`
*   `user_id`: Foreign key reference to `users(id)` with ON DELETE CASCADE rules handled via JPA configuration.
*   *Performance Note*: Searches on title and description are mapped using case-insensitive lowercase matching inside dynamic query filters.

### Table `tasks`
*   `project_id`: Foreign key reference to `projects(id)`.
*   `status`: Restricts inputs to `TODO`, `IN_PROGRESS`, `REVIEW`, and `DONE`.

### Table `attachments`
*   `filePath`: Stores the full disk store path of the file. File records are decoupled from the PostgreSQL database engine blocks.
*   `project_id` & `task_id`: Nullable foreign keys, allowing task-specific uploads or project-wide wiki files.
