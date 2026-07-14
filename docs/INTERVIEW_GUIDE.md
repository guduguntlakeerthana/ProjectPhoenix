# DevFlow AI Technical Interview Guide & Portfolio Resources

This document prepares you to explain the technical architecture, design decisions, and debugging achievements of DevFlow AI in senior software engineering interviews. It also includes a resume-ready description and LinkedIn launch content.

---

## 1. Core Overview: What is DevFlow AI?
DevFlow AI is an AI-assisted developer workspace designed for software engineers, freelancers, and agile teams. It consolidates task management, calendar planners, Kanban workflows, markdown notes, documentation wikis, system audit logs, and custom CSS reports into a single, cohesive developer portal. It integrates with Google's Gemini API (with simulated fallbacks) to suggest automated sprint breakdowns and technical documentation drafts.

### Why build it?
Modern developer tooling is scattered across multiple SaaS platforms (Jira for tasks, Confluence for wikis, Notion for notes, calendar schedulers for milestones, custom scripts for reports). DevFlow AI unites these tools under a high-performance developer dark-theme layout, demonstrating clean full-stack engineering, secure role-based controls, dynamic database query filters, and clean modular structures.

---

## 2. Deep Dive Architectural Stack

### Angular 21 Frontend Architecture
- **Standalone Design**: Built entirely with standalone components, eliminating routing and module bloat.
- **Signals State Management**: Employs Angular Signals (`signal`, `computed`, `effect`) for fine-grained reactivity, ensuring DOM nodes update only when their specific state changes, avoiding expensive Angular zone-based change detection cycles.
- **Functional Interceptors**: Uses a functional `authInterceptor` to intercept HTTP requests, retrieve the token from LocalStorage, and set the `Authorization: Bearer <token>` header dynamically.
- **Route Guards**: Uses functional guards (`authGuard`, `adminGuard`) to check roles and active sessions on route activation.

### Spring Boot 3.x Backend Architecture
- **Clean Layered Architecture**: Strictly divides components into DTOs, RestControllers, Services, Repositories, Entities, Config, and Exceptions.
- **Spring Security 6.x & JWT**: Custom filter intercepts incoming requests, parses claims, verifies signatures, and puts user contexts into Spring's security context state.
- **JPA & Hibernate**: Lazy loading collections are loaded within Transaction boundaries using `@Transactional` to optimize performance and prevent session timeout errors.

---

## 3. Real-World Technical Challenges & How They Were Debugged

### Challenge 1: The `lower(bytea) does not exist` PostgreSQL Error
- **The Issue**: When the user searched their project workspace, the application crashed with HTTP 500. The log reported: `ERROR: function lower(bytea) does not exist`.
- **Root Cause**: The repository query used JPQL:
  `AND (:search IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :search, '%')) ...)`
  If the `search` string parameter was null, Hibernate mapped the parameter to JDBC type `VARBINARY` (bytea). PostgreSQL does not have a function signature for `LOWER(bytea)`.
- **Debugging & Resolution**: We replaced the query with a clean **Spring Data JPA Specification**. Instead of writing complex JPQL statements, we dynamically construct JPA Criteria Predicates:
  ```java
  if (searchQuery != null) {
      String searchPattern = "%" + searchQuery.toLowerCase() + "%";
      Predicate titlePred = cb.like(cb.lower(root.get("title")), searchPattern);
      ...
  }
  ```
  If `searchQuery` is null, no search predicates are appended, completely bypassing calls to `cb.lower(...)` on null values and removing unsafe database casts.

### Challenge 2: `SignatureException: JWT signature does not match locally computed signature`
- **The Issue**: Requests failed with 401 or 500 if the user had an old, malformed, or expired token in their browser.
- **Root Cause**: The custom `JwtAuthenticationFilter` parsed the Authorization header and directly called `jwtService.extractEmail(token)`. If the signature was invalid or expired, the JWT library threw a `SignatureException` or `ExpiredJwtException`. Because these exceptions were uncaught inside the filter chain, the request failed with an uncaught 500 error instead of returning a proper 401 Unauthorized code or letting the filter chain handle public endpoints.
- **Debugging & Resolution**: We wrapped the extraction and token validation logic inside `JwtAuthenticationFilter` in a secure `try-catch` block:
  ```java
  try {
      String token = authHeader.substring(7);
      String email = jwtService.extractEmail(token);
      ...
  } catch (Exception e) {
      logger.warn("JWT authentication failed: " + e.getMessage());
  }
  ```
  Now, any invalid, malformed, or expired tokens are caught safely. The filter logs a warning and proceeds down the filter chain without authenticating the user, letting Spring Security correctly reject protected routes with 401 or allow public routes (like login/register) to succeed.

