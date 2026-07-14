# DevFlow AI Testing Strategy & Verification Guide

This document outlines the testing architecture, validation frameworks, and execution processes for ensuring the stability and performance of DevFlow AI.

---

## 1. Backend Testing Framework

The backend uses **JUnit 5**, **Mockito**, and **Spring Boot Test** tools for unit and integration testing.

### Test Execution
Navigate to the `backend/` directory and run:
```bash
.\mvnw.cmd clean test
```

### Key Areas Tested
1.  **Auth Bootstrapping**:
    - Tests check if the Spring context loads properly with the security filter chains.
2.  **JPA Repository Mocking**:
    - Validates service layer boundaries using Mockito.
3.  **Dynamic Specification Logic**:
    - Validates that project filters generate clean predicates and don't produce database execution failures.

---

## 2. Frontend Testing Configuration

The frontend is equipped with **Vitest** for unit testing.

### Test Execution
Navigate to `frontend/devflow-frontend/` and run:
```bash
npm run test
```

### Build Verification
To ensure TypeScript and Angular template compiler structures are completely valid before releases:
```bash
npm run build
```

---

## 3. End-to-End Verification Pipeline

Before packaging v1.0 release targets:
1.  **Run Clean Compiles**: Ensure the backend builds (`mvn clean compile`) and frontend builds (`ng build`) with zero warning thresholds.
2.  **Verify DB Schema**: Verify that Postgres starts, and that Hibernate `ddl-auto=update` matches database columns without drop/truncations.
3.  **Examine HTTP Boundaries**: Test API responses for 401 Unauthorized codes when Bearer tokens are missing, and check 403 Forbidden codes for unauthorized project/attachment edits.
