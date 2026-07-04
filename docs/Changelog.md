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