### Challenge 3: Angular TypeScript Configuration Mismatches
- **The Issue**: Typings or modules like `preserve` were complaining in the compiler or causing build warnings.
- **Root Cause**: Invalid syntax in the custom configurations under `tsconfig.app.json` and `tsconfig.spec.json` (such as double opening braces or misplaced nested blocks) broke JSON parsing during builds.
- **Debugging & Resolution**: We audited both configuration files, restructured them into single clean JSON models, and combined `compilerOptions` like `rootDir` and `outDir` into correct objects. This resolved the compiler parsing errors instantly.

### Challenge 4: CORS / 403 Forbidden Handshake Failures
- **The Issue**: Local browser requests from `localhost:4200` to `localhost:9091` were blocked by the browser.
- **Root Cause**: Missing Spring Security configuration mapping for pre-flight `OPTIONS` requests.
- **Debugging & Resolution**: We configured a centralized `CorsConfig` bean mapping specific origin headers (`http://localhost:4200`), allowed HTTP methods (`GET, POST, PUT, DELETE, OPTIONS`), and enabled credentials. This was integrated into the Spring Security chain: `http.cors(cors -> {})`, resolving the pre-flight block.

---

## 4. Resume-Ready Project Description

**DevFlow AI | Full-Stack Software Engineer**
*   Designed and deployed an AI-assisted developer productivity and agile planning platform using **Java 17, Spring Boot 3.x, Angular 21, and PostgreSQL**.
*   Secured API endpoints using **Spring Security 6.x & JWT token filters**, preventing request crashes on invalid/expired signatures using secure filter boundary catches.
*   Eliminated SQL/JPQL parameter casting errors (`lower(bytea)`) by refactoring filtering logic into dynamic **Spring Data JPA Specifications**, improving PostgreSQL query efficiency.
*   Enforced data boundaries using transactional context boundaries (`@Transactional`), avoiding lazy initialization collection errors across service and mapper boundaries.
*   Centralized frontend service calls by creating a dynamic API configuration layer that auto-detects development environments versus production domains.
*   Packaged and containerized the monorepo workspace using multi-stage **Dockerfiles** and **Docker Compose**, decreasing final production container weights.

---

## 5. LinkedIn Launch Post Template

🚀 **Excited to share my latest full-stack project: DevFlow AI!**

I wanted to build a unified workspace tailored for developers that brings notes, task tracking, Kanban stages, sprint planning, and AI helpers under a single dark-mode console.

Here's the tech stack I chose for v1.0:
- ☕ **Backend**: Java 17, Spring Boot 3.x, Spring Security, Spring Data JPA, PostgreSQL
- 🅰️ **Frontend**: Angular 21 (Standalone Components, Signals state, functional interceptors)
- 🐳 **Infrastructure**: Multi-stage Docker packaging, Nginx proxy configs, Docker Compose
- 🧠 **AI**: Google Gemini API integration (with mock fallbacks)

💡 **Some key technical challenges I solved along the way:**
- **Dynamic Database Queries**: Solved PostgreSQL bytea casting errors (`lower(bytea) does not exist`) by refactoring queries into dynamic Spring Data JPA Specifications.
- **JWT Signature Boundary Protection**: Wrapped auth filter token extractions in safe exception boundaries to log warnings on expired/corrupted tokens rather than crashing backend requests.
- **Performance Optimization**: Tuned Hibernate transactions using `@Transactional` to allow safe lazy loading while keeping Spring Open-In-View disabled for resource safety.

Check out the code here: [insert link to repo]

#java #springboot #angular #postgresql #docker #softwareengineering #webdevelopment #coding
