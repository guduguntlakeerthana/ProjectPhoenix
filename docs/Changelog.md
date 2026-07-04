# Changelog - DevFlow AI

All notable changes to this project will be documented in this file.

## [1.0.0] - 2026-07-04
### Added
- Root-level `.gitignore` configuring exclusions for Maven targets, Node modules, IDE files, and environment files.
- Root-level `README.md` detailing project structure, port maps, and local execution configurations.
- Consolidation of the project directories under a single root Git repository at `ProjectPhoenix/`, preserving full backend commit history.

### Changed
- Refactored Angular workspace routing (`app.routes.ts`) to secure components under `MainLayout` and `authGuard`.
- Standardized `authGuard` using Angular Signals and injected redirect logic.
- Built-out responsive developer-themed workspace navbar, sidebar, and dashboard components.

### Verified
- Tested backend compile and test execution: `BUILD SUCCESS` (compiles and context loads successfully).
- Verified local PostgreSQL database connectivity.
- Checked frontend production build compilation.

## [1.1.0] - 2026-07-04
### Secured
- Removed hardcoded secret key in `JwtService.java` and migrated to constructor injection via `@Value`.
- Converted `application.properties` database password and JWT secret configurations to use environment variables (`DB_PASSWORD` and `JWT_SECRET`) with safe fallbacks.
- Created `application-example.properties` outlining configuration template.

### Added
- Added custom paginated query in `ProjectRepository` supporting search, status filtering, and sorting.
- Added a statistics endpoint `/api/projects/stats` returning aggregated count metrics of projects.
- Integrated paginator footer controls and sort sorting dropdown to projects grid page in Angular.
- Connected dashboard widgets directly to backend statistics API.

## [1.2.0] - 2026-07-04
### Added
- Created `Task` database entity, mapping foreign key relationships to users and projects.
- Implemented Task REST API endpoints (`POST`, `GET`, `PUT`, `DELETE`).
- Built a developer-themed Task Board listing, editing, and deleting sprint tasks.
- Added client-side Signal filters for status, priority, and project scope.
